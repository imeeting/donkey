package com.richitec.donkey;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ivyinfo.donkey.db.cdr.CdrDAO;
import com.ivyinfo.donkey.db.supplier.SupplierInfoDAO;
import com.richitec.donkey.mvc.model.Admin;

public class ContextLoader extends ContextLoaderListener {
	
	private static WebApplicationContext context = null;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		ServletContext sc = event.getServletContext();
		context = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
	}
	
	public static Admin getAdmin(){
		return (Admin)context.getBean("system_admin");
	}
	
	public static SupplierInfoDAO getSupplierInfoDAO(){
		return (SupplierInfoDAO)context.getBean("supplierInfoDAO");
	}
	
	public static CdrDAO getCdrDAO(){
		return (CdrDAO)context.getBean("cdrDAO");
	}
}
