function classFind(frm) {
	try {
		//frm.preventDefault();
		var frm1 = $(frm);
		var jsonText = JSON.stringify(frm1.serializeArray());
		//alert(jsonText);
		var request = $.ajax({
			type: "POST",
			url: "/classlist/search",
			contentType: "application/json; charset=utf-8",
			data: jsonText,
			error: function(xhr, status, error){
				alert("Status:"+status+" Error: "+error);
			},
			success: function(data, status, error){
				//alert(data);
				$("#class-results").html(data);
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

$('#class-find').bind("keydown", function(e) {
  var code = e.keyCode || e.which;
  if (code  == 13) {
    e.preventDefault();
    var frm = $('#class-find');
    classFind(frm);
    return false;
  }
});
});