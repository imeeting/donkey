package com.ivyinfo.donkey;

public enum CallStatus {
	Calling(0),
	Connected(1),
	Hangup(2),
	CallFailed(3);
	
	private final int status;
	
	CallStatus(int status){
		this.status = status;
	}
	
	public int val() {
		return status;
	}
}