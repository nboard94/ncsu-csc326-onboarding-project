'use strict';
 
angular.module('myApp').factory('EditRecipeService', ['$http', '$q', function($http, $q){
 
	var REST_SERVICE_URI = '/api/v1/recipes';
	
    var factory = {
    		editRecipe: editRecipe,
    };
 
    return factory;
 
    
	
    function editRecipe(recipe) {
        var deferred = $q.defer();
        $http.post(REST_SERVICE_URI, recipe)
            .then(
            function (response) {
                deferred.resolve(response.data);
            },
            function(errResponse){
                console.error('Error while adding recipe');
                deferred.reject(errResponse);
            }
        );
        return deferred.promise;
    } 
    
}]);