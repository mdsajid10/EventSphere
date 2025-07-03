package modals;

public class Faculty {
    private int id;
    private String name;
    private String department;
    private String email;
    private String password;

    public Faculty(int id, String name, String department, String email, String password) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
