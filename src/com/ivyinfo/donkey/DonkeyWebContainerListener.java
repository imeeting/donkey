package com.ivyinfo.donkey;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Initialize when the server starts, destroy when the server stops, and it will
 * last in the whole life of the server
 * 
 * @author sk
 * 
 */
public class DonkeyWebContainerListener extends ContextLoaderListener {
	
	public DonkeyWebContainerListener() {
		super();
	}
	
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	public void contextInitialized(ServletContextEvent event) {

		super.contextInitialized(event);  
		
        ServletContext context = event.getServletContext();   
        ApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(context);   
        
        InputStream inStream = context.getResourceAsStream("/WEB-INF/donkey.properties");
        
        try {
        	Configuration.initilize(inStream);
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
}
