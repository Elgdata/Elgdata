function deleteUser(userId){
	// Delete funtion
	$(document).delegate('#deleteUser', 'click', function() {
		$( "#delete-user-dialog" ).dialog({
			 buttons: 
				 {
					 "Ja": function() {
						 jsRoutes.controllers.Security.deleteUser(userId).ajax({
						        success: function(data) {
							        	if (data) {

							        		// delete element from DOM
							        		$("#user-" + userId).remove();
							        	} else {
							        		alert ("Last admin!")
							        	}
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

// Add new User
function createUser(){
	// Create funtion
	$(document).delegate('#createUser', 'click', function() {
		$( "#create-user-dialog" ).dialog({
			 buttons: 
				 {
					 "Opprett": function() {
						 jsRoutes.controllers.Security.submitRegisterForm().ajax({
						 data : $("#create-user-form").serialize(),
						        success: function(data) {
						        	if(data){
							        	// id, username
							        	var userArray = data.split(';');
							        	var divString = 
							        	"<div class=\"col-lg-6\" id=\"user-" + userArray[0] +"\">"
										+"<div id=\"opacity\" class=\"jumbotron hero-spacer\">"
										+"<h3 id=\"white\" class=\"elgdata\"> <i class=\"glyphicon glyphicon-user\"> </i> " + userArray[1] +"</h3>"
										+"<table  id=\"white\" class=\"table table-condensed\" id=\"table\">"
										+"<tbody>"
										+"<tr>"
										+"<td>Rolle</td>"
										+"<th>"+ $("#create-user-form-role option:selected").text() +"</th>"
										+"</tr>"
										+"</tbody>"
										+"</table>"
										+"<input type=\"button\" value=\"Slett bruker\" class=\"btn btn-sm btn-danger\" href=\"#\" id=\"deleteUser\" data-role=\"button\" onClick=\"deleteUser(" + userArray[0] +")\">"
										+"</div>"
										+"</div>";
							        	$("#users-all").append( divString );
						        	} else {						        		
						        		alert("Ugyldige verdier")
						        		}
						        },
						        error: function() {
						          alert("Ugyldige verdier")
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



