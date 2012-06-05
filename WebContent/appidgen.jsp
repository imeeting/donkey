<%@page import="com.ivyinfo.donkey.Constant"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String loginName = (String)session.getAttribute(Constant.LoginName);
	if (loginName == null) {
		response.sendRedirect("index.jsp");
	}
%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="jquery/jquery.ui.css" type="text/css" rel="stylesheet"></link>
<link href="css/donkey.css?t=<%=System.currentTimeMillis() %>" rel="stylesheet" type="text/css" />
<title>应用管理</title>
</head>
<body>
	<div id="page-appid" class="wrapper">
		<%@include file="template/nav.jsp" %>

		<div class="toolbar">
			<div class="left">
				<input id="appid-add-btn" class="button" type="button" value="添加"/>
				<input id="appid-del-btn" class="button" type="button" value="删除"/>
		    </div>
		    <div class="right">
	      		<input type="button" class="button" id="devinfo_list_previous_page"	value="&lt;&lt;"> 
				<span>Page</span> <span id="list_offset"></span>
				<input type="button" class="button" id="devinfo_list_next_page" value="&gt;&gt;">		    
		    </div>
	    </div>
	
		<!-- Development Info List -->
		<table cellspacing="0" cellpadding="0">
			<thead>
				<tr>
					<td width="10%">
						<input class="devinfo_td_operaction_checkbox" id="select_all_checkbox" type="checkbox" />
					</td>
					<td width="15%">Name</td>
					<td width="15%">AppID</td>
					<td width="15%">AppKey</td>
					<td width="35%">Callback URL</td>
					<td width="10%">Operation</td>
				</tr>
			</thead>
			<tbody id="devinfo_list_tbody">
			</tbody>
		</table>

		<div id="template_devinfo" class="hidden">
			<table>
				<tr class="devinfo_tr_tpl">
					<td class="devinfo_td_checkbox"><input
						class="devinfo_td_operaction_checkbox" name="dev_checkbox" type="checkbox" />
					</td>
					<td class="devinfo_td_id hidden"></td>
					<td class="devinfo_td_name"></td>
					<td class="devinfo_td_appid"></td>
					<td class="devinfo_td_appkey"></td>
					<td class="devinfo_td_callbackurl"></td>
					<td class="devinfo_td_edit"><button
						class="devinfo_td_operaction_edit" >Edit</button>
					</td>
				</tr>
			</table>
		</div>
		
		<div id="dialog-add-appid" title="添加应用" class="hidden">
		    <form id="form-add-appid">
		        <fieldset class="ui-helper-reset">
		            <label for="dev-name">Name</label> 
		            <br/>
		            <input type="text"
		                name="name" id="user_name" value="" 
		                class="ui-widget-content ui-corner-all" />
		            <br/>
		            <label for="callback-url">Call Back URL</label> 
		            <br/>
		            <input type="text"
		                name="callback" id="callback_url" value="" 
		                class="ui-widget-content ui-corner-all" /><br><br>
		                
		        </fieldset>
		    </form>
		</div>
	
		<div id="dialog-edit-devinfo" title="编辑应用" class="hidden">
		    <form id="form-edit-devinfo">
		        <fieldset class="ui-helper-reset">
		        	<label id="id_edit" class="hidden"></label>
		            <label for="dev-name">Name</label>
		            <br/> 
		            <label id="dev_name_edit"></label>
		            <br/>
		            <label for="callback-url">Call Back URL</label> 
		            <br/>
		            <input type="text"
		                name="callback" id="callback_url_edit" value="" 
		                class="ui-widget-content ui-corner-all" /><br><br>
		        </fieldset>
		    </form>
		</div>		
	</div>
	<script src="jquery/jquery.js"></script>
	<script src="jquery/jquery.ui.js"></script>
	<script src="script/appgen.js?t=<%=System.currentTimeMillis() %>"></script>
</body>
</html>