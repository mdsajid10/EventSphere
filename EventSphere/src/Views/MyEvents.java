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

public class MyEvents extends JPanel {
    private static final Color CARD_BG_COLOR = new Color(240, 240, 240);
    private static final Color BORDER_COLOR = new Color(63, 102, 139);
    private static final Color BUTTON_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(147, 228, 88);
    private static final Color EDIT_BUTTON_COLOR = new Color(241, 196, 15);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    public MyEvents() {
        setLayout(null);
        setBackground(Color.WHITE);
        setBounds(0, 0, 1260, 720);

        // Header Panel with statistics
        JPanel headerPanel = createHeaderPanel();
        headerPanel.setBounds(30, 20, 1200, 150);
        add(headerPanel);

        // Create scroll pane for events
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(30, 190, 1200, 500);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Content panel inside scroll pane
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        scrollPane.setViewportView(contentPanel);
        add(scrollPane);

        try {
            List<Event> events = getMyEventsFromDatabase();
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

    private List<Event> getMyEventsFromDatabase() throws SQLException {
        List<Event> events = new ArrayList<>();
        Admin admin = Login.loggedInAdmin;
        if (admin == null) {
            throw new SQLException("No admin logged in");
        }

        String query = "SELECT * FROM Events WHERE HostID = ? ORDER BY StartDate, StartTime";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, admin.getId());
            try (ResultSet rs = stmt.executeQuery()) {
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
        }
        return events;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        headerPanel.setPreferredSize(new Dimension(1200, 150));
        headerPanel.setBackground(new Color(2, 36, 52));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        try {
            headerPanel.add(createStatCard("Total Events Hosted", getTotalEventsCount()));
            headerPanel.add(createStatCard("Upcoming Events", getUpcomingEventsCount()));
            headerPanel.add(createStatCard("Total Participants", getTotalParticipantsCount()));
        } catch (SQLException e) {
            headerPanel.add(createStatCard("Events Hosted", "N/A"));
            headerPanel.add(createStatCard("Upcoming", "N/A"));
            headerPanel.add(createStatCard("Participants", "N/A"));
        }

        return headerPanel;
    }

    private int getTotalEventsCount() throws SQLException {
        Admin admin = Login.loggedInAdmin;
        if (admin == null) {
            return 0;
        }

        String query = "SELECT COUNT(*) FROM Events WHERE HostID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, admin.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private int getUpcomingEventsCount() throws SQLException {
        Admin admin = Login.loggedInAdmin;
        if (admin == null) {
            return 0;
        }

        String query = "SELECT COUNT(*) FROM Events WHERE HostID = ? AND StartDate >= CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, admin.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private int getTotalParticipantsCount() throws SQLException {
        Admin admin = Login.loggedInAdmin;
        if (admin == null) {
            return 0;
        }

        // Assuming you have an EventParticipants table
        String query = "SELECT COUNT(*) FROM Participants ep " +
                "JOIN Events e ON ep.EventID = e.EventID " +
                "WHERE e.HostID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, admin.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
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
        styleButton(knowMoreBtn, BUTTON_COLOR);
        knowMoreBtn.addActionListener(e -> showEventDetails(event));
        buttonPanel.add(knowMoreBtn);

        JButton editBtn = new JButton("Edit Event");
        styleButton(editBtn, EDIT_BUTTON_COLOR);
        editBtn.addActionListener(e -> editEvent(event));
        buttonPanel.add(editBtn);

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

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void showEventDetails(Event event) {
        try {
            JDialog detailsDialog = new JDialog();
            detailsDialog.setTitle("Event Details: " + event.getTitle());
            detailsDialog.setLayout(new BorderLayout());
            detailsDialog.setSize(850, 600);
            detailsDialog.setLocationRelativeTo(this);

            // Main content panel
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            mainPanel.setBackground(Color.WHITE);

            // Button panel with back and edit buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setBackground(Color.WHITE);

            JButton backButton = new JButton("Back to Events");
            styleButton(backButton, BUTTON_COLOR);
            backButton.addActionListener(e -> detailsDialog.dispose());
            buttonPanel.add(backButton);

            JButton editButton = new JButton("Edit Event");
            styleButton(editButton, EDIT_BUTTON_COLOR);
            editButton.addActionListener(e -> {
                detailsDialog.dispose();
                editEvent(event);
            });
            buttonPanel.add(editButton);

            // Event Details Card
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

            // Details Grid
            JPanel detailsGrid = new JPanel(new GridLayout(0, 3, 10, 15));
            detailsGrid.setBackground(CARD_BG_COLOR);
            detailsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

            addDetailCell(detailsGrid, "📍 Venue", event.getVenue());

            String dateRange = DATE_FORMAT.format(Date.valueOf(event.getStartDate())) +
                    " to " + DATE_FORMAT.format(Date.valueOf(event.getEndDate()));
            addDetailCell(detailsGrid, "📅 Date", dateRange);

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

            // Participants Section
            eventCard.add(Box.createRigidArea(new Dimension(0, 25)));
            JLabel participantsTitle = new JLabel("Participants");
            participantsTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
            participantsTitle.setForeground(new Color(44, 62, 80));
            participantsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            eventCard.add(participantsTitle);
            eventCard.add(Box.createRigidArea(new Dimension(0, 10)));

            // Add participants list
            JPanel participantsPanel = new JPanel();
            participantsPanel.setLayout(new BoxLayout(participantsPanel, BoxLayout.Y_AXIS));
            participantsPanel.setBackground(CARD_BG_COLOR);
            participantsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            List<String> participants = getEventParticipants(event.getId());
            if (participants.isEmpty()) {
                participantsPanel.add(new JLabel("No participants yet"));
            } else {
                for (String participant : participants) {
                    participantsPanel.add(new JLabel("• " + participant));
                    participantsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }

            eventCard.add(participantsPanel);

            // Add to main panel
            mainPanel.add(eventCard);

            // Add to dialog
            detailsDialog.add(buttonPanel, BorderLayout.NORTH);
            detailsDialog.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
            detailsDialog.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading event details: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<String> getEventParticipants(int eventId) throws SQLException {
        List<String> participants = new ArrayList<>();
        String query = "SELECT NameOfParticipant FROM Participants WHERE EventID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    participants.add(rs.getString("NameOfParticipant"));
                }
            }
        }
        return participants;
    }

    private void editEvent(Event event) {
        JDialog editDialog = new JDialog();
        editDialog.setTitle("Edit Event: " + event.getTitle());
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(700, 600);
        editDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Form fields
        JTextField titleField = new JTextField(event.getTitle());
        JTextArea descArea = new JTextArea(event.getDescription(), 5, 20);
        JTextField venueField = new JTextField(event.getVenue());
        JTextField startDateField = new JTextField(event.getStartDate().toString());
        JTextField endDateField = new JTextField(event.getEndDate().toString());
        JTextField startTimeField = new JTextField(event.getStartTime().toString());
        JTextField endTimeField = new JTextField(event.getEndTime().toString());

        // Add form components
        mainPanel.add(createFormField("Event Title:", titleField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createFormField("Description:", new JScrollPane(descArea)));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createFormField("Venue:", venueField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel dateTimePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        dateTimePanel.setBackground(Color.WHITE);
        dateTimePanel.add(createFormField("Start Date:", startDateField));
        dateTimePanel.add(createFormField("End Date:", endDateField));
        dateTimePanel.add(createFormField("Start Time:", startTimeField));
        dateTimePanel.add(createFormField("End Time:", endTimeField));
        mainPanel.add(dateTimePanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(231, 76, 60));
        cancelButton.addActionListener(e -> editDialog.dispose());
        buttonPanel.add(cancelButton);

        JButton saveButton = new JButton("Save Changes");
        styleButton(saveButton, new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            try {
                if (updateEventInDatabase(event.getId(),
                        titleField.getText(),
                        descArea.getText(),
                        venueField.getText(),
                        startDateField.getText(),
                        endDateField.getText(),
                        startTimeField.getText(),
                        endTimeField.getText())) {
                    JOptionPane.showMessageDialog(editDialog,
                            "Event updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    editDialog.dispose();
                    refreshPanel();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(editDialog,
                        "Error updating event: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);

        editDialog.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setVisible(true);
    }

    private JPanel createFormField(String label, Component field) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(lbl, BorderLayout.WEST);

        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private boolean updateEventInDatabase(int eventId, String title, String description,
                                          String venue, String startDate, String endDate, String startTime, String endTime) throws SQLException {
        String query = "UPDATE Events SET " +
                "EventName = ?, " +
                "EventDescription = ?, " +
                "EventVenue = ?, " +
                "StartDate = ?, " +
                "EndDate = ?, " +
                "StartTime = ?, " +
                "EndTime = ? " +
                "WHERE EventID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, venue);
            stmt.setString(4, startDate);
            stmt.setString(5, endDate);
            stmt.setString(6, startTime);
            stmt.setString(7, endTime);
            stmt.setInt(8, eventId);

            return stmt.executeUpdate() > 0;
        }
    }

    private void refreshPanel() {
        removeAll();
        setLayout(null);
        setBackground(Color.WHITE);
        setBounds(0, 0, 1260, 720);

        // Recreate the panel
        JPanel headerPanel = createHeaderPanel();
        headerPanel.setBounds(30, 20, 1200, 150);
        add(headerPanel);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(30, 190, 1200, 500);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        scrollPane.setViewportView(contentPanel);
        add(scrollPane);

        try {
            List<Event> events = getMyEventsFromDatabase();
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

        revalidate();
        repaint();
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

        JLabel message = new JLabel("You haven't hosted any events yet", JLabel.CENTER);
        message.setFont(new Font("SansSerif", Font.ITALIC, 18));
        message.setForeground(new Color(150, 150, 150));

        panel.add(message, BorderLayout.CENTER);
        return panel;
    }
}