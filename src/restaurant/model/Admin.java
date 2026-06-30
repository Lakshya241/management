package restaurant.model;

/**
 * Class representing an Administrator, extending the User base class.
 * Demonstrates the OOP concept of Inheritance.
 */
public class Admin extends User {

    // Constructor calling super class
    public Admin(int id, String username, String password, String fullName, String phone) {
        super(id, username, password, "ADMIN", fullName, phone);
    }

    // Implementing the abstract method
    @Override
    public String getRoleDescription() {
        return "Administrator with capabilities to manage the menu items, view order histories, and track statistics.";
    }
}
