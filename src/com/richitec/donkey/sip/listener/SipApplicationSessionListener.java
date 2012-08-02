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

	/**
	 * SipApplicationSession Expired 以后，不会触发sessionReadyToInvalidate，
	 * 而是直接进入 sessionDestroyed。该SipApplicationSession的所有SipSession
	 * 也是直接进入 sessionDestroyed。
	 */
	@Override
	public void sessionExpired(SipApplicationSessionEvent ev) {
		SipApplicationSession sipAppSession = ev.getApplicationSession();
		
		Integer expireMinutes =
			(Integer) sipAppSession.getAttribute(ControlChannelActor.ExpireMinutes);
		if (null == expireMinutes){
			//this SipApplicationSession is not control channel
			sipAppSession.setExpires(10);
		} else {
			//control channel SipApplicationSession
			if (expireMinutes > 0){
				expireMinutes -= 1;
				sipAppSession.setAttribute(ControlChannelActor.ExpireMinutes, expireMinutes);
				sipAppSession.setExpires(1);
				if (expireMinutes <= 0){
					log.warn("\nAPP SESSION <" + sipAppSession.getId() + "> Expired");
					ActorRef actor = 
						(ActorRef) sipAppSession.getAttribute(ControlChannelActor.ConferenceActor);
					if (null != actor){
						actor.tell(ActorMessage.controlChannelExpired);
					}
				}
			}
		}
	}

	@Override
	public void sessionReadyToInvalidate(SipApplicationSessionEvent ev) {
		log.debug("\nAPP SESSION <" + ev.getApplicationSession().getId() + "> ReadyToInvalidate");	
	}

}
