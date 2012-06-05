package com.ivyinfo.donkey.ms;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipSession;

public interface IMediaServer {
	
	public static final String MEDIA_SERVER = "media_server";
	
	public String getSipURI();

	public void createConference(SipFactory sipFactory,
			SipApplicationSession sipAppSession) throws Exception ;
	
	public void destroyConference(SipApplicationSession sipAppSession) throws Exception ;
	
	public void joinConference(SipApplicationSession sipAppSession, SipSession sipSession) throws Exception ;
	
	public void muteConnection(SipApplicationSession sipAppSession, SipSession sipSession, boolean indicateFlag) throws Exception ;
	
	public void announceConference(SipApplicationSession sipAppSession, String announcementName, boolean indicateFlag) throws Exception ;
	
	public void recordConference(SipApplicationSession sipAppSession, String recordMaxtime, boolean indicateFlag) throws Exception ;
}
