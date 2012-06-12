<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/donkey/jquery/jquery.ui.css" type="text/css" rel="stylesheet"></link>
<link href="/donkey/css/donkey.css?t=<%=System.currentTimeMillis()%>"
	rel="stylesheet" type="text/css" />
<link href="/donkey/DatePicker/skin/WdatePicker.css" type="text/css" rel="stylesheet"></link>
<title>话单查询</title>
</head>
<body>
	<div id="page-cdr" class="wrapper">
		<%@include file="template/nav.jsp" %>
		<div class="toolbar">
			<div class="left">
				<label for="cdr-appid">AppID</label> 
				<input type="text" name="appid"	id="appid" value="" />
				<label for="cdr-querystarttime">开始时间</label> 
				<input type="text" name="querystarttime" id="querystarttime" value=""
					onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" />
				<label for="cdr-queryendtime">终止时间</label>
				<input type="text" name="queryendtime" id="queryendtime" value=""
					onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" />
				<input class="button" id="cdr-query-btn" type="button" value="查询" />
				<!--  
				<input class="button" id="cdr-export-btn" type="button" value="导出" />
				-->
				<a href="cdrExportServlet">导出</a>
			</div>
			<div class="right">
				<input type="button" class="button" id="cdr_list_previous_page"	value="&lt;&lt;"> 
				<span>Page</span><span id="list_offset"></span> 
				<input type="button" class="button" id="cdr_list_next_page" value="&gt;&gt;">	
			</div>		
		</div>

		<!-- Cdr List -->
		<table cellspacing="0" cellpadding="0">
			<thead>
				<tr>
					<td width="10%">AppID</td>
					<td width="14%">PhoneNumber</td>
					<td width="22%">CreatedTime</td>
					<td width="22%">StartTime</td>
					<td width="22%">Duration</td>
					<td width="10%">State</td>
				</tr>
			</thead>
			<tbody id="cdr_list_tbody">
			</tbody>
		</table>

		<div id="template_cdr" class="hidden">
			<table>
				<tr class="cdr_tr_tpl">
					<td class="cdr_td_appid"></td>
					<td class="cdr_td_phonenumber"></td>
					<td class="cdr_td_createdtime"></td>
					<td class="cdr_td_starttime"></td>
					<td class="cdr_td_endtime"></td>
					<td class="cdr_td_state"></td>
				</tr>
			</table>
		</div>
	</div>
	<script src="/donkey/jquery/jquery.js"></script>
	<script src="/donkey/jquery/jquery.ui.js"></script>
	<script src="/donkey/DatePicker/WdatePicker.js" type="text/javascript"></script>
	<script src="/donkey/script/cdr.js?t=<%=System.currentTimeMillis()%>"></script>
</body>
</html>