package ControllerPackage;

public class ToDoController {
    
    //Initialize(?)
        //No idea what this looks like yet
            // *Maybe have db url hardcoded or passed by another object*
    //Login
        //Display Login view
        //Get user input for username and password
            //
        //Create PGSimpleDataSource and fill fields with user input
        //if (connection is good)
            //Connection Success view
            //proceed
        //else
            //Send error text to Error view and display
            //Allow user to try again (Maybe max # of retries?)

    //Welcome
        //Display Welcome view
        //Get priority todos from CockroachHandler
        //if (has priority todos)
            //Send information to Priority view and display
        //else
            //No Priority view(?)

    //UserMenu (main repeating part of controller)
        //Get user todos and metadata from CockroachHandler
        //Send information to ToDo view and display
        //Display Menu view
        //Get user input for what they would like to do (Read, Create, Update, Delete, Quit)
            // *Other activities as program expands*
        //Parse user input and go to selected activity.

    //Read
        //Display Select ToDo view
        //Get user input on which todo to read
        //if (selection exists)
            //get selection from CoackroachHandler
            //Send todo to DetailTodo view and display
            // *Maybe allow user to select Update or Delete from here?*
            //return to UserMenu
        //else
            //Send error test to Error view and display
            //Try again (maybe return to top of method)

    //Update
        //if (single todo is not specified)
            //Display Select ToDo view
            //Get user input on which todo to update
            //get selection from CockroachHandler
        //Send todo to DetailTodo view and display
        //Display UpdatdeTodo view
        //Get user input for which field to update
        //Get user input for updated information
        //Bundle updated todo and send to CockroachHandler to update DB
        //if (success)
            //Display Successful Transaction view
        //else
            //Send error text to Error view and display
        //return to UserMenu

    //Create
        //Display Create Todo view
        //Get user input for fields
        //Bundle new todo and send to CockroachHandler to update DB
        //if (success)
            //Display Successful Transaction view
        //else
            //Send error text to Error view and display
        //return to UserMenu

    //Delete
        //Display Delete Todo view
        //Get user input for todo to delete
        //Send ID of selection to CockroachHandler
        //if (success)
            //Display Successful Transaction view
        //else
            //Send error text to Error view and display
        //return to UserMenu

    //Quit
        //Display Closing Program view
        //Close whatever needs closing and exit program
}
