import java.awt.*;
import java.awt.print.PrinterException;
import java.util.List;
import javax.swing.*;

public class FinancialReport extends JFrame {
    private final DatabaseConnection db;
    private final boolean canPrintReport;
    private JTextArea reportArea;
    
    public FinancialReport(DatabaseConnection db, boolean canPrintReport) {
        this.db = db;
        this.canPrintReport = canPrintReport;
        initComponents();
        generateReport();
    }
    
    private void initComponents() {
        setTitle("Financial Report");
        setLayout(new BorderLayout());
        
        reportArea = new JTextArea(20, 40);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printBtn = new JButton("Print Report");
        printBtn.addActionListener(e -> printReport());
        printBtn.setEnabled(canPrintReport);
        if (!canPrintReport) {
            printBtn.setToolTipText("Only admin users can print reports.");
        }
        buttonPanel.add(printBtn);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);
        
        setSize(500, 400);
        setLocationRelativeTo(null);
    }
    
    private void generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(50)).append("\n");
        report.append("FINANCIAL REPORT\n");
        report.append("Generated: ").append(new java.util.Date()).append("\n");
        report.append("=".repeat(50)).append("\n\n");
        
        double totalIncome = db.getTotalIncome();
        double totalExpenses = db.getTotalExpenses();
        double balance = totalIncome - totalExpenses;
        
        report.append("SUMMARY:\n");
        report.append(String.format("Total Income: PHP %,.2f\n", totalIncome));
        report.append(String.format("Total Expenses: PHP %,.2f\n", totalExpenses));
        report.append(String.format("Net Balance: PHP %,.2f\n", balance));
        report.append("\n");
        
        report.append("-".repeat(50)).append("\n");
        report.append("TRANSACTION DETAILS:\n");
        report.append("-".repeat(50)).append("\n");
        
        List<String[]> transactions = db.getTransactions();
        for (String[] trans : transactions) {
            report.append(String.format("ID: %s\n", trans[0]));
            report.append(String.format("Date: %s\n", trans[1]));
            report.append(String.format("Type: %s\n", trans[2]));
            double amount = Double.parseDouble(trans[3]);
            report.append(String.format("Amount: PHP %,.2f\n", amount));
            report.append(String.format("Description: %s\n", trans[4]));
            report.append("-".repeat(30)).append("\n");
        }
        
        reportArea.setText(report.toString());
    }
    
    private void printReport() {
        try {
            reportArea.print();
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Error printing report!");
        }
    }
}