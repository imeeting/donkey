package com.ivyinfo.donkey.sip;

import java.io.IOException;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSessionEvent;

import org.apache.log4j.Logger;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.db.cdr.CdrBean;
import com.ivyinfo.donkey.db.cdr.CdrManager;
import com.ivyinfo.donkey.http.api.DonkeyGearman;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;

/**
 * SipListener class AppSessionListener
 */
@javax.servlet.sip.annotation.SipListener
public class AppSessionListener implements
		javax.servlet.sip.SipApplicationSessionListener,
		javax.servlet.sip.SipSessionListener {

	private static final Logger logger = Logger
			.getLogger(AppSessionListener.class);

	public void sessionCreated(javax.servlet.sip.SipApplicationSessionEvent sase) {
		// TODO -- add implementation, if necessary
	}

	public void sessionDestroyed(
			javax.servlet.sip.SipApplicationSessionEvent sase) {
		// TODO -- add implementation, if necessary
	}

	public void sessionExpired(javax.servlet.sip.SipApplicationSessionEvent sase) {
		// TODO -- add implementation, if necessary
	}

	public void sessionReadyToInvalidate(SipApplicationSessionEvent sase) {
		SipApplicationSession sipAppSession = sase.getApplicationSession();
		logger.debug("SipApplicationSession[" + sipAppSession.getId()
				+ "] Ready To Invalidate");
	}

	public void sessionReadyToInvalidate(SipSessionEvent sse) {
		SipSession session = sse.getSession();

		logger.info("Session" + session.getId() + " its call-id: "
				+ session.getCallId() + " is ready to invalidate!");

		if (null == session.getAttribute(ConferenceManager.INVITE_USER_REQUEST)) {
			logger.info("The session not a user invite session, return immediately!");
			return;
		}

		SipSession linkedSession = (SipSession) session
				.getAttribute(ConferenceManager.LINKED_SESSION);
		if (null != linkedSession && linkedSession.isValid()) {
			// update call cdr
			Long startTime = 0L;
			Long endTime = 0L;

			CdrBean bean;
			try {
				bean = CdrManager.queryCdrs(
						CdrManager.getUpdateCdrBean(session)).get(0);

				if (null != bean) {
					logger.info("Unjoin conference request handle - StartTime: "
							+ bean.getStarttime()
							+ " EndTime: "
							+ bean.getEndtime());
					startTime = bean.getStarttime();
					endTime = bean.getEndtime();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			DonkeyGearman.getInstance().submit(
					DonkeyGearmanMessage.DonkeyUserUnJoin(linkedSession,
							startTime, endTime));

			// send bye to ms
			SipServletRequest bye = linkedSession.createRequest(SIPHelper.BYE);
			try {
				bye.send();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sessionCreated(SipSessionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionDestroyed(SipSessionEvent arg0) {
		// TODO Auto-generated method stub

	}
}
