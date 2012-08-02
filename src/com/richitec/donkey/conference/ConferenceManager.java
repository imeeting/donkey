package com.richitec.donkey.conference;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import akka.actor.ActorRef;

/**
 * 
 * 
 * @author huuguanghui
 *
 */
public class ConferenceManager {
	
	private static Log log = LogFactory.getLog(ConferenceManager.class);
	
	private Map<String, Set<String>> sipUriToConferenceMap;
	private Map<String, ActorRef> confIdToActorMap;
	
	public ConferenceManager(){
		sipUriToConferenceMap = new ConcurrentHashMap<String, Set<String>>();
		confIdToActorMap = new ConcurrentHashMap<String, ActorRef>();
	}
	
	public synchronized boolean addAttendeeToConference(String sipUri, String confId){
		Set<String> confSet = sipUriToConferenceMap.get(sipUri);
		if (null == confSet){
			confSet = new ConcurrentSkipListSet<String>();
			sipUriToConferenceMap.put(sipUri, confSet);
		}
		return confSet.add(confId);
	}
	
	public synchronized boolean removeAttendeeFromConference(String sipUri, String confId){
		Set<String> confSet = sipUriToConferenceMap.get(sipUri);
		if (null == confSet){
			return false;
		}
		
		boolean result = confSet.remove(confId);
		if (confSet.isEmpty()){
			sipUriToConferenceMap.remove(sipUri);
		}
		return result;
	}
	
	public boolean isAttendeeInConference(String sipUri, String confId){
		Set<String> confSet = sipUriToConferenceMap.get(sipUri);
		log.debug(confId + "<null == confSet : " + (null == confSet) + ">");
		return (null != confSet && confSet.contains(confId));
	}
	
	public Set<String> getConferenceByAttendee(String sipUri){
		return sipUriToConferenceMap.get(sipUri);
	}
	
	public Map<String, Set<String>> getSipUriToConferenceMap(){
		return sipUriToConferenceMap;
	}
	
	public void addConferenceActor(String confId, ActorRef actor){
		confIdToActorMap.put(confId, actor);
	}
	
	public ActorRef getConferenceActor(String confId){
		return confIdToActorMap.get(confId);
	}
	
	public boolean hasConference(String confId){
		return confIdToActorMap.containsKey(confId);
	}
	
	public ActorRef removeConferenceActor(String confId){
		return confIdToActorMap.remove(confId);
	}
	
	public Set<String> getAllConferenceID(){
		return confIdToActorMap.keySet();
	}

}
