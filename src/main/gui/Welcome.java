package main.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Welcome extends JFrame {
    private JButton loginButton;
    private JButton signUpButton;

    public Welcome() {
        super("Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Initialize buttons
        loginButton = new JButton("Login");
        signUpButton = new JButton("Sign Up");

        // Add action listener for the login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Assuming Login class is a JFrame
                new Login().setVisible(true);
                Welcome.this.dispose(); // Close the Welcome window
            }
        });

        // Add action listener for the sign up button
        signUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSignUpScreen();
            }
        });

        // Layout the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        // Add components to the main frame
        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private void showSignUpScreen() {
        // Dispose the current welcome screen
        this.dispose();

        // Create a frame to hold the SignUp panel
        JFrame frame = new JFrame("SignUp");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new SignUp());
        frame.pack(); // Adjusts window to fit components
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Welcome().setVisible(true);
            }
        });
    }
}