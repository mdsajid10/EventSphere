package Views;

import modals.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class HomePage extends JFrame {

    private JPanel roundedPanel;
    private JPanel currentContentPanel;
    private String userId; // Add this field



    public HomePage() {
        Admin admin = Login.loggedInAdmin;
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        setUndecorated(true);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                Color color1 = new Color(17, 27, 37);
                Color color2 = new Color(63, 102, 139);
                int width = getWidth();
                int height = getHeight();

                GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        headerPanel.setPreferredSize(new Dimension(JFrame.MAXIMIZED_HORIZ, 80));

        JLabel nameLabel = new JLabel("   Welcome "+admin.getName()+" ID "+  admin.getId());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Load circular profile image
        ImageIcon profileIcon;
        File profileImageFile = new File("src/images/eye.png");
        if (profileImageFile.exists()) {
            profileIcon = new ImageIcon("src/images/eye.png");
        } else {
            profileIcon = new ImageIcon(new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB)); // fallback blank image
        }


        headerPanel.add(nameLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Sidebar panel
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                Color color1 = new Color(17, 27, 37);
                Color color2 = new Color(63, 102, 139);
                int width = getWidth();
                int height = getHeight();

                GradientPaint gp = new GradientPaint(0, 0, color1, 0, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        String[] options = {"All Events", "My Events", "Add New", "Profile Settings", "Logout"};

        for (String option : options) {
            JButton btn = new JButton(option);
            btn.setFocusable(false);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            btn.setBorderPainted(false);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            btn.setPreferredSize(new Dimension(200, 50));
            btn.setBackground(new Color(17, 27, 37));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 12));

            // Hover effect
            addHoverEffect(btn, new Color(17, 27, 37), new Color(63, 102, 139));

            btn.addActionListener(e -> switchPanel(option));
            sidebarPanel.add(btn);
        }
        add(sidebarPanel, BorderLayout.WEST);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(1400, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(null);
        add(contentPanel, BorderLayout.EAST);

        // Rounded corner panel (class member)
        roundedPanel = new JPanel();
        roundedPanel.setBackground(Color.WHITE);
        roundedPanel.setBounds(100, 30, 1260, 720);
        roundedPanel.setLayout(null);
        contentPanel.add(roundedPanel); // ← Fixed: Removed BorderLayout.CENTER
        // Show default content when opening
        switchPanel("All Events");  // This will show the AllEvents panel by default

        setVisible(true);
    }

    // Helper method for hover effect
    private void addHoverEffect(JButton button, Color normal, Color hover) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normal);
            }
        });
    }

    private void switchPanel(String option) {
        roundedPanel.removeAll();

        JPanel newPanel;
        switch (option) {
            case "All Events":
                newPanel = new AllEvents(); // Replace with AllEventsPanel();
                break;
            case "My Events":
                newPanel = new MyEvents(); // Replace with MyEventsPanel();
                break;
            case "Add New":
                newPanel = new NewEvent(); // Replace with AddNewPanel();
                break;
            case "Profile Settings":
                newPanel = new ProfileSettings(); // Replace with ProfileSettingsPanel();
                break;
            case "Logout":
                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    new Login();
                }
                return;
            default:
                newPanel = new AllEvents();
        }

        newPanel.setLayout(null);
        newPanel.setBounds(0, 0, 1260, 720);
        newPanel.setBackground(Color.WHITE);
        roundedPanel.add(newPanel);
        currentContentPanel = newPanel;

        roundedPanel.revalidate();
        roundedPanel.repaint();
    }

}
