package JavaBeanPackage;

import java.io.Serializable;

/**The <code>ToDoBean</code> class is a standard JavaBean class used as a DTO for the CockroachDAO class.
 * 
 * 
 * @author Dan Luoma
 * @since 2023-10-03
 */

public class ToDoBean implements Serializable {

    private String uuid;
    private String event;
    private String created;
    private String notes;
    private String priority;

    public ToDoBean(){

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    
    
}
