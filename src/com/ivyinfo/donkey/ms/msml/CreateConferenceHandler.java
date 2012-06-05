package com.ivyinfo.donkey.ms.msml;

import java.io.ByteArrayInputStream;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.sip.BaseSIPResponseHandler;
import com.ivyinfo.donkey.sip.ISIPResponseHandler;
import com.ivyinfo.donkey.sip.SIPHelper;

public class CreateConferenceHandler extends BaseSIPResponseHandler {

	private static final String eventType = DonkeyGearmanMessage.conf_create;
	private static final String conf_create_failed = "conf.create.failed";

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		SipApplicationSession sipAppSession = response
				.getApplicationSession(false);
		String contentType = response.getContentType();
		String confid = null;
		String result = null;

		if (MSMLHelper.isMsmlContentType(contentType)) {
			try {
				byte[] content = (byte[]) response.getContent();

				result = MSMLHelper
						.getNodeAttributeValue(
								new ByteArrayInputStream(content), "result",
								"response");

				if ("200".equals(result)) {
					confid = MSMLHelper.getNodeValues(
							new ByteArrayInputStream(content), "result",
							"confid").elementAt(0);
				}
				// ms create conference failed
				else {
					SipSession controlSession = (SipSession) sipAppSession
							.getAttribute(ConferenceManager.CONTROL_SESSION);
					// control channel
					SipServletRequest bye = controlSession
							.createRequest(SIPHelper.BYE);

					String cseq = bye.getHeader("CSeq");
					controlSession.setAttribute(cseq
							+ ISIPResponseHandler.RESPONSE_HANDLER,
							new DestroyConferenceHandler());

					bye.send();

					gearman.submit(DonkeyGearmanMessage.DonkeyException(
							DonkeyGearmanMessage.conference_exception,
							eventType, sipAppSession, conf_create_failed));
					return;
				}
			} catch (Exception e) {
				gearman.submit(DonkeyGearmanMessage.DonkeyException(
						DonkeyGearmanMessage.conference_exception, eventType,
						sipAppSession, e.getMessage()));
				return;
			}

			sipAppSession.setAttribute(ConferenceManager.CONF_ID, confid);
			sipAppSession.setAttribute(ConferenceManager.CONTROL_SESSION,
					response.getSession(false));

			gearman.submit(DonkeyGearmanMessage.ConferenceCreated(eventType,
					sipAppSession));
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
