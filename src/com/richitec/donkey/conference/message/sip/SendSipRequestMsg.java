package com.richitec.donkey.conference.message.sip;


import java.io.IOException;

import javax.servlet.sip.SipServletRequest;

import akka.actor.ActorRef;

/**
 * 由于Actor有自己的线程池，如果在Actor中进行网络相关操作，会造成运行该Actor的线程阻塞。
 * 这样就会影响Actor的并发性能，所以所有网络相关的操作不在Actor线程中进行。
 * Actor发送SendSipRequestMsg给ConferenceActor，由ConferenceActor交给专门的线程池进行
 * 处理。
 * 
 * @author huuguanghui
 *
 */
public class SendSipRequestMsg {

	private SipServletRequest request;
	
	public SendSipRequestMsg(SipServletRequest request){
		this.request = request;
	}
	
	public SipServletRequest getSipServletRequest(){
		return this.request;
	}
	
	public void send(ActorRef origin, ActorRef self){
		try {
			request.send();
			origin.tell(new SendSipRequestCompleteMsg(request), self);
		} catch (IOException e) {
			origin.tell(new SendSipRequestErrorMsg(request, e), self);
		}
	}
}
