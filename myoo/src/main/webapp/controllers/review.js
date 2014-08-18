angular.module('myooApp').controller('reviewCtrl', function($scope, $filter, $http, FN_AJAX_FAILURE) {
	var dateFilter = $filter('date'),
		lastWeek = new Date(),
		selections = [ {
			name : 'Comparison',
			id : 'comparison'
		} ];
	lastWeek.setDate(lastWeek.getDate() - 7);
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
		$http.get('api/review/' + newValue.currentSelection.id, {
			params : {
				from : dateFilter(newValue.from, 'yyyy-MM-dd'),
				to : dateFilter(newValue.to, 'yyyy-MM-dd')
			}
		}).then(function(response) {

		}, FN_AJAX_FAILURE);
	}, true);
});