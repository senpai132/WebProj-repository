function getFormData($form)
{
	var unindexed_array = $form.serializeArray();
	var indexed_array = {};

	$.map(unindexed_array, function(n, i){
	indexed_array[n['name']] = n['value'];
	});

	return indexed_array;
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
		complete: function()
		{
			alert("Disc added successfully");
			listDiscs();
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
		success: function()
		{
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
			var discs = JSON.parse(data.responseText);
			$("#allVM_Categories").html("");
			discs.forEach(function(item, indeks){
				$("#allVM_Categories").append(
					"<tr>" +
					"<td>"+item.name+"</td>" +
					"<td>"+item.type+"</td>" +
					"<td>"+item.capacity+"</td>" +
					"<td>"+item.parentVM+"</td>" + 
					"<td><a onclick = \"deleteDisc('"+item.name+"')\">Delete</a></td>" + 
					"<td><a onclick = \"initEdit('" + item.name + "')\">Edit</a></td>" + 
					"</tr>"
				);
			});
		}
	});
}

function initEdit(name)
{
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
	document.getElementById("parentVM_Edit").value = vmc.parentVM;	
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

	if(document.getElementById(id+"").value === "")
	{
		$("#"+id+mode).html("<font color=\"red\">This field is required</font>");
		return false;
	}
	return true;
}

function validateIntInput(id, mode)
{
	$("#"+id+mode).html("");

	if(document.getElementById(id+"").value >>> 0 === parseFloat(document.getElementById(id+"").value) == false)
	{
		$("#"+id+mode).html("<font color=\"red\">This field must be a positive integer</font>");
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
		success: function()
		{
			alert("Disc edited successfully");
			listDiscs();
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
			discsReturned = JSON.parse(data.responseText);
			callback(name, removeDisc);
		}
	});
}