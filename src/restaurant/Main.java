package restaurant;

import restaurant.database.DatabaseHelper;
import restaurant.ui.LoginRegisterFrame;

import javax.swing.*;

/**
 * Application entry point for the Restaurant Billing System.
 */
public class Main {
    public static void main(String[] args) {
        // Set system look and feel for native elements
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        // Initialize Database tables and default records (Admin, initial Menu items)
        DatabaseHelper.initializeDatabase();

        // Launch the application GUI on the Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(() -> {
            LoginRegisterFrame loginFrame = new LoginRegisterFrame();
            loginFrame.setVisible(true);
        });
    }
}
