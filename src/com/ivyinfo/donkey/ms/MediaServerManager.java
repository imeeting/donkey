package com.ivyinfo.donkey.ms;

import java.util.ArrayList;

import com.ivyinfo.donkey.Configuration;
import com.ivyinfo.donkey.ms.msml.MSMLMediaServer;

/**
 * load balance for media server. The method getMediaServerSipUri()
 * will select one that has lowest load.
 * 
 * @author huuguanghui
 *
 */
public class MediaServerManager {
	
	private static MediaServerManager _instance;
	
	private ArrayList<IMediaServer> serverList;

	private MediaServerManager(){
		serverList = new ArrayList<IMediaServer>();
		IMediaServer msmlServer = new MSMLMediaServer(Configuration.getMediaServerSipUri());
		serverList.add(msmlServer);
	}
	
	public static synchronized MediaServerManager getInstance(){
		if (null == _instance){
			_instance = new MediaServerManager();
		}
		return _instance;
	}
	
	public IMediaServer getMediaServer(){
		return serverList.get(0);
	}
}
