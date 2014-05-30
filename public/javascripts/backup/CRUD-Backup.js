//Create new backup
function createBackupFile() {
	// Create funtion
	$("#create-backup-dialog")
			.dialog(
					{
						buttons : {
							"Opprett" : function() {
								jsRoutes.controllers.Backup
										.createDatabaseDump()
										.ajax(
												{
													success : function(data) {
														var addId = $(
																'#backupTable')
																.dataTable()
																.fnAddData(
																		[
																				data
																				,
																					"<a href=\"#\" class=\"glyphicon glyphicon-trash\" data-role=\"button\" style=\"color:black\" onClick=\"deleteBackup('"
																						+ data
																						+ "')\"></a>"
																				,
																					" <a href=\"#\" class=\"glyphicon glyphicon-open\" data-role=\"button\" style=\"color:black\" onClick=\"restoreBackupFile('"
																						+ data
																						+ "')\"></a>" ]);
														var newBackup = $(
																'#backupTable')
																.dataTable()
																.fnSettings().aoData[addId[0]].nTr;
														newBackup.setAttribute(
																'id', 'backup-'
																		+ data);
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

// Restore backup
function restoreBackupFile(filename) {
	// Create funtion

	$("#restore-backup-dialog").dialog({
		buttons : {
			"Avbryt" : function() {
				$(this).dialog("close");
			},

			"Gjennopprett" : function() {
				jsRoutes.controllers.Backup.restoreDatabase(filename).ajax({
					success : function(data) {
						alert(data);
					},
					error : function() {
						alert("Error!")
					}
				})
				$(this).dialog("close");
			}
		}
	});

}

function deleteBackup(backupFileName) {
	// Delete function

	$("#delete-backup-dialog").dialog(
			{
				buttons : {
					"Ja" : function() {
						jsRoutes.controllers.Backup.deleteDatabaseFile(
								backupFileName).ajax({
							success : function(data) {
								// delete
								// element
								// from
								// DOM

								$("#backup-" + backupFileName).remove();
							},
							error : function() {
								alert("Feil - Fikk ikke slettet")
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

function uploadBackupFile() {
	// Delete function
	// .serialize() upload-backup-file-form
	$("#upload-backup-dialog").dialog({
		buttons : {
			"Ja" : function() {
				jsRoutes.controllers.Backup.uploadBackupFile().ajax({
//					data : $("#upload-backup-file-form").serialize(),
					success : function(data) {
						// delete
						// element
						// from
						// DOM

						//$("#backup-" + backupFileName).remove();
					},
					error : function() {
						alert("Feil - Fikk ikke lastet opp fil")
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
