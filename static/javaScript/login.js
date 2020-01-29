function getFormData($form){
		var unindexed_array = $form.serializeArray();
		var indexed_array = {};

		$.map(unindexed_array, function(n, i){
		indexed_array[n['name']] = n['value'];
		});

		return indexed_array;
}

function login() {
	if(!validateEmail(document.getElementById("email").value))
	{
		$("#email_error").html("<font color = \"red\">* Invalid email format</font>");
		return;
	}
	$("#email_error").html("");

	if(document.getElementById("password").value == "")
	{
		$("#password_error").html("<font color = \"red\">* This field is required</font>");
		return;
	}
	$("#password_error").html("");

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

function init() {
	$.ajax({
		url: "/rest/isLoggedIn",
		type: "GET",
		complete: function(data) {
			d = JSON.parse(data.responseText);

			if(d.result) {
				window.location.replace("/");
			}
		}
	});
}

function validateEmail(email) {
  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}