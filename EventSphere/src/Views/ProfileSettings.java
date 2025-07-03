package Views;

import DBconnection.DBConnection;
import modals.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProfileSettings extends JPanel {

    private JTextField nameField, emailField, departmentField, contactField;
    private JPasswordField oldPasswordField, newPasswordField, confirmPasswordField;
    private JButton saveButton, changePasswordButton;

    public ProfileSettings() {
        setLayout(null);
        setBackground(new Color(240, 240, 240));
        setBounds(0, 0, 1260, 720);

        Admin admin = Login.loggedInAdmin;

        // Title Label (centered)
        JLabel titleLabel = new JLabel("Profile Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBounds(480, 20, 300, 40);
        add(titleLabel);

        // Profile Information Panel (raised higher)
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(null);
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBounds(300, 80, 650, 230); // Reduced height slightly
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        add(profilePanel);

        // Profile Fields (tighter spacing)
        addLabelAndField(profilePanel, "Name:", nameField = new JTextField(admin.getName()), 20, 20);
        addLabelAndField(profilePanel, "Email:", emailField = new JTextField(admin.getEmail()), 20, 70);
        addLabelAndField(profilePanel, "Department:", departmentField = new JTextField(admin.getDepartment()), 20, 120);
        addLabelAndField(profilePanel, "Contact No:", contactField = new JTextField(admin.getContactNo()), 20, 170);

        // Save Button (moved up)
        saveButton = new JButton("Save Changes");
        styleButton(saveButton, new Color(70, 130, 180));
        saveButton.setBounds(300, 330, 200, 40);
        saveButton.addActionListener(e -> saveChanges(
                nameField.getText(),
                emailField.getText(),
                departmentField.getText(),
                contactField.getText()
        ));
        add(saveButton);

        // Password Panel (moved up)
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(null);
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBounds(300, 390, 650, 230); // Reduced height slightly
        passwordPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        add(passwordPanel);

        // Password Fields (tighter spacing)
        addLabelAndPasswordField(passwordPanel, "Old Password:", oldPasswordField = new JPasswordField(), 20, 20);
        addLabelAndPasswordField(passwordPanel, "New Password:", newPasswordField = new JPasswordField(), 20, 70);
        addLabelAndPasswordField(passwordPanel, "Confirm Password:", confirmPasswordField = new JPasswordField(), 20, 120);

        // Change Password Button (moved up)
        changePasswordButton = new JButton("Update Password");
        styleButton(changePasswordButton, new Color(76, 175, 80));
        changePasswordButton.setBounds(300, 640, 200, 40); // Fits within 720 height
        changePasswordButton.addActionListener(e -> changePassword());
        add(changePasswordButton);
    }

    // For more noticeable changes, adjust both font and field size:
    private void addLabelAndField(JPanel panel, String labelText, JTextField field, int x, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Larger, bold label
        label.setBounds(x, y, 180, 30); // Wider and taller label
        panel.add(label);

        field.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Larger field text
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12) // More padding
        ));
        field.setBounds(x + 190, y, 350, 35); // Wider and taller field
        panel.add(field);
    }

    private void addLabelAndPasswordField(JPanel panel, String labelText, JPasswordField field, int x, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setBounds(x, y, 180, 30);
        panel.add(label);

        field.setFont(new Font("Segoe UI", Font.BOLD, 10));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBounds(x + 190, y, 350, 35);
        panel.add(field);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    private void saveChanges(String name, String email, String department, String contact) {
        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || department.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Admin admin = Login.loggedInAdmin;
            if (admin == null) {
                throw new IllegalStateException("No admin logged in");
            }

            admin.setName(name);
            admin.setEmail(email);
            admin.setDepartment(department);
            admin.setContactNo(contact);

            if (updateAdminInDatabase(admin)) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // In your ProfileSettings class
    private boolean updateAdminInDatabase(Admin admin) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE admin SET name=?, email=?, department=?, contactNo=? WHERE id=?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, admin.getName());
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, admin.getDepartment());
            stmt.setString(4, admin.getContactNo());
            stmt.setInt(5, admin.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
    }

    private void changePassword() {
        String oldPass = new String(oldPasswordField.getPassword()).trim();
        String newPass = new String(newPasswordField.getPassword()).trim();
        String confirmPass = new String(confirmPasswordField.getPassword()).trim();

        Admin admin = Login.loggedInAdmin;

        if (!admin.getPassword().trim().equals(oldPass)) {
            JOptionPane.showMessageDialog(this, "Old password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update password
        admin.setPassword(newPass);
        if (updatePasswordInDatabase(admin)) {
            JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Clear password fields only if successful
            oldPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Error updating password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean updatePasswordInDatabase(Admin admin) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE admin SET password=? WHERE id=?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, admin.getPassword());
            stmt.setInt(2, admin.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating password: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
