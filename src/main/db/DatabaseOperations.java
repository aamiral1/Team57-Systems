package main.db;

import java.sql.Connection;
import main.misc.*;
import main.store.Users.*;

import java.sql.*;

public class DatabaseOperations {

    /*
     * verifyLogin: This method takes three parameters: a database connection, a
     * username, and an entered password
     * (provided as a character array). It queries the database to retrieve user
     * information, including the user’s
     * stored password hash, the number of failed login attempts, and whether the
     * account is locked.
     */
    public static Boolean verifyLogin(Connection connection, String username, char[] enteredPassword) {
        try {
            String query = "SELECT * FROM User WHERE name=?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Get User Details
                String userID = resultSet.getString("user_id");
                // System.out.println("User ID found: " + userID);
                String storedPasswordHash = resultSet.getString("hashed_password");
                String email = resultSet.getString("email");
                String houseNumber = resultSet.getString("house_number");
                String roadName = resultSet.getString("road_name");
                String cityName = resultSet.getString("city_name");
                String postCode = resultSet.getString("post_code");

                Date joinDate = resultSet.getDate("join_date");

                int failedLoginAttempts = resultSet.getInt("failed_login_attempts");
                if (resultSet.wasNull()) {
                    failedLoginAttempts = -1; // sentinel value
                }
                boolean accountLocked = resultSet.getBoolean("account_locked");
                if (resultSet.wasNull()) {
                    accountLocked = false; // default value for locked
                }

                // check if account locked
                if (accountLocked) {
                    System.out.println("Account is locked. Please contact support");
                    return false;
                } else {
                    // verify entered password against stored hashed password
                    
                    if (verifyPassword(enteredPassword, storedPasswordHash)) {
                        // Login Successful
                        query = "UPDATE User SET last_login = CURRENT_TIMESTAMP, " +
                                "failed_login_attempts = 0 WHERE user_id = ?";

                        statement = connection.prepareStatement(query);
                        statement.setString(1, userID);
                        statement.executeUpdate();

                        // Create a user object and set it as current user
                        User myUser = new User(userID, username, email, storedPasswordHash, houseNumber, roadName,
                                cityName, postCode, joinDate, accountLocked);

                        // Set myUser as current user
                        UserManager.setCurrentUser(myUser);
                        System.out.println("Login successful for user: " + myUser);
                        return true;
                    } else {
                        // invalid login credentials
                        failedLoginAttempts++;
                        query = "UPDATE User SET failed_login_attempts = ? WHERE user_id = ?";

                        statement = connection.prepareStatement(query);
                        statement.setInt(1, failedLoginAttempts);
                        statement.setString(2, userID);
                        statement.executeUpdate();

                        System.out.println("Incorrect password. Failed login attempts: " + failedLoginAttempts);

                        return false;
                    }
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("User Not Found");
        return false;
    }

    public static Boolean verifyPassword(char[] enteredPassword, String storedPasswordHash) {
        Boolean matchStatus = false;
        try {
            // check if hashes match
            System.out.println("Password trying to match " + String.valueOf(enteredPassword));
            String encryptedEnteredPassword = Encryption.encrypt(String.valueOf(enteredPassword), User.salt);
            System.out.println(encryptedEnteredPassword);
    
            if (encryptedEnteredPassword.equals(storedPasswordHash)) {
                matchStatus = true; // passwords matched succesfully
                System.out.println("Match Successful");
            } else {System.out.println("Match Unsuccessful");}
        } catch (Exception e) {
            e.printStackTrace();
        }

        return matchStatus;
    }
}