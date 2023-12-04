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
            if (setId.startsWith("M")) { // Check if the ID starts with M
                groupedData.computeIfAbsent(setId, k -> new ArrayList<>()).add(data);
            }
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
        JDialog addDialog = new JDialog(parentFrame, "Add New Train Set", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(400, 300); // Adjust size as needed
        addDialog.setLocationRelativeTo(parentFrame);
    
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    
        fieldsPanel.add(new JLabel("BoxedSet ID:"));
        JTextField boxedSetIdField = new JTextField(20);
        fieldsPanel.add(boxedSetIdField);
    
        fieldsPanel.add(new JLabel("Locomotive Product Code:"));
        JTextField locomotiveCodeField = new JTextField(20);
        fieldsPanel.add(locomotiveCodeField);
    
        fieldsPanel.add(new JLabel("Locomotive Quantity:"));
        JTextField locomotiveQuantityField = new JTextField(20);
        fieldsPanel.add(locomotiveQuantityField);
    
        fieldsPanel.add(new JLabel("Rolling Stock Product Code:"));
        JTextField rollingStockCodeField = new JTextField(20);
        fieldsPanel.add(rollingStockCodeField);
    
        fieldsPanel.add(new JLabel("Rolling Stock Quantity:"));
        JTextField rollingStockQuantityField = new JTextField(20);
        fieldsPanel.add(rollingStockQuantityField);
    
        fieldsPanel.add(new JLabel("Track Pack Product Code:"));
        JTextField trackPackCodeField = new JTextField(20);
        fieldsPanel.add(trackPackCodeField);
    
        fieldsPanel.add(new JLabel("Track Pack Quantity:"));
        JTextField trackPackQuantityField = new JTextField(20);
        fieldsPanel.add(trackPackQuantityField);
    
        fieldsPanel.add(new JLabel("Controller Product Code:"));
        JTextField controllerCodeField = new JTextField(20);
        fieldsPanel.add(controllerCodeField);
    
        JButton saveButton = new JButton("Save New Train Set");
        saveButton.addActionListener(e -> {
            String boxedSetId = boxedSetIdField.getText().trim();
    
            // Validate BoxedSetID
            if (!boxedSetId.matches("\\d+")) {
                JOptionPane.showMessageDialog(addDialog,
                    "BoxedSetID must be a numeric value.",
                    "Invalid BoxedSetID",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            if (boxedSetId.length() < 4 || Integer.parseInt(boxedSetId) % 2 != 0) {
                JOptionPane.showMessageDialog(addDialog,
                    "BoxedSetID should be at least 4 digits long and end in an even number, e.g., 0002",
                    "Invalid BoxedSetID",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // If validation passes, proceed with database insertion
            Map<String, String> trainSetData = new HashMap<>();
            trainSetData.put("Boxed Set ID", boxedSetId);
            trainSetData.put("Locomotive Product Code", locomotiveCodeField.getText().trim());
            trainSetData.put("Locomotive Quantity", locomotiveQuantityField.getText().trim());
            trainSetData.put("Rolling Stock Product Code", rollingStockCodeField.getText().trim());
            trainSetData.put("Rolling Stock Quantity", rollingStockQuantityField.getText().trim());
            trainSetData.put("Track Pack Product Code", trackPackCodeField.getText().trim());
            trainSetData.put("Track Pack Quantity", trackPackQuantityField.getText().trim());
            trainSetData.put("Controller Product Code", controllerCodeField.getText().trim());
        
            // Other product codes and quantities as needed
        
            DatabaseConnectionHandler db = new DatabaseConnectionHandler(); // Ensure this is correctly initialized
            try {
                if (insertBoxedSetContents(db, trainSetData)) {
                    JOptionPane.showMessageDialog(addDialog, "Train set added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshBoxedSets(); // Refresh the data after a new entry is added
                } else {
                    JOptionPane.showMessageDialog(addDialog, "Failed to add train set.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(addDialog, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        
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
    
    // A method to convert boxed set IDs based on the pattern
    private String convertBoxedSetID(String boxedSetID) {
        // Assuming the input format is "000X"
        int number = Integer.parseInt(boxedSetID); // Parse the number from the input
        char prefix = 'P'; // Default prefix
        if (number % 2 == 0) {
            prefix = 'M'; // Change prefix to 'M' for even numbers
        }
        return String.format("%c%03d", prefix, number);
    }

    public boolean insertBoxedSetContents(DatabaseConnectionHandler db, Map<String, String> trainSetData) throws SQLException {
        boolean success = false; // Default to false, will be set to true if inserts succeed
        Connection conn = db.getConnection(); // Ensure this is your DatabaseConnectionHandler instance

        if (conn != null) {
            conn.setAutoCommit(false); // Begin transaction
            try {
                // Extract values from trainSetData
                String boxedSetId = trainSetData.get("Boxed Set ID");
                String locomotiveProductCode = trainSetData.get("Locomotive Product Code");
                int locomotiveQuantity = Integer.parseInt(trainSetData.get("Locomotive Quantity"));
                String rollingStockProductCode = trainSetData.get("Rolling Stock Product Code");
                int rollingStockQuantity = Integer.parseInt(trainSetData.get("Rolling Stock Quantity"));
                String trackPackProductCode = trainSetData.get("Track Pack Product Code");
                int trackPackQuantity = Integer.parseInt(trainSetData.get("Track Pack Quantity"));
                String controllerProductCode = trainSetData.get("Controller Product Code");

                // Insert into BoxedSetContents table for Locomotive
                String insertLocomotiveSQL = "INSERT INTO BoxedSetContents (boxedSetID, quantity, product_productCode) VALUES (?, ?, ?)";
                try (PreparedStatement pstmtLocomotive = conn.prepareStatement(insertLocomotiveSQL)) {
                    pstmtLocomotive.setString(1, boxedSetId);
                    pstmtLocomotive.setInt(2, locomotiveQuantity);
                    pstmtLocomotive.setString(3, locomotiveProductCode);
                    pstmtLocomotive.executeUpdate();
                }

                // Insert into BoxedSetContents table for Rolling Stock
                String insertRollingStockSQL = "INSERT INTO BoxedSetContents (boxedSetID, quantity, product_productCode) VALUES (?, ?, ?)";
                try (PreparedStatement pstmtRollingStock = conn.prepareStatement(insertRollingStockSQL)) {
                    pstmtRollingStock.setString(1, boxedSetId);
                    pstmtRollingStock.setInt(2, rollingStockQuantity);
                    pstmtRollingStock.setString(3, rollingStockProductCode);
                    pstmtRollingStock.executeUpdate();
                }

                // Insert into BoxedSetContents table for Track Pack
                String insertTrackPackSQL = "INSERT INTO BoxedSetContents (boxedSetID, quantity, product_productCode) VALUES (?, ?, ?)";
                try (PreparedStatement pstmtTrackPack = conn.prepareStatement(insertTrackPackSQL)) {
                    pstmtTrackPack.setString(1, boxedSetId);
                    pstmtTrackPack.setInt(2, trackPackQuantity);
                    pstmtTrackPack.setString(3, trackPackProductCode);
                    pstmtTrackPack.executeUpdate();
                }

                // Insert into BoxedSetContents table for Controller
                String insertControllerSQL = "INSERT INTO BoxedSetContents (boxedSetID, quantity, product_productCode) VALUES (?, ?, ?)";
                try (PreparedStatement pstmtController = conn.prepareStatement(insertControllerSQL)) {
                    pstmtController.setString(1, boxedSetId);
                    pstmtController.setInt(2, 1); // Assuming there is only one controller in each boxed set
                    pstmtController.setString(3, controllerProductCode);
                    pstmtController.executeUpdate();
                }

                // Convert boxed set ID for BoxedSet
                String convertedBoxedSetId = convertBoxedSetID(boxedSetId);

                // Insert into BoxedSet table
                String insertBoxedSetSQL = "INSERT INTO BoxedSet (productCode, boxedSetID) VALUES (?, ?)";
                try (PreparedStatement pstmtBoxedSet = conn.prepareStatement(insertBoxedSetSQL)) {
                    pstmtBoxedSet.setString(1, convertedBoxedSetId); // Use the converted value as productCode
                    pstmtBoxedSet.setString(2, boxedSetId); // Use the original value as boxedSetID
                    pstmtBoxedSet.executeUpdate();
                }

                conn.commit(); // Commit transaction
                success = true; // If all inserts were successful
            } catch (SQLException e) {
                conn.rollback(); // Roll back transaction if anything goes wrong
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Insert error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                success = false;
            } finally {
                conn.setAutoCommit(true); // Restore default behavior
            }
        } else {
            // Handle the scenario where conn is null, i.e., the connection could not be established
            throw new SQLException("Unable to establish a database connection.");
        }
        return success;
    }
    
    private java.util.List<String[]> getBoxedSetContents() {
        java.util.List<String[]> boxedSetContents = new java.util.ArrayList<>();
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();
        String sqlQuery = "SELECT " +
                "BoxedSet.productCode as boxedSetID, " + // Get ProductCode as boxedSetID
                "BoxedSetContents.quantity, " +
                "BoxedSetContents.product_productCode, " +
                "Individual.modelType, " +
                "Individual.gauge, " +
                "Product.brandName, " + // Additional details from Product table
                "Product.productName, " +
                "Product.retailPrice, " +
                "Product.productQuantity " +
                "FROM BoxedSetContents " +
                "INNER JOIN BoxedSet ON BoxedSetContents.boxedSetID = BoxedSet.boxedSetID " +
                "INNER JOIN Product ON BoxedSetContents.product_productCode = Product.productCode " + // Join with Product table
                "LEFT JOIN Individual ON BoxedSetContents.product_productCode = Individual.productCode";
    
        try (PreparedStatement pstmt = db.con.prepareStatement(sqlQuery);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String boxedSetID = rs.getString("boxedSetID");
                int quantity = rs.getInt("quantity");
                String individualProductCode = rs.getString("product_productCode");
                String modelType = rs.getString("modelType");
                String gauge = rs.getString("gauge");
                String brandName = rs.getString("brandName"); // Retrieve additional details
                String productName = rs.getString("productName");
                float retailPrice = rs.getFloat("retailPrice");
                int productQuantity = rs.getInt("productQuantity");
    
                boxedSetContents.add(new String[]{
                        "Boxed Set ID: " + boxedSetID,
                        "Individual Product Code: " + individualProductCode,
                        "Quantity: " + quantity,
                        "Model Type: " + modelType,
                        "Gauge: " + gauge,
                        "Brand Name: " + brandName, // Add these details to the array
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
                styleButton(editButton, new Color(144, 238, 144)); // Light green color

                editButton.addActionListener(e -> {
                    List<String> productDetailsForDialog = new ArrayList<>();
                    for (String[] details : boxedSetContents) {
                        String productCodeLabel = "Product Code";
                        String productCode = details[1].split(": ")[1]; // Extracting the product code
                        String quantityLabel = "Quantity";
                        String quantity = details[2].split(": ")[1]; // Extracting the quantity
                
                        // Add the labels and values to the list
                        productDetailsForDialog.add(productCodeLabel);
                        productDetailsForDialog.add(productCode);
                        productDetailsForDialog.add(quantityLabel);
                        productDetailsForDialog.add(quantity);
                    }
                    // Convert the list to an array and open the dialog
                    openEditDialog(productDetailsForDialog.toArray(new String[0]));
                });
                



                // Style the delete button using the existing styleButton method
                JButton deleteButton = new JButton("Delete");
                styleButton(deleteButton, new Color(255, 99, 71)); // Tomato color
                
                // Retrieve the boxed set ID for the delete button
                String boxedSetIdForDeletion = currentBoxedSetId; // Ensure this is the correct ID for the current set
                
                deleteButton.addActionListener(e -> {
                    int response = JOptionPane.showConfirmDialog(
                            null,
                            "Are you sure you want to delete this boxed set?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (response == JOptionPane.YES_OPTION) {
                        if (deleteBoxedSet(boxedSetIdForDeletion)) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Boxed Set deleted successfully.",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            refreshBoxedSets();
                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Failed to delete the boxed set.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                });
    
                // Add the buttons to the button panel
                buttonPanel.add(editButton);
                buttonPanel.add(deleteButton);
    
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
        
    // Add a method to delete the boxed set from the database
    private boolean deleteBoxedSet(String productCode) {
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        boolean deleted = false;
        String correctvalue = null;

        try {
            db.openConnection();
            db.con.setAutoCommit(false); // Begin transaction

            // Select the boxedSetID from the database using the productCode
            String selectBoxedSetIDSQL = "SELECT boxedSetID FROM BoxedSet WHERE productCode = ?";
            try (PreparedStatement pstmtBoxedSet = db.con.prepareStatement(selectBoxedSetIDSQL)) {
                pstmtBoxedSet.setString(1, productCode);
                try (ResultSet rs = pstmtBoxedSet.executeQuery()) {
                    if (rs.next()) {
                        correctvalue = rs.getString("boxedSetID");
                    }
                }
            }

            // Only proceed if the correctvalue (boxedSetID) was found
            if (correctvalue != null) {
                // Delete BoxedSetContents related to the boxed set
                String deleteBoxedSetContentsSQL = "DELETE FROM BoxedSetContents WHERE boxedSetID = ?";
                try (PreparedStatement pstmtBoxedSetContents = db.con.prepareStatement(deleteBoxedSetContentsSQL)) {
                    pstmtBoxedSetContents.setString(1, correctvalue);
                    pstmtBoxedSetContents.executeUpdate();
                }

                // Delete BoxedSet entry
                String deleteBoxedSetSQL = "DELETE FROM BoxedSet WHERE productCode = ?";
                try (PreparedStatement pstmtBoxedSet = db.con.prepareStatement(deleteBoxedSetSQL)) {
                    pstmtBoxedSet.setString(1, productCode);
                    pstmtBoxedSet.executeUpdate();
                }

                db.con.commit(); // Commit transaction
                deleted = true; // Mark deletion as successful
            } else {
                // If correctvalue was not found, rollback the transaction
                db.con.rollback();
            }
        } catch (SQLException e) {
            try {
                db.con.rollback(); // Roll back transaction if anything goes wrong
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (db.con != null) {
                    db.con.setAutoCommit(true); // Restore default behavior
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            db.closeConnection();
        }

        return deleted;
    }

    private void openEditDialog(String[] productDetails) {
        JDialog editDialog = new JDialog(parentFrame, "Edit Product Details", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(400, 300);
        editDialog.setLocationRelativeTo(parentFrame);
    
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Adjust the GridLayout rows as needed
    
        for (int i = 0; i < productDetails.length; i += 4) {
            // Create and add the product code label and text field
            fieldsPanel.add(new JLabel(productDetails[i])); // Product Code label
            JTextField productCodeField = new JTextField(productDetails[i + 1]); // Product Code value
            fieldsPanel.add(productCodeField);
    
            // Create and add the quantity label and text field
            fieldsPanel.add(new JLabel(productDetails[i + 2])); // Quantity label
            JTextField quantityField = new JTextField(productDetails[i + 3]); // Quantity value
            fieldsPanel.add(quantityField);
        }
    
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            // Logic to save changes goes here
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
