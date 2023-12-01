package gui;

import db.DatabaseConnectionHandler;
//import main.db.DatabaseOperations;
//import main.store.Users.User;
//import main.store.Users.UserManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class displayingBoxedProductsUI {

    //-----------------------------------------------------------------------------------------------------------------------

    public static HashMap<String, List<String[]>> getBoxedProducts(String productType) {
        // Open a connection to the database
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();
    
        HashMap<String, List<String[]>> groupedProductDetails = new HashMap<>();
    
        System.out.println("getBoxedProducts method has been called successfully");

        if (productType.equals("Track Packs")) {
    
        try (Statement statement = db.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
    
            // Execute the query to get all boxed sets and their contents
            String sqlQuery = "SELECT" +
                    " BoxedSet.boxedSetID," +
                    " BoxedSetContents.quantity," +
                    " BoxedSet.productCode as boxedSetProductCode," +
                    " BoxedSetContents.product_productCode, " +
                    " Individual.modelType," +
                    " Individual.gauge," +
                    " Product.brandName," +
                    " Product.productName," +
                    " Product.retailPrice," +
                    " Product.productQuantity" +
                    " FROM" +
                    " BoxedSet" +
                    " INNER JOIN BoxedSetContents ON BoxedSet.boxedSetID = BoxedSetContents.boxedSetID" +
                    " INNER JOIN Individual ON BoxedSetContents.product_productCode = Individual.productCode" +
                    " INNER JOIN Product ON Individual.productCode = Product.productCode" +
                    " WHERE BoxedSet.productCode LIKE 'P%'";
    
            try (ResultSet resultSet = statement.executeQuery(sqlQuery)) {
                // Process the ResultSet
                while (resultSet.next()) {
                    String boxedSetId = resultSet.getString("boxedSetId");
    
                    groupedProductDetails.putIfAbsent(boxedSetId, new ArrayList<>());
    
                    String[] details = new String[8]; // Adjust the size based on the number of fields
                    details[0] = resultSet.getString("quantity");
                    details[1] = resultSet.getString("product_productCode");
                    details[2] = resultSet.getString("modelType");
                    details[3] = resultSet.getString("gauge");
                    details[4] = resultSet.getString("brandName");
                    details[5] = resultSet.getString("productName");
                    details[6] = resultSet.getString("retailPrice");
    
                    groupedProductDetails.get(boxedSetId).add(details);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider a more robust error handling strategy
        } finally {
            // Close the database connection
            if (db != null) {
                db.closeConnection();
            }
        }
    
        return groupedProductDetails;

    } else if (productType.equals("Train Sets")) {

        try (Statement statement = db.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
    
            // Execute the query to get all boxed sets and their contents
            String sqlQuery = "SELECT" +
                    " BoxedSet.boxedSetID," +
                    " BoxedSetContents.quantity," +
                    " BoxedSet.productCode as boxedSetProductCode," +
                    " BoxedSetContents.product_productCode, " +
                    " Individual.modelType," +
                    " Individual.gauge," +
                    " Product.brandName," +
                    " Product.productName," +
                    " Product.retailPrice," +
                    " Product.productQuantity" +
                    " FROM" +
                    " BoxedSet" +
                    " INNER JOIN BoxedSetContents ON BoxedSet.boxedSetID = BoxedSetContents.boxedSetID" +
                    " INNER JOIN Individual ON BoxedSetContents.product_productCode = Individual.productCode" +
                    " INNER JOIN Product ON Individual.productCode = Product.productCode" +
                    " WHERE BoxedSet.productCode LIKE 'M%'";

    
            try (ResultSet resultSet = statement.executeQuery(sqlQuery)) {
                // Process the ResultSet
                while (resultSet.next()) {
                    String boxedSetId = resultSet.getString("boxedSetId");
    
                    groupedProductDetails.putIfAbsent(boxedSetId, new ArrayList<>());
    
                    String[] details = new String[8]; // Adjust the size based on the number of fields
                    details[0] = resultSet.getString("quantity");
                    details[1] = resultSet.getString("product_productCode");
                    details[2] = resultSet.getString("modelType");
                    details[3] = resultSet.getString("gauge");
                    details[4] = resultSet.getString("brandName");
                    details[5] = resultSet.getString("productName");
                    details[6] = resultSet.getString("retailPrice");
    
                    groupedProductDetails.get(boxedSetId).add(details);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider a more robust error handling strategy
        } finally {
            // Close the database connection
            if (db != null) {
                db.closeConnection();
            }
        }
    
        return groupedProductDetails;
    } else {

        // Return and empty array
        return groupedProductDetails;
    }
}
    

    //-----------------------------------------------------------------------------------------------------------------------
    
    public static void createAndShowGroupedGUI(HashMap<String, List<String[]>> groupedProductDetails) {
        System.out.println("createAndShowGroupedGUI method has been called successfully");

        DatabaseConnectionHandler db = new DatabaseConnectionHandler(); 
        db.openConnection(); // Make sure to open a connection to the database

        JFrame frame = new JFrame("Boxed Sets Product Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Grouped Product Details Page", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        northPanel.add(headerLabel, BorderLayout.NORTH);

        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> {
            // Close the current frame
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(homeButton);
            if (currentFrame != null) {
                currentFrame.dispose();
            }
        
            // Create a new frame for the CustomerUI
            JFrame customerFrame = new JFrame("Customer Page");
            customerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            customerFrame.setSize(1000, 700); // Adjust the size to accommodate title and labels
            customerFrame.add(new CustomerUI());
            customerFrame.setLocationRelativeTo(null); // Center on screen
            customerFrame.setVisible(true);
        });
        northPanel.add(homeButton, BorderLayout.WEST);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        for (String boxedSetId : groupedProductDetails.keySet()) {
            String productCode = getProductCodeForBoxedSet(boxedSetId, db);

            JPanel boxedSetPanel = new JPanel();
            boxedSetPanel.setLayout(new BoxLayout(boxedSetPanel, BoxLayout.Y_AXIS));
            boxedSetPanel.setBorder(BorderFactory.createTitledBorder("Product Code: " + (productCode != null ? productCode : "Unknown")));

            List<String[]> products = groupedProductDetails.get(boxedSetId);
            for (String[] productDetails : products) {
                JPanel productPanel = createBoxProductPanel(productDetails); // Method to create a JPanel for product details
                boxedSetPanel.add(productPanel);
            }

            mainPanel.add(boxedSetPanel);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(northPanel, BorderLayout.NORTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        db.closeConnection(); // Close the connection when done
    }

    private static String getProductCodeForBoxedSet(String boxedSetId, DatabaseConnectionHandler db) {
        String productCode = null;
        String productCodeQuery = "SELECT productCode FROM BoxedSet WHERE boxedSetID = ?";
        try (PreparedStatement ps = db.con.prepareStatement(productCodeQuery)) {
            ps.setString(1, boxedSetId);
            try (ResultSet rsProductCode = ps.executeQuery()) {
                if (rsProductCode.next()) {
                    productCode = rsProductCode.getString("productCode");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productCode;
    }

    //-----------------------------------------------------------------------------------------------------------------------

    private static JPanel createBoxProductPanel(String[] details) {

        System.out.println("createBoxProductPanel method has been called successfully");

        // Assuming 'details' contains: quantity, productCode, modelType, gauge, brandName, productName, retailPrice
        JPanel productPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2); // Provides some spacing between components
        gbc.anchor = GridBagConstraints.WEST;
    
        // Add product details to the panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        productPanel.add(new JLabel("Product Code:"), gbc);
        gbc.gridx = 1;
        productPanel.add(new JLabel(details[1]), gbc);
    
        gbc.gridx = 0;
        gbc.gridy++;
        productPanel.add(new JLabel("Model Type:"), gbc);
        gbc.gridx = 1;
        productPanel.add(new JLabel(details[2]), gbc);
    
        gbc.gridx = 0;
        gbc.gridy++;
        productPanel.add(new JLabel("Gauge:"), gbc);
        gbc.gridx = 1;
        productPanel.add(new JLabel(details[3]), gbc);
    
        gbc.gridx = 0;
        gbc.gridy++;
        productPanel.add(new JLabel("Brand Name:"), gbc);
        gbc.gridx = 1;
        productPanel.add(new JLabel(details[4]), gbc);
    
        gbc.gridx = 0;
        gbc.gridy++;
        productPanel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        productPanel.add(new JLabel(details[5]), gbc);
    
        gbc.gridx = 0;
        gbc.gridy++;
        productPanel.add(new JLabel("Retail Price:"), gbc);
        gbc.gridx = 1;
        productPanel.add(new JLabel(details[6]), gbc);
    
        gbc.gridx = 0;
        gbc.gridy++;
        productPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        productPanel.add(new JLabel(details[0]), gbc);
    
        productPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Sets a border for the product panel
    
        return productPanel;
    }

}
