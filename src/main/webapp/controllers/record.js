angular.module('myooApp').controller(
		'recordCtrl',
		[
				'$scope',
				'$http',
				'$q',
				'FN_AJAX_FAILURE',
				function($scope, $http, $q, FN_AJAX_FAILURE) {
					$scope.completeAchievement = function(achievement) {
						$http.post('api/projects/' + $scope.userState.currentProjectId + '/achievements/' + achievement.id + '/records', {}).then(
								function onAjaxSuccess(response) {
									console.log("Successfully inserted achievement record");
									$scope.userState.records.push(response.data.record);
									$scope.userState.totalPoints += response.data.record.points;
									achievement.completed = true;
								}, FN_AJAX_FAILURE);
					};
					$scope.clearAchievementCompletedState = function() {

						angular.forEach($scope.userState.achievements, function(achievement, i) {
							achievement.completed = false;
						});
					};
					$q.all([ $scope.getAchievementsPromise($scope.userState.currentProjectId), $scope.getRecordsPromise($scope.userState.currentProjectId) ])
							.then(function onAjaxSuccess(responses) {
								$scope.userState.achievements = responses[0].data.achievements;
								$scope.userState.records = responses[1].data.records;

								$scope.clearAchievementCompletedState();

								angular.forEach($scope.userState.records, function(record, i) {
									for (var j = 0, len = $scope.userState.achievements.length; j < len; j++) {
										var achievement = $scope.userState.achievements[j];
										if (achievement.id === record.achievementId) {
											achievement.completed = true;
											break;
										}
									}
								});
							}, FN_AJAX_FAILURE);
					$scope.$on('clearPointsSuccess', function onClearPointsSuccess() {
						$scope.clearAchievementCompletedState();
					});
				} ]);