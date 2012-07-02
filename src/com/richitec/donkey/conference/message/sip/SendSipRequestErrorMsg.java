package com.richitec.donkey.conference.message.sip;

import javax.servlet.sip.SipServletRequest;

public class SendSipRequestErrorMsg {
	
	private SipServletRequest request;
	private Exception exception;
	
	public SendSipRequestErrorMsg(SipServletRequest request, Exception e){
		this.request = request;
		this.exception = e;
	}
	
	public SipServletRequest getSipServletRequest(){
		return this.request;
	}
	
	public Exception getException(){
		return this.exception;
	}
}
