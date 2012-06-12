package com.richitec.donkey.mvc.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.db.cdr.CdrBean;
import com.ivyinfo.donkey.db.cdr.CdrManager;
import com.ivyinfo.util.DonkeyUtil;
import com.ivyinfo.util.Pager;
import com.ivyinfo.util.Time;

@Controller
@RequestMapping(value="/cdr")
public class CdrController {
	
	private static Log log = LogFactory.getLog(CdrController.class);
	
	@RequestMapping
	public String index(HttpSession session){
		return "cdr";
	}
	
	@RequestMapping(value="/list")
	public @ResponseBody String list(
			@RequestParam(value="appid", required=false) String appId,
			@RequestParam(value="querystarttime", required=false) String queryStartTime,
			@RequestParam(value="queryendtime", required=false) String queryEndTime,
			@RequestParam(value="offset", defaultValue="1") Integer offset) throws Exception{

		int pageSize = 10;
		
		CdrBean cdrBean=new CdrBean();
		cdrBean.setAppid(appId);
		cdrBean.setQueryState(2);
		
		if(!DonkeyUtil.isValidString(queryStartTime)){
			cdrBean.setQueryStartTime(-1L);
		}
		else{
			cdrBean.setQueryStartTime(Time.getDateFormatYMDHMSString(queryStartTime).getTime());
		}
		
		if(!DonkeyUtil.isValidString(queryEndTime)){
			cdrBean.setQueryEndTime(Long.MAX_VALUE);
		}
		else{
			cdrBean.setQueryEndTime(Time.getDateFormatYMDHMSString(queryEndTime).getTime());
		}
		
		int all = CdrManager.getAllEligibleCdrCount(cdrBean);
		
		log.debug("getAllEligibleCdr - total count: " + all);
		
		cdrBean.setStart((offset - 1) * pageSize);
		cdrBean.setEnd(pageSize);
		
		List<CdrBean> list = CdrManager.queryCdrs(cdrBean);
		log.debug("getAllEligibleCdr - list size: " + list.size());
		JSONArray infoArray = new JSONArray();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				CdrBean bean = list.get(i);
				
				JSONObject cdr = new JSONObject();
				cdr.put("appid", bean.getAppid());
				cdr.put("phonenumber", bean.getPhone());
				cdr.put("createdtime", Time.getDateTime(new Date(bean.getCreated())));
				cdr.put("starttime", Time.getDateTime(new Date(bean.getStarttime())));
				cdr.put("endtime", Time.getDateTime(new Date(bean.getEndtime())));
				cdr.put("state", bean.getState());
				cdr.put("duration", Time.format(bean.getDuration()));
				infoArray.put(cdr);
			}
		}
		
		Pager pager = new Pager(offset, pageSize, all, "/donkey/cdr/list");
		
		JSONObject data = new JSONObject();
		JSONObject jsonPager = new JSONObject();
		jsonPager.put(Constant.Offset, pager.getOffset());
		jsonPager.put(Constant.Previous, pager.getPreviousPage());
		jsonPager.put(Constant.Next, pager.getNextPage());
		jsonPager.put(Constant.PageNumber, pager.getPageNumber());
		data.put(Constant.List, infoArray);
		data.put(Constant.Pager, jsonPager);
		
		return data.toString();
	}
}
