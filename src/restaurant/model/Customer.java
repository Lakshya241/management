package restaurant.model;

/**
 * Class representing a Customer, extending the User base class.
 * Demonstrates the OOP concept of Inheritance.
 */
public class Customer extends User {

    // Constructor calling super class
    public Customer(int id, String username, String password, String fullName, String phone) {
        super(id, username, password, "CUSTOMER", fullName, phone);
    }

    // Implementing the abstract method
    @Override
    public String getRoleDescription() {
        return "Customer with capabilities to browse the menu, select items, and place billing orders.";
    }
}
