package com.richitec.donkey.conference.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NotifyMessageSender implements Runnable {
	
	private static Log log = LogFactory.getLog(NotifyMessageSender.class);
	
	private String notifyURL;
	private NotifyMessage.Message message;
	
	public NotifyMessageSender(NotifyMessage.Message message, String notifyURL){
		this.message = message;
		this.notifyURL = notifyURL;
	}

	@Override
	public void run() {
		log.info("\nSend Message to URL : " + this.notifyURL + 
				"\nMessage Body : " + message);
		//TODO: HTTP POST message to notifyURL.
	}

}
