$(document).ready(function() {
		initSelectTags("category", "organisation");
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

function initSelectTags(category, organisation)
{
	initCategorySelect(category);
	//initOrganisationSelect();
}

function initCategorySelect(id)
{
	$.ajax({
		url: "/getVM_Categories",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			VM_Categories = JSON.parse(data.responseText);
			$("#"+id).html("");
			VM_Categories.forEach(function(item){
				$("#"+id).append(
					"<option value = \"" + item.name + "\">" +
					item.name + 
					"</option>"
				);
			});
		}
	});
}

function validator(name, mode)
{
	ind = true;

	if(!validateStringInput(name, mode))
		ind =  false;

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

function listVMs()
{
	$.ajax({
		url: "/getVMs",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data){
			var discs = JSON.parse(data.responseText);
			$("#allVMs").html("");
			discs.forEach(function(item, indeks){
				$("#allVMs").append(
					"<tr>" +
					"<td>"+item.name+"</td>" +
					"<td>"+item.organisation+"</td>" +
					"<td>"+item.category+"</td>" +
					"<td><a onclick = \"initDeleteVM('"+item.name+"')\">Delete</a></td>" + 
					"<td><a onclick = \"initDetailsVM('" + item.name + "')\">Edit</a></td>" + 
					"</tr>"
				);
			});
		}
	});
}

function initDetailsVM(name)
{
	initSelectTags("categoryDetails", "organisationDetails");
	getVMDetails(name, findVM);
}

function getVMDetails(name, callback)
{
	$.ajax({
		url: "/getVMs",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			VMsReturned = JSON.parse(data.responseText);
			callback(name, findVM_Category);
		}
	});
}

function addVM()
{
	if(!validator("name", "Empty"))
		return;

	var $form = $("#addVM");
	var d = getFormData($form);
	var s = JSON.stringify(d);

	$.ajax({
		url: "/addVM",
		type: "POST",
		data: s,
		contentType: "application/json",
		dataType: "json",
		complete: function()
		{
			alert("VM added successfully");
			listVMs();
		}
	});
}

function removeVM(foundDisc)
{
	s = JSON.stringify(foundDisc);
	$.ajax({
		url: "/deleteVM",
		type: "DELETE",
		contentType: "application/json",
		dataType: "json",
		data: s,
		success: function()
		{
			alert("Disc deleted successfully");
			listVMs();
		}
	});
}

function findVM(name, callback)
{
	VMsReturned.forEach(function(item)
	{
		if(name === item.name)
		{
			foundVM = item;
			callback(item.category, 0);
		}		
	});
}

function findVM_Category(category, ind)
{
	$.ajax({
		url: "/getVM_Categories",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			VM_CategoriesReturned = JSON.parse(data.responseText);
			VM_CategoriesReturned.forEach(function(item){
				if(item.name == category)
				{
					if(ind == 0)
					{
						fillDetailsVM(foundVM);
					}
					fillDetailsVMC(item);
				}
					
			});
		}
	});
}

function initRefresh()
{
	var element = document.getElementById("categoryDetails");
	var selectedValue = element.options[element.selectedIndex].value;

	findVM_Category(selectedValue, 1);
}

function fillDetailsVMC(vmc)
{
	$("#VM_DetailsNoC").html(""+vmc.numberOfCores);
	$("#VM_DetailsRAM").html (""+vmc.gbOfRAM);
	$("#VM_DetailsGPU").html(""+vmc.numberOfGPUCores);
}

function fillDetailsVM(vm)
{
	document.getElementById("nameDetails").value = vm.name;
	$("#organisationDetails").val(""+vm.organisation);
	$("#categoryDetails").val(""+vm.category);
}

function initDeleteVM(name)
{
	getVMs(name, findVM);	
}

function getVMs(name, callback)
{
	$.ajax({
		url: "/getVM_Categories",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			VMsReturned = JSON.parse(data.responseText);
			callback(name, removeVM);
		}
	});
}