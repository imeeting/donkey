package com.ivyinfo.donkey.http.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;

import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.ms.IMediaServer;
import com.ivyinfo.util.DonkeyUtil;

public class RecordConferenceRequest extends DonkeyRequest {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		
		SipApplicationSession sipAppSession = getSipApplicationSession(request, response);
		if(sipAppSession == null){
			return;
		}
		
		String RecordMaxtime = request.getParameter(Constant.RecordMaxtime);
		if (!DonkeyUtil.isDigit(RecordMaxtime)) {
			DonkeyResponse.BadRequest(response);
			return;
		}
		
		IMediaServer ms = (IMediaServer) sipAppSession.getAttribute(IMediaServer.MEDIA_SERVER);
		try {
			ms.recordConference(sipAppSession, RecordMaxtime, true);
		} catch (Exception e) {
			e.printStackTrace();
			DonkeyResponse.InternalException(response, e);
			return;
		}
		
		DonkeyResponse.Accepted(response);
	}

}
