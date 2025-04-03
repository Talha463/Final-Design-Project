import java.io.*;
import java.util.*;

public class LoginManager {
    private static final String CSV_FILE = "login_info.csv";

    // Register a new user
    public boolean register(String username, String password) {
        if (userExists(username)) {
            return false; // username already exists
        }
        try (FileWriter fw = new FileWriter(CSV_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(username + "," + password);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Login by checking credentials against the CSV file
    public boolean login(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // CSV file might not exist yet on first run
        }
        return false;
    }

    // Check if a user already exists
    public boolean userExists(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // File may not exist on first run
        }
        return false;
    }
}
