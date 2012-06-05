package com.ivyinfo.donkey.http.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Configuration;
import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.db.supplier.DevAppIDInfoManager;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.http.api.DonkeyResponseMessage;
import com.ivyinfo.donkey.ms.IMediaServer;

public class CreateConferenceRequest extends DonkeyRequest {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		String appid = request.getParameter(Constant.AppID);
		String reqid = request.getParameter(Constant.ReqID);
		
		IMediaServer ms = msManager.getMediaServer();
		SipApplicationSession sipAppSession = ConferenceManager.getSipFactory().createApplicationSession();
		
		sipAppSession.setExpires(Configuration.getConfSessionExpire());
		sipAppSession.setInvalidateWhenReady(true);

		sipAppSession.setAttribute(IMediaServer.MEDIA_SERVER, ms);
		sipAppSession.setAttribute(Constant.ReqID, reqid);
		sipAppSession.setAttribute(Constant.AppID, appid);
		sipAppSession.setAttribute(Constant.CallBackURL, DevAppIDInfoManager.getCallBackURL(appid));
		
		try {
			ms.createConference(ConferenceManager.getSipFactory(), sipAppSession);
			DonkeyResponse.Accepted(response, DonkeyResponseMessage.ConferenceSession(sipAppSession.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			DonkeyResponse.InternalException(response, e);
		}
	}
	
}
