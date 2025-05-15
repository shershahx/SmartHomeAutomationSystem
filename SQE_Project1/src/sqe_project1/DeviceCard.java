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
import java.awt.event.*;
import javax.swing.event.*;
import java.sql.*;

public class DeviceCard extends JPanel {
    private Device device;
    private User user;
    private JLabel statusLabel;

    public DeviceCard(Device device, User user) {
        this.device = device;
        this.user = user;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.CARD_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtils.CARD_COLOR);

        JLabel nameLabel = new JLabel(device.getDeviceName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(nameLabel, BorderLayout.WEST);

        statusLabel = new JLabel(device.getStatus().equals("on") ? "ON" : "OFF");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(device.getStatus().equals("on") ? Color.GREEN.darker() : Color.RED);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Room label
        JLabel roomLabel = new JLabel(device.getRoom());
        roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roomLabel.setForeground(Color.GRAY);
        add(roomLabel, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(UIUtils.CARD_COLOR);

        if (device.getDeviceType().equals("thermostat")) {
            initThermostatControls();
        } else if (device.getDeviceType().equals("fan")) {
            initFanControls();
        } else {
            initStandardControls();
        }
    }

    private void initThermostatControls() {
        JLabel tempLabel = new JLabel("Temperature: " + device.getCurrentValue() + "°C");
        tempLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        int tempValue;
        try {
            tempValue = device.getCurrentValue().isEmpty() ? 20 : Integer.parseInt(device.getCurrentValue());
        } catch (NumberFormatException e) {
            tempValue = 20;
        }

        JSlider tempSlider = new JSlider(10, 30, tempValue);
        tempSlider.setMajorTickSpacing(5);
        tempSlider.setMinorTickSpacing(1);
        tempSlider.setPaintTicks(true);
        tempSlider.setPaintLabels(true);
        tempSlider.setBackground(UIUtils.CARD_COLOR);
        tempSlider.addChangeListener(e -> {
            int temp = tempSlider.getValue();
            tempLabel.setText("Temperature: " + temp + "°C");
            updateDeviceValue(String.valueOf(temp));
        });

        JPanel tempPanel = new JPanel(new BorderLayout());
        tempPanel.setBackground(UIUtils.CARD_COLOR);
        tempPanel.add(tempLabel, BorderLayout.NORTH);
        tempPanel.add(tempSlider, BorderLayout.CENTER);
        add(tempPanel, BorderLayout.SOUTH);
    }

    private void initFanControls() {
        JLabel speedLabel = new JLabel("Fan Speed:");
        speedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        ButtonGroup speedGroup = new ButtonGroup();
        JRadioButton lowBtn = new JRadioButton("Low");
        JRadioButton mediumBtn = new JRadioButton("Medium");
        JRadioButton highBtn = new JRadioButton("High");
        
        String currentSpeed = device.getCurrentValue().isEmpty() ? "medium" : device.getCurrentValue();
        switch (currentSpeed.toLowerCase()) {
            case "low": lowBtn.setSelected(true); break;
            case "medium": mediumBtn.setSelected(true); break;
            case "high": highBtn.setSelected(true); break;
            default: mediumBtn.setSelected(true);
        }

        speedGroup.add(lowBtn);
        speedGroup.add(mediumBtn);
        speedGroup.add(highBtn);

        lowBtn.addActionListener(e -> updateDeviceValue("low"));
        mediumBtn.addActionListener(e -> updateDeviceValue("medium"));
        highBtn.addActionListener(e -> updateDeviceValue("high"));

        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        speedPanel.setBackground(UIUtils.CARD_COLOR);
        speedPanel.add(speedLabel);
        speedPanel.add(lowBtn);
        speedPanel.add(mediumBtn);
        speedPanel.add(highBtn);
        add(speedPanel, BorderLayout.SOUTH);
    }

    private void initStandardControls() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(UIUtils.CARD_COLOR);

        JToggleButton toggleBtn = new JToggleButton(device.getStatus().equals("on") ? "ON" : "OFF");
        toggleBtn.setSelected(device.getStatus().equals("on"));
        toggleBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        toggleBtn.setBackground(toggleBtn.isSelected() ? Color.GREEN.darker() : Color.RED);
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFocusPainted(false);
        toggleBtn.addActionListener(e -> {
            String newStatus = toggleBtn.isSelected() ? "on" : "off";
            toggleBtn.setText(newStatus.toUpperCase());
            toggleBtn.setBackground(newStatus.equals("on") ? Color.GREEN.darker() : Color.RED);
            updateDeviceStatus(newStatus);
        });

        controlPanel.add(toggleBtn);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void updateDeviceStatus(String newStatus) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE devices SET status = ? WHERE device_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setInt(2, device.getDeviceId());
            stmt.executeUpdate();

            device.setStatus(newStatus);
            statusLabel.setText(newStatus.toUpperCase());
            statusLabel.setForeground(newStatus.equals("on") ? Color.GREEN.darker() : Color.RED);

            DBConnection.logActivity(user.getUserId(), "device_status", 
                "Changed status of " + device.getDeviceName() + " to " + newStatus);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating device status: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDeviceValue(String newValue) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE devices SET current_value = ? WHERE device_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newValue);
            stmt.setInt(2, device.getDeviceId());
            stmt.executeUpdate();

            device.setCurrentValue(newValue);
            DBConnection.logActivity(user.getUserId(), "device_value", 
                "Changed value of " + device.getDeviceName() + " to " + newValue);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating device value: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
