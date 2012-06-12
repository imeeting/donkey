$(function() {
	// run on page load
	$(function() {
		// load all dev info
		refreshAllDevInfo();
	});
	
	// refresh all dev info
	function refreshAllDevInfo() {
		$.ajax({
			type : "post",
			url : "/donkey/app/list",
			dataType : "json",
			statusCode : {
				200 : function(data) {
					DevInfoList.renderUI(data);
					
				},
				500 : function(data) {
					alert("Internal Exception: " + data.reason);
				}
			}
		});
	}
	
	// add appid dialog operation
	$('#appid-add-btn').click(function() {
		$addDevInfoDialog.dialog("open");
	});

	// delete selected devinfo
	$("#appid-del-btn").click(function() {
		if ($("input:checkbox[name='dev_checkbox'][checked]").length > 0) {
			if (confirm("Delete Selected Developer Info?")) {
				var selectedIDs = "";
				$("input:checkbox[name='dev_checkbox'][checked]").each(
								function() {
									var tr = $(this).parent().parent();
									var _appid = tr.find(".devinfo_td_appid").html();
									selectedIDs = _appid + "," + selectedIDs;
								});

				$.ajax({
					type : "post",
					url : "/donkey/app/delete",
					dataType : "json",
					data : {
						ids : selectedIDs
					},
					statusCode : {
						200 : function(data) {
							DevInfoList.cleanUI();
							refreshAllDevInfo();
						},
						500 : function(data) {
							alert("Internal Exception: "
									+ data.reason);
						}
					}
				});
			}
		} else {
			alert("No item selected!");
		}
	});
	
	// select and deselect all checkboxs
	$("#select_all_checkbox").click(function() {
		var selected = $("#select_all_checkbox").attr("checked");
		setAllCheckbox(selected);
	});
	
	function setAllCheckbox(selected) {
		$("input:checkbox[name='dev_checkbox']").each(function() {
			if (!$(this).attr("disabled")) {
				$(this).attr("checked", selected);
			} else {
				$(this).attr("checked", false);
			}
		});
	}
	
	var $addDevInfoDialog = $("#dialog-add-appid").dialog({
		autoOpen : false,
		modal : true,
		resizable : false,
		open : function() {
			$addDevInfoForm[0].reset();
		},
		buttons : {
			'Save' : function() {
				addDevInfoSave();
			},

			'Cancel' : function() {
				$addDevInfoDialog.dialog("close");
			}
		}
	});

	var $addDevInfoForm = $("form", $addDevInfoDialog);

	/**
	 * add development info
	 */
	function addDevInfoSave() {
		var _name = $("#user_name").val();
		var _callbackurl = $("#callback_url").val();
		if (_name == null || _name == "") {
			alert("Plese input Name!");
			return false;
		}
		if (_callbackurl == null || _callbackurl == "") {
			alert("Plese input Callback URL!");
			return false;
		}
		$.ajax({
					type : "post",
					url : "/donkey/app/register",
					dataType : "json",
					data : {
						name : _name,
						callbackurl : _callbackurl
					},
					statusCode : {
						200 : function() {
							$addDevInfoDialog.dialog("close");
							//DevInfoList.addDevInfo(devinfo);
							DevInfoList.cleanUI();
							refreshAllDevInfo();
						},
						409 : function(data) {
							
							alert("User name exists! Try another!");
						},
						500 : function(data) {
							$addDevInfoDialog.dialog("close");
							alert("Internal Exception: " + data.reason);
						}
					}
				});

	}
	
	var $editDevInfoDialog = $("#dialog-edit-devinfo").dialog({
		autoOpen : false,
		modal : true,
		resizable : false,
		buttons : {
			'Save' : function() {
				editDevInfoSave();
			},

			'Cancel' : function() {
				$editDevInfoDialog.dialog("close");
			}
		}
	});
	
	 function editDevInfoSave() {
			var _id = $("#id_edit").html();
			var _callbackurl = $("#callback_url_edit").val();
			
			if (_callbackurl == null || _callbackurl == "") {
				alert("Plese input Callback URL!");
				return false;
			}
			
			$.ajax({
				type : "post",
				url : "/donkey/app/edit",
				dataType : "json",
				data : {
					m  : "devinfo_edit",
					id : _id,
					callbackurl : _callbackurl
				},
				statusCode : {
					200 : function() {
						$editDevInfoDialog.dialog("close");
						DevInfoList.cleanUI();
						refreshAllDevInfo();
					},
					500 : function(data) {
						$editDevInfoDialog.dialog("close");
						alert("Internal Exception: " + data.reason);
					}
				}
			});
		}
	
	 $(".devinfo_td_operaction_edit").live("click", function(){
		 var tr = $(this).parent().parent();
		 var _id = tr.find(".devinfo_td_id").html();
		 var _name = tr.find(".devinfo_td_name").html();
		 var _callbackurl = tr.find(".devinfo_td_callbackurl").html();
		 
		 
		 $editDevInfoDialog.bind("dialogopen", function() {
				$("#id_edit").html(_id);
				$("#dev_name_edit").html(_name);
				$("#callback_url_edit").attr("value", _callbackurl); 
		 });
		 
		 $editDevInfoDialog.dialog("open");

	 });
	
	
	
	// UI related 
	 DevInfoList = {
		        UI : $("#devinfo_list_tbody")
	 };
	 
	 DevInfoList.createDevInfoUI = function(id, name, appid, appkey, callbackurl){
	        var tr = $("#template_devinfo .devinfo_tr_tpl").clone();
	        if (appid.length == 3) {
	        	var checkbox = $(tr).find(".devinfo_td_checkbox").find(".devinfo_td_operaction_checkbox");
	        	checkbox.attr("disabled", true);
	        	checkbox.attr("checked", false);
	        }
	        $(tr).find(".devinfo_td_id").html(id);
	        $(tr).find(".devinfo_td_name").html(name);
	        $(tr).find(".devinfo_td_appid").html(appid);
	        $(tr).find(".devinfo_td_appkey").html(appkey);
	        $(tr).find(".devinfo_td_callbackurl").html(callbackurl);
	        return tr;
	 };
	    
	 DevInfoList.cleanUI = function(){
	        var lis = $("tr", DevInfoList.UI);
	        $(lis).each(function(){
	            $(this).remove();
	        });
	 };	 
	 
	 // render all data
	 DevInfoList.renderUI = function(jsonData){
	        var devinfoArray = jsonData.list;
	        var devinfoPager = jsonData.pager;
	        if (devinfoArray.length <= 0) return;
	        for(var i = 0; i < devinfoArray.length; i++){
	        	DevInfoList.addDevInfo(devinfoArray[i]);
	        }
	        
	        if(devinfoPager.previous != ""){
	            $("#devinfo_list_previous_page").attr("disabled",false);
	            $("#devinfo_list_previous_page").attr("url",devinfoPager.previous);
	        }else{
	            $("#devinfo_list_previous_page").attr("disabled",true);
	        }
	        if(devinfoPager.next != ""){
	            $("#devinfo_list_next_page").attr("disabled",false);
	            $("#devinfo_list_next_page").attr("url",devinfoPager.next);
	        }else{
	            $("#devinfo_list_next_page").attr("disabled",true);
	        }
	        
	        $("#list_offset").html(devinfoPager.offset + "/" + devinfoPager.pagenumber);
	 };
	 
	 $("#devinfo_list_previous_page").click(function(){
		 	// clear all checkbox status
		 	$("#select_all_checkbox").attr("checked", false);
		 	setAllCheckbox(false); 
		 	
	        var url = $("#devinfo_list_previous_page").attr("url");
	        if(url != "" || url != null){
	            $.post(url,
	                    function(data){
	            			DevInfoList.cleanUI();
	            			DevInfoList.renderUI(data);
	                    }, 
	                    "json");
	        }
	        return false;
	    });
	    
	 $("#devinfo_list_next_page").click(function(){
		 	// clear all checkbox status
		 	$("#select_all_checkbox").attr("checked", false);
		 	setAllCheckbox(false); 
	        var url = $("#devinfo_list_next_page").attr("url");
	        if(url != "" || url != null){
	            $.post(url,
	                    function(data){
	            			DevInfoList.cleanUI();
	            			DevInfoList.renderUI(data);
	                    }, 
	                    "json");
	        }
	        return false;
	 });
	 
	 // add one dev info UI
	 DevInfoList.addDevInfo = function(devinfo) {
		 var devinfoUI = DevInfoList.createDevInfoUI( 
				 devinfo.id, devinfo.name, devinfo.appid,
				 devinfo.appkey, devinfo.callbackurl
                );
		 DevInfoList.UI.append(devinfoUI);
	 };
	  
});