package ViewPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import JavaBeanPackage.ToDoBean;

/**
 * The {@code ToDoView} provides a graphical output to the user of the ToDo application through the console.
 * 
 * @author Dan Luoma
 */
public class ToDoView {

    public ToDoView(){

    }

    public void loginView(){
        System.out.println("Logging in...");
        System.out.println();
    }

    public void loginSuccessView(){
        System.out.println("Success!");
        System.out.println();
    }

    public void welcomeView(List<ToDoBean> priority){
        Scanner logo = null;
        try {
            logo = new Scanner(new File("Logo.txt"));
            while (logo.hasNextLine())
                System.out.println(logo.nextLine());
        } catch (FileNotFoundException e) {
            System.out.println("YouToDo");
        }
        System.out.println("A simple ToDo app.");
        System.out.println("Dan Luoma   Version 1.0");
        System.out.println();
        logo.close();

        if (priority.size() != 0){
            System.out.println("*Your priority ToDos*");
    
            for (ToDoBean b : priority){
                System.out.print(b.getEvent());
                System.out.print(", created ");
                System.out.println(b.getCreated());
            }
        }
        else
            System.out.println("No priority ToDos.");
        
        System.out.println();
    }

    public void menuView(List<ToDoBean> beans){
        printToDos();
        int count = 1;
        for (ToDoBean bean : beans){
            System.out.print(count + ": "); 
            System.out.print(bean.getEvent());
            System.out.print(", created ");
            System.out.println(bean.getCreated());
            count++;
        }
        System.out.println();
    }       

    public void menuSelectView(){
        System.out.print("(R)ead, (C)reate, (U)pdate, (D)elete, (Q)uit?: ");
    }

    public void readView(){
        System.out.print("Select ToDo to read: ");
    }

    public void todoView(ToDoBean bean){
        System.out.println("Selected ToDo:");
        System.out.print(bean.getEvent());
        System.out.print(", created ");
        System.out.println(bean.getCreated() + ".");
        System.out.println("Notes: " + bean.getNotes());
        System.out.println("Priority: " + bean.getPriority());
        System.out.println();
    }

    public void readSelectView(){
        System.out.print("(U)pdate, (D)elete, (M)ain menu?: ");
    }

    public void updateView(){
        System.out.print("Select ToDo to update: ");
    }

    public void updateSelectView(){
        System.out.print("Update (Event), (N)otes, (P)riority?: ");
    }

    public void updateSuccessView(){
        System.out.println("Update successful!");
    }

    public void createView(){
        System.out.println("Create new ToDo...");
    }

    public void createEventView(){
        System.out.print("New event: ");
    }

    public void createNotesView(){
        System.out.print("Notes: ");
    }

    public void createPriorityView(){
        System.out.print("Make priority, (Y)es or (N)o: ");
    }

    public void createSuccessView(){
        System.out.println("Added ToDo!");
        System.out.println();
    }

    public void deleteView(){
        System.out.print("Select ToDo to delete: ");
    }

    public void deleteSuccessView(){
        System.out.println("ToDo deleted!");
        System.out.println();
    }

    public void quitView(){
        System.out.print("Goodbye");
        System.out.println();
    }

    public void loginErrorView() {
        System.out.println("Unable to log in. Please consult error log. Closing program...");
    }

    public void loginFailView() {
        System.out.println("Max retrys attempted, closing program...");
        System.out.println();
    }

    public void databaseErrorView() {
        System.out.println("Error in contacting database. Closing program...");
    }

    public void inputErrorView() {
        System.out.println("Input not recognized. Please try again.");
    }

    public void dbLookupError() {
        System.out.println("Error finding todo in database. Returning to menu...");
        System.out.println();
    }

    public void updateErrorView() {
        System.out.println("Unable to update todo. Returning to menu...");
        System.out.println();
    }

    public void createErrorView() {
        System.out.println("Unable to create todo. Returning to menu...");
        System.out.println();
    }

    public void deleteErrorView() {
        System.out.println("Unable to delete todo. Returning to menu...");
        System.out.println();
    }

    public void updateEventView() {
        System.out.print("Enter update for Event: ");
    }

    public void updateNotesView() {
        System.out.print("Enter update for Notes: ");
    }

    public void updatePriorityView() {
        System.out.print("Make priority, (Y)es or (N)o: ");
    }

    private void printToDos(){
        System.out.printf("--------------------------------------------%n");
        System.out.printf("                 Your ToDos                 %n");
        System.out.printf("--------------------------------------------%n");
    }
}
