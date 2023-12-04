package gui;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import db.DatabaseConnectionHandler;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrackPacks extends JPanel {

    private JFrame parentFrame;
    private JPanel boxesPanel;

    public TrackPacks(JFrame parentFrame) {
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

        JLabel titleLabel = new JLabel("TRACK PACKS PAGE", SwingConstants.CENTER);
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
        List<String[]> boxedSetsData = getBoxedSetContents(); // Fetch the boxed set contents
    
        // Group the data by BoxedSetID, filtering to include only those starting with 'P'
        Map<String, List<String[]>> groupedData = new HashMap<>();
        for (String[] data : boxedSetsData) {
            String setId = data[0].split(": ")[1]; // Extract Boxed Set ID
            if (setId.startsWith("P")) { // Filter for IDs starting with 'P'
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

    private void openAddDialog() {
        JDialog addDialog = new JDialog(parentFrame, "Add New Train Set", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(500, 400); // Adjust size as needed
        addDialog.setLocationRelativeTo(parentFrame);
    
        // Main panel for fields
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
    
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        fieldsPanel.add(inputPanel);
    
        inputPanel.add(new JLabel("BoxedSet ID:"));
        JTextField boxedSetIdField = new JTextField(20);
        inputPanel.add(boxedSetIdField);
    
        // Combo box for selecting the number of tracks
        inputPanel.add(new JLabel("Number of Tracks:"));
        String[] numberOfTracksOptions = {"2", "3", "4", "5"};
        JComboBox<String> trackNumberComboBox = new JComboBox<>(numberOfTracksOptions);
        inputPanel.add(trackNumberComboBox);
    
        // Panels to hold dynamically added track fields
        List<JPanel> trackPanels = new ArrayList<>();
        List<JTextField> productCodeFields = new ArrayList<>();
        List<JTextField> quantityFields = new ArrayList<>();
    
        // Initialize with 2 tracks
        updateTrackFields(2, trackPanels, productCodeFields, quantityFields, fieldsPanel);
    
        // Action listener for the combo box to update track fields
        trackNumberComboBox.addActionListener(e -> {
            int numberOfTracks = Integer.parseInt((String) trackNumberComboBox.getSelectedItem());
            updateTrackFields(numberOfTracks, trackPanels, productCodeFields, quantityFields, fieldsPanel);
            addDialog.pack();
        });
    
        // Save button with action listener to insert a new track pack
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String boxedSetId = boxedSetIdField.getText();
            String productCode = convertBoxedSetID(boxedSetId); // Convert boxedSetID to productCode
        
            DatabaseConnectionHandler db = new DatabaseConnectionHandler();
            try {
                db.openConnection();
                
                // Start transaction
                db.con.setAutoCommit(false);
        
                // Insert into BoxedSetContents
                for (int i = 0; i < productCodeFields.size(); i++) {
                    String trackProductCode = productCodeFields.get(i).getText();
                    int quantity = Integer.parseInt(quantityFields.get(i).getText()); // Ensure this is a valid integer
        
                    String insertBoxedSetContentsSQL = "INSERT INTO BoxedSetContents (boxedSetID, quantity, product_productCode) VALUES (?, ?, ?)";
                    try (PreparedStatement pstmtBoxedSetContents = db.con.prepareStatement(insertBoxedSetContentsSQL)) {
                        pstmtBoxedSetContents.setString(1, boxedSetId);
                        pstmtBoxedSetContents.setInt(2, quantity);
                        pstmtBoxedSetContents.setString(3, trackProductCode);
                        pstmtBoxedSetContents.executeUpdate();
                    }
                }
        
                // Insert into BoxedSet
                String insertBoxedSetSQL = "INSERT INTO BoxedSet (productCode, boxedSetID) VALUES (?, ?)";
                try (PreparedStatement pstmtBoxedSet = db.con.prepareStatement(insertBoxedSetSQL)) {
                    pstmtBoxedSet.setString(1, productCode);
                    pstmtBoxedSet.setString(2, boxedSetId);
                    pstmtBoxedSet.executeUpdate();
                }
        
                // Commit transaction
                db.con.commit();
        
                // Show success message
                JOptionPane.showMessageDialog(addDialog, "New train set added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                try {
                    db.con.rollback();
                } catch (SQLException ex1) {
                    ex1.printStackTrace();
                }
                JOptionPane.showMessageDialog(addDialog, "Failed to add new train set. Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    db.con.setAutoCommit(true);
                    db.closeConnection();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            // Refresh the displayed data
            refreshBoxedSets();
        });
        
        
        // Cancel button to dismiss the dialog
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> addDialog.dispose());
    
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    
        // Add the fields panel and button panel to the dialog
        JScrollPane scrollPane = new JScrollPane(fieldsPanel);
        addDialog.add(scrollPane, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setVisible(true);
    }
    
    private void updateTrackFields(int numberOfTracks, List<JPanel> trackPanels, List<JTextField> productCodeFields, List<JTextField> quantityFields, JPanel fieldsPanel) {
        // Remove previous track fields
        for (JPanel panel : trackPanels) {
            fieldsPanel.remove(panel);
        }
        trackPanels.clear();
        productCodeFields.clear();
        quantityFields.clear();
    
        // Add new track fields based on selection
        for (int i = 1; i <= numberOfTracks; i++) {
            JPanel trackPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            trackPanel.add(new JLabel("Track " + i + " Product Code:"));
            JTextField productCodeField = new JTextField(20);
            trackPanel.add(productCodeField);
            productCodeFields.add(productCodeField);
    
            trackPanel.add(new JLabel("Track " + i + " Quantity:"));
            JTextField quantityField = new JTextField(20);
            trackPanel.add(quantityField);
            quantityFields.add(quantityField);
    
            // Add the new panel to the list and main panel
            trackPanels.add(trackPanel);
            fieldsPanel.add(trackPanel);
        }
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
            JButton deleteButton = new JButton("Delete");
            buttonPanel.add(editButton);
            styleButton(editButton, new Color(144, 238, 144)); // Light green color
            buttonPanel.add(deleteButton);
            styleButton(deleteButton, new Color(255, 99, 71)); // Tomato color
        
            // Use the currentBoxedSetId as the boxedSetIdForDeletion
            String boxedSetIdForDeletion = currentBoxedSetId;
        
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
    
    private boolean deleteBoxedSet(String productCode) {
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        boolean deleted = false;
        String correctvalue = null;
    
        try {
            db.openConnection();
            db.con.setAutoCommit(false); // Start transaction
    
            // Retrieve the boxedSetID using the given productCode
            String selectBoxedSetIDSQL = "SELECT boxedSetID FROM BoxedSet WHERE productCode = ?";
            try (PreparedStatement pstmtBoxedSet = db.con.prepareStatement(selectBoxedSetIDSQL)) {
                pstmtBoxedSet.setString(1, productCode);
                try (ResultSet rs = pstmtBoxedSet.executeQuery()) {
                    if (rs.next()) {
                        correctvalue = rs.getString("boxedSetID");
                    }
                }
            }
    
            // Proceed only if the correct boxedSetID was found
            if (correctvalue != null) {
                // Delete related entries in BoxedSetContents
                String deleteBoxedSetContentsSQL = "DELETE FROM BoxedSetContents WHERE boxedSetID = ?";
                try (PreparedStatement pstmtBoxedSetContents = db.con.prepareStatement(deleteBoxedSetContentsSQL)) {
                    pstmtBoxedSetContents.setString(1, correctvalue);
                    pstmtBoxedSetContents.executeUpdate();
                }
    
                // Delete the entry in BoxedSet
                String deleteBoxedSetSQL = "DELETE FROM BoxedSet WHERE productCode = ?";
                try (PreparedStatement pstmtBoxedSet = db.con.prepareStatement(deleteBoxedSetSQL)) {
                    pstmtBoxedSet.setString(1, productCode);
                    pstmtBoxedSet.executeUpdate();
                }
    
                db.con.commit(); // Commit the transaction
                deleted = true;
            } else {
                db.con.rollback(); // Rollback if no matching boxedSetID was found
            }
        } catch (SQLException e) {
            try {
                db.con.rollback(); // Rollback on error
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (db.con != null) {
                    db.con.setAutoCommit(true); // Restore default auto-commit behavior
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            db.closeConnection();
        }
    
        return deleted;
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
