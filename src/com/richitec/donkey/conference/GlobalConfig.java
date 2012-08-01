package com.richitec.donkey.conference;

public class GlobalConfig {
	
	private String sipUri;
	private String outboundIP;
	private Integer outboundPort;
	private String mediaServerSipUri;
	private String softSwitchSipUri;
	private String softSwitchIP;
	private Integer expire;
	
	private String noConferenceVoice;
	private String joinConferenceVoice;
	private String inputConferenceVoice;
	private String delayVoice;
	
	public void setSipUri(String sipUri){
		this.sipUri = sipUri;
	}
	
	public String getSipUri(){
		return this.sipUri;
	}
	
	public void setOutboundIP(String ip){
		this.outboundIP = ip;
	}
	
	public String getOutboundIP(){
		return this.outboundIP;
	}
	
	public void setOutboundPort(Integer port){
		this.outboundPort = port;
	}
	
	public Integer getOutboundPort(){
		return this.outboundPort;
	}
	
	public void setMediaServerSipUri(String sipUri){
		this.mediaServerSipUri = sipUri;
	}
	
	public String getMediaServerSipUri(){
		return this.mediaServerSipUri;
	}
	
	public void setSoftSwitchSipUri(String sipUri){
		this.softSwitchSipUri = sipUri;
	}
	
	public void setSoftSwitchIP(String ipAddr){
		this.softSwitchIP = ipAddr;
	}
	
	public String getSoftSwitchSipUri(){
		return this.softSwitchSipUri;
	}
	
	public String getSoftSwitchIP(){
		return this.softSwitchIP;
	}
	
	public void setExpire(Integer expire){
		this.expire = expire;
	}
	
	public Integer getExpire(){
		return this.expire;
	}
	
	public void setNoConferenceVoice(String voice){
		this.noConferenceVoice = voice;
	}
	
	public String getNoConferenceVoice(){
		return this.noConferenceVoice;
	}
	
	public void setJoinConferenceVoice(String voice){
		this.joinConferenceVoice = voice;
	}
	
	public String getJoinConferenceVoice(){
		return this.joinConferenceVoice;
	}
	
	public void setInputConferenceVoice(String voice){
		this.inputConferenceVoice = voice;
	}
	
	public String getInputConferenceVoice(){
		return this.inputConferenceVoice;
	}
	
	public void setDelayVoice(String voice){
		this.delayVoice = voice;
	}
	
	public String getDelayVoice() {
		return this.delayVoice;
	}
}
