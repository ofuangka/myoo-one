angular.module('myooApp')
	.directive('resizingChart', function($window) {
		return {
			link : function(scope, element, attrs) {
				var drawChart = function() {
					var chartData = scope.$eval(attrs.resizingChart);
					if (angular.isDefined(chartData)) {
						(new google.visualization.ColumnChart(element.get(0))).draw(chartData, {
							vAxis : {
								format : '#,###.##\u03BC',
								viewWindow : {
									min : 0
								}
							}
						});
					}
				};
				scope.$watch(attrs.resizingChart, function() {
					drawChart();
				});
				angular.element($window).on('resize', function() {
					drawChart();
				});
			}
		};
	});