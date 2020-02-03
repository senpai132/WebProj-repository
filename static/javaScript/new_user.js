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
			if(!d.logged || d.role == "client") {
				window.location.replace("/");
			}
			else {
				if (d.superadmin) {
					$("#org_input").hide();
				}
				else {
					loadOrganizations();
				}
			}
		}
	});
}

function loadOrganizations() {
	$.ajax({
		url: "/rest/getOrganizations",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);
			
			if (d.result) {
				for(let org of d.orgs) {
					$("#organization").append(`<option value="${org.name}">${org.name}</option>`)
				}
			}
			else {
				window.location.replace("/");
			}
		}
	});
}

function addUser() {
	var error = false;
	$("#error_message").hide();
	
	if(!validateEmail(document.getElementById("email").value))
	{
		$("#email_error").html("Invalid email");
		$("#email_error").show();
		error = true;
	}
	else {
		$("#email_error").hide();
	}

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
	
	if (!error) {
		var data = getFormData($("#new_user_form"));
		
		var s = JSON.stringify(data);
		$.ajax({
			url: "/rest/addUser",
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
					window.location.replace("/html/users.html");
			}
		});
	}
}

function validateEmail(email) {
  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}