package main.gui.Categories;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.event.*;
import main.db.DatabaseConnectionHandler;
import main.store.Users.*;
// sql
import java.sql.*;
import javax.sql.*;

public class Locomotives extends JPanel {
    
    private static void getLocomotives() {
    DatabaseConnectionHandler db = new DatabaseConnectionHandler();
    db.openConnection();
    
    // Your SQL query
    String sqlQuery = "SELECT " +
            "Product.productCode, " +
            "Product.brandName, " +
            "Product.productName, " +
            "Product.retailPrice, " +
            "Product.productQuantity, " +
            "Individual.modelType, " +
            "Individual.gauge, " +
            "Locomotives.historicalEra, " +
            "Locomotives.DCCCode " +
            "FROM Product " +
            "INNER JOIN Individual ON Product.productCode = Individual.productCode " +
            "INNER JOIN Locomotives ON Individual.productCode = Locomotives.productCode;";

    try {
        PreparedStatement pstmt = db.con.prepareStatement(sqlQuery);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            // Retrieve each column value from the current row of the ResultSet
            String productCode = rs.getString("productCode");
            String brandName = rs.getString("brandName");
            String productName = rs.getString("productName");
            float retailPrice = rs.getFloat("retailPrice");
            int productQuantity = rs.getInt("productQuantity");
            String modelType = rs.getString("modelType");
            String gauge = rs.getString("gauge");
            String historicalEra = rs.getString("historicalEra");
            String dccCode = rs.getString("DCCCode");

            // Print out the values
            System.out.println("Product Code: " + productCode);
            System.out.println("Brand Name: " + brandName);
            System.out.println("Product Name: " + productName);
            System.out.println("Retail Price: " + retailPrice);
            System.out.println("Product Quantity: " + productQuantity);
            System.out.println("Model Type: " + modelType);
            System.out.println("Gauge: " + gauge);
            System.out.println("Historical Era: " + historicalEra);
            System.out.println("DCC Code: " + dccCode);
            System.out.println("-----------------------------------");
        }

        // Close the resources
        rs.close();
        pstmt.close();

    } catch (SQLException e) {
        System.out.println("An error occurred while querying the database:");
        e.printStackTrace();
    }
    // Do not close the db connection here if you need it open for other operations
}

    public static void main(String[] args) {
        getLocomotives();
        
    }

    public Locomotives() {
        // Main panel layout
        setLayout(new BorderLayout(10, 20)); // horizontal and vertical gaps

        // Title label
        JLabel titleLabel = new JLabel("LOCOMOTIVES PAGE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Panel for boxes with some padding
        JPanel boxesPanel = new JPanel();
        boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.Y_AXIS));
        boxesPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // top, left, bottom, right padding

        // Create and add boxes
        boxesPanel.add(createBox("Hello"));
        boxesPanel.add(Box.createRigidArea(new Dimension(0, 15))); // space between boxes
        boxesPanel.add(createBox("Hello world"));
        boxesPanel.add(Box.createRigidArea(new Dimension(0, 15))); // space between boxes
        boxesPanel.add(createBox("Hellow World"));

        // Add boxes panel to the center
        add(boxesPanel, BorderLayout.CENTER);
    }
     
    private JPanel createBox(String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10)); // horizontal and vertical gaps
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // border for the box
    
        // Label at the top of the box
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 16)); // Set the font for the text
        panel.add(label, BorderLayout.NORTH);
    
        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // flow layout with horizontal and vertical gaps
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding inside the button panel
    
        // Buttons
        // JButton addButton = new JButton("Add"); // Remove or comment out this line
        JButton deleteButton = new JButton("Delete");
        JButton editButton = new JButton("Edit");
    
        // Add buttons to the button panel
        // buttonPanel.add(addButton); // Remove or comment out this line
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
    
        // Add the button panel to the bottom of the box
        panel.add(buttonPanel, BorderLayout.SOUTH);
    
        return panel;
    }
}