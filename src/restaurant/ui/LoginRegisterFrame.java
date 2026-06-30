package restaurant.ui;

import restaurant.database.DatabaseHelper;
import restaurant.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Modern login and registration page using Java Swing and custom dark styling.
 * Uses CardLayout to transition between Login and Signup modes.
 */
public class LoginRegisterFrame extends JFrame {
    private JPanel mainCardPanel;
    private CardLayout cardLayout;

    // Login components
    private JTextField loginUserField;
    private JPasswordField loginPassField;

    // Register components
    private JTextField regUserField;
    private JPasswordField regPassField;
    private JPasswordField regConfirmPassField;
    private JTextField regNameField;
    private JTextField regPhoneField;

    public LoginRegisterFrame() {
        setTitle("Bite & Bill - Login / Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main background panel
        JPanel bgPanel = new JPanel(new BorderLayout());
        bgPanel.setBackground(Theme.BG_PRIMARY);
        bgPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setOpaque(false);

        // Create card views
        mainCardPanel.add(createLoginPanel(), "LOGIN");
        mainCardPanel.add(createRegisterPanel(), "REGISTER");

        bgPanel.add(mainCardPanel, BorderLayout.CENTER);
        add(bgPanel);

        // Default to Login view
        cardLayout.show(mainCardPanel, "LOGIN");
    }

    /**
     * Creates the login screen view.
     */
    private JPanel createLoginPanel() {
        Theme.RoundedPanel panel = new Theme.RoundedPanel(20, Theme.BG_SECONDARY);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Logo / Title
        JLabel brandLabel = new JLabel("BITE & BILL", SwingConstants.CENTER);
        brandLabel.setFont(Theme.FONT_TITLE_LARGE);
        brandLabel.setForeground(Theme.ACCENT);
        gbc.gridy = 0;
        panel.add(brandLabel, gbc);

        JLabel subLabel = new JLabel("Restaurant Billing System", SwingConstants.CENTER);
        subLabel.setFont(Theme.FONT_SMALL);
        subLabel.setForeground(Theme.TEXT_MUTED);
        gbc.gridy = 1;
        panel.add(subLabel, gbc);

        // Spacer
        gbc.gridy = 2;
        panel.add(Box.createVerticalStrut(30), gbc);

        // Username Field
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(Theme.FONT_BODY_BOLD);
        userLabel.setForeground(Theme.TEXT_PRIMARY);
        gbc.gridy = 3;
        panel.add(userLabel, gbc);

        loginUserField = new JTextField();
        Theme.styleTextField(loginUserField, "Enter username");
        gbc.gridy = 4;
        panel.add(loginUserField, gbc);

        // Password Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.FONT_BODY_BOLD);
        passLabel.setForeground(Theme.TEXT_PRIMARY);
        gbc.gridy = 5;
        panel.add(passLabel, gbc);

        loginPassField = new JPasswordField();
        Theme.stylePasswordField(loginPassField);
        gbc.gridy = 6;
        panel.add(loginPassField, gbc);

        // Spacer
        gbc.gridy = 7;
        panel.add(Box.createVerticalStrut(25), gbc);

        // Login Button
        JButton loginBtn = new Theme.StyledButton("Log In", Theme.ACCENT, Theme.BG_PRIMARY, 10);
        gbc.gridy = 8;
        panel.add(loginBtn, gbc);

        // Toggle to Register Button
        JButton toRegBtn = new JButton("<html>Don't have an account? <font color='#E0A96D'><b>Register here</b></font></html>");
        toRegBtn.setFont(Theme.FONT_SMALL);
        toRegBtn.setForeground(Theme.TEXT_MUTED);
        toRegBtn.setBorderPainted(false);
        toRegBtn.setContentAreaFilled(false);
        toRegBtn.setFocusPainted(false);
        toRegBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 9;
        panel.add(toRegBtn, gbc);

        // Listeners
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        toRegBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainCardPanel, "REGISTER");
            }
        });

        return panel;
    }

    /**
     * Creates the registration screen view.
     */
    private JPanel createRegisterPanel() {
        Theme.RoundedPanel panel = new Theme.RoundedPanel(20, Theme.BG_SECONDARY);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Title
        JLabel registerTitle = new JLabel("Create Account", SwingConstants.CENTER);
        registerTitle.setFont(Theme.FONT_HEADER);
        registerTitle.setForeground(Theme.ACCENT);
        gbc.gridy = 0;
        panel.add(registerTitle, gbc);

        // Username
        gbc.gridy = 1;
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(Theme.FONT_BODY_BOLD);
        userLabel.setForeground(Theme.TEXT_PRIMARY);
        panel.add(userLabel, gbc);

        regUserField = new JTextField();
        Theme.styleTextField(regUserField, "Choose a username");
        gbc.gridy = 2;
        panel.add(regUserField, gbc);

        // Full Name
        gbc.gridy = 3;
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(Theme.FONT_BODY_BOLD);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        panel.add(nameLabel, gbc);

        regNameField = new JTextField();
        Theme.styleTextField(regNameField, "Your full name");
        gbc.gridy = 4;
        panel.add(regNameField, gbc);

        // Phone
        gbc.gridy = 5;
        JLabel phoneLabel = new JLabel("Phone Number");
        phoneLabel.setFont(Theme.FONT_BODY_BOLD);
        phoneLabel.setForeground(Theme.TEXT_PRIMARY);
        panel.add(phoneLabel, gbc);

        regPhoneField = new JTextField();
        Theme.styleTextField(regPhoneField, "10-digit number");
        gbc.gridy = 6;
        panel.add(regPhoneField, gbc);

        // Password
        gbc.gridy = 7;
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.FONT_BODY_BOLD);
        passLabel.setForeground(Theme.TEXT_PRIMARY);
        panel.add(passLabel, gbc);

        regPassField = new JPasswordField();
        Theme.stylePasswordField(regPassField);
        gbc.gridy = 8;
        panel.add(regPassField, gbc);

        // Confirm Password
        gbc.gridy = 9;
        JLabel confirmPassLabel = new JLabel("Confirm Password");
        confirmPassLabel.setFont(Theme.FONT_BODY_BOLD);
        confirmPassLabel.setForeground(Theme.TEXT_PRIMARY);
        panel.add(confirmPassLabel, gbc);

        regConfirmPassField = new JPasswordField();
        Theme.stylePasswordField(regConfirmPassField);
        gbc.gridy = 10;
        panel.add(regConfirmPassField, gbc);

        // Spacer
        gbc.gridy = 11;
        panel.add(Box.createVerticalStrut(15), gbc);

        // Register Button
        JButton registerBtn = new Theme.StyledButton("Register", Theme.ACCENT, Theme.BG_PRIMARY, 10);
        gbc.gridy = 12;
        panel.add(registerBtn, gbc);

        // Back to login button
        JButton toLoginBtn = new JButton("<html>Already have an account? <font color='#E0A96D'><b>Log In</b></font></html>");
        toLoginBtn.setFont(Theme.FONT_SMALL);
        toLoginBtn.setForeground(Theme.TEXT_MUTED);
        toLoginBtn.setBorderPainted(false);
        toLoginBtn.setContentAreaFilled(false);
        toLoginBtn.setFocusPainted(false);
        toLoginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 13;
        panel.add(toLoginBtn, gbc);

        // Listeners
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        toLoginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainCardPanel, "LOGIN");
            }
        });

        return panel;
    }

    /**
     * Executes credentials verification and loads respective dashboard.
     */
    private void handleLogin() {
        String username = loginUserField.getText().trim();
        String password = new String(loginPassField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and Password fields cannot be empty.");
            return;
        }

        try {
            User user = DatabaseHelper.authenticate(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this,
                        "Welcome back, " + user.getFullName() + "!",
                        "Login Successful", JOptionPane.INFORMATION_MESSAGE);

                if ("ADMIN".equals(user.getRole())) {
                    // Open Admin dashboard
                    SwingUtilities.invokeLater(() -> new AdminFrame(user).setVisible(true));
                } else {
                    // Open Customer ordering panel
                    SwingUtilities.invokeLater(() -> new CustomerFrame(user).setVisible(true));
                }
                this.dispose(); // Close login frame
            } else {
                showError("Invalid username or password. Please try again.");
            }
        } catch (SQLException e) {
            showError("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Processes input validation and calls the DB helper to insert a new user.
     */
    private void handleRegistration() {
        String username = regUserField.getText().trim();
        String fullName = regNameField.getText().trim();
        String phone = regPhoneField.getText().trim();
        String password = new String(regPassField.getPassword()).trim();
        String confirmPassword = new String(regConfirmPassField.getPassword()).trim();

        // 1. Validation
        if (username.isEmpty() || fullName.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters.");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            showError("Phone number must be a valid 10-digit number.");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            // Check if username taken
            if (DatabaseHelper.isUsernameTaken(username)) {
                showError("Username '" + username + "' is already taken. Please choose another.");
                return;
            }

            // Register customer
            boolean success = DatabaseHelper.registerCustomer(username, password, fullName, phone);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Registration successful! You can now log in.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear fields
                regUserField.setText("");
                regNameField.setText("");
                regPhoneField.setText("");
                regPassField.setText("");
                regConfirmPassField.setText("");

                // Switch to login card
                cardLayout.show(mainCardPanel, "LOGIN");
            } else {
                showError("Registration failed. Please check your inputs.");
            }

        } catch (SQLException e) {
            showError("Database registration error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Custom styled error message dialog.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}
