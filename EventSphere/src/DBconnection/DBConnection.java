package DBconnection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/EventSphere";
    private static final String USER = "Archita";
    private static final String PASSWORD = "Archita@123";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ New database connection created");
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
            throw e;
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("🔒 Database connection closed.");
                }
            } catch (SQLException e) {
                System.out.println("⚠️ Failed to close database connection: " + e.getMessage());
            }
        }
    }
}