package modals;

public class Participant {
    private int id;
    private String name;
    private String department;
    private String email;
    private int eventId;

    public Participant(int id, String name, String department, String email, int eventId) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.email = email;
        this.eventId = eventId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getEmail() { return email; }
    public int getEventId() { return eventId; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setEmail(String email) { this.email = email; }
    public void setEventId(int eventId) { this.eventId = eventId; }
}
