package com.ivyinfo.donkey.db.supplier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class SupplierInfoDAO extends SqlMapClientDaoSupport {
	
	public void AddSupplierInfo(SupplierInfoBean supplierInfoBean) throws Exception{
		this.getSqlMapClientTemplate().insert("AddSupplierInfo", supplierInfoBean);
	}
	
	public void UpdSupplierInfo(SupplierInfoBean supplierInfoBean) throws Exception{
		this.getSqlMapClientTemplate().update("UpdSupplierInfo", supplierInfoBean);
	}
	
	public void DelSupplierInfo(String id) throws Exception{
		this.getSqlMapClientTemplate().delete("DelSupplierInfo", id);
	}
	
	public int getSupplierInfoCount(SupplierInfoBean supplierInfoBean) throws Exception{
		Map map = new HashMap();
		if(supplierInfoBean != null){
			String id = supplierInfoBean.getId();
			id = (id == null)?"":id;
			String name = supplierInfoBean.getName();
			name = (name == null)?"":name;
			String callbackurl = supplierInfoBean.getCallbackurl();
			callbackurl = (callbackurl == null)?"":callbackurl;
			String key = supplierInfoBean.getSkey();
			key = (key == null)?"":key;
			String appid = supplierInfoBean.getAppid();
			appid = (appid == null)?"":appid;
			
			if(!"".equals(id)){
				map.put("id", id);
			}
			if(!"".equals(name)){
				map.put("name", name);
			}
			if(!"".equals(callbackurl)){
				map.put("callbackurl", callbackurl);
			}
			if(!"".equals(key)){
				map.put("key", key);
			}
			if(!"".equals(appid)){
				map.put("appid", appid);
			}
		}
		String s = (String)this.getSqlMapClientTemplate().queryForObject("getSupplierInfoCount", map);
		return Integer.parseInt(s);
	}
	
	public List<SupplierInfoBean> getSupplierInfo(SupplierInfoBean supplierInfoBean) throws Exception{
		Map map = new HashMap();
		if(supplierInfoBean != null){
			String id = supplierInfoBean.getId();
			id = (id == null)?"":id;
			String name = supplierInfoBean.getName();
			name = (name == null)?"":name;
			String callbackurl = supplierInfoBean.getCallbackurl();
			callbackurl = (callbackurl == null)?"":callbackurl;
			String key = supplierInfoBean.getSkey();
			key = (key == null)?"":key;
			String appid = supplierInfoBean.getAppid();
			appid = (appid == null)?"":appid;
			int from = supplierInfoBean.getFrom();
			int to = supplierInfoBean.getTo();
			String order = supplierInfoBean.getOrder();
			order = (order == null)?"":order;
			String orderby = supplierInfoBean.getOrderby();
			orderby = (orderby == null)?"":orderby;
			
			if(!"".equals(id)){
				map.put("id", id);
			}
			if(!"".equals(name)){
				map.put("name", name);
			}
			if(!"".equals(callbackurl)){
				map.put("callbackurl", callbackurl);
			}
			if(!"".equals(key)){
				map.put("key", key);
			}
			if(!"".equals(appid)){
				map.put("appid", appid);
			}
			if(to != 0){
				map.put("from", from);
				map.put("to", to);
			}
			if(!"".equals(order) && !"".equals(orderby)){
				map.put("order", order);
				map.put("orderby", orderby);
			}
		}
		
		return this.getSqlMapClientTemplate().queryForList("getSupplierInfo", map);
	}
}
