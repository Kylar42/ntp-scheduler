function doLogin(frm) {
	try {
		//frm.preventDefault();
		var frm1 = $(frm);
		var jsonText = JSON.stringify(frm1.serializeArray());
		//alert(jsonText);
		var request = $.ajax({
			type: "POST",
			url: "/authentication/validate",
			contentType: "application/json; charset=utf-8",
			data: jsonText,
			error: function(xhr, status, error){
				alert(xhr.responseText);
			},
			success: function(data, status, error){
				//alert(data);
				location.href = data;
				//$("#class-results").html(data);
				//$('#class-results').text(xhr);
				//alert("SC! Status:"+status+" Error: "+error);
			}
		});
	} catch (err) {
		alert(err.message);
	}
}

//This waits until the document is ready, and then binds the enter key to the submit action.
$(document).ready(function(){

$('#flogin').bind("keydown", function(e) {
  var code = e.keyCode || e.which;
  if (code  == 13) {
    e.preventDefault();
    var frm = $('#flogin');
    doLogin(frm);
    return false;
  }
});
});