package restaurant.model;

/**
 * Class representing an item in an Order.
 * Demonstrates Object Interaction / Composition.
 */
public class OrderItem {
    private MenuItem menuItem;
    private int quantity;

    // Constructor
    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    // Getters and Setters
    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Calculates the subtotal for this specific order item.
     */
    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }
}
