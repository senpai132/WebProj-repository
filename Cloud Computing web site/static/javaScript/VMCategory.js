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
	var $form = $("#VMCAT");
	var d = getFormData($form);
	var s = JSON.stringify(d);

	$.ajax({
		url: "/addVM_Category",
		type: "POST",
		data: s,
		contentType: "application/json",
		dataType: "json",
		complete: function()
		{
			alert("VM category added successfully");
			listVM_Categories();
		}
	});
}

function removeVM_Category(name)
{
	$.ajax({
		url: '/DeleteVM_Category/'+name,
		type: "GET",
		contentType:"application/json",
		dataType:"json",
		complete: function()
		{
			alert("Alert VM Cat successufully deleted");
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
			VM_Categories = JSON.parse(data.responseText);
			$("#allVM_Categories").html("");
			VM_Categories.forEach(function(item, indeks){
				$("#allVM_Categories").append(
					"<tr>" +
					"<td>"+VM_Categories[indeks].name+"</td>" +
					"<td>"+VM_Categories[indeks].numberOfCores+"</td>" +
					"<td>"+VM_Categories[indeks].gbOfRAM+"</td>" +
					"<td>"+VM_Categories[indeks].numberOfGPUCores+"</td>" + 
					"<td><a onclick = \"removeVM_Category('"+VM_Categories[indeks].name+"')\">Delete</a></td>" + 
					"<td><a onclick = \"editVM_Category('" + item.name + "')\">Edit</a></td>" + 
					"</tr>"
				);
			});
		}
	});
}

function editVM_Category(vmc)
{
	$.ajax({
		url: '/getVM_Categories',
		type: "GET",
		contentType:"application/json",
		dataType:"json",
		complete: function(data){
			var VM_Categories = JSON.parse(data.responseText);
			VM_Categories.forEach(function(item, indeks){
				if(item.name === vmc)
				{
					setEditFormValues(item);
					s = JSON.stringify(item);
					$.ajax({
					url: '/EditVM_Category',
					type: "POST",
					data: s,
					contentType:"application/json",
					dataType:"json"
				});
				}
				
			});	
		}
	});
	//window.location.assign("../html/EditVM_Category.html");

	
}

function setEditFormValues(vmc)
{
	document.getElementById("nameEdit").value = vmc.name;
	document.getElementById("numberOfCoresEdit").value = vmc.numberOfCores;
	document.getElementById("gbOfRAMEdit").value = vmc.gbOfRAM;
	document.getElementById("numberOfGPUCoresEdit").value = vmc.numberOfGPUCores;	
}

function confirmEditVM_Category()
{
	var $form = $("#VMCAT_Edit");
	var d = getFormData($form);
	var s = JSON.stringify(d);

	$.ajax({
		url: '/SaveEditVM_Category',
		type: "POST",
		data: s,
		contentType:"application/json",
		dataType:"json",
		complete: function(){
			listVM_Categories();
		}
	});
}
