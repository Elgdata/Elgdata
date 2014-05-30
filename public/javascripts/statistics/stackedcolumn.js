var chart;

function generateChart(chartData) {

	AmCharts
			.ready(function() {
				// SERIAL CHART
				chart = new AmCharts.AmSerialChart();
				chart.pathToImages = "/assets/javascripts/amcharts/images/";
				chart.dataProvider = chartData;
				chart.pathToImages = "/assets/javascripts/amcharts/images/";
				chart.categoryField = "year";
				chart.plotAreaBorderAlpha = 0.2;

				// AXES
				// category
				var categoryAxis = chart.categoryAxis;
				categoryAxis.gridAlpha = 0.1;
				categoryAxis.axisAlpha = 0;
				categoryAxis.color = "white";
				categoryAxis.gridPosition = "start";

				// value
				var valueAxis = new AmCharts.ValueAxis();
				valueAxis.stackType = "regular";
				valueAxis.gridAlpha = 0.1;
				valueAxis.axisAlpha = 0;
				valueAxis.color = "white";
				valueAxis.totalText = "[[total]]"
				chart.addValueAxis(valueAxis);

				// GRAPHS
				// first graph
				var graph = new AmCharts.AmGraph();
				graph.title = "Eldre (okse)";
				graph.labelText = "[[value]]";
				graph.valueField = "adultMale";
				graph.type = "column";
				graph.lineAlpha = 0;
				graph.color = "white";
				graph.fillAlphas = 1;
				graph.lineColor = "#df0000";
				graph.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span style='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
				chart.addGraph(graph);

				// second graph
				graph = new AmCharts.AmGraph();
				graph.title = "Eldre (ku)";
				graph.labelText = "[[value]]";
				graph.valueField = "adultFemale";
				graph.type = "column";
				graph.lineAlpha = 0;
				graph.color = "white";
				graph.fillAlphas = 1;
				graph.lineColor = "#079400";
				graph.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span style='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
				chart.addGraph(graph);

				// third graph
				graph = new AmCharts.AmGraph();
				graph.title = "Ungdyr (okse)";
				graph.labelText = "[[value]]";
				graph.valueField = "youngMale";
				graph.type = "column";
				graph.lineAlpha = 0;
				graph.color = "white";
				graph.fillAlphas = 1;
				graph.lineColor = "#004eff";
				graph.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span style='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
				chart.addGraph(graph);

				// fourth graph
				graph = new AmCharts.AmGraph();
				graph.title = "Ungdyr (ku)";
				graph.labelText = "[[value]]";
				graph.valueField = "youngFemale";
				graph.type = "column";
				graph.lineAlpha = 0;
				graph.color = "white";
				graph.fillAlphas = 1;
				graph.lineColor = "#be008c";
				graph.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span style='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
				chart.addGraph(graph);

				// fifth graph
				graph = new AmCharts.AmGraph();
				graph.title = "Kalv (okse)";
				graph.labelText = "[[value]]";
				graph.valueField = "calfMale";
				graph.type = "column";
				graph.lineAlpha = 0;
				graph.color = "white";
				graph.fillAlphas = 1;
				graph.lineColor = "#00ace6";
				graph.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span style='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
				chart.addGraph(graph);

				// sixth graph
				graph = new AmCharts.AmGraph();
				graph.title = "Kalv (ku)";
				graph.labelText = "[[value]]";
				graph.valueField = "calfFemale";
				graph.type = "column";
				graph.lineAlpha = 0;
				graph.color = "white";
				graph.fillAlphas = 1;
				graph.lineColor = "#e57300";
				graph.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span class='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
				chart.addGraph(graph);

				// seventh graph
				graph = new AmCharts.AmGraph();
				graph.title = "Ukjent kjønn";
				graph.labelText = "[[value]]";
				graph.valueField = "unknownSex";
				graph.type = "column";
				graph.lineAlpha = 0;
				graph.color = "white";
				graph.fillAlphas = 1;
				graph.lineColor = "#e4cd32";
				graph.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span class='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
				chart.addGraph(graph);
				chart.hideGraph(graph);

				// eight graph
				graph = new AmCharts.AmGraph();
				graph.title = "Ukjent alder (ku)";
				graph.labelText = "[[value]]";
				graph.valueField = "unknownFemale";
				graph.type = "column";
				graph.lineAlpha = 0;
				graph.color = "white";
				graph.fillAlphas = 1;
				graph.lineColor = "#8400ff";
				graph.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span class='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
				chart.addGraph(graph);
				chart.hideGraph(graph);

				// ninth graph
				graph = new AmCharts.AmGraph();
				graph.title = "Ukjent alder (okse)";
				graph.labelText = "[[value]]";
				graph.valueField = "unknownMale";
				graph.type = "column";
				graph.lineAlpha = 0;
				graph.color = "white";
				graph.fillAlphas = 1;
				graph.lineColor = "#545498";
				graph.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span class='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
				chart.addGraph(graph);
				chart.hideGraph(graph);

				// CURSOR
				var chartCursor = new AmCharts.ChartCursor();
				chartCursor.cursorAlpha = 0;
				chartCursor.zoomable = true;
				chartCursor.oneBalloonOnly = true;
				chart.addChartCursor(chartCursor);

				// SCROLLBAR -
				var chartScrollbar = new AmCharts.ChartScrollbar();
				chart.addChartScrollbar(chartScrollbar);

				// LEGEND
				var legend = new AmCharts.AmLegend();
				legend.borderAlpha = 0.2;
				legend.horizontalGap = 10;
				legend.color = "white";
				chart.addLegend(legend);
				// EXPORT
			chart.exportConfig = {
				    menuTop: '40px',
				    menuRight: '21px',
				    menuBottom: 'auto',
				    backgroundColor: '#efefef',
				    menuItems: [{
				        textAlign: 'center',
				        onclick: function () {},
				        icon: 'assets/images/download.png',
				        iconTitle: 'Lagre som bilde',
				        items: [{
				            title: 'JPG',
				            format: 'jpg'
				        }, {
				            title: 'PNG',
				            format: 'png'
				        }, {
				            title: 'PDF',
				            format: 'pdf'
				        }]
				    }],
				    menuItemOutput:{
				        fileName:"slaktevekt",
				        backgroundColor:"#999999"
				    },
				    menuItemStyle: {
				        rollOverBackgroundColor: '#EFEFEF'
				    }
				}

				// WRITE
				chart.write("chartdiv");

				setSelectYear(chartData);
			});

	chart.addListener("dataUpdated", zoomChart);

}

// this method is called when chart is first initiated as we listen
// for "dataUpdated" event
function zoomChart() {
	// different zoom methods can be used - zoomToIndexes,
	// zoomToDates, zoomToCategoryValues
	chart.zoomToIndexes(chartData.length - 40, chartData.length - 1);
}

function setSelectYear(data) {
	var max = Math.max.apply(Math, data.map(function(o) {
		return o.year;
	}));
	var min = Math.min.apply(Math, data.map(function(o) {
		return o.year;
	}));
	$("#startYear").val(min);
	$("#endYear").val(max + 1);

}