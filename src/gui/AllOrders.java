package gui;

import javax.swing.*;
import db.DatabaseConnectionHandler;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AllOrders extends JPanel {

    // Utility method to delete order from the database
    private static void deleteOrder(DatabaseConnectionHandler db, String orderNumber, JFrame frame) {
        try {
            // Start transaction
            db.openConnection();
            db.con.setAutoCommit(false);

            // Delete from OrderLine
            String deleteOrderLineSQL = "DELETE FROM OrderLine WHERE order_number = ?";
            PreparedStatement pstmtOrderLine = db.con.prepareStatement(deleteOrderLineSQL);
            pstmtOrderLine.setString(1, orderNumber);
            pstmtOrderLine.executeUpdate();

            // Delete from OrderDetails
            String deleteOrderDetailsSQL = "DELETE FROM OrderDetails WHERE order_number = ?";
            PreparedStatement pstmtOrderDetails = db.con.prepareStatement(deleteOrderDetailsSQL);
            pstmtOrderDetails.setString(1, orderNumber);
            pstmtOrderDetails.executeUpdate();

            // Commit transaction
            db.con.commit();

            // Refresh the UI
            frame.dispose();
            createAndShowGUI();
        } catch (SQLException e) {
            try {
                db.con.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                db.con.setAutoCommit(true);
                db.closeConnection();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Utility method to update the order status in the database and decrease the product quantity
    private static void fulfillOrder(DatabaseConnectionHandler db, String orderNumber, JFrame frame) {
        db.openConnection();
        try {
            // Start transaction
            db.con.setAutoCommit(false);

            // Select productCode and Quantity from OrderLine where Order_number is the one provided
            String selectOrderLineSQL = "SELECT productCode, Quantity FROM OrderLine WHERE Order_number = ?";
            PreparedStatement pstmtSelectOrderLine = db.con.prepareStatement(selectOrderLineSQL);
            pstmtSelectOrderLine.setString(1, orderNumber);
            ResultSet rsOrderLine = pstmtSelectOrderLine.executeQuery();

            while (rsOrderLine.next()) {
                String productCode = rsOrderLine.getString("productCode");
                int quantityOrdered = rsOrderLine.getInt("Quantity");

                // Check current stock level for the product
                String checkStockSQL = "SELECT productQuantity FROM Product WHERE productCode = ?";
                PreparedStatement pstmtCheckStock = db.con.prepareStatement(checkStockSQL);
                pstmtCheckStock.setString(1, productCode);
                ResultSet rsCheckStock = pstmtCheckStock.executeQuery();

                if (rsCheckStock.next()) {
                    int currentStock = rsCheckStock.getInt("productQuantity");
                    if (currentStock < quantityOrdered) {
                        JOptionPane.showMessageDialog(frame, "Order could not be fulfilled. Please update stock levels for product code: " + productCode);
                        db.con.rollback();
                        return; // Exit the method early as we cannot fulfill this order
                    }
                }

                // Update the Product table to decrease the productQuantity by the quantity ordered
                String updateProductSQL = "UPDATE Product SET productQuantity = productQuantity - ? WHERE productCode = ?";
                PreparedStatement pstmtUpdateProduct = db.con.prepareStatement(updateProductSQL);
                pstmtUpdateProduct.setInt(1, quantityOrdered);
                pstmtUpdateProduct.setString(2, productCode);
                pstmtUpdateProduct.executeUpdate();
            }

            // Update OrderDetails to set the order status to 'fulfilled'
            String updateOrderDetailsSQL = "UPDATE OrderDetails SET order_status = 'fulfilled' WHERE order_number = ? AND order_status = 'confirmed'";
            PreparedStatement pstmtUpdateOrderDetails = db.con.prepareStatement(updateOrderDetailsSQL);
            pstmtUpdateOrderDetails.setString(1, orderNumber);
            int rowsAffectedOrderDetails = pstmtUpdateOrderDetails.executeUpdate();

            // Check if the order status update was successful
            if (rowsAffectedOrderDetails > 0) {
                JOptionPane.showMessageDialog(frame, "Order " + orderNumber + " has been fulfilled.");
                // Commit transaction
                db.con.commit();
            } else {
                JOptionPane.showMessageDialog(frame, "Order " + orderNumber + " could not be updated. It may not be in the correct state.");
                db.con.rollback();
            }

            // Refresh the UI
            frame.dispose();
            createAndShowGUI();
        } catch (SQLException e) {
            try {
                db.con.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                db.con.setAutoCommit(true);
                db.closeConnection();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Order Details with Products");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    });

        // Create a panel to hold the return button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Left aligns the button
        topPanel.add(returnButton);

        // Add the top panel to the frame
        frame.add(topPanel, BorderLayout.NORTH);

        // Maps to hold panels and order details
        Map<String, JPanel> orderPanelsMap = new HashMap<>();
        Map<String, BigDecimal> orderTotalsMap = new HashMap<>();
        Map<String, String> orderStatusMap = new HashMap<>();

        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

        try {
            // Query to get order details including status
            String query = "SELECT OrderLine.order_number, OrderLine.productCode, OrderLine.Quantity, " +
                    "OrderLine.Line_cost, Product.brandName, Product.productName, Product.retailPrice, " +
                    "OrderDetails.order_status FROM OrderLine JOIN Product ON OrderLine.productCode = Product.productCode " +
                    "JOIN OrderDetails ON OrderLine.order_number = OrderDetails.order_number ORDER BY OrderLine.order_number";

            PreparedStatement pstmt = db.con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String orderNumber = rs.getString("order_number");
                int quantity = rs.getInt("Quantity");
                BigDecimal retailPrice = rs.getBigDecimal("retailPrice");
                BigDecimal lineCost = retailPrice.multiply(new BigDecimal(quantity)); // Calculate line cost
                String orderStatus = rs.getString("order_status");
            
                // Accumulate the line costs for each order number
                BigDecimal currentTotal = orderTotalsMap.getOrDefault(orderNumber, BigDecimal.ZERO);
                currentTotal = currentTotal.add(lineCost);
                orderTotalsMap.put(orderNumber, currentTotal);
                orderStatusMap.put(orderNumber, orderStatus);

                JPanel orderPanel = orderPanelsMap.computeIfAbsent(orderNumber, k -> new JPanel());
                orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
                orderPanel.setBorder(BorderFactory.createTitledBorder("Order Number: " + orderNumber));

                JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                productPanel.add(new JLabel(rs.getString("productCode")));
                productPanel.add(new JLabel(rs.getString("brandName")));
                productPanel.add(new JLabel(rs.getString("productName")));
                productPanel.add(new JLabel(String.valueOf(rs.getInt("Quantity"))));
                productPanel.add(new JLabel("$" + rs.getBigDecimal("retailPrice").toString()));

                orderPanel.add(productPanel);
                orderPanelsMap.put(orderNumber, orderPanel);
            }

            // Add footer panels with total price and status
            for (Map.Entry<String, JPanel> entry : orderPanelsMap.entrySet()) {
                String orderNumber = entry.getKey();
                JPanel orderPanel = entry.getValue();
                BigDecimal totalCost = orderTotalsMap.get(orderNumber).setScale(2, RoundingMode.HALF_UP); // Ensure total cost is to 2 decimal places
                String orderStatus = orderStatusMap.get(orderNumber);

                JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                // Display the total price for the order
                JLabel totalPriceLabel = new JLabel("Total Price: $" + totalCost.toPlainString());
                footerPanel.add(totalPriceLabel);
                footerPanel.add(new JLabel(" | Order Status: " + orderStatus));

                if ("confirmed".equalsIgnoreCase(orderStatus)) {
                    JButton fulfillButton = new JButton("Fulfill");
                    fulfillButton.addActionListener(e -> fulfillOrder(db, orderNumber, frame));
                    footerPanel.add(fulfillButton);
                }

                JButton cancelButton = new JButton("Cancel Order");
                cancelButton.addActionListener(e -> deleteOrder(db, orderNumber, frame));
                footerPanel.add(cancelButton);

                orderPanel.add(footerPanel);
            }

            // Main panel to hold all order panels
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            for (JPanel panel : orderPanelsMap.values()) {
                mainPanel.add(panel);
            }

            JScrollPane scrollPane = new JScrollPane(mainPanel);
            frame.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }

        frame.setPreferredSize(new Dimension(800, 600)); // Set the preferred size
        frame.pack(); // Adjusts to accommodate the components
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AllOrders::createAndShowGUI);
    }
}