package gui;

import db.DatabaseConnectionHandler;
import db.DatabaseOperations;
import store.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;

public class displayInduvidualProductsUI {
    // Array list to store items added to cart ---> Needs to be saved not just from
    // the page that it is one - should be global
    public static ArrayList<String[]> cart = new ArrayList<>();

    public static String currentProductType = ""; // global variable to store page information for back button

    // -----------------------------------------------------------------------------------------------------------------------

    // Method to create and show the GUI
    public static void createAndShowGUI(String[][] productDetails) {

        JFrame frame = new JFrame("Product Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel("Product Details Page", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        northPanel.add(headerLabel, BorderLayout.NORTH);

        // View Cart Button
        JButton viewCartButton = new JButton("View Cart");

        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCart(); // Calls the view cart method
                frame.dispose(); // Closes the current window
            }
        });

        // Main Panel for product details
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        for (String[] details : productDetails) { // Iterates through the product details array
            JPanel productPanel = createProductPanel(details); // Creates panel containing details for each product
            mainPanel.add(productPanel); // Adds product panels to the main panel
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Adding the view cart button to the top right
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(viewCartButton);
        northPanel.add(topPanel, BorderLayout.CENTER);

        JPanel westPanel = new JPanel();
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerUI instance = new CustomerUI();

                JFrame window = new JFrame("Categories Page");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setSize(1000, 700); // Adjust the size to accommodate title and labels
                window.add(instance);
                window.setLocationRelativeTo(null);
                window.setVisible(true);
                frame.dispose(); // Closes the current window

            }
        });

        westPanel.add(homeButton);
        northPanel.add(westPanel, BorderLayout.WEST);

        frame.add(northPanel, BorderLayout.NORTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // -----------------------------------------------------------------------------------------------------------------------

    // Method to view items added to cart
    private static void viewCart() {

        JFrame cartFrame = new JFrame("View Cart");
        cartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cartFrame.setLayout(new BorderLayout());

        JPanel displayCartPanel = new JPanel();
        displayCartPanel.setLayout(new BoxLayout(displayCartPanel, BoxLayout.Y_AXIS));

        for (String[] product : cart) {
            JPanel productPanel = createProductCart(product);
            displayCartPanel.add(productPanel);
        }

        JScrollPane scrollPane = new JScrollPane(displayCartPanel);
        cartFrame.add(scrollPane, BorderLayout.CENTER);

        JLabel pageTitle = new JLabel("Your cart");
        cartFrame.add(pageTitle, BorderLayout.NORTH);

        JButton backButton = new JButton("Back"); // Action Listener for back button
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBackToProductDetailsPage(cartFrame);
            }
        });

        JButton confirmButton = new JButton("Proceed to checkout");
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

                    // open payment window
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
                    }
                }
                // Close the current window
                cartFrame.dispose();
                // Open a new instance of PaymentWindow

            }
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BorderLayout());

        buttonsPanel.add(backButton, BorderLayout.NORTH);
        buttonsPanel.add(confirmButton, BorderLayout.CENTER);

        cartFrame.add(buttonsPanel, BorderLayout.SOUTH);

        cartFrame.pack();
        cartFrame.setLocationRelativeTo(null);
        cartFrame.setVisible(true);

    }

    // -----------------------------------------------------------------------------------------------------------------------

    // Method to go back to previous category page
    private static void goBackToProductDetailsPage(JFrame cartFrame) {
        // Dispose the current cart frame
        cartFrame.dispose();

        String[][] productDetails = getProducts(currentProductType);
        createAndShowGUI(productDetails);
    }

    // -----------------------------------------------------------------------------------------------------------------------

    // Method that creates cart

    private static JPanel createProductCart(String[] productDetails) {

        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.X_AXIS));

        for (String detail : productDetails) {
            JLabel label = new JLabel(detail);
            label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            productPanel.add(label);
        }

        productPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return productPanel;
    }

    // -----------------------------------------------------------------------------------------------------------------------

    // Method that creates a product panel depending on which product category is
    // selected

    private static JPanel createProductPanel(String[] details) {

        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.X_AXIS));

        for (String detail : details) {
            JLabel label = new JLabel(detail);
            label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            productPanel.add(label);
        }

        // Quantity Selector

        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        Dimension spinnerSize = new Dimension(60, 25); // Sets the desired size for the spinner
        quantitySpinner.setMaximumSize(spinnerSize); // Sets the maximum size
        quantitySpinner.setPreferredSize(spinnerSize); // Sets the preferred size
        quantitySpinner.setMinimumSize(spinnerSize); // Sets the minimum size
        productPanel.add(quantitySpinner);

        // Creating add to Cart Button

        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Gets the details of the product row the button is on using cutom method and
                // adds to the cart data structure
                String[] productDetails = getProductDetailsFromPanel((JPanel) addToCartButton.getParent());
                int quantity = (int) quantitySpinner.getValue();

                // Adds the product details and quantity to the cart
                addToCart(productDetails, quantity);
            }
        });

        productPanel.add(addToCartButton);

        // This will keep the spinner and button to the right
        productPanel.add(Box.createHorizontalGlue());

        productPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return productPanel;
    }

    // -----------------------------------------------------------------------------------------------------------------------

    // Method to add products to cart

    private static void addToCart(String[] productDetails, int quantity) {

        // Create a copy of the product details array with the quantity
        String[] productWithQuantity = new String[productDetails.length + 1];
        System.arraycopy(productDetails, 0, productWithQuantity, 0, productDetails.length);
        productWithQuantity[productDetails.length] = String.valueOf(quantity);

        // Adds the product to the cart
        cart.add(productWithQuantity);

        // Displays a message to the user confirming the addition to the cart
        JOptionPane.showMessageDialog(null, "Product added to cart!");
    }

    // -----------------------------------------------------------------------------------------------------------------------

    // Method to get product details from current panel

    private static String[] getProductDetailsFromPanel(JPanel productPanel) {

        int componentCount = productPanel.getComponentCount();
        String[] details = new String[componentCount - 2]; // Exclude quantity spinner and button

        for (int i = 0; i < componentCount - 2; i++) {
            Component component = productPanel.getComponent(i);
            if (component instanceof JLabel) {
                details[i] = ((JLabel) component).getText();
            } else {
                // Handles the case when the component is not a JLabel (e.g., JSpinner)
                details[i] = "N/A";
            }
        }

        return details;
    }

    // -----------------------------------------------------------------------------------------------------------------------

    public static String[][] getProducts(String productType) {

        // Open a connection to the database
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

        String[][] productDetails = null;

        if (productType.equals("Locomotives")) {

            currentProductType = "Locomotives";

            try {

                // Create a statement
                Statement statement = db.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);

                // Execute the query
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
                        "FROM " +
                        "Locomotives " +
                        "INNER JOIN Product ON Locomotives.productCode = Product.productCode " +
                        "INNER JOIN Individual ON Locomotives.productCode = Individual.productCode;";

                ResultSet resultSet = statement.executeQuery(sqlQuery);

                int rowCount = 0;

                while (resultSet.next()) {

                    rowCount++;
                }

                productDetails = new String[rowCount][7];

                resultSet.beforeFirst();

                int rowNum = 0;

                // Process the ResultSet and populate array
                while (resultSet.next()) {
                    productDetails[rowNum][0] = resultSet.getString("modelType"); // This needs to be done for all of
                                                                                  // the information we want to store to
                                                                                  // display to customers
                    productDetails[rowNum][1] = resultSet.getString("productName");
                    productDetails[rowNum][2] = resultSet.getString("brandName");
                    productDetails[rowNum][3] = resultSet.getString("DCCCode");
                    productDetails[rowNum][4] = resultSet.getString("gauge");
                    productDetails[rowNum][5] = resultSet.getString("historicalEra");
                    productDetails[rowNum][6] = resultSet.getString("retailPrice");
                    rowNum++;
                }

            } catch (SQLException e) {
                // Handle the exception appropriately in your application
                e.printStackTrace();
            } finally {
                // Close resources
                db.closeConnection();
            }

            return productDetails;

        } else if (productType.equals("Controllers")) {

            currentProductType = "Controllers";

            try {

                // Create a statement
                Statement statement = db.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);

                // Execute the query
                String sqlQuery = "SELECT " +
                        "Product.productCode, " +
                        "Product.brandName, " +
                        "Product.productName, " +
                        "Product.retailPrice, " +
                        "Product.productQuantity, " +
                        "Individual.modelType, " +
                        "Individual.gauge, " +
                        "Controller.isDigital " +
                        "FROM " +
                        "Controller " +
                        "INNER JOIN Product ON Controller.productCode = Product.productCode " +
                        "INNER JOIN Individual ON Controller.productCode = Individual.productCode;";

                ResultSet resultSet = statement.executeQuery(sqlQuery);

                int rowCount = 0;

                while (resultSet.next()) {

                    rowCount++;
                }

                productDetails = new String[rowCount][7];

                resultSet.beforeFirst();

                int rowNum = 0;

                // Process the ResultSet and populate array
                while (resultSet.next()) {
                    productDetails[rowNum][0] = resultSet.getString("modelType");
                    productDetails[rowNum][1] = resultSet.getString("productName");
                    productDetails[rowNum][2] = resultSet.getString("brandName");
                    productDetails[rowNum][3] = resultSet.getString("isDigital");
                    productDetails[rowNum][4] = resultSet.getString("gauge");
                    productDetails[rowNum][5] = resultSet.getString("retailPrice");
                    rowNum++;
                }

            } catch (SQLException e) {
                // Handle the exception appropriately in your application
                e.printStackTrace();
            } finally {
                // Close resources
                db.closeConnection();
            }

            return productDetails;

        } else if (productType.equals("Track")) {

            currentProductType = "Track";

            try {

                // Create a statement
                Statement statement = db.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);

                // Execute the query
                String sqlQuery = "SELECT " +
                        "Product.productCode, " +
                        "Product.brandName, " +
                        "Product.productName, " +
                        "Product.retailPrice, " +
                        "Product.productQuantity, " +
                        "Individual.modelType, " +
                        "Individual.gauge " +
                        "FROM " +
                        "Track " +
                        "INNER JOIN Product ON Track.productCode = Product.productCode " +
                        "INNER JOIN Individual ON Track.productCode = Individual.productCode;";

                ResultSet resultSet = statement.executeQuery(sqlQuery);

                int rowCount = 0;

                while (resultSet.next()) {

                    rowCount++;
                }

                productDetails = new String[rowCount][7];

                resultSet.beforeFirst();

                int rowNum = 0;

                // Process the ResultSet and populate array
                while (resultSet.next()) {
                    productDetails[rowNum][0] = resultSet.getString("modelType");
                    productDetails[rowNum][1] = resultSet.getString("productName");
                    productDetails[rowNum][2] = resultSet.getString("brandName");
                    productDetails[rowNum][3] = resultSet.getString("gauge");
                    productDetails[rowNum][4] = resultSet.getString("retailPrice");
                    rowNum++;
                }

            } catch (SQLException e) {
                // Handle the exception appropriately in your application
                e.printStackTrace();
            } finally {
                // Close resources
                db.closeConnection();
            }

            return productDetails;

        } else if (productType.equals("Rolling Stock")) {

            currentProductType = "Rolling Stock";

            try {

                // Create a statement
                Statement statement = db.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);

                // Execute the query
                String sqlQuery = "SELECT " +
                        "Product.productCode, " +
                        "Product.brandName, " +
                        "Product.productName, " +
                        "Product.retailPrice, " +
                        "Product.productQuantity, " +
                        "Individual.modelType, " +
                        "Individual.gauge, " +
                        "RollingStock.historicalEra, " +
                        "RollingStock.markType, " +
                        "RollingStock.wagonType, " +
                        "RollingStock.carriageType " +
                        "FROM " +
                        "RollingStock " +
                        "INNER JOIN Product ON RollingStock.productCode = Product.productCode " +
                        "INNER JOIN Individual ON RollingStock.productCode = Individual.productCode;";

                ResultSet resultSet = statement.executeQuery(sqlQuery);

                int rowCount = 0;

                while (resultSet.next()) {

                    rowCount++;
                }

                productDetails = new String[rowCount][9];

                resultSet.beforeFirst();

                int rowNum = 0;

                // Process the ResultSet and populate array
                while (resultSet.next()) {
                    productDetails[rowNum][0] = resultSet.getString("modelType");
                    productDetails[rowNum][1] = resultSet.getString("productName");
                    productDetails[rowNum][2] = resultSet.getString("brandName");
                    productDetails[rowNum][3] = resultSet.getString("carriageType");
                    productDetails[rowNum][4] = resultSet.getString("markeType");
                    productDetails[rowNum][6] = resultSet.getString("gauge");
                    productDetails[rowNum][7] = resultSet.getString("historicalEra");
                    productDetails[rowNum][8] = resultSet.getString("retailPrice");
                    rowNum++;
                }

            } catch (SQLException e) {
                // Handle the exception appropriately in your application
                e.printStackTrace();
            } finally {
                // Close resources
                db.closeConnection();
            }

            return productDetails;

        } else {

            // Return and empty array
            productDetails = new String[0][0];
            return productDetails;
        }
    }
}