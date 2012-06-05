package com.ivyinfo.donkey.admin.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.db.cdr.CdrBean;
import com.ivyinfo.donkey.db.cdr.CdrManager;
import com.ivyinfo.util.CsvWriter;
import com.ivyinfo.util.DonkeyUtil;
import com.ivyinfo.util.Time;

public class CdrExportServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String [] callStatus = {"Calling", "Connected", "Hangup", "CallFailed"};

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String targetFile = "CDR.csv";
		
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition",
				"attachment; filename=\"" + targetFile + "\"");
		
		// 设置查询条件
		String appId = request.getParameter(Constant.AppID);
		String queryStartTime = request
				.getParameter(Constant.CdrQueryStartTime);
		String queryEndTime = request
				.getParameter(Constant.CdrQueryEndTime);
		
		CdrBean cdrBean = new CdrBean();
		cdrBean.setAppid(appId);
		// querycallstate 2 : query call state "hangup" and "callfailed"
		cdrBean.setQueryState(2);

		if (!DonkeyUtil.isValidString(queryStartTime)) {
			cdrBean.setQueryStartTime(-1L);
		} else {
			cdrBean.setQueryStartTime(Time.getDateFormatYMDHMSString(
					queryStartTime).getTime());
		}

		if (!DonkeyUtil.isValidString(queryEndTime)) {
			cdrBean.setQueryEndTime(Long.MAX_VALUE);
		} else {
			cdrBean.setQueryEndTime(Time.getDateFormatYMDHMSString(
					queryEndTime).getTime());
		}

		try {
			List<CdrBean> list = CdrManager.queryCdrs(
					cdrBean);
			
			OutputStream os = response.getOutputStream();
			CsvWriter writer = new CsvWriter(os, ',', Charset.forName("UTF-8"));
			writer.setForceQualifier(true);
			
			for (int i = 0; i < list.size(); i++) {
				CdrBean bean = list.get(i);
				writer.write(bean.getAppid());
				writer.write(bean.getPhone());
				writer.write(Time.getDateTime(new Date(bean.getCreated())));
				writer.write(Time.getDateTime(new Date(bean.getStarttime())));
				writer.write(Time.format(bean.getDuration()));
				writer.write(getStatename(bean.getState()));
				writer.endRecord();
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getStatename(int state){
		return callStatus[state];
	}

}
