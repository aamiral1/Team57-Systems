import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomerUI {

    public static void main(String[] args) {

        loadHomePage();

    }

    // Method that loads the home page for the customer

    public static void loadHomePage() {

        JFrame frame = new JFrame("CustomerUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        // Creating navigation bar
        JPanel navbarPanel = new JPanel();
        navbarPanel.setBackground(Color.BLACK);
        navbarPanel.setPreferredSize(new Dimension(frame.getWidth(), 50));

        // Creating new buttons and adding to the navigation bar
        JButton button1 = new JButton("Browse Items");
        JButton button2 = new JButton("View Recent Orders");
        JButton button3 = new JButton("Edit Details");
        JButton button4 = new JButton("Log Out");

        navbarPanel.add(button1);
        navbarPanel.add(button2);
        navbarPanel.add(button3);
        navbarPanel.add(button4);

        // Creating a product panel with grid layout
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new GridLayout(0, 3));

        // Iterating over the grid and adding labels product1 etc - to be replaced by products from the database
        for (int i = 1; i <= 9; i++) {
            final int productId = i; // Product ID to identify the product
            JLabel productLabel = new JLabel("Product " + i);
            productLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showProductDetailsPage(frame, productId);
                }
            });
            productPanel.add(productLabel);
        }

        // Adding the navigation panel and product panel to the frame
        frame.add(navbarPanel, BorderLayout.NORTH);
        frame.add(productPanel, BorderLayout.CENTER);

        frame.setVisible(true);

    }



    // A method that creates and shows product details for when a product is clicked on

    private static void showProductDetailsPage(JFrame frame, int productId) {
        frame.getContentPane().removeAll(); // Clear the existing content

        // Creating a product details panel
        JPanel productDetailsPanel = new JPanel();
        productDetailsPanel.setLayout(new FlowLayout());

        JLabel productDetailsLabel = new JLabel("Product Details - " + productId);
        JButton addToCartButton = new JButton("Add to Cart");
        JButton viewCartButton = new JButton("View Cart");

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add logic to add the item to the cart
                JOptionPane.showMessageDialog(frame, "Product " + productId + " added to cart");
            }
        });

        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add logic to view the cart
                JOptionPane.showMessageDialog(frame, "View Cart button clicked");
            }
        });

        productDetailsPanel.add(productDetailsLabel);
        productDetailsPanel.add(addToCartButton);
        productDetailsPanel.add(viewCartButton);

        // Adding the product details panel to the frame
        frame.add(productDetailsPanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }
}

