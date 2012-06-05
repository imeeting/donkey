$(function() {
	
	
	$("#login").submit(
			function() {
				var loginName = $("#loginname").val();
				if (loginName == "") {
					alert("Please input name");
					return false;
				}
				var pwd = $("#loginpwd").val();
				if (pwd == "" ) {
					alert("please input password");
					return false;
				}
				var postURL = "adminlogin?" + $("#loginname").attr("name") + "="
				+ $("#loginname").val() + "&"
				+ $("#loginpwd").attr("name") + "="
				+ $("#loginpwd").val();
				
				$.post(postURL, function(data) {
					var obj = eval(data);
					var result = obj.result;
					switch (result) {
					case "ok":
						location.href = "appidgen.jsp";
						break;
					case "login_fail":
						alert("Wrong user name or password!");
						$("#loginpwd").val("");
						break;
					}

				}, "json");
				return false;
			});
});