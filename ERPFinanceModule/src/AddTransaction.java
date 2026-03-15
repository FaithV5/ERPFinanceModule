import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class AddTransaction extends JFrame {
    private final DatabaseConnection db;
    private final Dashboard dashboard;
    private JTextField transIdField, dateField, amountField, descField;
    private JComboBox<String> typeCombo;
    
    public AddTransaction(DatabaseConnection db, Dashboard dashboard) {
        this.db = db;
        this.dashboard = dashboard;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Add Transaction");
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Transaction ID
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Transaction ID:"), gbc);
        gbc.gridx = 1;
        transIdField = new JTextField(15);
        transIdField.setText(generateTransactionId());
        add(transIdField, gbc);
        
        // Date
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(15);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
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
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> saveTransaction());
        add(saveBtn, gbc);
        
        gbc.gridx = 1;
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        add(cancelBtn, gbc);
        
        pack();
        setLocationRelativeTo(null);
    }

    private String generateTransactionId() {
        long suffix = Math.abs(System.currentTimeMillis() % 100000000L);
        return String.format("TX%08d", suffix);
    }
    
    private void saveTransaction() {
        try {
            String transId = transIdField.getText().trim();
            String date = dateField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String amountText = amountField.getText().trim();
            String desc = descField.getText().trim();

            if (transId.isEmpty()) {
                transId = generateTransactionId();
                transIdField.setText(transId);
            }

            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Amount is required!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate date before submitting to the database.
            LocalDate.parse(date, DateTimeFormatter.ISO_DATE);

            double amount = Double.parseDouble(amountText);
            if (amount < 0) {
                JOptionPane.showMessageDialog(this, "Amount cannot be negative!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (db.addTransaction(transId, date, type, amount, desc)) {
                JOptionPane.showMessageDialog(this, "Transaction added successfully!");
                dashboard.refreshSummary();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add transaction:\n" + db.getLastErrorMessage(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format!", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount!", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}