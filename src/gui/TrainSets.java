package gui;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import db.DatabaseConnectionHandler;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrainSets extends JPanel {

    private JFrame parentFrame;
    private JPanel boxesPanel;

    public TrainSets(JFrame parentFrame) {
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

        JLabel titleLabel = new JLabel("TRAIN SETS PAGE", SwingConstants.CENTER);
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
        refreshButton.addActionListener(e -> refreshBoxedSets());
        rightPanel.add(refreshButton);

        northPanel.add(rightPanel, BorderLayout.EAST);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(northPanel, BorderLayout.NORTH);

        boxesPanel = new JPanel();
        boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.Y_AXIS));
        boxesPanel.setBackground(Color.WHITE); // Setting background color to white
        refreshBoxedSets();

        JScrollPane scrollPane = new JScrollPane(boxesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshBoxedSets() {
        boxesPanel.removeAll();
        java.util.List<String[]> boxedSetsData = getBoxedSetContents(); // Fetch the boxed set contents

        // Group the data by BoxedSetID
        Map<String, List<String[]>> groupedData = new HashMap<>();
        for (String[] data : boxedSetsData) {
            String setId = data[0].split(": ")[1]; // Extract Boxed Set ID
            groupedData.computeIfAbsent(setId, k -> new ArrayList<>()).add(data);
        }

        // Create a box for each grouped set of details
        for (List<String[]> group : groupedData.values()) {
            JPanel boxPanel = createBox(group);
            boxesPanel.add(boxPanel);
            boxesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        boxesPanel.revalidate();
        boxesPanel.repaint();
    }


    // Call this method when the Add button is clicked
    private void openAddDialog() {
        JDialog addDialog = new JDialog(parentFrame, "Add New Locomotive", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(400, 300);
        addDialog.setLocationRelativeTo(parentFrame);

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        Map<String, JTextField> textFieldMap = new HashMap<>();

        // Define the fields for the new locomotive details
        String[] fields = {
            "Product Code", "Brand Name", "Product Name", "Retail Price",
            "Product Quantity", "Model Type", "Gauge", "Historical Era", "DCC Code"
        };

        // Create labels and text fields for each field
        for (String field : fields) {
            JLabel label = new JLabel(field);
            JTextField textField = new JTextField(20);
            fieldsPanel.add(label);
            fieldsPanel.add(textField);
            textFieldMap.put(field, textField);
        }

        JButton saveButton = new JButton("Save New Locomotive");
        saveButton.addActionListener(e -> {
            saveNewLocomotive(textFieldMap);
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

    private boolean insertLocomotive(DatabaseConnectionHandler db, Map<String, JTextField> textFieldMap) throws SQLException {
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
    
            // Insert into Locomotives table
            String insertLocomotivesSQL = "INSERT INTO Locomotives (productCode, historicalEra, DCCCode) VALUES (?, ?, ?)";
            try (PreparedStatement pstmtLocomotives = db.con.prepareStatement(insertLocomotivesSQL)) {
                pstmtLocomotives.setString(1, textFieldMap.get("Product Code").getText());
                pstmtLocomotives.setString(2, textFieldMap.get("Historical Era").getText());
                pstmtLocomotives.setString(3, textFieldMap.get("DCC Code").getText());
                pstmtLocomotives.executeUpdate();
            }
    
            db.con.commit(); // Commit transaction
            success = true; // if we reached this point, everything went well
    
        } catch (SQLException e) {
            db.con.rollback(); // Roll back transaction if anything goes wrong
            throw e; // Rethrow the exception after rolling back to handle it in the calling method
        } finally {
            db.con.setAutoCommit(true); // Restore default behavior
        }
    
        return success; // return the status of the insert operation
    }
    
    private void saveNewLocomotive(Map<String, JTextField> textFieldMap) {
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        boolean isInserted = false;
    
        try {
            db.openConnection();
            isInserted = insertLocomotive(db, textFieldMap);
    
            if (isInserted) {
                JOptionPane.showMessageDialog(this, "New locomotive added successfully!");
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
            refreshBoxedSets();
        }
    }


    private java.util.List<String[]> getBoxedSetContents() {
        java.util.List<String[]> boxedSetContents = new java.util.ArrayList<>();
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();
        String sqlQuery = "SELECT " +
                "BoxedSetContents.boxedSetId, " +
                "BoxedSetContents.individual_productCode, " +
                "BoxedSetContents.quantity, " +
                "Individual.modelType, " +
                "Individual.gauge, " +
                "Product.brandName, " +
                "Product.productName, " +
                "Product.retailPrice, " +
                "Product.productQuantity " +
                "FROM BoxedSetContents " +
                "INNER JOIN Individual ON BoxedSetContents.individual_productCode = Individual.productCode " +
                "INNER JOIN Product ON Individual.productCode = Product.productCode;";
    
        try (PreparedStatement pstmt = db.con.prepareStatement(sqlQuery);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String boxedSetId = rs.getString("boxedSetId");
                String individualProductCode = rs.getString("individual_productCode");
                int quantity = rs.getInt("quantity");
                String modelType = rs.getString("modelType");
                String gauge = rs.getString("gauge");
                String brandName = rs.getString("brandName");
                String productName = rs.getString("productName");
                float retailPrice = rs.getFloat("retailPrice");
                int productQuantity = rs.getInt("productQuantity");
    
                boxedSetContents.add(new String[]{
                        "Boxed Set ID: " + boxedSetId,
                        "Individual Product Code: " + individualProductCode,
                        "Quantity: " + quantity,
                        "Model Type: " + modelType,
                        "Gauge: " + gauge,
                        "Brand Name: " + brandName,
                        "Product Name: " + productName,
                        "Retail Price: $" + retailPrice,
                        "Product Quantity: " + productQuantity
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return boxedSetContents;
    }

    private JPanel createBox(List<String[]> boxedSetContents) {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
    
        String lastBoxedSetId = "";
    
        for (int i = 0; i < boxedSetContents.size(); i++) {
            String[] productDetails = boxedSetContents.get(i);
            String currentBoxedSetId = productDetails[0].split(": ")[1];
    
            if (!currentBoxedSetId.equals(lastBoxedSetId)) {
                // Reset the gridx for a new Boxed Set ID
                gbcMain.gridx = 0;
    
                // Boxed Set ID label
                JLabel setIdLabel = new JLabel("Boxed Set ID: " + currentBoxedSetId);
                setIdLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                JPanel setIdPanel = new JPanel(new GridBagLayout());
                setIdPanel.add(setIdLabel);
                gbcMain.gridwidth = GridBagConstraints.REMAINDER; // Span across all columns
                mainPanel.add(setIdPanel, gbcMain); // Add the label panel to the main panel
    
                // Move to the next row to start adding product details
                gbcMain.gridy++;
                lastBoxedSetId = currentBoxedSetId;
            }
    
            // Product details panel
            JPanel productPanel = new JPanel();
            productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.PAGE_AXIS));
            for (int j = 1; j < productDetails.length; j++) { // Skip the Boxed Set ID
                JLabel label = new JLabel(productDetails[j]);
                label.setFont(new Font("SansSerif", Font.PLAIN, 12));
                productPanel.add(label);
            }
    
            // Add the product panel to the main panel
            gbcMain.gridwidth = 1; // Each product detail panel takes up one column
            gbcMain.fill = GridBagConstraints.BOTH;
            gbcMain.weightx = 1.0;
            mainPanel.add(productPanel, gbcMain);
    
            // Check if this is the last item in the boxed set
            boolean isLastItemInSet = (i == boxedSetContents.size() - 1) || 
                                      (!boxedSetContents.get(i + 1)[0].split(": ")[1].equals(currentBoxedSetId));
            if (isLastItemInSet) {
                // Increment the row position for the buttons
                gbcMain.gridy++;
    
                // Create a panel to hold the edit and delete buttons
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the buttons
                JButton editButton = new JButton("Edit");
                JButton deleteButton = new JButton("Delete");
                buttonPanel.add(editButton);
                styleButton(editButton, new Color(144, 238, 144)); // Light green color
                buttonPanel.add(deleteButton);
                styleButton(deleteButton, new Color(255, 99, 71)); // Tomato color
    
                // Set the weightx for button panel to stretch across the grid width
                gbcMain.weightx = 1.0;
                gbcMain.fill = GridBagConstraints.HORIZONTAL;
    
                // Set insets for the button panel to add space between it and the product details
                gbcMain.insets = new Insets(50, 0, 0, 0); // Top padding
    
                // Add the button panel to the main panel, spanning the remaining columns
                gbcMain.gridwidth = GridBagConstraints.REMAINDER;
                mainPanel.add(buttonPanel, gbcMain);
    
                // Reset insets and weightx for the next components
                gbcMain.insets = new Insets(0, 0, 0, 0);
                gbcMain.weightx = 0;
    
                // Prepare for the next set of products
                gbcMain.gridy++;
                gbcMain.gridx = 0;
            } else {
                // Prepare for the next product panel in the same set
                gbcMain.gridx++;
            }
        }
    
        // Add glue at the end to push everything to the top
        gbcMain.weighty = 1; // Assign remaining vertical space to the glue
        mainPanel.add(Box.createVerticalGlue(), gbcMain);
    
        return mainPanel;
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

            refreshBoxedSets();
        }
    }

    private boolean updateLocomotive(DatabaseConnectionHandler db, Map<String, JTextField> textFieldMap, String productCode) throws SQLException {
        boolean success = false; // default to false, will be set to true if updates succeed
        db.con.setAutoCommit(false); // Begin transaction

        try {
            // Update Product table
            String updateProductSQL = "UPDATE Product SET brandName = ?, productName = ?, retailPrice = ?, productQuantity = ? WHERE productCode = ?";
            try (PreparedStatement pstmtProduct = db.con.prepareStatement(updateProductSQL)) {
                pstmtProduct.setString(1, textFieldMap.get("Brand Name").getText());
                pstmtProduct.setString(2, textFieldMap.get("Product Name").getText());
                String retailPriceText = textFieldMap.get("Retail Price").getText().replaceAll("[^\\d.]", ""); // Remove non-numeric characters.
                pstmtProduct.setFloat(3, Float.parseFloat(retailPriceText));
                pstmtProduct.setInt(4, Integer.parseInt(textFieldMap.get("Product Quantity").getText()));
                pstmtProduct.setString(5, productCode);
                pstmtProduct.executeUpdate();
            }

            // Update Individual table
            String updateIndividualSQL = "UPDATE Individual SET modelType = ?, gauge = ? WHERE productCode = ?";
            try (PreparedStatement pstmtIndividual = db.con.prepareStatement(updateIndividualSQL)) {
                pstmtIndividual.setString(1, textFieldMap.get("Model Type").getText());
                pstmtIndividual.setString(2, textFieldMap.get("Gauge").getText());
                pstmtIndividual.setString(3, productCode);
                pstmtIndividual.executeUpdate();
            }

            // Update Locomotives table
            String updateLocomotivesSQL = "UPDATE Locomotives SET historicalEra = ?, DCCCode = ? WHERE productCode = ?";
            try (PreparedStatement pstmtLocomotives = db.con.prepareStatement(updateLocomotivesSQL)) {
                pstmtLocomotives.setString(1, textFieldMap.get("Historical Era").getText());
                pstmtLocomotives.setString(2, textFieldMap.get("DCC Code").getText());
                pstmtLocomotives.setString(3, productCode);
                pstmtLocomotives.executeUpdate();
            }

            db.con.commit(); // Commit transaction
            success = true; // if we reached this point, everything went well

        } catch (SQLException e) {
            db.con.rollback(); // Roll back transaction if anything goes wrong
            throw e; // Rethrow the exception after rolling back to handle it in the calling method
        } finally {
            db.con.setAutoCommit(true); // Restore default behavior
        }

        return success; // return the status of the update operation
    }


    private void saveLocomotiveChanges(Map<String, JTextField> textFieldMap, String productCode) {
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        boolean isUpdated = false;
    
        try {
            db.openConnection();
            isUpdated = updateLocomotive(db, textFieldMap, productCode);
    
            if (isUpdated) {
                JOptionPane.showMessageDialog(this, "Locomotive updated successfully!");
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
            refreshBoxedSets();
        }
    }

    private void openEditDialog(String[] locomotiveData) {
        JDialog editDialog = new JDialog(parentFrame, "Edit Locomotive", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(400, 300);
        editDialog.setLocationRelativeTo(parentFrame);
    
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        Map<String, JTextField> textFieldMap = new HashMap<>();
    
        // Create text fields pre-filled with locomotive data
        for (String data : locomotiveData) {
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
        saveLocomotiveChanges(textFieldMap, locomotiveData[0].split(": ")[1]);
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