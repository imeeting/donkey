package com.ivyinfo.donkey.http.request;

import java.util.HashMap;

import com.ivyinfo.donkey.ConferenceManager;

public class DonkeyRequestHashmap {

	private static DonkeyRequestHashmap instance;

	private HashMap<String, String> requestMap;

	private DonkeyRequestHashmap() {
		requestMap = new HashMap<String, String>();
		requestMap.put(ConferenceManager.Create,
				CreateConferenceRequest.class.getCanonicalName());
		requestMap.put(ConferenceManager.Destroy,
				DestroyConferenceRequest.class.getCanonicalName());
		// requestMap.put(ConferenceManager.Join,
		// JoinConferenceRequest.class.getCanonicalName());
		 requestMap.put(ConferenceManager.Join,
		 JoinConferenceRequest2.class.getCanonicalName());
		requestMap.put(ConferenceManager.Unjoin,
				UnjoinConferenceRequest.class.getCanonicalName());
		requestMap.put(ConferenceManager.Mute,
				MuteConnectionRequest.class.getCanonicalName());
		requestMap.put(ConferenceManager.Unmute,
				UnmuteConnectionRequest.class.getCanonicalName());
		requestMap.put(ConferenceManager.Announce,
				AnnounceConferenceRequest.class.getCanonicalName());
		requestMap.put(ConferenceManager.Stopannounce,
				StopAnnounceConferenceRequest.class.getCanonicalName());
		requestMap.put(ConferenceManager.Record,
				RecordConferenceRequest.class.getCanonicalName());
		requestMap.put(ConferenceManager.Stoprecord,
				StopRecordConferenceRequest.class.getCanonicalName());
	}

	public static synchronized DonkeyRequestHashmap getInstance() {
		if (instance == null) {
			instance = new DonkeyRequestHashmap();
		}
		return instance;
	}

	public String getRequestClassName(String method) {
		return requestMap.get(method);
	}
}
