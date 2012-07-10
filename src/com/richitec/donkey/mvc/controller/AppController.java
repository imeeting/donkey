package com.richitec.donkey.mvc.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.db.supplier.SupplierInfoBean;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.http.api.DonkeyResponseMessage;
import com.ivyinfo.util.Pager;
import com.ivyinfo.util.RandomString;
import com.richitec.donkey.ContextLoader;
import com.richitec.donkey.mvc.model.ApplicationDAO;

@Controller
@RequestMapping(value="/app")
public class AppController {
	
	private static Log log = LogFactory.getLog(AppController.class);
	
	private ApplicationDAO applicationDAO;
	
	@PostConstruct
	public void init(){
		applicationDAO = ContextLoader.getApplicationDAO();
	}
	
	@RequestMapping
	public String index(HttpSession session){
		return "appidgen";
	}

	@RequestMapping(value="/")
	public String app(HttpSession session){
		return index(session);
	}
	
	@RequestMapping(value="/list")
	public void list(
			HttpSession session,
			HttpServletResponse response,
			@RequestParam(value="offset", required=false, defaultValue="1") Integer offset) throws Exception{
		
		int pageSize = 10;
		int all = applicationDAO.getAllSupplierInfoCount();
		
		log.debug("getAllDevInfo - total count: " + all);
		
		List<SupplierInfoBean> list = 
			applicationDAO.getSupplierInfos((offset - 1) * pageSize, pageSize);
		log.debug("getAllDevInfo - list size: " + list.size());
		JSONArray infoArray = new JSONArray();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				SupplierInfoBean bean = list.get(i);
				
				JSONObject devinfo = new JSONObject();
				devinfo.put(Constant.ID, bean.getId());
				devinfo.put("name", bean.getName());
				devinfo.put("appid", bean.getAppid());
				devinfo.put("appkey", bean.getSkey());
				devinfo.put("callbackurl", bean.getCallbackurl());
				infoArray.put(devinfo);
			}
		}
		
		Pager pager = new Pager(offset, pageSize, all, "/donkey/app/list");
		
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
	
	@RequestMapping(value="/delete")
	public void delete(
			HttpServletResponse response,
			@RequestParam(value="ids") String idList) throws Exception{
		if (idList != null && !idList.equals("")) {
			String[] appidArray = idList.split(",");
			if (appidArray != null) {
				for (int i = 0; i < appidArray.length; i++) {
					String appid = appidArray[i];
					if (appid != null && !appid.equals("")) {
						applicationDAO.deleteSupplierInfoByAppid(appid);
					}
				}
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	@RequestMapping(value="/edit")
	public void edit(
			HttpServletResponse response,
			@RequestParam(value="id") String id,
			@RequestParam(value="callbackurl") String callbackurl) throws Exception{
		int r = applicationDAO.updateCallBackURL(id, callbackurl);
		if (r == 1){
			response.setStatus(HttpServletResponse.SC_OK);
		} else if (r == 0) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else {
			// more records have same primary key 'id'
		}
	}
	
	@RequestMapping(value="/register")
	public void register(
			HttpServletResponse response,
			@RequestParam(value="name") String name,
			@RequestParam(value="callbackurl") String callbackURL) throws Exception{
		/*
		// fist check if the name exists in the database
		SupplierInfoBean queryBean = new SupplierInfoBean();
		queryBean.setName(name);
		SupplierInfoBean ret = DevAppIDInfoManager.querySupplierInfo(queryBean);
		if (ret != null) {
			
			DonkeyResponse.Conflict(response, DonkeyResponseMessage
					.GeneralError("Name exists! Try another name!"));
			return;
		}
		*/
		String appID = RandomString.genRandomNum(8);
		String key = RandomString.genRandomChars(8);

		SupplierInfoBean bean = new SupplierInfoBean();
		bean.setAppid(appID);
		bean.setSkey(key);
		bean.setName(name);
		bean.setCallbackurl(callbackURL);

		applicationDAO.addSupplierInfo(bean);
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
