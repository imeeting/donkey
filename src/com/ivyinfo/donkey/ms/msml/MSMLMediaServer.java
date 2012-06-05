package com.ivyinfo.donkey.ms.msml;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Configuration;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.ms.ControlChannelSipServlet;
import com.ivyinfo.donkey.ms.IMediaServer;
import com.ivyinfo.donkey.sip.ISIPResponseHandler;
import com.ivyinfo.donkey.sip.SDPHelper;
import com.ivyinfo.donkey.sip.SIPHelper;
import com.ivyinfo.util.RandomString;

public class MSMLMediaServer implements IMediaServer {

	private String sipUri;

	public MSMLMediaServer(String sipuri) {
		this.sipUri = sipuri;
	}

	@Override
	public void createConference(SipFactory sipFactory,
			SipApplicationSession sipAppSession) throws ServletException,
			IOException {
		String from = Configuration.getSipUri();

		SipServletRequest invite = sipFactory.createRequest(sipAppSession,
				SIPHelper.INVITE, from, this.sipUri);
		invite.setContent(SDPHelper.getNoMediaSDP(from),
				SDPHelper.SDP_CONTENT_TYPE);
		String cseq = invite.getHeader("CSeq");

		SipSession controlSession = invite.getSession();
		controlSession.setHandler(ControlChannelSipServlet.class
				.getSimpleName());
		controlSession.setAttribute(
				cseq + ISIPResponseHandler.RESPONSE_HANDLER,
				new CreateControlChannelHandler());

		String outboundIPAddr = Configuration.getOutboundIpAddrToMediaServer();
		Integer port = Configuration.getOutboundPort();
		InetSocketAddress address = new InetSocketAddress(outboundIPAddr, port);
		controlSession.setOutboundInterface(address);

		invite.send();
	}

	@Override
	public void destroyConference(SipApplicationSession sipAppSession)
			throws IOException {
		SipSession controlSession = (SipSession) sipAppSession
				.getAttribute(ConferenceManager.CONTROL_SESSION);

		String confid = (String) sipAppSession
				.getAttribute(ConferenceManager.CONF_ID);

		SipServletRequest info = controlSession.createRequest(SIPHelper.INFO);
		info.setContent(MSMLHelper.destroyconference(confid),
				MSMLHelper.MSML_CONTENT_TYPE);
		String cseq = info.getHeader("CSeq");
		controlSession.setAttribute(
				cseq + ISIPResponseHandler.RESPONSE_HANDLER,
				new InfoDestroyConferenceHandle());

		info.send();
	}

	@Override
	public void joinConference(SipApplicationSession sipAppSession,
			SipSession sipSession) throws Exception {
		SipSession controlSession = (SipSession) sipAppSession
				.getAttribute(ConferenceManager.CONTROL_SESSION);
		String confid = (String) sipAppSession
				.getAttribute(ConferenceManager.CONF_ID);
		String conn = (String) sipSession
				.getAttribute(ConferenceManager.USER_CONN);
//		String connRandomDialog = RandomString.genRandomNum(6);

		SipServletRequest info = controlSession.createRequest(SIPHelper.INFO);
//		info.setContent(MSMLHelper.join_notice(conn, connRandomDialog, confid),
//				MSMLHelper.MSML_CONTENT_TYPE);
		info.setContent(MSMLHelper.join(conn, confid), MSMLHelper.MSML_CONTENT_TYPE);
		String handlerName = info.getHeader("CSeq")
				+ ISIPResponseHandler.RESPONSE_HANDLER;
		controlSession.setAttribute(handlerName, new InfoMediaServerHandler(
				DonkeyGearmanMessage.conf_join, sipSession));

		info.send();
	}

	@Override
	public void muteConnection(SipApplicationSession sipAppSession,
			SipSession sipSession, boolean indicateFlag) throws IOException {
		SipSession controlSession = (SipSession) sipAppSession
				.getAttribute(ConferenceManager.CONTROL_SESSION);
		String conn = (String) sipSession
				.getAttribute(ConferenceManager.USER_CONN);
		String confid = (String) sipAppSession
				.getAttribute(ConferenceManager.CONF_ID);

		SipServletRequest info = controlSession.createRequest(SIPHelper.INFO);
		info.setContent(MSMLHelper.muteconnection(conn, confid, indicateFlag),
				MSMLHelper.MSML_CONTENT_TYPE);
		String cseq = info.getHeader("CSeq");
		controlSession
				.setAttribute(cseq + ISIPResponseHandler.RESPONSE_HANDLER,
						new InfoMediaServerHandler(
								indicateFlag ? DonkeyGearmanMessage.conn_mute
										: DonkeyGearmanMessage.conn_unmute,
								sipSession));

		info.send();
	}

	@Override
	public void announceConference(SipApplicationSession sipAppSession,
			String announcementName, boolean indicateFlag) throws IOException {
		SipSession controlSession = (SipSession) sipAppSession
				.getAttribute(ConferenceManager.CONTROL_SESSION);
		String confid = (String) sipAppSession
				.getAttribute(ConferenceManager.CONF_ID);

		SipServletRequest info = controlSession.createRequest(SIPHelper.INFO);
		info.setContent(
				MSMLHelper.announcement(confid, announcementName, indicateFlag),
				MSMLHelper.MSML_CONTENT_TYPE);
		String cseq = info.getHeader("CSeq");
		controlSession.setAttribute(
				cseq + ISIPResponseHandler.RESPONSE_HANDLER,
				new InfoMediaServerHandler(DonkeyGearmanMessage.conf_announce,
						null));

		info.send();
	}

	@Override
	public void recordConference(SipApplicationSession sipAppSession,
			String recordMaxtime, boolean indicateFlag) throws Exception {
		SipSession controlSession = (SipSession) sipAppSession
				.getAttribute(ConferenceManager.CONTROL_SESSION);
		String confid = (String) sipAppSession
				.getAttribute(ConferenceManager.CONF_ID);

		String recordidentifier = (String) sipAppSession
				.getAttribute(ConferenceManager.CONF_RECIDEN);
		if (recordidentifier == null) {
			recordidentifier = RandomString.genRandomChars(10);
			sipAppSession.setAttribute(ConferenceManager.CONF_RECIDEN,
					recordidentifier);
		}

		String nfshost = Configuration.getNFSHost();
		StringBuffer recordFileName = new StringBuffer();
		recordFileName.append(nfshost);
		recordFileName.append("/record/");
		recordFileName.append(recordidentifier);

		SipServletRequest info = controlSession.createRequest(SIPHelper.INFO);
		if (null == recordMaxtime || recordMaxtime.length() <= 0) {
			recordMaxtime = "0";
		}
		info.setContent(MSMLHelper.recordconference(confid,
				recordFileName.toString(), Long.parseLong(recordMaxtime),
				indicateFlag), MSMLHelper.MSML_CONTENT_TYPE);
		String cseq = info.getHeader("CSeq");
		controlSession.setAttribute(
				cseq + ISIPResponseHandler.RESPONSE_HANDLER,
				new InfoMediaServerHandler(DonkeyGearmanMessage.conf_record,
						null));

		info.send();
	}

	@Override
	public String getSipURI() {
		return this.sipUri;
	}

}
