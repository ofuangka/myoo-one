angular.module('myooApp').controller('reviewCtrl', function($scope, $filter, $http, FN_AJAX_FAILURE) {
	var dateFilter = $filter('date'),
		lastWeek = new Date(),
		selections = [ {
			name : 'Comparison',
			id : 'comparison'
		} ];
	lastWeek.setDate(lastWeek.getDate() - 7);
	var getComparisonData = function(comparison) {
		var arr = [],
			header = ['Date'],
			dateBuf;
		for (var user in comparison.users) {
			header.push(user);
		}
		arr.push(header);
		for (var i = 0, len = comparison.dates.length; i < len; i++) {
			dateBuf = [comparison.dates[i]];
			for (user in comparison.users) {
				dateBuf.push(comparison.users[user][i]);
			}
			arr.push(dateBuf)
		}
		return google.visualization.arrayToDataTable(arr);
	};
	var getChartData = function(data) {
		var ret = null;
		for (var chart in data) {
			switch (chart) {
			case 'comparison' : {
				ret = getComparisonData(data.comparison);
				break;
			}
			default : {}
			}
		}
		return ret;
	};
	
	$scope.userState.review = {
		from : lastWeek,
		to : new Date(),
		selections : selections,
		currentSelection : selections[0]
	};
	$scope.changeReviewSelection = function(selection) {
		$scope.userState.review.currentSelection = selection;
	};
	$scope.$watch('userState.review', function(newValue, oldValue) {
		$http.get('api/projects/' + $scope.userState.currentProjectId + '/review/' + newValue.currentSelection.id, {
			params : {
				from : dateFilter(newValue.from, 'yyyy-MM-dd'),
				to : dateFilter(newValue.to, 'yyyy-MM-dd')
			}
		}).then(function(response) {
			(new google.visualization.ColumnChart(document.getElementById('chart'))).draw(getChartData(response.data), {
				
			});
		}, FN_AJAX_FAILURE);
	}, true);
});