package modals;

import java.sql.Time;
import java.time.LocalDate;

public class Event {
    private int id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Time startTime;
    private Time endTime;
    private String venue;
    private int hostId; // Admin or Faculty ID who created the event

    // Full constructor
    public Event(int id, String title, String description, LocalDate startDate, LocalDate endDate,
                 Time startTime, Time endTime, String venue, int hostId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
        this.hostId = hostId;
    }

    // Constructor without ID (for creating new events)
    public Event(String title, String description, LocalDate startDate, LocalDate endDate,
                 Time startTime, Time endTime, String venue, int hostId) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
        this.hostId = hostId;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Time getStartTime() { return startTime; }
    public Time getEndTime() { return endTime; }
    public String getVenue() { return venue; }
    public int getHostId() { return hostId; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setHostId(int hostId) { this.hostId = hostId; }

    // Utility method to convert to SQL Date
    public java.sql.Date getStartSqlDate() {
        return java.sql.Date.valueOf(startDate);
    }

    public java.sql.Date getEndSqlDate() {
        return java.sql.Date.valueOf(endDate);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", venue='" + venue + '\'' +
                '}';
    }
}