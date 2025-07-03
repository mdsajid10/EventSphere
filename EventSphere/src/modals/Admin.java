package modals;

public class Admin {
    private int id;
    private String name;
    private String email;
    private String password;
    private String department;
    private String contactNo;

    public Admin(int id, String name, String email, String password,String department,String contactNo) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.department=department;
        this.contactNo=contactNo;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDepartment() { return department; }
    public String getContactNo() { return contactNo; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setDepartment(String department) { this.department= this.department; }
    public void setContactNo(String contactNo) { this.contactNo= this.contactNo; }
}
