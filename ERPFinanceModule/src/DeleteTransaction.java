import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DeleteTransaction extends JFrame {
    private final DatabaseConnection db;
    private JTable table;
    private DefaultTableModel model;
    private JButton deleteBtn;
    
    public DeleteTransaction(DatabaseConnection db) {
        this.db = db;
        initComponents();
        loadTransactions();
    }
    
    private void initComponents() {
        setTitle("Delete Transaction");
        setLayout(new BorderLayout());
        
        // Create table
        String[] columns = {"Select", "Transaction ID", "Date", "Type", "Amount", "Description"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
        };
        
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteSelected());
        buttonPanel.add(deleteBtn);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        setSize(700, 400);
        setLocationRelativeTo(null);
    }
    
    private void loadTransactions() {
        model.setRowCount(0);
        List<String[]> transactions = db.getTransactions();
        for (String[] trans : transactions) {
            model.addRow(new Object[]{false, trans[0], trans[1], trans[2], trans[3], trans[4]});
        }
    }
    
    private void deleteSelected() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete selected transactions?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            for (int i = model.getRowCount() - 1; i >= 0; i--) {
                Boolean selected = (Boolean) model.getValueAt(i, 0);
                if (selected) {
                    String transId = (String) model.getValueAt(i, 1);
                    db.deleteTransaction(transId);
                }
            }
            loadTransactions();
            JOptionPane.showMessageDialog(this, "Selected transactions deleted!");
        }
    }
}