package com.ivyinfo.donkey.db.cdr;


public class CdrBean implements java.io.Serializable{
	
	/**
	 * CdrBean serialVersionUID
	 */
	private static final long serialVersionUID = -4105807191639065085L;
	
	private Integer id;
	private String appid;
	private String conference;
	private String phone;
	private String sipuri;
	private String callid;
	private Long created;
	private Long starttime;
	private Long endtime;
	private Integer state;
	
	private Long duration;

	private Integer start; // 翻页 从第几条开始
	private Integer end; // 翻页 到第几条结束
	private String order; // 排序 根据什么字段排序，字段名，例如：id
	private String orderby; // 排序 asc,desc

	private Long queryStartTime;
	private Long queryEndTime;
	
	private Integer queryState;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getAppid() {
		return appid;
	}
	
	public void setAppid(String appid) {
		this.appid = appid;
	}
	
	public String getConference() {
		return conference;
	}
	
	public void setConference(String conference) {
		this.conference = conference;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getSipuri() {
		return sipuri;
	}
	
	public void setSipuri(String sipuri) {
		this.sipuri = sipuri;
	}
	
	public String getCallid() {
		return callid;
	}
	
	public void setCallid(String callid) {
		this.callid = callid;
	}
	
	public Long getCreated() {
		return created;
	}
	
	public void setCreated(Long created) {
		this.created = created;
	}
	
	public Long getStarttime() {
		return starttime;
	}
	
	public void setStarttime(Long starttime) {
		this.starttime = starttime;
	}
	
	public Long getEndtime() {
		return endtime;
	}
	
	public void setEndtime(Long endtime) {
		this.endtime = endtime;
	}
	
	public Integer getState() {
		return state;
	}
	
	public void setState(Integer state) {
		this.state = state;
	}
	
	public Integer getStart() {
		return start;
	}
	
	public void setStart(Integer start) {
		this.start = start;
	}
	
	public Integer getEnd() {
		return end;
	}
	
	public void setEnd(Integer end) {
		this.end = end;
	}
	
	public String getOrder() {
		return order;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}
	
	public String getOrderby() {
		return orderby;
	}
	
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	
	public Long getQueryStartTime() {
		return queryStartTime;
	}
	
	public void setQueryStartTime(Long querystarttime) {
		this.queryStartTime = querystarttime;
	}
	
	public Long getQueryEndTime() {
		return queryEndTime;
	}
	
	public void setQueryEndTime(long queryendtime) {
		this.queryEndTime = queryendtime;
	}
	
	public Integer getQueryState(){
		return queryState;
	}
	
	public void setQueryState(Integer querystate){
		this.queryState = querystate;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}
	
}
