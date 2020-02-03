$(document).ready( function() {
	$("#error_message").hide();
	init();
});

function getFormData($form){
		var unindexed_array = $form.serializeArray();
		var indexed_array = {};

		$.map(unindexed_array, function(n, i){
		indexed_array[n['name']] = n['value'];
		});

		return indexed_array;
}

function login() {
	var error = false;
	$("#error_message").hide();
	
	if(!validateEmail(document.getElementById("email").value))
	{
		$("#email_error").show();
		$("#email_error").html("Email is invalid");
		error = true;
	}
	else {
		$("#email_error").hide();
	}

	if(document.getElementById("password").value == "")
	{
		$("#password_error").show();
		$("#password_error").html("Password can't be empty");
		error = true;
	}
	else {
		$("#password_error").hide();
	}

	if (!error) {
		var form = $("#login_form");
		var data = getFormData(form);

		var s = JSON.stringify(data);
		$.ajax({
			url: "/rest/login",
			type:"POST",
			data: s,
			contentType:"application/json",
			dataType:"json",
			complete: function(data) {
				d = JSON.parse(data.responseText);
				console.log(d);
				if(!d.result)
				{
					$("#error_message").show();
					$("#error_message").html(d.message);
				}
				else {
					window.location.replace("/");
				}
			}
		});
	}
}

function init() {
	$.ajax({
		url: "/rest/getUserType",
		type: "GET",
		complete: function(data) {
			d = JSON.parse(data.responseText);

			if(d.logged) {
				window.location.replace("/");
			}
		}
	});
}

function validateEmail(email) {
  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}