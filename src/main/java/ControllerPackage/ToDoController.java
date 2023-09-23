package ControllerPackage;

import java.sql.SQLException;
import java.util.List;

import org.postgresql.ds.PGSimpleDataSource;

import JavaBeanPackage.ToDoBean;
import ServicePackage.CockroachHandler;
import WrapperPackage.ToDoWrapper;

public class ToDoController {

    private PGSimpleDataSource ds = new PGSimpleDataSource();
    private CockroachHandler handler = new CockroachHandler(ds);
    
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
    public void login(){
        ds.setUrl("jdbc:postgresql://stoic-cat-3327.g95.cockroachlabs.cloud:26257/ToDoDB?sslmode=verify-full");
        ds.setSslMode( "require" );
        ds.setUser("demo_todo");
        ds.setPassword("oKDnWiZLElJt7pCal8KsDA");
        handler = new CockroachHandler(ds);
    }

    //Welcome
        //Display Welcome view
        //Get priority todos from CockroachHandler
        //if (has priority todos)
            //Send information to Priority view and display
        //else
            //No Priority view(?)
    public void welcome() throws SQLException{
        System.out.println("Welcome to the ToDo app.");
        ToDoBean bean = new ToDoBean();
        bean.setPriority("true");
        ToDoWrapper results = handler.searchByPriority(bean);
        List<ToDoBean> priorities = results.getTodos();
        System.out.println("*Your priority todos*");

        for (ToDoBean b : priorities){
            System.out.print(b.getEvent());
            System.out.print(", created ");
            System.out.print(b.getCreated());
        }

    }

    //UserMenu (main repeating part of controller)
        //Get user todos and metadata from CockroachHandler
        //Send information to ToDo view and display
        //Display Menu view
        //Get user input for what they would like to do (Read, Create, Update, Delete, Quit)
            // *Other activities as program expands*
        //Parse user input and go to selected activity.
    public void userMenu(){
        
    }

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
