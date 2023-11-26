package main.gui;
import main.db.DatabaseConnectionHandler;
import main.db.DatabaseOperations;
import main.store.Users.User;
import main.store.Users.UserManager;

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

    public static HashMap<String,List<String[]>> getBoxedProducts(String productType) {

        // Open a connection to the database
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

        HashMap<String, List<String[]>> groupedProductDetails = null;

        System.out.println("getBoxedProducts method has been called successfully");

        if(productType.equals("Track Packs")) {

            displayInduvidualProductsUI.currentProductType = "Track Packs";
            Statement statement = null;
            ResultSet resultSet = null;
            groupedProductDetails = new HashMap<>();
        
            try {

                // Create a statement
                statement = db.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
                // Execute the query to get boxed sets
                String sqlQueryBoxedSets = "SELECT DISTINCT bsc.boxedSetId FROM BoxedSetContents bsc;";
                resultSet = statement.executeQuery(sqlQueryBoxedSets);
        
                // Loop through each boxedSetId
                while (resultSet.next()) {

                    String boxedSetId = resultSet.getString("boxedSetId");
                    groupedProductDetails.put(boxedSetId, new ArrayList<>());
        
                    // Now, get the individual product details for each boxedSetId
                    String sqlQueryProductDetails = "SELECT " +
                            "bsc.boxedSetId, " + 
                            "bsc.individual_productCode, " + 
                            "bsc.quantity, " + 
                            "i.modelType, " +
                            "i.gauge, " +
                            "p.brandName, " +
                            "p.productName, " +
                            "p.retailPrice, " +
                            "p.productQuantity " +
                            "FROM BoxedSetContents bsc " +
                            "INNER JOIN Individual i ON bsc.individual_productCode = i.productCode " +
                            "INNER JOIN Product p ON i.productCode = p.productCode " +
                            "WHERE bsc.boxedSetId = ?;"; // Use a prepared statement for the parameter
        
                    PreparedStatement preparedStatement = db.con.prepareStatement(sqlQueryProductDetails);
                    preparedStatement.setString(1, boxedSetId);
                    ResultSet resultSetProductDetails = preparedStatement.executeQuery();
        
                    // Process the ResultSet for product details
                    while (resultSetProductDetails.next()) {
                        String[] details = new String[8]; // Adjust the size based on the number of fields
                        details[0] = resultSetProductDetails.getString("quantity");
                        details[1] = resultSetProductDetails.getString("individual_productCode");
                        details[2] = resultSetProductDetails.getString("modelType");
                        details[3] = resultSetProductDetails.getString("gauge");
                        details[4] = resultSetProductDetails.getString("brandName");
                        details[5] = resultSetProductDetails.getString("productName"); 
                        details[6] = resultSetProductDetails.getString("retailPrice"); 
                        
                        groupedProductDetails.get(boxedSetId).add(details);
                    }

                    resultSetProductDetails.close();
                    preparedStatement.close();
                }
        
            } catch (SQLException e) {
                // Handle the exception appropriately in your application
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null && !resultSet.isClosed()) resultSet.close();
                    if (statement != null && !statement.isClosed()) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                // Only close the connection if you are done with all database operations
                if (db != null) db.closeConnection();
            }
        
            return groupedProductDetails;
        
        } else { 
            
            // Return and empty array
            return groupedProductDetails;

        } 
    }
    
    public static void createAndShowGroupedGUI(HashMap<String, List<String[]>> groupedProductDetails) {

        System.out.println("createAndShowGroupedGUI method has been called succesfully");

        JFrame frame = new JFrame("Grouped Product Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Grouped Product Details Page", SwingConstants.CENTER);

        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        northPanel.add(headerLabel, BorderLayout.NORTH);

        // Main Panel for grouped product details
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Iterate over the HashMap and create UI components for each group
        for (String boxedSetId : groupedProductDetails.keySet()) {

            // Panel for each boxed set group
            JPanel boxedSetPanel = new JPanel();
            boxedSetPanel.setLayout(new BoxLayout(boxedSetPanel, BoxLayout.Y_AXIS));
            boxedSetPanel.setBorder(BorderFactory.createTitledBorder("Boxed Set ID: " + boxedSetId));

            // Retrieve the list of product details for the current boxed set
            List<String[]> products = groupedProductDetails.get(boxedSetId);

            // Create a panel for each product and add it to the boxed set panel
            for (String[] details : products) {

                JPanel productPanel = createBoxProductPanel(details); // Assume createProductPanel is a method that creates a JPanel with product details

                boxedSetPanel.add(productPanel);
            }

            // Add "Add to Cart" button to each product panel
            JButton addToCartButton = new JButton("Add to Cart");

            addToCartButton.addActionListener(e -> {
                // Implement action listener
            });

            boxedSetPanel.add(addToCartButton);

            mainPanel.add(boxedSetPanel); // Add each grouped panel to the main panel
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Add a button to navigate back to home or other parts of the UI
        // ...

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

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
    
        // Add 'Add to Cart' button
        /* JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // addToCart(details); // Implement this method to handle adding the product to the cart
            }
        });
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2; // The button will span two columns
        gbc.fill = GridBagConstraints.HORIZONTAL;
        productPanel.add(addToCartButton, gbc); */
    
        productPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Sets a border for the product panel
    
        return productPanel;
    }

}
