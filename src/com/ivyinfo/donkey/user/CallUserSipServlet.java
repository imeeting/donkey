package com.ivyinfo.donkey.user;

import java.io.IOException;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.ivyinfo.donkey.CallStatus;
import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.db.cdr.CdrBean;
import com.ivyinfo.donkey.db.cdr.CdrManager;
import com.ivyinfo.donkey.http.api.DonkeyGearman;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.sip.BaseSipServlet;
import com.ivyinfo.donkey.sip.SIPHelper;

/**
 * SipServlet implementation class ControlChannelSipServlet
 */
@javax.servlet.sip.annotation.SipServlet
public class CallUserSipServlet extends BaseSipServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger
			.getLogger(CallUserSipServlet.class);

	protected static DonkeyGearman gearman = DonkeyGearman.getInstance();

	@Override
	protected void doBye(SipServletRequest req) {
		SipSession session = req.getSession(false);
		SipSession linkedSession = (SipSession) session
				.getAttribute(ConferenceManager.LINKED_SESSION);

		logger.info("SIP BYE From : " + req.getFrom().toString());

		// user session
		if (null != session.getAttribute(ConferenceManager.INVITE_USER_REQUEST)) {
			doBye(session, req);
		}
		// ms session
		else if (null != session
				.getAttribute(ConferenceManager.INVITE_MS_REQUEST)) {
			logger.info("MS do bye, because we send destroy conference request!");

			doBye(linkedSession, req);
		}
	}

	private void doBye(SipSession session, SipServletRequest request) {
		// define hangup update cdrBean, the invite session startTime and its
		// endTime
		Long startTime = 0L;
		Long endTime = 0L;
		CdrBean updateBean = CdrManager.getUpdateCdrBean(session);

		try {
			// update invite userBean endTime and callSattus
			updateBean.setEndtime(System.currentTimeMillis());
			updateBean.setState(CallStatus.Hangup.val());
			CdrManager.updateCdr(updateBean);

			CdrBean bean = CdrManager.queryCdrs(updateBean).get(0);
			startTime = bean.getStarttime();
			endTime = bean.getEndtime();

			logger.info("Session: " + session.getId() + " call-id: "
					+ session.getCallId() + " doBye - StartTime: " + startTime
					+ " EndTime: " + endTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// send bye to its linked user session and notify
		SipSession reqSession = request.getSession(false);
		if (null != reqSession
				.getAttribute(ConferenceManager.INVITE_MS_REQUEST)) {
			logger.info("ok");

			SipSession msSession = (SipSession) session
					.getAttribute(ConferenceManager.LINKED_SESSION);

			gearman.submit(DonkeyGearmanMessage.DonkeyUserUnJoin(msSession,
					startTime, endTime));

			SipServletRequest bye = session.createRequest(SIPHelper.BYE);
			try {
				bye.send();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// send 200OK to the session
		SipServletResponse resp = request
				.createResponse(SipServletResponse.SC_OK);
		try {
			resp.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
