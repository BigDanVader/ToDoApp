package ControllerPackage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.postgresql.ds.PGSimpleDataSource;

import JavaBeanPackage.ToDoBean;
import ServicePackage.CockroachHandler;
import TGUIPackage.TGUI;
import ViewPackage.ToDoView;
import WrapperPackage.ToDoWrapper;

public class ToDoController {

    private PGSimpleDataSource ds = new PGSimpleDataSource();
    private CockroachHandler handler = new CockroachHandler(ds);
    private ToDoView view = new ToDoView();
    private TGUI gui = new TGUI();
    
    //Initialize(?)
        //No idea what this looks like yet
            // *Maybe have db url hardcoded or passed by another object*

    public ToDoController(){
        
    }

    public void login() throws SQLException{
        //Display Login view
        view.loginView();

        //Get user input for username and password
        //Create PGSimpleDataSource and fill fields with user input
        ds.setUrl("jdbc:postgresql://stoic-cat-3327.g95.cockroachlabs.cloud:26257/ToDoDB?sslmode=verify-full");
        ds.setSslMode( "require" );
        ds.setUser("demo_todo");
        ds.setPassword("oKDnWiZLElJt7pCal8KsDA");
        handler = new CockroachHandler(ds);

        //if (connection is good)
        view.loginSuccessView();
        welcome();
        //else
            //Send error text to Error view and display
            //Allow user to try again (Maybe max # of retries?)
    }

    public void welcome() throws SQLException{
        //Get priority todos from CockroachHandler
        ToDoBean bean = new ToDoBean();
        bean.setPriority("true");
        ToDoWrapper results = handler.searchByPriority(bean);
        List<ToDoBean> priorities = results.getTodos();

        view.welcomeView(priorities);
       
        userMenu();
    }

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

            view.menuView(wrap.getTodos());

            view.menuSelectView();
            switch(gui.getCharSelection()){
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
        view.readView();

        //Get user input on which todo to read
        int input  = gui.getNumSelection();

        //if (selection exists)
            //get selection from CoackroachHandler
        ToDoWrapper choice = handler.searchByID(wrap.getTodos().get(input));
            //Send todo to DetailTodo view and display
        view.todoView(choice.getTodos().get(0));

            // Allow user to update or delete the ToDo, or return to main menu
        view.readSelectView();

        switch(gui.getCharSelection()){
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

    //Need overloaded method for when passing a selection from read()
    public void update(ToDoWrapper wrap){
        //if (single todo is not specified)
            //Display Select ToDo view
        view.updateView();

            //Get user input on which todo to update
        int input  = gui.getNumSelection();

            //get selection from CockroachHandler
        ToDoBean choice = wrap.getTodos().get(input);

        //Send todo to DetailTodo view and display
        view.todoView(choice);

        //Display UpdatdeTodo view
        view.updateSelectView();

        //Get user input for which field to update
        char sel = gui.getCharSelection();

        //Get user input for updated information
        view.updateInputView();
        String update = gui.getStringSelection();

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
        handler.update(choice);
        //if (success)
        view.updateSuccessView();

        //else
            //Send error text to Error view and display
    }

    public void create(){
        //Display Create Todo view
        view.createView();

        //Get user input for fields
        view.createEventView();
        String event = gui.getStringSelection();

        view.createNotesView();
        String notes = gui.getStringSelection();

        view.createPriorityView();
        String priority = "";
        char pri = gui.getCharSelection();

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
        view.createSuccessView();
        
        //else
            //Send error text to Error view and display
        //return to UserMenu
    }

    //Need overloaded method for when passing a selection from read()
    public void delete(ToDoWrapper wrap){
        //Display Delete Todo view
        view.deleteView();

        //Get user input for todo to delete
        int select = gui.getNumSelection();

        //Send ID of selection to CockroachHandler
        ToDoBean bean = wrap.getTodos().get(select);
        handler.delete(bean);

        //if (success)
            //Display Successful Transaction view
        view.deleteSuccessView();

        //else
            //Send error text to Error view and display
        //return to UserMenu
    }

    public void quit(){
        view.quitView();
        gui.close();
        //Then returns to userMenu and falls through the switch statements to the end of the method.
    }
}
