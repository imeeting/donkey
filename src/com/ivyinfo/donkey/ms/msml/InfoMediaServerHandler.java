package com.ivyinfo.donkey.ms.msml;

import java.io.ByteArrayInputStream;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.sip.BaseSIPResponseHandler;

public class InfoMediaServerHandler extends BaseSIPResponseHandler {

	private static final Logger logger = Logger
			.getLogger(InfoMediaServerHandler.class);

	private String eventName;

	private SipSession userSession;

	public InfoMediaServerHandler(String event, SipSession sipSession) {
		this.eventName = event;
		this.userSession = sipSession;
	}

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		// gearman.submit(DonkeyGearmanMessage.errSipResponse(
		// eventName, response));

		SipApplicationSession sipAppSession = response
				.getApplicationSession(false);
		String contentType = response.getContentType();
		String result = null;
		String description = null;
		if (MSMLHelper.isMsmlContentType(contentType)) {
			try {
				byte[] content = (byte[]) response.getContent();

				result = MSMLHelper
						.getNodeAttributeValue(
								new ByteArrayInputStream(content), "result",
								"response");

				if (!"200".equals(result)) {
					description = MSMLHelper.getNodeValues(
							new ByteArrayInputStream(content), "result",
							"description").elementAt(0);
					gearman.submit(DonkeyGearmanMessage.DonkeyException(
							DonkeyGearmanMessage.conference_info_exception,
							eventName, sipAppSession, description));
				}
				// info operate succeed
				else {
					if (eventName.equals(DonkeyGearmanMessage.conn_mute)
							|| eventName
									.equals(DonkeyGearmanMessage.conn_unmute)) {

						gearman.submit(DonkeyGearmanMessage.ConnMute(eventName,
								checkFirstInviteSession(userSession)));
					}
				}
			} catch (Exception e) {
				gearman.submit(DonkeyGearmanMessage.DonkeyException(
						DonkeyGearmanMessage.conference_info_exception,
						eventName, sipAppSession, e.getMessage()));
				return;
			}
		}
	}

	@Override
	public void doErrorResponse(SipServletResponse response) {
		SipApplicationSession sipAppSession = response
				.getApplicationSession(false);
		int statusCode = response.getStatus();
		gearman.submit(DonkeyGearmanMessage.DonkeyException(
				DonkeyGearmanMessage.conference_sip_error, eventName,
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
