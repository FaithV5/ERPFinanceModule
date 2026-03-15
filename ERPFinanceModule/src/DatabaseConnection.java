import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://db.voyhzfmagcdgcojblesj.supabase.co:5432/postgres?ssl=true&sslmode=require";
    private static final String USER = "postgres";
    private static final String PASSWORD = "ERPFinanceModule";
    
    private Connection connection;
    private String lastErrorMessage = "";
    
    public DatabaseConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            lastErrorMessage = "";
            System.out.println("Connected to Supabase successfully!");
        } catch (ClassNotFoundException | SQLException e) {
            lastErrorMessage = "Database initialization failed: " + e.getMessage();
            System.err.println(lastErrorMessage);
        }
    }
    
    public Connection getConnection() {
        return connection;
    }

    public String getLastErrorMessage() {
        return (lastErrorMessage == null || lastErrorMessage.isBlank()) ? "Unknown database error." : lastErrorMessage;
    }
    
    public boolean validateLogin(String username, String password) {
        if (connection == null) {
            lastErrorMessage = "No database connection. Check DB URL, credentials, network, and JDBC driver.";
            return false;
        }

        try {
            if (connection.isClosed()) {
                lastErrorMessage = "Database connection is closed. Please restart the app.";
                return false;
            }
        } catch (SQLException e) {
            lastErrorMessage = "Unable to verify database connection: " + e.getMessage();
            return false;
        }

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            boolean isValid = rs.next();
            lastErrorMessage = isValid ? "" : "Invalid username or password.";
            return isValid;
        } catch (SQLException e) {
            lastErrorMessage = "Login validation failed [SQLState=" + e.getSQLState() + "]: " + e.getMessage();
            System.err.println(lastErrorMessage);
            return false;
        }
    }

    public String getUserRole(String username) {
        if (connection == null) {
            return "FINANCE";
        }

        String query = "SELECT role FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                if (role != null && !role.isBlank()) {
                    return role.trim().toUpperCase();
                }
            }
        } catch (SQLException e) {
            // Fallback for schemas without a role column yet.
            if ("42703".equals(e.getSQLState())) {
                return "admin".equalsIgnoreCase(username) || "adminfaith".equalsIgnoreCase(username)
                    ? "ADMIN"
                    : "FINANCE";
            }
            System.err.println("Role lookup failed: " + e.getMessage());
        }
        return "FINANCE";
    }
    
    // Create transaction
    public boolean addTransaction(String transId, String date, String type, double amount, String desc) {
        if (connection == null) {
            lastErrorMessage = "No database connection. Check your DB URL/credentials/driver.";
            return false;
        }

        String query = "INSERT INTO transactions (transaction_id, date, type, amount, description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, transId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setString(3, type);
            pstmt.setDouble(4, amount);
            pstmt.setString(5, desc);
            lastErrorMessage = "";
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = "Add transaction failed [SQLState=" + e.getSQLState() + "]: " + e.getMessage();
            System.err.println(lastErrorMessage);
            return false;
        }
    }
    
    // Read transactions
    public List<String[]> getTransactions() {
        List<String[]> transactions = new ArrayList<>();
        String query = "SELECT transaction_id, date, type, amount, description FROM transactions ORDER BY date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String[] trans = new String[5];
                trans[0] = rs.getString("transaction_id");
                trans[1] = rs.getString("date");
                trans[2] = rs.getString("type");
                trans[3] = String.valueOf(rs.getDouble("amount"));
                trans[4] = rs.getString("description");
                transactions.add(trans);
            }
        } catch (SQLException e) {
            System.err.println("Load transactions failed: " + e.getMessage());
        }
        return transactions;
    }
    
    // Update transaction
    public boolean updateTransaction(String transId, String date, String type, double amount, String desc) {
        String query = "UPDATE transactions SET date = ?, type = ?, amount = ?, description = ? WHERE transaction_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, date);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, desc);
            pstmt.setString(5, transId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update transaction failed: " + e.getMessage());
            return false;
        }
    }
    
    // Delete transaction
    public boolean deleteTransaction(String transId) {
        String query = "DELETE FROM transactions WHERE transaction_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, transId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete transaction failed: " + e.getMessage());
            return false;
        }
    }
    
    // Get financial summary
    public double getTotalIncome() {
        String query = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'Income'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Read total income failed: " + e.getMessage());
        }
        return 0;
    }
    
    public double getTotalExpenses() {
        String query = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'Expense'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Read total expenses failed: " + e.getMessage());
        }
        return 0;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Close connection failed: " + e.getMessage());
        }
    }
}