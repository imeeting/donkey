package com.ivyinfo.donkey.sip.thirdpartycc;

import java.net.InetSocketAddress;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.CallStatus;
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

public class InviteAFirstHandler extends BaseSIPResponseHandler {

	private static final String eventType = DonkeyGearmanMessage.conf_join;

	private SipFactory sipFactory;

	public InviteAFirstHandler(SipFactory sipFactory) {
		this.sipFactory = sipFactory;
	}

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		SipSession sessionA = response.getSession(false);
		SipApplicationSession sipAppSession = sessionA.getApplicationSession();
		try {
			SipServletRequest ack = response.createAck();
			ack.send();

			String from = Configuration.getSipUri();
			String to = Configuration.getMediaServerSipUri();
			SipServletRequest invite = sipFactory.createRequest(sipAppSession,
					SIPHelper.INVITE, from, to);

			SipSession sessionB = invite.getSession();
			sessionB.setAttribute(ConferenceManager.LINKED_SESSION, sessionA);
			sessionA.setAttribute(ConferenceManager.LINKED_SESSION, sessionB);

			String outboundIPAddr = Configuration
					.getOutboundIpAddrToSoftSwitch();
			Integer port = Configuration.getOutboundPort();
			InetSocketAddress address = new InetSocketAddress(outboundIPAddr,
					port);
			sessionB.setOutboundInterface(address);

			String cseq = invite.getHeader("CSeq");
			sessionB.setAttribute(cseq + ISIPResponseHandler.RESPONSE_HANDLER,
					new InviteBHandler());
			sessionB.setHandler(CallUserSipServlet.class.getSimpleName());

			invite.send();
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

		try {
			CdrBean updateBean = CdrManager.getUpdateCdrBean(session);
			if (null == updateBean) {
				throw new Exception(Constant.nullCdrBeanExceptionMsg);
			}
			updateBean.setState(CallStatus.CallFailed.val());
			CdrManager.updateCdr(updateBean);
		} catch (Exception e) {
			gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
					DonkeyGearmanMessage.user_sip_error, eventType, session,
					e.getMessage()));
		}

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
