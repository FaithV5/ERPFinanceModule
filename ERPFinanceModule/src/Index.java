import javax.swing.*;

public class Index {
    public static void main(String[] args) {
        // Set Look and Feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.err.println("Could not set system look and feel: " + ex.getMessage());
        }
        
        // Start the application
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
        });
    }
}
