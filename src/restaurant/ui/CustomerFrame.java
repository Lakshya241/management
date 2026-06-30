package restaurant.ui;

import restaurant.database.DatabaseHelper;
import restaurant.model.*;
import restaurant.model.MenuItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Customer ordering dashboard. Allows customers to browse the menu, 
 * filter by category, add items to cart, view total live, and checkout.
 */
public class CustomerFrame extends JFrame {
    private User currentUser;
    private List<MenuItem> menuItemsList;
    private List<OrderItem> cartList;

    // UI Components
    private JPanel menuGridPanel;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel lblSubtotal;
    private JLabel lblGst;
    private JLabel lblTotal;
    
    // Category tabs
    private String selectedCategory = "All";

    public CustomerFrame(User user) {
        this.currentUser = user;
        this.cartList = new ArrayList<>();
        
        setTitle("Luxe Dine - Customer Dashboard");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Load menu items from database
        loadMenuFromDb();

        // Main Layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BG_PRIMARY);
        
        // 1. Header Panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Center Panel (Split: Left Menu, Right Cart)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Side: Menu browser
        gbc.gridx = 0;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(10, 15, 15, 10);
        contentPanel.add(createMenuBrowserPanel(), gbc);

        // Right Side: Cart panel
        gbc.gridx = 1;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(10, 0, 15, 15);
        contentPanel.add(createCartPanel(), gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Initial menu display
        filterMenu("All");
    }

    private void loadMenuFromDb() {
        try {
            menuItemsList = DatabaseHelper.getAllMenuItems();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load menu items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            menuItemsList = new ArrayList<>();
        }
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_SECONDARY);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x282835)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Left: Branding
        JLabel logoLabel = new JLabel("LUXE DINE");
        logoLabel.setFont(Theme.FONT_TITLE_LARGE);
        logoLabel.setForeground(Theme.ACCENT);
        header.add(logoLabel, BorderLayout.WEST);

        // Right: Profile + Logout
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        profilePanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + " (" + currentUser.getUsername() + ")");
        welcomeLabel.setFont(Theme.FONT_BODY_BOLD);
        welcomeLabel.setForeground(Theme.TEXT_PRIMARY);
        profilePanel.add(welcomeLabel);

        JButton logoutBtn = new Theme.StyledButton("Logout", Theme.BG_TERTIARY, Theme.TEXT_PRIMARY, 8);
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginRegisterFrame().setVisible(true);
                this.dispose();
            }
        });
        profilePanel.add(logoutBtn);

        header.add(profilePanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createMenuBrowserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Top: Categories filters
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);
        filterBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        String[] categories = {"All", "Starter", "Main Course", "Dessert", "Beverage"};
        for (String cat : categories) {
            JButton catBtn = new Theme.StyledButton(cat, Theme.BG_SECONDARY, Theme.TEXT_MUTED, 15);
            if (cat.equals("All")) {
                catBtn.setBackground(Theme.ACCENT);
                catBtn.setForeground(Theme.BG_PRIMARY);
            }
            catBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Reset other buttons colors (simple hack)
                    for (Component c : filterBar.getComponents()) {
                        if (c instanceof JButton) {
                            c.setBackground(Theme.BG_SECONDARY);
                            c.setForeground(Theme.TEXT_MUTED);
                        }
                    }
                    catBtn.setBackground(Theme.ACCENT);
                    catBtn.setForeground(Theme.BG_PRIMARY);
                    filterMenu(cat);
                }
            });
            filterBar.add(catBtn);
        }
        panel.add(filterBar, BorderLayout.NORTH);

        // Menu cards grid (inside scrollpane)
        menuGridPanel = new JPanel(new GridLayout(0, 2, 12, 12));
        menuGridPanel.setBackground(Theme.BG_PRIMARY);
        
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(menuGridPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void filterMenu(String category) {
        selectedCategory = category;
        menuGridPanel.removeAll();

        for (MenuItem item : menuItemsList) {
            if (category.equals("All") || item.getCategory().equalsIgnoreCase(category)) {
                menuGridPanel.add(createMenuItemCard(item));
            }
        }

        menuGridPanel.revalidate();
        menuGridPanel.repaint();
    }

    private JPanel createMenuItemCard(MenuItem item) {
        Theme.RoundedPanel card = new Theme.RoundedPanel(12, Theme.BG_SECONDARY);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        card.setPreferredSize(new Dimension(280, 110));

        // Center Details (Name + Category + Price)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(Theme.FONT_BODY_BOLD);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel catLabel = new JLabel(item.getCategory());
        catLabel.setFont(Theme.FONT_SMALL);
        catLabel.setForeground(Theme.TEXT_MUTED);

        JLabel priceLabel = new JLabel("INR " + String.format("%.2f", item.getPrice()));
        priceLabel.setFont(Theme.FONT_BODY_BOLD);
        priceLabel.setForeground(Theme.ACCENT);

        detailsPanel.add(nameLabel);
        detailsPanel.add(catLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(priceLabel);
        card.add(detailsPanel, BorderLayout.CENTER);

        // Right side: controls (spinner + Add button)
        JPanel ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.Y_AXIS));
        ctrlPanel.setOpaque(false);
        ctrlPanel.setPreferredSize(new Dimension(80, 80));

        SpinnerModel spinModel = new SpinnerNumberModel(1, 1, 20, 1);
        JSpinner qtySpinner = new JSpinner(spinModel);
        qtySpinner.setFont(Theme.FONT_BODY);
        // Style spinner editor
        JComponent editor = qtySpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(Theme.BG_TERTIARY);
            tf.setForeground(Theme.TEXT_PRIMARY);
            tf.setCaretColor(Theme.ACCENT);
        }
        qtySpinner.setBorder(BorderFactory.createLineBorder(new Color(0x3E3E52), 1));
        
        JButton addBtn = new Theme.StyledButton("+ Add", Theme.ACCENT, Theme.BG_PRIMARY, 8);
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantity = (int) qtySpinner.getValue();
                addToCart(item, quantity);
                qtySpinner.setValue(1); // Reset spinner
            }
        });

        ctrlPanel.add(qtySpinner);
        ctrlPanel.add(Box.createVerticalStrut(8));
        ctrlPanel.add(addBtn);

        card.add(ctrlPanel, BorderLayout.EAST);
        return card;
    }

    private JPanel createCartPanel() {
        Theme.RoundedPanel panel = new Theme.RoundedPanel(15, Theme.BG_SECONDARY);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Shopping Cart", SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_HEADER);
        titleLabel.setForeground(Theme.ACCENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Cart Table setup
        String[] columns = {"Item", "Qty", "Price", "Total"};
        cartModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only cells
            }
        };
        
        cartTable = new JTable(cartModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        Theme.styleTable(cartTable, scrollPane);
        
        // Set column widths
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(40);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(70);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom section: Actions + Calculations
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Calculations Panel
        JPanel calcPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        calcPanel.setOpaque(false);
        calcPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x3E3E52)),
                BorderFactory.createEmptyBorder(5, 5, 10, 5)
        ));

        JLabel l1 = new JLabel("Subtotal:"); l1.setFont(Theme.FONT_BODY); l1.setForeground(Theme.TEXT_MUTED);
        lblSubtotal = new JLabel("INR 0.00", SwingConstants.RIGHT); lblSubtotal.setFont(Theme.FONT_BODY); lblSubtotal.setForeground(Theme.TEXT_PRIMARY);

        JLabel l2 = new JLabel("GST (18%):"); l2.setFont(Theme.FONT_BODY); l2.setForeground(Theme.TEXT_MUTED);
        lblGst = new JLabel("INR 0.00", SwingConstants.RIGHT); lblGst.setFont(Theme.FONT_BODY); lblGst.setForeground(Theme.TEXT_PRIMARY);

        JLabel l3 = new JLabel("Grand Total:"); l3.setFont(Theme.FONT_BODY_BOLD); l3.setForeground(Theme.ACCENT);
        lblTotal = new JLabel("INR 0.00", SwingConstants.RIGHT); lblTotal.setFont(Theme.FONT_BODY_BOLD); lblTotal.setForeground(Theme.ACCENT);

        calcPanel.add(l1); calcPanel.add(lblSubtotal);
        calcPanel.add(l2); calcPanel.add(lblGst);
        calcPanel.add(l3); calcPanel.add(lblTotal);
        bottomContainer.add(calcPanel);

        // Action buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton removeBtn = new Theme.StyledButton("Remove Item", Theme.BG_TERTIARY, Theme.TEXT_PRIMARY, 8);
        JButton checkoutBtn = new Theme.StyledButton("Checkout", Theme.ACCENT, Theme.BG_PRIMARY, 8);

        removeBtn.addActionListener(e -> removeSelectedFromCart());
        checkoutBtn.addActionListener(e -> handleCheckout());

        actionPanel.add(removeBtn);
        actionPanel.add(checkoutBtn);
        bottomContainer.add(actionPanel);

        panel.add(bottomContainer, BorderLayout.SOUTH);
        return panel;
    }

    private void addToCart(MenuItem item, int qty) {
        // Check if item already exists in cart, if so, merge
        boolean exists = false;
        for (OrderItem oi : cartList) {
            if (oi.getMenuItem().getId() == item.getId()) {
                oi.setQuantity(oi.getQuantity() + qty);
                exists = true;
                break;
            }
        }

        if (!exists) {
            cartList.add(new OrderItem(item, qty));
        }

        updateCartTable();
    }

    private void removeSelectedFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow >= 0) {
            cartList.remove(selectedRow);
            updateCartTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item from the cart to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateCartTable() {
        cartModel.setRowCount(0);
        double subtotal = 0.0;

        for (OrderItem item : cartList) {
            double lineTotal = item.getSubtotal();
            subtotal += lineTotal;

            cartModel.addRow(new Object[]{
                    item.getMenuItem().getName(),
                    item.getQuantity(),
                    String.format("%.2f", item.getMenuItem().getPrice()),
                    String.format("%.2f", lineTotal)
            });
        }

        double gst = subtotal * 0.18;
        double total = subtotal + gst;

        lblSubtotal.setText("INR " + String.format("%.2f", subtotal));
        lblGst.setText("INR " + String.format("%.2f", gst));
        lblTotal.setText("INR " + String.format("%.2f", total));
    }

    private void handleCheckout() {
        if (cartList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your shopping cart is empty.", "Cart Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Confirm and print bill?", "Checkout Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Create Order Object
        Order order = new Order();
        order.setUserId(currentUser.getId());
        order.setCustomerName(currentUser.getFullName());
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        order.setOrderDate(sdf.format(new Date()));

        for (OrderItem item : cartList) {
            order.addItem(item);
        }

        // Calculate final totals (GST rate 18.0)
        order.calculateTotals(18.0);

        // Save order via JDBC
        boolean success = DatabaseHelper.saveOrder(order);
        if (success) {
            // Popup receipt dialog
            ReceiptDialog receipt = new ReceiptDialog(this, order);
            receipt.setVisible(true);

            // Reset cart
            cartList.clear();
            updateCartTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save your order. Please check database logs.", "Checkout Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
