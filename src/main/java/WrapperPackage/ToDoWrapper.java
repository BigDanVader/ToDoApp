package WrapperPackage;

import java.util.ArrayList;
import java.util.List;

import JavaBeanPackage.ToDoBean;

/**
 * The {@code ToDoWrapper} class is a wrapper class that provides contents of a database along with its metadata.
 * 
 * @author Dan Luoma
 */

public class ToDoWrapper {
    
    private List<ToDoBean> todos = new ArrayList<>();
    private List<String> metadata = new ArrayList<>();

    public ToDoWrapper(){

    }

    public void setToDos (List<ToDoBean> tdb){
        this.todos = tdb;
    }

    public void setMetadata(List<String> md){
        this.metadata = md;
    }

    public List<ToDoBean> getTodos(){
        return this.todos;
    }

    public List<String> getMetadata(){
        return this.metadata;
    }
}
