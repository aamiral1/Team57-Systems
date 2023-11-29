package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import db.DatabaseConnectionHandler;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagerUI extends JFrame {
    private JTable staffTable;
    private JButton demoteButton;
    private JTextField promotionField;
    private JButton promoteButton;
    private DatabaseConnectionHandler db;

    public ManagerUI() {
        db = new DatabaseConnectionHandler();
        initializeComponents();
        setUpLayout();
        displayStaff();
    }

    private void initializeComponents() {
        staffTable = new JTable(new DefaultTableModel(new Object[]{"Email", "Forename", "Surname"}, 0));
        demoteButton = new JButton("Demote Staff");
        promotionField = new JTextField(20);
        promoteButton = new JButton("Promote Staff");
        
        // Add action listener to the demoteButton
        demoteButton.addActionListener(e -> {
            int selectedRow = staffTable.getSelectedRow();
            if (selectedRow >= 0) { // Make sure a row is selected
                String emailToDemote = staffTable.getValueAt(selectedRow, 0).toString(); // Assuming email is in the first column
                demoteStaff(emailToDemote);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a staff member to demote.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        // Add action listener to the promoteButton
        promoteButton.addActionListener(e -> {
            String emailToPromote = promotionField.getText().trim();
            if (!emailToPromote.isEmpty()) {
                promoteStaff(emailToPromote);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter an email to promote.",
                                              "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void setUpLayout() {

        setLayout(new BorderLayout());

        // Add the staff table inside a JScrollPane
        add(new JScrollPane(staffTable), BorderLayout.CENTER);

        JButton returnToStaffUIButton = new JButton("Return");
        returnToStaffUIButton.addActionListener(e -> {
            dispose();
        });

        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPanel.add(returnToStaffUIButton);

        // Create a panel for the bottom section
        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(new JLabel("Enter email to promote:"));
        southPanel.add(promotionField);
        southPanel.add(promoteButton);
        southPanel.add(demoteButton);

        // Add the south panel to the frame
        add(southPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        // Frame properties
        pack(); // Adjusts window to fit all components
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Manager UI");
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }
    

    private void displayStaff() {
        String sql = "SELECT email, name FROM User WHERE role = 'Moderator'";
        try {
            db.openConnection();
            try (PreparedStatement pstmt = db.con.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                DefaultTableModel model = (DefaultTableModel) staffTable.getModel();
                model.setRowCount(0); // Clear existing data

                while (rs.next()) {
                    String email = rs.getString("email");
                    String fullName = rs.getString("name");
                    String[] names = splitName(fullName);

                    model.addRow(new Object[]{email, names[0], names[1]});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error accessing the database: " + e.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            db.closeConnection();
        }
    }

    private void promoteStaff(String email) {
        String sql = "UPDATE User SET role = 'Moderator' WHERE email = ?";
        try {
            db.openConnection();
            try (PreparedStatement pstmt = db.con.prepareStatement(sql)) {
                pstmt.setString(1, email);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Promotion successful for: " + email);
                    displayStaff(); // Refresh the table view to reflect the change
                } else {
                    System.out.println("No changes made. It's possible the email does not exist or is already a Moderator.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error while updating the database: " + ex.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            db.closeConnection();
        }
    }

    private void demoteStaff(String email) {
        String sql = "UPDATE User SET role = 'User' WHERE role = 'Moderator' AND email = ?";
        try {
            db.openConnection();
            try (PreparedStatement pstmt = db.con.prepareStatement(sql)) {
                pstmt.setString(1, email);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Demotion successful for: " + email);
                    displayStaff(); // Refresh the table view to reflect the change
                } else {
                    System.out.println("No changes made. It's possible the email does not exist or is not a Moderator.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error while updating the database: " + ex.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            db.closeConnection();
        }
    }

    private String[] splitName(String fullName) {
        String[] nameParts = fullName.split(" ");
        String forename = nameParts[0]; // First name
        String surname = (nameParts.length > 1) ? nameParts[nameParts.length - 1] : ""; // Last name
        return new String[]{forename, surname};
    }

    private void returnToStaffUI(){



    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManagerUI());
    }

}

