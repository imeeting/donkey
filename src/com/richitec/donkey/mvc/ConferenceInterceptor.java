package com.richitec.donkey.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.richitec.donkey.ContextLoader;
import com.richitec.donkey.conference.ConferenceManager;
import com.richitec.donkey.mvc.controller.ConferenceController;

public class ConferenceInterceptor implements HandlerInterceptor {
	
	private static Log log = LogFactory.getLog(ConferenceInterceptor.class);

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {
		String confId = request.getParameter(ConferenceController.Param_Conference);
		if (null == confId){
			log.debug("Parameter <" + ConferenceController.Param_Conference + "> is necessary!");
			return false;
		}
		ConferenceManager conferenceManager = ContextLoader.getConfereneManager();
		if (conferenceManager.hasConference(confId)){
			log.debug("Find Actor for conference: " + confId);
			return true;
		} else {
			log.debug("Cannot find Actor for conference: " + confId);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, 
					ConferenceController.Param_Conference + ":" + confId);
			return false;
		}
	}

}
