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
  
    $scope.fillValues = function() {
        console.log($scope.ctrl.selectedRecipe);
        for (var i = 0, len = $scope.recipes.length; i < len; i++) {
			if($scope.ctrl.selectedRecipe === $scope.recipes[i].name) {
				self.recipe = $scope.recipes[i];
        	}
        }
        console.log(self.recipe);
    }
    
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