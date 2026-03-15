import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ViewTransactions extends JFrame {
    private final DatabaseConnection db;
    private JTable table;
    private DefaultTableModel model;
    
    public ViewTransactions(DatabaseConnection db) {
        this.db = db;
        initComponents();
        loadTransactions();
    }
    
    private void initComponents() {
        setTitle("View Transactions");
        setLayout(new BorderLayout());
        
        // Create table
        String[] columns = {"Transaction ID", "Date", "Type", "Amount", "Description"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn, BorderLayout.SOUTH);
        
        setSize(600, 400);
        setLocationRelativeTo(null);
    }
    
    private void loadTransactions() {
        model.setRowCount(0);
        List<String[]> transactions = db.getTransactions();
        for (String[] trans : transactions) {
            model.addRow(trans);
        }
    }
}