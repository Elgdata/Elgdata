function deleteGraph(graphId){
	// Delete funtion
	$(document).delegate('#deleteGraph', 'click', function() {
		$( "#delete-dialog" ).dialog({
			 buttons: 
				 {
					 "Ja": function() {
						 jsRoutes.controllers.SavedGraphs.deleteGraph(graphId).ajax({
						        success: function(data) {
						        	// delete element from DOM
						        	$( "#graf-" + graphId ).remove();
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

// Add new Graph
function createGraph(){
	// Create funtion
	$(document).delegate('#createGraph', 'click', function() {
		$( "#create-graph-dialog" ).dialog({
			 buttons: 
				 {
					 "Opprett": function() {
						 jsRoutes.controllers.SavedGraphs.submitNewGraph().ajax({
						 data : $("#create-graph-form").serialize(),
						        success: function(data) {
//						        	{"id":51,"name":"Name","url":"URL","shortInfo":"ShortInfo","category":{"id":5,"name":null}} 
						        	var obj = jQuery.parseJSON( data );
						        	var divString = "<div id=\"graf-" + obj.id + "\" class=\"col-sm-4 col-lg-4 col-md-4\">"
									+"<h4><a style=\"color:#428BCA;\" href=\"" + obj.url + "\">" + obj.name + "</a></h4>"
									+"<p>" + obj.shortInfo + "</p>"
										+"<p>"
											+"<a style=\"color:#428BCA;\" class=\"glyphicon glyphicon-pencil\" href=\"/editgraph/" + obj.id + "\"></a>"
											+"<a style=\"color:#D9534F;\" onclick=\"deleteGraph(" + obj.id + ")\" data-role=\"button\" class=\"glyphicon glyphicon-remove\" id=\"deleteGraph\" href=\"#\"></a>"												
										+"</p>"
									+"</div>";
						        	$("#category-" + obj.category.id ).append( divString );
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
//Add new Category
function createCategory(){
	// Create funtion
	$(document).delegate('#createCategory', 'click', function() {
		$( "#create-category-dialog" ).dialog({
			 buttons: 
				 {
					 "Opprett": function() {
						 jsRoutes.controllers.SavedGraphs.submitNewCategory().ajax({
						 data : $("#create-category-form").serialize(),
						        success: function(data) {
						        	var obj = jQuery.parseJSON( data );
						        	// Add category tab
						        	var divString = "<li id=\"category-list-" + obj.id + "\"><a href=\"#category-" + obj.id + "\">" + obj.name + "</a></li>"
						        	$("#category-tabs").append( divString );
						        	
						        	// add category tab element div
						        	divString = "<div id=\"category-" + obj.id + "\"> " +
								        			"<p>Slett kategori" + 
								        				"<a href=\"#\" id=\"deleteCategory\" class=\"glyphicon glyphicon-remove\" data-role=\"button\" onClick=\"deleteCategory(" + obj.id + ")\" style=\"color:#D9534F;\"></a>" +
								        			"</p> " +
								        		"</div>";
						        	$("#category-tab-content").append( divString );
						        	$( "#tabs" ).tabs( "refresh" );
						        	
						        	// Add new cat to select box
						        	$("#category_id").append("<option value=\"" + obj.id + "\">" + obj.name + "</option>")
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

function deleteCategory(categoryId){
	// Delete function
	$(document).delegate('#deleteCategory', 'click', function() {
		$( "#delete-dialog" ).dialog({
			 buttons: 
				 {
					 "Ja": function() {
						 jsRoutes.controllers.SavedGraphs.deleteCategory(categoryId).ajax({
						        success: function(data) {
						        	// delete element from DOM
						        	$( "#category-" + categoryId ).remove();
						        	$( "#category-list-" + categoryId ).remove();
						        },
						        error: function() {
						          alert("Feil - Slett grafer")
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


