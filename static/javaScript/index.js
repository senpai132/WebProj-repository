$(document).ready( function() {
	init();
});

function init() {
	$.ajax({
		url: "/rest/getUserType",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);

			if(!d.logged) {
				window.location.replace("/html/login.html");
			}
			else {
				//TODO ovde ide dalje ucitavanje
			}
		}
	});
}
