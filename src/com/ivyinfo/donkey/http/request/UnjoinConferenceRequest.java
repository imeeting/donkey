package com.ivyinfo.donkey.http.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSession.State;

import com.ivyinfo.donkey.CallStatus;
import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.db.cdr.CdrBean;
import com.ivyinfo.donkey.db.cdr.CdrManager;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.sip.ISIPResponseHandler;
import com.ivyinfo.donkey.sip.SIPHelper;
import com.ivyinfo.donkey.user.UnjoinUserHandler;

public class UnjoinConferenceRequest extends DonkeyRequest {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {

		SipSession sipSession = checkAttendeeSession(getSipSession(request,
				response));
		if (null == sipSession) {
			return;
		}

		State state = sipSession.getState();
		try {
			if (state.equals(State.EARLY) || state.equals(State.INITIAL)) {
				cancelInviteRequest(sipSession);
			} else if (state.equals(State.CONFIRMED)) {
				SipServletRequest bye = sipSession.createRequest(SIPHelper.BYE);
				String cseq = bye.getHeader("CSeq");
				sipSession.setAttribute(cseq
						+ ISIPResponseHandler.RESPONSE_HANDLER,
						new UnjoinUserHandler());
				bye.send();
			}
		} catch (IOException e) {
			e.printStackTrace();
			DonkeyResponse.InternalException(response, e);
		}

		DonkeyResponse.Accepted(response);

		try {
			CdrBean updateBean = CdrManager.getUpdateCdrBean(sipSession);
			if (null == updateBean) {
				throw new Exception(Constant.nullCdrBeanExceptionMsg);
			}
			updateBean.setEndtime(System.currentTimeMillis());
			updateBean.setState(CallStatus.Hangup.val());
			CdrManager.updateCdr(updateBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
