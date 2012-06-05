package com.ivyinfo.donkey.admin.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.admin.DevInfoOperationManager;
import com.ivyinfo.donkey.http.api.DonkeyResponse;

/**
 * Servlet implementation class DevInfoManageServlet
 */
public class DevInfoManageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DevInfoManageServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}

	private void doAction(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession sess = request.getSession(false);
		if (sess == null) {
			DonkeyResponse.Forbidden(response);
			return;
		} else {
			String loginName = (String) sess.getAttribute(Constant.LoginName);
			if (loginName == null) {
				DonkeyResponse.Forbidden(response);
				return;
			}
		}

		DevInfoOperationManager.dispatch(request, response);
	}

}
