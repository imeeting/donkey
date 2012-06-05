package com.ivyinfo.donkey.admin.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.ivyinfo.donkey.Configuration;
import com.ivyinfo.donkey.Constant;

/**
 * Servlet implementation class AdminLoginServlet
 */
public class AdminLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(AdminLoginServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminLoginServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		validateAdmin(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		validateAdmin(request, response);
	}

	private void validateAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String name = request.getParameter(Constant.LoginName);
		String pwd = request.getParameter(Constant.LoginPWD);
		
		logger.info("login name: " + name + " login pwd: " + pwd);
		
		if (Configuration.getProperty("admin_name").equals(name) && Configuration.getProperty("password").equals(pwd)) {
			logger.info("correct account!");
			HttpSession sess = request.getSession();
			sess.setAttribute(Constant.LoginName, name);
		
			JSONObject obj = new JSONObject();
			try {
				obj.put(Constant.Result, "ok");
				PrintWriter writer = response.getWriter();
				writer.print(obj.toString());
				writer.close();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		} else {
			logger.info("wrong account!");
			JSONObject obj = new JSONObject();
			try {
				obj.put(Constant.Result, "login_fail");
				PrintWriter writer = response.getWriter();
				writer.print(obj.toString());
				writer.close();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
