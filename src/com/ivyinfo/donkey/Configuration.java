package com.ivyinfo.donkey;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	
	private static Properties properties = new Properties();

	public static void initilize(InputStream inStream) throws IOException{
		properties.load(inStream);
		inStream.close();
	}
	
	public static String getProperty(String key){
		return properties.getProperty(key);
	}
	
	public static String getProperty(String key, String defaultValue){
		return properties.getProperty(key, defaultValue);
	}
	
	public static String getSipUri(){
		return properties.getProperty("sip_uri", "sip:donkey.com");
	}
	
	public static Integer getOutboundPort(){
	    String port = properties.getProperty("outbound_port", "5060");
		return Integer.parseInt(port);	
	}	
	
	public static String getMediaServerSipUri(){
		return properties.getProperty("ms_sip_uri");
	}
	
	public static String getOutboundIpAddrToMediaServer(){
		return properties.getProperty("outbound_ip_to_ms");
	}
	
	public static String getSoftSwitchSipURI(){
		return properties.getProperty("ss_sip_uri");
	}		
	
	public static String getOutboundIpAddrToSoftSwitch(){
		return properties.getProperty("outbound_ip_to_ss");
	}
	
	public static Integer getConfSessionExpire(){
		String expire = properties.getProperty("conf_session_expire");
		return Integer.parseInt(expire);
	}
	
	public static String getNFSHost(){
		return properties.getProperty("nfs_host", "127.0.0.1");
	}
	
	public static String getRecordPathURL(){
		return properties.getProperty("recordpath_url");
	}
}
