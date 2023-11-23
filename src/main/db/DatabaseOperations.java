package main.db;

import java.sql.Connection;
import main.misc.*;
import main.store.Users.*;

import java.net.http.HttpResponse.ResponseInfo;
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
            String query = "SELECT * FROM User WHERE username=?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Get User Details
                String userID = resultSet.getString("user_id");
                String myUsername = resultSet.getString("username");
                String name = resultSet.getString("name");
                String storedPasswordHash = resultSet.getString("hashed_password");
                String email = resultSet.getString("email");
                String houseNumber = resultSet.getString("house_number");
                String roadName = resultSet.getString("road_name");
                String cityName = resultSet.getString("city_name");
                String postCode = resultSet.getString("post_code");
                String role = resultSet.getString("role");
                String salt = resultSet.getString("salt");
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

                    if (verifyPassword(enteredPassword, storedPasswordHash, salt)) {
                        // Login Successful
                        query = "UPDATE User SET last_login = CURRENT_TIMESTAMP, " +
                                "failed_login_attempts = 0 WHERE user_id = ?";

                        statement = connection.prepareStatement(query);
                        statement.setString(1, userID);
                        statement.executeUpdate();

                        // Create a user object and set it as current user
                        User myUser = new User(userID, username, name, storedPasswordHash, email, houseNumber, cityName,
                                roadName, postCode, joinDate, role, salt);

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

    public static Boolean verifyPassword(char[] enteredPassword, String storedPasswordHash, String salt) {
        Boolean matchStatus = false;
        try {
            // check if hashes match
            System.out.println("Password trying to match " + String.valueOf(enteredPassword));
            String encryptedEnteredPassword = Encryption.encrypt(String.valueOf(enteredPassword), salt);
            System.out.println(encryptedEnteredPassword);

            if (encryptedEnteredPassword.equals(storedPasswordHash)) {
                matchStatus = true; // passwords matched succesfully
                System.out.println("Match Successful");
            } else {
                System.out.println("Match Unsuccessful");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return matchStatus;
    }

    public static Boolean signUp(User signUpUser) {
        Boolean flag = false;
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

        Object[] userAttributes = signUpUser.getAttributes();

        try {
            // initiate transaction
            db.con.setAutoCommit(false);

            // Create entries for inputted address in address table
            String addressQuery = "INSERT INTO Address (house_number, city_name, road_name, post_code) VALUES (?,?,?,?)";
            PreparedStatement apstmt = db.con.prepareStatement(addressQuery);

            // Insert user's address fields into sql query
            for (int n = 1; n <= 4; n++) {
                apstmt.setString(n, userAttributes[n + 4].toString());
            }

            apstmt.executeUpdate();

            // Pass in sql query to sign up user to User table
            String query = "INSERT INTO User VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = db.con.prepareStatement(query);

            // Pass in parameters to SQL Query
            for (int i = 0; i < 9; i++) {
                System.out.println(userAttributes[i]);
                pstmt.setString(i + 1, userAttributes[i].toString());
            }
            pstmt.setDate(10, Date.valueOf(userAttributes[9].toString()));
            // set last login and failed attempts to null
            pstmt.setNull(11, Types.NULL);
            pstmt.setNull(12, Types.NULL);
            pstmt.setBoolean(13, Boolean.valueOf(userAttributes[10].toString()));
            pstmt.setString(14, userAttributes[11].toString());
            pstmt.setString(15, userAttributes[12].toString());

            System.out.println(Date.valueOf(userAttributes[9].toString()));

            // execute Query
            pstmt.executeUpdate();
            flag = true;
            System.out.println("Signed up successfully");

            // commit transaction
            db.con.commit();
        } catch (SQLException e) {
            // If any database operations fail, attempt to rollback changes
            try {
                if (db.con != null) {
                    db.con.rollback();
                }
            } catch (SQLException se2) {
                // Handle the error of the rollback failure if necessary
                se2.printStackTrace();
            }
            // Handle the original exception
            e.printStackTrace();
        } finally {
            // Re-enable auto-commit mode after the transaction is complete
            try {
                if (db.con != null) {
                    db.con.setAutoCommit(true);
                    db.con.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return flag;
    }
}