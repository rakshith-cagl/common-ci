useragent = navigator.userAgent.toLowerCase();
status = useragent.indexOf('android');

if(status != -1){
	$('input[type="date"]').click(function(e){
		readOnly = $(this).attr('readonly');
		if(readOnly == undefined){
			$(this).prop('type','');					     
     		Android.startDatePicker($(this).attr('id'));
		}
	});	
}
