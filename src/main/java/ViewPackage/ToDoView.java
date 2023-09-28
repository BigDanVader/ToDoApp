package ViewPackage;

import java.util.List;

import JavaBeanPackage.ToDoBean;

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
        System.out.println("Welcome to the ToDo app.");
        System.out.println();

        if (priority.size() != 0){
            System.out.println("*Your priority todos*");
            System.out.println();
    
            for (ToDoBean b : priority){
                System.out.print(b.getEvent());
                System.out.print(", created ");
                System.out.println(b.getCreated());
            }
        }
        else
            System.out.println("No priority todos.");
        
        System.out.println();
    }

    public void menuView(List<ToDoBean> beans){
        int count = 1;
        System.out.println("Your ToDos:");
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

    public void updateInputView(){
        System.out.print("Enter update:");
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
}