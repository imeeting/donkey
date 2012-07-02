package com.richitec.donkey.conference.actor;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ivyinfo.donkey.db.supplier.DevAppIDInfoManager;
import com.richitec.donkey.ContextLoader;
import com.richitec.donkey.conference.ConferenceManager;
import com.richitec.donkey.conference.message.ActorMessage;
import com.richitec.donkey.conference.message.ActorMessage.EvtConferenceCreateError;
import com.richitec.donkey.conference.message.ActorMessage.EvtConferenceCreateSuccess;
import com.richitec.donkey.conference.message.NotifyMessage;
import com.richitec.donkey.conference.message.NotifyMessageSender;
import com.richitec.donkey.conference.message.sip.SendSipRequestMsg;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

public class ConferenceActor extends UntypedActor {
	
	private static Log log = LogFactory.getLog(ConferenceActor.class);
	
	public enum State {Idle, Creating, Creating_Error, Created, Destroy_Wait};
	private State state = State.Idle;

	private String appId;
	private String confId;
	private String reqId;
	
	private String notifyUrl;
	
	private ActorRef controlChannelActor;
	private Map<String, ActorRef> attendeeActorMap;
	
	public ConferenceActor(String confId, String appId, String reqId){
		this.confId = confId;
		this.appId = appId;
		this.reqId = reqId;
		this.attendeeActorMap = new HashMap<String, ActorRef>();
		this.notifyUrl = DevAppIDInfoManager.getCallBackURL(appId);
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof ActorMessage.CmdCreateConference){
			onCmdCreateConference((ActorMessage.CmdCreateConference) msg);
		} else
		if (msg instanceof ActorMessage.EvtConferenceCreateSuccess){
			onEvtConferenceCreateSuccess((EvtConferenceCreateSuccess) msg);
		} else
		if (msg instanceof ActorMessage.EvtConferenceCreateError){
			onEvtConferenceCreateError((EvtConferenceCreateError) msg);
		} else 
		if(msg instanceof ActorMessage.CmdDestroyConference) {
			onCmdDestroyConference((ActorMessage.CmdDestroyConference) msg);
		} else 
		if (msg instanceof ActorMessage.CmdJoinConference) {
			onCmdJoinConference((ActorMessage.CmdJoinConference) msg);
		} else 
		if (msg instanceof ActorMessage.EvtAttendeeCallInConference) {
			onEvtAttendeeCallInConference((ActorMessage.EvtAttendeeCallInConference) msg);
		} else
		if (msg instanceof ActorMessage.EvtAttendeeCallEstablished ){
			onEvtAttendeeCallEstablished((ActorMessage.EvtAttendeeCallEstablished) msg);	
		} else 
		if (msg instanceof ActorMessage.EvtAttendeeCallFailed){
			onEvtAttendeeCallFailed((ActorMessage.EvtAttendeeCallFailed) msg);
		} else 
		if (msg instanceof ActorMessage.EvtMediaServerCallFailed) {
			onEvtMediaServerCallFailed((ActorMessage.EvtMediaServerCallFailed) msg);
		} else
		if (msg instanceof ActorMessage.EvtAttendeeCallTerminated) {
			onEvtAttendeeCallTerminated((ActorMessage.EvtAttendeeCallTerminated) msg);
		} else
		if (msg instanceof ActorMessage.CmdUnjoinConference){
			onCmdUnjoinConference((ActorMessage.CmdUnjoinConference) msg);
		} else 
		if (msg instanceof ActorMessage.EvtControlChannelTerminated) {
			onEvtControlChannelTerminated((ActorMessage.EvtControlChannelTerminated) msg);
		} else 
		if (msg instanceof ActorMessage.CmdMuteAttendee) {
			onCmdMuteAttendee((ActorMessage.CmdMuteAttendee) msg);
		} else 
		if (msg instanceof ActorMessage.CmdUnmuteAttendee) {
			onCmdUnmuteAttendee((ActorMessage.CmdUnmuteAttendee) msg);
		} else
		if (msg instanceof ActorMessage.ErrAttendeeStatusConflict) {
			onErrAttendeeStatusConflict((ActorMessage.ErrAttendeeStatusConflict) msg);
		} else
		if(msg instanceof SendSipRequestMsg) {
			onSendSipRequestMsg((SendSipRequestMsg) msg);
		} else {
			unhandled(msg);
		}		
	}
	
	private void onCmdCreateConference(ActorMessage.CmdCreateConference msg){
		controlChannelActor = getContext().actorOf(
				new Props(new UntypedActorFactory() {
					@Override
					public Actor create() {
						try {
							return new ControlChannelActor();
						} catch (JAXBException e) {
							e.printStackTrace();
						}
						return null;
					}
				}), ControlChannelActor.Name);
		controlChannelActor.tell(msg, getSelf());
		this.state = State.Creating;
	}
	
	private void onCmdDestroyConference(ActorMessage.CmdDestroyConference msg){
		if (this.state == State.Created){
			this.state = State.Destroy_Wait;
			controlChannelActor.tell(msg, getSelf());
			
			ConferenceManager confManager = ContextLoader.getConfereneManager();
			for (Entry<String, ActorRef> e : attendeeActorMap.entrySet()){
				String sipUri = e.getKey();
				confManager.removeAttendeeFromConference(sipUri, confId);
				ActorRef actor = e.getValue();
				actor.tell(msg, getSelf());
			}
		} else {
			log.error("Cannot <" + msg.getMethod()+"> conference <" + 
					confId + "> when ConferenceActor state is " + state.name());
			notify(new NotifyMessage.ConferenceStatusConflict(msg.getMethod(), null, state.name()));
		}
	}
	
	private void onCmdJoinConference(ActorMessage.CmdJoinConference msg){
		if (this.state != State.Created){
			log.error("Cannot <" + msg.getMethod()+"> <"+msg.getSipUri()+"> in conference <" + 
					confId + "> when ConferenceActor state is " + state.name());
			notify(new NotifyMessage.ConferenceStatusConflict(msg.getMethod(), msg.getSipUri(), state.name()));
			return;
		}
		
		final String sipUri = msg.getSipUri();
		ActorRef actor = attendeeActorMap.get(sipUri);
		if (null == actor){
			actor = getContext().actorOf(new Props(new UntypedActorFactory() {
				@Override
				public Actor create() {
					return new AttendeeActor(sipUri, controlChannelActor);
				}
			}), sipUri);
			
			attendeeActorMap.put(sipUri, actor);
		}
		
		actor.tell(msg, getSelf());
	}
	
	private ActorRef getAttendeeActor(String method, String sipUri){
		if (this.state != State.Created){
			log.error("Cannot <" + method +"> <"+ sipUri +"> in conference <" + 
					confId + "> when ConferenceActor state is " + state.name());
			notify(new NotifyMessage.ConferenceStatusConflict(method, sipUri, state.name()));
			return null;
		}
		
		ActorRef attendeeActor = attendeeActorMap.get(sipUri);
		if (null == attendeeActor){
			log.error("Cannot find AttendeeActor for <" + sipUri + 
					"> in conference <"  + confId + ">");
			return null;
		}
		
		return attendeeActor;
	}
	
	private void onCmdUnjoinConference(ActorMessage.CmdUnjoinConference msg){
		ActorRef attendeeActor = getAttendeeActor(msg.getMethod(), msg.getSipUri());
		if (null != attendeeActor){
			attendeeActor.tell(msg, getSelf());
		}
	}	
	
	private void onCmdMuteAttendee(ActorMessage.CmdMuteAttendee msg){
		ActorRef attendeeActor = getAttendeeActor(msg.getMethod(), msg.getSipUri());
		if (null != attendeeActor){
			attendeeActor.tell(msg, getSelf());
		}
	}
	
	private void onCmdUnmuteAttendee(ActorMessage.CmdUnmuteAttendee msg){
		ActorRef attendeeActor = getAttendeeActor(msg.getMethod(), msg.getSipUri());
		if (null != attendeeActor){
			attendeeActor.tell(msg, getSelf());
		}
	}
	
	private void onEvtConferenceCreateSuccess(ActorMessage.EvtConferenceCreateSuccess msg){
		this.state = State.Created;
		notify(new NotifyMessage.ConferenceCreateSuccess());
	}
	
	private void onEvtConferenceCreateError(ActorMessage.EvtConferenceCreateError msg){
		this.state = State.Creating_Error;
		notify(new NotifyMessage.ConferenceCreateError());
	}	
	
	private void onEvtAttendeeCallInConference(ActorMessage.EvtAttendeeCallInConference msg){
		if (this.state != State.Created){
			msg.bye();
			return;
		}
		
		final SipApplicationSession sipAppSession = msg.getSipAppSession();
		final SipSession userSession = msg.getUserSession();
		final SipSession mediaServerSession = msg.getMediaServerSession();
		final String sipUri = msg.getSipUri();
		final String conn = msg.getConn();
		
		ActorRef actor = attendeeActorMap.get(sipUri);
		if (null == actor){
			actor = getContext().actorOf(new Props(new UntypedActorFactory() {
				@Override
				public Actor create() {
					return new AttendeeActor(sipAppSession, 
							userSession, mediaServerSession, sipUri, conn);
				}
			}), sipUri);
			
			attendeeActorMap.put(sipUri, actor);
		}
		
		mediaServerSession.setAttribute(AttendeeActor.Actor, actor);
		userSession.setAttribute(AttendeeActor.Actor, actor);
		
		controlChannelActor.tell(new ActorMessage.EvtAttendeeCallEstablished(sipUri, conn), getSelf());
		notify(new NotifyMessage.AttendeeCallEstablished(sipUri));
	}	
	
	private void onEvtAttendeeCallEstablished(ActorMessage.EvtAttendeeCallEstablished msg){
		if (this.state != State.Created){
			log.error("Cannot join conn of <" + msg.getSipUri() + "> to conference <" + 
					confId + "> when ConferenceActor state is " + this.state.name());
			getSender().tell(new ActorMessage.ErrConferenceStatusConflict(this.state), getSelf());
			return;
		}
		controlChannelActor.tell(msg, getSelf());
		notify(new NotifyMessage.AttendeeCallEstablished(msg.getSipUri()));
	}
	
	private void onEvtAttendeeCallFailed(ActorMessage.EvtAttendeeCallFailed msg){
		notify(new NotifyMessage.AttendeeCallFailed(msg.getSipUri(), msg.getStatus()));
	}
	
	private void onEvtMediaServerCallFailed(ActorMessage.EvtMediaServerCallFailed msg){
		notify(new NotifyMessage.AttendeeCallFailed(msg.getSipUri(), msg.getStatus()));
	}
	
	private void onEvtAttendeeCallTerminated(ActorMessage.EvtAttendeeCallTerminated msg){
		notify(new NotifyMessage.AttendeeCallTerminated(msg.getSipUri()));
	}
	
	private void onEvtControlChannelTerminated(ActorMessage.EvtControlChannelTerminated msg){
		notify(new NotifyMessage.ConferenceDestroySuccess());
	}
	
	private void onErrAttendeeStatusConflict(ActorMessage.ErrAttendeeStatusConflict msg){
		log.error("Cannot <" + msg.getMethod()+"> <"+msg.getSipUri()+"> in conference <" + 
				confId + "> when AttendeeActor state is " + msg.getState());
		notify(new NotifyMessage.AttendeeStatusConflict(msg.getMethod(), msg.getSipUri(), msg.getState()));
	}
	
	private void onSendSipRequestMsg(SendSipRequestMsg msg) throws IOException{
		//TODO: execute network operation in tread pool.
		msg.send(getSender(), getSelf());
	}
	
	private void notify(NotifyMessage.Message msg){
		msg.put(NotifyMessage.appId, this.appId);
		msg.put(NotifyMessage.reqId, this.reqId);
		msg.put(NotifyMessage.conference, this.confId);
//		NotifyMessageSender sender = new NotifyMessageSender(msg, notifyUrl);
		log.info("\nNotify : " + msg);
	}
	
}