package com.ivyinfo.donkey.sample;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.http.api.Validater;
import com.sun.enterprise.web.connector.grizzly.comet.CometContext;
import com.sun.enterprise.web.connector.grizzly.comet.CometEngine;

public class GearmanEventController {

	private static GearmanEventController instance;

	private static final Logger logger = Logger
			.getLogger(GearmanEventController.class);

	private GearmanEventController() {
		//
	}

	public static GearmanEventController getInstance() {
		if (instance == null) {
			instance = new GearmanEventController();
		}
		return instance;
	}

	protected void dispatch(HttpServletRequest request,
			HttpServletResponse response) {
		int length = request.getContentLength();
		InputStream iStream;
		try {
			iStream = request.getInputStream();
			byte[] data = new byte[length];
			iStream.read(data, 0, length);
			String str = new String(data);

			JSONObject jsonObject = new JSONObject(str);

			logger.info("dispatch : " + jsonObject.toString());

			String confSessionID = jsonObject.getString(Constant.Conference);
			String methodType = jsonObject.getString(Constant.Type);

			if (null == confSessionID || confSessionID.length() <= 0
					|| null == methodType
					|| !Validater.isSupportedMessage(methodType)) {
				DonkeyResponse.BadRequest(response);
				return;
			}

			// just process "conf.stoprecord" message
			if (DonkeyGearmanMessage.conf_stoprecord.equals(methodType)) {
				JSONObject msg = new JSONObject();
				msg.put(Constant.RecordURL,
						jsonObject.getString(Constant.RecordURL));

				logger.info("dispatch - recordUrl: "
						+ jsonObject.getString(Constant.RecordURL));

				notify(msg.toString(), confSessionID);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * notify the message
	 * 
	 * @throws IOException
	 */
	private void notify(String notice, String confSessionID) throws IOException {
		if (notice != null) {
			// notify to page
			CometEngine engine = CometEngine.getEngine();
			CometContext context = engine.getCometContext(confSessionID);
			if (context != null) {
				Set<RecordConfCometHandler> handlers = context
						.getCometHandlers();

				if (handlers != null && handlers.size() > 0) {
					context.notify(notice);
				}
			}
		}
	}

}
