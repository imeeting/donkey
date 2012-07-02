package com.richitec.donkey.conference.actor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;

public class ActorManager {
	
	private static Log log = LogFactory.getLog(ActorManager.class);
	
	private ActorSystem actorSystem = null;
	
	public ActorManager(){
		actorSystem = ActorSystem.create("donkey");
	}
	
	public ActorRef getConference(final String confId){
		return actorSystem.actorFor("/user/" + confId);
	}
	
	public ActorRef createConference(final String confId, final String appId, final String reqId) {
		ActorRef actor = actorSystem.actorOf(new Props(new UntypedActorFactory(){
			@Override
			public Actor create() {
				return new ConferenceActor(confId, appId, reqId);
			}
		}), confId);
		
		return actor;
	}
}
