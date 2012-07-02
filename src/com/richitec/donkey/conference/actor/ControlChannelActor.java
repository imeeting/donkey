package com.richitec.donkey.conference.actor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.ivyinfo.donkey.ms.msml.MSMLHelper;
import com.ivyinfo.donkey.sip.SDPHelper;
import com.richitec.donkey.ContextLoader;
import com.richitec.donkey.conference.GlobalConfig;
import com.richitec.donkey.conference.message.ActorMessage;
import com.richitec.donkey.conference.message.ActorMessage.CreateControlChannelComplete;
import com.richitec.donkey.conference.message.sip.SendSipRequestCompleteMsg;
import com.richitec.donkey.conference.message.sip.SendSipRequestErrorMsg;
import com.richitec.donkey.msml.AudiomixType;
import com.richitec.donkey.msml.GainAgcDatatype;
import com.richitec.donkey.msml.AudiomixType.Asn;
import com.richitec.donkey.msml.AudiomixType.NLoudest;
import com.richitec.donkey.msml.Msml;
import com.richitec.donkey.msml.Msml.Result;
import com.richitec.donkey.msml.StreamType;
import com.richitec.donkey.sip.servlet.ControlChannelSIPServlet;


/**
 * This actor is used for media server control channel.
 * 
 * @author huuguanghui
 *
 */
public class ControlChannelActor extends BaseActor {
	
	private static Log log = LogFactory.getLog(ControlChannelActor.class);
	
	public static final String Name = "controlChannel";
	public static final String Actor = "actor";
	public static final String NoMedia = "nomedia";
	public static final String NoControl = "nocontrol";
	public static final String INVITE = "INVITE";
	public static final String INFO = "INFO";
	public static final String BYE = "BYE";
	
	public static final String Cseq = "CSeq";
	
	private JAXBContext jc;
	private Unmarshaller ju;
	private Marshaller jm;
	
	private SipFactory sipFactory;
	private SipApplicationSession sipAppSession;
	private SipSession controlSession;
	private String mediaServerConfId;
	private String deleteWhen = NoMedia;
	private String destroyInfoCSeq;
	
	private GlobalConfig config;
	
	private enum State {EARLY, INVITE, CHANNEL_CREATED, CONF_CREATED};
	
	private State state = State.EARLY;
	
	public ControlChannelActor() throws JAXBException{
		this.sipFactory = ContextLoader.getSipFactory();
		this.sipAppSession = sipFactory.createApplicationSession();
		this.config = ContextLoader.getGlobalConfig();
		this.jc = JAXBContext.newInstance(Msml.class);
		this.ju = jc.createUnmarshaller();
		this.jm = jc.createMarshaller();
	}
	
	private void transition(State next){
		this.state = next;
	}
	
	@Override
	public void postStop(){
		
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof ActorMessage.CmdCreateConference){
			onCmdCreateConference((ActorMessage.CmdCreateConference) msg);
		} else
		if (msg instanceof ActorMessage.CreateControlChannelComplete) {
			onCreateControlChannelComplete((CreateControlChannelComplete) msg);
		} else
		if (msg instanceof ActorMessage.ControlChannelInfoResponse){
			onControlChannelInfoResponse((ActorMessage.ControlChannelInfoResponse) msg);
		} else
		if (msg instanceof ActorMessage.ControlChannelInfoRequest){
			onControlChannelInfoRequest((ActorMessage.ControlChannelInfoRequest) msg);
		} else
		if (msg instanceof ActorMessage.CmdDestroyConference) {
			onCmdDestroyConference((ActorMessage.CmdDestroyConference) msg);	
		} else 
		if (msg instanceof ActorMessage.EvtAttendeeCallEstablished){
			onEAttendeeCallEstablished((ActorMessage.EvtAttendeeCallEstablished) msg);
		} else
		if (msg instanceof ActorMessage.CmdMuteAttendee) {
			onCmdMuteAttendee((ActorMessage.CmdMuteAttendee) msg);
		} else 
		if (msg instanceof ActorMessage.CmdUnmuteAttendee) {
			onCmdUnmuteAttendee((ActorMessage.CmdUnmuteAttendee) msg);
		} else
		if (msg instanceof ActorMessage.SipSessionReadyToInvalidate) {
			onSipSessionReadyToInvalidate((ActorMessage.SipSessionReadyToInvalidate) msg);
		} else 
		if (msg instanceof SendSipRequestCompleteMsg){
			onSendSipRequestCompleteMsg((SendSipRequestCompleteMsg) msg);
		} else if (msg instanceof SendSipRequestErrorMsg) {
			onSendSipRequestErrorMsg((SendSipRequestErrorMsg) msg);
		} else {
			unhandled(msg);
		}
	}
	
	private void sendInfo(Object msmlRequest) throws JAXBException, UnsupportedEncodingException{
		Msml msml = new Msml();
		msml.getMsmlRequest().add(msmlRequest);
		
		SipServletRequest info = controlSession.createRequest(INFO);
		OutputStream os = new ByteArrayOutputStream();
		jm.marshal(msml, os);
		info.setContent(os.toString(), MSMLHelper.MSML_CONTENT_TYPE);
		if (msmlRequest instanceof Msml.Destroyconference){
			destroyInfoCSeq = info.getHeader(Cseq);
			log.debug("CSeq of INFO <destroyconference> : " + destroyInfoCSeq);
		}
		send(info);	
	}
	
	/**
	 * INVITE media server to establish SIP control channel.
	 * 
	 * @param msg
	 * @throws UnsupportedEncodingException
	 * @throws ServletException
	 */
	private void onCmdCreateConference(ActorMessage.CmdCreateConference msg) 
					throws UnsupportedEncodingException, ServletException{
		this.deleteWhen = msg.getDeleteWhen();
		
		SipServletRequest invite = sipFactory.createRequest(sipAppSession, 
				INVITE, config.getSipUri(), config.getMediaServerSipUri());
		invite.setContent(SDPHelper.getNoMediaSDP(),
				SDPHelper.SDP_CONTENT_TYPE);
		
		controlSession = invite.getSession();
		controlSession.setHandler(ControlChannelSIPServlet.class.getSimpleName());
		controlSession.setAttribute(Actor, getSelf());
		
		String outboundIPAddr = config.getOutboundIP();
		Integer port = config.getOutboundPort();
		InetSocketAddress address = new InetSocketAddress(outboundIPAddr, port);
		controlSession.setOutboundInterface(address);
		
		transition(State.INVITE);
		send(invite);
	}
	
	/**
	 * When control channel is established, INFO media server to create conference. 
	 * 
	 * @param msg
	 * @throws IOException
	 * @throws JAXBException 
	 */
	private void onCreateControlChannelComplete(CreateControlChannelComplete msg) throws IOException, JAXBException{
		int status = msg.getStatus();
		if (status == SipServletResponse.SC_OK){
			transition(State.CHANNEL_CREATED);
			Msml.Createconference createConference = new Msml.Createconference();
			createConference.setDeletewhen(deleteWhen);
			AudiomixType audioMix = new AudiomixType();
			audioMix.setId("audiomix");
			Asn asn = new Asn();
			asn.setRi("10s");
			NLoudest nloudest = new NLoudest();
			nloudest.setN(3);
			audioMix.setAsn(asn);
			audioMix.setNLoudest(nloudest);
			createConference.setAudiomix(audioMix);
			
			sendInfo(createConference);			
		} else {
			log.error("Cannot establish control channel with Media Server, SIP Status : " + status);
			getContext().parent().tell(
					ActorMessage.createConferenceError, getSelf());
		}
	}
	
	private Msml getMsmlFromContent(byte [] content) throws JAXBException{
		InputStream iStream = new ByteArrayInputStream(content);
		Msml msml = (Msml) this.ju.unmarshal(iStream);
		return msml;
	}
	
	private void onControlChannelInfoResponse(ActorMessage.ControlChannelInfoResponse msg) throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException, JAXBException{
		SipServletResponse sipResp = msg.getResponse();
		String contentType = sipResp.getContentType();
		if (!MSMLHelper.isMsmlContentType(contentType)){
			log.error("Error content type from Media Server : " + contentType);
			return;
		}
		
		Msml msml = getMsmlFromContent(sipResp.getRawContent());
		Result result = msml.getResult();
		
		if (State.CHANNEL_CREATED == this.state){
			String response = result.getResponse();
			if ("200".equals(response)) {
				List<JAXBElement<String>> confIdList = result.getConfidOrDialogid();
				if (confIdList.size()>0){
					mediaServerConfId = confIdList.get(0).getValue();
					transition(State.CONF_CREATED);
					getContext().parent().tell(
							ActorMessage.createConferenceSuccess, getSelf());
				}
			} else {
				log.error("Cannot create conference in Media Server.");
				String content = new String(sipResp.getRawContent());
				log.error("\nRespone from Media Server :\n" + content + "\n");
				getContext().parent().tell(
						ActorMessage.createConferenceError, getSelf());				
			}
		} else if (State.CONF_CREATED == this.state) {
			String cseq = sipResp.getHeader(Cseq);
			if (cseq.equals(destroyInfoCSeq)){
				byeControlChannel();
			}
		} else {
			//TODO: error state.
		}
	}
	
	private void onControlChannelInfoRequest(ActorMessage.ControlChannelInfoRequest msg) throws UnsupportedEncodingException, IOException, JAXBException{
		SipServletRequest request = msg.getRequest();
		String contentType = request.getContentType();
		if (!MSMLHelper.isMsmlContentType(contentType)){
			log.error("Error content type from Media Server : " + contentType);
			return;
		}
		Msml msml = getMsmlFromContent(request.getRawContent());
		Msml.Event event = msml.getEvent();
		String name = event.getName();
		if ("msml.conf.nomedia".equals(name)){
			log.debug("nomedia in conf: " + mediaServerConfId);
			byeControlChannel();
		}
	}
	
	private void onCmdDestroyConference(ActorMessage.CmdDestroyConference msg) throws UnsupportedEncodingException, JAXBException{
		if (NoControl.equals(deleteWhen)){
			byeControlChannel();
		} else if (NoMedia.equals(deleteWhen)) {
			Msml.Destroyconference destroyConference = new Msml.Destroyconference();
			destroyConference.setId(mediaServerConfId);
			sendInfo(destroyConference);
		} else {
			log.error("Invalid deleteWhen value : " + deleteWhen);
		}
	}
	
	private void byeControlChannel(){
		//TODO: notify ConferenceActor that the conference is terminated.
		SipServletRequest bye = controlSession.createRequest(BYE);
		send(bye);
	}
	
	private void onEAttendeeCallEstablished(ActorMessage.EvtAttendeeCallEstablished msg) throws UnsupportedEncodingException, JAXBException{
		Msml.Join join = new Msml.Join();
		join.setId1(mediaServerConfId);
		join.setId2("conn:" + msg.getConn());
		
		sendInfo(join);
	}
	
	private void onCmdMuteAttendee(ActorMessage.CmdMuteAttendee msg) throws UnsupportedEncodingException, JAXBException{
		Msml.Modifystream modifyStream = new Msml.Modifystream();
		modifyStream.setId1(mediaServerConfId);
		modifyStream.setId2("conn:" + msg.getConn());
		StreamType stream = new StreamType();
		stream.setMedia("audio");
		stream.setDir("to-id1");
		StreamType.Gain gain = new StreamType.Gain();
		gain.setAmt("mute");
		stream.getGainOrClamp().add(gain);
		modifyStream.getStream().add(stream);
		
		sendInfo(modifyStream);
	}
	
	private void onCmdUnmuteAttendee(ActorMessage.CmdUnmuteAttendee msg) throws UnsupportedEncodingException, JAXBException{
		Msml.Modifystream modifyStream = new Msml.Modifystream();
		modifyStream.setId1(mediaServerConfId);
		modifyStream.setId2("conn:" + msg.getConn());
		StreamType stream = new StreamType();
		stream.setMedia("audio");
		stream.setDir("to-id1");
		StreamType.Gain gain = new StreamType.Gain();
		gain.setAgc(GainAgcDatatype.TRUE);
		gain.setTgtlvl(-18);
		stream.getGainOrClamp().add(gain);
		modifyStream.getStream().add(stream);
		
		sendInfo(modifyStream);
	}
	
	private void onSipSessionReadyToInvalidate(ActorMessage.SipSessionReadyToInvalidate msg){
		log.info("\nControlChannelActor <" + mediaServerConfId + "> SIP Session ReadyToInvalidate.");
		getContext().parent().tell(ActorMessage.controlChannelTerminated, getSelf());
		getContext().stop(getSelf());
	}
	
	
	private void onSendSipRequestCompleteMsg(SendSipRequestCompleteMsg msg) {
		//TODO:
	}
	
	private void onSendSipRequestErrorMsg(SendSipRequestErrorMsg msg){
		//TODO: 
	}
}
