package WrapperPackage;

import java.util.ArrayList;
import java.util.List;

import JavaBeanPackage.ToDoBean;

/**ToDoWrapper is a wrapper class for transfering a list of ToDoBean class objects and
 * a list containing the CockroachDB metadata.
 * 
 * 
 * @author Dan Luoma
 * @since 2023-09-19
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

    //Consider making these methods return deep copies
    public List<ToDoBean> getTodos(){
        return this.todos;
    }

    public List<String> getMetadata(){
        return this.metadata;
    }
}
