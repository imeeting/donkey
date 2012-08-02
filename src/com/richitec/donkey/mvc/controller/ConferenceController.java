package com.richitec.donkey.mvc.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import akka.actor.ActorRef;

import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.http.api.DonkeyResponseMessage;
import com.ivyinfo.util.RandomString;
import com.richitec.donkey.ContextLoader;
import com.richitec.donkey.conference.ConferenceManager;
import com.richitec.donkey.conference.actor.ActorManager;
import com.richitec.donkey.conference.message.ActorMessage;

@Controller
@RequestMapping(value="/conference")
public class ConferenceController {
	
	private static Log log = LogFactory.getLog(ConferenceController.class);
	
	public static final String Param_Conference = "conference";
	public static final String Param_AppId = "appid";
	public static final String Param_RequestId = "reqid";
	public static final String Param_DeleteWhen = "deleteWhen";
	public static final String Param_SipUri = "sipuri";
	public static final String Param_SipUriList = "sipuriList";
	
	private ActorManager actorManager;
	private ConferenceManager conferenceManager;
	
	@PostConstruct
	public void init(){
		actorManager = ContextLoader.getActorManager();
		conferenceManager = ContextLoader.getConfereneManager();
	}

	@RequestMapping(value="/create")
	public void create(
		HttpServletResponse response,
		@RequestParam(value=Param_Conference, required=false) String confId,
		@RequestParam(value=Param_DeleteWhen, defaultValue="nocontrol") String deleteWhen,
		@RequestParam(value=Param_SipUriList, required=false) String sipUriList,
		@RequestParam(value=Param_AppId) String appId,
		@RequestParam(value=Param_RequestId) String reqId) throws IOException, JSONException{
		log.info("confId : " + confId);
		if (null == confId || confId.length()==0) {
			confId = RandomString.genRandomNum(6);
		}
		
		ActorRef actor = conferenceManager.getConferenceActor(confId);
		if (null == actor){
			actor = actorManager.createConference(confId, appId, reqId);
			conferenceManager.addConferenceActor(confId, actor);
			actor.tell(new ActorMessage.CmdCreateConference(deleteWhen));
			if (null != sipUriList && sipUriList.length()>0){
				log.debug("sipUriList : " + sipUriList);
				addAttenddeeFromJSON(confId, sipUriList);
			}
			DonkeyResponse.Accepted(response, DonkeyResponseMessage.ConferenceSession(confId));
		} else {
			response.sendError(HttpServletResponse.SC_CONFLICT);
		}
	}
	
	@RequestMapping(value="/destroy")
	public void destroy(
			HttpServletResponse response,
			@RequestParam(value=Param_Conference) String confId,
			@RequestParam(value=Param_AppId) String appId) throws IOException{
		ActorRef actor = conferenceManager.getConferenceActor(confId);
		actor.tell(ActorMessage.destroyConference);
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
	}
	
	/**
	 * add one or more phone to the conference. 
	 * @throws IOException 
	 */
	@RequestMapping(value="/add")
	public void add(
			HttpServletResponse response,
			@RequestParam(value=Param_Conference) String confId,
			@RequestParam(value=Param_SipUri) String sipUri,
			@RequestParam(value=Param_AppId) String appId) throws IOException{
		boolean result = conferenceManager.addAttendeeToConference(sipUri, confId);
		if (result){
			DonkeyResponse.Accepted(response, DonkeyResponseMessage.AttendeeSession(confId, sipUri));
		} else {
			response.sendError(HttpServletResponse.SC_CONFLICT);
		}
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
	}
	
	@RequestMapping(value="/addmore")
	public void addMore(
			HttpServletResponse response,
			@RequestParam(value=Param_Conference) String confId,
			@RequestParam(value=Param_SipUriList) String sipUriList,
			@RequestParam(value=Param_AppId) String appId) throws JSONException{
		addAttenddeeFromJSON(confId, sipUriList);
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
	}
	
	private void addAttenddeeFromJSON(String confId, String sipUriList) {
		try {
			JSONArray sipUriJSONArray = new JSONArray(sipUriList);
			for (int i=0; i< sipUriJSONArray.length(); i++){
				String sipUri = sipUriJSONArray.getString(i);
				boolean result = conferenceManager.addAttendeeToConference(sipUri, confId);
				if (!result){
					log.warn("Cannot add SIP URI <" + sipUri + "> to conference <" + confId+ ">");
				} else {
					log.debug("Add <" + sipUri + "> to conference <" + confId + ">");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * call phone in the conference
	 * @throws IOException 
	 */
	@RequestMapping(value="/call")
	public void call(
			HttpServletResponse response,
			@RequestParam(value=Param_Conference) String confId,
			@RequestParam(value=Param_SipUri) String sipUri,
			@RequestParam(value=Param_AppId) String appId ) throws IOException{
		boolean result = conferenceManager.isAttendeeInConference(sipUri, confId);
		if (result){
			ActorRef actor = conferenceManager.getConferenceActor(confId);
			actor.tell(new ActorMessage.CmdJoinConference("call", sipUri));
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
		} else {
			log.debug("call sipuri: " + sipUri);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	/**
	 * add one phone to the conference and call it immediately.
	 * @throws IOException 
	 */
	@RequestMapping(value="/join")
	public void join(
			HttpServletResponse response,
			@RequestParam(value=Param_Conference) String confId,
			@RequestParam(value=Param_SipUri) String sipUri,
			@RequestParam(value=Param_AppId) String appId ) throws IOException{
		boolean result = conferenceManager.addAttendeeToConference(sipUri, confId);
		if (result){
			ActorRef actor = conferenceManager.getConferenceActor(confId);
			actor.tell(new ActorMessage.CmdJoinConference("join", sipUri));
			DonkeyResponse.Accepted(response, DonkeyResponseMessage.AttendeeSession(confId, sipUri));
		} else {
			response.sendError(HttpServletResponse.SC_CONFLICT);
		}
	}
	
	/**
	 * hang up one phone call.
	 */
	@RequestMapping(value="/hangup")
	public void hangup(
			HttpServletResponse response,
			@RequestParam(value=Param_Conference) String confId,
			@RequestParam(value=Param_SipUri) String sipUri,
			@RequestParam(value=Param_AppId) String appId ) throws IOException{
		boolean result = conferenceManager.isAttendeeInConference(sipUri, confId);
		if (result){
			ActorRef actor = conferenceManager.getConferenceActor(confId);
			actor.tell(new ActorMessage.CmdUnjoinConference("hangup", sipUri));
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	/**
	 * hang up and remove one phone from the conference
	 * @throws IOException 
	 */
	@RequestMapping(value="/unjoin")
	public void unjoin(
			HttpServletResponse response,
			@RequestParam(value=Param_Conference) String confId,
			@RequestParam(value=Param_SipUri) String sipUri,
			@RequestParam(value=Param_AppId) String appId ) throws IOException{
		boolean result = conferenceManager.removeAttendeeFromConference(sipUri, confId);
		if (result){
			ActorRef actor = conferenceManager.getConferenceActor(confId);
			actor.tell(new ActorMessage.CmdUnjoinConference("unjoin", sipUri));
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, sipUri + " in conf:" + confId);
		}
	}
	
	@RequestMapping(value="/mute")
	public void mute(			
			HttpServletResponse response,
			@RequestParam(value=Param_Conference) String confId,
			@RequestParam(value=Param_SipUri) String sipUri,
			@RequestParam(value=Param_AppId) String appId ) throws IOException{
		boolean result = conferenceManager.isAttendeeInConference(sipUri, confId);
		if (result){
			ActorRef actor = conferenceManager.getConferenceActor(confId);
			actor.tell(new ActorMessage.CmdMuteAttendee(sipUri));
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	@RequestMapping(value="/unmute")
	public void unmute(			
			HttpServletResponse response,
			@RequestParam(value=Param_Conference) String confId,
			@RequestParam(value=Param_SipUri) String sipUri,
			@RequestParam(value=Param_AppId) String appId ) throws IOException{
		boolean result = conferenceManager.isAttendeeInConference(sipUri, confId);
		if (result){
			ActorRef actor = conferenceManager.getConferenceActor(confId);
			actor.tell(new ActorMessage.CmdUnmuteAttendee(sipUri));
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	@RequestMapping(value="/list")
	public void list(){
		
	}
	
}
