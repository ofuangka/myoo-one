angular.module('myooApp', ['ngRoute'])
    .constant('SECTIONS', [
        { id : 'record', name : 'Record', description : 'Use this section to record your achievements.' },
        { id : 'review', name : 'Review', description : 'Use this section to track your progress and compare to others.' },
        { id : 'config', name : 'Config', description : 'Use this section to configure a project attributes and achievements.' }
    ])
    .constant('DEFAULT_SECTION_ID', 'record')
    .constant('REVIEW_SECTIONS', [
		{
			name : 'Comparison',
			id : 'comparison'
		}, {
			name : 'Achievements',
			id : 'achievements'
		}
    ])
    .constant('DATE_REGEX', /\d{4}-\d{2}-\d{2}/)
    .constant('FN_AJAX_FAILURE', function ajaxFailure(jqXhr) {
    	$('#alertBoxTitle').html('Uh oh...');
    	$('#alertBoxBody').html('Something went terribly wrong, and the page won\'t work as expected. Please wait and try again a little later.');
        $('#alertBox').modal('show');
    })
    .constant('TODAY', new Date())
    .constant('LAST_WEEK', (function() {
    	var ret = new Date();
    	ret.setDate(ret.getDate() - 7);
    	return ret;
    })())
    .constant('LAST_MONTH', (function() {
    	var ret = new Date();
    	ret.setMonth(ret.getMonth() - 1);
    	return ret;
    })())
    .factory('cacheBuster', ['$q', function($q) {
    	return {
    		'request' : function(request) {
    			if (request.method === 'GET') {
    				var sep = (request.url.indexOf('?') === -1) ? '?' : '&';
    				request.url = request.url + sep + '_=' + new Date().getTime();
    			}
    			return request || $q.when(request);
    		}
    	};
    }])
    .config(['$routeProvider', '$httpProvider', 'DEFAULT_SECTION_ID', function($routeProvider, $httpProvider, DEFAULT_SECTION_ID) {
    	window.addEventListener('load', function() {
    		new FastClick(document.body);
    	}, false);
    	$('#alertBox').modal({
        	show : false
        });
    	$('#preferencesBox').modal({
    		show : false
    	});
    	$httpProvider.interceptors.push('cacheBuster');
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
    }])
    .controller('rootCtrl', ['$scope', '$route', '$filter', '$location', '$http', 'FN_AJAX_FAILURE', 'SECTIONS', 'REVIEW_SECTIONS', 'TODAY', 'LAST_WEEK', function($scope, $route, $filter, $location, $http, FN_AJAX_FAILURE, SECTIONS, REVIEW_SECTIONS, TODAY, LAST_WEEK) {
        var dateFilter = $filter('date');
    	$scope.userState = {
            sections : SECTIONS,
            subscriptions : {},
            achievementsPromises : {},
            recordsPromises : {},
            review : {
        		from : dateFilter(LAST_WEEK, 'yyyy-MM-dd'),
        		to : dateFilter(TODAY, 'yyyy-MM-dd'),
        		selections : REVIEW_SECTIONS,
        		currentSelection : REVIEW_SECTIONS[0],
        		refresh : false
        	}
        };
        $scope.$on('$routeChangeSuccess', function onRouteChangeSuccess() {
            $scope.userState.currentProjectId = $route.current.params.projectId;
            $scope.userState.mobileCurrentProjectId = $route.current.params.projectId;
            $scope.userState.currentSectionId = $route.current.params.sectionId;
        });
        $scope.$watch('userState.mobileCurrentProjectId', function doOnMobileCurrentProjectIdChange(newValue, oldValue) {
        	if (angular.isDefined(newValue) && angular.isDefined(oldValue) && newValue !== oldValue) {
        		if (angular.isDefined($scope.userState.currentSectionId)) {
        			$location.path('/project/' + newValue + '/section/' + $scope.userState.currentSectionId);
        		} else {
        			$location.path('/project/' + newValue);
        		}
        	}
        });
        $scope.createProject = function() {
        	$http.post('api/projects', {
        		name : '...',
        		description : '...'
        	}).then(function onAjaxSuccess(response) {
        		$scope.userState.subscriptions[response.data.project.id] = true;
                $scope.userState.projects.push(response.data.project);
                $location.path('/project/' + response.data.project.id + '/section/config');
        	}, FN_AJAX_FAILURE);
        };
        $scope.showPreferences = function() {
        	$('#preferencesBox').modal('show');
        };
        $scope.confirmClearPoints = function(project) {
        	var choice = confirm('Are you sure you want to clear your points on this project? This operation cannot be undone.');
        	if (choice) {
        		$http.delete('api/projects/' + project.id + '/achievements/all/records').then(function onAjaxSuccess() {
        			delete $scope.userState.recordsPromises[project.id];
        			$scope.populateTotalPoints();
        			$scope.$broadcast('clearPointsSuccess');
        	    	$('#alertBoxTitle').html('Success');
        	    	$('#alertBoxBody').html('Project points cleared successfully.');
        	        $('#alertBox').modal('show');
        			
        		}, FN_AJAX_FAILURE);
        	}
        };
        $scope.isSubscribed = function(project) {
        	return $scope.userState.subscriptions[project.id];
        };
        $http.get('api/subscriptions').then(function onAjaxSuccess(response) {
        	$scope.userState.subscriptions = response.data.subscriptions;
        	$scope.$watch('userState.subscriptions', function saveSubscriptions(newValue, oldValue) {
        		$http.post('api/subscriptions', newValue).then(function onAjaxSuccess(response) {
        			
                    $http.get('api/projects').then(function onAjaxSuccess(response) {
                        $scope.userState.projects = response.data.projects;            
                    }, FN_AJAX_FAILURE);
        		}, FN_AJAX_FAILURE);
        	}, true);
        }, FN_AJAX_FAILURE);
        $scope.populateTotalPoints = function() {
            $http.get('api/projects/all/achievements/all/records').then(function onAjaxSuccess(response) {
            	var records = response.data.records;
            	var totalPoints = 0;
            	angular.forEach(records, function doForEachRecord(record, i) {
            		totalPoints += record.points;
            	});
            	$scope.userState.totalPoints = totalPoints;
            }, FN_AJAX_FAILURE);
        };
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
        $scope.populateTotalPoints();
    }]);