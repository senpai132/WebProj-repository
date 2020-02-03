$(document).ready( function() {
	init();
});

function init() {
	$.ajax({
		url: "/rest/getUserType",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);

			if(d.role != "superadmin") {
				window.location.replace("/");
			}
			else {
				loadOrganizations();
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

			if (d.result) {
				var table = $("#table_organizations");
				
				for(let org of d.orgs) {
					table.append(`<tr>
						<td class="align-middle">${org.name}</td>
						<td class="align-middle">${org.description}</td>
						<td class="align-middle"><img height="50" width="50" src="${org.logo}"/></td>
						<td class="align-middle"><a href="/rest/goToEditOrganization" onclick="setEditOrganization('${org.name}')">Edit</a></td>
						</tr>`);
				}
			}
			else {
				window.location.replace("/");
			}
		}
	});
}

function setEditOrganization(orgName) {
	
	var data = [orgName];
	var s = JSON.stringify(data);
	$.ajax({
		url: "/rest/setEditOrganization",
		type:"POST",
		contentType: "application/json",
		dataType: "json",
		data: s
	});
}