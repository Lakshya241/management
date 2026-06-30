package restaurant.model;

/**
 * Abstract class representing a User in the Restaurant Billing System.
 * Demonstrates the concept of Abstraction and Encapsulation.
 */
public abstract class User {
    private int id;
    private String username;
    private String password;
    private String role; // "ADMIN" or "CUSTOMER"
    private String fullName;
    private String phone;

    // Constructor
    public User(int id, String username, String password, String role, String fullName, String phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.phone = phone;
    }

    // Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Abstract method to get user description.
     * Subclasses must provide an implementation.
     */
    public abstract String getRoleDescription();
}
