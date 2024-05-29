package Maps;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import IdGenerator.IdGenerator;

public class JavaMaps {

    private static final String DB_URL = "jdbc:sqlite:employees.db";

    public static void createDatabase() {
        try {
            // Load the SQLite JDBC driver explicitly
            Class.forName("org.sqlite.JDBC");
            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                if (conn != null) {
                    DatabaseMetaData meta = conn.getMetaData();
                    System.out.println("The driver name is " + meta.getDriverName());
                    System.out.println("A new database has been created.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        }
    }

    public static void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS employees (
                 name TEXT PRIMARY KEY,
                 id TEXT NOT NULL,
                 salary INTEGER NOT NULL
                );""";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertEmployee(String name, int salary) {
        String sqlSelect = "SELECT id FROM employees WHERE name = ?";
        String sqlInsert = "INSERT INTO employees(name, id, salary) VALUES(?,?,?)";
        String id = IdGenerator.generateRandomId();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect);
             PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {

            pstmtSelect.setString(1, name);
            ResultSet rs = pstmtSelect.executeQuery();

            if (!rs.next()) {
                pstmtInsert.setString(1, name);
                pstmtInsert.setString(2, id);
                pstmtInsert.setInt(3, salary);
                pstmtInsert.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateDatabase(Map<String, Integer> newEmployees) {
        // Step 1: Retrieve existing employee data
        HashMap<String, EmployeeData> existingEmployees = getEmployeeData();

        // Step 2: Delete all rows in the table
        String sqlDeleteAll = "DELETE FROM employees";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlDeleteAll);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Step 3: Reinsert existing employees
        String sqlInsert = "INSERT INTO employees(name, id, salary) VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
            for (Map.Entry<String, EmployeeData> entry : existingEmployees.entrySet()) {
                String name = entry.getKey();
                EmployeeData data = entry.getValue();
                pstmtInsert.setString(1, name);
                pstmtInsert.setString(2, data.getId());
                pstmtInsert.setInt(3, data.getSalary());
                pstmtInsert.addBatch();
            }
            pstmtInsert.executeBatch();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Step 4: Insert new employees
        for (Map.Entry<String, Integer> entry : newEmployees.entrySet()) {
            insertEmployee(entry.getKey(), entry.getValue());
        }
    }

    public static void deleteEmployee(String name) {
        String sqlDelete = "DELETE FROM employees WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)) {
            pstmtDelete.setString(1, name);
            pstmtDelete.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static HashMap<String, EmployeeData> getEmployeeData() {
        HashMap<String, EmployeeData> employeeData = new HashMap<>();
        String sql = "SELECT name, id, salary FROM employees";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                String id = rs.getString("id");
                int salary = rs.getInt("salary");
                employeeData.put(name, new EmployeeData(id, salary));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return employeeData;
    }

    public static void main(String[] args) {
        createDatabase();
        createTable();

        // Insert initial employees
        insertEmployee("Aragon", 0);
        insertEmployee("Cazou", 0);

        // New employees to be added
        Map<String, Integer> newEmployees = new HashMap<>();
        newEmployees.put("Mathix", 0);
        newEmployees.put("PolTak", 0);

        // Update database with new employees
        updateDatabase(newEmployees);

        // Print all employees
        HashMap<String, EmployeeData> employees = getEmployeeData();
        for (String name : employees.keySet()) {
            EmployeeData data = employees.get(name);
            System.out.println(name + ": " + data.getId() + ", " + data.getSalary());
        }
    }
}
