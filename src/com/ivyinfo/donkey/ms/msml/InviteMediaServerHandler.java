package com.ivyinfo.donkey.ms.msml;

import java.io.IOException;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.sip.BaseSIPResponseHandler;
import com.ivyinfo.donkey.sip.ISIPResponseHandler;
import com.ivyinfo.donkey.sip.SDPHelper;
import com.ivyinfo.donkey.sip.SIPHelper;

/**
 * 该呼叫流程比较简单，不能满足多种软交换的要求。
 * 
 * @author huuguanghui
 * 
 */
@Deprecated
public class InviteMediaServerHandler extends BaseSIPResponseHandler {

	private static final String eventName = DonkeyGearmanMessage.conf_join;

	private SipServletResponse inviteUserResponse;

	public InviteMediaServerHandler(SipServletResponse response) {
		this.inviteUserResponse = response;
	}

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		SipApplicationSession sipAppSession = response
				.getApplicationSession(false);
		SipSession userSession = inviteUserResponse.getSession(false);
		try {
			SipServletRequest userAck = inviteUserResponse.createAck();
			userAck.setContent(response.getContent(), response.getContentType());
			userAck.send();

			SipServletRequest msAck = response.createAck();
			msAck.send();

			gearman.submit(DonkeyGearmanMessage.DonkeyUserJoin(userSession));
		} catch (IOException e) {
			gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
					DonkeyGearmanMessage.user_exception, eventName,
					userSession, e.getMessage()));
		}

		String conn = response.getTo().getParameter("tag");
		userSession.setAttribute(ConferenceManager.USER_CONN, conn);
		String confid = (String) sipAppSession
				.getAttribute(ConferenceManager.CONF_ID);

		SipSession controlSession = (SipSession) sipAppSession
				.getAttribute(ConferenceManager.CONTROL_SESSION);
		SipServletRequest info = controlSession.createRequest(SIPHelper.INFO);
		try {
			String connRandomString = SDPHelper.randomUUID();
			// info.setContent(MSMLHelper.join(conn, confid),
			// MSMLHelper.MSML_CONTENT_TYPE);
			info.setContent(
					MSMLHelper.join_notice(conn, connRandomString, confid),
					MSMLHelper.MSML_CONTENT_TYPE);
			String handlerName = info.getHeader("CSeq")
					+ ISIPResponseHandler.RESPONSE_HANDLER;
			controlSession.setAttribute(handlerName,
					new InfoMediaServerHandler(eventName, userSession));
			info.send();
		} catch (Exception e) {
			gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
					DonkeyGearmanMessage.user_exception, eventName,
					userSession, e.getMessage()));
		}
	}

	@Override
	public void doErrorResponse(SipServletResponse response) {
		SipSession session = inviteUserResponse.getSession(false);
		int statusCode = response.getStatus();
		gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
				DonkeyGearmanMessage.user_sip_error, eventName, session,
				Integer.toString(statusCode)));
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
