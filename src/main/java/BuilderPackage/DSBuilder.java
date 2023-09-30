package BuilderPackage;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.postgresql.ds.PGSimpleDataSource;

public class DSBuilder {

    public DSBuilder(){

    }

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
