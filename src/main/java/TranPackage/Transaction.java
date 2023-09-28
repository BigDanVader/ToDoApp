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
        try {
            conn = ds.getConnection();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage() + ". Please correct error and try again.");
        }
    }

    public Connection getConnection(){
        try {
            if (conn == null || conn.isClosed())
                createConnection();
        } catch (SQLException e) {
            printSQLError(e, "getConnection");
        }

        return conn;
    }

    public void start() throws SQLException{
        createConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            printSQLError(e, "start");
        }
    }

    public void end(){
        try {
            conn.commit();
        } catch (SQLException e) {
            try {
                close();
            } catch (Exception ee) {
                printError(ee, "end (after failed commit and failed close)");
            }
            printSQLError(e, "end (after failed commit and successful close)");
        }

        try {
            close();
        } catch (Exception e) {
            printError(e, "end (after successful commit and failed close)");
        }
    }

    public void rollback(){
        try {
            conn.rollback();
        } catch (SQLException e) {
            try {
                close();
            } catch (Exception ee) {
                printError(ee, "rollback (after failed rollback and failed close)");
            }
            printSQLError(e, "rollback (after failed rollback and successful close)");
        }
    
        try {
            close();
        } catch (Exception e) {
            printError(e, "rollback (after successful rollback and failed close)");
        }
    }

    private void close(){
        try {
            conn.close();
            conn = null;
        } catch (SQLException e) {
            printSQLError(e, "close");
        }
    }

    private void printSQLError(SQLException e, String source){
        System.out.printf("ToDoTransaction.%s SQLERROR: { state => %s, cause => %s, message => %s }\n", source ,e.getSQLState(), e.getCause(), e.getMessage());
    }

    private void printError(Exception e, String source){
        System.out.printf("ToDoTransaction.%s ERROR: { cause => %s, message => %s }\n", source, e.getCause(), e.getMessage());

    }
}