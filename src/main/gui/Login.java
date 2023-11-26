package main.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import main.db.*;
import main.misc.*;
import main.store.Users.*;

// sql
import java.sql.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public Login() {
        // Create components
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("LOGIN");

        // Layout setup
        setLayout(new GridLayout(3, 1));

        // Adding Username Panel
        JPanel usernamePanel = new JPanel();
        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(usernameField);
        add(usernamePanel);

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
                String usernameInput = usernameField.getText();
                char[] password = passwordField.getPassword();
                // For demo purposes, we just print the credentials
                System.out.println("Username: " + usernameInput);
                System.out.println("Password: " + new String(password));

                // open database connection
                DatabaseConnectionHandler db = new DatabaseConnectionHandler();
                db.openConnection();

                // verify login
                if (password != null) {
                    loginStatus = DatabaseOperations.verifyLogin(db.con, usernameInput, password);
                    if (loginStatus) {
                        // Open customer UI
                        System.out.println("Opening Customer View");
                        new CustomerUI().setVisible(true);
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
                    // open customer dashboard if login is successful
                    Login.this.setVisible(false); // or Login.this.dispose();

                    // Open the CustomerUI window
                    CustomerUI customerUI = new CustomerUI();
                    // Configure board settings
                    customerUI.setTitle("Customer Dashboard");
                    customerUI.setLocationRelativeTo(null); // Center on screen
                    customerUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    customerUI.setMinimumSize(new Dimension(800, 600));
                    customerUI.setVisible(true);
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
