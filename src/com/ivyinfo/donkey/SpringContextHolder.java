package com.ivyinfo.donkey;

import org.springframework.context.ApplicationContext;

public class SpringContextHolder {
	
	private static ApplicationContext applicationContext;
	
	public static void setApplicationContext(ApplicationContext context){
		applicationContext = context;
	}
	
	public static ApplicationContext getApplicationContext(){
		return applicationContext;
	}
	
    public static Object getBean(String beanName) {
    	return applicationContext.getBean(beanName);
    }
}
