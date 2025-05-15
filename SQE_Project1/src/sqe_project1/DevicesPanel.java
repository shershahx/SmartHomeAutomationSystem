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
import java.util.*;

public class DevicesPanel extends JPanel {
    private User user;
    private JPanel devicesContainer;
    private Map<Integer, DeviceCard> deviceCards;

    public DevicesPanel(User user) {
        this.user = user;
        this.deviceCards = new HashMap<>();
        setLayout(new BorderLayout());
        setBackground(UIUtils.SECONDARY_COLOR);
        initComponents();
        refreshDevices();
    }

    private void initComponents() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtils.SECONDARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Your Devices", JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(UIUtils.PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton addButton = UIUtils.createStyledButton("+ Add Device", UIUtils.ACCENT_COLOR);
        addButton.addActionListener(e -> showAddDeviceDialog());
        headerPanel.add(addButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Devices container (scrollable)
        devicesContainer = new JPanel();
        devicesContainer.setLayout(new BoxLayout(devicesContainer, BoxLayout.Y_AXIS));
        devicesContainer.setBackground(UIUtils.SECONDARY_COLOR);
        devicesContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(devicesContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshDevices() {
        devicesContainer.removeAll();
        deviceCards.clear();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM devices WHERE user_id = ? ORDER BY room, device_name";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getUserId());
            ResultSet rs = stmt.executeQuery();

            String currentRoom = null;
            JPanel roomPanel = null;

            while (rs.next()) {
                Device device = new Device(
                    rs.getInt("device_id"),
                    rs.getString("device_name"),
                    rs.getString("device_type"),
                    rs.getString("room"),
                    rs.getString("status"),
                    rs.getString("current_value"),
                    rs.getInt("user_id")
                );

                // Create room header if needed
                if (!device.getRoom().equals(currentRoom)) {
                    currentRoom = device.getRoom();
                    roomPanel = createRoomPanel(currentRoom);
                    devicesContainer.add(roomPanel);
                }

                // Create device card
                DeviceCard card = new DeviceCard(device, user);
                deviceCards.put(device.getDeviceId(), card);
                devicesContainer.add(card);
                devicesContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            devicesContainer.revalidate();
            devicesContainer.repaint();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading devices: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createRoomPanel(String roomName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.SECONDARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JLabel label = new JLabel(roomName);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(UIUtils.PRIMARY_COLOR);
        panel.add(label, BorderLayout.WEST);

        return panel;
    }

    private void showAddDeviceDialog() {
        new AddDeviceDialog(this, user).setVisible(true);
    }
}
