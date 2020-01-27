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
}
	
function isLoggedOut() {
	$.ajax({
		url: "rest/isLoggedIn",
		type: "GET",
		complete: function(data) {
			d = JSON.parse(data.responseText);
			if(!d.loggedIn) {
				window.location.replace("/html/login.html");
			}
			else {
				//TODO ovde ide dalje ucitavanje
			}
		}
	});
}
