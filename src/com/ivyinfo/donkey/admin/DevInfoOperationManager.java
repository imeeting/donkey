package com.ivyinfo.donkey.admin;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.db.supplier.DevAppIDInfoManager;
import com.ivyinfo.donkey.db.supplier.SupplierInfoBean;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.http.api.DonkeyResponseMessage;
import com.ivyinfo.util.Pager;
import com.ivyinfo.util.RandomString;

/**
 * Manage the operation requests of developer info
 * 
 * @author sk
 * 
 */
public class DevInfoOperationManager {
	
	private static Logger logger = Logger.getLogger(DevInfoOperationManager.class);

	public static void dispatch(HttpServletRequest request,
			HttpServletResponse response) {
		String method = request.getParameter(Constant.Method);

		if (method != null) {
			try {
				if (method.equals(Constant.DevInfoRegister)) {
					// do registration
					registerDevInfo(request, response);
				} else if (method.equals(Constant.DevInfoDelete)) {
					// do deletion
					deleteDevInfo(request, response);
				} else if (method.equals(Constant.DevInfoGetAll)) {
					// get all dev info
					getAllDevInfo(request, response);
				} else if (method.equals(Constant.DevInfoQuery)) {
					// do query
				} else if (method.equals(Constant.DevInfoEdit)) {
					// do edit
					editDevInfo(request, response);
				} else if (method.equals(Constant.DevInfoMultiDelete)) {
					deleteMultipleDevInfo(request, response);
				}
			} catch (Exception e) {
				e.printStackTrace();
				DonkeyResponse.InternalException(response, e);
			}
		} else {
			DonkeyResponse.BadRequest(response);
		}
	}

	private static void registerDevInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String name = request.getParameter(Constant.Name);
		String callbackURL = request.getParameter(Constant.CallBackURL);
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
		}
	}

	/**
	 * delete one dev info
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private static void deleteDevInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String appid = request.getParameter(Constant.AppID);
		if (appid != null && !appid.equals("")) {
			DevAppIDInfoManager.deleteSupplierInfoByAppid(appid);
		}
	}
	
	/**
	 * delete multiple dev info
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private static void deleteMultipleDevInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String appids = request.getParameter(Constant.IDS);
		
		if (appids != null && !appids.equals("")) {
			String[] appidArray = appids.split(",");
			if (appidArray != null) {
				for (int i = 0; i < appidArray.length; i++) {
					String appid = appidArray[i];
					if (appid != null && !appid.equals("")) {
						DevAppIDInfoManager.deleteSupplierInfoByAppid(appid);
					}
				}
			}
		}
	}

	private static void getAllDevInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String offset = request.getParameter(Constant.Offset);
		if(offset == null || offset.length()<1){
			offset = "1";
		}
		int pageSize = 10;
		int index = Integer.parseInt(offset);
		
		int all = DevAppIDInfoManager.getAllSupplierInfoCount();
		
		logger.debug("getAllDevInfo - total count: " + all);
		
		SupplierInfoBean queryBean = new SupplierInfoBean();
		queryBean.setFrom((index - 1) * pageSize);
		queryBean.setTo(pageSize);
		
		List<SupplierInfoBean> list = DevAppIDInfoManager.getSupplierInfos(queryBean);
		logger.debug("getAllDevInfo - list size: " + list.size());
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
		
		Pager pager = new Pager(index, pageSize, all, "devinfomanage?m=devinfo_getall");
		
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
	
	private static void editDevInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = request.getParameter(Constant.ID);
		String callbackurl = request
				.getParameter(Constant.CallBackURL);

		logger.info("[DevInfoOperationManager] dispatch - id: "
				+ id + " callbackurl: " + callbackurl);

		if (id != null && callbackurl != null && !id.equals("")
				&& !callbackurl.equals("")) {
			SupplierInfoBean queryBean = new SupplierInfoBean();
			queryBean.setId(id);
			SupplierInfoBean editBean = DevAppIDInfoManager.querySupplierInfo(queryBean);
			editBean.setCallbackurl(callbackurl);
			DevAppIDInfoManager.updateSupplierInfo(editBean);

		}
	}
}
