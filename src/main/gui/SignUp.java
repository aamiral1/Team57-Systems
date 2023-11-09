package main.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class SignUp extends JPanel {
    private JButton sign;
    private JTextArea fNameBox;
    private JTextArea jcomp3;
    private JLabel fName;
    private JLabel sName;
    private JTextArea houseNumberBox;
    private JLabel houseNumber;
    private JTextField roadNameBox;
    private JTextField cityNameBox;
    private JTextField postcodeBox;
    private JLabel roadName;
    private JLabel postcode;
    private JLabel cityName;
    private JTextField sNameBox;
    private JLabel jcomp15;

    public SignUp() {
        //construct components
        sign = new JButton ("Sign up");
        fNameBox = new JTextArea (5, 5);
        jcomp3 = new JTextArea (5, 5);
        fName = new JLabel ("Forename :");
        sName = new JLabel ("Surname :");
        houseNumberBox = new JTextArea (5, 5);
        houseNumber = new JLabel ("House Number :");
        roadNameBox = new JTextField (5);
        cityNameBox = new JTextField (5);
        postcodeBox = new JTextField (5);
        roadName = new JLabel ("Road Name :");
        postcode = new JLabel ("Postcode :");
        cityName = new JLabel ("City Name :");
        sNameBox = new JTextField (5);
        jcomp15 = new JLabel ("WELCOME TO THE LOGIN PAGE ");

        //adjust size and set layout
        setPreferredSize (new Dimension (752, 457));
        setLayout (null);

        //add components
        add (sign);
        add (fNameBox);
        add (jcomp3);
        add (fName);
        add (sName);
        add (houseNumberBox);
        add (houseNumber);
        add (roadNameBox);
        add (cityNameBox);
        add (postcodeBox);
        add (roadName);
        add (postcode);
        add (cityName);
        add (sNameBox);
        add (jcomp15);

        //set component bounds (only needed by Absolute Positioning)
        sign.setBounds (320, 340, 100, 20);
        fNameBox.setBounds (290, 125, 170, 20);
        jcomp3.setBounds (290, 125, 170, 20);
        fName.setBounds (220, 120, 65, 25);
        sName.setBounds (225, 155, 60, 20);
        houseNumberBox.setBounds (290, 185, 170, 20);
        houseNumber.setBounds (195, 185, 100, 15);
        roadNameBox.setBounds (290, 215, 170, 20);
        cityNameBox.setBounds (290, 245, 170, 20);
        postcodeBox.setBounds (290, 270, 170, 20);
        roadName.setBounds (210, 215, 75, 15);
        postcode.setBounds (220, 270, 65, 15);
        cityName.setBounds (215, 245, 65, 15);
        sNameBox.setBounds (290, 155, 170, 20);
        jcomp15.setBounds (270, 80, 200, 20);
    }


    public static void main (String[] args) {
        JFrame frame = new JFrame ("SignUp");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add (new SignUp());
        frame.pack();
        frame.setVisible (true);
    }
}

// Implement action listener for signup button
    // Open database connection (ensure VPN is on)
    // Create a instance of the User class with entered inputs (using constructor)
    // Create a new row in the database with the attributes of the user instance
    // Add a sql query that will add a new row with columsn matching the attributes of the user instance