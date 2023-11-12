package main.gui;

import java.awt.*;
import javax.swing.*;

public class SignUp extends JPanel {
    private JButton sign;
    private JTextArea fNameBox, houseNumberBox, jcomp3;
    private JLabel fName, sName, houseNumber, roadName, postcode, cityName, jcomp15;
    private JTextField roadNameBox, cityNameBox, postcodeBox, sNameBox;

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
        fNameBox = new JTextArea(1, 20);
        sName = new JLabel("Surname:");
        sNameBox = new JTextField(20);
        houseNumber = new JLabel("House Number:");
        houseNumberBox = new JTextArea(1, 20);
        roadName = new JLabel("Road Name:");
        roadNameBox = new JTextField(20);
        cityName = new JLabel("City Name:");
        cityNameBox = new JTextField(20);
        postcode = new JLabel("Postcode:");
        postcodeBox = new JTextField(20);

        // Add components to center panel using GridBagLayout constraints
        gbc.gridx = 0; gbc.gridy = 0; centerPanel.add(fName, gbc);
        gbc.gridx = 1; gbc.gridy = 0; centerPanel.add(fNameBox, gbc);
        gbc.gridx = 0; gbc.gridy = 1; centerPanel.add(sName, gbc);
        gbc.gridx = 1; gbc.gridy = 1; centerPanel.add(sNameBox, gbc);
        gbc.gridx = 0; gbc.gridy = 2; centerPanel.add(houseNumber, gbc);
        gbc.gridx = 1; gbc.gridy = 2; centerPanel.add(houseNumberBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3; centerPanel.add(roadName, gbc);
        gbc.gridx = 1; gbc.gridy = 3; centerPanel.add(roadNameBox, gbc);
        gbc.gridx = 0; gbc.gridy = 4; centerPanel.add(cityName, gbc);
        gbc.gridx = 1; gbc.gridy = 4; centerPanel.add(cityNameBox, gbc);
        gbc.gridx = 0; gbc.gridy = 5; centerPanel.add(postcode, gbc);
        gbc.gridx = 1; gbc.gridy = 5; centerPanel.add(postcodeBox, gbc);

        // Create a footer panel for the sign-up button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sign = new JButton("Sign up");
        footerPanel.add(sign);

        // Add the header, center, and footer panels to the main panel
        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
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


// Implement action listener for signup button
    // Open database connection (ensure VPN is on)
    // Create a instance of the User class with entered inputs (using constructor)
    // Create a new row in the database with the attributes of the user instance
    // Add a sql query that will add a new row with columsn matching the attributes of the user instance