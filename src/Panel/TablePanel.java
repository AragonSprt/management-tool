package Panel;

import Maps.EmployeeData;
import Maps.JavaMaps;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class TablePanel {
 
    public static void main(String[] args) {
        JFrame window = new JFrame("Table of Employees");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(600, 400);
        window.setLocationRelativeTo(null);
        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Cyril_\\Documents\\dev\\Java\\Java maps\\src\\Panel\\Games Projects.png");
        window.setIconImage(icon);

        // Create the panel for displaying employees
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Create the table model and table
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Name", "ID", "Salary"}, 0);
        JTable table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Create a panel for managing employees
        JPanel managePanel = new JPanel();
        managePanel.setLayout(new FlowLayout());

        // Text fields and buttons for deleting and creating employees
        JTextField nameField = new JTextField(20);
        JButton deleteButton = new JButton("Delete Employee");
        JButton createButton = new JButton("Create Employee");

        managePanel.add(new JLabel("Employee Name:"));
        managePanel.add(nameField);
        managePanel.add(deleteButton);
        managePanel.add(createButton);

        panel.add(managePanel, BorderLayout.SOUTH);

        // Load and display employee data
        loadEmployeeData(tableModel);

        // Add action listener for delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                if (!name.isEmpty()) {
                    JavaMaps.deleteEmployee(name);
                    loadEmployeeData(tableModel);
                    nameField.setText("");
                }
            }
        });

        // Add action listener for create button
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                if (!name.isEmpty()) {
                    JavaMaps.insertEmployee(name, 0);
                    loadEmployeeData(tableModel);
                    nameField.setText("");
                }
            }
        });

        window.add(panel);
        window.setVisible(true);
    }

    private static void loadEmployeeData(DefaultTableModel tableModel) {
        // Clear existing data
        tableModel.setRowCount(0);

        HashMap<String, EmployeeData> employees = JavaMaps.getEmployeeData();
        for (String name : employees.keySet()) {
            EmployeeData data = employees.get(name);
            tableModel.addRow(new Object[]{name, data.getId(), data.getSalary()});
        }
    }
}
