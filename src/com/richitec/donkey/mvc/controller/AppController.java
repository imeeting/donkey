package com.richitec.donkey.mvc.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
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
import com.ivyinfo.donkey.db.supplier.DevAppIDInfoManager;
import com.ivyinfo.donkey.db.supplier.SupplierInfoBean;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.http.api.DonkeyResponseMessage;
import com.ivyinfo.util.Pager;
import com.ivyinfo.util.RandomString;

@Controller
@RequestMapping(value="/app")
public class AppController {
	
	private static Log log = LogFactory.getLog(AppController.class);
	
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
		int all = DevAppIDInfoManager.getAllSupplierInfoCount();
		
		log.debug("getAllDevInfo - total count: " + all);
		
		SupplierInfoBean queryBean = new SupplierInfoBean();
		queryBean.setFrom((offset - 1) * pageSize);
		queryBean.setTo(pageSize);
		
		List<SupplierInfoBean> list = DevAppIDInfoManager.getSupplierInfos(queryBean);
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
						DevAppIDInfoManager.deleteSupplierInfoByAppid(appid);
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
		if (id != null && callbackurl != null && !id.equals("")
				&& !callbackurl.equals("")) {
			SupplierInfoBean queryBean = new SupplierInfoBean();
			queryBean.setId(id);
			SupplierInfoBean editBean = DevAppIDInfoManager.querySupplierInfo(queryBean);
			editBean.setCallbackurl(callbackurl);
			DevAppIDInfoManager.updateSupplierInfo(editBean);
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/register")
	public void register(
			HttpServletResponse response,
			@RequestParam(value="name") String name,
			@RequestParam(value="callbackurl") String callbackURL) throws Exception{
		if (name != null && !name.equals("")) {
			// fist check if the name exists in the database
			SupplierInfoBean queryBean = new SupplierInfoBean();
			queryBean.setName(name);
			SupplierInfoBean ret = DevAppIDInfoManager.querySupplierInfo(queryBean);
			if (ret != null) {
				
				DonkeyResponse.Conflict(response, DonkeyResponseMessage
						.GeneralError("Name exists! Try another name!"));
				return;
			}

			String appID = RandomString.genRandomNum(8);
			String key = RandomString.genRandomChars(8);

			SupplierInfoBean bean = new SupplierInfoBean();
			bean.setAppid(appID);
			bean.setSkey(key);
			bean.setName(name);
			bean.setCallbackurl(callbackURL);

			DevAppIDInfoManager.addSupplierInfo(bean);
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}
