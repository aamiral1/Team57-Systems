package Categories;

import java.awt.*;
import javax.swing.*;
import main.db.DatabaseConnectionHandler;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Controllers extends JPanel {

    private JFrame parentFrame;
    private JPanel boxesPanel;

    public Controllers(JFrame parentFrame) {
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

        JLabel titleLabel = new JLabel("CONTROLLERS PAGE", SwingConstants.CENTER);
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
        refreshButton.addActionListener(e -> refreshController());
        rightPanel.add(refreshButton);

        northPanel.add(rightPanel, BorderLayout.EAST);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(northPanel, BorderLayout.NORTH);

        boxesPanel = new JPanel();
        boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.Y_AXIS));
        boxesPanel.setBackground(Color.WHITE); // Setting background color to white
        refreshController();

        JScrollPane scrollPane = new JScrollPane(boxesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshController() {
        boxesPanel.removeAll();
        java.util.List<String[]> Controllers = getController();
        for (String[] Controller : Controllers) {
            JPanel boxPanel = createBox(Controller);
            boxesPanel.add(boxPanel);
            boxesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        boxesPanel.revalidate();
        boxesPanel.repaint();
    }

    // Call this method when the Add button is clicked
    private void openAddDialog() {
        JDialog addDialog = new JDialog(parentFrame, "Add New Controller", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(400, 300);
        addDialog.setLocationRelativeTo(parentFrame);

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        Map<String, JTextField> textFieldMap = new HashMap<>();

        // Define the fields for the new Controller details
        String[] fields = {
            "Product Code", "Brand Name", "Product Name", "Retail Price",
            "Product Quantity", "Model Type", "Gauge", "IsDigital"
        };

        // Create labels and text fields for each field
        for (String field : fields) {
            JLabel label = new JLabel(field);
            JTextField textField = new JTextField(20);
            fieldsPanel.add(label);
            fieldsPanel.add(textField);
            textFieldMap.put(field, textField);

        }

        JButton saveButton = new JButton("Save New Controller");
        saveButton.addActionListener(e -> {
            saveController(textFieldMap);
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


    private boolean insertController(DatabaseConnectionHandler db, Map<String, JTextField> textFieldMap) throws SQLException {
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

            // Retrieve the product code and isDigital values from the text fields
            String productCode = textFieldMap.get("Product Code").getText().trim();
            JTextField isDigitalField = textFieldMap.get("IsDigital"); // Capitalize 'IsDigital'
            ;

            // Check if the isDigital field is missing                  
            if (isDigitalField == null) {
                System.out.println("The 'isDigital' field is missing.");
                return false;
            }

            // Normalize the isDigital input to lower case for comparison
            String isDigitalText = isDigitalField.getText().trim().toLowerCase();
            int isDigitalValue;

            // Determine the isDigitalValue based on the input
            if ("yes".equals(isDigitalText) || "1".equals(isDigitalText)) {
                isDigitalValue = 1;
            } else if ("no".equals(isDigitalText) || "0".equals(isDigitalText)) {
                isDigitalValue = 0;
            } else {
                // Show an error message if the input is not recognized
                JOptionPane.showMessageDialog(null, "Invalid input for 'isDigital'. Must be 'Yes' or 'No', or '1' or '0'.");
                return false;
            }

            // Construct the SQL query with the checked values
            String insertControllersSQL = "INSERT INTO Controller (productCode, isDigital) VALUES ('" + productCode + "', " + isDigitalValue + ")";

            try (Statement stmtControllers = db.con.createStatement()) {
                // Execute the update with the SQL statement
                stmtControllers.executeUpdate(insertControllersSQL);
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
    
    private void saveController(Map<String, JTextField> textFieldMap) {
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        boolean isInserted = false;
    
        try {
            db.openConnection();
            isInserted = insertController(db, textFieldMap);
    
            if (isInserted) {
                JOptionPane.showMessageDialog(this, "New Controller added successfully!");
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
            refreshController();
        }
    }

    private java.util.List<String[]> getController() {
        java.util.List<String[]> Controllers = new java.util.ArrayList<>();
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
                "Controller.isDigital " +
                "FROM Product " +
                "INNER JOIN Individual ON Product.productCode = Individual.productCode " +
                "INNER JOIN Controller ON Individual.productCode = Controller.productCode;";
    
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
                Integer isDigital = rs.getInt("isDigital");
    
                Controllers.add(new String[]{
                        "Product Code: " + productCode,
                        "Brand Name: " + brandName,
                        "Product Name: " + productName,
                        "Retail Price: $" + retailPrice,
                        "Product Quantity: " + productQuantity,
                        "Model Type: " + modelType,
                        "Gauge: " + gauge,
                        "IsDigital: " + isDigital
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return Controllers;
    }

    private JPanel createBox(String[] ControllerData) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.setBackground(Color.WHITE);

        for (String data : ControllerData) {
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
        String productCode = ControllerData[0].split(": ")[1];
        deleteButton.addActionListener(e -> deleteController(productCode));
        buttonPanel.add(deleteButton);

        JButton editButton = new JButton("Edit");
        styleButton(editButton, new Color(144, 238, 144)); // Light green color
        editButton.addActionListener(e -> openEditDialog(ControllerData));
        buttonPanel.add(editButton);

        panel.add(buttonPanel);

        return panel;
    }


    private void deleteController(String productCode) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this Controller?",
                "Delete Confirmation",
                JOptionPane.YES_NO_OPTION);
    
        if (confirm == JOptionPane.YES_OPTION) {
            DatabaseConnectionHandler db = new DatabaseConnectionHandler();
            db.openConnection();
            try {
                db.con.setAutoCommit(false); // Start transaction

                String deleteControllersSQL = "DELETE FROM Controller WHERE productCode = ?";
                try (PreparedStatement pstmtControllers = db.con.prepareStatement(deleteControllersSQL)) {
                    pstmtControllers.setString(1, productCode);
                    pstmtControllers.executeUpdate();
                }
    
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
    
            refreshController(); // Refresh the display to show that the Controller has been deleted
        }
    }

    private boolean updateController(DatabaseConnectionHandler db, Map<String, JTextField> textFieldMap, String productCode) throws SQLException {
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
            

            String updateControllersSQL = "UPDATE Controller SET isDigital = ? WHERE productCode = ?";
            try (PreparedStatement pstmtController = db.con.prepareStatement(updateControllersSQL)) {
                JTextField isDigitalField = textFieldMap.get("IsDigital"); // Capitalize 'IsDigital'                
                if (isDigitalField == null) {
                    throw new IllegalStateException("Error: 'isDigital' field is missing from the form.");
                }
                
                String isDigitalText = isDigitalField.getText().trim();
                int isDigitalValue;
                try {
                    isDigitalValue = Integer.parseInt(isDigitalText);
                    if (isDigitalValue != 0 && isDigitalValue != 1) {
                        throw new IllegalArgumentException("isDigital field must be '1' or '0'.");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid format for 'isDigital'. Must be a valid integer '1' or '0'.");
                }
                
                pstmtController.setInt(1, isDigitalValue);
                pstmtController.setString(2, productCode);                
                pstmtController.executeUpdate();
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


    private void saveControllerChanges(Map<String, JTextField> textFieldMap, String productCode) {
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        boolean isUpdated = false;
    
        try {
            db.openConnection();
            isUpdated = updateController(db, textFieldMap, productCode);
    
            if (isUpdated) {
                JOptionPane.showMessageDialog(this, "Controller updated successfully!");
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
            refreshController();
        }
    }

    private void openEditDialog(String[] ControllerData) {
        JDialog editDialog = new JDialog(parentFrame, "Edit Controllers", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(400, 300);
        editDialog.setLocationRelativeTo(parentFrame);
    
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        Map<String, JTextField> textFieldMap = new HashMap<>();
    
        // Create text fields pre-filled with Controller data
        for (String data : ControllerData) {
            String[] splitData = data.split(":\\s+");
            if (splitData.length == 2) {
                String key = splitData[0].trim(); // Trim the key to remove any leading/trailing whitespace
                JTextField textField = new JTextField(splitData[1]);
                fieldsPanel.add(new JLabel(key));
                fieldsPanel.add(textField);
                textFieldMap.put(key, textField); // Use the trimmed key
            }
        }
    
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            if (textFieldMap.containsKey("IsDigital")) {
                String isDigitalValue = textFieldMap.get("IsDigital").getText().trim();
                
                if (!isDigitalValue.isEmpty()) {
                    // Continue with saving changes
                    saveControllerChanges(textFieldMap, ControllerData[0].split(": ")[1]);
                    editDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(editDialog, "The 'isDigital' field is empty.");
                }
            } else {
                JOptionPane.showMessageDialog(editDialog, "The 'isDigital' field is missing from the form.");
            }
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
