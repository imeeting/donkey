package com.ivyinfo.donkey.http.api;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.util.HexUtils;
import org.apache.log4j.Logger;

import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.db.supplier.DevAppIDInfoManager;
import com.ivyinfo.donkey.db.supplier.SupplierInfoBean;


public class Validater {
	
	private static final Logger logger = Logger.getLogger(Validater.class);

	public static boolean isValidRequest(HttpServletRequest request){
		String appid = request.getParameter(Constant.AppID);
		String reqid = request.getParameter(Constant.ReqID);
		String m = request.getParameter(Constant.Method);
		String sig = request.getParameter(Constant.Signature);
		if (isValidAppID(appid) &&
			isValidReqID(reqid) &&
			isValidSignature(request, sig)){
			return true;
		}
		return false;
	}

	private static boolean isValidAppID(String appid) {
		if (appid == null) {
			return false;
		}
		
		if (appid.length() == 3) {
			return true;
		}
		
		try {
			SupplierInfoBean bean = DevAppIDInfoManager.querySupplierInfo(appid);
			if (bean != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	private static boolean isValidReqID(String reqid){
		if (null == reqid || reqid.trim().length() <= 0){
			return false;
		}
		return true;
	}

	private static boolean isValidSignature(HttpServletRequest request,
			String sig) {
		String appid = request.getParameter(Constant.AppID);
		if (appid == null) {
			return false;
		} else if (appid.length() == 3) {
			return true;
		}
		
		
		ArrayList<String> paramList = new ArrayList<String>();
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String key = names.nextElement();
			if (!key.equals(Constant.Signature)) {
				String value = request.getParameter(key);
				StringBuffer sb = new StringBuffer();
				sb.append(key).append("=").append(value);
				paramList.add(sb.toString());
			}
		}

		Collections.sort(paramList);

		StringBuffer sb2 = new StringBuffer();
		for (int i = 0; i < paramList.size(); i++) {
			sb2.append(paramList.get(i));
		}

		try {
			SupplierInfoBean devInfoBean = DevAppIDInfoManager.querySupplierInfo(appid);
			if (devInfoBean != null) {
				sb2.append(devInfoBean.getSkey());
			} else {
				return false;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}

		boolean ret = false;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] md5Value = md5.digest(sb2.toString().getBytes());
			String digest = HexUtils.convert(md5Value);

			logger.debug("digest: " + digest);

			if (digest.equalsIgnoreCase(sig)) {
				ret = true;
				logger.debug("valid signature");
			} else {
				logger.info("invalid signature");
				logger.info(printList(paramList));
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return ret;
	}


	// @test
	private static String printList(List<String> list) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i) + "\n");
		}
		return sb.toString();
	}
}
