function init() {
	$.ajax({
		url: "/rest/isAdmin",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);
			if(!d.message) {
				window.location.replace("/");
			}
			else {
				if (!d.super)
					$("#org_input").hide();
			}
		}
	});
}

function addUser() {
	if(!validateEmail(document.getElementById("email").value))
	{
		$("#mailError").html("<font color = \"red\">* Invalid email format</font>");
		return;
	}
	$("#mailError").html("");

	if(document.getElementById("password").value === "")
	{
		$("#passError").html("<font color = \"red\">* This field is required</font>");
		return;
	}
	$("#passError").html("");
	
	if(document.getElementById("name").value === "")
	{
		$("#nameError").html("<font color = \"red\">* This field is required</font>");
		return;
	}
	$("#nameError").html("");
	
	if(document.getElementById("lastName").value === "")
	{
		$("#lastNameError").html("<font color = \"red\">* This field is required</font>");
		return;
	}
	$("#lastNameError").html("");
	
	$.ajax({
		url: "/rest/newUser",
		type:"POST",
		data: s,
		contentType:"application/json",
		dataType:"json",
		complete: function(data) {
			d = JSON.parse(data.responseText);
			if(!d.result)
			{
				$("#invalidLogIn").html(d.message);
			}
			else 
				window.location.replace("/html/users.html");
		}
	});
}

function validateEmail(email) {
  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}