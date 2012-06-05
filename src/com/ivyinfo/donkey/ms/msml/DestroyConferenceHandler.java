package com.ivyinfo.donkey.ms.msml;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletResponse;

import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.sip.BaseSIPResponseHandler;

public class DestroyConferenceHandler extends BaseSIPResponseHandler {

	private static final String eventType = DonkeyGearmanMessage.conf_destroy;

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		SipApplicationSession sipAppSession = response
				.getApplicationSession(false);
		gearman.submit(DonkeyGearmanMessage.ConferenceDestroied(eventType,
				sipAppSession));
	}

	@Override
	public void doErrorResponse(SipServletResponse response) {
		SipApplicationSession sipAppSession = response
				.getApplicationSession(false);
		int statusCode = response.getStatus();
		gearman.submit(DonkeyGearmanMessage.DonkeyException(
				DonkeyGearmanMessage.conference_sip_error, eventType,
				sipAppSession, Integer.toString(statusCode)));
	}

	@Override
	public void doProvisionalResponse(SipServletResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doBranchResponse(SipServletResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doRedirectResponse(SipServletResponse response) {
		// TODO Auto-generated method stub

	}

}
