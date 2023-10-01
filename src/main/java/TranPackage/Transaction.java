package TranPackage;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**Transaction is a transaction layer class for creating a connection,
 * opening a connection, end a connection, and roll back a connection.
 * 
 * 
 * @author Dan Luoma
 * @since 2023-09-18
 */

public class Transaction {
    
    private DataSource ds;
    private Connection conn;

    public Transaction(){

    }

    public Transaction(DataSource ds) throws SQLException{
        setDataSource(ds);
    }

    public void setDataSource(DataSource data) throws SQLException{
        this.ds = data;
        createConnection();
    }

    //Trying throwing an error message. Trying to have thrown from CockroachHandler constructor
    private void createConnection() throws SQLException{
        conn = ds.getConnection();
    }

    public Connection getConnection() throws SQLException{
        if (conn == null || conn.isClosed())
            createConnection();

        return conn;
    }

    public void start() throws SQLException{
        createConnection();
        conn.setAutoCommit(false);
    }

    public void end() throws SQLException{
        try {
            conn.commit();
        } catch (SQLException e) {
            close();
        }

        close();
    }

    public void rollback() throws SQLException{
        try {
            conn.rollback();
        } catch (SQLException e) {
            close();
        }
    
        close();
    }

    private void close() throws SQLException{
        conn.close();
        conn = null;

    }

    private void printSQLError(SQLException e, String source){
        System.out.printf("ToDoTransaction.%s SQLERROR: { state => %s, cause => %s, message => %s }\n", source ,e.getSQLState(), e.getCause(), e.getMessage());
    }

    private void printError(Exception e, String source){
        System.out.printf("ToDoTransaction.%s ERROR: { cause => %s, message => %s }\n", source, e.getCause(), e.getMessage());

    }
}