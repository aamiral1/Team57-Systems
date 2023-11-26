import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManagerUI extends JFrame {
    private JTable staffTable;
    private JButton removeButton;
    private JTextField promotionField;
    private JButton promoteButton;

    public ManagerUI() {
        initializeComponents();
        setUpLayout();
    }

    private void initializeComponents() {
        staffTable = new JTable(new DefaultTableModel(new Object[]{"Email", "Forename", "Surname"}, 0));
        removeButton = new JButton("Remove Staff");
        promotionField = new JTextField(20);
        promoteButton = new JButton("Promote to Staff");
    }

    private void setUpLayout() {
        setLayout(new BorderLayout());

        // Add the staff table inside a JScrollPane
        add(new JScrollPane(staffTable), BorderLayout.CENTER);

        // Create a panel for the bottom section
        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(new JLabel("Enter email to promote:"));
        southPanel.add(promotionField);
        southPanel.add(promoteButton);
        southPanel.add(removeButton);

        // Add the south panel to the frame
        add(southPanel, BorderLayout.SOUTH);

        // Frame properties
        pack(); // Adjusts window to fit all components
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Manager UI");
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ManagerUI();
            }
        });
    }
}