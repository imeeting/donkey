package com.richitec.donkey.sip.listener;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.richitec.donkey.conference.actor.ControlChannelActor;
import com.richitec.donkey.conference.message.ActorMessage;

import akka.actor.ActorRef;

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
		SipApplicationSession sipAppSession = ev.getApplicationSession();
		log.debug("\nAPP SESSION <" + sipAppSession.getId() + "> Expired");
		
		Integer expireMinutes =
			(Integer) sipAppSession.getAttribute(ControlChannelActor.ExpireMinutes);
		if (null == expireMinutes){
			//this SipApplicationSession is not control channel
			sipAppSession.setExpires(10);
		} else {
			//control channel SipApplicationSession
			if (expireMinutes > 0){
				expireMinutes -= 10;
				sipAppSession.setAttribute(ControlChannelActor.ExpireMinutes, expireMinutes);
				sipAppSession.setExpires(10);
				if (expireMinutes <= 0){
					ActorRef actor = 
						(ActorRef) sipAppSession.getAttribute(ControlChannelActor.Actor);
					if (null != actor){
						actor.tell(new ActorMessage.SipAppSessionExpired(sipAppSession));
					}
				}
			}
		}
	}

	@Override
	public void sessionReadyToInvalidate(SipApplicationSessionEvent ev) {
		SipApplicationSession sipAppSession = ev.getApplicationSession();
		log.debug("\nAPP SESSION <" + sipAppSession.getId() + "> ReadyToInvalidate");	
	}

}
