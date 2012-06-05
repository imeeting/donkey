package com.ivyinfo.donkey.sip;

import javax.servlet.sip.SipServletResponse;

public interface ISIPResponseHandler {

	public static final String RESPONSE_HANDLER = "_HANDLER";

	public void doProvisionalResponse(SipServletResponse response);

	public void doBranchResponse(SipServletResponse response);

	public void doRedirectResponse(SipServletResponse response);

	public void doSuccessResponse(SipServletResponse response);

	public void doErrorResponse(SipServletResponse response);

}
