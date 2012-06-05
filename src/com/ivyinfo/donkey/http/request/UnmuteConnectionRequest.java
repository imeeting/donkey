package com.ivyinfo.donkey.http.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.ms.IMediaServer;

public class UnmuteConnectionRequest extends DonkeyRequest {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {

		SipApplicationSession sipAppSession = getSipApplicationSession(request,
				response);
		SipSession sipSession = checkAttendeeSession(getSipSession(request,
				response));
		if (null == sipAppSession || null == sipSession) {
			return;
		}

		IMediaServer ms = (IMediaServer) sipAppSession
				.getAttribute(IMediaServer.MEDIA_SERVER);
		try {
			ms.muteConnection(sipAppSession, sipSession, false);
		} catch (Exception e) {
			e.printStackTrace();
			DonkeyResponse.InternalException(response, e);
			return;
		}

		DonkeyResponse.Accepted(response);
	}
}
