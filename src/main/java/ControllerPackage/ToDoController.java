package ControllerPackage;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

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
    public void login() throws SQLException{
        //Display Login view
        System.out.println("Logging in...");
        //Get user input for username and password
        //Create PGSimpleDataSource and fill fields with user input
        ds.setUrl("jdbc:postgresql://stoic-cat-3327.g95.cockroachlabs.cloud:26257/ToDoDB?sslmode=verify-full");
        ds.setSslMode( "require" );
        ds.setUser("demo_todo");
        ds.setPassword("oKDnWiZLElJt7pCal8KsDA");
        handler = new CockroachHandler(ds);
        //if (connection is good)
            //Connection Success view
            //proceed
        //else
            //Send error text to Error view and display
            //Allow user to try again (Maybe max # of retries?)
        System.out.println("Success!");
        welcome();
    }

    //Welcome
    public void welcome() throws SQLException{
        //Display Welcome view
        System.out.println("Welcome to the ToDo app.");
        System.out.println();
        //Get priority todos from CockroachHandler
        ToDoBean bean = new ToDoBean();
        bean.setPriority("true");
        ToDoWrapper results = handler.searchByPriority(bean);
        List<ToDoBean> priorities = results.getTodos();
        //if (has priority todos)
            //Send information to Priority view and display
        //else
            //No Priority view(?)
        System.out.println("*Your priority todos*");
        System.out.println();

        for (ToDoBean b : priorities){
            System.out.print(b.getEvent());
            System.out.print(", created ");
            System.out.println(b.getCreated());
        }
        System.out.println();
        userMenu();

    }

    //UserMenu (main repeating part of controller)
    public void userMenu() throws SQLException{
        //Get user todos and metadata from CockroachHandler
        ToDoWrapper wrap = handler.getAll();
        //Send information to ToDo view and display
        int count = 1;
        System.out.println("Your ToDos:");
        for (ToDoBean b : wrap.getTodos()){
            System.out.print(count + ": "); 
            System.out.print(b.getEvent());
            System.out.print(", created ");
            System.out.println(b.getCreated());
            count++;
        }
        System.out.println();
        //Display Menu view
        System.out.print("(R)ead, (C)reate, (U)pdate, (D)elete, (Q)uit?: ");
        //Get user input for what they would like to do (Read, Create, Update, Delete, Quit)
            // *Other activities as program expands*
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        char sel = Character.toUpperCase(input.charAt(0));
        in.close();
        //Parse user input and go to selected activity.
        System.out.println();
        switch(sel){
            case 'R':
                read(wrap);
                break;
            case 'C':
                create();
                break;
            case 'U':
                update(wrap);
                break;
            case 'D':
                delete(wrap);
                break;
            case 'Q':
                quit();
                break;
            default:
                break;
        }
    }
        
    public void read(ToDoWrapper wrap) throws SQLException{
        //Display Select ToDo view
        System.out.print("Select ToDo to read");
        //Get user input on which todo to read
        Scanner in = new Scanner(System.in);
        int input  = in.nextInt();
        in.close();
        System.out.println();
        //if (selection exists)
            //get selection from CoackroachHandler
        ToDoWrapper choice = handler.searchByID(wrap.getTodos().get(input));
            //Send todo to DetailTodo view and display
        System.out.println("Selected ToDo:");
        System.out.print(choice.getTodos().get(0).getEvent());
        System.out.print(", created ");
        System.out.println(choice.getTodos().get(0).getCreated() + ".");
        System.out.println("Notes: " + choice.getTodos().get(0).getNotes());
        System.out.println("Priority: " + choice.getTodos().get(0).getPriority());
        System.out.println();
            // *Maybe allow user to select Update or Delete from here?*
            //return to UserMenu
        System.out.print("(U)pdate, (D)elete, (M)ain menu?: ");
        in = new Scanner(System.in);
        String next = in.nextLine();
        char sel = Character.toUpperCase(next.charAt(0));
        in.close();
        System.out.println();

        switch(sel){
            case 'U':
                update(wrap);
                break;
            case 'D':
                delete(wrap);
                break;
            case 'M':
                userMenu();
                break;
            default:
                break;
        }



        //else
            //Send error test to Error view and display
            //Try again (maybe return to top of method)
    }

    //Update
    public void update(ToDoWrapper wrap){
        //if (single todo is not specified)
            //Display Select ToDo view
        System.out.println("Select ToDo to update");
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
    }

    //Create
        //Display Create Todo view
        //Get user input for fields
        //Bundle new todo and send to CockroachHandler to update DB
        //if (success)
            //Display Successful Transaction view
        //else
            //Send error text to Error view and display
        //return to UserMenu
    public void create(){

    }

    //Delete
        //Display Delete Todo view
        //Get user input for todo to delete
        //Send ID of selection to CockroachHandler
        //if (success)
            //Display Successful Transaction view
        //else
            //Send error text to Error view and display
        //return to UserMenu
    public void delete(ToDoWrapper wrap){

    }

    //Quit
        //Display Closing Program view
        //Close whatever needs closing and exit program
    public void quit(){
        System.out.print("Goodbye");
        //Then returns to userMenu and falls through the switch statements to the end of the method.
    }
}
