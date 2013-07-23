var BASE_URL = "http://localhost:8080/openlegacy-rest-sample/";

(function() {

	'use strict';

	/* Controllers */

	var module = angular.module('controllers', [])

	.controller(
		'loginController',
		function($scope, $location, $olHttp, $rootScope) {
			$scope.login = function() {
				$olHttp.get('login?user=' + $scope.user + '&password='+ $scope.password, 
					function() {
						$rootScope.loggedInUser = $scope.user;
						$location.path("/items");
					}
				);
			};
		})
	.controller('itemsController',
		function($scope, $location, $olHttp) {
			$olHttp.get('items', 
					function(data) {
						$scope.items = data.screenModel.screenEntity.itemsRecords;
					}
				);
			
		})
	.controller('itemDetailsController',
			function($scope, $location, $olHttp,$routeParams) {
				$olHttp.get('itemDetails/' + $routeParams.itemNumber, 
						function(data) {
							$scope.model = data.screenModel.screenEntity;
						}
					);
				
			});
})();
