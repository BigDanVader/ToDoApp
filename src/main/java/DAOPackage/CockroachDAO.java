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

/**The BasicDAO class recieves a DataSource object and issues SQL commands to the
 * associated database. Currently also prints given table, though this feature will 
 * be removed in the future.
 * 
 * 
 * @author Dan Luoma
 * @since 2023-09-17
 */

public class CockroachDAO {
    private static final Logger LOGGER = Logger.getLogger(CockroachDAO.class.getName());
    private static final int MAX_RETRY_COUNT = 3;
    private static final String RETRY_SQL_STATE = "40001";

    private Transaction transaction;

    private final Random rand = new Random();

    public CockroachDAO() {
    }

    public CockroachDAO(Transaction tran){
        setTransaction(tran);
    }

    public void setTransaction(Transaction tran){
        this.transaction = tran;
    }

    /**
     * Run SQL code in a way that automatically handles the
     * transaction retry logic so we don't have to duplicate it in
     * various places.
     *
     * @param sqlCode a String containing the SQL code you want to
     * execute.  Can have placeholders, e.g., "INSERT INTO accounts
     * (id, balance) VALUES (?, ?)".
     * @param args Option arguments ofr filling in placeholder in @param sqlCode
     * @return a DTO that passes the database results and metadata. May or may not
     * be used by calling class method.
     * @throws SQLException
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

    public ToDoWrapper read() throws SQLException{
        return runSQL("SELECT id, event, created, notes, priority FROM todos");
    }

    public ToDoWrapper read(String column, String search) throws SQLException{
        String sql = String.format("SELECT id, event, created, notes, priority FROM todos WHERE %s = ?", column);
        return runSQL(sql, search);
    }

    public void update(String id, String event, String created, String notes, String priority) throws SQLException{
        runSQL("UPSERT INTO todos (id, event, created, notes, priority) VALUES (?, ?, ?, ?, ?)", id, event, created, notes, priority);
    }

    public void create(String event, String created, String notes, String priority) throws SQLException{
        runSQL("INSERT INTO todos (id, event, created, notes, priority) VALUES (gen_random_uuid(),?,?,?,?)", event, created, notes, priority);
    }

    public void delete(String id) throws SQLException{
        runSQL("DELETE FROM todos WHERE id = ?", id);
    }
}
