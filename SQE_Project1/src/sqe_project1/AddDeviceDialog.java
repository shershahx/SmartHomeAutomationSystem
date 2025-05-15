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

public class AddDeviceDialog extends JDialog {
    private DevicesPanel parentPanel;
    private User user;

    public AddDeviceDialog(DevicesPanel parentPanel, User user) {
        super((Frame)SwingUtilities.getWindowAncestor(parentPanel), "Add New Device", true);
        this.parentPanel = parentPanel;
        this.user = user;
        setSize(400, 400);
        setLocationRelativeTo(parentPanel);
        getContentPane().setBackground(UIUtils.SECONDARY_COLOR);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(UIUtils.SECONDARY_COLOR);

        JLabel titleLabel = new JLabel("Add New Device", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIUtils.PRIMARY_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(UIUtils.SECONDARY_COLOR);

        // Device Name
        JTextField nameField = new JTextField();
        addFormField(formPanel, "Device Name:", nameField);

        // Device Type
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Light", "Thermostat", "Security", "Appliance", "Fan"});
        addFormField(formPanel, "Device Type:", typeCombo);

        // Room
        JTextField roomField = new JTextField();
        addFormField(formPanel, "Room:", roomField);

        // Initial Value
        JTextField valueField = new JTextField("20");
        addFormField(formPanel, "Initial Value:", valueField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(UIUtils.SECONDARY_COLOR);

        JButton saveButton = UIUtils.createStyledButton("Save", UIUtils.PRIMARY_COLOR);
        saveButton.addActionListener(e -> saveDevice(
            nameField.getText().trim(),
            (String) typeCombo.getSelectedItem(),
            roomField.getText().trim(),
            valueField.getText().trim()
        ));

        JButton cancelButton = UIUtils.createStyledButton("Cancel", Color.GRAY);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addFormField(JPanel panel, String label, JComponent field) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        fieldPanel.setBackground(UIUtils.SECONDARY_COLOR);
        
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        fieldPanel.add(jLabel, BorderLayout.WEST);
        fieldPanel.add(field, BorderLayout.CENTER);
        
        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void saveDevice(String name, String type, String room, String value) {
        if (name.isEmpty() || room.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set default values based on device type
        if (type.equalsIgnoreCase("thermostat") && value.isEmpty()) {
            value = "20";
        } else if (type.equalsIgnoreCase("fan") && value.isEmpty()) {
            value = "medium";
        } else if (value.isEmpty()) {
            value = "0";
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO devices (device_name, device_type, room, status, current_value, user_id) " +
                        "VALUES (?, ?, ?, 'off', ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, type.toLowerCase());
            stmt.setString(3, room);
            stmt.setString(4, value);
            stmt.setInt(5, user.getUserId());
            stmt.executeUpdate();

            DBConnection.logActivity(user.getUserId(), "device_add", "Added device: " + name);
            parentPanel.refreshDevices();
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving device: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
