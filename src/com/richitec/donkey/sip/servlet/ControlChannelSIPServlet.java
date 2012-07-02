package com.richitec.donkey.sip.servlet;

import java.io.IOException;

import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import akka.actor.ActorRef;

import com.richitec.donkey.conference.actor.ControlChannelActor;
import com.richitec.donkey.conference.message.ActorMessage;

@javax.servlet.sip.annotation.SipServlet
public class ControlChannelSIPServlet extends SipServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void  doInfo(SipServletRequest request) {
    	SipServletResponse response = request.createResponse(SipServletResponse.SC_OK);
    	try {
    		response.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SipSession session = request.getSession(false);
		ActorRef actor = (ActorRef) session.getAttribute(ControlChannelActor.Actor);
		actor.tell(new ActorMessage.ControlChannelInfoRequest(request));
	}
	
	@Override
	protected void doResponse(SipServletResponse resp){
		int status = resp.getStatus();
		if (status < 200){
			return;
		}
		
		String method = resp.getMethod();
		SipSession session = resp.getSession(false);
		ActorRef actor = (ActorRef) session.getAttribute(ControlChannelActor.Actor);
		if (ControlChannelActor.INVITE.equals(method)){
			SipServletRequest ack = resp.createAck();
			try {
				ack.send();
			} catch (IOException e) {
				e.printStackTrace();
			}
			actor.tell(new ActorMessage.CreateControlChannelComplete(status));
		} else if (ControlChannelActor.INFO.equals(method)){
			actor.tell(new ActorMessage.ControlChannelInfoResponse(resp));
		}
	}

}
