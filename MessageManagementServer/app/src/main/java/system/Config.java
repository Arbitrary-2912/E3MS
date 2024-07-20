package system;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class represents the configuration parameters of the system.
 */
public class Config {
    public static final boolean DB_ENABLED = false;
    public static String DB_URL = "jdbc:mysql://localhost:3306/";
    public static String DB_USERNAME = "e3ms";
    public static String DB_PASSWORD = "password";
    public static Integer PORT = 8080;

    static {
        try {
            // Adjust the DB_URL to include the actual database name and use the internal Docker network hostname
            String hostName = InetAddress.getLocalHost().getHostName();
            DB_URL = "jdbc:mysql://" + hostName + ":3306/e3ms";
        } catch (UnknownHostException e) {
            System.out.println("Failed to get the hostname of the container.");
            // Fallback in case of error
            DB_URL = "jdbc:mysql://localhost:3306/e3ms";
        }
        DB_USERNAME = System.getenv().getOrDefault("DB_USERNAME", "e3ms");
        DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "password");
        // Default to 8080 if the environment variable is not set
        PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
    }
}
