package com.ivyinfo.donkey.sip.thirdpcc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.CallStatus;
import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.db.cdr.CdrBean;
import com.ivyinfo.donkey.db.cdr.CdrManager;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.ms.IMediaServer;
import com.ivyinfo.donkey.sip.BaseSIPResponseHandler;

public class InviteAttendeeHandler extends BaseSIPResponseHandler {

	private static final String eventType = DonkeyGearmanMessage.conf_join;

	private SipServletResponse msResponse;

	public InviteAttendeeHandler(SipServletResponse response) {
		this.msResponse = response;
	}

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		// get callee sipSession and linkedSession caller sipSession
		SipSession sessionAttendee = response.getSession(false);
		SipApplicationSession sipAppSession = response
				.getApplicationSession(false);

		// get response status
		int status = response.getStatus();

		SipServletRequest ackAttendee = response.createAck();
		try {
			ackAttendee.send();

			// process 200 ok of inviteB
			if (SipServletResponse.SC_OK == status) {
				String conn = msResponse.getTo().getParameter("tag");
				sessionAttendee.setAttribute(ConferenceManager.USER_CONN, conn);

				IMediaServer ms = (IMediaServer) sipAppSession
						.getAttribute(IMediaServer.MEDIA_SERVER);
				ms.joinConference(sipAppSession, sessionAttendee);

				gearman.submit(DonkeyGearmanMessage
						.DonkeyUserJoin(checkFirstInviteSession(sessionAttendee)));

				// donkey call connected succeed cdr
				CdrBean updateBean = CdrManager
						.getUpdateCdrBean(sessionAttendee);
				if (null == updateBean) {
					throw new Exception(Constant.nullCdrBeanExceptionMsg);
				}
				updateBean.setStarttime(System.currentTimeMillis());
				updateBean.setState(CallStatus.Connected.val());
				CdrManager.updateCdr(updateBean);
			}
		} catch (Exception e) {
			gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
					DonkeyGearmanMessage.user_exception, eventType,
					checkFirstInviteSession(sessionAttendee), e.getMessage()));
		}
	}

	@Override
	public void doErrorResponse(SipServletResponse response) {
		SipSession sessionAttendee = response.getSession(false);
		int statusCode = response.getStatus();

		try {
			CdrBean updateBean = CdrManager.getUpdateCdrBean(sessionAttendee);
			if (null == updateBean) {
				throw new Exception(Constant.nullCdrBeanExceptionMsg);
			}
			updateBean.setState(CallStatus.CallFailed.val());
			CdrManager.updateCdr(updateBean);
		} catch (Exception e) {
			gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
					DonkeyGearmanMessage.user_sip_error, eventType,
					checkFirstInviteSession(sessionAttendee), e.getMessage()));
		}

		gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
				DonkeyGearmanMessage.user_sip_error, eventType,
				checkFirstInviteSession(sessionAttendee),
				Integer.toString(statusCode)));
	}

	@Override
	public void doProvisionalResponse(SipServletResponse response) {
		SipSession sessionAttendee = response.getSession(false);
		int statusCode = response.getStatus();

		try {
			if (SipServletResponse.SC_SESSION_PROGRESS == statusCode) {
				SipServletRequest ackMS = msResponse.createAck();
				ackMS.setContent(response.getContent(),
						response.getContentType());
				ackMS.send();

				// submit conf.join calling status
				gearman.submit(DonkeyGearmanMessage
						.DonkeyUserJoining(checkFirstInviteSession(sessionAttendee)));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
