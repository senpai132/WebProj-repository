$(document).ready(function() {
		initSelectTags("category", "organisation");
		initFreeDisks("#freeDiscs");
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

function initFreeDisks(id)
{
	$.ajax({
		url: "/getFreeDiscs",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			freeDiscs = JSON.parse(data.responseText);
			$(id).html("");
			freeDiscs.forEach(function(item){
				$(id).append(
					"<tr>" +
					"<td>"+item.name+"</td>" +
					"<td>"+item.type+"</td>" +
					"<td>"+item.capacity+"</td>" +
					"<td><input type=\"checkbox\" name = \""+item.name+"\" value = \""+item.name+"\"></td>" + 
					"</tr>"
				);
			});
		}
	});
}

function saveSelectedDiscs(callback)
{
	var sList = "[";
	$('input[type=checkbox]').each(function () {
		sList +=  this.checked ? $(this).val() + "," : "";
	});
	sList += "]";
	alert(sList);
	$.ajax({
		url: "/saveSelectedDiscs",
		type: "POST",
		data: sList,
		contentType: "application/json",
		dataType: "json",
		complete: function(){
			callback();
		}
	});
}

function editVM()
{
	var $form = $("#detailsVM");
	var d = getFormData($form);

	pairOfVM = [foundVM, d];

	var s = JSON.stringify(pairOfVM);

	$.ajax({
		url: "/editVM",
		type: "POST",
		data: s,
		contentType: "application/json",
		dataType: "json",
		complete: function()
		{
			alert("VM edited successfully");
			listVMs();
		}
	});
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
					"<td>"+item.category+"</td>" + (1 === 1 ?
					"<td><a onclick = \"initDeleteVM('"+item.name+"')\">Delete</a></td>" + 
					"<td><a href = \"goToEditDetailsVM\" onclick = \"initDetailsVM('" + item.name + "')\">Edit</a></td>" + 
					"</tr>" : "</tr>")
				);
			});
		}
	});
}

function initDetailsVM(name)
{
	data = [name];
	s = JSON.stringify(data);
	
	$.ajax(
	{
		url: "/setNameDetailsVM",
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		data: s
	});	
}

function loadDetailsVM()
{
	$.ajax(
	{
		url: "/getDetailsVM",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			vm = JSON.parse(data.responseText);
			
			initSelectTags("categoryDetails", "organisationDetails");
			initFreeDisks("#freeDiscsEdit");
			getVMDetails(vm.name, findVM);
		}
	});
	
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

	//saveSelectedDiscs();
	
	var $form = $("#addVM");
	var d = getFormData($form);
	var s = JSON.stringify(d);
	
	$.ajax({
		url: "/addVM",
		type: "POST",
		data: s,
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			alert("VM added successfully");
			alert(data.responseText);
			stat = JSON.parse(data.responseText);
			alert(stat.statusAkcije);
			if(stat.statusAkcije == true)
				window.location.assign("/");
			
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
		if(name == item.name)
		{
			foundVM = item;
			callback(item.category, 0);
		}		
	});
}

function findVM_delete(name, callback)
{
	VMsReturned_delete.forEach(function(item)
	{
		if(name === item.name)
		{
			foundVM = item;
			callback(item);
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
					fillActiveDiscs(foundVM);
				}
					
			});
		}
	});
}

function fillActiveDiscs(foundVM)
{
	s = JSON.stringify(foundVM);
	$.ajax({
		url: "/getActiveDiscs",
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		data: s,
		complete: function(data)
		{
			discsReturned = JSON.parse(data.responseText);
			discsReturned.forEach(function(item){
				$("#freeDiscsEdit").append(
					"<tr>" +
					"<td>"+item.name+"</td>" +
					"<td>"+item.type+"</td>" +
					"<td>"+item.capacity+"</td>" +
					"<td><input type=\"checkbox\" name = \""+item.name+"\" value = \""+item.name+"\" checked></td>" + 
					"</tr>"
				);
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
	getVMs(name, findVM_delete);	
}

function getVMs(name, callback)
{
	$.ajax({
		url: "/getVMs",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data)
		{
			VMsReturned_delete = JSON.parse(data.responseText);
			callback(name, removeVM);
		}
	});
}

function validateIntInput(id)
{
	$("#"+id+"Error").html("");

	if(document.getElementById(id+"").value >>> 0 === parseFloat(document.getElementById(id+"").value) == false)
	{
		$("#"+id+"Error").html("<font color=\"red\">This field must be a positive integer</font>");
		return false;
	}
	return true;
}

function validateIntFilter(id, varName)
{
	if(document.getElementById(id).value != "")
	{
		if(validateIntInput(id)==true)
		{
			varName = parseFloat(document.getElementById(id).value);
			return true;
		}
			
		else
			return false;
	}
	else
		varName = 0;

	return true;
}

function getFilterFormData()
{
	$("#minRAMError").html("");
	$("#minGPUError").html("");
	$("#minCoresError").html("");
	filterName = document.getElementById("findVMName").value;

	minCores = 0;
	maxCores = 0;
	minRAM = 0;
	maxRAM = 0;
	minGPU = 0;
	maxGPU = 0;
	ind = true;

	if(validateIntFilter("minCores", minCores)==false)
		return false;
	else
		minCores = document.getElementById("minCores").value =="" ? 0: parseFloat(document.getElementById("minCores").value);
	
	if(validateIntFilter("maxCores", maxCores)==false)
		return false;
	else
		maxCores = document.getElementById("maxCores").value =="" ? 1000:parseFloat(document.getElementById("maxCores").value);

	if(validateIntFilter("minRAM", minRAM)==false)
		return false;
	else
		minRAM = document.getElementById("minRAM").value =="" ? 0:parseFloat(document.getElementById("minRAM").value);

	if(validateIntFilter("maxRAM", maxRAM)==false)
		return false;
	else
		maxRAM = document.getElementById("maxRAM").value =="" ? 1000:parseFloat(document.getElementById("maxRAM").value);

	if(validateIntFilter("minGPU", minGPU)==false)
		return false;
	else
		minGPU = document.getElementById("minGPU").value =="" ? 0:parseFloat(document.getElementById("minGPU").value);

	if(validateIntFilter("maxGPU", maxGPU)==false)
		return false;
	else
		maxGPU = document.getElementById("maxGPU").value =="" ? 1000:parseFloat(document.getElementById("maxGPU").value);
	
	if(document.getElementById("minRAM").value >>> maxRAM)
	{
		$("#minRAMError").html("<font color=\"red\">Max must be greater or equal to Min</font>");
		ind = false;
	}
		
	if(document.getElementById("minCores").value >>> maxCores)
	{
		$("#minCoresError").html("<font color=\"red\">Max must be greater or equal to Min</font>");
		ind = false;
	}

	if(document.getElementById("minGPU").value >>> maxGPU)
	{
		$("#minGPUError").html("<font color=\"red\">Max must be greater or equal to Min</font>");
		ind = false;
	}
		
	return ind;
}

function filterVMs()
{
	if(getFilterFormData() == false)
		return;

	var cores1 = [minCores, maxCores];
	var ram1 = [minRAM, maxRAM];
	var gpu1 = [minGPU, maxGPU];
	
	var d = {
		name: filterName,
		cores: cores1,
		ram: ram1,
		gpu: gpu1
	};
	var s = JSON.stringify(d);

	$.ajax({
		url: "/filterVMs",
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		data: s,
		complete: function(data){
			var vms = JSON.parse(data.responseText);
			$("#filteredVMs").html("");
			vms.forEach(function(item, indeks){
				$("#filteredVMs").append(
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