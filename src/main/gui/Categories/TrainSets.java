package Categories;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.event.*;
import main.db.DatabaseConnectionHandler;
import main.store.Users.*;
// sql
import java.sql.*;
import javax.sql.*;

public class TrainSets extends JPanel {
    public TrainSets() {
        // Main panel layout
        setLayout(new BorderLayout(10, 20)); // horizontal and vertical gaps

        // Title label
        JLabel titleLabel = new JLabel("TRAIN SETS PAGE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Panel for boxes with some padding
        JPanel boxesPanel = new JPanel();
        boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.Y_AXIS));
        boxesPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // top, left, bottom, right padding

        // Create and add boxes
        boxesPanel.add(createBox("Hello"));
        boxesPanel.add(Box.createRigidArea(new Dimension(0, 15))); // space between boxes
        boxesPanel.add(createBox("Hello world"));
        boxesPanel.add(Box.createRigidArea(new Dimension(0, 15))); // space between boxes
        boxesPanel.add(createBox("Hellow World"));

        // Add boxes panel to the center
        add(boxesPanel, BorderLayout.CENTER);
    }

    private JPanel createBox(String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10)); // horizontal and vertical gaps
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // border for the box

        // Label at the top of the box
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 16)); // Set the font for the text
        panel.add(label, BorderLayout.NORTH);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // flow layout with horizontal and vertical gaps
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding inside the button panel

        // Buttons
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton editButton = new JButton("Edit");

        // Add buttons to the button panel
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);

        // Add the button panel to the bottom of the box
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
}