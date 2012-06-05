package com.ivyinfo.donkey.admin;

import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.db.cdr.CdrBean;
import com.ivyinfo.donkey.db.cdr.CdrManager;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.util.DonkeyUtil;
import com.ivyinfo.util.Pager;
import com.ivyinfo.util.Time;

/**
 * Manage the operation requests of developer info
 * 
 * @author qk
 * 
 */
public class CdrOperationManager {
	
	private static Logger logger = Logger.getLogger(CdrOperationManager.class.getName());

	public static void dispatch(HttpServletRequest request,
			HttpServletResponse response) {
		String method = request.getParameter(Constant.Method);

		if (method != null) {
			try {
				if (method.equals(Constant.CdrGetAllEligible)) {
					// get all eligible cdr 
					getAllEligibleCdr(request, response);
				}
			} catch (Exception e) {
				e.printStackTrace();
				DonkeyResponse.InternalException(response, e);
			}
		} else {
			DonkeyResponse.BadRequest(response);
		}
	}
	
	private static void getAllEligibleCdr(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("utf-8");
		String offset = request.getParameter(Constant.Offset);
		if(offset == null || offset.length()<1){
			offset = "1";
		}
		int pageSize = 10;
		int index = Integer.parseInt(offset);
		
		String appId = request.getParameter(Constant.AppID);
		String queryStartTime = request.getParameter(Constant.CdrQueryStartTime);
		String queryEndTime = request.getParameter(Constant.CdrQueryEndTime);
		
		CdrBean cdrBean=new CdrBean();
		cdrBean.setAppid(appId);
		// querycallstate 2 : query call state "hangup" and "callfailed"
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
		
		logger.debug("getAllEligibleCdr - total count: " + all);
		
		cdrBean.setStart((index - 1) * pageSize);
		cdrBean.setEnd(pageSize);
		
		List<CdrBean> list = CdrManager.queryCdrs(cdrBean);
		logger.debug("getAllEligibleCdr - list size: " + list.size());
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
		
		Pager pager = new Pager(index, pageSize, all, "cdrmanage?m=cdr_getalleligible");
		
		JSONObject data = new JSONObject();
		JSONObject jsonPager = new JSONObject();
		jsonPager.put(Constant.Offset, pager.getOffset());
		jsonPager.put(Constant.Previous, pager.getPreviousPage());
		jsonPager.put(Constant.Next, pager.getNextPage());
		jsonPager.put(Constant.PageNumber, pager.getPageNumber());
		data.put(Constant.List, infoArray);
		data.put(Constant.Pager, jsonPager);
		String msg = data.toString();

		PrintWriter writer = response.getWriter();
		writer.print(msg);
		writer.close();
	}
}

