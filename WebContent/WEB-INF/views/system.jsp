<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Set" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/donkey/jquery/jquery.ui.css" type="text/css" rel="stylesheet"></link>
<link href="/donkey/css/donkey.css?t=<%=System.currentTimeMillis()%>"
	rel="stylesheet" type="text/css" />
<link href="/donkey/DatePicker/skin/WdatePicker.css" type="text/css" rel="stylesheet"></link>
<title>系统状态</title>
</head>
<% Set<String> confIdSet = (Set<String>)request.getAttribute("confIdSet"); %>
<body>
	<div id="page-sys" class="wrapper">
		<%@include file="template/nav.jsp" %>
	</div>
	<% for(String s : confIdSet) { %>
	<p><a href=#><%=s%></a></p>
	<% } %>
	<script src="/donkey/jquery/jquery.js"></script>
	<script src="/donkey/jquery/jquery.ui.js"></script>
</body>
</html>