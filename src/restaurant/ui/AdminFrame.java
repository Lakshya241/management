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
import java.util.ArrayList;
import java.util.List;

/**
 * Admin dashboard. Includes Menu Item Management (CRUD operations with validation)
 * and Order History tracking with analytical reports.
 */
public class AdminFrame extends JFrame {
    private User currentUser;
    private List<MenuItem> menuItemsList;
    private List<Order> ordersList;

    // Menu Management Components
    private JTable menuTable;
    private DefaultTableModel menuModel;
    private JTextField txtItemName;
    private JComboBox<String> comboCategory;
    private JTextField txtPrice;
    private JLabel lblSelectedId;

    // Order History Components
    private JTable ordersTable;
    private DefaultTableModel ordersModel;
    private JLabel lblTotalRevenue;
    private JLabel lblTotalOrders;
    private JLabel lblAverageBill;

    public AdminFrame(User user) {
        this.currentUser = user;
        
        setTitle("Luxe Dine - Admin Dashboard");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BG_PRIMARY);

        // 1. Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Tabs panel (Styled to look modern)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Theme.BG_SECONDARY);
        tabbedPane.setForeground(Theme.TEXT_MUTED);
        tabbedPane.setFont(Theme.FONT_BODY_BOLD);
        // Style Tabbed Pane
        UIManager.put("TabbedPane.selected", Theme.BG_PRIMARY);
        UIManager.put("TabbedPane.contentAreaColor", Theme.BG_PRIMARY);
        UIManager.put("TabbedPane.selectHighlight", Theme.ACCENT);
        tabbedPane.updateUI();

        tabbedPane.addTab("Menu Management", createMenuManagementTab());
        tabbedPane.addTab("Order History & Analytics", createOrdersHistoryTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);

        // Initial loads
        refreshMenuData();
        refreshOrdersData();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_SECONDARY);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x282835)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Left: Branding
        JLabel logoLabel = new JLabel("LUXE DINE - ADMIN");
        logoLabel.setFont(Theme.FONT_TITLE_LARGE);
        logoLabel.setForeground(Theme.ACCENT);
        header.add(logoLabel, BorderLayout.WEST);

        // Right: Profile + Logout
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        profilePanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Admin: " + currentUser.getFullName());
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

    // ==========================================
    // TAB 1: MENU MANAGEMENT
    // ==========================================
    private JPanel createMenuManagementTab() {
        JPanel tab = new JPanel(new GridBagLayout());
        tab.setBackground(Theme.BG_PRIMARY);
        tab.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Side: JTable of items
        gbc.gridx = 0;
        gbc.weightx = 0.6;
        gbc.insets = new Insets(0, 0, 0, 10);
        
        String[] menuColumns = {"ID", "Item Name", "Category", "Price (INR)"};
        menuModel = new DefaultTableModel(menuColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        menuTable = new JTable(menuModel);
        JScrollPane scrollPane = new JScrollPane(menuTable);
        Theme.styleTable(menuTable, scrollPane);
        
        // Column size tuning
        menuTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        menuTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        menuTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        menuTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        // Row selection updates fields
        menuTable.getSelectionModel().addListSelectionListener(e -> populateFieldsFromSelection());

        tab.add(scrollPane, gbc);

        // Right Side: Control Form
        gbc.gridx = 1;
        gbc.weightx = 0.4;
        gbc.insets = new Insets(0, 10, 0, 0);

        Theme.RoundedPanel formPanel = new Theme.RoundedPanel(15, Theme.BG_SECONDARY);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints fGbc = new GridBagConstraints();
        fGbc.fill = GridBagConstraints.HORIZONTAL;
        fGbc.gridx = 0;
        fGbc.insets = new Insets(8, 0, 8, 0);
        fGbc.weightx = 1.0;

        // Title
        JLabel formTitle = new JLabel("Manage Menu Item");
        formTitle.setFont(Theme.FONT_HEADER);
        formTitle.setForeground(Theme.ACCENT);
        fGbc.gridy = 0;
        formPanel.add(formTitle, fGbc);

        // ID display
        fGbc.gridy = 1;
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        idPanel.setOpaque(false);
        JLabel idLabel = new JLabel("Selected Item ID: ");
        idLabel.setFont(Theme.FONT_BODY);
        idLabel.setForeground(Theme.TEXT_MUTED);
        lblSelectedId = new JLabel("None");
        lblSelectedId.setFont(Theme.FONT_BODY_BOLD);
        lblSelectedId.setForeground(Theme.ACCENT);
        idPanel.add(idLabel);
        idPanel.add(lblSelectedId);
        formPanel.add(idPanel, fGbc);

        // Name field
        fGbc.gridy = 2;
        JLabel nameLabel = new JLabel("Item Name");
        nameLabel.setFont(Theme.FONT_BODY_BOLD);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        formPanel.add(nameLabel, fGbc);

        txtItemName = new JTextField();
        Theme.styleTextField(txtItemName, "e.g., Paneer Butter Masala");
        fGbc.gridy = 3;
        formPanel.add(txtItemName, fGbc);

        // Category dropdown
        fGbc.gridy = 4;
        JLabel catLabel = new JLabel("Category");
        catLabel.setFont(Theme.FONT_BODY_BOLD);
        catLabel.setForeground(Theme.TEXT_PRIMARY);
        formPanel.add(catLabel, fGbc);

        comboCategory = new JComboBox<>(new String[]{"Starter", "Main Course", "Dessert", "Beverage"});
        Theme.styleComboBox(comboCategory);
        fGbc.gridy = 5;
        formPanel.add(comboCategory, fGbc);

        // Price field
        fGbc.gridy = 6;
        JLabel priceLabel = new JLabel("Price (INR)");
        priceLabel.setFont(Theme.FONT_BODY_BOLD);
        priceLabel.setForeground(Theme.TEXT_PRIMARY);
        formPanel.add(priceLabel, fGbc);

        txtPrice = new JTextField();
        Theme.styleTextField(txtPrice, "e.g., 250.00");
        fGbc.gridy = 7;
        formPanel.add(txtPrice, fGbc);

        // Spacer
        fGbc.gridy = 8;
        formPanel.add(Box.createVerticalStrut(20), fGbc);

        // Buttons Layout
        fGbc.gridy = 9;
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setOpaque(false);

        JButton addBtn = new Theme.StyledButton("Add New", Theme.ACCENT, Theme.BG_PRIMARY, 8);
        JButton updateBtn = new Theme.StyledButton("Update", Theme.BG_TERTIARY, Theme.TEXT_PRIMARY, 8);
        JButton deleteBtn = new Theme.StyledButton("Delete", Theme.ERROR, Theme.TEXT_PRIMARY, 8);
        JButton clearBtn = new Theme.StyledButton("Clear", Theme.BG_TERTIARY, Theme.TEXT_MUTED, 8);

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);
        formPanel.add(btnPanel, fGbc);

        // Action Listeners
        addBtn.addActionListener(e -> handleAddMenuItem());
        updateBtn.addActionListener(e -> handleUpdateMenuItem());
        deleteBtn.addActionListener(e -> handleDeleteMenuItem());
        clearBtn.addActionListener(e -> clearFormFields());

        tab.add(formPanel, gbc);

        return tab;
    }

    private void refreshMenuData() {
        try {
            menuItemsList = DatabaseHelper.getAllMenuItems();
            menuModel.setRowCount(0);
            for (MenuItem item : menuItemsList) {
                menuModel.addRow(new Object[]{
                        item.getId(),
                        item.getName(),
                        item.getCategory(),
                        String.format("%.2f", item.getPrice())
                });
            }
        } catch (SQLException e) {
            showError("Failed to fetch menu items: " + e.getMessage());
        }
    }

    private void populateFieldsFromSelection() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow >= 0) {
            lblSelectedId.setText(menuModel.getValueAt(selectedRow, 0).toString());
            txtItemName.setText(menuModel.getValueAt(selectedRow, 1).toString());
            comboCategory.setSelectedItem(menuModel.getValueAt(selectedRow, 2).toString());
            txtPrice.setText(menuModel.getValueAt(selectedRow, 3).toString());
        }
    }

    private void clearFormFields() {
        menuTable.clearSelection();
        lblSelectedId.setText("None");
        txtItemName.setText("");
        comboCategory.setSelectedIndex(0);
        txtPrice.setText("");
    }

    private void handleAddMenuItem() {
        String name = txtItemName.getText().trim();
        String category = comboCategory.getSelectedItem().toString();
        String priceStr = txtPrice.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            showError("All fields must be filled out.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                showError("Price must be a positive number.");
                return;
            }

            MenuItem item = new MenuItem(name, category, price);
            boolean success = DatabaseHelper.addMenuItem(item);
            if (success) {
                JOptionPane.showMessageDialog(this, "Menu item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshMenuData();
                clearFormFields();
            } else {
                showError("Could not add menu item.");
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid decimal number for the price.");
        } catch (SQLException e) {
            showError("Database operation failed: " + e.getMessage());
        }
    }

    private void handleUpdateMenuItem() {
        String idStr = lblSelectedId.getText();
        if ("None".equals(idStr)) {
            showError("Please select a menu item from the table to update.");
            return;
        }

        String name = txtItemName.getText().trim();
        String category = comboCategory.getSelectedItem().toString();
        String priceStr = txtPrice.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            showError("Fields cannot be empty.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                showError("Price must be positive.");
                return;
            }

            MenuItem item = new MenuItem(id, name, category, price);
            boolean success = DatabaseHelper.updateMenuItem(item);
            if (success) {
                JOptionPane.showMessageDialog(this, "Menu item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshMenuData();
                clearFormFields();
            } else {
                showError("Could not update menu item.");
            }
        } catch (NumberFormatException e) {
            showError("Invalid price decimal input.");
        } catch (SQLException e) {
            showError("Database operation failed: " + e.getMessage());
        }
    }

    private void handleDeleteMenuItem() {
        String idStr = lblSelectedId.getText();
        if ("None".equals(idStr)) {
            showError("Select a menu item from the table to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this menu item?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean success = DatabaseHelper.deleteMenuItem(id);
            if (success) {
                JOptionPane.showMessageDialog(this, "Menu item deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshMenuData();
                clearFormFields();
            } else {
                showError("Item could not be deleted.");
            }
        } catch (SQLException e) {
            showError("Database operation failed: " + e.getMessage());
        }
    }


    // ==========================================
    // TAB 2: ORDER HISTORY & ANALYTICS
    // ==========================================
    private JPanel createOrdersHistoryTab() {
        JPanel tab = new JPanel(new BorderLayout(15, 15));
        tab.setBackground(Theme.BG_PRIMARY);
        tab.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top: Analytics Cards Panel
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setPreferredSize(new Dimension(0, 100));

        // Card 1: Total revenue
        Theme.RoundedPanel card1 = new Theme.RoundedPanel(12, Theme.BG_SECONDARY);
        card1.setLayout(new GridLayout(2, 1, 5, 5));
        card1.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel title1 = new JLabel("TOTAL SALES REVENUE"); title1.setFont(Theme.FONT_SMALL); title1.setForeground(Theme.TEXT_MUTED);
        lblTotalRevenue = new JLabel("INR 0.00"); lblTotalRevenue.setFont(Theme.FONT_TITLE_LARGE); lblTotalRevenue.setForeground(Theme.SUCCESS);
        card1.add(title1); card1.add(lblTotalRevenue);

        // Card 2: Total orders count
        Theme.RoundedPanel card2 = new Theme.RoundedPanel(12, Theme.BG_SECONDARY);
        card2.setLayout(new GridLayout(2, 1, 5, 5));
        card2.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel title2 = new JLabel("TOTAL BILLS PROCESSED"); title2.setFont(Theme.FONT_SMALL); title2.setForeground(Theme.TEXT_MUTED);
        lblTotalOrders = new JLabel("0"); lblTotalOrders.setFont(Theme.FONT_TITLE_LARGE); lblTotalOrders.setForeground(Theme.ACCENT);
        card2.add(title2); card2.add(lblTotalOrders);

        // Card 3: Avg Order size
        Theme.RoundedPanel card3 = new Theme.RoundedPanel(12, Theme.BG_SECONDARY);
        card3.setLayout(new GridLayout(2, 1, 5, 5));
        card3.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel title3 = new JLabel("AVERAGE ORDER VALUE"); title3.setFont(Theme.FONT_SMALL); title3.setForeground(Theme.TEXT_MUTED);
        lblAverageBill = new JLabel("INR 0.00"); lblAverageBill.setFont(Theme.FONT_TITLE_LARGE); lblAverageBill.setForeground(Theme.TEXT_PRIMARY);
        card3.add(title3); card3.add(lblAverageBill);

        cardsPanel.add(card1);
        cardsPanel.add(card2);
        cardsPanel.add(card3);
        tab.add(cardsPanel, BorderLayout.NORTH);

        // Center: Order list table
        String[] orderColumns = {"Order ID", "Customer Name", "Order Date", "Subtotal", "GST", "Total Amount"};
        ordersModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable = new JTable(ordersModel);
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        Theme.styleTable(ordersTable, scrollPane);

        // Set column widths
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        tab.add(scrollPane, BorderLayout.CENTER);

        // Bottom: Action Panel
        JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botPanel.setOpaque(false);
        JButton btnViewReceipt = new Theme.StyledButton("View Order Receipt", Theme.ACCENT, Theme.BG_PRIMARY, 8);
        btnViewReceipt.addActionListener(e -> handleViewReceipt());
        botPanel.add(btnViewReceipt);
        tab.add(botPanel, BorderLayout.SOUTH);

        return tab;
    }

    private void refreshOrdersData() {
        try {
            ordersList = DatabaseHelper.getAllOrders();
            ordersModel.setRowCount(0);

            double totalSales = 0.0;
            int totalOrders = ordersList.size();

            for (Order o : ordersList) {
                totalSales += o.getTotal();
                ordersModel.addRow(new Object[]{
                        o.getOrderId(),
                        o.getCustomerName() != null ? o.getCustomerName() : "Guest User",
                        o.getOrderDate(),
                        String.format("%.2f", o.getSubtotal()),
                        String.format("%.2f", o.getGst()),
                        String.format("%.2f", o.getTotal())
                });
            }

            double avgOrder = totalOrders > 0 ? (totalSales / totalOrders) : 0.0;

            // Update stats labels
            lblTotalRevenue.setText("INR " + String.format("%.2f", totalSales));
            lblTotalOrders.setText(String.valueOf(totalOrders));
            lblAverageBill.setText("INR " + String.format("%.2f", avgOrder));

        } catch (SQLException e) {
            showError("Failed to fetch order history: " + e.getMessage());
        }
    }

    private void handleViewReceipt() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow >= 0) {
            Order selectedOrder = ordersList.get(selectedRow);
            ReceiptDialog receipt = new ReceiptDialog(this, selectedOrder);
            receipt.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an order from the list first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
