$(document).ready( function() {
	init();
});

function init() {
	$.ajax({
		url: "/rest/getUserType",
		type: "GET",
		complete: function(data) {
			d = JSON.parse(data.responseText);

			if(d.client) {
				window.location.replace("/");
			}
			else {
				//loadOrganization();
			}
		}
	});
}