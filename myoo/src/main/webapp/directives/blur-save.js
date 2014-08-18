angular.module('myooApp')
	.directive('blurSave', function($http, FN_AJAX_FAILURE) {
		return {
			link : function(scope, element, attrs) {
				element.on('blur', function() {
					if (attrs.blurSave && attrs.blurSavePostData) {
						var url = scope.$eval(attrs.blurSave),
							postData = scope.$eval(attrs.blurSavePostData);
						$http.put(url, postData, {
							
						}).then(function onAjaxSuccess(response) {
							var responseData = response.data,
								update = {};
							for (var key in responseData) {
								update = responseData[key];
							}
							postData.lastUpdatedBy = update.lastUpdatedBy;
							postData.lastUpdatedTs = update.lastUpdatedTs;
						}, FN_AJAX_FAILURE);
					} else {
						console.error('blur-save directive requires blur-save and blur-save-post-data attributes');
					}
				})
			}
		};
	});