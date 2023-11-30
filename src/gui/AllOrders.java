package gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import db.DatabaseConnectionHandler;
import store.UserManager;

import java.awt.*;
import java.math.BigDecimal;
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
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Utility method to update the order status in the database
    private static void fulfillOrder(DatabaseConnectionHandler db, String orderNumber, JFrame frame) {

        db.openConnection();
        try {
            String updateSQL = "UPDATE OrderDetails SET order_status = 'fulfilled' WHERE order_number = ? AND order_status = 'confirmed'";
            PreparedStatement pstmtUpdate = db.con.prepareStatement(updateSQL);
            pstmtUpdate.setString(1, orderNumber);
            int rowsAffected = pstmtUpdate.executeUpdate();

            // Check if the update was successful
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Order " + orderNumber + " has been fulfilled.");
            } else {
                JOptionPane.showMessageDialog(frame, "Order " + orderNumber + " could not be updated. It may not be in the correct state.");
            }

            // Refresh the UI
            frame.dispose();
            createAndShowGUI();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }

    }

    
    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Order Details with Products");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

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
                BigDecimal lineCost = rs.getBigDecimal("Line_cost");
                String orderStatus = rs.getString("order_status");

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
                productPanel.add(new JLabel(String.valueOf(lineCost)));
                productPanel.add(new JLabel("$" + rs.getBigDecimal("retailPrice").toString()));

                orderPanel.add(productPanel);
                orderPanelsMap.put(orderNumber, orderPanel);
            }

            // Add footer panels with total price, status, and action buttons
            for (Map.Entry<String, JPanel> entry : orderPanelsMap.entrySet()) {
                String orderNumber = entry.getKey();
                JPanel orderPanel = entry.getValue();
                BigDecimal totalCost = orderTotalsMap.get(orderNumber);
                String orderStatus = orderStatusMap.get(orderNumber);

                JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                footerPanel.add(new JLabel("Total Price: $" + totalCost.toString()));
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