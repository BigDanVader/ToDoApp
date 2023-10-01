package ControllerPackage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
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
                    //pass stuff to custom logger class;
                }
            }

            if (retryCount == MAX_RETRY_COUNT){
                view.loginFailView();
                quit();
            }
        } catch (IOException e) {
            // TODO: Pass stuff to custom logger class
            view.loginErrorView();
            quit();
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
            // TODO Pass stuff to custom logger class
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
                    // TODO Pass stuff to custom logger class
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
        int input = 0;
        Boolean isIncorrect;
        do{
            isIncorrect = false;
            view.readView();
            try{
                input  = gui.getNumSelection();
                if (input < 0 || input >= this.beans.size()){
                    isIncorrect = true;
                    view.inputErrorView();
                }
            }catch (InputMismatchException e){
                //No need to log. Essentially the same as an input error
                isIncorrect = true;
                view.inputErrorView();
            }
        }while (isIncorrect);

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
            // TODO Pass stuff to custom logger class
            view.dbLookupError();
        }
    }


    private void updateSelect(){
        int input = 0;
        ToDoBean choice = new ToDoBean();
        Boolean isIncorrect;
        do{
            isIncorrect = false;
            view.updateView();
            try{
                input  = this.gui.getNumSelection();
                if (input < 0 || input >= this.beans.size()){
                    isIncorrect = true;
                    view.inputErrorView();
                }
            }catch (InputMismatchException e){
                //No need to log. Essentially the same as an input error
                isIncorrect = true;
                view.inputErrorView();
            }
        }while (isIncorrect);
        choice = this.beans.get(input);
        view.todoView(choice);

        update(choice);
        
    }

    //Updating priority is vague and doesnt have proper input checking for y/n
    //Exception handling worked just fine, though.
    private void update(ToDoBean bean){
        view.updateSelectView();

        char userSelection = this.gui.getCharSelection();
        while (userSelection != 'E' && userSelection != 'N' && userSelection != 'P'){
            view.inputErrorView();
            view.updateSelectView();
            userSelection = this.gui.getCharSelection();
        }
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

        try {
            this.handler.update(bean);
            this.refreshDB = true;
            view.updateSuccessView();
        } catch (SQLException e) {
            // TODO Pass stuff to custom logger class
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
            // TODO Pass stuff to custom logger class
            view.createErrorView();
        }
    }

    private void deleteSelect(){
        int input = 0;
        Boolean isIncorrect;
        do{
            isIncorrect = false;
            view.deleteView();
            try{
                input = this.gui.getNumSelection();
                if (input < 0 || input >= this.beans.size()){
                    isIncorrect = true;
                    view.inputErrorView();
                }
            }catch (InputMismatchException e){
                //No need to log. Essentially the same as an input error
                isIncorrect = true;
                view.inputErrorView();
            }
        }while (isIncorrect);

        ToDoBean bean = this.beans.get(input);
        delete(bean);        
    }

    private void delete(ToDoBean bean){
        try {
            this.handler.delete(bean);
            this.refreshDB = true;
            view.deleteSuccessView();
        } catch (SQLException e) {
            // TODO Pass stuff to custom logger class
            view.deleteErrorView();
        }
    }

    private void quit(){
        view.quitView();
        this.gui.close();
        System.exit(0);
    }
}
