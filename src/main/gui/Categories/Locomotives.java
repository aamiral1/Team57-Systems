package Categories;

import java.awt.*;
import javax.swing.*;
import main.db.DatabaseConnectionHandler;
import main.gui.StaffUI; // Make sure this class exists in your project
import java.sql.*;

public class Locomotives extends JPanel {

    private JFrame parentFrame;
    private JPanel boxesPanel;

    public Locomotives(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE); // Setting background color to white

        JButton returnButton = new JButton("Return");
        styleButton(returnButton, new Color(135, 206, 250)); // Light blue color
        returnButton.addActionListener(e -> {
            parentFrame.setContentPane(new StaffUI()); // Make sure StaffUI constructor accepts JFrame
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        northPanel.add(returnButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("LOCOMOTIVES PAGE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); // Setting font
        titleLabel.setForeground(Color.BLACK); // Text color
        northPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE); // Setting background color to white

        JButton addButton = new JButton("Add");
        styleButton(addButton, new Color(50, 205, 50)); // Green color
        rightPanel.add(addButton);

        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton, new Color(30, 144, 255)); // Dodger blue color
        refreshButton.addActionListener(e -> refreshLocomotives());
        rightPanel.add(refreshButton);

        northPanel.add(rightPanel, BorderLayout.EAST);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(northPanel, BorderLayout.NORTH);

        boxesPanel = new JPanel();
        boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.Y_AXIS));
        boxesPanel.setBackground(Color.WHITE); // Setting background color to white
        refreshLocomotives();

        JScrollPane scrollPane = new JScrollPane(boxesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshLocomotives() {
        boxesPanel.removeAll();
        java.util.List<String[]> locomotives = getLocomotives();
        for (String[] locomotive : locomotives) {
            JPanel boxPanel = createBox(locomotive);
            boxesPanel.add(boxPanel);
            boxesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        boxesPanel.revalidate();
        boxesPanel.repaint();
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
                        "Retail Price: $" + retailPrice,
                        "Product Quantity: " + productQuantity,
                        "Model Type: " + modelType,
                        "Gauge: " + gauge,
                        "Historical Era: " + historicalEra,
                        "DCC Code: " + dccCode
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return locomotives;
    }

    private JPanel createBox(String[] locomotiveData) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.setBackground(Color.WHITE);

        for (String data : locomotiveData) {
            JLabel label = new JLabel(data);
            label.setFont(new Font("SansSerif", Font.PLAIN, 12));
            label.setForeground(Color.BLACK);
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel.add(label);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE); // Setting background color to white

        JButton deleteButton = new JButton("Delete");
        styleButton(deleteButton, new Color(255, 99, 71)); // Tomato color
        String productCode = locomotiveData[0].split(": ")[1];
        deleteButton.addActionListener(e -> deleteLocomotive(productCode));
        buttonPanel.add(deleteButton);

        JButton editButton = new JButton("Edit");
        styleButton(editButton, new Color(144, 238, 144)); // Light green color
        buttonPanel.add(editButton);

        panel.add(buttonPanel);

        return panel;
    }


    private void deleteLocomotive(String productCode) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this locomotive?",
                "Delete Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DatabaseConnectionHandler db = new DatabaseConnectionHandler();
            db.openConnection();
            try {
                db.con.setAutoCommit(false);

                String deleteLocomotivesSQL = "DELETE FROM Locomotives WHERE productCode = ?";
                try (PreparedStatement pstmtLocomotives = db.con.prepareStatement(deleteLocomotivesSQL)) {
                    pstmtLocomotives.setString(1, productCode);
                    pstmtLocomotives.executeUpdate();
                }

                String deleteIndividualSQL = "DELETE FROM Individual WHERE productCode = ?";
                try (PreparedStatement pstmtIndividual = db.con.prepareStatement(deleteIndividualSQL)) {
                    pstmtIndividual.setString(1, productCode);
                    pstmtIndividual.executeUpdate();
                }

                String deleteProductSQL = "DELETE FROM Product WHERE productCode = ?";
                try (PreparedStatement pstmtProduct = db.con.prepareStatement(deleteProductSQL)) {
                    pstmtProduct.setString(1, productCode);
                    pstmtProduct.executeUpdate();
                }

                db.con.commit();
            } catch (SQLException e) {
                try {
                    db.con.rollback();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                try {
                    db.con.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                db.closeConnection();
            }

            refreshLocomotives();
        }
    }

     private void styleButton(JButton button, Color color) {
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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
