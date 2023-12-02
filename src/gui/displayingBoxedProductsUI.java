package gui;

import db.DatabaseConnectionHandler;
import db.DatabaseOperations;
import misc.UniqueUserIDGenerator;
import store.User;
import store.UserManager;

//import main.db.DatabaseOperations;
//import main.store.Users.User;
//import main.store.Users.UserManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
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

        JButton viewCartButton = new JButton("View Cart");

        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCart(); // Calls the view cart method
                frame.dispose(); // Closes the current window
            }
        });

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

        // Adding the view cart button to the top right
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(viewCartButton);
        northPanel.add(topPanel, BorderLayout.CENTER);

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

            // Add a JSpinner for the quantity of the boxed set - user can select quantity they want, this should be stored when adding to cart.
            SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1); 
            JSpinner quantitySpinner = new JSpinner(spinnerModel);
            boxedSetPanel.add(new JLabel("Select Quantity: "));
            boxedSetPanel.add(quantitySpinner);


            JButton addToCart = new JButton("Add to cart");
            addToCart.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    User currentUserRole = UserManager.getCurrentUser();
                    String usersID = currentUserRole.getUserID();
                    int selectedQuantity = (int) quantitySpinner.getValue();
                    String productCodeFromUI = productCode; // The productCode of the add to cart which was clicked
                    float retailPrice = 0.0f;
                    float lineCost = 0.0f;
            
                    try {
                        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
                        db.openConnection();
            
                        // Query to get retailPrice using the productCode
                        String queryRetailPrice = "SELECT retailPrice FROM Product WHERE productCode = ?";
                        try (PreparedStatement ps = db.con.prepareStatement(queryRetailPrice)) {
                            ps.setString(1, productCodeFromUI);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    retailPrice = rs.getFloat("retailPrice");
                                    lineCost = retailPrice * selectedQuantity;
                                }
                            }
                        }
            
                        // Find an existing 'pending' order or create a new one
                        String orderNumber = null;
                        String ord_num_query = "SELECT order_number FROM OrderDetails WHERE user_id = ? AND order_status = 'pending'";
                        try (PreparedStatement pstmt = db.con.prepareStatement(ord_num_query)) {
                            pstmt.setString(1, usersID);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                if (rs.next()) {
                                    orderNumber = rs.getString("order_number");
                                } else {
                                    // Create a new order if no pending order exists
                                    String createOrderQuery = "INSERT INTO OrderDetails (user_id, order_status) VALUES (?, 'pending')";
                                    try (PreparedStatement psCreateOrder = db.con.prepareStatement(createOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
                                        psCreateOrder.setString(1, usersID);
                                        psCreateOrder.executeUpdate();
                                        try (ResultSet generatedKeys = psCreateOrder.getGeneratedKeys()) {
                                            if (generatedKeys.next()) {
                                                orderNumber = generatedKeys.getString(1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
            
                        if (orderNumber != null) {
                            // Update the order line if it exists or insert a new one
                            String updateOrderLineSQL = "UPDATE OrderLine SET Quantity = Quantity + ?, Line_cost = Line_cost + ? WHERE order_number = ? AND productCode = ?";
                            try (PreparedStatement pstmtUpdate = db.con.prepareStatement(updateOrderLineSQL)) {
                                pstmtUpdate.setInt(1, selectedQuantity);
                                pstmtUpdate.setFloat(2, lineCost);
                                pstmtUpdate.setString(3, orderNumber);
                                pstmtUpdate.setString(4, productCodeFromUI);
                                int rowsAffected = pstmtUpdate.executeUpdate();
                                
                                // If the update did not affect any rows, then the item is not in the cart and we insert a new line
                                if (rowsAffected == 0) {
                                    String insertOrderLineSQL = "INSERT INTO OrderLine (order_number, line_id, productCode, Quantity, Line_cost) VALUES (?, ?, ?, ?, ?)";
                                    try (PreparedStatement pstmtInsert = db.con.prepareStatement(insertOrderLineSQL)) {
                                        pstmtInsert.setString(1, orderNumber);
                                        pstmtInsert.setString(2, UniqueUserIDGenerator.generateUniqueUserID());
                                        pstmtInsert.setString(3, productCodeFromUI);
                                        pstmtInsert.setInt(4, selectedQuantity);
                                        pstmtInsert.setFloat(5, lineCost);
                                        pstmtInsert.executeUpdate();
                                    }
                                }
                            }
                            JOptionPane.showMessageDialog(null, "Cart updated successfully!");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace(); // Proper error handling should be implemented
                    } finally {
                        db.closeConnection(); // Ensure the database connection is closed in the finally block
                    }
                }
            });

            
            boxedSetPanel.add(addToCart);
        
            mainPanel.add(boxedSetPanel);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(northPanel, BorderLayout.NORTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Method to view items added to cart
    static void viewCart() {
        JFrame cartFrame = new JFrame("View Cart");
        cartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cartFrame.setLayout(new BorderLayout(10, 10));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        headerPanel.add(new JLabel("Order Number"));
        headerPanel.add(new JLabel("Product Code"));
        headerPanel.add(new JLabel("Quantity"));
        headerPanel.add(new JLabel("Line Cost"));
        
        // Display Panel
        JPanel displayCartPanel = new JPanel();
        displayCartPanel.setLayout(new BoxLayout(displayCartPanel, BoxLayout.Y_AXIS));
        displayCartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        displayCartPanel.add(headerPanel);
    
        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(displayCartPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cartFrame.add(scrollPane, BorderLayout.CENTER);
        
        // Database Connection
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();
        User currentUserRole = UserManager.getCurrentUser();
        String usersID = currentUserRole.getUserID();
    
        try {
            PreparedStatement pstmt = db.con.prepareStatement(
                "SELECT OrderLine.order_number, OrderLine.productCode, OrderLine.Quantity, OrderLine.Line_cost " +
                "FROM OrderLine INNER JOIN OrderDetails ON OrderLine.order_number = OrderDetails.order_number " +
                "WHERE OrderDetails.user_id = ? AND OrderDetails.order_status = 'pending'");
            pstmt.setString(1, usersID);
    
            ResultSet rs = pstmt.executeQuery();
            
            // Process ResultSet
            while (rs.next()) {
                String orderNumber = rs.getString("order_number");
                String productCode = rs.getString("productCode");
                int quantity = rs.getInt("Quantity");
                BigDecimal lineCost = rs.getBigDecimal("Line_cost");
            
                JPanel productPanel = new JPanel(new GridLayout(1, 5, 5, 5)); // 1 row, 5 columns, to accommodate the delete button
                productPanel.add(new JLabel(orderNumber));
                productPanel.add(new JLabel(productCode));
                productPanel.add(new JLabel(String.valueOf(quantity)));
                productPanel.add(new JLabel(lineCost.toPlainString()));
            
                // Create a delete button and add it to the row
                JButton deleteButton = new JButton("Delete");
                deleteButton.addActionListener(e -> {
                    // Delete the row from the database
                    deleteRowFromDatabase(orderNumber, productCode);
                    // Remove the product panel from the display
                    displayCartPanel.remove(productPanel);
                    displayCartPanel.revalidate();
                    displayCartPanel.repaint();
                });
            
                productPanel.add(deleteButton); // Add the delete button to the product panel
                displayCartPanel.add(productPanel);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            db.closeConnection();
        }
    
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            // Dispose the current window
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
            currentFrame.dispose();
        
            // Create a new JFrame to hold the CustomerUI panel
            JFrame customerFrame = new JFrame("Customer Page");
            customerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            customerFrame.setSize(1000, 700); // Set the size according to your needs
            customerFrame.setLocationRelativeTo(null); // Center on screen
        
            // Add an instance of CustomerUI to the JFrame
            CustomerUI customerUI = new CustomerUI();
            customerFrame.add(customerUI);
            customerFrame.setVisible(true); // Make the JFrame visible
        });

        JButton confirmButton = new JButton("Proceed to checkout");
        buttonsPanel.add(backButton);
        buttonsPanel.add(confirmButton);
        cartFrame.add(buttonsPanel, BorderLayout.SOUTH);

        // Display the window
        cartFrame.pack();
        cartFrame.setMinimumSize(new Dimension(600, 400)); // Set a minimum size for the window
        cartFrame.setLocationRelativeTo(null);
        cartFrame.setVisible(true);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform actions when Confirm & Pay button is clicked
                // Add logic to process payment details
                // open Database connection
                DatabaseConnectionHandler db = new DatabaseConnectionHandler();
                db.openConnection();

                User currentUser = UserManager.getCurrentUser();
                ArrayList<String> bankDetails = (ArrayList<String>) DatabaseOperations.getCard(currentUser, db.con);

                // if no bank details present with the current user
                if (bankDetails.isEmpty()) {
                    System.out.println("CURRENT ID: " + currentUser.getUserID());
                    System.out.println("No Bank Detail Exist");
            
                    // open payment window and wait for the user to enter details
                    SwingUtilities.invokeLater(() -> {
                        PaymentWindow newPaymentWindow = new PaymentWindow();
                    });
                }
                // Bank Details exist for current user
                else {
                    // Get Current User's cardNumber and hide first 12 digits
                    String cardNumber = bankDetails.get(0);
                    StringBuilder sb = new StringBuilder("");
                    int count = 0;
                    for (int i = 0; i < cardNumber.length(); i++) {
                        if (count < 12)
                            sb.append("*");
                        else
                            sb.append(cardNumber.charAt(i));
                        count++;
                    }
                    String hiddenCardNumber = sb.toString();

                    // Pop Up to confirm existing payment
                    int confirmationResult = JOptionPane.showConfirmDialog(null,
                            "Do you want to use this card for payment? \n" + hiddenCardNumber,
                            "Payment Confirmation", JOptionPane.YES_NO_OPTION);

                    if (confirmationResult == JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(null, "Order Placed Successfully");
                        // TODO: Add order to OrderLine and Orders tables in database
                        boolean orderConfirmedStatus = DatabaseOperations.placeOrder(currentUser, db.con);
                        // Show confirmation message
                        JOptionPane.showMessageDialog(
                                null,
                                "Order Status: " + (orderConfirmedStatus ? "Confirmed" : "Rejected"),
                                "Confirmation",
                                JOptionPane.INFORMATION_MESSAGE);
                        cartFrame.dispose(); // Closes the current window
                        System.out.println("Opening Customer View");
                        JFrame frame = new JFrame("Customer Dashboard");
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.setSize(800, 600); // Set the size of the frame
                        frame.setLocationRelativeTo(null);
    
                        // Add the CustomerUI instance to the frame
                        CustomerUI customerUI = new CustomerUI();
                        frame.add(customerUI);
    
                        // Make the frame visible
                        frame.setVisible(true);                    }
                    else if (confirmationResult == JOptionPane.NO_OPTION) {
                        JOptionPane.showMessageDialog(null, "Order Placed Unsuccessfully");
                        viewCart(); // Calls the view cart method
                        cartFrame.dispose(); // Closes the current window
                        System.out.println("Opening Customer View");
                        JFrame frame = new JFrame("Customer Dashboard");
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.setSize(800, 600); // Set the size of the frame
                        frame.setLocationRelativeTo(null);
    
                        // Add the CustomerUI instance to the frame
                        CustomerUI customerUI = new CustomerUI();
                        frame.add(customerUI);
    
                        // Make the frame visible
                        frame.setVisible(true);                    }
                }
                // Close the current window
                cartFrame.dispose();
                // Open a new instance of CustomerUI
            }
        });
    }

    private static void deleteRowFromDatabase(String orderNumber, String productCode) {
        // Open a connection to the database
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();
        
        try {
            // Prepare the SQL DELETE statement
            String sql = "DELETE FROM OrderLine WHERE order_number = ? AND productCode = ?";
            PreparedStatement pstmt = db.con.prepareStatement(sql);
            pstmt.setString(1, orderNumber);
            pstmt.setString(2, productCode);
            
            // Execute the delete statement
            pstmt.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            db.closeConnection();
        }
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
