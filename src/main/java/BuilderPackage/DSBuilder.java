package BuilderPackage;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.postgresql.ds.PGSimpleDataSource;

/**
 * The <code>DSBuilder</code> class constructs and returns a <code>PGSimpleDataSource</code> ready to connect 
 * with a CockroachDB database.
 * 
 * @author Dan Luoma
 * @since 2023-10-03
 */
public class DSBuilder {

    public DSBuilder(){

    }

    /**
     * This creates a new <code>PGSimpleDataSource</code> object, sets the necessary variables to values
     * located in the <code>dbData.properties</code> file, and returns the <code>PGSimpleDataSource</code>.
     * 
     * @return a <code>PGSimpleDataSource</code> 
     * @throws IOException
     */
    public PGSimpleDataSource buildDS() throws IOException{
        PGSimpleDataSource ds = new PGSimpleDataSource();
        Properties prop = new Properties();
        FileReader reader = new FileReader("dbData.properties");
        prop.load(reader);

        ds.setURL(prop.getProperty("url"));
        ds.setSslMode(prop.getProperty("sslmode"));
        ds.setUser(prop.getProperty("username"));
        ds.setPassword(prop.getProperty("password"));

        return ds;
    }
    
}
