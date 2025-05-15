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

public class AdminPanel extends JPanel {
    public AdminPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UIUtils.SECONDARY_COLOR);
        JLabel label = new JLabel("Admin Panel - Coming Soon", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(UIUtils.PRIMARY_COLOR);
        add(label, BorderLayout.CENTER);
    }
}
