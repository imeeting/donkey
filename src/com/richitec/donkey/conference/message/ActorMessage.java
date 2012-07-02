package com.richitec.donkey.conference.message;

import java.io.IOException;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.richitec.donkey.conference.actor.AttendeeActor;
import com.richitec.donkey.conference.actor.ConferenceActor;


public class ActorMessage {
	
	public static CmdDestroyConference destroyConference = new CmdDestroyConference();
	public static EvtConferenceCreateSuccess createConferenceSuccess = new EvtConferenceCreateSuccess();
	public static EvtConferenceCreateError createConferenceError = new EvtConferenceCreateError();
	public static EvtControlChannelTerminated controlChannelTerminated = new EvtControlChannelTerminated();
	
	public static class CmdCreateConference {
		private String deleteWhen;
		public CmdCreateConference(String deleteWhen) {
			this.deleteWhen = deleteWhen;
		}
		public String getDeleteWhen(){
			return this.deleteWhen;
		}
	}
	
	public static class CmdDestroyConference {
		public String getMethod(){
			return "destroy";
		}
	}
	
	private static class CmdMessage {
		private String sipUri;
		private String method;
		public CmdMessage(String method, String sipUri) {
			this.method = method;
			this.sipUri = sipUri;
		}
		public String getSipUri(){
			return this.sipUri;
		}
		public String getMethod(){
			return this.method;
		}
	}
	
	public static class CmdJoinConference extends CmdMessage {
		public CmdJoinConference(String method, String sipUri) {
			super(method, sipUri);
		}
	}
	
	public static class CmdUnjoinConference extends CmdMessage {
		public CmdUnjoinConference(String method, String sipUri) {
			super(method, sipUri);
		}
	}
	
	public static class CmdMuteAttendee extends CmdMessage{
		private String conn;
		public CmdMuteAttendee(String sipUri) {
			super("mute", sipUri);
		}
		public void setConn(String conn){
			this.conn = conn;
		}
		public String getConn(){
			return this.conn;
		}
	}
	
	public static class CmdUnmuteAttendee extends CmdMessage {
		private String conn;
		public CmdUnmuteAttendee(String sipUri) {
			super("unmute", sipUri);
		}
		public void setConn(String conn){
			this.conn = conn;
		}
		public String getConn(){
			return this.conn;
		}
	}
	
	public static class EvtAttendeeCallInConference {
		private SipApplicationSession sipAppSession;
		private SipSession userSession;
		private SipSession mediaServerSession;
		private String sipUri;
		private String conn;
		
		public EvtAttendeeCallInConference(SipApplicationSession sipAppSession,
				SipSession userSession, SipSession mediaServerSession, 
				String sipUri, String conn){
			this.sipAppSession = sipAppSession;
			this.userSession = userSession;
			this.mediaServerSession = mediaServerSession;
			this.sipUri = sipUri;
			this.conn = conn;
		}
		
		public SipApplicationSession getSipAppSession(){
			return this.sipAppSession;
		}
		
		public SipSession getUserSession() {
			return this.userSession;
		}
		
		public SipSession getMediaServerSession() {
			return this.mediaServerSession;
		}
		
		public String getSipUri(){
			return this.sipUri;
		}
		
		public String getConn(){
			return this.conn;
		}
		
		public void bye(){
			SipServletRequest byeMediaServer = mediaServerSession.createRequest("BYE");
			try {
				byeMediaServer.send();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			SipServletRequest byeAttendee = userSession.createRequest("BYE");
			try {
				byeAttendee.send();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	
	public static class ErrConferenceStatusConflict {
		private ConferenceActor.State state;
		public ErrConferenceStatusConflict(ConferenceActor.State state){
			this.state = state;
		}
		public String getState(){
			return this.state.name();
		}
	}
	
	public static class ErrAttendeeStatusConflict {
		private AttendeeActor.AttendeeState state;
		private String sipUri;
		private String method;
		public ErrAttendeeStatusConflict(String method, String sipUri, AttendeeActor.AttendeeState state){
			this.method = method;
			this.sipUri = sipUri;
			this.state = state;
		}
		public String getMethod() {
			return this.method;
		}
		public String getSipUri() {
			return this.sipUri;
		}
		public String getState(){
			return this.state.name();
		}
	}
	
	/**
	 * 
	 * Control Channel Messages
	 */
	
	public static class CreateControlChannelComplete {
		private int status;
		public CreateControlChannelComplete(int status) {
			this.status = status;
		}
		public int getStatus(){
			return this.status;
		}
	}
	
	public static class EvtConferenceCreateSuccess {
		
	}
	
	public static class EvtConferenceCreateError {

	}
	
	public static class ControlChannelInfoRequest {
		private SipServletRequest request;
		public ControlChannelInfoRequest(SipServletRequest request) {
			this.request = request;
		}
		public SipServletRequest getRequest(){
			return this.request;
		}
	}
	
	public static class ControlChannelInfoResponse {
		private SipServletResponse response;
		public ControlChannelInfoResponse(SipServletResponse response) {
			this.response = response;
		}
		public SipServletResponse getResponse(){
			return this.response;
		}
	}
	
	public static class EvtControlChannelTerminated {
		
	}
	
	/**
	 * B2BUASipServlet related 
	 * 
	 * @author huuguanghui
	 *
	 */
	private static class SipResponse {
		private SipServletResponse response;
		public SipResponse(SipServletResponse response){
			this.response = response;
		}
		public SipServletResponse getResponse(){
			return this.response;
		}
	}
	
	public static class SipProvisionalResponse extends SipResponse {
		public SipProvisionalResponse(SipServletResponse response) {
			super(response);
		}
	}
	
	public static class SipSuccessResponse extends SipResponse {
		public SipSuccessResponse(SipServletResponse response) {
			super(response);
		}
	}	
	
	public static class SipErrorResponse extends SipResponse {
		public SipErrorResponse(SipServletResponse response) {
			super(response);
		}
	}
	
	public static class SipByeRequest {
		private SipSession session;
		public SipByeRequest(SipSession session){
			this.session = session;
		}
		public SipSession getSipSession(){
			return this.session;
		}
	}
	
	public static class EvtAttendeeCallEstablished {
		private String conn;
		private String sipUri;
		public EvtAttendeeCallEstablished(String sipUri, String conn) {
			this.sipUri = sipUri;
			this.conn = conn;
		}
		public String getConn(){
			return this.conn;
		}
		public String getSipUri() {
			return this.sipUri;
		}
	}
	
	public static class EvtAttendeeCallFailed {
		private int status;
		private String sipUri;
		public EvtAttendeeCallFailed(String sipUri, int status) {
			this.sipUri = sipUri;
			this.status = status;
		}
		public int getStatus(){
			return this.status;
		}
		public String getSipUri(){
			return this.sipUri;
		}
	}
	
	public static class EvtAttendeeCallTerminated {
		private String sipUri;
		public EvtAttendeeCallTerminated(String sipUri) {
			this.sipUri = sipUri;
		}
		public String getSipUri(){
			return this.sipUri;
		}
	}
	
	public static class EvtMediaServerCallFailed extends EvtAttendeeCallFailed {
		public EvtMediaServerCallFailed(String sipUri, int status) {
			super(sipUri, status);
		}
	}
	
	public static class SipSessionReadyToInvalidate {
		private SipSession session;
		public SipSessionReadyToInvalidate(SipSession session){
			this.session = session;
		}
		public SipSession getSipSession(){
			return this.session;
		}
	}

}
