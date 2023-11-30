package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

import db.*;

import store.UserManager;

public class Login extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public Login() {
        // Create components
        emailField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("LOGIN");

        // Layout setup
        setLayout(new GridLayout(3, 1));

        // Adding Username Panel
        JPanel emailPanel = new JPanel();
        emailPanel.add(new JLabel("Email:"));
        emailPanel.add(emailField);
        add(emailPanel);

        // Adding Password Panel
        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(passwordField);
        add(passwordPanel);

        // Adding Login Button and Remember Me Checkbox
        JPanel loginPanel = new JPanel();

        loginPanel.add(loginButton);
        add(loginPanel);

        // Action Listener for login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Boolean loginStatus = false;
                // Logic to handle login
                String emailInput = emailField.getText();
                char[] password = passwordField.getPassword();
                // For demo purposes, we just print the credentials
                System.out.println("Email: " + emailInput);
                System.out.println("Password: " + new String(password));

                // open database connection
                DatabaseConnectionHandler db = new DatabaseConnectionHandler();
                db.openConnection();

                // verify login
                if (password != null) {

                    Connection connection = db.getConnection();

                    loginStatus = DatabaseOperations.verifyLogin(db.con, emailInput, password);
                    if (loginStatus) {
                        // Open customer UI
                        System.out.println("Opening Customer View");
                        new CustomerUI().setVisible(true);
                        // Check if there are no pending orders under current user id
                        DatabaseOperations.createOrderLine(UserManager.getCurrentUser().getUserID(), connection);

                        // dispose login view
                        Login.this.dispose();
                        // JFrame customerFrame = new JFrame("Customer Dashboard");
                        // customerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        // customerFrame.setSize(300,200);

                        // CustomerUI customerUI = new CustomerUI();
                        // customerFrame.add(customerUI);

                        // // Make the frame visible
                        // customerFrame.setVisible(true);
                    }
                } else {
                    // show error box
                    JOptionPane.showMessageDialog(Login.this, "Invalid Credentials");
                }

                if (loginStatus) {
                    // Hide the login panel
                    Login.this.setVisible(false);

                    // Create the main JFrame
                    JFrame frame = new JFrame("Customer Dashboard");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(800, 600); // Set the size of the frame
                    frame.setLocationRelativeTo(null);

                    // Add the CustomerUI instance to the frame
                    CustomerUI customerUI = new CustomerUI();
                    frame.add(customerUI);

                    // Make the frame visible
                    frame.setVisible(true);

                    // customerUI.setTitle("Customer Dashboard");
                    // customerUI.setLocationRelativeTo(null); // Center on screen
                    // customerUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    // customerUI.setMinimumSize(new Dimension(800, 600));
                    // customerUI.setVisible(true);
                }

                db.closeConnection();
            }
        });

        // Final touches on the JFrame
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {

        // Run the application
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Login();
            }
        });
    }
}
