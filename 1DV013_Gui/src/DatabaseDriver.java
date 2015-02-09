

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseDriver {
    private static DatabaseDriver instance = new DatabaseDriver();
    public static final String URL = "jdbc:mysql://localhost/moviesdb";
    public static final String USER = "root";
    public static final String PASSWORD = "root";
    public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";

    private DatabaseDriver() {
    	try {
    		/* load jdbc driver in memory */
			Class.forName(DRIVER_CLASS);
		} catch (ClassNotFoundException exception) {
			System.out.println("Unable to locate JDBC....");
		}
    }

    private Connection connect() {
    	Connection con = null;

    	try {
			con = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (SQLException exception) {
			System.out.println("Unable to connect to the database....");
		}
    	return con;
    }

    public static Connection open() {
    	return instance.connect();
    }
    


    
}
