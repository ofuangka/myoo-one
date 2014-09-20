angular.module('myooApp')
	.directive('saveOnEvent', function($log, $http, FN_AJAX_FAILURE) {
		return {
			link : function(scope, element, attrs) {
				var event = attrs.saveOnEvent,
					saveUrl = attrs.saveUrl,
					savePostData = attrs.savePostData;
				if (event) {
					element.on(event, function() {

						if (saveUrl && savePostData) {
							var url = scope.$eval(saveUrl),
								postData = scope.$eval(savePostData);
							$http.put(url, postData, {
								
							}).then(function onAjaxSuccess(response) {
								var responseData = response.data,
									update = {};
								
								// assume that the response is in the form { key : { lastUpdatedBy, lastUpdatedTs } }
								for (var key in responseData) {
									update = responseData[key];
								}
								postData.lastUpdatedBy = update.lastUpdatedBy;
								postData.lastUpdatedTs = update.lastUpdatedTs;
							}, FN_AJAX_FAILURE);
						} else {
							console.error('save-on-event requires save-url and save-post-data attributes');
						}
					});
				} else {
					$log.error('save-on-event requires save-on-event attribute')
				}
			}
		};
	});