import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ConnectionManager {
    private static String DB_URL     = "jdbc:mysql://rc1d-x3tqozn93u8f0tkd.mdb.yandexcloud.net:3306/thesis?useSSL=true&rewriteBatchedStatements=true";
    private static String DB_USER    = "thesis";
    private static String DB_PASS    = "12345678";

    private static Connection connection;

    public static Connection getConnection() {
        System.setProperty("javax.net.ssl.trustStore", "/home/admin/.mysql/YATrustStore");
        System.setProperty("javax.net.ssl.trustStorePassword", "12345678");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                ResultSet q = connection.createStatement().executeQuery("SELECT version()");
                if(q.next()) {System.out.println(q.getString(1));}
            } catch (SQLException ex) {
                // log an exception. fro example:
                System.out.println("Failed to create the database connection.");
            }
        } catch (ClassNotFoundException ex) {
            // log an exception. for example:
            System.out.println("Driver not found.");
        }
        return connection;
    }
}