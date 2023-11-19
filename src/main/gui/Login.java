package main.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import main.db.DatabaseConnectionHandler;
import main.misc.*;
import main.store.Users.*;

// sql
import java.sql.*;

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

        // Adding Email Panel
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
                // Logic to handle login
                String emailInput = emailField.getText();
                char[] password = passwordField.getPassword();
                // For demo purposes, we just print the credentials
                System.out.println("Email: " + emailInput);
                System.out.println("Password: " + new String(password));

                // open database connection
                DatabaseConnectionHandler db = new DatabaseConnectionHandler();
                db.openConnection();

                // Check if user exists
                Statement stmt = null;
                try {
                    stmt = db.con.createStatement();

                    PreparedStatement pstmt = db.con.prepareStatement("SELECT * FROM User WHERE email=? AND hashed_password=?");
                    PreparedStatement countStatement = db.con.prepareStatement("SELECT COUNT(*) FROM User WHERE email=? AND hashed_password=?");

                    
                    pstmt.setString(1, emailInput); //email = *
                    pstmt.setString(2, new String(password)); // need to encrypt given user input

                    countStatement.setString(1, emailInput);
                    countStatement.setString(2, new String(password));

                    // Placeholders for user match count and details
                    ResultSet count = countStatement.executeQuery();
                    ResultSet res = pstmt.executeQuery();
                    // If User exists
                    while (res.next()) {
                        // count.next();
                        // System.out.println(count.getString(1));
                        String email = res.getString(4);
                        String hashed_password = res.getString(3);
                        
                        // Decrypt hashed_password
                        String decryptedPassword = Encryption.decrypt(hashed_password, User.cryptoPassword);
                        if (count.next()){
                            if (decryptedPassword.equals(new String(password)) && email.equals(emailInput)) {
                                // Need to encrypt password
                                System.out.println("Log In Successful");
                            }
                        }
                    } else {
                        System.out.println("Invalid Credentials");
                    }

                    pstmt.close();
                }

                catch (SQLException ex) {
                    ex.printStackTrace();
                }
                catch (Exception ex){
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
