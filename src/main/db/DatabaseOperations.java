package main.db;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import main.misc.*;
import main.store.Users.*;

import java.net.http.HttpResponse.ResponseInfo;
import main.misc.Encryption;
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
                    // verify entered password against stored hashed password and username
                    if (verifyPassword(enteredPassword, storedPasswordHash, salt) && username.equals(myUsername)) {
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

            // update user ID now in address table
            String addressQuery2 = "UPDATE Address SET user_id=? WHERE house_number=? AND city_name=? AND road_name=? AND post_code=?";
            PreparedStatement qstmt = db.con.prepareStatement(addressQuery2);
            qstmt.setString(1, signUpUser.getUserID());
            qstmt.setString(2, userAttributes[5].toString());
            qstmt.setString(3, userAttributes[6].toString());
            qstmt.setString(4, userAttributes[7].toString());
            qstmt.setString(5, userAttributes[8].toString());
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

    public static Boolean userExists(User myUser) {
        Boolean isExists = false;
        String username;
        // open db connection
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

        try {
            String query = "SELECT 1 FROM User WHERE username=?";
            PreparedStatement pstmt = db.con.prepareStatement(query);

            // get username
            pstmt.setString(1, myUser.getUsername());
            ResultSet matchedUsers = pstmt.executeQuery();

            if (matchedUsers.next()) {
                isExists = true; // a user with entered username already exists
            }

        } catch (SQLException se) {
            se.printStackTrace();
        }
        return isExists;
    }

    public static Boolean saveUserEditDetails(Connection connection, String userID, String username, String name,
            String enteredPassword, String emailString,
            String houseNumber, String cityName, String roadName, String postCode, String salt) {

        // Find user in database and update details
        try {

            // update address changes in address table
            String addressQuery = "UPDATE Address SET house_number=?, road_name=?, city_name=?, post_code=? WHERE user_id=? ";
            PreparedStatement astmt = connection.prepareStatement(addressQuery);
            astmt.setString(1, houseNumber);
            astmt.setString(2, roadName);
            astmt.setString(3, cityName);
            astmt.setString(4, postCode);
            astmt.setString(5, userID);

            astmt.executeUpdate();
            // Update User Table
            String updateQuery = "UPDATE User SET username=?, name=?, hashed_password=?, email=?, house_number=?, city_name=?, road_name=?, "
                    +
                    "post_code=? WHERE user_id=?";

            PreparedStatement pstmt = connection.prepareStatement(updateQuery);
            pstmt.setString(1, username);
            pstmt.setString(2, name);
            // encrypt password with existing salt
            String hashedPassword = null;
            try {
                hashedPassword = Encryption.encrypt(enteredPassword, salt);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, emailString);
            pstmt.setString(6, cityName);
            pstmt.setString(7, roadName);
            pstmt.setString(8, postCode);

            // pass in current User's userid
            pstmt.setString(9, userID);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /*
     * Checks if given user contains a card. If yes, returns the card details in a
     * String[]. If not, returns null
     */
    public static List<String> getCard(User myUser, Connection con) {
        // Check if card exists
        List<String> cardDetails = new ArrayList<>();
        String userID = myUser.getUserID(); // get current user's id
        PreparedStatement pstmt = null;
        ResultSet res = null;

        try {
            // Check if card exists
            String query = "SELECT * FROM BankingDetails WHERE user_id=?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, userID);
            res = pstmt.executeQuery();

            // if card exists for user
            if (res.next()) {
                String salt = res.getString("salt");
                try {
                    // String cardName = Encryption.decrypt(res.getString("card_name"), salt);
                    String cardNumber = Encryption.decrypt(res.getString("card_number"), salt);
                    // Format and decrypt date properly
                    // Date expiryDate = res.getDate("expiry_date");
                    // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    // String decryptedDate = Encryption.decrypt(dateFormat.format(expiryDate), salt);
                    // String cvv = Encryption.decrypt(String.valueOf(res.getInt("cvv")), salt);

                    // add details to array
                    cardDetails.add(cardNumber);
                    // cardDetails.add(cardName);
                    // cardDetails.add(decryptedDate);
                    // cardDetails.add(cvv);

                    System.out.println("User's existing card details being added: " + cardNumber);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            // close resources if applicable
            try {
                if (res != null) {
                    res.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return cardDetails;
    }

    public static Boolean addBankDetail(User myUser, BankDetail bankDetail, Connection con) {
        boolean isAdded = false;

        try {
            // Prepare SQL Query with bank detail parameters
            String query = "INSERT INTO BankingDetails (user_id, card_name, card_number, expiry_date, cvv, valid_status, salt) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(query);

            pstmt.setString(1, myUser.getUserID());
            pstmt.setString(2, bankDetail.getCardName());
            pstmt.setString(3, bankDetail.getCardNumber());
            pstmt.setDate(4, bankDetail.getExpiryDate());
            pstmt.setString(5, bankDetail.getCVV());
            pstmt.setBoolean(6, true);
            pstmt.setString(7, bankDetail.getBankSalt());

            // Run the sql query
            pstmt.executeUpdate();

            System.out.println("Bank Details Updated for User " + myUser.getUserID() + " successfully!");
            isAdded = true;

        } catch (SQLException se) {
            System.out.println("Failed to add bank details for user  " + myUser.getUserID() + " " + bankDetail.getCardNumber());
            se.printStackTrace();
            isAdded = false;
        }

        return isAdded;
    }


}