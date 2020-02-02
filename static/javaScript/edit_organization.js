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

			if(!d.logged || d.role == "client") {
				window.location.replace("/");
			}
			else {
				loadOrganization();
			}
		}
	});
}

function loadOrganization() {
	$.ajax({
		url: "/rest/getEditOrganization",
		type: "GET",
		complete: function(data) {
			d = JSON.parse(data.responseText);
			console.log(d);
			if(d.result) {
				
				$("#name").val(d.org.name);
				$("#description").val(d.org.description);
				$("#imgLogo").attr("src",d.org.logo);
			}
			else {
				$("#error_message").html = "You do not have right to edit this organization";
			}
		}
	});
}

function editOrganization() {
	var error = false;
	
	if(document.getElementById("name").value == "")
	{
		$("#name_error").html("Name is required");
		error = true;
	}
	else {
		$("#name_error").html("");
	}

	if(document.getElementById("description").value == "")
	{
		$("#description_error").html("Description is required");
		error = true;
	}
	else {
		$("#description_error").html("");
	}
	
	if (!error) {
		var formData = getFormData($("#edit_organization_form"));

	    getImgBytes(function(jsonData) {
	        $.ajax({
	            url: "/rest/editOrganization",
	            type: "POST",
	            data: jsonData,
	            contentType: "application/json",
	            dataType: "json",
	            complete: function(data) {
	            	d = JSON.parse(data.responseText);

	            	if (d.result) {
	            		window.location.replace("/html/organizations.html");
	            	}
	            	else {
	            		$("#error_message").html(d.message);
	            	}
	            }
	        });
	    }, formData);
	}
}

function getImgBytes(callback, data) {
    logoImg = $("#logo")[0];

    if(logoImg.files.length != 0) {
        if(logoImg.files[0].type.includes("image") && (logoImg.files[0].size / 1048576.0) < 1.0) {
            var reader = new FileReader();
            reader.onload = function(evt) {
                data.logo = evt.target.result;
                callback(JSON.stringify(data));
            };

            reader.readAsBinaryString(logoImg.files[0]);
            return;
        } else {
            var error = $("#image_error");
            error.text("Please select image that is lower then 1MB");
            error.show();
            return;
        }
    }

    callback(JSON.stringify(data));
}