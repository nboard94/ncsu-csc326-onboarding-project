package edu.ncsu.csc.coffee_maker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class for the index (or front page) of CoffeeMaker. The controller
 * returns editrecipe.html in the /src/main/resources/templates folder.
 *
 * @author Nick Board
 */
@Controller
public class EditRecipeController {

    /**
     * On a GET request to /editrecipe, the IndexController will return
     * /src/main/resources/templates/editrecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( "/editrecipe" )
    public String index ( final Model model ) {
        return "editrecipe";
    }
}
