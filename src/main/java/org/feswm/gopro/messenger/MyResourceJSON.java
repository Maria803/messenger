package org.feswm.gopro.messenger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.feswm.gopro.messenger.model.Benefit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresourcejson")
public class MyResourceJSON {

	final String thisNeedsToGoInToConfigUrl = "http://localhost:8081/GoPro";
	final HttpClient client = HttpClientBuilder.create().build();

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getBenefit() {
		Benefit benefit = new Benefit();

		ObjectMapper mapper = new ObjectMapper();

		// Object to JSON in String
		String jsonInString = "";
		try {
			jsonInString = mapper.writeValueAsString(benefit);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonInString;
	}

	@POST
	@Path("/importcases")
	@Produces(MediaType.APPLICATION_JSON)
	public Response importCase(@FormParam("data") final String data) {
		// {{"offenceType" : "UndeclaredIncomeGTE160000LT100000", "firstName" : "John",
		// "lastName" : "Smith", "postcode" : "M5", "NINO" : "PD 54 45 43 C"},
		// {"offenceType" : "LivingTogetherLT3Months", "firstName" : "Rob", "lastName" :
		// "Jones", "postcode" : "M5", "NINO" : "AA 12 37 77 A"}}
		try {
				String loginToken = login(thisNeedsToGoInToConfigUrl, "luke.doyle", "password");
				System.out.println(loginToken);
				System.out.println(data);
				return importCases(thisNeedsToGoInToConfigUrl, loginToken, data);
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(Status.BAD_REQUEST).build();
	}
	
	private Response importCases(final String endpoint, String token, String data) {
		 
	    final HttpPost importRequest = new HttpPost(endpoint + "/api/fesw/importcases");
	    try {
	    	importRequest.setHeader("Authorization", "Bearer " + token);
	        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	        urlParameters.add(new BasicNameValuePair("data", data));
	        importRequest.setEntity(new UrlEncodedFormEntity(urlParameters));
	 
	        //
	        // Perform
	        HttpResponse clientResponse = client.execute(importRequest);
	        if (clientResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
	        	return Response.status(Status.BAD_REQUEST).build();
	        }
	 
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (Exception e){
	    	e.printStackTrace();
	    }
	 
	    return Response.status(Status.OK).build();
	}

	
	private String login(final String endpoint, String user, String password) {
		 
	    final HttpPost loginRequest = new HttpPost(endpoint + "/api/Client/Access/Login");
	    HttpResponse clientResponse = null;
	    try {
	        //
	        // Wrap the Credentials
	        final String credsPattern = "{\"_type\": \"UserCredentialsType\",\"username\": \"%s\",\"password\": \"%s\"})}";
	        final String credentials = String.format(credsPattern, user, password);
	        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	        urlParameters.add(new BasicNameValuePair("credentials", credentials));
	        loginRequest.setEntity(new UrlEncodedFormEntity(urlParameters));
	 
	        //
	        // Perform
	       clientResponse = client.execute(loginRequest);
	        for (final Header header : clientResponse.getHeaders("X-GoProToken")) {
	            return header.getValue();
	        }
	 
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } 
	 
	    return null;
	}
}
