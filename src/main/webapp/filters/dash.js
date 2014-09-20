angular.module('myooApp')
	.filter('dash', function() {
		return function(data, dash) {
			if (angular.isUndefined(data) || data === null) {
				return dash || '--';
			} else if (angular.isString(data)) {
				if (data.length === 0) {
					return dash || '--';
				} else {
					return data;
				}
			} else {
				return data;
			}
		};
	})