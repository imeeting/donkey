package com.ivyinfo.donkey.sip;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

@javax.servlet.sip.annotation.SipServlet
public class BaseSipServlet extends SipServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(BaseSipServlet.class);

	// Reference to context - The ctx Map is used as a central storage for this
	// app
	ServletContext ctx = null;

	/*
	 * Demonstrates extension with a new "REPUBLISH" method
	 */
	@Override
	protected void doRequest(SipServletRequest req) throws ServletException,
			IOException {
		if (req.getMethod().equals("REPUBLISH")) {
			doRepublish(req);
		} else {
			super.doRequest(req);
		}
	}

	/*
	 * Implement the REPUBLISH extension here
	 */
	protected void doRepublish(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ctx = config.getServletContext();
	}

	@Override
	protected void doBranchResponse(SipServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doBranchResponse(resp);
		doResponse(resp,
				Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Override
	protected void doErrorResponse(SipServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doErrorResponse(resp);
		doResponse(resp,
				Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Override
	protected void doProvisionalResponse(SipServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doProvisionalResponse(resp);
		doResponse(resp,
				Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Override
	protected void doRedirectResponse(SipServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doRedirectResponse(resp);
		doResponse(resp,
				Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Override
	protected void doSuccessResponse(SipServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doSuccessResponse(resp);
		doResponse(resp,
				Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	private void doResponse(SipServletResponse resp, String method) {
		SipSession session = resp.getSession(false);
		String handlerName = resp.getHeader("CSeq")
				+ ISIPResponseHandler.RESPONSE_HANDLER;
		ISIPResponseHandler handler = (ISIPResponseHandler) session
				.getAttribute(handlerName);

		try {
			if (null != handler) {
				Method m = handler.getClass().getMethod(method,
						SipServletResponse.class);
				m.invoke(handler, resp);
				// session.removeAttribute(handlerName);
			} else {
				logger.error("Not found [" + handlerName + "] for CallId: "
						+ session.getCallId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("BaseSipServlet doResponse error!" + e.getMessage());
		}
	}

}
