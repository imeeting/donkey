/**
 * 
 */

$(function() {

	var confSession;

	$("#btn-create").click(function() {
		$.ajax("api", {
			data : {
				m : "create",
				appid : "100",
				reqid : "001",
				sig : "XXXX"
			},
			dataType : "json",
			statusCode : {
				202 : function(data) {
					confSession = data.conference;
					alert(confSession);
				}
			}
		});
	});

	$("#btn-destroy").click(function() {
		$.ajax("api", {
			data : {
				m : "destroy",
				appid : "100",
				reqid : "001",
				sig : "XXXX",
				conference : confSession
			},
			statusCode : {
				202 : function() {
					// alert("Accepted");
				}
			}
		});
	});

	$("#btn-playmusic").click(function() {
		var musicName = "welcome1";
		fclick({
			m : "announce",
			appid : "100",
			reqid : "001",
			sig : "XXXX",
			conference : confSession,
			audioidentifier : musicName
		});
	});

	$("#btn-stopplay").click(function() {
		fclick({
			m : "stopannounce",
			appid : "100",
			reqid : "001",
			sig : "XXXX",
			conference : confSession
		});
	});

	$("#btn-record").click(function() {
		var maxTime = "60";
		fclick({
			m : "record",
			appid : "100",
			reqid : "001",
			sig : "XXXX",
			conference : confSession,
			maxtime : maxTime
		});
		$.ajax("RecordConfSubscribe", {
			dataType : "json",
			data : {
				conference : confSession
			},
			statusCode : {
				200 : function(data) {
					$("#btn-getrecordfile").attr("href", data.recordurl);
				}
			}
		});
	});

	$("#btn-stoprecord").click(function() {
		fclick({
			m : "stoprecord",
			appid : "100",
			reqid : "001",
			sig : "XXXX",
			conference : confSession
		});
	});

	$(".btn-join").click(function() {
		var attendeeInput = $(this).parent().find(".ipt-attendee");
		var sipInput = $(this).parent().find(".ipt-sipuri");
		var sipUri = "sip:0" + $(sipInput).val() + "@donkey.com";
		fclick({
			m : "join",
			appid : "100",
			reqid : "001",
			sig : "XXXX",
			conference : confSession,
			sipuri : sipUri
		}, function(data) {
			alert(data.attendee);
			$(attendeeInput).val(data.attendee);
		});
	});

	$(".btn-unjoin").click(function() {
		var attendeeVal = $(this).parent().find(".ipt-attendee").val();
		fclick({
			m : "unjoin",
			appid : "100",
			reqid : "001",
			sig : "XXXX",
			conference : confSession,
			attendee : attendeeVal
		});
	});

	$(".btn-mute").click(function() {
		var attendeeVal = $(this).parent().find(".ipt-attendee").val();
		fclick({
			m : "mute",
			appid : "100",
			reqid : "001",
			sig : "XXXX",
			conference : confSession,
			attendee : attendeeVal
		});
	});

	$(".btn-unmute").click(function() {
		var attendeeVal = $(this).parent().find(".ipt-attendee").val();
		fclick({
			m : "unmute",
			appid : "100",
			reqid : "001",
			sig : "XXXX",
			conference : confSession,
			attendee : attendeeVal
		});
	});

	function fclick(postData, fn) {
		$.ajax("api", {
			data : postData,
			dataType : "json",
			statusCode : {
				202 : function(data) {
					if (fn != undefined) {
						fn(data);
					}
				}
			}
		});
	}
});
