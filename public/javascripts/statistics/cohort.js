function getCohortForYears(arrayYears) {
	jsRoutes.controllers.Statistics.newCohortWeight(arrayYears).ajax({
		success : function(data) {
			var obj = jQuery.parseJSON(data);
			reloadAmCharts(data, arrayYears)
		},
		error : function() {
			if(arrayYears.length!=1){
			alert("Kan ikke ta imot tekst. Fjern denne.")
			}
		}
	});
}