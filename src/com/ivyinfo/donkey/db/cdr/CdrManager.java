package com.ivyinfo.donkey.db.cdr;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.CallStatus;
import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.http.api.DonkeyGearman;
import com.ivyinfo.donkey.http.api.DonkeyGearmanMessage;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.util.DonkeyUtil;
import com.richitec.donkey.ContextLoader;

public class CdrManager {

	private static CdrDAO dao() {
		return ContextLoader.getCdrDAO();
	}

	public static int addCdr(CdrBean bean) throws Exception {
		return dao().addCdr(bean);
	}

	public static void updateCdr(CdrBean bean) throws Exception {
		dao().updateCdr(bean);
	}

	public static int getAllEligibleCdrCount(CdrBean bean) throws Exception {
		return dao().getCdrCount(bean);
	}

	public static List<CdrBean> queryCdrs(CdrBean bean) throws Exception {
		return dao().queryCdr(bean);
	}

	public static CdrBean generateCdrBean(SipSession session,
			HttpServletResponse response) {
		String appId = (String) session.getApplicationSession().getAttribute(
				Constant.AppID);
		String confId = (String) session.getApplicationSession().getId();
		String sipUri = (String) session
				.getAttribute(ConferenceManager.USER_SIP_URI);
		// String[] phoneNum = DonkeyUtil.splitText(sipUri, SipUriPrefix,
		// SipUriPostfix);
		String phone = DonkeyUtil.getUserFromSipURI(sipUri);
		String callId = session.getCallId();

		if (!DonkeyUtil.isValidString(appId)
				&& !DonkeyUtil.isValidString(confId)
				&& !DonkeyUtil.isValidString(sipUri)
				&& !DonkeyUtil.isValidString(phone)
				&& !DonkeyUtil.isValidString(callId)) {
			DonkeyResponse.BadRequest(response);
			return null;
		}

		CdrBean bean = new CdrBean();
		bean.setAppid(appId);
		bean.setConference(confId);
		bean.setSipuri(sipUri);
		bean.setPhone(phone);
		bean.setCallid(callId);
		bean.setCreated(System.currentTimeMillis());
		bean.setStarttime(0L);
		bean.setEndtime(0L);
		bean.setState(CallStatus.Calling.val());

		return bean;
	}

	public static CdrBean generateCdrBean(SipSession session) {
		String appId = (String) session.getApplicationSession().getAttribute(
				Constant.AppID);
		String confId = (String) session.getApplicationSession().getId();
		String sipUri = (String) session
				.getAttribute(ConferenceManager.USER_SIP_URI);
		// String[] phoneNum = DonkeyUtil.splitText(sipUri, SipUriPrefix,
		// SipUriPostfix);
		String phone = DonkeyUtil.getUserFromSipURI(sipUri);
		String callId = session.getCallId();

		if (!DonkeyUtil.isValidString(appId)
				&& !DonkeyUtil.isValidString(confId)
				&& !DonkeyUtil.isValidString(sipUri)
				&& !DonkeyUtil.isValidString(phone)
				&& !DonkeyUtil.isValidString(callId)) {
			DonkeyGearman.getInstance().submit(
					DonkeyGearmanMessage.DonkeyUserException(
							DonkeyGearmanMessage.user_exception,
							DonkeyGearmanMessage.conn_cdr, session,
							Constant.generateCdrBeanErrorMsg));
			return null;
		}

		CdrBean bean = new CdrBean();
		bean.setAppid(appId);
		bean.setConference(confId);
		bean.setSipuri(sipUri);
		bean.setPhone(phone);
		bean.setCallid(callId);
		bean.setCreated(System.currentTimeMillis());
		bean.setStarttime(0L);
		bean.setEndtime(0L);
		bean.setState(CallStatus.Calling.val());

		return bean;
	}

	public static CdrBean getUpdateCdrBean(SipSession session) {
		String appId = (String) session.getApplicationSession().getAttribute(
				Constant.AppID);
		String confId = (String) session.getApplicationSession().getId();
		String callId = session.getCallId();

		if (!DonkeyUtil.isValidString(appId)
				&& !DonkeyUtil.isValidString(confId)
				&& !DonkeyUtil.isValidString(callId)) {
			return null;
		}

		CdrBean bean = new CdrBean();
		bean.setAppid(appId);
		bean.setConference(confId);
		bean.setCallid(callId);

		return bean;
	}

}
