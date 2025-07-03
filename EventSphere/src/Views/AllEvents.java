package Views;

import DBconnection.DBConnection;
import modals.Event;
import modals.Admin;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

public class AllEvents extends JPanel {
    private static final Color CARD_BG_COLOR = new Color(240, 240, 240);
    private static final Color BORDER_COLOR = new Color(63, 102, 139);
    private static final Color BUTTON_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(147, 228, 88);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    public AllEvents() {
        setLayout(null); // Using null layout for absolute positioning
        setBackground(Color.WHITE);
        setBounds(0, 0, 1260, 720);
       // setBorder(BorderFactory.createLineBorder(Color.CYAN, 12));

        // Header Panel with statistics
        JPanel headerPanel = createHeaderPanel();
        headerPanel.setBounds(30, 20, 1200, 150);
        add(headerPanel);

        // Create scroll pane for events
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(30, 190, 1200, 500); // Position below header
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Content panel inside scroll pane
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        scrollPane.setViewportView(contentPanel);
        add(scrollPane);

        try {
            List<Event> events = getAllEventsFromDatabase();
            if (events.isEmpty()) {
                contentPanel.add(createNoEventsPanel());
            } else {
                for (Event event : events) {
                    contentPanel.add(createEventCard(event));
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                }
            }
        } catch (SQLException e) {
            handleDatabaseError(e, contentPanel);
        }
    }

    private List<Event> getAllEventsFromDatabase() throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM Events ORDER BY StartDate, StartTime";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("EventID"),
                        rs.getString("EventName"),
                        rs.getString("EventDescription"),
                        rs.getDate("StartDate").toLocalDate(),
                        rs.getDate("EndDate").toLocalDate(),
                        rs.getTime("StartTime"),
                        rs.getTime("EndTime"),
                        rs.getString("EventVenue"),
                        rs.getInt("HostID")
                );
                events.add(event);
            }
        }
        return events;
    }

    private JPanel createEventCard(Event event) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(1150, Integer.MAX_VALUE));

        // Event Name
        JLabel nameLabel = new JLabel(event.getTitle());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        nameLabel.setForeground(new Color(44, 62, 80));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Event Venue
        JLabel venueLabel = new JLabel("📍 " + event.getVenue());
        venueLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        venueLabel.setForeground(new Color(127, 140, 141));
        venueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Date and Time
        String dateTimeText = String.format("📅 %s to %s  ⏰ %s to %s",
                DATE_FORMAT.format(Date.valueOf(event.getStartDate())),
                DATE_FORMAT.format(Date.valueOf(event.getEndDate())),
                TIME_FORMAT.format(event.getStartTime()),
                TIME_FORMAT.format(event.getEndTime())
        );

        JLabel dateTimeLabel = new JLabel(dateTimeText);
        dateTimeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dateTimeLabel.setForeground(new Color(127, 140, 141));
        dateTimeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Description
        JTextArea descArea = new JTextArea(event.getDescription());
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descArea.setForeground(new Color(44, 62, 80));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(CARD_BG_COLOR);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(CARD_BG_COLOR);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton knowMoreBtn = new JButton("Know More");
        knowMoreBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        knowMoreBtn.setBackground(BUTTON_COLOR);
        knowMoreBtn.setForeground(Color.WHITE);
        knowMoreBtn.setFocusPainted(false);
        knowMoreBtn.addActionListener(e -> showEventDetails(event));
        buttonPanel.add(knowMoreBtn);

        // Add components to card
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(venueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(dateTimeLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(descArea);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(buttonPanel);

        return card;
    }

    private void showEventDetails(Event event) {
        try {
            // Create a new dialog
            JDialog detailsDialog = new JDialog();
            detailsDialog.setTitle("Event Details: " + event.getTitle());
            detailsDialog.setLayout(new BorderLayout());
            detailsDialog.setSize(850, 600); // Slightly more compact
            detailsDialog.setLocationRelativeTo(this);

            // Main content panel
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            mainPanel.setBackground(Color.WHITE);

            // Back button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setBackground(Color.WHITE);

            JButton backButton = new JButton("Back to Events");
            backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            backButton.setBackground(BUTTON_COLOR);
            backButton.setForeground(Color.WHITE);
            backButton.addActionListener(e -> detailsDialog.dispose());
            buttonPanel.add(backButton);

            // Event Details Card - single panel version
            JPanel eventCard = new JPanel();
            eventCard.setLayout(new BoxLayout(eventCard, BoxLayout.Y_AXIS));
            eventCard.setBackground(CARD_BG_COLOR);
            eventCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            eventCard.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));

            // Event Title
            JLabel titleLabel = new JLabel(event.getTitle());
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
            titleLabel.setForeground(new Color(44, 62, 80));
            eventCard.add(titleLabel);
            eventCard.add(Box.createRigidArea(new Dimension(0, 15)));

            // Details Grid (3 columns for better space utilization)
            JPanel detailsGrid = new JPanel(new GridLayout(0, 3, 10, 15));
            detailsGrid.setBackground(CARD_BG_COLOR);
            detailsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Venue
            addDetailCell(detailsGrid, "📍 Venue", event.getVenue());

            // Date Range
            String dateRange = DATE_FORMAT.format(Date.valueOf(event.getStartDate())) +
                    " to " + DATE_FORMAT.format(Date.valueOf(event.getEndDate()));
            addDetailCell(detailsGrid, "📅 Date", dateRange);

            // Time Range
            String timeRange = TIME_FORMAT.format(event.getStartTime()) +
                    " to " + TIME_FORMAT.format(event.getEndTime());
            addDetailCell(detailsGrid, "⏰ Time", timeRange);

            eventCard.add(detailsGrid);
            eventCard.add(Box.createRigidArea(new Dimension(0, 20)));

            // Description
            JTextArea descArea = new JTextArea(event.getDescription());
            descArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
            descArea.setForeground(new Color(44, 62, 80));
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setEditable(false);
            descArea.setBackground(CARD_BG_COLOR);
            JScrollPane descScroll = new JScrollPane(descArea);
            descScroll.setBorder(BorderFactory.createTitledBorder("Description"));
            descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            eventCard.add(descScroll);

            // Host Details Section (added directly to the same card)
            Admin host = getHostDetails(event.getHostId());
            if (host != null) {
                eventCard.add(Box.createRigidArea(new Dimension(0, 25)));

                // Host title
                JLabel hostTitle = new JLabel("Host Details");
                hostTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
                hostTitle.setForeground(new Color(44, 62, 80));
                hostTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
                eventCard.add(hostTitle);
                eventCard.add(Box.createRigidArea(new Dimension(0, 10)));

                // Host details grid (3 columns)
                JPanel hostGrid = new JPanel(new GridLayout(0, 3, 10, 15));
                hostGrid.setBackground(CARD_BG_COLOR);
                hostGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

                addDetailCell(hostGrid, "👤 Name", host.getName());
                addDetailCell(hostGrid, "🏛️ Department", host.getDepartment());
                addDetailCell(hostGrid, "📧 Email", host.getEmail());

                eventCard.add(hostGrid);
            }

            // Add to main panel
            mainPanel.add(eventCard);

            // Add to dialog
            detailsDialog.add(buttonPanel, BorderLayout.NORTH);
            detailsDialog.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
            detailsDialog.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading host details: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDetailCell(JPanel panel, String label, String value) {
        JPanel cell = new JPanel();
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.setBackground(CARD_BG_COLOR);
        cell.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(new Color(63, 102, 139));
        cell.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.PLAIN, 14));
        val.setForeground(new Color(44, 62, 80));
        cell.add(val);

        panel.add(cell);
    }

    private Admin getHostDetails(int hostId) throws SQLException {
        String query = "SELECT * FROM Admin WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, hostId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("department"),
                            rs.getString("contactNo")
                    );
                }
            }
        }
        return null;
    }

    private JPanel createDetailSection(String title, String... details) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1),
                        title
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setMaximumSize(new Dimension(1180, Integer.MAX_VALUE));

        for (String detail : details) {
            JLabel label = new JLabel(detail);
            label.setFont(new Font("SansSerif", Font.PLAIN, 16));
            label.setForeground(new Color(44, 62, 80));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(label);
            panel.add(Box.createVerticalStrut(8));
        }

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        headerPanel.setPreferredSize(new Dimension(1200, 150));
        headerPanel.setBackground(new Color(2, 36, 52));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        try {
            headerPanel.add(createStatCard("Total Events", getTotalEventsCount()));
            headerPanel.add(createStatCard("Upcoming", getUpcomingEventsCount()));
            headerPanel.add(createStatCard("Hosts", getUniqueHostsCount()));
            headerPanel.add(createStatCard("Participants", "N/A"));
        } catch (SQLException e) {
            headerPanel.add(createStatCard("Events", "N/A"));
            headerPanel.add(createStatCard("Upcoming", "N/A"));
            headerPanel.add(createStatCard("Hosts", "N/A"));
            headerPanel.add(createStatCard("Participants", "N/A"));
        }

        return headerPanel;
    }

    private int getTotalEventsCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Events";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getUpcomingEventsCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Events WHERE StartDate >= CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getUniqueHostsCount() throws SQLException {
        String query = "SELECT COUNT(DISTINCT HostID) FROM Events";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private JPanel createStatCard(String title, Object value) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        JLabel cardTitle = new JLabel(title, JLabel.CENTER);
        cardTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        cardTitle.setForeground(new Color(17, 27, 37));
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(cardTitle, gbc);

        JLabel cardInfo = new JLabel(value.toString(), JLabel.CENTER);
        cardInfo.setFont(new Font("SansSerif", Font.BOLD, 28));
        cardInfo.setForeground(ACCENT_COLOR);
        gbc.insets = new Insets(5, 0, 0, 0);
        card.add(cardInfo, gbc);

        return card;
    }

    private void handleDatabaseError(SQLException e, JPanel panel) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Error loading events: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        panel.add(createErrorPanel("Failed to load events"));
    }

    private JPanel createErrorPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(Color.RED);

        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createNoEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel message = new JLabel("No events found", JLabel.CENTER);
        message.setFont(new Font("SansSerif", Font.ITALIC, 18));
        message.setForeground(new Color(150, 150, 150));

        panel.add(message, BorderLayout.CENTER);
        return panel;
    }
}