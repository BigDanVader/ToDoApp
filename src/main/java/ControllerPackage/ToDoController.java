package ControllerPackage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.postgresql.ds.PGSimpleDataSource;

import BuilderPackage.DSBuilder;
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
    private List<ToDoBean> beans;

    private static final int MAX_RETRY_COUNT = 3;

    public ToDoController(){
        ds = new PGSimpleDataSource();
        handler = new CockroachHandler();
        view = new ToDoView();
        gui = new TGUI();
    }

    public void start() throws SQLException, IOException{
        //Maybe move initial ds construction and testing here
        //Then do user login at login
        login();
    }

    private void login() throws SQLException, IOException{
        view.loginView();

        //Get user input for username and password
        //Create PGSimpleDataSource and fill fields with user input
        DSBuilder builder = new DSBuilder();
        this.ds = builder.buildDS();
        int retryCount = 0;
        
        while (retryCount < MAX_RETRY_COUNT){
            try {
                this.handler = new CockroachHandler(ds);
                view.loginSuccessView();
                userMenu();
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
    
    private void welcome() throws SQLException{
        ToDoBean bean = new ToDoBean();
        bean.setPriority("true");
        ToDoWrapper priorityResults = this.handler.searchByPriority(bean);
        List<ToDoBean> priorities = priorityResults.getTodos();

        view.welcomeView(priorities);
    }

    private void userMenu() throws SQLException{
        welcome();
        Boolean quitToDo = false;

        while (!quitToDo){
            if (this.refreshDB){
                this.beans = this.handler.getAll().getTodos();
                refreshDB = false;
            }

            view.menuView(this.beans);

            view.menuSelectView();
            char userSelection = this.gui.getCharSelection();
            switch(userSelection){
                case 'R':
                    read();
                    break;
                case 'C':
                    create();
                    break;
                case 'U':
                    updateSelect();
                    break;
                case 'D':
                    deleteSelect();
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
        
    private void read() throws SQLException{
        view.readView();

        int input  = gui.getNumSelection();
        //if (selection doesnt exist)
            //Display error and have user try again or crash back to main menu
        ToDoWrapper choice = this.handler.searchByID(this.beans.get(input));
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
    }


    private void updateSelect(){
        view.updateView();
        int input  = this.gui.getNumSelection();
        ToDoBean choice = this.beans.get(input);
        view.todoView(choice);

        update(choice);
        
    }

    private void update(ToDoBean bean){
        view.updateSelectView();
        char userSelection = this.gui.getCharSelection();
        view.updateInputView();
        String update = this.gui.getStringSelection();

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

        this.handler.update(bean);
        //if (success)
        this.refreshDB = true;
        view.updateSuccessView();

        //else
            //Send error text to Error view and display
            //return to user menu
    }

    private void create(){
        view.createView();

        view.createEventView();
        String event = this.gui.getStringSelection();

        view.createNotesView();
        String notes = this.gui.getStringSelection();

        view.createPriorityView();
        String priority = new String();
        char priorityChoice = this.gui.getCharSelection();

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
        this.handler.create(bean);

        //if (success)
        this.refreshDB = true;
        view.createSuccessView();
        
        //else
            //Send error text to Error view and display
        //return to UserMenu
    }

    private void deleteSelect(){
        view.deleteView();
        int select = this.gui.getNumSelection();
        ToDoBean bean = this.beans.get(select);

        delete(bean);        
    }

    private void delete(ToDoBean bean){
        this.handler.delete(bean);

        //if (success)
        this.refreshDB = true;
        view.deleteSuccessView();

        //else
            //Send error text to Error view and display
        //return to UserMenu
    }

    private void quit(){
        view.quitView();
        this.gui.close();
        //Then returns to userMenu and falls through the switch statements to the end of the method.
    }
}
