package com.ivyinfo.donkey;

import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipSessionsUtil;

public class ConferenceManager {

	public static final String Create = "create";
	public static final String Destroy = "destroy";
	public static final String Join = "join";
	public static final String Unjoin = "unjoin";
	public static final String Mute = "mute";
	public static final String Unmute = "unmute";
	public static final String Announce = "announce";
	public static final String Stopannounce = "stopannounce";
	public static final String Record = "record";
	public static final String Stoprecord = "stoprecord";

	public static final String CONF_ID = "confid";
	public static final String CONTROL_SESSION = "control_session";
	public static final String LINKED_SESSION = "linked_session";
	public static final String INVITE_USER_REQUEST = "invite_user_request";
	public static final String INVITE_MS_REQUEST = "invite_ms_request";
	public static final String FIRST_INVITE_REQUEST = "first_invite_request";
	public static final String USER_SIP_URI = "sip_uri";

	public static final String USER_CONN = "user_conn";

	public static final String CONF_RECIDEN = "conf_record_identifier";

	private static SipFactory sipFactory;

	private static SipSessionsUtil sipSessionUtil;

	public static void setSipFactory(SipFactory factory) {
		sipFactory = factory;
	}

	public static SipFactory getSipFactory() {
		return sipFactory;
	}

	public static void setSipSessionsUtil(SipSessionsUtil util) {
		sipSessionUtil = util;
	}

	public static SipSessionsUtil getSipSessionsUtil() {
		return sipSessionUtil;
	}
	/*
	 * private void joinConference(SipApplicationSession sipAppSession, String
	 * userSipUri) throws IOException, ServletException { String from =
	 * Configuration.getSipUri(); SipServletRequest invite =
	 * sipFactory.createRequest(sipAppSession, SIPHelper.INVITE, from,
	 * userSipUri);
	 * 
	 * String ssSipURI = Configuration.getSoftSwitchSipURI(); Address routeAddr
	 * = sipFactory.createAddress(ssSipURI); routeAddr.setParameter("lr", "");
	 * // setting lr parameter is important. invite.pushRoute(routeAddr);
	 * 
	 * SipSession session = invite.getSession(); // remember this session so we
	 * can unjoin this call. sipAppSession.setAttribute(userSipUri, session);
	 * session.setAttribute(INVITE_USER_REQUEST, invite);
	 * session.setAttribute(USER_SIP_URI, userSipUri);
	 * 
	 * String outboundIPAddr = Configuration.getOutboundIpAddrToSoftSwitch();
	 * Integer port = Configuration.getOutboundPort(); InetSocketAddress address
	 * = new InetSocketAddress(outboundIPAddr, port);
	 * session.setOutboundInterface(address);
	 * 
	 * String cseq = invite.getHeader("CSeq"); session.setAttribute(cseq +
	 * ISIPResponseHandler.RESPONSE_HANDLER, new InviteUserHandler(sipFactory));
	 * 
	 * session.setHandler(BaseSipServlet.class.getSimpleName()); invite.send();
	 * }
	 */
}
