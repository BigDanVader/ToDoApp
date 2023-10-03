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

    public ToDoWrapper searchByPriority(ToDoBean bean) throws SQLException{
        return search("priority", bean.getPriority());
    }

    public void update(ToDoBean bean) throws SQLException{
        dao.update(bean.getUuid(), bean.getEvent(), bean.getCreated(), bean.getNotes(), bean.getPriority());
    }

    public void create(ToDoBean bean) throws SQLException{
        dao.create(bean.getEvent(), bean.getCreated(), bean.getNotes(), bean.getPriority());
    }

    public void delete(ToDoBean bean) throws SQLException{
        dao.delete(bean.getUuid());
    }
    
    public Boolean isValidConnection() throws SQLException{
        if (transaction.getConnection() != null)
            return true;
        
        return false;
    }
}
