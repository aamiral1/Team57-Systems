package Categories;

import java.awt.*;
import javax.swing.*;
import main.db.DatabaseConnectionHandler;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Track extends JPanel {

    private JFrame parentFrame;
    private JPanel boxesPanel;

    public Track(JFrame parentFrame) {
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

        JLabel titleLabel = new JLabel("TRACK PAGE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); // Setting font
        titleLabel.setForeground(Color.BLACK); // Text color
        northPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE); // Setting background color to white

        JButton addButton = new JButton("Add");
        styleButton(addButton, new Color(50, 205, 50)); // Green color
        addButton.addActionListener(e -> openAddDialog());
        rightPanel.add(addButton);

        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton, new Color(30, 144, 255)); // Dodger blue color
        refreshButton.addActionListener(e -> refreshTrack());
        rightPanel.add(refreshButton);

        northPanel.add(rightPanel, BorderLayout.EAST);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(northPanel, BorderLayout.NORTH);

        boxesPanel = new JPanel();
        boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.Y_AXIS));
        boxesPanel.setBackground(Color.WHITE); // Setting background color to white
        refreshTrack();

        JScrollPane scrollPane = new JScrollPane(boxesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshTrack() {
        boxesPanel.removeAll();
        java.util.List<String[]> TrackPacks = getTrack();
        for (String[] Track : TrackPacks) {
            JPanel boxPanel = createBox(Track);
            boxesPanel.add(boxPanel);
            boxesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        boxesPanel.revalidate();
        boxesPanel.repaint();
    }

    // Call this method when the Add button is clicked
    private void openAddDialog() {
        JDialog addDialog = new JDialog(parentFrame, "Add New Track", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(400, 300);
        addDialog.setLocationRelativeTo(parentFrame);

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        Map<String, JTextField> textFieldMap = new HashMap<>();

        // Define the fields for the new Track details
        String[] fields = {
            "Product Code", "Brand Name", "Product Name", "Retail Price",
            "Product Quantity", "Model Type", "Gauge"
        };

        // Create labels and text fields for each field
        for (String field : fields) {
            JLabel label = new JLabel(field);
            JTextField textField = new JTextField(20);
            fieldsPanel.add(label);
            fieldsPanel.add(textField);
            textFieldMap.put(field, textField);
        }

        JButton saveButton = new JButton("Save New Track");
        saveButton.addActionListener(e -> {
            saveTrack(textFieldMap);
            addDialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> addDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        addDialog.add(fieldsPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setVisible(true);
    }


    private boolean insertTrack(DatabaseConnectionHandler db, Map<String, JTextField> textFieldMap) throws SQLException {
        boolean success = false; // default to false, will be set to true if inserts succeed
        db.con.setAutoCommit(false); // Begin transaction
    
        try {
            // Insert into Product table
            String insertProductSQL = "INSERT INTO Product (productCode, brandName, productName, retailPrice, productQuantity) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmtProduct = db.con.prepareStatement(insertProductSQL)) {
                pstmtProduct.setString(1, textFieldMap.get("Product Code").getText());
                pstmtProduct.setString(2, textFieldMap.get("Brand Name").getText());
                pstmtProduct.setString(3, textFieldMap.get("Product Name").getText());
                
                // Check and parse retail price
                String retailPriceText = textFieldMap.get("Retail Price").getText().replaceAll("[^\\d.]", ""); // Remove non-numeric characters
                if (retailPriceText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Retail Price cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false; // Return early or throw an exception
                }
                try {
                    pstmtProduct.setFloat(4, Float.parseFloat(retailPriceText));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid number format for Retail Price.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false; // Return early or throw an exception
                }
    
                // Check and parse product quantity
                String productQuantityText = textFieldMap.get("Product Quantity").getText();
                if (productQuantityText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Product Quantity cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                try {
                    pstmtProduct.setInt(5, Integer.parseInt(productQuantityText));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid number format for Product Quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
    
                pstmtProduct.executeUpdate();
            }
    
            // Insert into Individual table
            String insertIndividualSQL = "INSERT INTO Individual (productCode, modelType, gauge) VALUES (?, ?, ?)";
            try (PreparedStatement pstmtIndividual = db.con.prepareStatement(insertIndividualSQL)) {
                pstmtIndividual.setString(1, textFieldMap.get("Product Code").getText());
                pstmtIndividual.setString(2, textFieldMap.get("Model Type").getText());
                pstmtIndividual.setString(3, textFieldMap.get("Gauge").getText());
                pstmtIndividual.executeUpdate();
            }
    
            db.con.commit(); // Commit transaction
            success = true; // if we reached this point, everything went well
    
        } catch (SQLException e) {
            db.con.rollback(); // Roll back transaction if anything goes wrong
            throw e; 
        } finally {
            db.con.setAutoCommit(true); // Restore default behavior
        }
    
        return success; // return the status of the insert operation
    }
    
    private void saveTrack(Map<String, JTextField> textFieldMap) {
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        boolean isInserted = false;
    
        try {
            db.openConnection();
            isInserted = insertTrack(db, textFieldMap);
    
            if (isInserted) {
                JOptionPane.showMessageDialog(this, "New Track added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Insert failed, no changes were made.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Insert error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (db.con != null && !db.con.isClosed()) {
                    db.closeConnection();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error closing the database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    
        if (isInserted) {
            refreshTrack();
        }
    }

    private java.util.List<String[]> getTrack() {
        java.util.List<String[]> Tracks = new java.util.ArrayList<>();
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();
        String sqlQuery = "SELECT " +
                "Product.productCode, " +
                "Product.brandName, " +
                "Product.productName, " +
                "Product.retailPrice, " +
                "Product.productQuantity, " +
                "Individual.modelType, " +
                "Individual.gauge " +
                "FROM Product " +
                "INNER JOIN Individual ON Product.productCode = Individual.productCode " +
                "WHERE Individual.modelType = 'Track';";
    
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
    
                Tracks.add(new String[]{
                        "Product Code: " + productCode,
                        "Brand Name: " + brandName,
                        "Product Name: " + productName,
                        "Retail Price: $" + retailPrice,
                        "Product Quantity: " + productQuantity,
                        "Model Type: " + modelType,
                        "Gauge: " + gauge
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return Tracks;
    }

    private JPanel createBox(String[] TrackData) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.setBackground(Color.WHITE);

        for (String data : TrackData) {
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
        String productCode = TrackData[0].split(": ")[1];
        deleteButton.addActionListener(e -> deleteTrack(productCode));
        buttonPanel.add(deleteButton);

        JButton editButton = new JButton("Edit");
        styleButton(editButton, new Color(144, 238, 144)); // Light green color
        editButton.addActionListener(e -> openEditDialog(TrackData));
        buttonPanel.add(editButton);

        panel.add(buttonPanel);

        return panel;
    }


    private void deleteTrack(String productCode) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this Track?",
                "Delete Confirmation",
                JOptionPane.YES_NO_OPTION);
    
        if (confirm == JOptionPane.YES_OPTION) {
            DatabaseConnectionHandler db = new DatabaseConnectionHandler();
            db.openConnection();
            try {
                db.con.setAutoCommit(false); // Start transaction
    
                // Delete from Individual table
                String deleteIndividualSQL = "DELETE FROM Individual WHERE productCode = ?";
                try (PreparedStatement pstmtIndividual = db.con.prepareStatement(deleteIndividualSQL)) {
                    pstmtIndividual.setString(1, productCode);
                    pstmtIndividual.executeUpdate();
                }
    
                // Delete from Product table
                String deleteProductSQL = "DELETE FROM Product WHERE productCode = ?";
                try (PreparedStatement pstmtProduct = db.con.prepareStatement(deleteProductSQL)) {
                    pstmtProduct.setString(1, productCode);
                    pstmtProduct.executeUpdate();
                }
    
                db.con.commit(); // Commit transaction
    
            } catch (SQLException e) {
                try {
                    db.con.rollback(); // If there's an error, roll back the transaction
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Delete error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    db.con.setAutoCommit(true); // Restore default auto-commit behavior
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                db.closeConnection(); // Close the database connection
            }
    
            refreshTrack(); // Refresh the display to show that the Track has been deleted
        }
    }

    private boolean updateTrack(DatabaseConnectionHandler db, Map<String, JTextField> textFieldMap, String productCode) throws SQLException {
        boolean success = false; // default to false, will be set to true if updates succeed
        db.con.setAutoCommit(false); // Begin transaction
    
        try {
            // Update Product table
            String updateProductSQL = "UPDATE Product SET brandName = ?, productName = ?, retailPrice = ?, productQuantity = ? WHERE productCode = ?";
            try (PreparedStatement pstmtProduct = db.con.prepareStatement(updateProductSQL)) {
                pstmtProduct.setString(1, textFieldMap.get("Brand Name").getText().trim());
                pstmtProduct.setString(2, textFieldMap.get("Product Name").getText().trim());
    
                // Check and parse retail price
                String retailPriceText = textFieldMap.get("Retail Price").getText().trim().replaceAll("[^\\d.]", ""); // Remove non-numeric characters
                if (retailPriceText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Retail Price cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false; // Return early or throw an exception
                }
                try {
                    pstmtProduct.setFloat(3, Float.parseFloat(retailPriceText));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid number format for Retail Price.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false; // Return early or throw an exception
                }
    
                // Check and parse product quantity
                String productQuantityText = textFieldMap.get("Product Quantity").getText().trim();
                if (productQuantityText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Product Quantity cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                try {
                    pstmtProduct.setInt(4, Integer.parseInt(productQuantityText));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid number format for Product Quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
    
                pstmtProduct.setString(5, productCode);
                pstmtProduct.executeUpdate();
            }
    
            // Update Individual table
            String updateIndividualSQL = "UPDATE Individual SET modelType = ?, gauge = ? WHERE productCode = ?";
            try (PreparedStatement pstmtIndividual = db.con.prepareStatement(updateIndividualSQL)) {
                pstmtIndividual.setString(1, textFieldMap.get("Model Type").getText().trim());
                pstmtIndividual.setString(2, textFieldMap.get("Gauge").getText().trim());
                pstmtIndividual.setString(3, productCode);
                pstmtIndividual.executeUpdate();
            }
    
            db.con.commit(); // Commit transaction
            success = true; // if we reached this point, everything went well
    
        } catch (SQLException e) {
            db.con.rollback(); // Roll back transaction if anything goes wrong
            JOptionPane.showMessageDialog(null, "An error occurred during the update: " + e.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
            return false; // Return early or throw an exception
        } finally {
            db.con.setAutoCommit(true); // Restore default behavior
        }
    
        return success; // return the status of the update operation
    }


    private void saveTrackChanges(Map<String, JTextField> textFieldMap, String productCode) {
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        boolean isUpdated = false;
    
        try {
            db.openConnection();
            isUpdated = updateTrack(db, textFieldMap, productCode);
    
            if (isUpdated) {
                JOptionPane.showMessageDialog(this, "Track updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Update failed, no changes were made.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (db.con != null && !db.con.isClosed()) {
                    db.closeConnection();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error closing the database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    
        if (isUpdated) {
            refreshTrack();
        }
    }

    private void openEditDialog(String[] trackData) {
        JDialog editDialog = new JDialog(parentFrame, "Edit Track", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(400, 300);
        editDialog.setLocationRelativeTo(parentFrame);
    
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        Map<String, JTextField> textFieldMap = new HashMap<>();
    
        // Create text fields pre-filled with Track data
        for (String data : trackData) {
            String[] splitData = data.split(":\\s+");
            if (splitData.length == 2) {
                JLabel label = new JLabel(splitData[0].trim());
                JTextField textField = new JTextField(splitData[1]);
                fieldsPanel.add(label);
                fieldsPanel.add(textField);
                // Remove the colon and trim the label before using it as a key
                textFieldMap.put(splitData[0].trim(), textField);
            }
        }

    JButton saveButton = new JButton("Save Changes");
    saveButton.addActionListener(e -> {
        saveTrackChanges(textFieldMap, trackData[0].split(": ")[1]);
        editDialog.dispose();
    });

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> editDialog.dispose());

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    editDialog.add(fieldsPanel, BorderLayout.CENTER);
    editDialog.add(buttonPanel, BorderLayout.SOUTH);
    editDialog.setVisible(true);
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
