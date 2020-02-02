function getFormData($form)
{
	var unindexed_array = $form.serializeArray();
	var indexed_array = {};

	$.map(unindexed_array, function(n, i){
	indexed_array[n['name']] = n['value'];
	});

	return indexed_array;
}

function addVM_Category()
{
	if(!validator("name", "numberOfCores", "gbOfRAM", "numberOfGPUCores", "Empty"))
		return;

	var $form = $("#VMCAT");
	var d = getFormData($form);
	var s = JSON.stringify(d);

	$.ajax({
		url: "/addVM_Category",
		type: "POST",
		data: s,
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			if(data.responseText == "Unauthorized operation!")
				return;
				//window.location.assign("/");
			alert("VM category added successfully");
			window.location.assign("../html/viewVM_Categories.html");
		}
	});
}

function removeVM_Category(name)
{
	s = JSON.stringify(name);
	$.ajax({
		url: '/DeleteVM_Category',
		type: "DELETE",
		contentType:"application/json",
		dataType:"json",
		data: s,
		complete: function()
		{
			alert("VM Cat successufully deleted");
			listVM_Categories();
		}
	});
}

function listVM_Categories()
{
	$.ajax({
		url: "/getVM_Categories",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data){
			if(data.responseText === "Unauthorized operation!")
				return;
			VM_Categories = JSON.parse(data.responseText);
			$("#allVM_Categories").html("");
			VM_Categories.forEach(function(item, indeks){
				$("#allVM_Categories").append(
					"<tr>" +
					"<td>"+VM_Categories[indeks].name+"</td>" +
					"<td>"+VM_Categories[indeks].numberOfCores+"</td>" +
					"<td>"+VM_Categories[indeks].gbOfRAM+"</td>" +
					"<td>"+VM_Categories[indeks].numberOfGPUCores+"</td>" + 
					"<td><a onclick = \"initDeleteVM_Category('"+VM_Categories[indeks].name+"')\">Delete</a></td>" + 
					"<td><a href = \"/goToEditDetailsVMC\" onclick = \"initDetailsVMC('" + item.name + "')\">Edit</a></td>" + 
					"</tr>"
				);
			});
		}
	});
}

function initDetailsVMC(name)
{
	data = [name];
	s = JSON.stringify(data);
	
	$.ajax(
	{
		url: "/setNameDetailsVMC",
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		data: s
	});	
}

function loadDetailsVMC()
{
	$.ajax(
	{
		url: "/getDetailsVMC",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			vm = JSON.parse(data.responseText);
			
			initEditVM_Category(vm.name);
		}
	});
	
}

function initEditVM_Category(name)
{
	getEditVMCs(name, findVMCsForEdit);
}

function findVMCsForEdit(name, callback)
{
	VMCsReturned.forEach(function(item)
	{
		if(name === item.name)
		{
			VMCToEdit = item;
			callback(item);
		}			
	});
}

function getEditVMCs(name, callback)
{
	$.ajax({
		url: "/getVM_Categories",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			VMCsReturned = JSON.parse(data.responseText);
			callback(name, setEditFormValues);
		}
	});
}

function initDeleteVM_Category(name)
{
	getVMCs(name, findVMC);	
}

function findVMC(name, callback)
{
	VMCsReturned.forEach(function(item)
	{
		if(name === item.name)
		{
			callback(item);
		}		
	});
}

function editVMC()
{
	if(!validator("nameEdit", "numberOfCoresEdit", "gbOfRAMEdit", "numberOfGPUCoresEdit", "EmptyEdit"))
		return;

	var $form = $("#VMCAT_Edit");
	var data = getFormData($form);
	VMCPair = [VMCToEdit, data];
	s = JSON.stringify(VMCPair);

	$.ajax({
		url: "/EditVM_Category",
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		data: s,
		success: function()
		{
			alert("VMC edited successfully");
			window.location.assign("../html/viewVM_Categories.html");
		}
	});
}

function getVMCs(name, callback)
{
	$.ajax({
		url: "/getVM_Categories",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			VMCsReturned = JSON.parse(data.responseText);
			callback(name, removeVM_Category);
		}
	});
}



function setEditFormValues(vmc)
{
	document.getElementById("nameEdit").value = vmc.name;
	document.getElementById("numberOfCoresEdit").value = vmc.numberOfCores;
	document.getElementById("gbOfRAMEdit").value = vmc.gbOfRAM;
	document.getElementById("numberOfGPUCoresEdit").value = vmc.numberOfGPUCores;	
}

function validator(name, cores, ram, gpu, mode)
{
	ind = true;

	if(!validateStringInput(name, mode))
		ind =  false;
	if(!validateIntInput(cores, mode))
		ind = false;
	if(!validateIntInput(ram, mode))
		ind = false;
	if(!validateIntInput(gpu, mode))
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
