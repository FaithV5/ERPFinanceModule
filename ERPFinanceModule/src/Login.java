import java.awt.*;
import javax.swing.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final DatabaseConnection db;
    
    public Login() {
        db = new DatabaseConnection();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("ERP Finance Module - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Create panel for login form
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Login"));
        
        // Username label
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel("Username:"), gbc);
        
        // Username field
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);
        
        // Password label
        gbc.gridx = 0; 
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        
        // Password field
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);
        
        // Login button
        gbc.gridx = 0; 
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> performLogin());
        panel.add(loginButton, gbc);
        
        add(panel);
        
        setSize(300, 200);
        setLocationRelativeTo(null);
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (db.validateLogin(username, password)) {
            String role = db.getUserRole(username);
            JOptionPane.showMessageDialog(this, "Login Successful!");
            new Dashboard(db, role).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, db.getLastErrorMessage(), 
                                        "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}