angular.module('myooApp').directive('badgeSelector', function($http, FN_AJAX_FAILURE) {
	var linkFn = function(scope, element, attrs) {
		var width = attrs.width, 
			height = attrs.height, 
			relX = scope.$eval(attrs.relX), 
			relY = scope.$eval(attrs.relY), 
			url = scope.$eval(attrs.saveUrl),
			badgeSelector = attrs.badgeSelector;
		if (angular.isDefined(width) && angular.isDefined(height) && angular.isDefined(relX) && angular.isDefined(relY)
				&& angular.isDefined(url) && angular.isDefined(badgeSelector)) {
			var absX = width * relX, 
				absY = height * relY,
				spanEl = element.find('span');
			spanEl.css('background-position-x', absX + 'px');
			spanEl.css('background-position-y', absY + 'px');
			element.on('click', function(ev) {
				var postData = scope.$eval(badgeSelector);
				postData.backgroundPositionX = absX;
				postData.backgroundPositionY = absY;
				$http.put(url, postData).then(function onAjaxSuccess(response) {
					var responseData = response.data,
					update = {};
					
					// assume that the response is in the form { key : { lastUpdatedBy, lastUpdatedTs } }
					for (var key in responseData) {
						update = responseData[key];
					}
					postData.lastUpdatedBy = update.lastUpdatedBy;
					postData.lastUpdatedTs = update.lastUpdatedTs;
					scope.$emit("badgeSelectorChangeSuccess");
				});
			});
		} else {
			console.error('badgeSelector unable to render - missing arguments');
		}
	};
	return {
		compile : function(element, attrs) {
			element.addClass('badgeSelector');
			element.html('<span class="achievementIcon"></span>');
			return linkFn;
		}
	};
});