package edu.ncsu.csc.selenium;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Tests Edit Recipe functionality.
 *
 * @author aaltaf
 */
public class EditRecipeTest extends SeleniumTest {

    /** The URL for CoffeeMaker - change as needed */
    private WebDriver          driver;
    private String             baseUrl;
    private final StringBuffer verificationErrors = new StringBuffer();

    @Override
    @Before
    protected void setUp () throws Exception {
        super.setUp();

        driver = new HtmlUnitDriver( true );
        baseUrl = "http://localhost:8080";
        driver.manage().timeouts().implicitlyWait( 20, TimeUnit.SECONDS );

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
     * Edit recipe
     *
     * @throws Exception
     *
     */
    private void editRecipe ( final String recipeName, final int price, final int amtCoffee, final int amtMilk,
            final int amtSugar, final int amtChocolate, final String expectedMessage ) throws Exception {
        createRecipe( recipeName, price, amtCoffee, amtMilk, amtSugar, amtChocolate );

        driver.get( baseUrl + "" );
        driver.findElement( By.linkText( "Edit a Recipe" ) ).click();

        selectRecipe( recipeName );

        // Enter the recipe information
        driver.findElement( By.name( "name" ) ).clear();
        driver.findElement( By.name( "name" ) ).sendKeys( "Coffee" );
        driver.findElement( By.name( "price" ) ).clear();
        driver.findElement( By.name( "price" ) ).sendKeys( "50" );
        driver.findElement( By.name( "coffee" ) ).clear();
        driver.findElement( By.name( "coffee" ) ).sendKeys( "3" );
        driver.findElement( By.name( "milk" ) ).clear();
        driver.findElement( By.name( "milk" ) ).sendKeys( "1" );
        driver.findElement( By.name( "sugar" ) ).clear();
        driver.findElement( By.name( "sugar" ) ).sendKeys( "1" );
        driver.findElement( By.name( "chocolate" ) ).clear();
        driver.findElement( By.name( "chocolate" ) ).sendKeys( "0" );

        // Submit the recipe.
        driver.findElement( By.cssSelector( "input[type=\"submit\"]" ) ).click();

        // Make sure the proper message was displayed.
        final String src = driver.getPageSource();
        Assert.assertTrue( src.contains( expectedMessage ) );
    }

    /**
     * Test for editing recipe
     *
     * @throws Exception
     */
    @Test
    public void testEditRecipe () throws Exception {
        editRecipe( "Coffee", 70, 3, 1, 1, 0, "Recipe Edited" );

        // Invalid recipes. Expect to get an error.
        editRecipe( "Coffee", -70, 3, 1, 1, 0, "Error while editing recipe" );
        editRecipe( "Coffee", 70, -3, 1, 1, 0, "Error while editing recipe" );
        editRecipe( "Coffee", 70, 3, -1, 1, 0, "Error while editing recipe" );
        editRecipe( "Coffee", 70, 3, 1, -1, 0, "Error while editing recipe" );
        editRecipe( "Coffee", 70, 3, 1, 1, -4, "Error while editing recipe" );
    }

    /**
     * Invalid edit. Non-numeric price
     */
    @Test
    public void testInvalidRecipe1 () throws Exception {
        driver.get( baseUrl + "" );
        driver.findElement( By.linkText( "Edit a Recipe" ) ).click();

        selectRecipe( "Coffee" );
        // Enter the recipe information
        driver.findElement( By.name( "name" ) ).clear();
        driver.findElement( By.name( "name" ) ).sendKeys( "Coffee" );
        driver.findElement( By.name( "price" ) ).clear();
        driver.findElement( By.name( "price" ) ).sendKeys( "a" );
        driver.findElement( By.name( "coffee" ) ).clear();
        driver.findElement( By.name( "coffee" ) ).sendKeys( "3" );
        driver.findElement( By.name( "milk" ) ).clear();
        driver.findElement( By.name( "milk" ) ).sendKeys( "1" );
        driver.findElement( By.name( "sugar" ) ).clear();
        driver.findElement( By.name( "sugar" ) ).sendKeys( "1" );
        driver.findElement( By.name( "chocolate" ) ).clear();
        driver.findElement( By.name( "chocolate" ) ).sendKeys( "0" );

        // Submit the recipe.
        driver.findElement( By.cssSelector( "input[type=\"submit\"]" ) ).click();

        // Make sure the proper message was displayed.
        final String src = driver.getPageSource();
        Assert.assertTrue( src.contains( "Error while editing recipe" ) );
    }

    /**
     * Invalid edit. Non-numeric coffee
     */
    @Test
    public void testInvalidRecipe2 () throws Exception {
        driver.get( baseUrl + "" );
        driver.findElement( By.linkText( "Edit a Recipe" ) ).click();

        selectRecipe( "Coffee" );
        // Enter the recipe information
        driver.findElement( By.name( "name" ) ).clear();
        driver.findElement( By.name( "name" ) ).sendKeys( "Coffee" );
        driver.findElement( By.name( "price" ) ).clear();
        driver.findElement( By.name( "price" ) ).sendKeys( "6" );
        driver.findElement( By.name( "coffee" ) ).clear();
        driver.findElement( By.name( "coffee" ) ).sendKeys( "a" );
        driver.findElement( By.name( "milk" ) ).clear();
        driver.findElement( By.name( "milk" ) ).sendKeys( "1" );
        driver.findElement( By.name( "sugar" ) ).clear();
        driver.findElement( By.name( "sugar" ) ).sendKeys( "1" );
        driver.findElement( By.name( "chocolate" ) ).clear();
        driver.findElement( By.name( "chocolate" ) ).sendKeys( "0" );

        // Submit the recipe.
        driver.findElement( By.cssSelector( "input[type=\"submit\"]" ) ).click();

        // Make sure the proper message was displayed.
        final String src = driver.getPageSource();
        Assert.assertTrue( src.contains( "Error while editing recipe" ) );
    }

    @Override
    @After
    public void tearDown () throws Exception {
        driver.quit();
        final String verificationErrorString = verificationErrors.toString();
        if ( !"".equals( verificationErrorString ) ) {
            fail( verificationErrorString );
        }
    }

}
