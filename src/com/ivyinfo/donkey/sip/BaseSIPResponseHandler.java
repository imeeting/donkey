package com.ivyinfo.donkey.sip;

import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.http.api.DonkeyGearman;
import com.ivyinfo.util.DonkeyUtil;

public abstract class BaseSIPResponseHandler implements ISIPResponseHandler {

	protected static DonkeyGearman gearman = DonkeyGearman.getInstance();

	protected SipSession checkFirstInviteSession(SipSession session) {
		return DonkeyUtil.checkMatchedSession(session,
				ConferenceManager.FIRST_INVITE_REQUEST);
	}

	@Override
	public abstract void doSuccessResponse(SipServletResponse response);

	@Override
	public abstract void doProvisionalResponse(SipServletResponse response);

	@Override
	public abstract void doBranchResponse(SipServletResponse response);

	@Override
	public abstract void doRedirectResponse(SipServletResponse response);

	@Override
	public abstract void doErrorResponse(SipServletResponse response);

}
