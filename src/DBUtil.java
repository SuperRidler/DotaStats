import java.sql.DriverManager;

import com.mysql.jdbc.Connection;


public class DBUtil {

	private static final String driver = "org.gjt.mm.mysql.Driver";
	private static final String url = "jdbc:mysql://localhost/DotaStats";
	
	public static Connection connectToDB() {
		try {
			Class.forName(driver);
			Connection con = (Connection) DriverManager.getConnection(url, "root", "");
			con.setDumpQueriesOnException(true);
			System.out.println("Database connection setup.");
			return con;
		} catch (Exception e) {
			System.out.println("Failed to connect to the database.");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public static void closeDB(Connection con) {
		try {
			con.close();
			System.out.println("Connection to database closed.");
		} catch (Exception e) {
			System.out.println("Failed to close the database connection.");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
