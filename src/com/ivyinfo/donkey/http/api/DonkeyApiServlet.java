package com.ivyinfo.donkey.http.api;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipSessionsUtil;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.http.request.DonkeyRequestHashmap;
import com.ivyinfo.donkey.http.request.IRequest;


/**
 * Servlet implementation class DonkeyHttpApiServlet
 */
public class DonkeyApiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource
	private SipFactory sipFactory;

	@Resource
	private SipSessionsUtil sipSessionUtil;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);	
		ConferenceManager.setSipFactory(sipFactory);
		ConferenceManager.setSipSessionsUtil(sipSessionUtil);
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DonkeyApiServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		if (Validater.isValidRequest(request)){
			String reqClsName = DonkeyRequestHashmap.getInstance().getRequestClassName(request.getParameter(Constant.Method));
			if(reqClsName != null){
				try {
					Class<IRequest> specificRequestCls = (Class<IRequest>) Class.forName(reqClsName);
					IRequest specificRequest = specificRequestCls.newInstance();
					specificRequest.execute(request, response);
					return;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		DonkeyResponse.BadRequest(response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}
