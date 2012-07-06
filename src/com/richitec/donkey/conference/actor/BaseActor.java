package com.richitec.donkey.conference.actor;

import java.io.IOException;

import javax.servlet.sip.SipServletRequest;

import com.richitec.donkey.ContextLoader;
import com.richitec.donkey.DonkeyThreadPool;
import com.richitec.donkey.conference.message.sip.SendSipRequestCompleteMsg;
import com.richitec.donkey.conference.message.sip.SendSipRequestErrorMsg;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class BaseActor extends UntypedActor {
	
	private DonkeyThreadPool threadPool;
	
	public BaseActor(){
		threadPool = ContextLoader.getThreadPool();
	}
	
	public void send(SipServletRequest request){
		SipRequestSender sender = new SipRequestSender(request, getSelf());
		threadPool.submit(sender);
	}
	
	@Override
	public void onReceive(Object arg0) throws Exception {
		
	}
	
	public static class SipRequestSender implements Runnable {
		
		private SipServletRequest request;
		private ActorRef origin;
		
		public SipRequestSender(SipServletRequest req, ActorRef origin){
			this.request = req;
			this.origin = origin;
		}

		@Override
		public void run() {
			try {
				request.send();
				origin.tell(new SendSipRequestCompleteMsg(request));
			} catch (IOException e) {
				origin.tell(new SendSipRequestErrorMsg(request, e));
			}			
		}
	}

}
