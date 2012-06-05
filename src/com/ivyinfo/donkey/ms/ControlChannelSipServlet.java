
package com.ivyinfo.donkey.ms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Configuration;
import com.ivyinfo.donkey.http.api.DonkeyGearman;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.ms.msml.DestroyConferenceHandler;
import com.ivyinfo.donkey.ms.msml.MSMLHelper;
import com.ivyinfo.donkey.sip.BaseSipServlet;
import com.ivyinfo.donkey.sip.ISIPResponseHandler;
import com.ivyinfo.donkey.sip.SIPHelper;

/**
 * SipServlet implementation class ControlChannelSipServlet
 */
@javax.servlet.sip.annotation.SipServlet
public class ControlChannelSipServlet extends BaseSipServlet {

    private static final long serialVersionUID = 3978425801979081269L;  
    
    private static final String conf_play_terminated = "play.terminated";
    private static final String conf_play_completed = "play.complete";
    private static final String conf_record_terminated = "record.terminated";
    private static final String conf_record_completed = "record.complete.maxlength";
    
    private static final String featureEventId = "dialog:";
    private static final String featureRecordRelativePath = "/record";
    
    protected static DonkeyGearman gearman = DonkeyGearman.getInstance();
	
    @Override
    protected void doInfo(SipServletRequest req){
    	SipServletResponse response = req.createResponse(SipServletResponse.SC_OK);
    	try {
    		response.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SipApplicationSession sipAppSession = req.getApplicationSession(false);
		
		String contentType = req.getContentType();
		String eventname = null;
		String eventid = null;
		if (MSMLHelper.isMsmlContentType(contentType)) {
			try {
				byte[] content = (byte[]) req.getContent();
				
				eventname = MSMLHelper.getNodeAttributeValue(new ByteArrayInputStream(content), "event", "name");
				eventid = MSMLHelper.getNodeAttributeValue(new ByteArrayInputStream(content), "event", "id");
			} catch (Exception e) {
				gearman.submit(DonkeyGearmanMessage.DonkeyException(DonkeyGearmanMessage.conference_exception, "conf.error", sipAppSession, e.getMessage()));
				return;
			}
			
			if("msml.conf.nomedia".equals(eventname)){
				SipSession controlSession = (SipSession)sipAppSession.getAttribute(ConferenceManager.CONTROL_SESSION);
				
				//control channel
				SipServletRequest bye = controlSession.createRequest(SIPHelper.BYE);
			
				String cseq = bye.getHeader("CSeq");
				controlSession.setAttribute(cseq + ISIPResponseHandler.RESPONSE_HANDLER, 
						new DestroyConferenceHandler());		
			
				try {
					bye.send();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			if("msml.dialog.exit".equals(eventname)){			
				int index = eventid.indexOf(featureEventId);
				if (index >= 0) {
					Vector<String> vecEventName = new Vector<String>();
					Vector<String> vecEventValue = new Vector<String>();
					String type = eventid.substring(index + featureEventId.length());
					StringBuffer typeBuffer = new StringBuffer();
					typeBuffer.append(type.substring(0, 4));
					typeBuffer.append(".");
					typeBuffer.append(type.substring(4, type.length()));
					type = typeBuffer.toString();
					
					if (MSMLHelper.isMsmlContentType(contentType)) {
						try {
							byte[] content = (byte[]) req.getContent();
							
							vecEventName = MSMLHelper.getNodeValues(new ByteArrayInputStream(content), "event", "name");
							vecEventValue = MSMLHelper.getNodeValues(new ByteArrayInputStream(content), "event", "value");
						} catch (Exception e) {
							gearman.submit(DonkeyGearmanMessage.DonkeyException(DonkeyGearmanMessage.conference_info_exception, type, sipAppSession, e.getMessage()));
							return;
						}
					}
					
					if(DonkeyGearmanMessage.conf_announce.equals(type)){
						String playEndType = vecEventValue.elementAt(vecEventName.indexOf(new String("play.end")));
						if(!conf_play_completed.equals(playEndType) && !conf_play_terminated.equals(playEndType)){
							gearman.submit(DonkeyGearmanMessage.DonkeyException(DonkeyGearmanMessage.conference_info_exception, type, sipAppSession, playEndType));
						}
					}
					
					if(DonkeyGearmanMessage.conf_record.equals(type)){
						String recordEndType = vecEventValue.elementAt(vecEventName.indexOf(new String("record.end")));
						String recordLength = vecEventValue.elementAt(vecEventName.indexOf(new String("record.len")));
						String recordId = vecEventValue.elementAt(vecEventName.indexOf(new String("record.recordid")));
						if(conf_record_completed.equals(recordEndType) || conf_record_terminated.equals(recordEndType)){
							StringBuffer recordPath = new StringBuffer();
							recordPath.append(Configuration.getRecordPathURL());
							recordPath.append(recordId.substring(recordId.indexOf(featureRecordRelativePath)));
							gearman.submit(DonkeyGearmanMessage.ConferenceRecorded(DonkeyGearmanMessage.conf_stoprecord, sipAppSession, recordPath.toString(), recordLength));
						}
						else{
							gearman.submit(DonkeyGearmanMessage.DonkeyException(DonkeyGearmanMessage.conference_info_exception, type, sipAppSession, recordEndType));
						}
					}
				}
			}
		}
    }
}
