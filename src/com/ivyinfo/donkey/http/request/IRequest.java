package com.ivyinfo.donkey.http.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IRequest {
	
	public void execute(HttpServletRequest request, HttpServletResponse response);
}
