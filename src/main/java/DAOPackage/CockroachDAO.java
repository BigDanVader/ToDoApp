package DAOPackage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import JavaBeanPackage.ToDoBean;
import TranPackage.Transaction;
import WrapperPackage.ToDoWrapper;

/**The CockroachDAO class takes a {@link Transaction} class object, establishes a connection
 * to a CockroachDB database, and performs basic CRUD operations while returning results as needed.
 * 
 * @author Dan Luoma
 */

public class CockroachDAO {
    private static final Logger LOGGER = Logger.getLogger(CockroachDAO.class.getName());
    private static final int MAX_RETRY_COUNT = 3;
    private static final String RETRY_SQL_STATE = "40001";

    private Transaction transaction;

    private final Random rand = new Random();

    /**
     * Standard constructor.
     */
    public CockroachDAO() {
    }

    /**
     * Constructor that sets the <code>Transaction</code> private data member.
     */
    public CockroachDAO(Transaction tran){
        setTransaction(tran);
    }

    
    /** 
     * Sets the <code>Transaction</code> private data member of the class.
     * 
     * @param tran a <code>Transaction</code> class object
     */
    public void setTransaction(Transaction tran){
        this.transaction = tran;
    }

    /*This takes a String representation of an SQL command along with optional arguments, constructs a
     * PreparedStatement, and executes the command. If there are results returned, they are packaged into
     * a ToDoWrapper and returned to the calling method.
     * 
     * @param sqlCode a String representing an SQL commaand. May contain wildcards (example; SELECT ? FROM todos)
     * @param args optional arguments for formatting sqlCode
     * @return a ToDoWrapper object containing a representation of the results returned as well as the database
     * metadata. Will return null if no results are produced by the sql command.
     */
    private ToDoWrapper runSQL(String sqlCode, String... args) throws SQLException{
        ToDoWrapper results = new ToDoWrapper();
        int retryCount = 0;

        this.transaction.start();

        while (retryCount <= MAX_RETRY_COUNT){
            if (retryCount == MAX_RETRY_COUNT) {
                String err = String.format("hit max of %s retries, aborting", MAX_RETRY_COUNT);
                throw new RuntimeException(err);
            }

            try (PreparedStatement pstmt = this.transaction.getConnection().prepareStatement(sqlCode)){
                for (int i = 0; i < args.length; i++) {
                    int place = i + 1;
                    String arg = args[i];

                    //determines the type of argument that should be constructed
                    try {
                        UUID uuid = UUID.fromString(arg);
                        pstmt.setObject(place, uuid);
                    } catch (IllegalArgumentException ie) {
                        if (arg == "true" || arg == "false"){
                            Boolean bool = Boolean.parseBoolean(arg);
                            pstmt.setBoolean(place, bool);
                        }
                        else
                            pstmt.setString(place, arg);
                    }
                }

                if (pstmt.execute()){
                    ResultSet rs = pstmt.getResultSet();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    results = wrap(rs, rsmd);
                }

                this.transaction.end();
                break;

            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
                if (RETRY_SQL_STATE.equals(e.getSQLState())) {
                    // Since this is a transaction retry error, we
                    // roll back the transaction and sleep a
                    // little before trying again.  Each time
                    // through the loop we sleep for a little
                    // longer than the last time
                    // (A.K.A. exponential backoff).
                    this.transaction.rollback();
                    retryCount++;
                    int sleepMillis = (int)(Math.pow(2, retryCount) * 100) + rand.nextInt(100);

                    try {
                        Thread.sleep(sleepMillis);
                    } 
                    catch (InterruptedException ignored) {
                        // Necessary to allow the Thread.sleep()
                        // above so the retry loop can continue.
                    }

                } else 
                    throw e;
            }
        }
    
        return results;
    }

    /*Takes the ResultSet and ResultSetMetaData produced by an sql query, turns both into seperate
     * ArrayList objects, then packages them into a ToDoWrapper object and returns the object.
     * 
     * @param rs a ResultSet produced from an sql command
     * @param rsmd a ResultSetMetaData object containing the metadata of the database
     * @return a ToDoWrapper object containing a representation of the results returned as well as the database
     * metadata.
     */
    private ToDoWrapper wrap (ResultSet rs, ResultSetMetaData rsmd) throws SQLException{
        List<ToDoBean> results = new ArrayList<>();
        List<String> metadata = new ArrayList<>();

            while (rs.next()){
                ToDoBean bean = new ToDoBean();
                bean.setUuid(rs.getString("id"));
                bean.setEvent(rs.getString("event"));
                bean.setCreated(rs.getString("created"));
                bean.setNotes(rs.getString("notes"));
                bean.setPriority(rs.getString("priority"));
                results.add(bean);
            }

        for (int i = 1; i <= rsmd.getColumnCount(); i++){
            String str = rsmd.getColumnName(i);
            metadata.add(str);
        }

        ToDoWrapper wrapper = new ToDoWrapper();
        wrapper.setToDos(results);
        wrapper.setMetadata(metadata);

        return wrapper;
    }

    /**
     * Retrieves all todo entries from the database.
     * 
     * @return a <code>ToDoWrapper</code> containing all todo entries
     * @throws SQLException
     */
    public ToDoWrapper read() throws SQLException{
        return runSQL("SELECT id, event, created, notes, priority FROM todos");
    }

    /**
     * Retrieves todo entries from the database based on a speficied database column and a search term.
     * 
     * @param column a column in the database
     * @param search a <code>String</code> representation of a column entry to be found in the todo database
     * @return a <code>ToDoWrapper</code> object containing all todo entries meeting the search criteria.
     * @throws SQLException
     */
    public ToDoWrapper read(String column, String search) throws SQLException{
        String sql = String.format("SELECT id, event, created, notes, priority FROM todos WHERE %s = ?", column);
        return runSQL(sql, search);
    }

    /**
     * Updates a todo entry in the database.
     * Intended to be used with a <code>ToDoBean</code> object previously pulled from the database and with updated values.
     * Used for several search methods in the <code>CockroachHandler</code> class.
     * 
     * @param id a <code>String</code> representation of an <code>UUID</code> identifier
     * @param event a description of the event  
     * @param created the day and time the todo entry was created
     * @param notes any notes appliciable to the todo entry
     * @param priority a <code>String</code> representation of the priority status of the todo entry
     * @throws SQLException
     */
    public void update(String id, String event, String created, String notes, String priority) throws SQLException{
        runSQL("UPSERT INTO todos (id, event, created, notes, priority) VALUES (?, ?, ?, ?, ?)", id, event, created, notes, priority);
    }

    /**
     * Creates a new todo entry in the database.
     * 
     * @param event a description of the event
     * @param created the day and time the todo entry was created
     * @param notes any notes appliciable to the todo entry
     * @param priority a <code>String</code> representation of the priority status of the todo entry
     * @throws SQLException
     */
    public void create(String event, String created, String notes, String priority) throws SQLException{
        runSQL("INSERT INTO todos (id, event, created, notes, priority) VALUES (gen_random_uuid(),?,?,?,?)", event, created, notes, priority);
    }

    /**
     * Finds and deletes a todo entry in the database.
     * 
     * @param id a UUID for the todo entry to delete
     * @throws SQLException
     */
    public void delete(String id) throws SQLException{
        runSQL("DELETE FROM todos WHERE id = ?", id);
    }
}
