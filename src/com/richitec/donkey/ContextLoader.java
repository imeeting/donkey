package com.richitec.donkey;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipSessionsUtil;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ivyinfo.donkey.Configuration;
import com.ivyinfo.donkey.db.cdr.CdrDAO;
import com.ivyinfo.donkey.db.supplier.SupplierInfoDAO;
import com.ivyinfo.donkey.ms.IMediaServer;
import com.richitec.donkey.conference.ConferenceManager;
import com.richitec.donkey.conference.GlobalConfig;
import com.richitec.donkey.conference.actor.ActorManager;
import com.richitec.donkey.mvc.model.Admin;

public class ContextLoader extends ContextLoaderListener {
	
	private static WebApplicationContext context = null;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		ServletContext sc = event.getServletContext();
		context = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
		
        InputStream inStream = context.getServletContext().getResourceAsStream("/WEB-INF/config/donkey.properties");
        
        try {
        	Configuration.initilize(inStream);
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
	}
	
	public static GlobalConfig getGlobalConfig(){
		return (GlobalConfig)context.getBean("global_config");
	}
	
	public static ConferenceManager getConfereneManager(){
		return (ConferenceManager)context.getBean("conference_manager");
	}	
	
	public static ActorManager getActorManager(){
		return (ActorManager)context.getBean("actor_manager");
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
	
	public static SipFactory getSipFactory(){
		return (SipFactory)context.getServletContext().getAttribute("javax.servlet.sip.SipFactory");
	}
	
	public static SipSessionsUtil getSipSessionsUtil(){
		return (SipSessionsUtil)context.getServletContext().getAttribute("javax.servlet.sip.SipSessionsUtil");
	}
	
//	public static IMediaServer getMediaServer(){
//		return (IMediaServer)context.getBean("media_server");
//	}
}
