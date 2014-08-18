angular.module('myooApp')
    .controller('configCtrl', function($scope, $http, $location, FN_AJAX_FAILURE) {
        $scope.addAchievement = function() {
        	$http.post('api/projects/' + $scope.userState.currentProjectId + '/achievements', {
        		
        	}).then(function onAjaxSuccess(response) {
                $scope.userState.achievements.push(response.data.achievement);
        	}, FN_AJAX_FAILURE);
        };
        $scope.deleteAchievement = function(achievement) {
        	$http.delete('api/projects/' + $scope.userState.currentProjectId + '/achievements/' + achievement.id, {}).then(function onAjaxSuccess() {
                $scope.userState.achievements.splice($scope.userState.achievements.indexOf(achievement), 1);
        	}, FN_AJAX_FAILURE);
        };
        $scope.confirmDeleteProject = function(project) {
            var choice = confirm('Are you sure you want to delete this project? This operation cannot be undone.');
            if (choice) {
            	$http.delete('api/projects/' + project.id, {
            		
            	}).then(function onAjaxSuccess() {
            		
            		// clear the achievements
            		while ($scope.userState.achievements.length > 0) {
            			$scope.userState.achievements.pop();
            		}
            		
            		// remove the project
                    $scope.userState.projects.splice($scope.userState.projects.indexOf(project), 1);
                    
                    // return to the main page
                    $location.url('/');
            	}, FN_AJAX_FAILURE);
            }
        };
        $scope.getAchievementsPromise($scope.userState.currentProjectId).then(function doOnAjaxSuccess(response) {
            $scope.userState.achievements = response.data.achievements;
        }, FN_AJAX_FAILURE);
    });