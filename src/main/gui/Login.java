package main.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import main.db.DatabaseConnectionHandler;

// sql
import java.sql.*;
import javax.sql.*;

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
                // Logic to handle login
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                // For demo purposes, we just print the credentials
                System.out.println("Username: " + username);
                System.out.println("Password: " + new String(password));

                // open database connection
                DatabaseConnectionHandler db = new DatabaseConnectionHandler();
                db.openConnection();

                // Check if user exists
                Statement stmt = null;
                try {
                    stmt = db.con.createStatement();

                    PreparedStatement pstmt = db.con
                            .prepareStatement("SELECT * FROM User WHERE name=? AND hashed_password=?");
                    pstmt.setString(1, username);
                    pstmt.setString(2, new String(password));

                    ResultSet res = pstmt.executeQuery();

                    // If User exists
                    if (res.next()) {
                        System.out.println("Log In Successful");
                    } else {
                        System.out.println("Invalid Credentials");
                    }

                    pstmt.close();
                }

                catch (SQLException ex) {
                    ex.printStackTrace();
                }

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
