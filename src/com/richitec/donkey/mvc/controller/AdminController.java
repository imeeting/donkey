package com.richitec.donkey.mvc.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.richitec.donkey.ContextLoader;
import com.richitec.donkey.mvc.model.Admin;

@Controller
@RequestMapping(value="/admin")
public class AdminController {
	
	private static Log log = LogFactory.getLog(AdminController.class);

	@RequestMapping(value="/login")
	public @ResponseBody String login(
			HttpSession session,
			@RequestParam(value="login_name") String name,
			@RequestParam(value="login_pwd") String password) throws JSONException{
		log.info("Login Name: " + name + " Password: " + password);
		Admin admin = ContextLoader.getAdmin();
		Boolean isValid = admin.isValid(name, password);
		JSONObject json = new JSONObject();
		if (isValid){
			session.setAttribute(Admin.SYSTEM_ADMIN, admin);
			json.put("result", "ok");
		} else {
			log.warn("Login Failed!");
			json.put("result", "login_fail");
		}
		
		return json.toString();
	}
	
	@RequestMapping(value="/logout")
	public String logout(HttpSession session){
		session.removeAttribute(Admin.SYSTEM_ADMIN);
		return "redirect:/";
	}

}
