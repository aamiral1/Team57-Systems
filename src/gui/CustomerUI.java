package gui;

import javax.imageio.ImageIO;
import javax.swing.*;

//import db.DatabaseConnectionHandler;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        });
        navBarPanel.add(editDetailsButton);

        // logout button - to be implemented
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            // Log out user and return to welcome screen
            System.out.println("Log out button clicked");
            UserManager.logout();

            // Close the current JFrame that contains this JPanel
            JFrame topLevelFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topLevelFrame != null) {
                topLevelFrame.dispose();
            }

            // Open the welcome screen
            Welcome welcomeScreen = new Welcome();
            welcomeScreen.setVisible(true);
        });
        navBarPanel.add(logoutButton);

        JButton viewCartButton = new JButton("View Cart");
        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Calls the view cart method
                displayInduvidualProductsUI.viewCart();
        
                // Dispose the current window by finding the JFrame ancestor of the button
                JFrame topLevelFrame = (JFrame) SwingUtilities.getWindowAncestor(viewCartButton);
                if (topLevelFrame != null) {
                    topLevelFrame.dispose(); // Closes the current window
                }
            }
        });
        
        navBarPanel.add(viewCartButton);


        String currentUserRole = UserManager.getCurrentUser().getRole();

        JButton staffPortal = new JButton("Staff Portal");

        if (!(currentUserRole.equals("Staff") || currentUserRole.equals("Manager"))) {
         staffPortal.setVisible(false); // This will make the button invisible in the UI
        }
        else {
        staffPortal.addActionListener(e -> {
            if (currentUserRole.equals("Staff") || currentUserRole.equals("Manager")) {
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
            }
            else {
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
                        HashMap<String, List<String[]>> trackPackDetails = displayingBoxedProductsUI.getBoxedProducts("Track Packs");
                        displayingBoxedProductsUI.createAndShowGroupedGUI(trackPackDetails);
                    } else if (buttonText.equals("Train Sets")) {
                        HashMap<String, List<String[]>> trainSetDetails = displayingBoxedProductsUI.getBoxedProducts("Train Sets");
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
