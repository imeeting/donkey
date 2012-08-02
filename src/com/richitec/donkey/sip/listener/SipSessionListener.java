package com.richitec.donkey.sip.listener;

import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSessionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.richitec.donkey.conference.message.ActorMessage;

import akka.actor.ActorRef;

@javax.servlet.sip.annotation.SipListener
public class SipSessionListener implements javax.servlet.sip.SipSessionListener {
	
	private static final Log log = LogFactory.getLog(SipSessionListener.class);

	@Override
	public void sessionCreated(SipSessionEvent event) {
		log.debug("\nSIP SESSION <" + event.getSession().getId() + "> Created");
	}

	@Override
	public void sessionDestroyed(SipSessionEvent event) {
		log.debug("\nSIP SESSION <" + event.getSession().getId() + "> Destroyed");
	}

	@Override
	public void sessionReadyToInvalidate(SipSessionEvent event) {
		log.debug("\nSIP SESSION <" + event.getSession().getId() + "> ReadyToInvalidate");
		
		SipSession session = event.getSession();
		ActorRef actor = (ActorRef) session.getAttribute("actor");
		if (null != actor){
			actor.tell(new ActorMessage.SipSessionReadyToInvalidate(session) );
		}
	}

}
