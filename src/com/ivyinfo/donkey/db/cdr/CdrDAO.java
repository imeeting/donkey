package com.ivyinfo.donkey.db.cdr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class CdrDAO extends SqlMapClientDaoSupport {

	public int addCdr(CdrBean cdr) throws Exception {
		return ((Integer)this.getSqlMapClientTemplate().insert("addCdr", cdr)).intValue();
	}

	public void updateCdr(CdrBean cdr) throws Exception {
		Map map = new HashMap();
		if (cdr != null) {
			String appid = cdr.getAppid();
			appid = (appid == null) ? "" : appid;

			String conference = cdr.getConference();
			conference = (conference == null) ? "" : conference;

			String callid = cdr.getCallid();
			callid = (callid == null) ? "" : callid;

			Long starttime = cdr.getStarttime();

			Long endtime = cdr.getEndtime();

			Integer state = cdr.getState();

			if (!"".equals(appid)) {
				map.put("appid", appid);
			}
			if (!"".equals(conference)) {
				map.put("conference", conference);
			}
			if (!"".equals(callid)) {
				map.put("callid", callid);
			}
			if (starttime != null) {
				map.put("starttime", starttime);
			}
			if(endtime != null){
				map.put("endtime", endtime);
			}
			if(state != null){
				map.put("state", state);
			}
		}

		this.getSqlMapClientTemplate().update("updateCdr", map);
	}
	
	public int getCdrCount(CdrBean cdr) throws Exception {
		Map map = new HashMap();
		if(cdr != null){
			Integer id = cdr.getId();
			
			String appid = cdr.getAppid();
			appid = (appid == null)?"":appid;
			
			String conference = cdr.getConference();
			conference = (conference == null)?"":conference;
			
			String phone = cdr.getPhone();
			phone = (phone == null)?"":phone;
			
			String sipuri = cdr.getSipuri();
			sipuri = (sipuri == null)?"":sipuri;
			
			String callid = cdr.getCallid();
			callid = (callid == null)?"":callid;
			
			Long created = cdr.getCreated();
			
			Long starttime = cdr.getStarttime();
			
			Long endtime = cdr.getEndtime();
			
			Integer state = cdr.getState();
			
			Long querystarttime = cdr.getQueryStartTime();
			
			Long queryendtime = cdr.getQueryEndTime();
			
			Integer querystate = cdr.getQueryState();
			
			if(null != id){
				map.put("id", id);
			}
			if(!"".equals(appid)){
				map.put("appid", appid);
			}
			if(!"".equals(conference)){
				map.put("conference", conference);
			}
			if(!"".equals(phone)){
				map.put("phone", phone);
			}
			if(!"".equals(sipuri)){
				map.put("sipuri", sipuri);
			}
			if(!"".equals(callid)){
				map.put("callid", callid);
			}
			if(created != null){
				map.put("created", created);
			}
			if(starttime != null){
				map.put("starttime", starttime);
			}
			if(endtime != null){
				map.put("endtime", endtime);
			}
			if(state != null){
				map.put("state", state);
			}
			if(querystarttime != null){
				map.put("querystarttime", querystarttime);
			}
			if(queryendtime != null){
				map.put("queryendtime", queryendtime);
			}
			if(querystate != null){
				map.put("querystate", querystate);
			}
		}
		
		String s = (String)this.getSqlMapClientTemplate().queryForObject("getCdrCount", map);
		return Integer.parseInt(s);
	}

	public List<CdrBean> queryCdr(CdrBean cdr) throws Exception {
		// TODO Auto-generated method stub
		Map map = new HashMap();
		if(cdr != null){
			Integer id = cdr.getId();
			
			String appid = cdr.getAppid();
			appid = (appid == null)?"":appid;
			
			String conference = cdr.getConference();
			conference = (conference == null)?"":conference;
			
			String phone = cdr.getPhone();
			phone = (phone == null)?"":phone;
			
			String sipuri = cdr.getSipuri();
			sipuri = (sipuri == null)?"":sipuri;
			
			String callid = cdr.getCallid();
			callid = (callid == null)?"":callid;
			
			Long created = cdr.getCreated();
			
			Long starttime = cdr.getStarttime();
			
			Long endtime = cdr.getEndtime();
			
			Integer state = cdr.getState();
			
			Long querystarttime = cdr.getQueryStartTime();
			
			Long queryendtime = cdr.getQueryEndTime();
			
			Integer querystate = cdr.getQueryState();
			
			Integer start = cdr.getStart();
			Integer end = cdr.getEnd();
			String order = cdr.getOrder();
			order = (order == null)?"":order;
			String orderby = cdr.getOrderby();
			orderby = (orderby == null)?"":orderby;
			
			if(null != id){
				map.put("id", id);
			}
			if(!"".equals(appid)){
				map.put("appid", appid);
			}
			if(!"".equals(conference)){
				map.put("conference", conference);
			}
			if(!"".equals(phone)){
				map.put("phone", phone);
			}
			if(!"".equals(sipuri)){
				map.put("sipuri", sipuri);
			}
			if(!"".equals(callid)){
				map.put("callid", callid);
			}
			if(created != null){
				map.put("created", created);
			}
			if(starttime != null){
				map.put("starttime", starttime);
			}
			if(endtime != null){
				map.put("endtime", endtime);
			}
			if(state != null){
				map.put("state", state);
			}
			if(querystarttime != null){
				map.put("querystarttime", querystarttime);
			}
			if(queryendtime != null){
				map.put("queryendtime", queryendtime);
			}
			if(querystate != null){
				map.put("querystate", querystate);
			}
			if(end != null){
				map.put("start", start);
				map.put("end", end);
			}
			if(!"".equals(order) && !"".equals(orderby)){
				map.put("order", order);
				map.put("orderby", orderby);
			}
		}
		
		return this.getSqlMapClientTemplate().queryForList("queryCdr", map);
	}

}
