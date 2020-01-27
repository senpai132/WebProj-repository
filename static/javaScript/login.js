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
		$("#mailError").html("<font color = \"red\">* Invalid email format</font>");
		return;
	}
	$("#mailError").html("");

	if(checkPassword())
	{
		$("#passError").html("<font color = \"red\">* This field is required</font>");
		return;
	}
	$("#passError").html("");

	var $form = $("#login");
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
			if(!d.message)
			{
				$("#invalidLogIn").html("Invalid email or password");
			}
			else 
				window.location.replace("/");
		}
	});
}

function checkPassword() {
	return document.getElementById("password").value === "";
}

function validateEmail(email) {
  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}