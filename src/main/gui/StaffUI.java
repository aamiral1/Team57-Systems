package main.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class StaffUI extends JPanel {
    private static final String[] LABEL_TEXTS = {
        "Train Sets", "Track Packs", "Locomotives",
        "Rolling Stock", "Track", "Controllers"
    };
    
    private static final int IMAGE_WIDTH = 300;
    private static final int IMAGE_HEIGHT = 200;

    public StaffUI() {
        // Use BorderLayout for the main panel
        setLayout(new BorderLayout());

        // Title panel with FlowLayout for centering the title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("CATEGORIES PAGE");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        
        // Add the title panel to the top of the BorderLayout
        add(titlePanel, BorderLayout.NORTH);

        // Panel for images and labels with GridLayout
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        try {
            for (int i = 0; i < 6; i++) {
                BufferedImage image = ImageIO.read(new URL(getImageUrl(i)));
                ImageIcon imageIcon = new ImageIcon(resizeImage(image, IMAGE_WIDTH, IMAGE_HEIGHT));
                JLabel imageLabel = new JLabel(imageIcon);
                // Set preferred size for the label to ensure the layout respects the image size
                imageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
                
                // Create a sub-panel with BorderLayout for each image and label
                JPanel cellPanel = new JPanel(new BorderLayout());
                cellPanel.add(imageLabel, BorderLayout.CENTER);
                
                JLabel textLabel = new JLabel(LABEL_TEXTS[i], SwingConstants.CENTER);
                cellPanel.add(textLabel, BorderLayout.SOUTH);
                
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
            window.add(new StaffUI());
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}
