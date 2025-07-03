package Views;
import DBconnection.DBConnection;
import Utilities.GradientPanel;
import modals.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {
    public static Admin loggedInAdmin;
    public Login() {
        setTitle("Login");
        setSize(1000, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(new GradientPanel());
        setLayout(null);

        JLabel titleLabel = new JLabel("Login to continue");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(350, 40, 500, 40);
        add(titleLabel);

        // Rounded panel container
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBounds(300, 100, 400, 300);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        loginPanel.setBackground(new Color(255, 255, 255, 45));

        // --- USER ID ---
        JLabel uidLabel = new JLabel("User ID:");
        uidLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        uidLabel.setForeground(Color.WHITE);

        JTextField uidField = new JTextField();
        uidField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        loginPanel.add(uidLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        loginPanel.add(uidField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- PASSWORD ---
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        passLabel.setForeground(Color.WHITE);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        loginPanel.add(passLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- SUBMIT BUTTON ---
        JButton loginButton = new JButton("Submit");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        loginButton.setBackground(new Color(255, 255, 255, 255));
        loginButton.setForeground(new Color(2, 36, 52));
        loginButton.setFocusable(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));

        loginPanel.add(loginButton);
        add(loginPanel);
        setVisible(true);

        loginButton.addActionListener(e -> {
            String userId = uidField.getText();
            String password = new String(passwordField.getPassword());

            String result = authenticateUser(userId, password);
            if (result.equals("success")) {
                new HomePage();  // Make sure you pass the correct userId
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, result, "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private String authenticateUser(String userId, String password) {
        if (userId.trim().isEmpty()) return "Please enter User ID.";
        if (password.trim().isEmpty()) return "Please enter Password.";

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Admin WHERE id = ?"; // Ensure your table has 'department' column
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Verify password first
                    String correctPassword = rs.getString("password");
                    if (!correctPassword.equals(password)) {
                        return "Incorrect password.";
                    }

                    // If password matches, create Admin object with ALL fields
                    Login.loggedInAdmin = new Admin(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("department") ,
                            rs.getString("contactNo")// Added department
                    );
                    return "success";
                } else {
                    return "User ID doesn't exist.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database connection error.";
        }
    }

}
