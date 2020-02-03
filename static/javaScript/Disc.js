$(document).ready(function() {
	initSelectTag("parentVMSelect");
	initSelectTag("parentVMSelectEdit");
});

function getFormData($form)
{
	var unindexed_array = $form.serializeArray();
	var indexed_array = {};

	$.map(unindexed_array, function(n, i){
	indexed_array[n['name']] = n['value'];
	});

	return indexed_array;
}

function indexSecurity()
{
	if(userType === "client")
	{
		$("#addDiscSecurity").hide();
		$("#organisations").hide();
		$("#users").hide();
		$("#categories").hide();
	}
	if(userType === "admin");
	{
		$("#organisations").hide();
		$("#users").hide();
		$("categories").hide();
	}
	if(userType === "superadmin")
	{
		$("#addDiscSecurity").show();
		$("#organisations").show();
		$("#users").show();
		$("#categories").show();
	}
}

function editSecurity()
{
	if(userType === "client")
	{
		$("#addVMSecurity").hide();
		$("#organisations").hide();
		$("#users").hide();
		$("#categories").hide();
		$("#nameEdit").attr("disabled", "disabled");
		$("#typeEdit").prop("disabled", "disabled");
		$("#capacityEdit").prop("disabled", "disabled");
		$("#parentVMSelectEdit").prop("disabled", "disabled");
	}
	if(userType === "admin");
	{
		$("#organisations").hide();
		$("#categories").hide();
		$("#users").hide();
		$("#organisationDetails").attr("disabled", "disabled");
		$("#categoryDetails").prop("readonly", false);
		$("#nameDetails").prop("readonly", false);
	}
	if(userType === "superadmin")
	{
		$("#addVMSecurity").show();
		$("#organisation").show();
		$("#users").show();
		$("#organisationDetails").prop("readonly", false);
		$("#categoryDetails").prop("readonly", false);
		$("#nameDetails").prop("readonly", false);
	}
}

function getUserType(callback)
{
	$.ajax({
		url: "/rest/getUserType",
		type:"GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			user = JSON.parse(data.responseText);
			userType = user.role;
			
			callback();
			listDiscs();
		}
	});
}

function initSelectTag(id)
{
	$.ajax({
		url: "/getVMs",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			if(data.responseText === "Unauthorized operation!")
				return;
			VMs = JSON.parse(data.responseText);
			$("#"+id).html("");
			$("#"+id).append("<option value = \"\"></option>");
			VMs.forEach(function(item){
				$("#"+id).append(
					"<option value = \"" + item.name + "\">" +
					item.name + 
					"</option>"
				);
			});
		}
	});
}

function addDisc()
{
	if(!validator("name", "capacity", "Empty"))
		return;

	var $form = $("#addDisc");
	var d = getFormData($form);
	var s = JSON.stringify(d);

	$.ajax({
		url: "/addDisc",
		type: "POST",
		data: s,
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			if(data.responseText === "Unauthorized operation!")
				return;
			alert("Disc added successfully");
		}
	});
}

function removeDisc(foundDisc)
{
	s = JSON.stringify(foundDisc);
	$.ajax({
		url: "/deleteDisc",
		type: "DELETE",
		contentType: "application/json",
		dataType: "json",
		data: s,
		success: function(data)
		{
			if(data.responseText === "Unauthorized operation!")
				return;
			alert("Disc deleted successfully");
			listDiscs();
		}
	});
}

function listDiscs()
{
	$.ajax({
		url: "/getDiscs",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data){
			if(data.responseText === "Unauthorized operation!")
				return;
			var discs = JSON.parse(data.responseText);
			$("#allDiscs").html("<thead class=\"thead-dark\">"+
					"<tr>"+
					"<th scope=\"col\">Name</th>"+
					"<th scope=\"col\">Type</th>"+
					"<th scope=\"col\">Capacity</th>"+
					"<th scope=\"col\">Parent VM</th>"+
					"<th scope=\"col\"></th>"+
					"<th scope=\"col\"></th>"+
				"</tr>"+
			"</thead>");
			discs.forEach(function(item, indeks){
				$("#allDiscs").append(
					"<tr>" +
					"<td>"+item.name+"</td>" +
					"<td>"+item.type+"</td>" +
					"<td>"+item.capacity+"</td>" +
					"<td>"+item.parentVM+"</td>" + (userType != "client" ?
					"<td><a onclick = \"deleteDisc('"+item.name+"')\">Delete</a></td>" : "") +
					"<td><a href = \"/goToEditDetailsDisc\" onclick = \"initDetailsDisc('" + item.name + "')\">Edit</a></td>" + 
					"</tr>"
				);
			});
		}
	});
}

function initDetailsDisc(name)
{
	data = [name];
	s = JSON.stringify(data);
	
	$.ajax(
	{
		url: "/setNameDetailsDisc",
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		data: s
	});	
}

function loadDetailsDisc()
{
	$.ajax(
	{
		url: "/getDetailsDisc",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			disc = JSON.parse(data.responseText);
			
			initEdit(disc.name);
		}
	});
	
}

function initEdit(name)
{
	//initSelectTag("parentVMSelectEdit");
	getEditDiscs(name, findDiscForEdit);
}

function getEditDiscs(name, callback)
{
	$.ajax({
		url: "/getDiscs",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			discsReturned = JSON.parse(data.responseText);
			callback(name, setEditFormValues);
		}
	});
}

function setEditFormValues(vmc)
{
	document.getElementById("nameEdit").value = vmc.name;
	$("#typeEdit").val(""+vmc.type);
	document.getElementById("capacityEdit").value = vmc.capacity;
	$("#parentVMSelectEdit").val(vmc.parentVM);	
}

function validator(name, capacity, mode)
{
	ind = true;

	if(!validateStringInput(name, mode))
		ind =  false;
	if(!validateIntInput(capacity, mode))
		ind = false;

	return ind;
}

function validateStringInput(id, mode)
{
	$("#"+id+mode).html("");
	$("#"+id+mode).hide();

	if(document.getElementById(id+"").value === "")
	{
		$("#"+id+mode).html("<font color=\"red\">This field is required</font>");
		$("#"+id+mode).show();
		return false;
	}
	return true;
}

function validateIntInput(id, mode)
{
	$("#"+id+mode).html("");
	$("#"+id+mode).hide();

	if(document.getElementById(id+"").value >>> 0 === parseFloat(document.getElementById(id+"").value) == false)
	{
		$("#"+id+mode).html("<font color=\"red\">This field must be a positive integer</font>");
		$("#"+id+mode).show();
		return false;
	}
	return true;
}

function editDisc()
{
	if(!validator("nameEdit", "capacityEdit", "EmptyEdit"))
		return;

	var $form = $("#DiscEdit");
	var data = getFormData($form);
	discPair = [discToEdit, data];
	s = JSON.stringify(discPair);

	$.ajax({
		url: "/editDisc",
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		data: s,
		success: function(data)
		{
			if(data.responseText === "Unauthorized operation!")
				return;
			alert("Disc edited successfully");
		}
	});
}

function findDiscForEdit(name, callback)
{
	discsReturned.forEach(function(item)
	{
		if(name === item.name)
		{
			discToEdit = item;
			callback(item);
		}			
	});
}

function findDisc(name, callback)
{
	discsReturned.forEach(function(item)
	{
		if(name === item.name)
		{
			callback(item);
		}		
	});
}

function deleteDisc(name)
{
	getDiscs(name, findDisc);	
}

function getDiscs(name, callback)
{
	$.ajax({
		url: "/getDiscs",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			if(data.responseText === "Unauthorized operation!")
				return;
			discsReturned = JSON.parse(data.responseText);
			callback(name, removeDisc);
		}
	});
}