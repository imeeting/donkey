package com.ivyinfo.donkey.sip.thirdpcc;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.servlet.sip.Address;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Configuration;
import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.db.cdr.CdrBean;
import com.ivyinfo.donkey.db.cdr.CdrManager;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.sip.BaseSIPResponseHandler;
import com.ivyinfo.donkey.sip.ISIPResponseHandler;
import com.ivyinfo.donkey.sip.SIPHelper;
import com.ivyinfo.donkey.user.CallUserSipServlet;
import com.ivyinfo.util.DonkeyUtil;

public class InviteMSHandler extends BaseSIPResponseHandler {

	private static final String eventType = DonkeyGearmanMessage.conf_join;

	private static final Logger logger = Logger
			.getLogger(InviteMSHandler.class);

	private SipFactory sipFactory;

	public InviteMSHandler(SipFactory sipFactory) {
		this.sipFactory = sipFactory;
	}

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		SipSession sessionMS = response.getSession(false);
		SipApplicationSession sipAppSession = sessionMS.getApplicationSession();

		try {
			String from = Configuration.getSipUri();
			String to = DonkeyUtil.checkSipUri((String) sessionMS
					.getAttribute(ConferenceManager.USER_SIP_URI));

			logger.info("User sipUri: " + to);

			SipServletRequest invite = sipFactory.createRequest(sipAppSession,
					SIPHelper.INVITE, from, to);

			// set invite to B content with SDP of A
			invite.setContent(response.getContent(), response.getContentType());

			// set route address
			Address routeAddr = ConferenceManager.getSipFactory()
					.createAddress(Configuration.getSoftSwitchSipURI());
			// set lr parameter, it is important
			routeAddr.setParameter("lr", "");
			invite.pushRoute(routeAddr);

			SipSession sessionAttendee = invite.getSession();
			sessionAttendee.setAttribute(ConferenceManager.LINKED_SESSION,
					sessionMS);
			sessionAttendee.setAttribute(ConferenceManager.INVITE_USER_REQUEST,
					invite);
			sessionAttendee.setAttribute(ConferenceManager.USER_SIP_URI, to);
			sessionMS.setAttribute(ConferenceManager.LINKED_SESSION,
					sessionAttendee);

			String outboundIPAddr = Configuration
					.getOutboundIpAddrToSoftSwitch();
			Integer port = Configuration.getOutboundPort();
			InetSocketAddress address = new InetSocketAddress(outboundIPAddr,
					port);
			sessionAttendee.setOutboundInterface(address);

			String cseq = invite.getHeader("CSeq");
			sessionAttendee.setAttribute(cseq
					+ ISIPResponseHandler.RESPONSE_HANDLER,
					new InviteAttendeeHandler(response));
			sessionAttendee
					.setHandler(CallUserSipServlet.class.getSimpleName());

			invite.send();

			// add new donkey cdr
			CdrBean bean = CdrManager.generateCdrBean(sessionAttendee);
			if (null == bean) {
				throw new Exception(Constant.nullCdrBeanExceptionMsg);
			}
			CdrManager.addCdr(bean);
		} catch (Exception e) {
			logger.error("Invite user error!");

			// call user error, send cancel to ms
			SipServletRequest invite = (SipServletRequest) sessionMS
					.getAttribute(ConferenceManager.INVITE_MS_REQUEST);
			SipServletRequest cancel = invite.createCancel();
			try {
				cancel.send();
			} catch (IOException e1) {
				e1.printStackTrace();

				logger.error("Send cancel ms invite sip servlet request error!");
			}

			gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
					DonkeyGearmanMessage.user_exception, eventType, sessionMS,
					e.getMessage()));
		}
	}

	@Override
	public void doErrorResponse(SipServletResponse response) {
		SipSession sessionMS = response.getSession(false);
		int statusCode = response.getStatus();

		gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
				DonkeyGearmanMessage.user_sip_error, eventType, sessionMS,
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
