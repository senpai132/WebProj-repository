$(document).ready(function()
{
	loadUser();
});

function loadUser()
{
	var korisnik;
	$.ajax({
		url: "/preuzmiKorisnika",
		type:"GET",
		contentType:"application/json",
		dataType:"json",
		complete: function(data) {
			korisnik = JSON.parse(data.responseText);
			$("#testDiv").html(korisnik.email + " " + korisnik.password);
		}
	});
}