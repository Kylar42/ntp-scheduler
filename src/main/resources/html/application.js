function doSomething(frm) {
	try {
		//frm.preventDefault();
		var frm1 = $(frm);
		var jsonText = JSON.stringify(frm1.serializeArray());
		alert(jsonText);
		var request = $.ajax({
			type: "POST",
			url: "/search/classes",
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			data: jsonText,
			error: function(xhr, status, error){
				alert("Status:"+status+" Error: "+error);
			},
			success: function(xhr, status, error){
				alert("SC! Status:"+status+" Error: "+error);
			}
		});
	} catch (err) {
		alert(err.message);
	}
}
//$(document).ready(function() {
//	new setupAjaxForm('class-find');
//});
