package main.gui;
import main.db.DatabaseConnectionHandler;
import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class displayInduvidualProductsUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI(getLocomotives()));
    }

    public static void createAndShowGUI(String[][] locomotiveDetails) {
        JFrame frame = new JFrame("Product Details");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Product Details Page", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        northPanel.add(headerLabel, BorderLayout.NORTH);

        // View Cart Button
        JButton viewCartButton = new JButton("View Cart");
        viewCartButton.addActionListener(e -> {
            // TODO: Implement the view cart functionality
        });

        // Main Panel for product details
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        for (String[] details : locomotiveDetails) {
            JPanel productPanel = createProductPanel(details);
            mainPanel.add(productPanel);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Adding the view cart button to the top right
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(viewCartButton);
        northPanel.add(topPanel, BorderLayout.CENTER);

        frame.add(northPanel, BorderLayout.NORTH);

        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }

    private static JPanel createProductPanel(String[] details) {
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.X_AXIS));
    
        for (String detail : details) {
            JLabel label = new JLabel(detail);
            label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            productPanel.add(label);
        }
    
        // Quantity Selector
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        Dimension spinnerSize = new Dimension(60, 25); // Set the desired size for the spinner
        quantitySpinner.setMaximumSize(spinnerSize); // Set the maximum size
        quantitySpinner.setPreferredSize(spinnerSize); // Set the preferred size
        quantitySpinner.setMinimumSize(spinnerSize); // Set the minimum size
        productPanel.add(quantitySpinner);
    
        // Add to Cart Button
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> {
            // TODO: Implement add to cart functionality
        });
        productPanel.add(addToCartButton);
    
        // This will keep the spinner and button to the right
        productPanel.add(Box.createHorizontalGlue());
    
        productPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        return productPanel;
    }
    

    public static String[][] getLocomotives() {
        // Open a connection to the database
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

        String[][] locomotiveDetails = null;
    
        try {
            // Create a statement
            Statement statement = db.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // Execute the query
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
                  "FROM " +
                  "Locomotives " +
                  "INNER JOIN Product ON Locomotives.productCode = Product.productCode " +
                  "INNER JOIN Individual ON Locomotives.productCode = Individual.productCode;";

            
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            int rowCount = 0; 

            while (resultSet.next()) {
                
                rowCount++;
            }
            
            locomotiveDetails = new String[rowCount][7];

            resultSet.beforeFirst();

            int rowNum = 0;
    
            // Process the ResultSet and populate array
            while (resultSet.next()){
                locomotiveDetails[rowNum][0] = resultSet.getString("modelType"); // This needs to be done for all of the information we want to store to display to customers
                locomotiveDetails[rowNum][1] = resultSet.getString("productName");
                locomotiveDetails[rowNum][2] = resultSet.getString("brandName");
                locomotiveDetails[rowNum][3] = resultSet.getString("DCCCode");
                locomotiveDetails[rowNum][4] = resultSet.getString("gauge");
                locomotiveDetails[rowNum][5] = resultSet.getString("historicalEra");
                locomotiveDetails[rowNum][6] = resultSet.getString("retailPrice");
                rowNum++;
            }

        } catch (SQLException e) {
            // Handle the exception appropriately in your application
            e.printStackTrace();  
        } finally {
            // Close resources
            db.closeConnection();
        }
    
        return locomotiveDetails;
    }
}
