package com.ivyinfo.donkey.sip.thirdpartycc;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.ivyinfo.donkey.CallStatus;
import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.db.cdr.CdrBean;
import com.ivyinfo.donkey.db.cdr.CdrManager;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.ms.IMediaServer;
import com.ivyinfo.donkey.sip.BaseSIPResponseHandler;
import com.ivyinfo.donkey.sip.BaseSipServlet;

public class InviteASecondHandler extends BaseSIPResponseHandler {

	private static final String eventType = DonkeyGearmanMessage.conf_join;

	private static final Logger logger = Logger.getLogger(InviteASecondHandler.class);
	
	private SipServletResponse bResponse;

	public InviteASecondHandler(SipServletResponse response) {
		this.bResponse = response;
	}

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		logger.debug("success response code : " + response.getStatus());
		SipSession sessionA = response.getSession(false);
		SipApplicationSession sipAppSession = response
				.getApplicationSession(false);
		try {
			SipServletRequest ackB = bResponse.createAck();
			ackB.setContent(response.getContent(), response.getContentType());
			ackB.send();

			SipServletRequest ackA = response.createAck();
			ackA.send();

			String conn = bResponse.getTo().getParameter("tag");
			sessionA.setAttribute(ConferenceManager.USER_CONN, conn);

			IMediaServer ms = (IMediaServer) sipAppSession
					.getAttribute(IMediaServer.MEDIA_SERVER);
			ms.joinConference(sipAppSession, sessionA);

			gearman.submit(DonkeyGearmanMessage.DonkeyUserJoin(sessionA));

			// donkey call connected succeed cdr
			CdrBean updateBean = CdrManager.getUpdateCdrBean(sessionA);
			if (null == updateBean) {
				throw new Exception(Constant.nullCdrBeanExceptionMsg);
			}
			updateBean.setStarttime(System.currentTimeMillis());
			updateBean.setState(CallStatus.Connected.val());
			CdrManager.updateCdr(updateBean);
		} catch (Exception e) {
			gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
					DonkeyGearmanMessage.user_exception, eventType, sessionA,
					e.getMessage()));
		}
	}

	@Override
	public void doErrorResponse(SipServletResponse response) {
		SipSession session = response.getSession(false);
		int statusCode = response.getStatus();
		logger.debug("error response code : " + response.getStatus());
		gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
				DonkeyGearmanMessage.user_sip_error, eventType, session,
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
