package com.ivyinfo.donkey.db.supplier;

public class SupplierInfoBean implements java.io.Serializable{
	private static final long serialVersionUID = -4433034043604684316L;
	
	private String id;
	private String name;
	private String callbackurl;
	private String skey;
	private String appid;
	
	private int from;				//翻页	从第几条开始
	private int to;					//翻页	到第几条结束
	private String order;			//排序	根据什么字段排序，字段名，例如：id
	private String orderby;			//排序	asc,desc
	
	public String getSkey() {
		return skey;
	}
	
	public void setSkey(String skey) {
		this.skey = skey;
	}
	
	public int getFrom() {
		return from;
	}
	
	public void setFrom(int from) {
		this.from = from;
	}
	
	public int getTo() {
		return to;
	}
	
	public void setTo(int to) {
		this.to = to;
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
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCallbackurl() {
		return callbackurl;
	}
	
	public void setCallbackurl(String callbackurl) {
		this.callbackurl = callbackurl;
	}
	
	public String getAppid() {
		return appid;
	}
	
	public void setAppid(String appid) {
		this.appid = appid;
	}
}
