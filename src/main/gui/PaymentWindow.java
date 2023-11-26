//package main.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PaymentWindow extends JFrame {

    public PaymentWindow() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setTitle("Payment Window");

        // Create components for first name, last name, postcode, house number, road, city, card number, ccv code, etc.
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField postCodeField = new JTextField(20);
        JTextField houseNumberField = new JTextField(20);
        JTextField roadField = new JTextField(20);
        JTextField cityField = new JTextField(20);
        JTextField cardNumberField = new JTextField(20);
        JTextField ccvCodeField = new JTextField(20);

        JButton confirmAndPayButton = new JButton("Confirm & Pay");
        JButton cancelButton = new JButton("Cancel Order");

        // Create layout and add components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(10, 2, 5, 5));

        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);

        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);

        panel.add(new JLabel("Post Code:"));
        panel.add(postCodeField);

        panel.add(new JLabel("House Number:"));
        panel.add(houseNumberField);

        panel.add(new JLabel("Road:"));
        panel.add(roadField);

        panel.add(new JLabel("City:"));
        panel.add(cityField);

        panel.add(new JLabel("Card Number:"));
        panel.add(cardNumberField);

        panel.add(new JLabel("CCV Code:"));
        panel.add(ccvCodeField);

        panel.add(confirmAndPayButton);
        panel.add(cancelButton);

        confirmAndPayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform actions when Confirm & Pay button is clicked
                // Add logic to process payment details
                JOptionPane.showMessageDialog(null, "Payment confirmed!");
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform actions when Cancel button is clicked
                dispose();  // Close the current window
            }
        });

        add(panel);
        setLocationRelativeTo(null);  // Center the window
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaymentWindow window = new PaymentWindow();
        });
    }
}
