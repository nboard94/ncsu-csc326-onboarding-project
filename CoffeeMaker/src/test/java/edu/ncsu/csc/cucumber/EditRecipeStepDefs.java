package edu.ncsu.csc.cucumber;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.coffee_maker.models.CoffeeMaker;
import edu.ncsu.csc.coffee_maker.models.persistent.Recipe;
import edu.ncsu.csc.test_utils.SharedRecipeData;

/**
 * Selenium + Cucumber tests
 *
 * @author Kai Presler-Marshall
 * @author Sarah Elder
 *
 */
public class EditRecipeStepDefs {
    private final CoffeeMaker      coffeeMaker;
    private final SharedRecipeData recipeData;
    private final WebDriver        driver  = new HtmlUnitDriver( true );
    private final String           baseUrl = "http://localhost:8080";

    /**
     * Constructor for the EditRecipeStepDefs. Used to keep track of
     * CoffeeMaker's state to ensure that the test completed successfully.
     *
     * @param coffeemaker
     *            CoffeeMaker object; used to properly unit test it without
     *            Spring
     * @param srd
     *            SharedRecipeData; a backup of the recipes to ensure that the
     *            CoffeeMaker is behaving appropriately.
     */
    public EditRecipeStepDefs ( final CoffeeMaker coffeemaker, final SharedRecipeData srd ) {
        this.coffeeMaker = coffeemaker;
        this.recipeData = srd;
    }

    /**
     * Deletes the existing batch of recipies in the CoffeeMaker and then
     * populates it with a new batch of Recipies.
     *
     * @param numRecipes
     *            The number of recipies that should be in the RecipeBook
     * @throws Exception
     *             If the number of recipies to add is greater than the max
     *             allowed
     */
    @Given ( "^the CoffeeMaker already had (\\d+) Recipes$" )
    public void existingRecipes ( final int numRecipes ) throws Exception {
        // Check current number of recipes in Recipe Book
        final int total = this.coffeeMaker.getRecipes().size();
        if ( total != 0 ) {
            int count = 0;
            while ( count < total ) {
                this.coffeeMaker.deleteRecipe( 0 );
                count++;
            }
            Assert.assertEquals( 0, this.coffeeMaker.getRecipes().size() );
        }

        if ( numRecipes == 0 ) {
            return; // no need to execute the rest of the code
        }
        if ( numRecipes > 3 ) {
            throw new IllegalArgumentException( "Number of Recipes cannot be greater than 3" );
        }
        else {

            for ( int i = 0; i < numRecipes; i++ ) {
                try {
                    final Recipe testR = new Recipe();
                    try {
                        testR.setName( "Recipe" + i );
                        final Integer pr = new Integer( i * 10 );
                        testR.setPrice( pr );
                        testR.setCoffee( new Integer( i ) );
                        testR.setMilk( 1 );
                        testR.setSugar( 1 );
                        testR.setChocolate( 1 );
                    }
                    catch ( final Exception e ) {
                        Assert.fail( "Error in creating recipes" );
                    }
                    recipeData.latestRecipeAdded = this.coffeeMaker.addRecipe( testR );

                }
                catch ( final Exception e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Create a recipe from the valid parameters specified.
     *
     * @param name
     *            Name of the new Recipe
     * @param cost
     *            Cost of the new Recipe
     * @param coffeeAmt
     *            Amount of Coffee needed for the new Recipe
     * @param milkAmt
     *            Amount of Milk needed for the new Recipe
     * @param sugarAmt
     *            Amount of Sugar needed for the new Recipe
     * @param chocolateAmt
     *            Amount of Chocolate needed for the new Recipe
     */
    @When ( "^I submitted valid values for name: (.+); cost: (\\d+); and ingredients: (\\d+) coffee, (\\d+) milk, (\\d+) sugar, (\\d+) chocolate$" )
    public void addSpecificRecipe ( final String name, final int cost, final int coffeeAmt, final int milkAmt,
            final int sugarAmt, final int chocolateAmt ) {
        recipeData.recipeError = "";
        recipeData.latestRecipeAdded = false;
        final Recipe newR = new Recipe();
        try {

            newR.setName( name );
            newR.setPrice( cost );
            newR.setCoffee( coffeeAmt );
            newR.setMilk( milkAmt );
            newR.setSugar( sugarAmt );
            newR.setChocolate( chocolateAmt );
        }
        catch ( final Exception e ) {
            recipeData.recipeError = e.getMessage();
            Assert.fail( "Unable to create new Recipe" );
        }
        recipeData.currentRecipe = newR;
        final boolean tempCheck = this.coffeeMaker.addRecipe( newR );
        recipeData.latestRecipeAdded = tempCheck;
    }

    /**
     * Helper to create a recipe to make
     *
     * @return the name of the recipe
     * @throws Exception
     *             if there was an issue in submitting the recipe
     */
    private void createRecipe ( final String name, final int price, final int amtCoffee, final int amtMilk,
            final int amtSugar, final int amtChocolate ) throws Exception {
        driver.get( baseUrl + "" );
        driver.findElement( By.linkText( "Add a Recipe" ) ).click();

        // Enter the recipe information
        final String recipeName = "Coffee";
        driver.findElement( By.name( "name" ) ).clear();
        driver.findElement( By.name( "name" ) ).sendKeys( recipeName );
        driver.findElement( By.name( "price" ) ).clear();
        driver.findElement( By.name( "price" ) ).sendKeys( price + "" );
        driver.findElement( By.name( "coffee" ) ).clear();
        driver.findElement( By.name( "coffee" ) ).sendKeys( amtCoffee + "" );
        driver.findElement( By.name( "milk" ) ).clear();
        driver.findElement( By.name( "milk" ) ).sendKeys( amtMilk + "" );
        driver.findElement( By.name( "sugar" ) ).clear();
        driver.findElement( By.name( "sugar" ) ).sendKeys( amtSugar + "" );
        driver.findElement( By.name( "chocolate" ) ).clear();
        driver.findElement( By.name( "chocolate" ) ).sendKeys( amtChocolate + "" );

        // Submit the recipe.
        driver.findElement( By.cssSelector( "input[type=\"submit\"]" ) ).click();
        Thread.sleep( 5000 );

        // Make sure the proper message was displayed.
        assertTrue( driver.getPageSource().contains( "Recipe Created" ) );
    }

    /**
     * Looks through the list of available recipes and selects the specified
     * recipe
     *
     * @param name
     * @return true if found and selected, false if not
     * @throws InterruptedException
     */
    private boolean selectRecipe ( final String name ) throws InterruptedException {
        final List<WebElement> list = driver.findElements( By.name( "name" ) );
        Thread.sleep( 5000 );

        // Select the recipe
        for ( final WebElement we : list ) {
            if ( name.equals( we.getAttribute( "value" ) ) ) {
                we.click();
                // Wait for thread to perform operation
                while ( !we.isSelected() ) {
                    Thread.sleep( 5000 );
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Edit the most recently referenced recipe and give it the new parameters
     * provided
     *
     * @param cost
     *            New cost of the Recipe
     * @param coffeeAmt
     *            New amount of Coffee needed for the Recipe
     * @param milkAmt
     *            New amount of Milk needed for the Recipe
     * @param sugarAmt
     *            New amount of Sugar needed for the Recipe
     * @param chocolateAmt
     *            New amount of Chocolate needed for the Recipe
     */
    @When ( "^I edited that recipe to have cost: (\\d+); and ingredients: (\\d+) coffee, (\\d+) milk, (\\d+) sugar, (\\d+) chocolate$" )
    private void editRecipe ( final int price, final int amtCoffee, final int amtMilk, final int amtSugar,
            final int amtChocolate ) throws Exception {
        createRecipe( "Coffee", price, amtCoffee, amtMilk, amtSugar, amtChocolate );

        driver.get( baseUrl + "" );
        driver.findElement( By.linkText( "Edit a Recipe" ) ).click();

        selectRecipe( "Coffee" );

        // Enter the recipe information
        driver.findElement( By.name( "name" ) ).clear();
        driver.findElement( By.name( "name" ) ).sendKeys( "Coffee" );
        driver.findElement( By.name( "price" ) ).clear();
        driver.findElement( By.name( "price" ) ).sendKeys( price + "" );
        driver.findElement( By.name( "coffee" ) ).clear();
        driver.findElement( By.name( "coffee" ) ).sendKeys( amtCoffee + "" );
        driver.findElement( By.name( "milk" ) ).clear();
        driver.findElement( By.name( "milk" ) ).sendKeys( amtMilk + "" );
        driver.findElement( By.name( "sugar" ) ).clear();
        driver.findElement( By.name( "sugar" ) ).sendKeys( amtSugar + "" );
        driver.findElement( By.name( "chocolate" ) ).clear();
        driver.findElement( By.name( "chocolate" ) ).sendKeys( amtChocolate + "" );

        // Submit the recipe.
        driver.findElement( By.cssSelector( "input[type=\"submit\"]" ) ).click();

        // Make sure the proper message was displayed.
        final String src = driver.getPageSource();
        Assert.assertTrue( src.contains( "Recipe Edited" ) );
    }

    /**
     * Unsuccessfully attempt to edit the most recently referenced recipe with
     * the new parameters provided.
     *
     * @param cost
     *            New cost of the Recipe
     * @param coffeeAmt
     *            New amount of Coffee needed for the Recipe
     * @param milkAmt
     *            New amount of Milk needed for the Recipe
     * @param sugarAmt
     *            New amount of Sugar needed for the Recipe
     * @param chocolateAmt
     *            New amount of Chocolate needed for the Recipe
     */
    @When ( "^I invalidly editted that recipe to have cost: (.+); and ingredients: (.+) coffee, (.+) milk, (.+) sugar, (.+) chocolate$" )
    public void invalidEditRecipe ( final String cost, final String coffeeAmt, final String milkAmt,
            final String sugarAmt, final String chocolateAmt ) throws Exception {

        driver.get( baseUrl + "" );
        driver.findElement( By.linkText( "Edit a Recipe" ) ).click();

        selectRecipe( "Coffee" );
        // Enter the recipe information
        driver.findElement( By.name( "name" ) ).clear();
        driver.findElement( By.name( "name" ) ).sendKeys( "Coffee" );
        driver.findElement( By.name( "price" ) ).clear();
        driver.findElement( By.name( "price" ) ).sendKeys( cost );
        driver.findElement( By.name( "coffee" ) ).clear();
        driver.findElement( By.name( "coffee" ) ).sendKeys( coffeeAmt );
        driver.findElement( By.name( "milk" ) ).clear();
        driver.findElement( By.name( "milk" ) ).sendKeys( milkAmt );
        driver.findElement( By.name( "sugar" ) ).clear();
        driver.findElement( By.name( "sugar" ) ).sendKeys( sugarAmt );
        driver.findElement( By.name( "chocolate" ) ).clear();
        driver.findElement( By.name( "chocolate" ) ).sendKeys( chocolateAmt );

        // Submit the recipe.
        driver.findElement( By.cssSelector( "input[type=\"submit\"]" ) ).click();

        // Make sure the proper message was displayed.
        final String src = driver.getPageSource();
        Assert.assertTrue( src.contains( "Error while editing recipe" ) );
    }

    /**
     * Ensure that a recipe was _not_ edited as expected by comparing the actual
     * results in the CoffeeMaker to what is stored in the backup
     */
    @Then ( "^the recipe was not edited$" )
    public void recipeUnsuccessfullyEdited () {
        final Recipe current = recipeData.currentRecipe;
        final Recipe other = coffeeMaker.getRecipes().get( recipeData.index );
        Assert.assertNotEquals( "Recipe was edited when it shouldn't have been", current, other );
    }

    /**
     * Ensure that a recipe was edited as expected by comparing the actual
     * results in the CoffeeMaker to what is stored in the backup
     */
    @Then ( "^the recipe was successfully edited$" )
    public void recipeSuccessfullyEdited () {
        Assert.assertEquals( "Recipe was not edited correctly", recipeData.currentRecipe,
                coffeeMaker.getRecipes().get( recipeData.index ) );
    }
}
