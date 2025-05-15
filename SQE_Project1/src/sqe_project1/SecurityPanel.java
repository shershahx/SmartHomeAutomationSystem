/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqe_project1;

/**
 *
 * @author Legendary Khan
 */

import java.awt.*;
import javax.swing.*;

public class SecurityPanel extends JPanel {
    public SecurityPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UIUtils.SECONDARY_COLOR);
        JLabel label = new JLabel("Security Panel - Coming Soon", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(UIUtils.PRIMARY_COLOR);
        add(label, BorderLayout.CENTER);
    }
}
