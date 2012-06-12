$(function() {
	// query cdr dialog operation
	$('#cdr-query-btn').click(function() {
		var _appid = $("#appid").val();
		var _querystarttime = $("#querystarttime").val();
		var _queryendtime = $("#queryendtime").val();

		if (_appid == null) {
			_appid = "";
		}
		if (_querystarttime == null) {
			_querystarttime = "";
		}
		if (_queryendtime == null) {
			_queryendtime = "";
		}

		$.ajax({
			type : "post",
			url : "cdr/list",
			dataType : "json",
			data : {
				appid : _appid,
				querystarttime : _querystarttime,
				queryendtime : _queryendtime
			},
			statusCode : {
				200 : function(jsonData) {
					CdrInfoList.cleanUI();
					CdrInfoList.renderUI(jsonData);
				},
				500 : function(jsonData) {
					alert("Internal Exception: " + jsonData.reason);
				}
			}
		});
	});
	
	// UI related
	CdrInfoList = {
		UI : $("#cdr_list_tbody")
	};

	CdrInfoList.createCdrInfoUI = function(appid, phonenumber,
			createdtime, starttime, endtime, state,duration) {
		var tr = $("#template_cdr .cdr_tr_tpl").clone();
		$(tr).find(".cdr_td_appid").html(appid);
		$(tr).find(".cdr_td_phonenumber").html(phonenumber);
		$(tr).find(".cdr_td_createdtime").html(createdtime);
		$(tr).find(".cdr_td_starttime").html(starttime);
		$(tr).find(".cdr_td_endtime").html(duration);
		$(tr).find(".cdr_td_state").html(getCallState(state));
		return tr;
	};

	CdrInfoList.cleanUI = function() {
		var lis = $("tr", CdrInfoList.UI);
		$(lis).each(function() {
			$(this).remove();
		});
	};

	// render all data
	CdrInfoList.renderUI = function(jsonData) {
		var cdrinfoArray = jsonData.list;
		var cdrinfoPager = jsonData.pager;
		if (cdrinfoArray.length <= 0)
			return;
		for ( var i = 0; i < cdrinfoArray.length; i++) {
			CdrInfoList.addOneCdrInfoUI(cdrinfoArray[i]);
		}

		if (cdrinfoPager.previous != "") {
			$("#cdr_list_previous_page").attr("disabled", false);
			$("#cdr_list_previous_page").attr("url", cdrinfoPager.previous);
		} else {
			$("#cdr_list_previous_page").attr("disabled", true);
		}
		if (cdrinfoPager.next != "") {
			$("#cdr_list_next_page").attr("disabled", false);
			$("#cdr_list_next_page").attr("url", cdrinfoPager.next);
		} else {
			$("#cdr_list_next_page").attr("disabled", true);
		}

		$("#list_offset").html(
				cdrinfoPager.offset + "/" + cdrinfoPager.pagenumber);
	};

	$("#cdr_list_previous_page").click(function() {
		var url = $("#cdr_list_previous_page").attr("url");
		if (url != "" || url != null) {
			$.post(
				url, 
				{ 
					vappid: $("#appid").val(),
					querystarttime : $("#querystarttime").val(),
					queryendtime : $("#queryendtime").val()
				},
				function(data) {
					CdrInfoList.cleanUI();
					CdrInfoList.renderUI(data);
				}, "json");
		}
		return false;
	});

	$("#cdr_list_next_page").click(function() {
		var url = $("#cdr_list_next_page").attr("url");
		if (url != "" || url != null) {
			$.post(
				url, 
				{ 
					vappid: $("#appid").val(),
					querystarttime : $("#querystarttime").val(),
					queryendtime : $("#queryendtime").val()
				},
				function(data) {
					CdrInfoList.cleanUI();
					CdrInfoList.renderUI(data);
				}, "json");
		}
		return false;
	});

	// add one cdr info UI
	CdrInfoList.addOneCdrInfoUI = function(cdrinfo) {
		var cdrinfoUI = CdrInfoList.createCdrInfoUI(cdrinfo.appid,
				cdrinfo.phonenumber, cdrinfo.createdtime,
				cdrinfo.starttime, cdrinfo.endtime, cdrinfo.state,cdrinfo.duration);
		CdrInfoList.UI.append(cdrinfoUI);
	};

	function getCallState(state) {
		switch (state) {
		case 0:
			return "Calling";
		case 1:
			return "Connected";
		case 2:
			return "Hangup";
		case 3:
			return "CallFailed";
		}
	}

});