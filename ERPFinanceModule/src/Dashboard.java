import java.awt.*;
import javax.swing.*;

public class Dashboard extends JFrame {
    private final DatabaseConnection db;
    private final String userRole;
    private JTextArea summaryArea;
    
    public Dashboard(DatabaseConnection db, String userRole) {
        this.db = db;
        this.userRole = userRole;
        initComponents();
        updateSummary();
    }
    
    private void initComponents() {
        setTitle("ERP Finance Module - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Finance Module - Role: " + userRole, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Main menu panel
        JPanel menuPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        menuPanel.setBorder(BorderFactory.createTitledBorder("Main Menu"));
        
        JButton addBtn = new JButton("Add Transaction");
        JButton viewBtn = new JButton("View Transactions");
        JButton updateBtn = new JButton("Update Transaction");
        JButton deleteBtn = new JButton("Delete Transaction");
        JButton reportBtn = new JButton("Financial Report");
        JButton exitBtn = new JButton("Exit");
        
        addBtn.addActionListener(e -> new AddTransaction(db, this).setVisible(true));
        viewBtn.addActionListener(e -> new ViewTransactions(db).setVisible(true));
        updateBtn.addActionListener(e -> new UpdateTransaction(db).setVisible(true));
        deleteBtn.addActionListener(e -> new DeleteTransaction(db).setVisible(true));
        reportBtn.addActionListener(e -> showFinancialReport());
        exitBtn.addActionListener(e -> {
            db.closeConnection();
            new Login().setVisible(true);
            dispose();
        });

        if ("FINANCE".equalsIgnoreCase(userRole)) {
            updateBtn.setEnabled(false);
            updateBtn.setToolTipText("Finance users cannot update transactions.");

            deleteBtn.setEnabled(false);
            deleteBtn.setToolTipText("Finance users cannot delete transactions.");
        }
        
        menuPanel.add(addBtn);
        menuPanel.add(viewBtn);
        menuPanel.add(updateBtn);
        menuPanel.add(deleteBtn);
        menuPanel.add(reportBtn);
        menuPanel.add(exitBtn);
        
        add(menuPanel, BorderLayout.CENTER);
        
        // Summary panel
        summaryArea = new JTextArea(5, 30);
        summaryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Financial Summary"));
        add(scrollPane, BorderLayout.SOUTH);
        
        setSize(500, 400);
        setLocationRelativeTo(null);
    }
    
    private void updateSummary() {
        double income = db.getTotalIncome();
        double expenses = db.getTotalExpenses();
        double balance = income - expenses;
        
        summaryArea.setText(String.format(
            "Total Income: PHP %.2f\nTotal Expenses: PHP %.2f\nNet Balance: PHP %.2f",
            income, expenses, balance
        ));
    }
    
    private void showFinancialReport() {
        boolean canPrintReport = "ADMIN".equalsIgnoreCase(userRole);
        new FinancialReport(db, canPrintReport).setVisible(true);
    }
    
    public void refreshSummary() {
        updateSummary();
    }
}