package org.feswm.gopro.messenger;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresourcexml")
public class MyResourceXML {

	final String thisNeedsToGoInToConfigUrl = "http://localhost:8081/GoPro";
	final HttpClient client = HttpClientBuilder.create().build();
	
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getBenefit() {
		Benefit benefit = new Benefit();

	    String xmlString = "";
	    try {
	        JAXBContext context = JAXBContext.newInstance(Benefit.class);
	        Marshaller m = context.createMarshaller();

	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML
	        
	        StringWriter sw = new StringWriter();
	        m.marshal(benefit, sw);
	        xmlString = sw.toString();

	    } catch (JAXBException e) {
	        e.printStackTrace();
	    }

	    return xmlString;
	}

	@POST
	@Path("/importcases")
	@Produces(MediaType.APPLICATION_XML)
	public Response importCase() throws IOException, URISyntaxException {
		
		try {
				String loginToken = login(thisNeedsToGoInToConfigUrl, "luke.doyle", "password");
				System.out.println(loginToken);
				return importCases(thisNeedsToGoInToConfigUrl, loginToken, getXMLContents());
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(Status.BAD_REQUEST).build();
	}
	
	private Response importCases(final String endpoint, String token, String data) {
		 
	    final HttpPost importRequest = new HttpPost(endpoint + "/api/fesw/importcasesxml");
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
	
	private String getXMLContents() {
		
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<cases>\r\n" + 
				"	<case>\r\n" + 
				"		<offenceType>UndeclaredIncomeGTE160000LT100000</offenceType>\r\n" + 
				"		<firstName>Adam</firstName>\r\n" + 
				"		<lastName>Jones</lastName>\r\n" + 
				"		<postcode>M5</postcode>\r\n" + 
				"		<NINO>AB 22 33 44 S</NINO>\r\n" + 
				"	</case>\r\n" + 
				"	<case>\r\n" + 
				"		<offenceType>LivingTogetherLT3Months</offenceType>\r\n" + 
				"		<firstName>Joe</firstName>\r\n" + 
				"		<lastName>White</lastName>\r\n" + 
				"		<postcode>M5</postcode>\r\n" + 
				"		<NINO>DC 45 92 12 J</NINO>\r\n" + 
				"	</case>\r\n" + 
				"</cases>";
		
	}
}
