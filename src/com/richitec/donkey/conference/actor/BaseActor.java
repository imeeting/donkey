package com.richitec.donkey.conference.actor;

import javax.servlet.sip.SipServletRequest;

import com.richitec.donkey.conference.message.sip.SendSipRequestMsg;

import akka.actor.UntypedActor;

public class BaseActor extends UntypedActor {
	
	public void send(SipServletRequest request){
		getContext().parent().tell(new SendSipRequestMsg(request), getSelf());
	}
	
	@Override
	public void onReceive(Object arg0) throws Exception {
		
	}

}
