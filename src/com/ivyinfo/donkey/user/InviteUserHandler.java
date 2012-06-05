package com.ivyinfo.donkey.user;

import java.net.InetSocketAddress;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Configuration;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.ms.IMediaServer;
import com.ivyinfo.donkey.ms.msml.InviteMediaServerHandler;
import com.ivyinfo.donkey.sip.BaseSIPResponseHandler;
import com.ivyinfo.donkey.sip.ISIPResponseHandler;
import com.ivyinfo.donkey.sip.SIPHelper;

@Deprecated
public class InviteUserHandler extends BaseSIPResponseHandler {

	private static final String eventName = DonkeyGearmanMessage.conf_join;

	private SipFactory sipFactory;

	public InviteUserHandler(SipFactory sipFactory) {
		this.sipFactory = sipFactory;
	}

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		SipApplicationSession sipAppSession = response
				.getApplicationSession(false);
		IMediaServer ms = (IMediaServer) sipAppSession
				.getAttribute(IMediaServer.MEDIA_SERVER);
		String from = response.getTo().getValue();
		String to = ms.getSipURI();
		SipSession userSession = null;
		try {
			SipServletRequest invite = sipFactory.createRequest(sipAppSession,
					SIPHelper.INVITE, from, to);
			invite.setContent(response.getContent(), response.getContentType());

			String cseq = invite.getHeader("CSeq");

			SipSession msSession = invite.getSession();
			userSession = response.getSession(false);
			msSession.setAttribute(ConferenceManager.LINKED_SESSION,
					userSession);
			userSession.setAttribute(ConferenceManager.LINKED_SESSION,
					msSession);
			userSession.removeAttribute(ConferenceManager.INVITE_USER_REQUEST);

			msSession.setHandler(CallUserSipServlet.class.getSimpleName());
			msSession.setAttribute(cseq + ISIPResponseHandler.RESPONSE_HANDLER,
					new InviteMediaServerHandler(response));

			String outboundIPAddr = Configuration
					.getOutboundIpAddrToMediaServer();
			Integer port = Configuration.getOutboundPort();
			InetSocketAddress address = new InetSocketAddress(outboundIPAddr,
					port);
			msSession.setOutboundInterface(address);
			invite.send();
		} catch (Exception e) {
			gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
					DonkeyGearmanMessage.user_exception, eventName,
					userSession, e.getMessage()));
		}
	}

	@Override
	public void doErrorResponse(SipServletResponse response) {
		SipSession session = response.getSession(false);
		int statusCode = response.getStatus();
		gearman.submit(DonkeyGearmanMessage.DonkeyUserException(
				DonkeyGearmanMessage.user_sip_error, eventName, session,
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
