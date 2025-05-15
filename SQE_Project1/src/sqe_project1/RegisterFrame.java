/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqe_project1;

/**
 *
 * @author Legendary Khan
 */


import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {
    private JTextField usernameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeCombo;

    public RegisterFrame() {
        setTitle("Smart Home - Register");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.SECONDARY_COLOR);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UIUtils.SECONDARY_COLOR);

        // Header
        JLabel headerLabel = new JLabel("Register New User", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(UIUtils.PRIMARY_COLOR);
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(UIUtils.SECONDARY_COLOR);

        // Form components
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        emailField = new JTextField();
        phoneField = new JTextField();
        userTypeCombo = new JComboBox<>(new String[]{"guest", "admin"});

        // Add components to form
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("User Type:"));
        formPanel.add(userTypeCombo);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(UIUtils.SECONDARY_COLOR);

        JButton registerButton = UIUtils.createStyledButton("Register", UIUtils.PRIMARY_COLOR);
        registerButton.addActionListener(e -> registerUser());

        JButton cancelButton = UIUtils.createStyledButton("Cancel", Color.GRAY);
        cancelButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String userType = (String) userTypeCombo.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Check if username exists
            String checkSql = "SELECT username FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert new user
            String insertSql = "INSERT INTO users (username, password, email, phone, user_type) " +
                             "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, email);
            insertStmt.setString(4, phone);
            insertStmt.setString(5, userType);
            insertStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
            new LoginFrame().setVisible(true);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
