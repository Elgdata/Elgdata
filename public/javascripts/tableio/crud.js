function deleteElk(elkId){
	// Delete funtion
	$(document).delegate('#deleteElk', 'click', function() {
		$( "#delete-elk-dialog" ).dialog({
			 buttons: 
				 {
					 "Ja": function() {
						 jsRoutes.controllers.TableIO.deleteElk(elkId).ajax({
						        success: function(data) {
						        	// delete element from DOM
						        	$( "#elk-" + elkId ).remove();
						        },
						        error: function() {
						          alert("Error!")
						        }
						      })
							
					 	$( this ).dialog( "close" );
					 },
					 "Avbryt": function() {
					 	$( this ).dialog( "close" );
				 	 }
				 }
		});
	});
}