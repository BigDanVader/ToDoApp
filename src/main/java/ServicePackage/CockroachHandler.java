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
 */

public class CockroachHandler{

    private Transaction transaction = new Transaction();
    private CockroachDAO dao;

    /**
     * Standard constructor.
     */
    public CockroachHandler(){

    }

    /**
     * Constructor that passes the provided <code>DataSource</code> to the {@link #setDataSource} 
     * class method.
     * 
     * @param ds a <code>DataSource</code> object provided by the caller
     * @throws SQLException
     */
    public CockroachHandler(DataSource ds) throws SQLException{
        setDataSource(ds);
    }

    /**
     * This initiallizes a connection to a CockroachDB database.
     * Sets the local {@link Transaction} data member and initializes a local {@link CockroachDB} data member.
     * 
     * @param ds a {@code DataSource} object provided by the calling method
     * @throws SQLException
     */
    public void setDataSource(DataSource ds) throws SQLException{
        this.transaction.setDataSource(ds);
        this.dao = new CockroachDAO(transaction);
    }

    /**
     * This returns all the entries in the attached CockroachDB database.
     * 
     * @return a {@link ToDoWrapper} object.
     * @throws SQLException
     */
    public ToDoWrapper getAll() throws SQLException{
        return dao.read();
    }

    private ToDoWrapper search(String column, String search) throws SQLException{
        return dao.read(column, search);
    }

    /**
     * This returns all entries in the attached CockroachDB database that match a provided {@code UUID}.
     * 
     * @param bean a {@link ToDoBean} data object containing a representation of a database entry
     * @return a {@code ToDoWrapper} with the results of the search.
     * @throws SQLException
     */
    public ToDoWrapper searchByID(ToDoBean bean) throws SQLException{
        return search("id", bean.getUuid());
    }

    /**
     * This returns all entries in the attached CockroachDB database that match a provided event entry.
     * 
     * @param bean a {@link ToDoBean} data object containing a representation of a database entry
     * @return a {@code ToDoWrapper} with the results of the search.
     * @throws SQLException
     */
    public ToDoWrapper searchByEvent(ToDoBean bean) throws SQLException{
        return search("event", bean.getEvent());
    }

    /**
     * This returns all entries in the attached CockroachDB database that match a provided creation date.
     * 
     * @param bean a {@link ToDoBean} data object containing a representation of a database entry
     * @return a {@code ToDoWrapper} with the results of the search.
     * @throws SQLException
     */
    public ToDoWrapper searchByCreated(ToDoBean bean) throws SQLException{
        return search("created", bean.getCreated());
    }

    /**
     * This returns all entries in the attached CockroachDB database that match a provided priority state.
     * 
     * @param bean a {@link ToDoBean} data object containing a representation of a database entry
     * @return a {@code ToDoWrapper} with the results of the search.
     * @throws SQLException
     */
    public ToDoWrapper searchByPriority(ToDoBean bean) throws SQLException{
        return search("priority", bean.getPriority());
    }

    /**
     * This updates an entry in the attached CockroachDB database.
     * 
     * @param bean a {@code ToDoBean} object representing an updated database entry
     * @throws SQLException
     */
    public void update(ToDoBean bean) throws SQLException{
        dao.update(bean.getUuid(), bean.getEvent(), bean.getCreated(), bean.getNotes(), bean.getPriority());
    }

    /**
     * This creates a new entry in the attached CockroachDB database.
     * 
     * @param bean a {@code ToDoBean} object representing a database entry
     * @throws SQLException
     */
    public void create(ToDoBean bean) throws SQLException{
        dao.create(bean.getEvent(), bean.getCreated(), bean.getNotes(), bean.getPriority());
    }

    /**
     * This deletes an entry in the attached CockraochDB database
     * 
     * @param bean a {@code ToDoBean} object representing a current entry in the database
     * @throws SQLException
     */
    public void delete(ToDoBean bean) throws SQLException{
        dao.delete(bean.getUuid());
    }
    
    /**
     * This is used to test if this componant has a connection to the attached CockroachDB database.
     * Expected to be used for login testing by the calling method
     * 
     * @return  a {@code Boolean} representing the current status of the local {@code Transaction} data member
     * @throws SQLException
     */
    public Boolean isValidConnection() throws SQLException{
        if (transaction.getConnection() != null)
            return true;
        
        return false;
    }
}
