package org.feswm.gopro.messenger.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Benefit {
	
	private String caseId;
	private String benefitType	;
	private String benefitAward	;
	private Date benefitStartDate;
	private Date benefitEndDate;
	
	public Benefit(){
		this.caseId = "1";
		this.benefitAward = "20";
		this.benefitType = "Living Together";
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			this.benefitStartDate = fmt.parse("2011-12-11");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		try {
			this.benefitEndDate = fmt.parse("2019-09-10");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	public Benefit(String benefitType, String benefitAward, Date benefitStartDate,Date benefitEndDate ){
	
		
	}

	public String getBenefitType() {
		return benefitType;
	}

	public void setBenefitType(String benefitType) {
		this.benefitType = benefitType;
	}

	public String getBenefitAward() {
		return benefitAward;
	}

	public void setBenefitAward(String benefitAward) {
		this.benefitAward = benefitAward;
	}

	public Date getBenefitStartDate() {
		return benefitStartDate;
	}

	public void setBenefitStartDate(Date benefitStartDate) {
		this.benefitStartDate = benefitStartDate;
	}

	public Date getBenefitEndDate() {
		return benefitEndDate;
	}

	public void setBenefitEndDate(Date benefitEndDate) {
		this.benefitEndDate = benefitEndDate;
	}
	

		
	
	
}
