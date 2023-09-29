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

    private PGSimpleDataSource ds;
    private CockroachHandler handler;
    private ToDoView view;
    private TGUI gui;
    private Boolean refreshDB = true;

    private static final int MAX_RETRY_COUNT = 3;

    public ToDoController(){
        ds = new PGSimpleDataSource();
        handler = new CockroachHandler();
        view = new ToDoView();
        gui = new TGUI();
    }

    public void login() throws SQLException{
        view.loginView();

        //Get user input for username and password
        //Create PGSimpleDataSource and fill fields with user input
        ds.setUrl("jdbc:postgresql://stoic-cat-3327.g95.cockroachlabs.cloud:26257/ToDoDB?sslmode=verify-full");
        ds.setSslMode( "require" );
        ds.setUser("demo_todo");
        ds.setPassword("oKDnWiZLElJt7pCal8KsDA");
        int retryCount = 0;
        
        while (retryCount < MAX_RETRY_COUNT){
            try {
                handler = new CockroachHandler(ds);
                view.loginSuccessView();
                welcome();
                //Class returns here when we quit, so has to break out
                //of while loop and travel to end of method
                break;
            } catch (SQLException e) {
                retryCount++;
                view.loginErrorView(e.getMessage());
            }
        }

        if (retryCount == MAX_RETRY_COUNT){
            view.loginFailView();
            quit();
        }
        else{
            //This is where class actully stops running
        }
    }

    /**
     * Creates a bean object and sets that bean's priority to true.
     * This bean is used to call searchByPriority method from the
     * @param handler object. This list of ToDoBeans that have a
     * priority of true are then sent to the welcomeView method to be printed.
     * 
     * @throws SQLException
     */
    
    public void welcome() throws SQLException{
        ToDoBean bean = new ToDoBean();
        bean.setPriority("true");
        ToDoWrapper priorityResults = handler.searchByPriority(bean);
        List<ToDoBean> priorities = priorityResults.getTodos();

        view.welcomeView(priorities);
       
        userMenu();
    }

    public void userMenu() throws SQLException{
        Boolean quitToDo = false;
        //Fuck this. Convert wrap into beans straight from handler call.
        ToDoWrapper wrap = new ToDoWrapper();

        while (!quitToDo){
            if (refreshDB){
                wrap = handler.getAll();
                refreshDB = false;
            }

            List<ToDoBean> beans = wrap.getTodos();

            view.menuView(beans);

            view.menuSelectView();
            switch(gui.getCharSelection()){
                case 'R':
                    read(wrap);
                    break;
                case 'C':
                    create();
                    break;
                case 'U':
                    updateSelect(wrap);
                    break;
                case 'D':
                    deleteSelect(wrap);
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
        view.readView();

        int input  = gui.getNumSelection();
        //if (selection exists)
            //get selection from CoackroachHandler
        ToDoWrapper choice = handler.searchByID(wrap.getTodos().get(input));
            //Send todo to todoView and display
        ToDoBean bean = choice.getTodos().get(0);
        view.todoView(bean);

        view.readSelectView();

        switch(gui.getCharSelection()){
            case 'U':
                update(bean);
                break;
            case 'D':
                delete(bean);
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


    public void updateSelect(ToDoWrapper wrap){
        view.updateView();
        int input  = gui.getNumSelection();
        ToDoBean choice = wrap.getTodos().get(input);
        view.todoView(choice);

        update(choice);
        
    }

    private void update(ToDoBean bean){
        view.updateSelectView();
        char userSelection = gui.getCharSelection();
        view.updateInputView();
        String update = gui.getStringSelection();

        switch(userSelection){
            case 'E':
                bean.setEvent(update);
                break;
            case 'N':
                bean.setNotes(update);
                break;
            case 'P':
                bean.setPriority(update);
                break;
            default:
                break;
        }

        handler.update(bean);
        //if (success)
        this.refreshDB = true;
        view.updateSuccessView();

        //else
            //Send error text to Error view and display
    }

    public void create(){
        view.createView();

        view.createEventView();
        String event = gui.getStringSelection();

        view.createNotesView();
        String notes = gui.getStringSelection();

        view.createPriorityView();
        String priority = new String();
        char priorityChoice = gui.getCharSelection();

        switch(priorityChoice){
            case 'Y':
                priority = "true";
                break;
            case 'N':
                priority = "false";
                break;
            default:
                break;
        }

        ToDoBean bean = new ToDoBean();
        bean.setEvent(event);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now(); 
        bean.setCreated(dtf.format(now));
        bean.setNotes(notes);
        bean.setPriority(priority);
        handler.create(bean);

        //if (success)
        this.refreshDB = true;
        view.createSuccessView();
        
        //else
            //Send error text to Error view and display
        //return to UserMenu
    }

    public void deleteSelect(ToDoWrapper wrap){
        view.deleteView();
        int select = gui.getNumSelection();
        ToDoBean bean = wrap.getTodos().get(select);

        delete(bean);        
    }

    private void delete(ToDoBean bean){
        handler.delete(bean);

        //if (success)
        this.refreshDB = true;
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
