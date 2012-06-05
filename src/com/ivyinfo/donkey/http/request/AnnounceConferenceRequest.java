package com.ivyinfo.donkey.http.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;

import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.ms.IMediaServer;

public class AnnounceConferenceRequest extends DonkeyRequest {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		
		SipApplicationSession sipAppSession = getSipApplicationSession(request, response);
		if(sipAppSession == null){
			return;
		}
		
		String announcementName = request.getParameter(Constant.AudioIdentifier);
		if ((null == announcementName || announcementName.length() <= 0)) {
			DonkeyResponse.BadRequest(response);
			return;
		}
		
		IMediaServer ms = (IMediaServer) sipAppSession.getAttribute(IMediaServer.MEDIA_SERVER);
		try {
			ms.announceConference(sipAppSession, announcementName, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DonkeyResponse.InternalException(response, e);
			return;
		}
		
		DonkeyResponse.Accepted(response);
	}

}
