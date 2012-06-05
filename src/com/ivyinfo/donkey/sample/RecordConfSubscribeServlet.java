package com.ivyinfo.donkey.sample;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.sun.enterprise.web.connector.grizzly.comet.CometContext;
import com.sun.enterprise.web.connector.grizzly.comet.CometEngine;
import com.sun.enterprise.web.connector.grizzly.comet.CometHandler;

/**
 * Servlet implementation class RecordConferenceSubscribeServlet
 */
public class RecordConfSubscribeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final Logger logger = Logger.getLogger(RecordConfSubscribeServlet.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecordConfSubscribeServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		subscribe(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		subscribe(request, response);
	}
	
	/**
	 * subscribe to the document presentation
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void subscribe(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String confSessionID = request.getParameter(Constant.Conference);
		
		logger.info("confSession id: " + confSessionID);
		
		if (null == confSessionID || confSessionID.length() <= 0) {
			DonkeyResponse.BadRequest(response);
			return;
		}	
		
		// register comet
		CometEngine engine = CometEngine.getEngine();
		CometContext context = engine.getCometContext(confSessionID);
		if (context == null){
			context = engine.register(confSessionID);
			context.setExpirationDelay(3600 * 1000);//6 hours is enough for any presentation conference.
		} 
		
		CometHandler handler = new RecordConfCometHandler();
		context.addCometHandler(handler);
		handler.attach(response);
	}

}
