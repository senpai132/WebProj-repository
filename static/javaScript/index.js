function init() {
	$.ajax({
		url: "/rest/isLoggedIn",
		type: "GET",
		complete: function(data){
			d = JSON.parse(data.responseText);
			if(!d.result) {
				window.location.replace("/html/login.html");
			}
			else {
				//TODO ovde ide dalje ucitavanje
			}
		}
	});
}
