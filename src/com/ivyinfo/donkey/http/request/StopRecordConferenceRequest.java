package com.ivyinfo.donkey.http.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;

import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.ms.IMediaServer;

public class StopRecordConferenceRequest extends DonkeyRequest {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response){
		
		SipApplicationSession sipAppSession = getSipApplicationSession(request, response);
		if(sipAppSession == null){
			return;
		}
		
		IMediaServer ms = (IMediaServer) sipAppSession.getAttribute(IMediaServer.MEDIA_SERVER);
		try {
			ms.recordConference(sipAppSession, "", false);
		} catch (Exception e) {
			e.printStackTrace();
			DonkeyResponse.InternalException(response, e);
			return;
		}
		
		DonkeyResponse.Accepted(response);
	}
}
