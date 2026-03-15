import java.awt.*;
import java.util.List;
import javax.swing.*;

public class UpdateTransaction extends JFrame {
    private final DatabaseConnection db;
    private JComboBox<String> transCombo;
    private JTextField dateField, amountField, descField;
    private JComboBox<String> typeCombo;
    private List<String[]> transactions;
    
    public UpdateTransaction(DatabaseConnection db) {
        this.db = db;
        initComponents();
        loadTransactions();
    }
    
    private void initComponents() {
        setTitle("Update Transaction");
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Transaction selection
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Select Transaction:"), gbc);
        gbc.gridx = 1;
        transCombo = new JComboBox<>();
        transCombo.addActionListener(e -> loadSelectedTransaction());
        add(transCombo, gbc);
        
        // Date
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(15);
        add(dateField, gbc);
        
        // Type
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        add(typeCombo, gbc);
        
        // Amount
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(15);
        add(amountField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descField = new JTextField(15);
        add(descField, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 5;
        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(e -> updateTransaction());
        add(updateBtn, gbc);
        
        gbc.gridx = 1;
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        add(cancelBtn, gbc);
        
        pack();
        setSize(450, 300);
        setMinimumSize(new Dimension(450, 300));
        setLocationRelativeTo(null);
    }
    
    private void loadTransactions() {
        transactions = db.getTransactions();
        for (String[] trans : transactions) {
            transCombo.addItem(trans[0] + " - " + trans[4]); // ID - Description
        }
    }
    
    private void loadSelectedTransaction() {
        int index = transCombo.getSelectedIndex();
        if (index >= 0 && index < transactions.size()) {
            String[] trans = transactions.get(index);
            dateField.setText(trans[1]);
            typeCombo.setSelectedItem(trans[2]);
            amountField.setText(trans[3]);
            descField.setText(trans[4]);
        }
    }
    
    private void updateTransaction() {
        int index = transCombo.getSelectedIndex();
        if (index >= 0) {
            try {
                String transId = transactions.get(index)[0];
                String date = dateField.getText();
                String type = (String) typeCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String desc = descField.getText();
                
                if (db.updateTransaction(transId, date, type, amount, desc)) {
                    JOptionPane.showMessageDialog(this, "Transaction updated successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update transaction!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!");
            }
        }
    }
}