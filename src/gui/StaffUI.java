package gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import store.UserManager;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class StaffUI extends JPanel {
    private static final String[] LABEL_TEXTS = {
        "Train Sets", "Track Packs", "Locomotives",
        "Rolling Stock", "Track", "Controllers"
    };

    public StaffUI() {
        setLayout(new BorderLayout());
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createGridPanel(), BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel titleLabel = new JLabel("CATEGORIES PAGE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        JButton viewOrdersButton = new JButton("View Orders");
        viewOrdersButton.setFont(new Font("Arial", Font.BOLD, 12));

        viewOrdersButton.addActionListener(e -> {
            AllOrders.createAndShowGUI();
        });

        JButton managerPortalButton = new JButton("Manager Portal");
        managerPortalButton.setFont(new Font("Arial", Font.BOLD, 12));

        JButton returnToCustomerUIButton = new JButton("Return");
        returnToCustomerUIButton.setFont(new Font("Arial", Font.BOLD, 12));
        returnToCustomerUIButton.addActionListener(e -> returnToCustomerUI());

        String currentUserRole = UserManager.getCurrentUser().getRole();

        if (!(currentUserRole.equals("Manager"))) {
            managerPortalButton.setVisible(false); // This will make the button invisible in the UI
           }
        else {
            managerPortalButton.addActionListener(e -> {
            if (currentUserRole.equals("Manager")) {
                // Create a new frame for the Manager UI
                JFrame managerFrame = new JFrame("Manager Portal");
                managerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                managerFrame.setSize(1000, 700); // Set the size as per your requirement
                managerFrame.setLocationRelativeTo(null); // Center on screen
                managerFrame.add(new ManagerUI()); // Assuming ManagerUI is a JPanel
            
                // Display the new frame
                managerFrame.setVisible(true);
            
                // Get the current frame to close it
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
                currentFrame.dispose(); // This will close the current window
            }
        });

        }
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
    
        // Add the Manager Portal button to the right
        titlePanel.add(viewOrdersButton, BorderLayout.EAST);
        titlePanel.add(managerPortalButton, BorderLayout.EAST);
        titlePanel.add(returnToCustomerUIButton, BorderLayout.WEST);
        // Set a border for spacing if needed
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        return titlePanel;
    }

    private void returnToCustomerUI() {
        // Create a new frame for the Customer UI (assuming you have a CustomerUI class)
        JFrame customerFrame = new JFrame("Customer Page");
        customerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        customerFrame.setSize(1000, 700);
        customerFrame.setLocationRelativeTo(null);
        customerFrame.add(new CustomerUI()); // Replace CustomerUI with your actual customer panel
    
        // Display the new frame
        customerFrame.setVisible(true);
    
        // Get the current frame to close it
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        currentFrame.dispose();
    }

    private JPanel createGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        for (int i = 0; i < LABEL_TEXTS.length; i++) {
            gridPanel.add(createCellPanel(i));
        }
        
        return gridPanel;
    }

    private JPanel createCellPanel(int index) {
        JPanel cellPanel = new JPanel(new BorderLayout());
        try {
            BufferedImage image = ImageIO.read(new URL(getImageUrl(index)));
            ImageIcon imageIcon = new ImageIcon(resizeImage(image, 300, 200));
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setPreferredSize(new Dimension(300, 200));
            cellPanel.add(imageLabel, BorderLayout.CENTER);

            JButton button = new JButton(LABEL_TEXTS[index]);
            styleButton(button);
            button.addActionListener(getCategoryActionListener(LABEL_TEXTS[index]));
            cellPanel.add(button, BorderLayout.SOUTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cellPanel;
    }

    private ActionListener getCategoryActionListener(String category) {
        return e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
            JPanel newPanel;

            switch (category) {
                case "Train Sets":
                    newPanel = new TrainSets(topFrame); // Placeholder for the actual TrainSets panel
                    break;
                case "Track Packs":
                    newPanel = new TrackPacks(topFrame); // Pass the frame to the TrackPacks panel
                    break;
                case "Locomotives":
                    newPanel = new Locomotives(topFrame); // Pass the frame to the Locomotives panel
                    break;
                case "Rolling Stock":
                    newPanel = new RollingStock(topFrame); // Pass the frame to the RollingStock panel
                    break;
                case "Track":
                    newPanel = new Track(topFrame); // Pass the frame to the Track panel
                    break;
                case "Controllers":
                    newPanel = new Controllers(topFrame); // Pass the frame to the Controllers panel
                    break;
                default:
                    newPanel = new JPanel();
            }

            topFrame.setContentPane(newPanel);
            topFrame.revalidate();
            topFrame.repaint();
        };
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(100, 100, 255)); // Similar style to Locomotives Return button
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(300, 30));
    }

    private Image resizeImage(BufferedImage originalImage, int width, int height) {
        return originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    private String getImageUrl(int index) {
        String[] urls = {
            // Add your actual URLs for each category here
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
            window.setSize(1000, 700);
            window.add(new StaffUI());
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}