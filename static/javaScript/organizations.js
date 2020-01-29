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
				//TODO ovde ide dalje ucitavanje
			}
		}
	});
}
