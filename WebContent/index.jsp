<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="jquery/jquery.ui.css" type="text/css" rel="stylesheet"></link>
<link href="css/donkey.css?t=<%=System.currentTimeMillis() %>" rel="stylesheet" type="text/css" />
<title>Administrator Page</title>
</head>
<body>
	<form class="login" id="login" accept-charset="UTF-8" method="post">
		<div>
			<label>Name</label>
			<br/>
			<input id="loginname" name="login_name" type="text" size="20" />
		</div>
		<div>			
			<label>Password</label>
			<br/>
			<input id="loginpwd" name="login_pwd" type="password" size="20" />
		</div>			
		<div>
			<input class="button" value="Login" name="LoginSubmit" type="submit" />
		</div>
	</form>
	<script src="jquery/jquery.js"></script>
	<script src="script/main.js?t=<%=System.currentTimeMillis() %>"></script>
</body>
</html>