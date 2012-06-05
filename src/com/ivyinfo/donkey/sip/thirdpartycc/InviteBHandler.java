package com.ivyinfo.donkey.sip.thirdpartycc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.sip.BaseSIPResponseHandler;
import com.ivyinfo.donkey.sip.ISIPResponseHandler;
import com.ivyinfo.donkey.sip.SIPHelper;
import com.ivyinfo.donkey.user.CallUserSipServlet;

public class InviteBHandler extends BaseSIPResponseHandler {

	private static final String eventType = DonkeyGearmanMessage.conf_join;

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		SipSession sessionB = response.getSession(false);
		SipSession sessionA = (SipSession) sessionB
				.getAttribute(ConferenceManager.LINKED_SESSION);
		try {
			SipServletRequest reInvite = sessionA
					.createRequest(SIPHelper.INVITE);
			reInvite.setContent(response.getContent(),
					response.getContentType());

			String cseq = reInvite.getHeader("CSeq");
			sessionA.setAttribute(cseq + ISIPResponseHandler.RESPONSE_HANDLER,
					new InviteASecondHandler(response));
			sessionA.setHandler(CallUserSipServlet.class.getSimpleName());

			reInvite.send();
		} catch (Exception e) {
			gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
					DonkeyGearmanMessage.user_exception, eventType, sessionA,
					e.getMessage()));
		}
	}

	@Override
	public void doErrorResponse(SipServletResponse response) {
		SipSession sessionB = response.getSession(false);
		SipSession sessionA = (SipSession) sessionB
				.getAttribute(ConferenceManager.LINKED_SESSION);
		int statusCode = response.getStatus();
		gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
				DonkeyGearmanMessage.user_sip_error, eventType, sessionA,
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
