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

			for(let org of d.orgs) {
				$("#role").append(`<option value="${org}">${org}</option>`)
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