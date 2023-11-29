package gui;

import javax.swing.*;
import db.DatabaseConnectionHandler;
import store.UserManager;

import java.awt.BorderLayout;
//import java.awt.*;
import java.sql.*;

public class viewRecentOrdersUI extends JFrame {

    public void orderViewer() {

        // Page title
        setTitle("Recent Orders");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainMainPanel = new JPanel();
        mainMainPanel.setLayout(new BorderLayout());
        add(mainMainPanel);

        // Create a panel with BoxLayout along the Y-axis
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

        try (Statement statement = db.con.createStatement()) {

            String userIdToRetrieve = UserManager.getCurrentUser().getUserID(); // retrieve current user ID to access correct orders

            // Query to get order details for user
            String orderDetailsQuery = "SELECT " +
                    "User.user_id, " +
                    "OrderDetails.order_number, " +
                    "OrderDetails.order_status, " +
                    "OrderDetails.order_date " +
                    "FROM " +
                    "OrderDetails " +
                    "INNER JOIN User ON OrderDetails.user_id = User.user_id " +
                    "WHERE User.user_id = '" + userIdToRetrieve + "'";

            ResultSet orderDetailsResultSet = statement.executeQuery(orderDetailsQuery);

            while (orderDetailsResultSet.next()) {

                int orderNumber = orderDetailsResultSet.getInt("order_number");

                // Debugg print statement
                System.out.println("Processing Order Number: " + orderNumber);

                // Create a panel for each order with border
                JPanel orderPanel = new JPanel();
                orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
                orderPanel.setBorder(BorderFactory.createTitledBorder("Order"));

                // label to display order numbe 
                /*
                JLabel orderLabel = new JLabel("Order Number: " + totalCost);
                orderPanel.add(orderLabel); */

                // Execute the nested query to get order lines for the current order number
                String orderLinesQuery = "SELECT " +
                        "OrderLine.productCode, " +
                        "OrderLine.Quantity, " +
                        "OrderLine.Line_cost " +
                        "FROM " +
                        "OrderLine " +
                        "WHERE OrderLine.order_number = '" + orderNumber + "'";

                try (Statement getOrderLines = db.con.createStatement()) {

                    ResultSet orderLinesResultSet = getOrderLines.executeQuery(orderLinesQuery);

                    // Create a panel for order lines
                    JPanel orderLinesPanel = new JPanel();
                    orderLinesPanel.setLayout(new BoxLayout(orderLinesPanel, BoxLayout.Y_AXIS));
                    orderLinesPanel.setBorder(BorderFactory.createTitledBorder("Products"));

                    int orderTotal = 0;

                    while (orderLinesResultSet.next()) { // goes through each order line and gets relevant details to display

                        String productCode = orderLinesResultSet.getString("productCode");
                        int quantity = orderLinesResultSet.getInt("Quantity");
                        double lineCost = orderLinesResultSet.getDouble("Line_cost");

                        int totalCost = (int) (quantity * lineCost); // calculating total cost for line

                        // Create a label to display order line details
                        JLabel orderLineLabel = new JLabel("Product Code: " + productCode +
                                ", Quantity: " + quantity +
                                ", Product price: £" + lineCost);

                        orderLinesPanel.add(orderLineLabel);

                        orderTotal = orderTotal + totalCost;

                    }

                    // label to display order numbe
                    JLabel orderLabel = new JLabel("Order Number: " + orderNumber +  " Total: £" + orderTotal);
                    orderPanel.add(orderLabel);

                    orderLinesResultSet.close();

                    // Add the order lines panel to the main order panel
                    orderPanel.add(orderLinesPanel);
                }

                // Add the order panel to the main panel
                mainPanel.add(orderPanel);
            }

            // Close the ResultSet for order details
            orderDetailsResultSet.close();

        // Add the main panel to the frame
        mainMainPanel.add(mainPanel, BorderLayout.CENTER);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }

        // Set the size and make the frame visible
        setSize(600, 400);
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new viewRecentOrdersUI().orderViewer());
    }
}
