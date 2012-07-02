package com.richitec.donkey.conference.message.sip;

import javax.servlet.sip.SipServletRequest;

public class SendSipRequestCompleteMsg {

	private SipServletRequest request;
	
	public SendSipRequestCompleteMsg(SipServletRequest request){
		this.request = request;
	}
	
	public SipServletRequest getSipServletRequest(){
		return this.request;
	}
}
