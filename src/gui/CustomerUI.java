package gui;

import javax.imageio.ImageIO;
import javax.swing.*;

//import db.DatabaseConnectionHandler;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import store.*;

public class CustomerUI extends JPanel {

    private static final String[] LABEL_TEXTS = {
            "Train Sets", "Track Packs", "Locomotives",
            "Rolling Stock", "Track", "Controllers"
    };

    private static final int IMAGE_WIDTH = 300;
    private static final int IMAGE_HEIGHT = 200;

    public CustomerUI() {

        setLayout(new BorderLayout());

        // Panel for organisation with BorderLayout
        JPanel navBarPanelOrganiser = new JPanel(new BorderLayout());

        // Navigation bar panel with FlowLayout
        JPanel navBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Browse items button - I don't think this button is necessary
        JButton browseItemsButton = new JButton("Browse Items");
        browseItemsButton.addActionListener(e -> {
        });
        navBarPanel.add(browseItemsButton);

        // View orders button
        JButton viewOrdersButton = new JButton("View Recent Orders");
        viewOrdersButton.addActionListener(e -> {
            viewRecentOrdersUI recentOrders = new viewRecentOrdersUI();
            recentOrders.orderViewer();
        });

        navBarPanel.add(viewOrdersButton);

        // Edit details button
        JButton editDetailsButton = new JButton("Edit Details");
        editDetailsButton.addActionListener(e -> {
            System.out.println("Edit Button Clicked");
            new editUserDetails(UserManager.getCurrentUser()).setVisible(true);
            // System.out.println("Edit button clicked");
            // // Create pop up dialog box
            // JDialog dialog = new JDialog();
            // dialog.setTitle("Edit Personal Details");
            // dialog.setSize(300,400);
            // dialog.setLayout(new GridLayout(0, 2));

            // // Retrieve current user's details
            // User currentUser = UserManager.getCurrentUser();
            // Object[] attributes = currentUser.getAttributes();
            // String decrypted_password =

            // // Labels and text fields for user input
            // // Create labels and text fields for user input
            // JLabel usernameLabel = new JLabel("Username:");
            // JTextField usernameField = new JTextField(attributes[1].toString());
            // JLabel nameLabel = new JLabel("Name:");
            // JTextField nameField = new JTextField(attributes[2].toString());
            // JLabel passwordLabel = new JLabel("Password:");
            // JPasswordField passwordField = new JPasswordField();
            // JLabel confirmPasswordLabel = new JLabel("Confirm password:");
            // JPasswordField confirmPasswordField = new JPasswordField();
            // JLabel emailLabel = new JLabel("Email Address:");
            // JTextField emailField = new JTextField(attributes[4].toString());
            // JLabel houseNumberLabel = new JLabel("House Number:");
            // JTextField houseNumberField = new JTextField(attributes[5].toString());
            // JLabel cityNameLabel = new JLabel("City Name:");
            // JTextField cityNameField = new JTextField(attributes[6].toString());
            // JLabel roadNameLabel = new JLabel("Road Name:");
            // JTextField roadNameField = new JTextField(attributes[7].toString());
            // JLabel postcodeLabel = new JLabel("Postcode:");
            // JTextField postcodeField = new JTextField(attributes[8].toString());
            // JButton submitButton = new JButton("Submit"); // Add a button for submitting
            // edited details

            // // Set default close operation
            // dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            // // Add the labels and text fields to the dialog
            // dialog.add(usernameLabel);
            // dialog.add(usernameField);
            // dialog.add(nameLabel);
            // dialog.add(nameField);
            // dialog.add(passwordLabel);
            // dialog.add(passwordField);
            // dialog.add(confirmPasswordLabel);
            // dialog.add(confirmPasswordField);
            // dialog.add(emailLabel);
            // dialog.add(emailField);
            // dialog.add(houseNumberLabel);
            // dialog.add(houseNumberField);
            // dialog.add(cityNameLabel);
            // dialog.add(cityNameField);
            // dialog.add(roadNameLabel);
            // dialog.add(roadNameField);
            // dialog.add(postcodeLabel);
            // dialog.add(postcodeField);
            // dialog.add(submitButton);

            // // set initial focus
            // usernameField.requestFocusInWindow();

            // dialog.setVisible(true);

            // submitButton.addActionListener(ev -> {
            // // Validate input
            // // flag for validation
            // Boolean isValid = true;

            // // Validate username
            // if (usernameField.getText().trim().isEmpty() ||
            // usernameField.getText().trim().length() < 3){
            // JOptionPane.showMessageDialog(dialog, "Username must be atleast 3
            // characters");
            // isValid = false;
            // }
            // // Validate name
            // if (nameField.getText().trim().isEmpty()){
            // JOptionPane.showMessageDialog(dialog, "Name cannot be empty");
            // isValid = false;
            // }
            // // Validate password if empty
            // if (new String(passwordField.getPassword()).trim().isEmpty() || new
            // String(confirmPasswordField.getPassword()).trim().isEmpty()){
            // JOptionPane.showMessageDialog(dialog, "Password cannot be empty");
            // isValid = false;
            // }
            // // Validate if passwords match
            // if (!new String(passwordField.getPassword()).trim().equals(new
            // String(confirmPasswordField.getPassword()).trim())){
            // JOptionPane.showMessageDialog(dialog, "Entered passwords do not match");
            // isValid = false;
            // }

            // // Validate email adddress
            // String emailPattern = "^(.+)@(.+)$";
            // if (!emailField.getText().matches(emailPattern)) {
            // JOptionPane.showMessageDialog(dialog, "Email address is not valid.");
            // isValid = false;
            // }

            // // Vallidate house number
            // if (houseNumberField.getText().trim().isEmpty()) {
            // JOptionPane.showMessageDialog(dialog, "House number cannot be empty.");
            // isValid = false;
            // }

            // // Validate City Name
            // if (cityNameField.getText().trim().isEmpty()) {
            // JOptionPane.showMessageDialog(dialog, "City name cannot be empty.");
            // isValid = false;
            // }
            // // Validate Road Name
            // if (roadNameField.getText().trim().isEmpty()) {
            // JOptionPane.showMessageDialog(dialog, "Road name cannot be empty.");
            // isValid = false;
            // }
            // // Validate Post Code
            // if (postcodeField.getText().trim().isEmpty()
            // ||postcodeField.getText().trim().length() < 5) {
            // JOptionPane.showMessageDialog(dialog, "Post Code must be atleast 5 characters
            // long");
            // isValid = false;
            // }

            // // Update user details if validation is passed
            // if (isValid){
            // String userID = currentUser.getUserID();
            // String salt = currentUser.getSalt();
            // Boolean updateStatus = currentUser.updateDetails(userID,
            // usernameField.getText(), nameField.getText(), new
            // String(confirmPasswordField.getPassword()), emailField.getText(),
            // houseNumberField.getText(),
            // cityNameField.getText(),roadNameField.getText(), postcodeField.getText(),
            // salt);

            // // Show success/failure message
            // if (updateStatus){
            // JOptionPane.showMessageDialog(dialog, "Details updated succesfully!");
            // dialog.dispose();
            // } else {
            // JOptionPane.showMessageDialog(dialog, "Details update error!");
            // dialog.dispose();
            // }
            // }

            // });
        });
        navBarPanel.add(editDetailsButton);

        // logout button - to be implemented
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
        });
        navBarPanel.add(logoutButton);

        String currentUserRole = UserManager.getCurrentUser().getRole();

        JButton staffPortal = new JButton("Staff Portal");

        if (!(currentUserRole.equals("Moderator") || currentUserRole.equals("Admin"))) {
            staffPortal.setVisible(false); // This will make the button invisible in the UI
        } else {
            staffPortal.addActionListener(e -> {
                if (currentUserRole.equals("Moderator") || currentUserRole.equals("Admin")) {
                    // Create a new frame for the Manager UI
                    JFrame managerFrame = new JFrame("Manager Portal");
                    managerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    managerFrame.setSize(1000, 700); // Set the size as per your requirement
                    managerFrame.setLocationRelativeTo(null); // Center on screen
                    managerFrame.add(new StaffUI()); // Assuming ManagerUI is a JPanel

                    // Display the new frame
                    managerFrame.setVisible(true);

                    // Get the current frame to close it
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
                    currentFrame.dispose(); // This will close the current window
                } else {
                    System.out.println("Customer has clicked");
                }
            });
        }

        navBarPanel.add(staffPortal);

        // Add the navigation bar panel to the top of the BorderLayout
        navBarPanelOrganiser.add(navBarPanel, BorderLayout.CENTER);

        // Title panel with FlowLayout for centering the title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Categories");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Add the title panel to the top of the BorderLayout
        navBarPanelOrganiser.add(titlePanel, BorderLayout.NORTH);

        add(navBarPanelOrganiser, BorderLayout.NORTH);

        // Panel for images and labels with GridLayout
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        try {

            for (int i = 0; i < 6; i++) {

                BufferedImage image = ImageIO.read(new URL(getImageUrl(i)));
                ImageIcon imageIcon = new ImageIcon(resizeImage(image, IMAGE_WIDTH, IMAGE_HEIGHT));
                JLabel imageLabel = new JLabel(imageIcon);
                imageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));

                // Create a sub-panel with BorderLayout for each image and label
                JPanel cellPanel = new JPanel(new BorderLayout());
                cellPanel.add(imageLabel, BorderLayout.CENTER);

                // Change JLabel to JButton
                JButton button = new JButton(LABEL_TEXTS[i]);
                button.setPreferredSize(new Dimension(IMAGE_WIDTH, 30)); // Set the preferred height of the button

                button.addActionListener(e -> {

                    // Get the label text of the clicked button
                    String buttonText = ((JButton) e.getSource()).getText();

                    // Close the current window
                    Window currentWindow = SwingUtilities.getWindowAncestor((Component) e.getSource());
                    currentWindow.dispose();

                    if (buttonText.equals("Locomotives")) {
                        String[][] locomotiveDetails = displayInduvidualProductsUI.getProducts("Locomotives");
                        displayInduvidualProductsUI.createAndShowGUI(locomotiveDetails);
                    } else if (buttonText.equals("Controllers")) {
                        String[][] controllerDetails = displayInduvidualProductsUI.getProducts("Controllers");
                        displayInduvidualProductsUI.createAndShowGUI(controllerDetails);
                    } else if (buttonText.equals("Track")) {
                        String[][] trackDetails = displayInduvidualProductsUI.getProducts("Track");
                        displayInduvidualProductsUI.createAndShowGUI(trackDetails);
                    } else if (buttonText.equals("Rolling Stock")) {
                        String[][] rollingStockDetails = displayInduvidualProductsUI.getProducts("Rolling Stock");
                        displayInduvidualProductsUI.createAndShowGUI(rollingStockDetails);
                    } else if (buttonText.equals("Track Packs")) {
                        HashMap<String, List<String[]>> trackPackDetails = displayingBoxedProductsUI
                                .getBoxedProducts("Track Packs");
                        displayingBoxedProductsUI.createAndShowGroupedGUI(trackPackDetails);
                    } else if (buttonText.equals("Train Sets")) {
                        HashMap<String, List<String[]>> trainSetDetails = displayingBoxedProductsUI
                                .getBoxedProducts("Train Sets");
                        displayingBoxedProductsUI.createAndShowGroupedGUI(trainSetDetails);
                    }

                });

                cellPanel.add(button, BorderLayout.SOUTH);

                gridPanel.add(cellPanel);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the grid panel to the center of the BorderLayout
        add(gridPanel, BorderLayout.CENTER);
    }

    private Image resizeImage(BufferedImage originalImage, int width, int height) {
        Image resultingImage = originalImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        return resultingImage;
    }

    private String getImageUrl(int index) {
        String[] urls = {
                "https://www.bigjigstoys.co.uk/cdn/shop/products/BJT016-4.jpg?v=1671207335",
                "https://railwaymodels.uk/pimg/hornby-R3945-o.jpg",
                "https://s7d2.scene7.com/is/image/Caterpillar/CM20200325-fd782-a61d2",
                "https://www.pkcgroup.com/media/site-images/train.png",
                "https://i.etsystatic.com/9299240/r/il/7a9e15/818131480/il_fullxfull.818131480_mied.jpg",
                "https://www.keyboardspecialists.co.uk/cdn/shop/products/rd_frontview_709x700.jpg?v=1610537906"
        };

        return urls[index];
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Categories Page");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setSize(1000, 700); // Adjust the size to accommodate title and labels
            window.add(new CustomerUI());
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}
