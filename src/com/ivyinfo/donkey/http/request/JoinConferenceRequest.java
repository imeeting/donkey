package com.ivyinfo.donkey.http.request;

import java.net.InetSocketAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Configuration;
import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.db.cdr.CdrBean;
import com.ivyinfo.donkey.db.cdr.CdrManager;
import com.ivyinfo.donkey.http.api.DonkeyGearman;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.http.api.DonkeyResponseMessage;
import com.ivyinfo.donkey.sip.ISIPResponseHandler;
import com.ivyinfo.donkey.sip.SDPHelper;
import com.ivyinfo.donkey.sip.SIPHelper;
import com.ivyinfo.donkey.sip.thirdpartycc.InviteAFirstHandler;
import com.ivyinfo.donkey.user.CallUserSipServlet;

public class JoinConferenceRequest extends DonkeyRequest {

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
		String to = userSipUri;
		if (!to.startsWith("sip:")) {
			to = "sip:" + to + "@donkey.com";
		}

		try {
			SipServletRequest invite = ConferenceManager.getSipFactory().createRequest(
					sipAppSession, SIPHelper.INVITE, from, to);
			invite.setContent(SDPHelper.getNoMediaSDP(""),
					SDPHelper.SDP_CONTENT_TYPE);
			String ssSipURI = Configuration.getSoftSwitchSipURI();
			Address routeAddr = ConferenceManager.getSipFactory()
					.createAddress(ssSipURI);
			// setting lr parameter is important
			routeAddr.setParameter("lr", "");
			invite.pushRoute(routeAddr);

			SipSession session = invite.getSession();
			// remember this session so we can unjoin this call.
			session.setAttribute(ConferenceManager.INVITE_USER_REQUEST, invite);
			session.setAttribute(ConferenceManager.USER_SIP_URI, userSipUri);

			String outboundIPAddr = Configuration
					.getOutboundIpAddrToSoftSwitch();
			Integer port = Configuration.getOutboundPort();
			InetSocketAddress address = new InetSocketAddress(outboundIPAddr,
					port);
			session.setOutboundInterface(address);

			String cseq = invite.getHeader("CSeq");
			session.setAttribute(cseq + ISIPResponseHandler.RESPONSE_HANDLER,
					new InviteAFirstHandler(ConferenceManager.getSipFactory()));
			session.setHandler(CallUserSipServlet.class.getSimpleName());

			invite.send();

			DonkeyResponse.Accepted(response, DonkeyResponseMessage
					.AttendeeSession(sipAppSession.getId(), session.getId()));

			// submit conf.create calling status
			DonkeyGearman.getInstance().submit(
					DonkeyGearmanMessage.DonkeyUserJoining(session));

			// add new donkey cdr
			CdrBean bean = CdrManager.generateCdrBean(session, response);
			if (null == bean) {
				throw new Exception(Constant.nullCdrBeanExceptionMsg);
			}
			CdrManager.addCdr(bean);
		} catch (Exception e) {
			DonkeyResponse.InternalException(response, e);
		}
	}

}
