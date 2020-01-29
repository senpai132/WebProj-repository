function getFilterFormData()
{

}

function filterVMs()
{
	getFormData();

	$.ajax({
		url: "/filterVMs",
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		complete: function(){
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


function initSliders()
{
	$.ajax({
		url: "/initVM_Sliders",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		complete: function(data){
			
		}  
	});
}

function updateCores()
{
	$("#test").html(sliderCores.value);
}