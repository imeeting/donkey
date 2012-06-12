<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    	               "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/donkey/css/donkey.css?t=<%=System.currentTimeMillis()%>"
	rel="stylesheet" type="text/css" />
<title>Donkey</title>
</head>
<body>
	<div id="page-test" class="wrapper">
		<%@include file="template/nav.jsp"%>

		<h1>Hello Donkey!</h1>

		<button id="btn-create">Create Conference</button>
		<button id="btn-destroy">Destroy Conference</button>
		<button id="btn-playmusic">Play Music</button>
		<button id="btn-stopplay">Stop Play</button>
		<button id="btn-record">Record Conference</button>
		<button id="btn-stoprecord">Stop Record</button>
		<a id="btn-getrecordfile" href="#">Get Record File</a>

		<div>
			<input class="ipt-attendee" type="hidden" /> <input
				class="ipt-sipuri" type="text" value="13770662051" />
			<button class="btn-join">Join Conference</button>
			<button class="btn-unjoin">Unjoin Conference</button>
			<button class="btn-mute">Mute</button>
			<button class="btn-unmute">Unmute</button>
		</div>
		<div>
			<input class="ipt-attendee" type="hidden" /> <input
				class="ipt-sipuri" type="text" value="13382794516" />
			<button class="btn-join">Join Conference</button>
			<button class="btn-unjoin">Unjoin Conference</button>
			<button class="btn-mute">Mute</button>
			<button class="btn-unmute">Unmute</button>
		</div>
		<div>
			<input class="ipt-attendee" type="hidden" /> <input
				class="ipt-sipuri" type="text" value="13813005146" />
			<button class="btn-join">Join Conference</button>
			<button class="btn-unjoin">Unjoin Conference</button>
			<button class="btn-mute">Mute</button>
			<button class="btn-unmute">Unmute</button>
		</div>

	</div>
	<script src="/donkey/jquery/jquery.js"></script>
	<script src="/donkey/script/test.js?t=<%=System.currentTimeMillis()%>"></script>
</body>
</html>
