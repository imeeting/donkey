package com.ivyinfo.donkey.http.request;

import java.net.InetSocketAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Configuration;
import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.http.api.DonkeyResponseMessage;
import com.ivyinfo.donkey.sip.ISIPResponseHandler;
import com.ivyinfo.donkey.sip.SIPHelper;
import com.ivyinfo.donkey.sip.thirdpcc.InviteMSHandler;
import com.ivyinfo.donkey.user.CallUserSipServlet;

public class JoinConferenceRequest2 extends DonkeyRequest {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		SipApplicationSession sipAppSession = getSipApplicationSession(request,
				response);
		if (sipAppSession == null) {
			return;
		}

		String userSipUri = request.getParameter(Constant.SipURI);
		if (null == userSipUri || userSipUri.length() <= 0) {
			DonkeyResponse.BadRequest(response);
			return;
		}

		String from = Configuration.getSipUri();
		String to = Configuration.getMediaServerSipUri();

		SipServletRequest invite;
		try {
			invite = ConferenceManager.getSipFactory().createRequest(
					sipAppSession, SIPHelper.INVITE, from, to);

			SipSession session = invite.getSession();
			// remember the userSipUri
			session.setAttribute(ConferenceManager.USER_SIP_URI, userSipUri);
			// remember the first invite sipSession then we can notify its
			// sessionID
			session.setAttribute(ConferenceManager.FIRST_INVITE_REQUEST, invite);
			// remember the invite ms request sipSession then we can cancel the
			// ms invite request
			session.setAttribute(ConferenceManager.INVITE_MS_REQUEST, invite);

			String outboundIPAddr = Configuration
					.getOutboundIpAddrToSoftSwitch();
			Integer port = Configuration.getOutboundPort();
			InetSocketAddress address = new InetSocketAddress(outboundIPAddr,
					port);
			session.setOutboundInterface(address);

			String cseq = invite.getHeader("CSeq");
			session.setAttribute(cseq + ISIPResponseHandler.RESPONSE_HANDLER,
					new InviteMSHandler(ConferenceManager.getSipFactory()));
			session.setHandler(CallUserSipServlet.class.getSimpleName());

			invite.send();

			DonkeyResponse.Accepted(response, DonkeyResponseMessage
					.AttendeeSession(sipAppSession.getId(), session.getId()));
		} catch (Exception e) {
			DonkeyResponse.InternalException(response, e);
		}
	}

}
