$(document).ready( function() {
	initUsers();
});

function initUsers() {
	$.ajax({
		url: "/rest/getUserType",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);

			if(d.superadmin || d.admin) {
				loadUsers(d.superadmin);
			}
			else {
				window.location.replace("/");
			}
		}
	});
}

function loadUsers(superAdmin) {
	$.ajax({
		url: "/rest/getUsers",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);
			
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
		row = row.concat(`<td>${user.organization}</td>`);
	}
	row = row.concat(`</tr>`)
	
	return row;
}