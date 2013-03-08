$(document).ready(function(){

$('#class-find').bind("keydown", function(e) {
  var code = e.keyCode || e.which;
  if (code  == 13) {
    e.preventDefault();
    console.log('stop');
    var frm = $('#class-find');
    classFind(frm);
    return false;
  }
});
});
function classFind(frm) {
    alert('submitted!');
}