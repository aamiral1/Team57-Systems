package main.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class StaffUI extends JPanel {

    public StaffUI() {
        // Set the layout to have 2 rows and 3 columns
        setLayout(new GridLayout(2, 3));
        try {
            BufferedImage image1 = ImageIO.read(new URL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRbRxxurWWcghKIQHZ16lnBbm8RnTkEFrjJElOeR84Y0nMPzN6trzLKjMqZnfiKcPpNF4A&usqp=CAU"));
            ImageIcon imageIcon = new ImageIcon(image1);

            BufferedImage image2 = ImageIO.read(new URL("https://www.jacksonsmodels.co.uk/img/product/hornby-track-for-model-railways-oo-gauge-r8224-track-extension-pack-d-15017494-600.jpg"));
            ImageIcon imageIcon2 = new ImageIcon(image2);

            // Create six labels with the same image
            JLabel label1 = new JLabel(imageIcon);
            JLabel label2 = new JLabel(imageIcon2);
            JLabel label3 = new JLabel(imageIcon);
            JLabel label4 = new JLabel(imageIcon);
            JLabel label5 = new JLabel(imageIcon);
            JLabel label6 = new JLabel(imageIcon);

            // Add the labels to the panel
            this.add(label1);
            this.add(label2);
            this.add(label3);
            this.add(label4);
            this.add(label5);
            this.add(label6);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame window = new JFrame("Display Image");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setSize(1000, 1000);
                window.add(new StaffUI());
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            }
        });
    }
}
