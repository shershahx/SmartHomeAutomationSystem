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
import javax.swing.Timer;

public class MainFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    private DevicesPanel devicesPanel;
    private Timer refreshTimer;

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("Smart Home Automation - Welcome " + user.getUsername());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.SECONDARY_COLOR);

        initComponents();
        startRefreshTimer();
    }

    private void initComponents() {
        // Create main tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(UIUtils.SECONDARY_COLOR);
        tabbedPane.setForeground(UIUtils.PRIMARY_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Add panels to tabs
        devicesPanel = new DevicesPanel(currentUser);
        tabbedPane.addTab("Devices", devicesPanel);
        tabbedPane.addTab("Automation", new AutomationPanel(currentUser));
        tabbedPane.addTab("Security", new SecurityPanel(currentUser));
        
        if (currentUser.getUserType().equals("admin")) {
            tabbedPane.addTab("Admin", new AdminPanel(currentUser));
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom panel with logout button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(UIUtils.SECONDARY_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton logoutButton = UIUtils.createStyledButton("Logout", UIUtils.PRIMARY_COLOR);
        logoutButton.addActionListener(e -> {
            DBConnection.logActivity(currentUser.getUserId(), "logout", "User logged out");
            new LoginFrame().setVisible(true);
            dispose();
            stopRefreshTimer();
        });

        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void startRefreshTimer() {
        refreshTimer = new Timer(5000, e -> devicesPanel.refreshDevices());
        refreshTimer.start();
    }

    private void stopRefreshTimer() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }
}
