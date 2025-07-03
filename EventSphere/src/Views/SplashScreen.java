package Views;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D; // Needed for rounded corners

public class SplashScreen extends JFrame {

    JProgressBar progressBar;

    public SplashScreen() {
        // Frame setup
        setSize(1000, 500);
        setLayout(null);
        setUndecorated(true);  // No title bar
        setLocationRelativeTo(null);  // Center screen
        getContentPane().setBackground(Color.WHITE);

        // 🔘 Rounded corners
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15)); // radius = 50

        // Splash image
        JLabel imageLabel = new JLabel(new ImageIcon("src/images/ss.png")); // <-- Your image file
        imageLabel.setBounds(0, 0, 1000, 500);
        add(imageLabel);

        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setBounds(10, 470, 980, 10);
        progressBar.setForeground(new Color(2, 36, 52));  // Blue color
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorderPainted(false);
        progressBar.setStringPainted(true);
        add(progressBar);

        // Show and start loading
        setVisible(true);
        fillProgressBar();
    }

    private void fillProgressBar() {
        int i = 0;
        while (i <= 100) {
            try {
                Thread.sleep(10);  // Speed of progress
                progressBar.setValue(i);
                progressBar.setStringPainted(false);
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Close splash and open main app
        dispose();
        new Login();
    }
    public static void main(String[] args) {
        new SplashScreen();
    }
}
