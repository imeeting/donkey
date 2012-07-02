package com.richitec.donkey.sip.listener;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@javax.servlet.sip.annotation.SipListener
public class SipApplicationSessionListener 
		implements javax.servlet.sip.SipApplicationSessionListener {

	private static final Log log = LogFactory.getLog(SipApplicationSessionListener.class);
	
	@Override
	public void sessionCreated(SipApplicationSessionEvent ev) {
		log.debug("\nAPP SESSION <" + ev.getApplicationSession().getId() + "> Created");
	}

	@Override
	public void sessionDestroyed(SipApplicationSessionEvent ev) {
		log.debug("\nAPP SESSION <" + ev.getApplicationSession().getId() + "> Destroyed");
	}

	@Override
	public void sessionExpired(SipApplicationSessionEvent ev) {
		log.debug("\nAPP SESSION <" + ev.getApplicationSession().getId() + "> Expired");
		SipApplicationSession session = ev.getApplicationSession();
		int r = session.setExpires(30);
		log.debug("APP setExpires = " + r);
	}

	@Override
	public void sessionReadyToInvalidate(SipApplicationSessionEvent ev) {
		log.debug("\nAPP SESSION <" + ev.getApplicationSession().getId() + "> ReadyToInvalidate");	
	}

}
