import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;

public class SmartHomeSystem {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smart_home_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color ACCENT_COLOR = new Color(0, 153, 204);
    private static final Color CARD_COLOR = Color.WHITE;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }

    // Database connection utility
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found. Add MySQL Connector/J to your project.");
        }
    }

    // User model class
    public static class User {
        private int userId;
        private String username;
        private String password;
        private String email;
        private String phone;
        private String userType;

        public User(int userId, String username, String password, String email, String phone, String userType) {
            this.userId = userId;
            this.username = username;
            this.password = password;
            this.email = email;
            this.phone = phone;
            this.userType = userType;
        }

        // Getters
        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getUserType() { return userType; }
    }

    // Device model class
    public static class Device {
        private int deviceId;
        private String deviceName;
        private String deviceType;
        private String room;
        private String status;
        private String currentValue;
        private int userId;

        public Device(int deviceId, String deviceName, String deviceType, String room, 
                     String status, String currentValue, int userId) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.deviceType = deviceType;
            this.room = room;
            this.status = status;
            this.currentValue = currentValue;
            this.userId = userId;
        }

        // Getters and setters
        public int getDeviceId() { return deviceId; }
        public String getDeviceName() { return deviceName; }
        public String getDeviceType() { return deviceType; }
        public String getRoom() { return room; }
        public String getStatus() { return status; }
        public String getCurrentValue() { return currentValue; }
        public int getUserId() { return userId; }
        
        public void setStatus(String status) { this.status = status; }
        public void setCurrentValue(String currentValue) { this.currentValue = currentValue; }
    }

    // Modern Login Frame
    public static class LoginFrame extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;

        public LoginFrame() {
            setTitle("Smart Home - Login");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            getContentPane().setBackground(SECONDARY_COLOR);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            mainPanel.setBackground(SECONDARY_COLOR);

            // Header
            JLabel headerLabel = new JLabel("Smart Home Automation", JLabel.CENTER);
            headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            headerLabel.setForeground(PRIMARY_COLOR);
            mainPanel.add(headerLabel, BorderLayout.NORTH);

            // Form panel
            JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            formPanel.setBackground(SECONDARY_COLOR);

            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            usernameField = new JTextField();
            usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            passwordField = new JPasswordField();
            passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            formPanel.add(usernameLabel);
            formPanel.add(usernameField);
            formPanel.add(passwordLabel);
            formPanel.add(passwordField);

            mainPanel.add(formPanel, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBackground(SECONDARY_COLOR);

            JButton loginButton = createStyledButton("Login", PRIMARY_COLOR);
            loginButton.addActionListener(e -> login());

            JButton registerButton = createStyledButton("Register", ACCENT_COLOR);
            registerButton.addActionListener(e -> {
                new RegisterFrame().setVisible(true);
                dispose();
            });

            buttonPanel.add(loginButton);
            buttonPanel.add(registerButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
        }

        private JButton createStyledButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            
            // Add hover effects
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(color.darker());
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(color);
                }
            });
            
            return button;
        }

        private void login() {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = getConnection()) {
                String sql = "SELECT * FROM users WHERE username = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (password.equals(storedPassword)) {
                        User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("user_type")
                        );
                        
                        logActivity(user.getUserId(), "login", "User logged in");
                        
                        new MainFrame(user).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid password", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User not found", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Register Frame
    public static class RegisterFrame extends JFrame {
        private JTextField usernameField, emailField, phoneField;
        private JPasswordField passwordField;
        private JComboBox<String> userTypeCombo;

        public RegisterFrame() {
            setTitle("Smart Home - Register");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            getContentPane().setBackground(SECONDARY_COLOR);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            mainPanel.setBackground(SECONDARY_COLOR);

            JLabel headerLabel = new JLabel("Register New User", JLabel.CENTER);
            headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            headerLabel.setForeground(PRIMARY_COLOR);
            mainPanel.add(headerLabel, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
            formPanel.setBackground(SECONDARY_COLOR);

            JLabel usernameLabel = new JLabel("Username:");
            usernameField = new JTextField();
            usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JLabel passwordLabel = new JLabel("Password:");
            passwordField = new JPasswordField();
            passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JLabel emailLabel = new JLabel("Email:");
            emailField = new JTextField();
            emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JLabel phoneLabel = new JLabel("Phone:");
            phoneField = new JTextField();
            phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JLabel typeLabel = new JLabel("User Type:");
            typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            userTypeCombo = new JComboBox<>(new String[]{"guest", "admin"});
            userTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            formPanel.add(usernameLabel);
            formPanel.add(usernameField);
            formPanel.add(passwordLabel);
            formPanel.add(passwordField);
            formPanel.add(emailLabel);
            formPanel.add(emailField);
            formPanel.add(phoneLabel);
            formPanel.add(phoneField);
            formPanel.add(typeLabel);
            formPanel.add(userTypeCombo);

            mainPanel.add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBackground(SECONDARY_COLOR);

            JButton registerButton = createStyledButton("Register", PRIMARY_COLOR);
            registerButton.addActionListener(e -> registerUser());

            JButton cancelButton = createStyledButton("Cancel", Color.GRAY);
            cancelButton.addActionListener(e -> {
                new LoginFrame().setVisible(true);
                dispose();
            });

            buttonPanel.add(registerButton);
            buttonPanel.add(cancelButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
        }

        private JButton createStyledButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            
            // Add hover effects
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(color.darker());
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(color);
                }
            });
            
            return button;
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

            try (Connection conn = getConnection()) {
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

    // Main Application Frame with modern UI
    public static class MainFrame extends JFrame {
        private User currentUser;
        private JTabbedPane tabbedPane;
        private DevicesPanel devicesPanel;
        private javax.swing.Timer refreshTimer;

        public MainFrame(User user) {
            this.currentUser = user;
            setTitle("Smart Home Automation - Welcome " + user.getUsername());
            setSize(1000, 700);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            getContentPane().setBackground(SECONDARY_COLOR);

            // Create main tabbed pane
            tabbedPane = new JTabbedPane();
            tabbedPane.setBackground(SECONDARY_COLOR);
            tabbedPane.setForeground(PRIMARY_COLOR);
            tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            // Add panels to tabs
            devicesPanel = new DevicesPanel(user);
            tabbedPane.addTab("Devices", devicesPanel);
            tabbedPane.addTab("Automation", new AutomationPanel(user));
            tabbedPane.addTab("Security", new SecurityPanel(user));
            
            if (user.getUserType().equals("admin")) {
                tabbedPane.addTab("Admin", new AdminPanel(user));
            }

            add(tabbedPane, BorderLayout.CENTER);

            // Bottom panel with logout button
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.setBackground(SECONDARY_COLOR);
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JButton logoutButton = createStyledButton("Logout", PRIMARY_COLOR);
            logoutButton.addActionListener(e -> {
                logActivity(user.getUserId(), "logout", "User logged out");
                new LoginFrame().setVisible(true);
                dispose();
                stopRefreshTimer();
            });

            bottomPanel.add(logoutButton);
            add(bottomPanel, BorderLayout.SOUTH);

            // Start refresh timer for real-time updates
            startRefreshTimer();
        }

        private JButton createStyledButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            
            // Add hover effects
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(color.darker());
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(color);
                }
            });
            
            return button;
        }

        private void startRefreshTimer() {
            refreshTimer = new javax.swing.Timer(5000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    devicesPanel.refreshDevices();
                }
            });
            refreshTimer.start();
        }

        private void stopRefreshTimer() {
            if (refreshTimer != null && refreshTimer.isRunning()) {
                refreshTimer.stop();
            }
        }
    }

    // Modern Devices Panel with card layout
    public static class DevicesPanel extends JPanel {
        private User user;
        private JPanel devicesContainer;
        private Map<Integer, DeviceCard> deviceCards;

        public DevicesPanel(User user) {
            this.user = user;
            this.deviceCards = new HashMap<>();
            setLayout(new BorderLayout());
            setBackground(SECONDARY_COLOR);

            // Header panel
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(SECONDARY_COLOR);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel("Your Devices", JLabel.LEFT);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(PRIMARY_COLOR);
            headerPanel.add(titleLabel, BorderLayout.WEST);

            JButton addButton = createStyledButton("+ Add Device", ACCENT_COLOR);
            addButton.addActionListener(e -> showAddDeviceDialog());
            headerPanel.add(addButton, BorderLayout.EAST);

            add(headerPanel, BorderLayout.NORTH);

            // Devices container (scrollable)
            devicesContainer = new JPanel();
            devicesContainer.setLayout(new BoxLayout(devicesContainer, BoxLayout.Y_AXIS));
            devicesContainer.setBackground(SECONDARY_COLOR);
            devicesContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JScrollPane scrollPane = new JScrollPane(devicesContainer);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            add(scrollPane, BorderLayout.CENTER);

            // Load initial devices
            refreshDevices();
        }

        public void refreshDevices() {
            devicesContainer.removeAll();
            deviceCards.clear();

            try (Connection conn = getConnection()) {
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
            panel.setBackground(SECONDARY_COLOR);
            panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

            JLabel label = new JLabel(roomName);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            label.setForeground(PRIMARY_COLOR);
            panel.add(label, BorderLayout.WEST);

            return panel;
        }

        private void showAddDeviceDialog() {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Device", true);
            dialog.setSize(400, 400);
            dialog.setLocationRelativeTo(this);
            dialog.getContentPane().setBackground(SECONDARY_COLOR);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            mainPanel.setBackground(SECONDARY_COLOR);

            JLabel titleLabel = new JLabel("Add New Device", JLabel.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(PRIMARY_COLOR);
            mainPanel.add(titleLabel, BorderLayout.NORTH);

            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBackground(SECONDARY_COLOR);

            // Device Name
            JPanel namePanel = new JPanel(new BorderLayout(5, 5));
            namePanel.setBackground(SECONDARY_COLOR);
            JLabel nameLabel = new JLabel("Device Name:");
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JTextField nameField = new JTextField();
            nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            namePanel.add(nameLabel, BorderLayout.WEST);
            namePanel.add(nameField, BorderLayout.CENTER);
            formPanel.add(namePanel);
            formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // Device Type
            JPanel typePanel = new JPanel(new BorderLayout(5, 5));
            typePanel.setBackground(SECONDARY_COLOR);
            JLabel typeLabel = new JLabel("Device Type:");
            typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Light", "Thermostat", "Security", "Appliance", "Fan"});
            typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            typePanel.add(typeLabel, BorderLayout.WEST);
            typePanel.add(typeCombo, BorderLayout.CENTER);
            formPanel.add(typePanel);
            formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // Room
            JPanel roomPanel = new JPanel(new BorderLayout(5, 5));
            roomPanel.setBackground(SECONDARY_COLOR);
            JLabel roomLabel = new JLabel("Room:");
            roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JTextField roomField = new JTextField();
            roomField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            roomPanel.add(roomLabel, BorderLayout.WEST);
            roomPanel.add(roomField, BorderLayout.CENTER);
            formPanel.add(roomPanel);
            formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // Initial Value (for thermostats)
            JPanel valuePanel = new JPanel(new BorderLayout(5, 5));
            valuePanel.setBackground(SECONDARY_COLOR);
            JLabel valueLabel = new JLabel("Initial Value:");
            valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JTextField valueField = new JTextField("20"); // Default temperature value
            valueField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            valuePanel.add(valueLabel, BorderLayout.WEST);
            valuePanel.add(valueField, BorderLayout.CENTER);
            formPanel.add(valuePanel);

            mainPanel.add(formPanel, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBackground(SECONDARY_COLOR);

            JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
            saveButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                String type = (String) typeCombo.getSelectedItem();
                String room = roomField.getText().trim();
                String value = valueField.getText().trim();

                if (name.isEmpty() || room.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields", 
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

                try (Connection conn = getConnection()) {
                    String sql = "INSERT INTO devices (device_name, device_type, room, status, current_value, user_id) " +
                                "VALUES (?, ?, ?, 'off', ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, name);
                    stmt.setString(2, type.toLowerCase());
                    stmt.setString(3, room);
                    stmt.setString(4, value);
                    stmt.setInt(5, user.getUserId());
                    stmt.executeUpdate();

                    logActivity(user.getUserId(), "device_add", "Added device: " + name);
                    refreshDevices();
                    dialog.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "Error saving device: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton cancelButton = createStyledButton("Cancel", Color.GRAY);
            cancelButton.addActionListener(e -> dialog.dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.pack();
            dialog.setVisible(true);
        }

        private JButton createStyledButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            
            // Add hover effects
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(color.darker());
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(color);
                }
            });
            
            return button;
        }
    }

    // Device Card Component
    public static class DeviceCard extends JPanel {
        private Device device;
        private User user;
        private JLabel statusLabel;

        public DeviceCard(Device device, User user) {
            this.device = device;
            this.user = user;
            setLayout(new BorderLayout(10, 10));
            setBackground(CARD_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

            // Header panel
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(CARD_COLOR);

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
            controlPanel.setBackground(CARD_COLOR);

            if (device.getDeviceType().equals("thermostat")) {
                // Thermostat controls with error handling
                JLabel tempLabel = new JLabel("Temperature: " + device.getCurrentValue() + "°C");
                tempLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                int tempValue;
                try {
                    tempValue = device.getCurrentValue().isEmpty() ? 20 : Integer.parseInt(device.getCurrentValue());
                } catch (NumberFormatException e) {
                    tempValue = 20; // Default temperature if parsing fails
                }

                JSlider tempSlider = new JSlider(10, 30, tempValue);
                tempSlider.setMajorTickSpacing(5);
                tempSlider.setMinorTickSpacing(1);
                tempSlider.setPaintTicks(true);
                tempSlider.setPaintLabels(true);
                tempSlider.setBackground(CARD_COLOR);
                tempSlider.addChangeListener(e -> {
                    int temp = tempSlider.getValue();
                    tempLabel.setText("Temperature: " + temp + "°C");
                    updateDeviceValue(String.valueOf(temp));
                });

                JPanel tempPanel = new JPanel(new BorderLayout());
                tempPanel.setBackground(CARD_COLOR);
                tempPanel.add(tempLabel, BorderLayout.NORTH);
                tempPanel.add(tempSlider, BorderLayout.CENTER);
                add(tempPanel, BorderLayout.SOUTH);
            } else if (device.getDeviceType().equals("fan")) {
                // Fan speed controls
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
                speedPanel.setBackground(CARD_COLOR);
                speedPanel.add(speedLabel);
                speedPanel.add(lowBtn);
                speedPanel.add(mediumBtn);
                speedPanel.add(highBtn);
                add(speedPanel, BorderLayout.SOUTH);
            } else {
                // Standard on/off toggle
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
        }

        private void updateDeviceStatus(String newStatus) {
            try (Connection conn = getConnection()) {
                String sql = "UPDATE devices SET status = ? WHERE device_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, newStatus);
                stmt.setInt(2, device.getDeviceId());
                stmt.executeUpdate();

                device.setStatus(newStatus);
                statusLabel.setText(newStatus.toUpperCase());
                statusLabel.setForeground(newStatus.equals("on") ? Color.GREEN.darker() : Color.RED);

                logActivity(user.getUserId(), "device_status", 
                    "Changed status of " + device.getDeviceName() + " to " + newStatus);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating device status: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateDeviceValue(String newValue) {
            try (Connection conn = getConnection()) {
                String sql = "UPDATE devices SET current_value = ? WHERE device_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, newValue);
                stmt.setInt(2, device.getDeviceId());
                stmt.executeUpdate();

                device.setCurrentValue(newValue);
                logActivity(user.getUserId(), "device_value", 
                    "Changed value of " + device.getDeviceName() + " to " + newValue);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating device value: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Automation Panel
    public static class AutomationPanel extends JPanel {
        public AutomationPanel(User user) {
            setLayout(new BorderLayout());
            setBackground(SECONDARY_COLOR);
            JLabel label = new JLabel("Automation Panel - Coming Soon", JLabel.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 18));
            label.setForeground(PRIMARY_COLOR);
            add(label, BorderLayout.CENTER);
        }
    }

    // Security Panel
    public static class SecurityPanel extends JPanel {
        public SecurityPanel(User user) {
            setLayout(new BorderLayout());
            setBackground(SECONDARY_COLOR);
            JLabel label = new JLabel("Security Panel - Coming Soon", JLabel.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 18));
            label.setForeground(PRIMARY_COLOR);
            add(label, BorderLayout.CENTER);
        }
    }

    // Admin Panel
    public static class AdminPanel extends JPanel {
        public AdminPanel(User user) {
            setLayout(new BorderLayout());
            setBackground(SECONDARY_COLOR);
            JLabel label = new JLabel("Admin Panel - Coming Soon", JLabel.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 18));
            label.setForeground(PRIMARY_COLOR);
            add(label, BorderLayout.CENTER);
        }
    }

    // Utility method to log activities
    private static void logActivity(int userId, String activityType, String description) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO activity_logs (user_id, activity_type, description) " +
                         "VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, activityType);
            stmt.setString(3, description);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}