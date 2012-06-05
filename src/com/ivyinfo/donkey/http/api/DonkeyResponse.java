package com.ivyinfo.donkey.http.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class DonkeyResponse {

	private static final Logger logger = Logger.getLogger(DonkeyResponse.class);

	public static void Accepted(HttpServletResponse response) {
		sendHttpResponse(response, HttpServletResponse.SC_ACCEPTED, null);
	}

	public static void Accepted(HttpServletResponse response,
			DonkeyResponseMessage msg) {
		sendHttpResponse(response, HttpServletResponse.SC_ACCEPTED, msg);
	}

	public static void InternalException(HttpServletResponse response,
			Exception e) {
		sendHttpResponse(response,
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				DonkeyResponseMessage.InternalException(e));
	}

	public static void BadRequest(HttpServletResponse response) {
		sendHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, null);
	}

	public static void Forbidden(HttpServletResponse response) {
		sendHttpResponse(response, HttpServletResponse.SC_FORBIDDEN, null);
	}

	public static void NotFound(HttpServletResponse response,
			DonkeyResponseMessage msg) {
		sendHttpResponse(response, HttpServletResponse.SC_NOT_FOUND, msg);
	}

	public static void Gone(HttpServletResponse response,
			DonkeyResponseMessage msg) {
		sendHttpResponse(response, HttpServletResponse.SC_GONE, msg);
	}

	public static void Conflict(HttpServletResponse response,
			DonkeyResponseMessage msg) {
		sendHttpResponse(response, HttpServletResponse.SC_CONFLICT, msg);
	}

	private static void sendHttpResponse(HttpServletResponse response,
			int errCode, DonkeyResponseMessage msg) {
		try {
			response.setStatus(errCode);
			response.setContentType("application/json");
			if (null != msg) {
				PrintWriter writer = response.getWriter();
				String str = msg.toJSON();
				if (null != str && str.length() >= 0) {
					writer.print(str);
				}
				writer.close();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
