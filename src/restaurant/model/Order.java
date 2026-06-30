package restaurant.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a complete customer Order / Bill.
 * Contains list of OrderItem, calculates subtotal, GST, and grand total.
 */
public class Order {
    private int orderId;
    private int userId;
    private String customerName; // Cached helper for UI display
    private String orderDate;
    private List<OrderItem> items;
    private double subtotal;
    private double gst;
    private double total;

    // Constructors
    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(int orderId, int userId, String customerName, String orderDate, double subtotal, double gst, double total) {
        this.orderId = orderId;
        this.userId = userId;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.items = new ArrayList<>();
        this.subtotal = subtotal;
        this.gst = gst;
        this.total = total;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getGst() {
        return gst;
    }

    public double getTotal() {
        return total;
    }

    /**
     * Calculates the subtotal, GST, and grand total based on current order items.
     * @param gstPercentage GST rate (e.g. 18.0 for 18%)
     */
    public void calculateTotals(double gstPercentage) {
        this.subtotal = 0.0;
        for (OrderItem item : items) {
            this.subtotal += item.getSubtotal();
        }
        this.gst = this.subtotal * (gstPercentage / 100.0);
        this.total = this.subtotal + this.gst;
    }
}
