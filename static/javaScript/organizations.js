$(document).ready( function() {
	init();
});

function init() {
	$.ajax({
		url: "/rest/getUserType",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);
			if(!d.superadmin) {
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
			orgs = JSON.parse(data.responseText);
			
			var table = $("#table_organizations");
			
			for(let org of orgs) {
				table.append(`<tr><td>${org.name}</td><td>${org.description}</td><td><img height="50" width="50" src="${org.logo}"/></td></tr>`);
			}
		}
	});
}
