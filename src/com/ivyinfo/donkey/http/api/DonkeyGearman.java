package com.ivyinfo.donkey.http.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONException;

import com.ivyinfo.donkey.Constant;

public class DonkeyGearman {
	
	private static Logger logger = Logger.getLogger(DonkeyGearman.class);

	private static DonkeyGearman _instance;

	private ExecutorService executor;

	private DonkeyGearman() {
		executor = Executors.newCachedThreadPool();
	}

	public static DonkeyGearman getInstance() {
		if (null == _instance) {
			_instance = new DonkeyGearman();
		}
		return _instance;
	}

	public void submit(DonkeyGearmanMessage msg) {
//		executor.execute(new Worker(msg));
	}

	/**
	 * Worker thread that will send out notifications.
	 * 
	 * @author huuguanghui
	 * 
	 */
	private static class Worker implements Runnable {

		private DonkeyGearmanMessage message;

		public Worker(DonkeyGearmanMessage message) {
			this.message = message;
		}

		@Override
		public void run() {
			try {
				String url = (String) message.get(Constant.CallBackURL);
				
				// remove notify url from message
				message.remove(Constant.CallBackURL);
				logger.debug(message.toJSON());

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				HttpEntity entity = new StringEntity(message.toJSON(), "UTF-8");
				httpPost.setEntity(entity);
				httpClient.execute(httpPost);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
