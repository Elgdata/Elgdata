function deleteLoggingData(LoggingId) {
	// Delete funtion
	$("#delete-dialog").dialog({
		buttons : {
			"Ja" : function() {
				jsRoutes.controllers.FileIO.deleteLoggingData(LoggingId).ajax({
					success : function(data) {
						// delete
						// element
						// from
						// DOM
						$("#logging-" + LoggingId).remove();
					},
					error : function() {
						alert("Error!")
					}
				})
				$(this).dialog("close");
			},
			"Avbryt" : function() {
				$(this).dialog("close");
			}
		}
	});
}
// Create new log data
function createLoggingData() {
	// Create funtion
	$("#create-logg-dialog")
			.dialog(
					{
						buttons : {
							"Opprett" : function() {
								jsRoutes.controllers.FileIO
										.submitNewLoggingData()
										.ajax(
												{
													data : $(
															"#create-logg-form")
															.serialize(),
													success : function(data) {
														if(data.success == true){
														var obj = jQuery
																.parseJSON(data);
														$('#loggTable')
																.dataTable()
																.fnAddData(
																		[
																				obj.id,
																				obj.logging,
																				obj.clearance,
																				obj.grazing,
																				obj.logging
																						+ obj.clearance
																						+ obj.grazing,
																				"<a href=\"#\" class=\"glyphicon glyphicon-remove\" data-role=\"button\" onClick=\"deleteLoggingData("
																						+ obj.id
																						+ ")\"></a>" ]);
														}
														else{
															alert("Ugyldige verdier!");
														}
													},
													error : function() {
														alert("Error!")
													}
												})

								$(this).dialog("close");
							},
							"Avbryt" : function() {
								$(this).dialog("close");
							}
						}
					});
}
