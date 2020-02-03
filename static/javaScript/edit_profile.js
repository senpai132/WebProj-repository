$(document).ready( function() {
	init();
	$("#error_message").hide();
});

function getFormData($form){
		var unindexed_array = $form.serializeArray();
		var indexed_array = {};

		$.map(unindexed_array, function(n, i){
		indexed_array[n['name']] = n['value'];
		});

		return indexed_array;
}

function init() {
	$.ajax({
		url: "/rest/getUserType",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);
			if(!d.logged) {
				window.location.replace("/");
			}
			else {
				loadUser();
			}
		}
	});
}

function loadUser() {
	$.ajax({
		url: "/rest/getLoggedUser",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);
			
			if (d.result) {
				$("#email").val(d.user.email);
				$("#password").val(d.user.password);
				$("#name").val(d.user.name);
				$("#lastName").val(d.user.lastName);
				$("#organization").val(d.user.organization);
				$("#role").val(d.user.role);
			}
			else {
				window.location.replace("/");
			}
		}
	});
}

function editProfile() {
	var error = false;
	$("#error_message").hide();

	if(document.getElementById("password").value === "")
	{
		$("#password_error").html("Password is required");
		$("#password_error").show();
		error = true;
	}
	else {
		$("#password_error").hide();
	}
	
	if(document.getElementById("name").value === "")
	{
		$("#name_error").html("Name is required");
		$("#name_error").show();
		error = true;
	}
	else {
		$("#name_error").hide();
	}
	
	if(document.getElementById("lastName").value === "")
	{
		$("#lastName_error").html("Last name is required");
		$("#lastName_error").show();
		error = true;
	}
	else {
		$("#lastName_error").hide();
	}
	
	if(document.getElementById("password").value.localeCompare(document.getElementById("password_repeat").value) != 0)
	{
		$("#password_error").html("Password does not match");
		$("#password_error").show();
		$("#repeat_password_error").html("Password does not match");
		$("#repeat_password_error").show();
		error = true;
	}
	else {
		$("#password_error").hide();
		$("#repeat_password_error").hide();
	}
	
	if (!error) {
		var data = getFormData($("#edit_profile_form"));
		
		var s = JSON.stringify(data);
		$.ajax({
			url: "/rest/editProfile",
			type:"POST",
			data: s,
			contentType:"application/json",
			dataType:"json",
			complete: function(data) {
				d = JSON.parse(data.responseText);
				if(!d.result)
				{
					$("#error_message").html(d.message);
					$("#error_message").show();
				}
				else 
					window.location.replace("/");
			}
		});
	}
}

function validateEmail(email) {
  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}