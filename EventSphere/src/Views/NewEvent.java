package Views;

import DBconnection.DBConnection;
import modals.Event;
import modals.Admin;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class NewEvent extends JPanel {
    private JTextField titleField, venueField, startDateField, endDateField, startTimeField, endTimeField;
    private JTextArea descriptionArea;
    private JButton createButton;

    public NewEvent() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(null);
        setBackground(new Color(240, 240, 240));
        setBounds(0, 0, 1260, 720);

        // Title Label
        JLabel titleLabel = new JLabel("CREATE NEW EVENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBounds(0, 30, 1260, 40);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);

        // Main Form Panel
        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBounds(280, 100, 700, 540);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        add(formPanel);

        // Form Fields
        addFormField(formPanel, "Event Title:", titleField = new JTextField(), 50, 30, 600, 35);

        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        descLabel.setBounds(50, 90, 600, 25);
        formPanel.add(descLabel);

        descriptionArea = new JTextArea();
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBounds(50, 120, 600, 120);
        formPanel.add(scrollPane);

        // Date/Time Fields
        JLabel dateHeader = new JLabel("DATE & TIME DETAILS");
        dateHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dateHeader.setBounds(50, 260, 600, 25);
        formPanel.add(dateHeader);

        addFormField(formPanel, "Start Date (yyyy-mm-dd):", startDateField = new JTextField(), 50, 300, 250, 35);
        addFormField(formPanel, "End Date (yyyy-mm-dd):", endDateField = new JTextField(), 350, 300, 250, 35);
        addFormField(formPanel, "Start Time (HH:mm):", startTimeField = new JTextField(), 50, 370, 250, 35);
        addFormField(formPanel, "End Time (HH:mm):", endTimeField = new JTextField(), 350, 370, 250, 35);
        addFormField(formPanel, "Venue:", venueField = new JTextField(), 50, 440, 600, 35);

        // Create Button
        createButton = new JButton("CREATE EVENT");
        styleButton(createButton, new Color(70, 130, 180));
        createButton.setBounds(440, 660, 200, 45);
        createButton.addActionListener(this::handleCreateEvent);
        add(createButton);
    }

    private void addFormField(JPanel panel, String labelText, JTextField field, int x, int y, int width, int height) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setBounds(x, y, width, 25);
        panel.add(label);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBounds(x, y + 30, width, height);
        panel.add(field);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }

    private void handleCreateEvent(ActionEvent e) {
        try {
            // Validate required fields
            if (!validateFields()) return;

            // Create event object
            Event event = createEventFromInput();

            // Save to database
            if (saveEventToDatabase(event)) {
                showSuccessMessage(event);
                clearForm();
            } else {
                showError("Failed to save event to database");
            }
        } catch (DateTimeParseException e1) {
            showError("Invalid date format (use yyyy-mm-dd)");
        } catch (IllegalArgumentException e2) {
            showError("Invalid time format (use HH:mm)");
        } catch (SQLException e3) {
            showError("Database error: " + e3.getMessage());
        }
    }

    private boolean validateFields() {
        if (titleField.getText().trim().isEmpty() ||
                startDateField.getText().trim().isEmpty() ||
                startTimeField.getText().trim().isEmpty() ||
                venueField.getText().trim().isEmpty()) {
            showError("Please fill in all required fields");
            return false;
        }
        return true;
    }

    private Event createEventFromInput() throws DateTimeParseException {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        LocalDate startDate = LocalDate.parse(startDateField.getText().trim());
        LocalDate endDate = endDateField.getText().trim().isEmpty() ?
                startDate : LocalDate.parse(endDateField.getText().trim());
        String venue = venueField.getText().trim();
        int hostId = Login.loggedInAdmin.getId();

        // Format time strings
        String startTime = formatTimeString(startTimeField.getText().trim());
        String endTime = endTimeField.getText().trim().isEmpty() ?
                "23:59:00" : formatTimeString(endTimeField.getText().trim());

        return new Event(
                title, description, startDate, endDate,
                Time.valueOf(startTime), Time.valueOf(endTime), venue, hostId
        );
    }

    private String formatTimeString(String time) {
        if (!time.contains(":")) {
            time += ":00"; // Assume minutes are 00 if not provided
        } else if (time.split(":").length == 2) {
            time += ":00"; // Add seconds if missing
        }
        return time;
    }

    private boolean saveEventToDatabase(Event event) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO Events (EventName, EventDescription, EventVenue, " +
                    "StartDate, StartTime, EndDate, EndTime, HostID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getVenue());
            stmt.setDate(4, Date.valueOf(event.getStartDate()));
            stmt.setTime(5, event.getStartTime());
            stmt.setDate(6, Date.valueOf(event.getEndDate()));
            stmt.setTime(7, event.getEndTime());
            stmt.setInt(8, event.getHostId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            if (stmt != null) stmt.close();
            DBConnection.closeConnection(conn);
        }
    }

    private void showSuccessMessage(Event event) {
        String message = String.format(
                "<html><div style='text-align:center;'>" +
                        "<b>Event Created Successfully!</b><br><br>" +
                        "<b>%s</b><br>%s to %s<br>%s</div></html>",
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate(),
                event.getVenue()
        );
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align:center;'>" + message + "</div></html>",
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        startDateField.setText("");
        endDateField.setText("");
        startTimeField.setText("");
        endTimeField.setText("");
        venueField.setText("");
    }
}