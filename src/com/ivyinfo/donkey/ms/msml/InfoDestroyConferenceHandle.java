package com.ivyinfo.donkey.ms.msml;

import java.io.IOException;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.sip.BaseSIPResponseHandler;
import com.ivyinfo.donkey.sip.ISIPResponseHandler;
import com.ivyinfo.donkey.sip.SIPHelper;

public class InfoDestroyConferenceHandle extends BaseSIPResponseHandler {

	private static final String eventType = DonkeyGearmanMessage.conf_info_destroy;

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		try {
			SipSession controlSession = response.getSession(false);

			// control channelÏòÃ½Ìå·þÎñÆ÷·¢ËÍBYE¡£
			SipServletRequest bye = controlSession.createRequest(SIPHelper.BYE);

			String cseq = bye.getHeader("CSeq");
			controlSession.setAttribute(cseq
					+ ISIPResponseHandler.RESPONSE_HANDLER,
					new DestroyConferenceHandler());

			bye.send();
		} catch (IOException e) {
			SipApplicationSession sipAppSession = response
					.getApplicationSession(false);
			gearman.submit(DonkeyGearmanMessage.DonkeyException(
					DonkeyGearmanMessage.conference_exception, eventType,
					sipAppSession, e.getMessage()));
		}
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
