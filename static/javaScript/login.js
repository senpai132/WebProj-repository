$(document).ready( function() {
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
	
	if(!validateEmail(document.getElementById("email").value))
	{
		$("#email_error").html("<font color = \"red\">* Invalid email format</font>");
		error = true;
	}
	else {
		$("#email_error").html("");
	}

	if(document.getElementById("password").value == "")
	{
		$("#password_error").html("<font color = \"red\">* This field is required</font>");
		error = true;
	}
	else {
		$("#password_error").html("");
	}

	if (!error) {
		var $form = $("#login_form");
		var data = getFormData($form);

		var s = JSON.stringify(data);
		$.ajax({
			url: "/rest/login",
			type:"POST",
			data: s,
			contentType:"application/json",
			dataType:"json",
			complete: function(data) {
				d = JSON.parse(data.responseText);

				if(!d.result)
				{
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