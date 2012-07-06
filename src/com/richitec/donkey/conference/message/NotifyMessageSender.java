package com.richitec.donkey.conference.message;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

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
		HttpEntity entity = new StringEntity(message.toString(), Consts.UTF_8);
		HttpPost post = new HttpPost(notifyURL);
		post.setEntity(entity);
		HttpClient client = new DefaultHttpClient();
		try {
			client.execute(post);
		} catch (ClientProtocolException e) {
			log.error("\nPOST Notification error : " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("\nPOST Notification error : " + e.getMessage());
			e.printStackTrace();
		}
	}

}
