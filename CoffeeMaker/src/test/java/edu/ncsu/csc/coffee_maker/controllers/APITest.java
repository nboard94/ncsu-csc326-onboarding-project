package edu.ncsu.csc.coffee_maker.controllers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;

import edu.ncsu.csc.coffee_maker.models.persistent.Inventory;
import edu.ncsu.csc.coffee_maker.models.persistent.Recipe;

/**
 * Perform a quick check of one of the API methods to ensure that the API
 * controller is up and receiving requests as it should be
 *
 * @author Kai Presler-Marshall
 *
 */
@RunWith ( SpringRunner.class )
@SpringBootTest ( properties = "logging.level.org.springframework.web=DEBUG" )
@AutoConfigureMockMvc
public class APITest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Tests that we are able to make a call to the REST API. If such a call
     * cannot be made, throws an exception instead
     *
     * @throws Exception
     */
    @Test
    public void testApi () throws Exception {
        /*
         * Verify that we are able to make a request to the Inventory API
         * endpoint and that we get a 400 (OK) status in return
         */
        mvc.perform( get( "/api/v1/inventory" ) ).andExpect( status().isOk() );
    }

    /**
     * Test updating inventory
     */
    @Test
    public void testUpdateInventory () throws Exception {
        final Inventory inventory = new Inventory( 1, 1, 1, 1 );
        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( new Gson().toJson( inventory ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.coffee", is( 16 ) ) ).andExpect( jsonPath( "$.milk", is( 16 ) ) )
                .andExpect( jsonPath( "$.sugar", is( 16 ) ) ).andExpect( jsonPath( "$.chocolate", is( 16 ) ) );
    }

    /**
     * Test adding recipes
     *
     * @throws Exception
     */
    @Test
    public void testAddRecipes () throws Exception {
        final Recipe recipe = new Recipe();
        recipe.setChocolate( 6 );
        recipe.setCoffee( 6 );
        recipe.setMilk( 6 );
        recipe.setName( "mocha" );
        recipe.setPrice( 6 );
        recipe.setSugar( 6 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( new Gson().toJson( recipe ) ) ).andExpect( status().isOk() );

        // adding a recipe with the same name as an existing recipe - error
        final Recipe duplicate = new Recipe();
        duplicate.setChocolate( 6 );
        duplicate.setCoffee( 6 );
        duplicate.setMilk( 6 );
        duplicate.setName( "mocha" );
        duplicate.setPrice( 6 );
        duplicate.setSugar( 6 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( new Gson().toJson( duplicate ) ) ).andExpect( status().is4xxClientError() );

        final Recipe recipe2 = new Recipe();
        recipe2.setChocolate( 6 );
        recipe2.setCoffee( 6 );
        recipe2.setMilk( 6 );
        recipe2.setName( "cappuccino" );
        recipe2.setPrice( 6 );
        recipe2.setSugar( 6 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( new Gson().toJson( recipe2 ) ) ).andExpect( status().isOk() );

        final Recipe recipe3 = new Recipe();
        recipe3.setChocolate( 6 );
        recipe3.setCoffee( 6 );
        recipe3.setMilk( 6 );
        recipe3.setName( "espresso" );
        recipe3.setPrice( 6 );
        recipe3.setSugar( 6 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( new Gson().toJson( recipe3 ) ) ).andExpect( status().isOk() );

        // adding a 4th recipe - error
        final Recipe recipe4 = new Recipe();
        recipe4.setChocolate( 6 );
        recipe4.setCoffee( 6 );
        recipe4.setMilk( 6 );
        recipe4.setName( "a" );
        recipe4.setPrice( 6 );
        recipe4.setSugar( 6 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( new Gson().toJson( recipe4 ) ) ).andExpect( status().is5xxServerError() );

        // adding an invalid recipe - error
        final Recipe invalid = new Recipe();
        invalid.setChocolate( -6 );
        invalid.setCoffee( 6 );
        invalid.setMilk( 6 );
        invalid.setName( "latte" );
        invalid.setPrice( 6 );
        invalid.setSugar( 6 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( new Gson().toJson( invalid ) ) ).andExpect( status().is5xxServerError() );

        final Recipe nullRecipe = new Recipe();
        mvc.perform( put( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( new Gson().toJson( nullRecipe ) ) ).andExpect( status().is4xxClientError() );
    }

    /**
     * Test deleting recipe
     *
     * @throws Exception
     */
    @Test
    public void testDeleteRecipe () throws Exception {

    }
}
