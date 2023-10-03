package ControllerPackage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.postgresql.ds.PGSimpleDataSource;

import BuilderPackage.DSBuilder;
import JavaBeanPackage.ToDoBean;
import ServicePackage.CockroachHandler;
import TGUIPackage.TGUI;
import ViewPackage.ToDoView;
import WrapperPackage.ToDoWrapper;

/**
 * The <code>ToDoController</code> class connects to a CockroachDB database, provides views of the todos
 * in that database through a console GUI, and allows for basic CRUD operations to be performed on the
 * connected database.
 * 
 * @author Dan Luoma
 * @since 2023-10-03
 */
public class ToDoController {
    private static final Logger LOGGER = Logger.getLogger(ToDoController.class.getName());

    private PGSimpleDataSource ds;
    private CockroachHandler handler;
    private ToDoView view;
    private TGUI gui;
    private List<ToDoBean> beans;
    private Boolean refreshDB = true;


    private static final int MAX_RETRY_COUNT = 3;

    public ToDoController(){
        ds = new PGSimpleDataSource();
        handler = new CockroachHandler();
        view = new ToDoView();
        gui = new TGUI();
    }

    /**
     * This starts the business logic of the program, calling the various private methods
     * in the class until the user decides to quit the program.
     */
    public void start(){
        login();
    }

    private void login(){
        view.loginView();

        DSBuilder builder = new DSBuilder();
        try {
            this.ds = builder.buildDS();
            int retryCount = 0;
        
            while (retryCount < MAX_RETRY_COUNT){
                try {
                    this.handler = new CockroachHandler(ds);
                    view.loginSuccessView();
                    welcome();
                } catch (SQLException e) {
                    retryCount++;
                    LOGGER.log(Level.SEVERE, e.toString(), e);
                }
            }

            if (retryCount == MAX_RETRY_COUNT){
                view.loginFailView();
                quit();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            view.loginErrorView();
            quit();
        }
    }
    
    private void welcome(){
        ToDoBean bean = new ToDoBean();
        bean.setPriority("true");
        ToDoWrapper priorityResults;
        try {
            priorityResults = this.handler.searchByPriority(bean);
            List<ToDoBean> priorities = priorityResults.getTodos();
            view.welcomeView(priorities);
            userMenu();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            view.databaseErrorView();
            quit();
        }
    }

    private void userMenu(){
        //Loop is intended to run infinitely until user elects to quit program
        while (true){
            if (this.refreshDB){
                try {
                    this.beans = this.handler.getAll().getTodos();
                    refreshDB = false;
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, e.toString(), e);
                    view.databaseErrorView();
                    quit(); 
                }
            }

            view.menuView(this.beans);

            Boolean isIncorrectInput;
            do{
                isIncorrectInput = false;
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
                        break;
                    default:
                        isIncorrectInput = true;
                        view.inputErrorView();
                        break;
                }
            } while (isIncorrectInput);
        }
    }
        
    private void read(){
        int input;
        view.readView();
            
        input  = gui.getNumSelection();
        while (input <= 0 || input > this.beans.size()){
            view.inputErrorView();
            view.readView();
            input  = gui.getNumSelection();
        }
        //Subtracting 1 from user selection to match intended index of beans arraylist
        input -= 1;

        ToDoWrapper choice;
        try {
            choice = this.handler.searchByID(this.beans.get(input));
            ToDoBean bean = choice.getTodos().get(0);

            view.todoView(bean);

            Boolean isIncorrectInput;
            do{
                isIncorrectInput = false;
                view.readSelectView();
                char userSelection = this.gui.getCharSelection();
                switch(userSelection){
                    case 'U':
                        update(bean);
                        break;
                    case 'D':
                        delete(bean);
                        break;
                    case 'M':
                        break;
                    default:
                        isIncorrectInput = true;
                        view.inputErrorView();
                        break;
                }
            }while (isIncorrectInput);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            view.dbLookupError();
        }
    }

    private void updateSelect(){
        view.updateView();
        int input  = this.gui.getNumSelection();
        while (input <= 0 || input > this.beans.size()){
            view.inputErrorView();
            view.updateView();
            input  = this.gui.getNumSelection();
        }
        //Subtracting 1 from user selection to match intended index of beans arraylist
        input -= 1;
  
        ToDoBean choice = this.beans.get(input);
        view.todoView(choice);
        update(choice);
        
    }

    private void update(ToDoBean bean){
        Boolean isIncorrectInput;
        String update = "";

        do{
            isIncorrectInput = false;
            view.updateSelectView();
            char userSelection = this.gui.getCharSelection();
            
            switch(userSelection){
                case 'E':
                    view.updateEventView();
                    update = this.gui.getStringSelection();
                    bean.setEvent(update);
                    break;
                case 'N':
                    view.updateNotesView();
                    update = this.gui.getStringSelection();
                    bean.setNotes(update);
                    break;
                case 'P':
                    do{
                        isIncorrectInput = false;
                        view.updatePriorityView();;
                        char priorityChoice = this.gui.getCharSelection();

                        switch(priorityChoice){
                            case 'Y':
                                update = "true";
                                break;
                            case 'N':
                                update = "false";
                                break;
                            default:
                                isIncorrectInput = true;
                                view.inputErrorView();
                                break;
                        }
                    }while (isIncorrectInput);
                    bean.setPriority(update);
                    break;
                default:
                    isIncorrectInput = true;
                    view.inputErrorView();
                    break;
            }
        }while(isIncorrectInput);

        try {
            this.handler.update(bean);
            this.refreshDB = true;
            view.updateSuccessView();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            view.updateErrorView();
        }
    }

    private void create(){
        view.createView();

        view.createEventView();
        String event = this.gui.getStringSelection();

        view.createNotesView();
        String notes = this.gui.getStringSelection();

        String priority = new String();
        Boolean isIncorrectInput;
        do{
            isIncorrectInput = false;
            view.createPriorityView();
            char priorityChoice = this.gui.getCharSelection();

            switch(priorityChoice){
                case 'Y':
                    priority = "true";
                    break;
                case 'N':
                    priority = "false";
                    break;
                default:
                    isIncorrectInput = true;
                    view.inputErrorView();
                    break;
            }
        }while (isIncorrectInput);

        ToDoBean bean = new ToDoBean();
        bean.setEvent(event);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now(); 
        bean.setCreated(dtf.format(now));
        bean.setNotes(notes);
        bean.setPriority(priority);
        
        try {
            this.handler.create(bean);
            this.refreshDB = true;
            view.createSuccessView();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            view.createErrorView();
        }
    }

    private void deleteSelect(){
        view.deleteView();
        int input = this.gui.getNumSelection();
        while (input <= 0 || input > this.beans.size()){
            view.inputErrorView();
            view.deleteView();
            input = this.gui.getNumSelection();
        }
        //Subtracting 1 from user selection to match intended index of beans arraylist
        input -= 1;

        ToDoBean bean = this.beans.get(input);
        delete(bean);        
    }

    private void delete(ToDoBean bean){
        try {
            this.handler.delete(bean);
            this.refreshDB = true;
            view.deleteSuccessView();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            view.deleteErrorView();
        }
    }

    private void quit(){
        view.quitView();
        this.gui.close();
        System.exit(0);
    }
}
