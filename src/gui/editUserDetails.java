package gui;

import javax.swing.*;
import db.DatabaseConnectionHandler;
import db.DatabaseOperations;
import store.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import misc.*;

public class editUserDetails extends JFrame {
    private JTextField usernameField, nameField, emailAddressField, houseNumberField, cityNameField, roadNameField,
            postcodeField;
    private JPasswordField passwordField, confirmPasswordField;
    private User currentUser;

    public editUserDetails(User user) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.currentUser = user;
        setLayout(new GridLayout(0, 2));

        // set the size
        this.setSize(500, 300);
        // Display the Input fields
        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(currentUser.getUsername());
        add(usernameLabel);
        add(usernameField);

        // Name
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(currentUser.getName());
        add(nameLabel);
        add(nameField);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        add(passwordLabel);
        add(passwordField);

        // Confirm Password
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField();
        add(confirmPasswordLabel);
        add(confirmPasswordField);

        // Email Address
        JLabel emailAddressLabel = new JLabel("Email Address:");
        emailAddressField = new JTextField(currentUser.getEmailAddress());
        add(emailAddressLabel);
        add(emailAddressField);

        // House Number
        JLabel houseNumberLabel = new JLabel("House Number:");
        houseNumberField = new JTextField(currentUser.getHouseNumber());
        add(houseNumberLabel);
        add(houseNumberField);

        // City Name
        JLabel cityNameLabel = new JLabel("City Name:");
        cityNameField = new JTextField(currentUser.getCityName());
        add(cityNameLabel);
        add(cityNameField);

        // Road Name
        JLabel roadNameLabel = new JLabel("Road Name:");
        roadNameField = new JTextField(currentUser.getRoadName());
        add(roadNameLabel);
        add(roadNameField);

        // Postcode
        JLabel postcodeLabel = new JLabel("Postcode:");
        postcodeField = new JTextField(currentUser.getPostcode());
        add(postcodeLabel);
        add(postcodeField);

        // Submit Button
        JButton submitButton = new JButton("Submit");
        add(submitButton);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performUpdate();

            }
        });

        addEditBankDetailsButton();
        usernameField.requestFocusInWindow();
    }

    // Display "edit bank details button" if bank details exist for current user
    private void addEditBankDetailsButton() {
        DatabaseConnectionHandler dbHandler = new DatabaseConnectionHandler();
        try {
            Connection con = dbHandler.getConnection();
            if (!DatabaseOperations.getCard(currentUser, con).isEmpty()) {
                JButton bankButton = new JButton("Edit Bank Details");
                add(bankButton);
                bankButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(() -> {
                            PaymentWindow newPaymentWindow = new PaymentWindow(false);
                        });
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbHandler.closeConnection();
        }
    }

    private void performUpdate() {
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        try {
            Connection con = db.getConnection();
            con.setAutoCommit(false); // Disable auto-commit

            if (validateAndUpdateFields(con)) {
                con.commit(); // Commit the transaction if all updates are successful
                JOptionPane.showMessageDialog(this, "Details updated successfully!");
            } else {
                con.rollback(); // Rollback the transaction in case of failure
                JOptionPane.showMessageDialog(this, "Update Failed");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }

    private boolean validateAndUpdateFields(Connection con) {
        try {
            if (validateUsername() && validateName() && validatePassword()
                    && validateEmail(emailAddressField.getText())) {
                updateUsername(con);
                updateName(con);
                updatePassword(con);
                updateEmail(con);
                updateHouseNumber(con);
                updateCityName(con);
                updateRoadName(con);
                updatePostCode(con);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean validateUsername() {
        String newUsername = usernameField.getText();
        if (!newUsername.equals(currentUser.getUsername()) && DatabaseOperations.userExists(newUsername)) {
            JOptionPane.showMessageDialog(this, "Username taken. Please try a different username");
            return false;
        }
        return true;
    }

    private boolean validateName() {
        String newName = nameField.getText().strip();
        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Empty name");
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        char[] pass = passwordField.getPassword();
        char[] confirmPass = confirmPasswordField.getPassword();
        // passwords not empty
        if (pass.length != 0 || confirmPass.length != 0) {
            if (!Arrays.equals(pass, confirmPass)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match or are empty.");
                return false;
            }
        }
        return true;
    }

    private boolean validateEmail(String email) {
        // if email field has been changed
        if (!email.equals(currentUser.getEmailAddress())) {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            return email.matches(emailRegex);
        }
        return true;
    }

    private void updateUsername(Connection con) throws SQLException {
        if (!usernameField.getText().equals(currentUser.getUsername())) {
            if (!DatabaseOperations.userExists(currentUser.getUsername())) {
                currentUser.setUsername(usernameField.getText());
                DatabaseOperations.updateField("username", usernameField.getText(), currentUser.getUserID(), con);
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Username Taken",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateName(Connection con) throws SQLException {
        if (!nameField.getText().equals(currentUser.getName())) {
            currentUser.setName(nameField.getText());
            DatabaseOperations.updateField("name", nameField.getText(), currentUser.getUserID(), con);
        }
    }

    private void updatePassword(Connection con) throws SQLException {
        char[] pass = passwordField.getPassword();
        if (pass.length > 0) {
            String hashedPassword = "";
            try {
                hashedPassword = Encryption.encrypt(new String(pass), currentUser.getSalt());
            } catch (Exception e) {
                System.out.println("Password hashing failed");
                e.printStackTrace();
            }
            currentUser.setHashedPassword(hashedPassword);
            DatabaseOperations.updateField("hashed_password", hashedPassword, currentUser.getUserID(), con);
        }
    }

    private void updateEmail(Connection con) throws SQLException {
        if (!emailAddressField.getText().equals(currentUser.getEmailAddress())) {
            currentUser.setEmailAddress(emailAddressField.getText());
            DatabaseOperations.updateField("email", emailAddressField.getText(), currentUser.getUserID(), con);
        }
    }

    private void updateHouseNumber(Connection con) throws SQLException {
        if (!houseNumberField.getText().equals(currentUser.getHouseNumber())) {
            currentUser.setHouseNumber(houseNumberField.getText());
            DatabaseOperations.updateField("house_number", houseNumberField.getText(), currentUser.getUserID(), con);
        }
    }

    private void updateCityName(Connection con) throws SQLException {
        if (!cityNameField.getText().equals(currentUser.getCityName())) {
            currentUser.setCityName(cityNameField.getText());
            DatabaseOperations.updateField("city_name", cityNameField.getText(), currentUser.getUserID(), con);
        }
    }

    private void updateRoadName(Connection con) throws SQLException {
        if (!roadNameField.getText().equals(currentUser.getRoadName())) {
            currentUser.setRoadName(roadNameField.getText());
            DatabaseOperations.updateField("road_name", roadNameField.getText(), currentUser.getUserID(), con);
        }
    }

    private void updatePostCode(Connection con) throws SQLException {
        if (!postcodeField.getText().equals(currentUser.getPostcode())) {
            currentUser.setPostcode(postcodeField.getText());
            DatabaseOperations.updateField("post_code", postcodeField.getText(), currentUser.getUserID(), con);
        }
    }
}
