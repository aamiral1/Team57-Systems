package main.gui.Categories;

import java.awt.*;
import javax.swing.*;
import main.db.DatabaseConnectionHandler;
import main.gui.StaffUI; // Make sure to import your StaffUI class
import java.sql.*;

public class Locomotives extends JPanel {

    public Locomotives(JFrame parentFrame) {
        setLayout(new BorderLayout());

        // Panel for the title, return button, and add button
        JPanel northPanel = new JPanel(new BorderLayout());

        // Return button
        JButton returnButton = new JButton("Return");
        styleButton(returnButton, new Color(100, 100, 255)); // Apply styling with a custom color
        returnButton.addActionListener(e -> {
            // Switch back to the Categories page
            parentFrame.setContentPane(new StaffUI());
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        northPanel.add(returnButton, BorderLayout.WEST);

        // Title label
        JLabel titleLabel = new JLabel("LOCOMOTIVES PAGE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        northPanel.add(titleLabel, BorderLayout.CENTER);

        // Right panel for the 'Add' button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add");
        styleButton(addButton, new Color(100, 255, 100)); // Apply styling with a custom color
        // Placeholder for adding action listener to the 'Add' button
        // addButton.addActionListener(...);
        rightPanel.add(addButton);

        northPanel.add(rightPanel, BorderLayout.EAST);

        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add the north panel to the top of the main panel
        add(northPanel, BorderLayout.NORTH);

        // Scrollable panel for boxes
        JPanel boxesPanel = new JPanel();
        boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(boxesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Fetch the locomotives data and create boxes for them
        java.util.List<String[]> locomotives = getLocomotives();
        for (String[] locomotive : locomotives) {
            JPanel boxPanel = createBox(locomotive);
            boxesPanel.add(boxPanel);
            boxesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Add boxes panel to the center, wrapped in a scroll pane
        add(scrollPane, BorderLayout.CENTER);
    }

    private java.util.List<String[]> getLocomotives() {
        java.util.List<String[]> locomotives = new java.util.ArrayList<>();
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

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

        try (PreparedStatement pstmt = db.con.prepareStatement(sqlQuery);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String productCode = rs.getString("productCode");
                String brandName = rs.getString("brandName");
                String productName = rs.getString("productName");
                float retailPrice = rs.getFloat("retailPrice");
                int productQuantity = rs.getInt("productQuantity");
                String modelType = rs.getString("modelType");
                String gauge = rs.getString("gauge");
                String historicalEra = rs.getString("historicalEra");
                String dccCode = rs.getString("DCCCode");

                locomotives.add(new String[]{
                        "Product Code: " + productCode,
                        "Brand Name: " + brandName,
                        "Product Name: " + productName,
                        "Retail Price: " + retailPrice,
                        "Product Quantity: " + productQuantity,
                        "Model Type: " + modelType,
                        "Gauge: " + gauge,
                        "Historical Era: " + historicalEra,
                        "DCC Code: " + dccCode
                });
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while querying the database:");
            e.printStackTrace();
        } finally {
            db.closeConnection(); // Make sure to close the connection properly
        }

        return locomotives;
    }

    private JPanel createBox(String[] locomotiveData) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        panel.setBackground(Color.WHITE);

        for (String data : locomotiveData) {
            JLabel label = new JLabel(data);
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            panel.add(label);
        }

        // Buttons panel at the bottom of the box
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton deleteButton = new JButton("Delete");
        JButton editButton = new JButton("Edit");
        styleButton(deleteButton, new Color(255, 100, 100)); // Apply delete button styling
        styleButton(editButton, new Color(100, 255, 100)); // Apply edit button styling
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(buttonPanel);

        return panel;
    }

    // Method to style buttons
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        // Mouse listener for hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }
}