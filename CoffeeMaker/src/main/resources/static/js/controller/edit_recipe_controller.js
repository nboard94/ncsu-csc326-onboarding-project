'use strict';
 
angular.module('myApp').controller('EditRecipeController', ['$scope', 'EditRecipeService', '$http', function($scope, EditRecipeService, $http) {
    var self = this;
    self.recipe={name:'', price:'', coffee:'', milk:'', sugar:'', chocolate:''};
 
    self.submit = submit;
    self.reset = reset;
 

    function updateRecipes() {
		$http.get('/api/v1/recipes').then(function (response) {
				$scope.recipes = response.data;
			});
	}
    
    updateRecipes();
    
    
    function editRecipe(recipe){
    		$scope.success = false;
    		$scope.failure = false;
    		
    		EditRecipeService.editRecipe(recipe)
            	.then(
            		$scope.success = true,
            function(errResponse){
            		$scope.failure = true;
            		$scope.success = false;
                console.error('Error while editing recipe');
            }
        );
    		
    		$scope.success = !($scope.failure);
    }
 
    function submit() {
    		editRecipe(self.recipe);
        
        reset();
    }
 	
 
    function reset(){
    		self.recipe={name:'', price:'', coffee:'', milk:'', sugar:'', chocolate:''};
        $scope.editRecipeForm.$setPristine(); //reset Form
    }
 
}]);