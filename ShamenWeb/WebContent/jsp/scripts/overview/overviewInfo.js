/**
 * Author: Shane Duncan 
 * Functions for overviewInfo.jsp
 */
$(document).ready(function() {
	initCharts();
	$('[type=radio]').each(function(i) {
		var id = "showPie" + $(this).val();
		$(this).attr('id',id);
	});
	
	doChartOne();
	doChartTwo();
	doChartThree();
});

function initCharts(){
	doughnutChartOptions = {
			responsive : false,
			maintainAspectRatio: true,
			cutoutPercentage : 65,
			animation: {
				animateRotate: true,
				animateScale: true
			},
			legend : {
				position : 'right',
				labels: {
					fontColor: 'white',
					boxWidth: 10,
					borderWidth: 0,
					usePointStyle: false
				}
			
			},
			tooltips: {
				yPadding: 7,
				xPadding: 13,
				caretSize: 5,
				backgroundColor: '#fff',
				multiKeyBackground: "#fff",
				bodyFontColor: "#000000",
				callbacks: {}
			},
			title: {
				display: true,
				fontSize: 18,
				fontColor: 'white',
				text: ''
				
			}
		};
	noAppsData = {
		datasets : [{
			data : ["1"],
			borderWidth: 0,
			backgroundColor : ["#000000"]
		}],
		labels : ['No Apps']
	};
	noAppsTooltips = {
		label: function(tooltipItem, data) {
			return ' No Apps';
		}
	};
	hasAppsTooltips = {
		label: function(tooltipItem, data) {
			var label = ' ' + data.labels[tooltipItem.index] + ': '+ data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
			return label;
		}
	};	
}

function changePie(refId){
	document.forms[0].submit();
}

function doChartOne(){
	var doughnutChart;
	doughnutChartOptions.title.text = chart1Text;
	var sum = parseFloat(chart1Good) + parseFloat(chart1Bad) + parseFloat(chart1Unreportable);
	var doughnutChartCanvas = $("#donut-1").get(0).getContext("2d");
	 if( sum > 0){
		 doughnutChartOptions.tooltips.callbacks = hasAppsTooltips;
		doughnutChart = new Chart(doughnutChartCanvas, {
			type : 'doughnut',
			data : {
				labels : ['Thriving', 'In Error', 'Unreportable' ],
				datasets : [ {
					data : [ chart1Good, chart1Bad, chart1Unreportable ],
					backgroundColor : [ '#3CB371', '#C24642', '#ADB5BD', '#A2D1CF', '#7F6084' ],
					borderWidth: 0,
					label : 'Dataset 1'
				} ]
			},
			options : doughnutChartOptions
		});
	 }else{
		 doughnutChartOptions.tooltips.callbacks = noAppsTooltips;
		doughnutChart = new Chart(doughnutChartCanvas, {
			type : 'doughnut',
			data : noAppsData,
			options : doughnutChartOptions
		});
	}
}
function doChartTwo(){
	var doughnutChart;
	doughnutChartOptions.title.text = chart2Text;
	var sum2 = parseFloat(chart2Good) + parseFloat(chart2Bad) + parseFloat(chart2Unreportable) + parseFloat(chart2Pending) + parseFloat(chart2Running) + parseFloat(chart2OffSchedule) + parseFloat(chart2Inactive);
	var doughnutChartCanvas = document.getElementById("donut-2").getContext("2d");
	
	if(sum2 > 0){
		doughnutChartOptions.tooltips.callbacks = hasAppsTooltips;
		doughnutChart = new Chart(doughnutChartCanvas, {
			type: 'doughnut',
			data: {
				labels: [chart2GoodText, chart2BadText , chart2UnreportableText, chart2PendingText, chart2RunningText, chart2OffScheduleText, chart2InactiveText],
				datasets:[{
					data: [chart2Good , chart2Bad , chart2Unreportable , chart2Pending , chart2Running , chart2OffSchedule , chart2Inactive],
					backgroundColor: ["#3CB371", "#C24642","#ADB5BD","#A2D1CF","#7F6084","#FECC58","#A0522D"],
					borderWidth: 0,
					label: 'Dataset 2'
				}]
			},
			options : doughnutChartOptions
		});	
	}else{
		doughnutChartOptions.tooltips.callbacks = noAppsTooltips;
		doughnutChart = new Chart(doughnutChartCanvas, {
			type : 'doughnut',
			data : noAppsData,
			options : doughnutChartOptions
		});
	}
}
function doChartThree(){
	var doughnutChart;
	doughnutChartOptions.title.text = chart3Text;
	var doughnutChartCanvas = $("#donut-3").get(0).getContext("2d");
	var sum3 = parseFloat(chart3Active) + parseFloat(chart3Bad) + parseFloat(chart3Unreportable) + parseFloat(chart3Info) + parseFloat(chart3Suspended);
	if(sum3 > 0){
		doughnutChartOptions.tooltips.callbacks = hasAppsTooltips;
		doughnutChart = new Chart(doughnutChartCanvas, {
		      type: 'doughnut',
		      data: {
		    	  datasets:[{
		    		 data: [chart3Active , chart3Bad , chart3Unreportable , chart3Info , chart3Suspended],
		    		 backgroundColor: ["#3CB371", "#C24642","#ADB5BD","#87CEFA","#FF7F50"],
		    		 borderWidth: 0,
		    		 label: 'Dataset 3'
					}],
					labels: ["Active", "In Error" , chart3UnreportableText,  chart3InfoText,  chart3SuspendedText]
		      },
		      options : doughnutChartOptions
			});	
	}else{
		doughnutChartOptions.tooltips.callbacks = noAppsTooltips;
		doughnutChart = new Chart(doughnutChartCanvas, {
			type : 'doughnut',
			data : noAppsData,
			options : doughnutChartOptions
		});
	}
}
