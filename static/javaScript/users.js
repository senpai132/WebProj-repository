$(document).ready( function() {
	initUsers();
});

function initUsers() {
	$.ajax({
		url: "/rest/isAdmin",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);
			if(!d.message) {
				window.location.replace("/");
			}
			else {
				loadUsers();
			}
		}
	});
}

function loadUsers() {
	$.ajax({
		url: "/rest/getUsers",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);
			
			var superAdmin = d.superadmin;
			var users = d.users;
			var table = $("#table_users");

			for(let user of users) {
				table.append(makeTableRow(user, superAdmin));
			}
			
			if (!superAdmin) {
				$("#org_column").hide();
			}
		}
	});
}

function makeTableRow(user, superAdmin) {

	var row =
		`<tr>
			<td>${user.email}</td>
			<td>${user.name}</td>
			<td>${user.lastName}</td>`;
	
	if (superAdmin) {
		row.concat(`<td>${user.organizacija}</td>`);
	}
	row.concat(`</tr>`)
	
	return row;
}