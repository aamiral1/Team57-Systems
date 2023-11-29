package gui;

import javax.swing.*;

import db.DatabaseConnectionHandler;
import db.DatabaseOperations;
import store.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.regex.Pattern;

public class PaymentWindow extends JFrame {

    public PaymentWindow() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setTitle("Payment Window");

        // Create components for first name, last name, postcode, house number, road,
        // city, card number, ccv code, etc.
        JTextField cardNameField = new JTextField(20);
        JTextField cardNumberField = new JTextField(20);
        JTextField cvv = new JTextField(3);
        // create a date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JFormattedTextField expiryDateField = new JFormattedTextField(dateFormat);
        expiryDateField.setColumns(10);

        JButton confirmAndPayButton = new JButton("Confirm & Pay");
        JButton cancelButton = new JButton("Cancel Order");

        // Create layout and add components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(10, 2, 5, 5));

        panel.add(new JLabel("Full Name (as on card):"));
        panel.add(cardNameField);

        panel.add(new JLabel("Card Number:"));
        panel.add(cardNumberField);

        panel.add(new JLabel("Expiry Date (yyyy-mm-dd):"));
        panel.add(expiryDateField);

        panel.add(new JLabel("CVV:"));
        panel.add(cvv);

        panel.add(confirmAndPayButton);
        panel.add(cancelButton);

        confirmAndPayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform actions when Confirm & Pay button is clicked
                // Add logic to process payment details

                // User Inputted Card Details
                String cardName = cardNameField.getText();
                String cardNumber = cardNumberField.getText().strip().replace(" ", "");
                String expiryDate = expiryDateField.getText();
                String cvvCode = cvv.getText();

                // Class to validate card methods
                class cardValidator {
                    // Validate inputted card number
                    private static Boolean validateCardNumber(String cardNumber) {
                        // Check if the card number has 16 digits
                        if (!Pattern.matches("^\\d{16}$", cardNumber)) {
                            System.out.println("AFDF");
                            return false;
                        }

                        // Check if the card number passes the Luhn algorithm
                        int sum = 0;
                        boolean doubleDigit = false;
                        for (int i = cardNumber.length() - 1; i >= 0; i--) {
                            int digit = cardNumber.charAt(i) - '0';
                            if (doubleDigit) {
                                digit *= 2;
                                if (digit > 9) {
                                    digit -= 9;
                                }
                            }
                            sum += digit;
                            doubleDigit = !doubleDigit;
                        }
                        return (sum % 10 == 0);
                    }

                    private static boolean validateExpirationDate(String expirationDate) {
                        try {
                            LocalDate expiry = LocalDate.parse(expirationDate,
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            LocalDate current = LocalDate.now();
                            return expiry.isAfter(current);
                        } catch (DateTimeParseException e) {
                            return false; // Invalid date format
                        }
                    }

                    private static boolean validateCVV(String cvv) {
                        // Check if the CVV is numeric and has the correct length
                        return Pattern.matches("^\\d{3}$", cvv);
                    }

                    public static boolean checkCard(String cardNumber, String expirationDate, String cvv) {
                        boolean isCardValid = cardValidator.validateCardNumber(cardNumber);
                        boolean isDateValid = cardValidator.validateExpirationDate(expirationDate);
                        boolean isCvvValid = cardValidator.validateCVV(cvv);

                        return isCardValid && isDateValid && isCvvValid;
                    }
                }

                // Throw basic validation error popups
                if (cardName.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Please enter the full name as on the card.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (cardNumber.isEmpty() || cardNumber.length() != 16 || !cardNumber.matches("\\d{16}") || !cardValidator.validateCardNumber(cardNumber)) {
                    JOptionPane.showMessageDialog(panel, "Please enter a valid 16-digit card number."+cardValidator.validateCardNumber(cardNumber), "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (expiryDate.isEmpty() || !cardValidator.validateExpirationDate(expiryDate)) {
                    JOptionPane.showMessageDialog(panel, "Please enter a valid expiry date in the format yyyy-mm-dd.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (cvvCode.isEmpty() || !cardValidator.validateCVV(cvvCode)) {
                    JOptionPane.showMessageDialog(panel, "Please enter a valid CVV.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Perform final validation check
                Boolean isCardValid = cardValidator.checkCard(cardNumber, expiryDate,
                        cvvCode);
                // Create a bank detail object
                if (isCardValid) {
                    // Create a BankingDetail Object and upload to database
                    BankDetail userBankDetail = new BankDetail(cardNameField.getText(), cardNumberField.getText(),
                            expiryDateField.getText(), cvv.getText());

                    // open Database connection
                    DatabaseConnectionHandler db = new DatabaseConnectionHandler();
                    db.openConnection();
                    Boolean addedBankDetail = DatabaseOperations.addBankDetail(UserManager.getCurrentUser(),
                            userBankDetail, db.con);

                    System.out.println("Banking Detail Update Status: " + addedBankDetail);

                    // update User's banking details in database

                } else {
                    JOptionPane.showMessageDialog(null, "Card Details are Invalid");
                }

                System.out.println("Confirm and pay button clicked");

            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform actions when Cancel button is clicked
                dispose(); // Close the current window
            }
        });

        add(panel);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaymentWindow window = new PaymentWindow();
        });
    }
}
