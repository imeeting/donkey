package com.ivyinfo.donkey.user;

import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.sip.BaseSIPResponseHandler;

public class UnjoinUserHandler extends BaseSIPResponseHandler {

	private static final String eventName = DonkeyGearmanMessage.conf_unjoin;

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doErrorResponse(SipServletResponse response) {
		SipSession session = response.getSession(false);
		int errCode = response.getStatus();
		gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
				DonkeyGearmanMessage.user_sip_error, eventName,
				checkFirstInviteSession(session), Integer.toString(errCode)));
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
