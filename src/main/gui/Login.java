import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public Login() {
        // Create components
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("LOGIN");
        
        // Layout setup
        setLayout(new GridLayout(3, 1));

        // Adding Username Panel
        JPanel usernamePanel = new JPanel();
        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(usernameField);
        add(usernamePanel);

        // Adding Password Panel
        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(passwordField);
        add(passwordPanel);

        // Adding Login Button and Remember Me Checkbox
        JPanel loginPanel = new JPanel();
        
        loginPanel.add(loginButton);
        add(loginPanel);

        // Action Listener for login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logic to handle login
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                // For demo purposes, we just print the credentials
                System.out.println("Username: " + username);
                System.out.println("Password: " + new String(password));
            }
        });

        // Final touches on the JFrame
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        // Run the application
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Login();
            }
        });
    }
}

