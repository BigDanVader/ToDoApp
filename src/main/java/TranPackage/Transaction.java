package TranPackage;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**The {@code Transaction} is a transaction layer class that provides a connection to a database. 
 * Manages creating the connection and commiting any SQL commands provided to it.
 * Includes error checking and rollback in case of sever errors.
 * 
 * @author Dan Luoma
 */

public class Transaction {
    
    private DataSource ds;
    private Connection conn;

    /**
     * Standard constructor.
     */
    public Transaction(){

    }

    /**
     * Constructor that calls the local {@link setDataSource} method.
     * 
     * @param ds a {@code DataSource} object provided by the calling method
     * @throws SQLException
     */
    public Transaction(DataSource ds) throws SQLException{
        setDataSource(ds);
    }

    /**
     * This sets the local {@code DataSource} data member and initializes a connection.
     * 
     * @param data a {@code DataSource} object provided by the calling method
     * @throws SQLException
     */
    public void setDataSource(DataSource data) throws SQLException{
        this.ds = data;
        createConnection();
    }

    private void createConnection() throws SQLException{
        conn = ds.getConnection();
    }

    /**
     * Returns the local {@code Connection} data member.
     * Initializes the {@code Connection} first if required.
     * 
     * @return a connection to be used with a database
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException{
        if (conn == null || conn.isClosed())
            createConnection();

        return conn;
    }

    /**
     * This method creates a connection if it doesnt already exist and manages its own commit.
     */
    public void start() throws SQLException{
        createConnection();
        conn.setAutoCommit(false);
    }

    /**
     * This commits the commands asked of it and then closes itself.
     * Provides error checking.
     * 
     * @throws SQLException
     */
    public void end() throws SQLException{
        try {
            conn.commit();
        } catch (SQLException e) {
            close();
        }

        close();
    }

    /**
     * This provides rollback for failed commits.
     * 
     * @throws SQLException
     */
    public void rollback() throws SQLException{
        try {
            conn.rollback();
        } catch (SQLException e) {
            close();
        }
    
        close();
    }

    /*
     * This closes and sets the conn data member to null in preperation of being used again.
     */
    private void close() throws SQLException{
        conn.close();
        conn = null;

    }
}