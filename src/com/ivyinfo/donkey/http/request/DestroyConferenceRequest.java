package com.ivyinfo.donkey.http.request;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSession.State;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.ms.IMediaServer;

public class DestroyConferenceRequest extends DonkeyRequest {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {

		SipApplicationSession sipAppSession = getSipApplicationSession(request, response);
		if(sipAppSession == null){
			return;
		}

		SipSession controlSession = (SipSession) sipAppSession.getAttribute(ConferenceManager.CONTROL_SESSION);
		try {
			if(controlSession != null){
				IMediaServer ms = (IMediaServer) sipAppSession.getAttribute(IMediaServer.MEDIA_SERVER);
				ms.destroyConference(sipAppSession);
			}
			
			// CANCEL inverting SIP sessions
			Iterator<SipSession> sipSessions = (Iterator<SipSession>) sipAppSession.getSessions("SIP");
			while(sipSessions.hasNext()){
				SipSession session = sipSessions.next();
				if (session.isValid()){
					State state = session.getState();
					if (state.equals(State.EARLY) || state.equals(State.INITIAL)){
						cancelInviteRequest(session);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			DonkeyResponse.InternalException(response, e);
			return;
		}
		
		DonkeyResponse.Accepted(response);
	}

}
