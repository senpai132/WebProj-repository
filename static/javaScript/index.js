$(document).ready(initApp)

function getFormData($form){
		var unindexed_array = $form.serializeArray();
		var indexed_array = {};

		$.map(unindexed_array, function(n, i){
		indexed_array[n['name']] = n['value'];
		});

		return indexed_array;
}

function initApp()
{
	$.ajax({
		url: "/initApp",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data){

		}
	});
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
		url: "/login",
		type:"POST",
		data: s,
		contentType:"application/json",
		dataType:"json",
		complete: function(data) {
			if(data.responseText.startsWith("Invalid"))
			{
				$("#invalidLogIn").html(data.responseText);
			}
			else 
				window.location.assign("html\\KorisnikFrontPage.html");
		}
	});
}

function checkPassword()
{
	return document.getElementById("password").value === "";
}

function validateEmail(email) {
  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}