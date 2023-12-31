package gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import db.*;
import misc.*;
import store.*;

public class SignUp extends JPanel {
    private JButton sign, backButton;
    private JLabel fName, sName, username, houseNumber, roadName, postcode, cityName, jcomp15, emailAddress, password;
    private JTextField roadNameBox, cityNameBox, postcodeBox, sNameBox, fNameBox, usernameBox, houseNumberBox,
            emailAddressBox,
            passwordBox;

    public SignUp() {
        // Use BorderLayout for the main panel
        setLayout(new BorderLayout(10, 10));

        // Create a header panel for the title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jcomp15 = new JLabel("WELCOME TO THE SIGNUP PAGE");
        headerPanel.add(jcomp15);

        // Create a center panel with GridBagLayout for form fields
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4); // Padding between grid cells
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left (west)

        fName = new JLabel("Forename:");
        fNameBox = new JTextField(20);
        sName = new JLabel("Surname:");
        sNameBox = new JTextField(20);
        username = new JLabel("Username:");
        usernameBox = new JTextField(20);
        emailAddress = new JLabel("Email Address:");
        emailAddressBox = new JTextField(20);
        password = new JLabel("Password:");
        passwordBox = new JTextField(20);
        houseNumber = new JLabel("House Number:");
        houseNumberBox = new JTextField(20);
        roadName = new JLabel("Road Name:");
        roadNameBox = new JTextField(20);
        cityName = new JLabel("City Name:");
        cityNameBox = new JTextField(20);
        postcode = new JLabel("Postcode:");
        postcodeBox = new JTextField(20);

        // Add components to center panel using GridBagLayout constraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(fName, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        centerPanel.add(fNameBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(sName, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        centerPanel.add(sNameBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(username, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        centerPanel.add(usernameBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(emailAddress, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        centerPanel.add(emailAddressBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        centerPanel.add(password, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        centerPanel.add(passwordBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        centerPanel.add(houseNumber, gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        centerPanel.add(houseNumberBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        centerPanel.add(roadName, gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        centerPanel.add(roadNameBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        centerPanel.add(cityName, gbc);
        gbc.gridx = 1;
        gbc.gridy = 7;
        centerPanel.add(cityNameBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 8;
        centerPanel.add(postcode, gbc);
        gbc.gridx = 1;
        gbc.gridy = 8;
        centerPanel.add(postcodeBox, gbc);

        // Create a footer panel for the sign-up button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sign = new JButton("Sign up");
        backButton = new JButton("Back");
        footerPanel.add(sign);
        footerPanel.add(backButton);

        // Action listener to sign up button
        sign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if inputs are validated
                if (validateInputs()) {
                    String userID = UniqueUserIDGenerator.generateUniqueUserID();
                    String username = usernameBox.getText().strip();
                    String name = fNameBox.getText() + " " + sNameBox.getText().strip();
                    String hashedPassword = passwordBox.getText().strip();
                    String emailAddress = emailAddressBox.getText().strip();
                    String houseNumber = houseNumberBox.getText().strip();
                    String roadName = roadNameBox.getText().strip();
                    String cityName = cityNameBox.getText().strip();
                    String postcode = postcodeBox.getText().strip();
                    String role = "Customer"; // Default signup is customer
                    String salt = Encryption.generateSalt().strip();

                    // store date of sign up
                    java.util.Date utilDate = new java.util.Date();
                    java.sql.Date joinDate = new java.sql.Date(utilDate.getTime());

                    // open database connection
                    DatabaseConnectionHandler db = new DatabaseConnectionHandler();
                    db.openConnection();

                    // Sign Up User
                    User newUser = new User(userID, username, name, hashedPassword, emailAddress, houseNumber, cityName,
                            roadName, postcode, joinDate, role, salt);
                    if (!DatabaseOperations.userExists(newUser.getUsername()) && !DatabaseOperations.emailExists(newUser.getEmailAddress())) {
                        boolean isSignUp = DatabaseOperations.signUp(newUser);
                        if (isSignUp){
                            JOptionPane.showMessageDialog(
                            null,
                            "Signed Up Successfully",
                            "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else if (DatabaseOperations.userExists(newUser.getUsername())){
                        // Create an error pop-up dialog
                        JOptionPane.showMessageDialog(
                                null,
                                "Username Taken",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else if (DatabaseOperations.emailExists(newUser.getEmailAddress())){
                        // Create an error pop-up dialog
                        JOptionPane.showMessageDialog(
                                null,
                                "Email Taken",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    

                    db.closeConnection();                

                }

            }
        });

        // Action listener to back Button
        backButton.addActionListener(e -> {
            new Welcome().setVisible(true); // Directly instantiate and display Welcome frame

            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
            currentFrame.dispose(); // Dispose the current SignUp frame
        });

        // Add the header, center, and footer panels to the main panel
        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private boolean validateInputs() {
        if (!isValidInput(fNameBox, "Forename is required"))
            return false;
        if (!isValidInput(sNameBox, "Surname is required"))
            return false;
        if (!isValidInput(usernameBox, "Username is required"))
            return false;
        if (!isValidEmail(emailAddressBox))
            return false;
        if (!isValidInput(passwordBox, "Password is required"))
            return false;
        if (!isValidInput(houseNumberBox, "House Number is required"))
            return false;
        if (!isValidInput(roadNameBox, "Road Name is required"))
            return false;
        if (!isValidInput(cityNameBox, "City Name is required"))
            return false;
        if (!isValidLength(postcodeBox, 10, "Postcode must be 10 characters or fewer"))
            return false;

        return true;
    }

    private boolean isValidInput(JTextField textField, String errorMessage) {
        if (textField.getText().trim().isEmpty()) {
            showErrorDialog(errorMessage);
            return false;
        }
        return true;
    }

    private boolean isValidEmail(JTextField textField) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!textField.getText().matches(emailRegex)) {
            showErrorDialog("Invalid email format");
            return false;
        }
        return true;
    }

    private boolean isValidLength(JTextField textField, int maxLength, String errorMessage) {
        if (textField.getText().strip().length() > maxLength) {
            showErrorDialog(errorMessage);
            return false;
        }
        return true;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // JTextFields are treated like Strings
    public static boolean isStringOrNot(JTextField textField) {
        String text = textField.getText();
        // Check if the text is not null and not empty
        return text != null && !text.trim().isEmpty();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SignUp");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(750, 750));
        frame.getContentPane().add(new SignUp());
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }
}