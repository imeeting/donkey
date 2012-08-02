<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Set, java.util.Map" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/donkey/jquery/jquery.ui.css" type="text/css" rel="stylesheet"></link>
<link href="/donkey/css/donkey.css?t=<%=System.currentTimeMillis()%>"
	rel="stylesheet" type="text/css" />
<title>系统状态</title>
</head>
<% Set<String> confIdSet = (Set<String>)request.getAttribute("confIdSet"); %>
<% Map<String, Set<String>> map = (Map<String, Set<String>>)request.getAttribute("sipUriConfMap"); %>
<body>
	<div id="page-sys" class="wrapper">
		<%@include file="template/nav.jsp" %>
		<h1>当前共有<%=confIdSet.size()%>个会议正在进行</h1>
		<% for(String s : confIdSet) { %>
		<h2><a href="/donkey/sys/conf/<%=s%>"><%=s%></a></h2>
		<% } %>
		<hr>
		<h1>所有参会者及其相应的会议</h1>
		<% for(Map.Entry<String, Set<String>> e : map.entrySet()){ %>
		<h2><%=e.getKey()%></h2>
		<p>
			<% for(String s : e.getValue()) { %>
			<span><%=s%>, </span>
			<% } %>
		</p>
		<% } %>
	</div>
	<script src="/donkey/jquery/jquery.js"></script>
	<script src="/donkey/jquery/jquery.ui.js"></script>
</body>
</html>