$(document).ready( function() {
	initUsers();
	$("#error_message").hide();
});

function initUsers() {
	$.ajax({
		url: "/rest/getUserType",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);

			if(!d.logged || d.role == "client") {
				window.location.replace("/");
			}
			else {
				loadUsers(d.role == "superadmin");
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
			
			if (d.result) {
				var users = d.users;
				var table = $("#table_users");

				for(let user of users) {
					table.append(makeTableRow(user, superAdmin));
				}
				
				if (!superAdmin) {
					$("#org_column").hide();
				}
			}
			else {
				window.location.replace("/");
			}
		}
	});
}

function setEditUser(email) {
	
	var data = [email];
	var s = JSON.stringify(data);
	$.ajax({
		url: "/rest/setEditUser",
		type:"POST",
		contentType: "application/json",
		dataType: "json",
		data: s
	});
}

function deleteUser(email) {
	$("#error_message").hide();
	var data = [email];
	var s = JSON.stringify(data);
	$.ajax({
		url: "/rest/deleteUser",
		type:"POST",
		contentType: "application/json",
		dataType: "json",
		data: s,
		complete: function(data) {
			d = JSON.parse(data.responseText);
			if(!d.result)
			{
				$("#error_message").html(d.message);
				$("#error_message").show();
			}
			else 
				location.reload();
		}
	});
}

function makeTableRow(user, superAdmin) {

	var row =
		`<tr>
			<td class="align-middle">${user.email}</td>
			<td class="align-middle">${user.name}</td>
			<td class="align-middle">${user.lastName}</td>`;
	if (superAdmin) {
		if (user.role == "SUPERADMIN") {
			row = row.concat(`<td class="align-middle">none</td>`);
		}
		else {
			row = row.concat(`<td class="align-middle">${user.organization}</td>`);
		}
	}
	row = row.concat(`<td class="align-middle"><a href="/rest/goToEditUser" onclick="setEditUser('${user.email}')">Edit</a></td>
					  <td class="align-middle"><button type="button" class="btn btn-primary" onclick="deleteUser('${user.email}')">Delete</button></td>
					  </tr>`);
	
	return row;
}