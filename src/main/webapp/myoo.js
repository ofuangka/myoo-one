angular.module('myooApp', ['ngRoute'])
    .constant('SECTIONS', [
        { id : 'record', name : 'Record', description : 'Use this section to record your achievements.' },
        { id : 'review', name : 'Review', description : 'Use this section to track your progress and compare to others.' },
        { id : 'config', name : 'Config', description : 'Use this section to configure a project attributes and achievements.' }
    ])
    .constant('DEFAULT_SECTION_ID', 'record')
    .constant('FN_AJAX_FAILURE', function ajaxFailure(jqXhr) {
    	$('#alertBoxTitle').html('Uh oh...');
    	$('#alertBoxBody').html('Something went terribly wrong, and the page won\'t work as expected. Please wait and try again a little later.');
        $('#alertBox').modal('show');
    })
    .config(function($routeProvider, DEFAULT_SECTION_ID) {
    	$('#alertBox').modal({
        	show : false
        });
    	$('#preferencesBox').modal({
    		show : false
    	});
        $routeProvider.when('/project/:projectId/section/:sectionId', {
            templateUrl : function getTemplateUrl(routeParams) {
                var ret = 'partials/' + DEFAULT_SECTION_ID + '.html';
                if (routeParams.sectionId === 'record' || routeParams.sectionId === 'review' || routeParams.sectionId === 'config') {
                    ret = 'partials/' + routeParams.sectionId + '.html';
                }
                return ret;
            }
        });
        $routeProvider.when('/project/:projectId/section', {
            templateUrl : 'partials/section-list.html'
        });
        $routeProvider.when('/project/:projectId', {
            redirectTo : function getRedirectTo(routeParams) {
                return '/project/' + routeParams.projectId + '/section/' + DEFAULT_SECTION_ID;
            }
        });
        $routeProvider.when('/project', {
            templateUrl : 'partials/project-list.html'
        });
        $routeProvider.otherwise({
            redirectTo : '/project'
        });
    })
    .controller('rootCtrl', function($scope, $route, $http, FN_AJAX_FAILURE, SECTIONS) {
        $scope.userState = {
            sections : SECTIONS,
            selectedProjects : {},
            achievementsPromises : {},
            recordsPromises : {}
        };
        $scope.$on('$routeChangeSuccess', function onRouteChangeSuccess() {
            $scope.userState.currentProjectId = $route.current.params.projectId;
            $scope.userState.currentSectionId = $route.current.params.sectionId;
        });
        $scope.createProject = function() {
        	$http.post('api/projects', {
        		name : '...',
        		description : '...'
        	}).then(function onAjaxSuccess(response) {
        		$scope.userState.selectedProjects[response.data.project.id] = true;
                $scope.userState.projects.push(response.data.project);
        	}, FN_AJAX_FAILURE);
        };
        $scope.showPreferences = function() {
        	$('#preferencesBox').modal('show');
        };
        $scope.confirmClearPoints = function(project) {
        	var choice = confirm('Are you sure you want to clear your points on this project? This operation cannot be undone.');
        	if (choice) {
        		// TODO: do something
        	}
        }
        $http.get('api/selected-projects').then(function onAjaxSuccess(response) {
        	$scope.userState.selectedProjects = response.data.selectedProjects;
        	$scope.$watch('userState.selectedProjects', function saveSelectedProjects(newValue, oldValue) {
        		$http.post('api/selected-projects', newValue).then(function onAjaxSuccess(response) {
        			
                    $http.get('api/projects').then(function onAjaxSuccess(response) {
                        $scope.userState.projects = response.data.projects;            
                    }, FN_AJAX_FAILURE);
        		}, FN_AJAX_FAILURE);
        	}, true);
        }, FN_AJAX_FAILURE);
        $http.get('api/projects/all/achievements/all/records').then(function onAjaxSuccess(response) {
        	var records = response.data.records;
        	var totalPoints = 0;
        	angular.forEach(records, function doForEachRecord(record, i) {
        		totalPoints += record.points;
        	});
        	$scope.userState.totalPoints = totalPoints;
        }, FN_AJAX_FAILURE);
        $scope.getAchievementsPromise = function(projectId) {
            if (angular.isUndefined($scope.userState.achievementsPromises[projectId])) {
                $scope.userState.achievementsPromises[projectId] = $http.get('api/projects/' + projectId + '/achievements');
            }
            return $scope.userState.achievementsPromises[projectId];
        };
        $scope.getRecordsPromise = function(projectId) {
        	if (angular.isUndefined($scope.userState.recordsPromises[projectId])) {
        		$scope.userState.recordsPromises[projectId] = $http.get('api/projects/' + projectId + '/achievements/all/records');
        	}
        	return $scope.userState.recordsPromises[projectId];
        }
        $scope.getProjectById = function(projectId) {
            var ret = {},
                projects = $scope.userState.projects || [];
            for (var i = 0, len = projects.length; i < len; i++) {
                if (projects[i].id === projectId) {
                    ret = projects[i];
                    break;
                }
            }
            return ret;
        };
    });