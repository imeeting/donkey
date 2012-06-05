package com.ivyinfo.donkey.db.supplier;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ivyinfo.donkey.SpringContextHolder;

/**
 * DonkeyDevAppIDInfoManager - manage the operations of developer info
 * 
 * @author sk
 * 
 */
public class DevAppIDInfoManager {
	
	private static final Logger logger = Logger.getLogger(DevAppIDInfoManager.class);

	private static ConcurrentHashMap<String, SupplierInfoBean> 
		devInfoMap = new ConcurrentHashMap<String, SupplierInfoBean>();
	
	private static SupplierInfoDAO dao(){
		return (SupplierInfoDAO) SpringContextHolder.getBean("supplierInfoDAO");
	}

	/**
	 * add supplier info (dev info)
	 * 
	 * @param supplierInfoBean
	 *            id：increase automatically, do not input the field
	 * @throws Exception
	 */
	public static void addSupplierInfo(SupplierInfoBean bean) throws Exception {
		logger.info("addSupplierInfo - name: " + bean.getName());// @test
		dao().AddSupplierInfo(bean);
	}

	/**
	 * modify the supplier info (dev info)
	 * 
	 * @param supplierInfoBean
	 *            id：can't be null or empty string ("")
	 * @throws Exception
	 */
	public static void updateSupplierInfo(SupplierInfoBean bean) throws Exception {
		logger.info("updateSupplierInfo - name: " + bean.getName());// @test
		dao().UpdSupplierInfo(bean);
	}

	/**
	 * delete the dev info
	 * 
	 * @param appid
	 * @throws Exception
	 */
	public static void deleteSupplierInfoByAppid(String appid) throws Exception {
		SupplierInfoBean bean = querySupplierInfo(appid);
		if (bean != null) {
			dao().DelSupplierInfo(bean.getId());
		}
	}

	/**
	 * get the supplier info (dev info)
	 * 
	 * @param supplierInfoBean
	 *            - input the value into the field as condition, and the other
	 *            fields can be null. from and to field can be null if there is
	 *            no page down or page up. order and orderby can be null if
	 *            there is no sorting.
	 * 
	 * @return List, and there may be multiple SupplierInfoBeans in the list
	 * @throws Exception
	 */
	public static List<SupplierInfoBean> getSupplierInfos(SupplierInfoBean bean)
			throws Exception {
		return dao().getSupplierInfo(bean);
	}

	/**
	 * get call back url by appid
	 * 
	 * @param appid
	 * @return
	 */
	public static String getCallBackURL(String appid) {
		String callbackURL = "";
		try {
			SupplierInfoBean bean = querySupplierInfo(appid);
			if (bean != null) {
				callbackURL = bean.getCallbackurl();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return callbackURL;
	}

	/**
	 * query the supplier info by appid, first it checks the hashmap in the
	 * memory, if the specified appid doesn't exist, it will read the info from
	 * database, and put it into hashmap
	 * 
	 * @param appid
	 * @return SupplierInfoBean or null if cannot get the info by appid
	 * @throws Exception
	 */
	public static SupplierInfoBean querySupplierInfo(String appid) throws Exception {
		SupplierInfoBean ret = null;
		SupplierInfoBean bean = devInfoMap.get(appid);
		if (bean == null) {
			SupplierInfoBean queryBean = new SupplierInfoBean();
			queryBean.setAppid(appid);
			List<SupplierInfoBean> list = getSupplierInfos(queryBean);

			if (list.size() > 0) {
				ret = list.get(0);
				devInfoMap.put(appid, ret);
			}
		} else {
			ret = bean;
		}
		return ret;
	}

	/**
	 * query the supplier info (dev info)
	 * 
	 * @param queryBean
	 * @return
	 * @throws Exception
	 */
	public static SupplierInfoBean querySupplierInfo(SupplierInfoBean queryBean)
			throws Exception {
		List<SupplierInfoBean> list = getSupplierInfos(queryBean);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * get all supplier info count number
	 * 
	 * @return
	 * @throws Exception
	 */
	public static int getAllSupplierInfoCount() throws Exception {
		return dao().getSupplierInfoCount(new SupplierInfoBean());
	}
}
