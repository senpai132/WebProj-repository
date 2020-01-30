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


function init() {
	$.ajax({
		url: "/rest/getUserType",
		type: "GET",
		complete: function(data) {
			d = JSON.parse(data.responseText);

			if(!d.superadmin) {
				window.location.replace("/");
			}
		}
	});
}

function addOrganization() {
	/*if(!validateEmail(document.getElementById("email").value))
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
	$("#password_error").html("");*/
	
	
	var fd = new FormData();
    var files = $('#logo')[0].files[0];
    fd.append('logo',files);
    console.log(fd);
    
    var s = JSON.stringify(data);
	$.ajax({
		url: "/rest/addOrganization",
		type:"POST",
		data: s,
		contentType:"application/json",
		dataType:"json",
		complete: function(data) {
			d = JSON.parse(data.responseText);

			/*if(!d.result)
			{
				$("#error_message").html(d.message);
			}
			else {
				window.location.replace("/");
			}*/
		}
	});
}
