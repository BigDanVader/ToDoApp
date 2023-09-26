package ControllerPackage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import org.postgresql.ds.PGSimpleDataSource;

import JavaBeanPackage.ToDoBean;
import ServicePackage.CockroachHandler;
import WrapperPackage.ToDoWrapper;

public class ToDoController {

    private PGSimpleDataSource ds = new PGSimpleDataSource();
    private CockroachHandler handler = new CockroachHandler(ds);
    private Scanner in = new Scanner(System.in);
    
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
        //Set a couple boolean values to store if db was changed
        //or user doesnt want to continue
        Boolean quitToDo = false, refreshDB = true;
        ToDoWrapper wrap = new ToDoWrapper();

        while (!quitToDo){
            //Get user todos and metadata from CockroachHandler
            if (refreshDB){
                wrap = handler.getAll();
                refreshDB = false;
            }

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

            //Get user input for what they would like to do
                // *Other activities as program expands*
            in = new Scanner(System.in);
            String input = in.nextLine();
            char sel = Character.toUpperCase(input.charAt(0));

            //Parse user input and go to selected activity.
            System.out.println();
            switch(sel){
                case 'R':
                    read(wrap);
                    break;
                case 'C':
                    create();
                    refreshDB = true;
                    break;
                case 'U':
                    update(wrap);
                    refreshDB = true;
                    break;
                case 'D':
                    delete(wrap);
                    refreshDB = true;
                    break;
                case 'Q':
                    quit();
                    quitToDo = true;
                    break;
                default:
                    break;
            }
        }
    }
        
    public void read(ToDoWrapper wrap) throws SQLException{
        //Display Select ToDo view
        System.out.print("Select ToDo to read: ");

        //Get user input on which todo to read
        in = new Scanner(System.in);
            //input - 1 to get actual selection
        int input  = in.nextInt() - 1; 
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

            // Allow user to update or delete the ToDo, or return to main menu
        System.out.print("(U)pdate, (D)elete, (M)ain menu?: ");
        in = new Scanner(System.in);
        String next = in.nextLine();
        char sel = Character.toUpperCase(next.charAt(0));
        System.out.println();

        switch(sel){
            case 'U':
                update(wrap);
                break;
            case 'D':
                delete(wrap);
                break;
            case 'M':
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
        System.out.print("Select ToDo to update: ");

            //Get user input on which todo to update
        in = new Scanner(System.in);
                //input - 1 to get actual selection
        int input  = in.nextInt() - 1;
        System.out.println();

            //get selection from CockroachHandler
        ToDoBean choice = wrap.getTodos().get(input);

        //Send todo to DetailTodo view and display
        System.out.println("Event: " + choice.getEvent());
        System.out.println("Notes: " + choice.getNotes());
        System.out.println("Priority: " + choice.getPriority());
        System.out.println();

        //Display UpdatdeTodo view
        System.out.print("Update (Event), (N)otes, (P)riority?: ");

        //Get user input for which field to update
        in = new Scanner(System.in);
        String next = in.nextLine();
        char sel = Character.toUpperCase(next.charAt(0));
        System.out.println();

        //Get user input for updated information
        System.out.println("Enter update:");
        in = new Scanner(System.in);
        String update = in.nextLine();
        System.out.println();

        //Bundle updated todo and send to CockroachHandler to update DB
        switch(sel){
            case 'E':
                choice.setEvent(update);
                break;
            case 'N':
                choice.setNotes(update);
                break;
            case 'P':
                choice.setPriority(update);
                break;
            default:
                break;
        }

        //if (success)
            //Display Successful Transaction view
        System.out.println("Update successful!");

        //else
            //Send error text to Error view and display
        //return to UserMenu
    }

    //Create
    public void create(){
        //Display Create Todo view
        System.out.println("Create new ToDo...");

        //Get user input for fields
        System.out.print("New event: ");
        in = new Scanner(System.in);
        String event = in.nextLine();
        System.out.println();
        System.out.print("Notes: ");
        String notes = in.nextLine();
        System.out.println();
        System.out.print("Make priority, (Y)es or (N)o: ");
        String priority = in.nextLine();
        char pri = Character.toUpperCase(priority.charAt(0));
        System.out.println();
        switch(pri){
            case 'Y':
                priority = "true";
                break;
            case 'N':
                priority = "false";
                break;
            default:
                break;
        }

        //Bundle new todo and send to CockroachHandler to update DB
        ToDoBean bean = new ToDoBean();
        bean.setEvent(event);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now(); 
        bean.setCreated(dtf.format(now));
        bean.setNotes(notes);
        bean.setPriority(priority);
        handler.create(bean);

        //if (success)
            //Display Successful Transaction view
        System.out.println("Added ToDo!");
        System.out.println();
        //else
            //Send error text to Error view and display
        //return to UserMenu
    }

    //Delete
    public void delete(ToDoWrapper wrap){
        //Display Delete Todo view
        System.out.print("Select ToDo to delete: ");

        //Get user input for todo to delete
        in = new Scanner(System.in);
        int select = in.nextInt() - 1;
        System.out.println();

        //Send ID of selection to CockroachHandler
        ToDoBean bean = wrap.getTodos().get(select);
        handler.delete(bean);

        //if (success)
            //Display Successful Transaction view
        System.out.println("ToDo deleted!");
        System.out.println();

        //else
            //Send error text to Error view and display
        //return to UserMenu
    }

    //Quit
        //Display Closing Program view
        //Close whatever needs closing and exit program
    public void quit(){
        System.out.print("Goodbye");
        System.out.println();
        in.close();
        //Then returns to userMenu and falls through the switch statements to the end of the method.
    }
}
