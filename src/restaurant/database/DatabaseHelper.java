package restaurant.database;

import restaurant.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to manage database connections and operations using JDBC.
 * Employs proper exception handling and SQL query execution.
 */
public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:restaurant.db";

    static {
        // Load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQLite JDBC driver: " + e.getMessage());
        }
    }

    /**
     * Gets a connection to the SQLite database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Initializes the database tables and inserts default values if they are empty.
     */
    public static void initializeDatabase() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT UNIQUE NOT NULL,"
                + "password TEXT NOT NULL,"
                + "role TEXT NOT NULL,"
                + "full_name TEXT NOT NULL,"
                + "phone TEXT"
                + ");";

        String createMenuTable = "CREATE TABLE IF NOT EXISTS menu ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "category TEXT NOT NULL,"
                + "price REAL NOT NULL"
                + ");";

        String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER,"
                + "order_date TEXT NOT NULL,"
                + "subtotal REAL NOT NULL,"
                + "gst REAL NOT NULL,"
                + "total REAL NOT NULL,"
                + "FOREIGN KEY(user_id) REFERENCES users(id)"
                + ");";

        String createOrderItemsTable = "CREATE TABLE IF NOT EXISTS order_items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "order_id INTEGER NOT NULL,"
                + "menu_item_id INTEGER NOT NULL,"
                + "quantity INTEGER NOT NULL,"
                + "price REAL NOT NULL,"
                + "FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE,"
                + "FOREIGN KEY(menu_item_id) REFERENCES menu(id)"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create tables
            stmt.execute(createUsersTable);
            stmt.execute(createMenuTable);
            stmt.execute(createOrdersTable);
            stmt.execute(createOrderItemsTable);

            // Populate default admin if no users exist
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertAdmin = "INSERT INTO users (username, password, role, full_name, phone) "
                            + "VALUES ('admin', 'admin123', 'ADMIN', 'System Administrator', '9876543210');";
                    stmt.execute(insertAdmin);
                    System.out.println("Default admin user created (admin / admin123).");
                }
            }

            // Populate default menu items if empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM menu")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String[] insertQueries = {
                        "INSERT INTO menu (name, category, price) VALUES ('Paneer Tikka', 'Starter', 220.00);",
                        "INSERT INTO menu (name, category, price) VALUES ('Crispy Corn', 'Starter', 180.00);",
                        "INSERT INTO menu (name, category, price) VALUES ('Chicken Tikka', 'Starter', 280.00);",
                        "INSERT INTO menu (name, category, price) VALUES ('Butter Masala Paneer', 'Main Course', 260.00);",
                        "INSERT INTO menu (name, category, price) VALUES ('Dal Makhani', 'Main Course', 210.00);",
                        "INSERT INTO menu (name, category, price) VALUES ('Kadhai Chicken', 'Main Course', 320.00);",
                        "INSERT INTO menu (name, category, price) VALUES ('Butter Naan', 'Main Course', 50.00);",
                        "INSERT INTO menu (name, category, price) VALUES ('Jeera Rice', 'Main Course', 120.00);",
                        "INSERT INTO menu (name, category, price) VALUES ('Chocolate Brownie', 'Dessert', 150.00);",
                        "INSERT INTO menu (name, category, price) VALUES ('Gulab Jamun (2 Pcs)', 'Dessert', 80.00);",
                        "INSERT INTO menu (name, category, price) VALUES ('Virgin Mojito', 'Beverage', 110.00);",
                        "INSERT INTO menu (name, category, price) VALUES ('Iced Tea', 'Beverage', 90.00);"
                    };

                    for (String query : insertQueries) {
                        stmt.execute(query);
                    }
                    System.out.println("Default menu items initialized.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================
    // USER OPERATIONS
    // ==========================================

    /**
     * Registers a new customer in the database.
     */
    public static boolean registerCustomer(String username, String password, String fullName, String phone) throws SQLException {
        String query = "INSERT INTO users (username, password, role, full_name, phone) VALUES (?, ?, 'CUSTOMER', ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, phone);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    /**
     * Authenticates a user and returns an instance of Admin or Customer (Polymorphism).
     */
    public static User authenticate(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String role = rs.getString("role");
                    String fullName = rs.getString("full_name");
                    String phone = rs.getString("phone");

                    if ("ADMIN".equalsIgnoreCase(role)) {
                        return new Admin(id, username, password, fullName, phone);
                    } else {
                        return new Customer(id, username, password, fullName, phone);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if a username already exists.
     */
    public static boolean isUsernameTaken(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // ==========================================
    // MENU OPERATIONS
    // ==========================================

    /**
     * Retrieves all items on the menu.
     */
    public static List<MenuItem> getAllMenuItems() throws SQLException {
        List<MenuItem> items = new ArrayList<>();
        String query = "SELECT * FROM menu ORDER BY category, name";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                items.add(new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price")
                ));
            }
        }
        return items;
    }

    /**
     * Adds a new item to the menu.
     */
    public static boolean addMenuItem(MenuItem item) throws SQLException {
        String query = "INSERT INTO menu (name, category, price) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getCategory());
            pstmt.setDouble(3, item.getPrice());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    /**
     * Updates an existing menu item.
     */
    public static boolean updateMenuItem(MenuItem item) throws SQLException {
        String query = "UPDATE menu SET name = ?, category = ?, price = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getCategory());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setInt(4, item.getId());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    /**
     * Deletes a menu item.
     */
    public static boolean deleteMenuItem(int id) throws SQLException {
        String query = "DELETE FROM menu WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    // ==========================================
    // ORDER OPERATIONS
    // ==========================================

    /**
     * Saves an order and its items. Uses transaction control.
     */
    public static boolean saveOrder(Order order) {
        Connection conn = null;
        PreparedStatement orderPstmt = null;
        PreparedStatement itemPstmt = null;

        String insertOrderQuery = "INSERT INTO orders (user_id, order_date, subtotal, gst, total) VALUES (?, ?, ?, ?, ?)";
        String insertItemQuery = "INSERT INTO order_items (order_id, menu_item_id, quantity, price) VALUES (?, ?, ?, ?)";

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Insert order
            orderPstmt = conn.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS);
            orderPstmt.setInt(1, order.getUserId());
            orderPstmt.setString(2, order.getOrderDate());
            orderPstmt.setDouble(3, order.getSubtotal());
            orderPstmt.setDouble(4, order.getGst());
            orderPstmt.setDouble(5, order.getTotal());

            int affectedRows = orderPstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            int orderId = -1;
            try (ResultSet generatedKeys = orderPstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                    order.setOrderId(orderId);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            // 2. Insert order items
            itemPstmt = conn.prepareStatement(insertItemQuery);
            for (OrderItem item : order.getItems()) {
                itemPstmt.setInt(1, orderId);
                itemPstmt.setInt(2, item.getMenuItem().getId());
                itemPstmt.setInt(3, item.getQuantity());
                itemPstmt.setDouble(4, item.getMenuItem().getPrice());
                itemPstmt.addBatch();
            }

            itemPstmt.executeBatch();
            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            System.err.println("Transaction rolled back for order creation. Error: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (orderPstmt != null) orderPstmt.close();
                if (itemPstmt != null) itemPstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fetches all completed orders, including customer details.
     */
    public static List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u.full_name FROM orders o "
                + "LEFT JOIN users u ON o.user_id = u.id "
                + "ORDER BY o.id DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("order_date"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("gst"),
                        rs.getDouble("total")
                );

                // Fetch items for this order
                loadOrderItems(conn, order);
                orders.add(order);
            }
        }
        return orders;
    }

    /**
     * Loads the items for a specific order using an open connection.
     */
    private static void loadOrderItems(Connection conn, Order order) throws SQLException {
        String query = "SELECT oi.*, m.name, m.category FROM order_items oi "
                + "JOIN menu m ON oi.menu_item_id = m.id "
                + "WHERE oi.order_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, order.getOrderId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem(
                            rs.getInt("menu_item_id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getDouble("price") // This is the historical price
                    );
                    OrderItem orderItem = new OrderItem(item, rs.getInt("quantity"));
                    order.addItem(orderItem);
                }
            }
        }
    }
}
