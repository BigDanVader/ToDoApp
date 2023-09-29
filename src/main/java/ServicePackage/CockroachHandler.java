package ServicePackage;

import java.sql.SQLException;
import javax.sql.DataSource;

import DAOPackage.CockroachDAO;
import JavaBeanPackage.ToDoBean;
import TranPackage.Transaction;
import WrapperPackage.ToDoWrapper;

/**CockroachHandler is a service level object that allows a business application
 * to access a CockroachDB database without exposing it to any DAO logic.
 * 
 * 
 * @author Dan Luoma
 * @since 2023-09-20
 */

public class CockroachHandler{

    private Transaction transaction;
    private CockroachDAO dao;

    //find out how to call this in the overloaded constructor
    public CockroachHandler(){
        transaction = new Transaction();
        dao = new CockroachDAO(transaction);
    }

    public CockroachHandler(DataSource ds) throws SQLException{
        transaction = new Transaction();
        dao = new CockroachDAO(transaction);
        setDataSource(ds);
    }

    public void setDataSource(DataSource ds) throws SQLException{
        this.transaction.setDataSource(ds);
    }

    public ToDoWrapper getAll() throws SQLException{
        return dao.read();
    }

    private ToDoWrapper search(String column, String search) throws SQLException{
        return dao.read(column, search);
    }

    public ToDoWrapper searchByID(ToDoBean bean) throws SQLException{
        return search("id", bean.getUuid());
    }

    public ToDoWrapper searchByEvent(ToDoBean bean) throws SQLException{
        return search("event", bean.getEvent());
    }

    public ToDoWrapper searchByCreated(ToDoBean bean) throws SQLException{
        return search("created", bean.getCreated());
    }

    //TESTING: change search to bean.getPriority()
    public ToDoWrapper searchByPriority(ToDoBean bean) throws SQLException{
        return search("priority", bean.getPriority());
    }

    public void update(ToDoBean bean){
        try {
            dao.update(bean.getUuid(), bean.getEvent(), bean.getCreated(), bean.getNotes(), bean.getPriority());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void create(ToDoBean bean){
        try {
            dao.create(bean.getEvent(), bean.getCreated(), bean.getNotes(), bean.getPriority());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void delete(ToDoBean bean){
        try {
            dao.delete(bean.getUuid());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    public Boolean isValidConnection(){
        if (transaction.getConnection() != null)
            return true;
        
        return false;
    }
}
