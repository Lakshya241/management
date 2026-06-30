package restaurant.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Visual styling theme for the Restaurant Billing System.
 * Focuses on rich, modern, dark aesthetics with warm amber/gold accents.
 */
public class Theme {
    // Colors
    public static final Color BG_PRIMARY = new Color(0x121216);      // Very dark charcoal
    public static final Color BG_SECONDARY = new Color(0x1D1D26);    // Slate/Charcoal gray panel
    public static final Color BG_TERTIARY = new Color(0x282835);     // Card backgrounds
    
    public static final Color ACCENT = new Color(0xE0A96D);          // Elegant Warm Amber/Gold
    public static final Color ACCENT_HOVER = new Color(0xC7935B);    // Hover state
    public static final Color ACCENT_LIGHT = new Color(0xF5E6D3);    // Muted gold/white for selection
    
    public static final Color TEXT_PRIMARY = new Color(0xF5F5F7);    // Pure off-white
    public static final Color TEXT_MUTED = new Color(0x9A9AB0);      // Cool gray
    
    public static final Color SUCCESS = new Color(0x2ECC71);         // Emerald Green
    public static final Color ERROR = new Color(0xE74C3C);           // Crimson Red

    // Fonts
    public static final Font FONT_TITLE_LARGE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    /**
     * Styles a standard JButton to look modern and flat with rounded corners.
     */
    public static void styleButton(JButton button, Color bg, Color fg) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(FONT_BODY_BOLD);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bg == ACCENT ? ACCENT_HOVER : bg.brighter());
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
                button.repaint();
            }
        });
    }

    public static void stylePrimaryButton(JButton button) {
        styleButton(button, ACCENT, BG_PRIMARY);
    }

    public static void styleSecondaryButton(JButton button) {
        styleButton(button, BG_TERTIARY, TEXT_PRIMARY);
    }

    public static void styleDangerButton(JButton button) {
        styleButton(button, ERROR, TEXT_PRIMARY);
    }

    /**
     * Styles a JTextfield with a clean, dark background and thin bottom border.
     */
    public static void styleTextField(JTextField textField, String placeholder) {
        textField.setBackground(BG_TERTIARY);
        textField.setForeground(TEXT_PRIMARY);
        textField.setCaretColor(ACCENT);
        textField.setFont(FONT_BODY);
        textField.putClientProperty("JTextField.showClearButton", true);
        
        // Add subtle padding and custom border
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3E3E52), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Add focus listener for active borders
        textField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0x3E3E52), 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }

    /**
     * Styles JPasswordField similarly to JTextField.
     */
    public static void stylePasswordField(JPasswordField passwordField) {
        passwordField.setBackground(BG_TERTIARY);
        passwordField.setForeground(TEXT_PRIMARY);
        passwordField.setCaretColor(ACCENT);
        passwordField.setFont(FONT_BODY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3E3E52), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        passwordField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0x3E3E52), 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }

    /**
     * Styles a JComboBox for modern appearance.
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(BG_TERTIARY);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setFont(FONT_BODY);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(0x3E3E52), 1));
        // Simple cell renderer for dark theme
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setBackground(isSelected ? ACCENT : BG_TERTIARY);
                l.setForeground(isSelected ? BG_PRIMARY : TEXT_PRIMARY);
                l.setBorder(new EmptyBorder(5, 10, 5, 10));
                return l;
            }
        });
    }

    /**
     * Styles a JTable to look clean and neat, removing default grey grids.
     */
    public static void styleTable(JTable table, JScrollPane scrollPane) {
        table.setBackground(BG_SECONDARY);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setGridColor(new Color(0x282835));
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(BG_PRIMARY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setShowVerticalLines(false);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_TERTIARY);
        header.setForeground(ACCENT);
        header.setFont(FONT_BODY_BOLD);
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x3E3E52)));

        // Column Alignment & Padding Renderer
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    JLabel l = (JLabel) c;
                    l.setBorder(new EmptyBorder(0, 10, 0, 10));
                    if (column == 0 || column == 3 || column == 4) {
                        l.setHorizontalAlignment(SwingConstants.CENTER);
                    } else {
                        l.setHorizontalAlignment(SwingConstants.LEFT);
                    }
                }
                
                // Zebra stripes
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BG_SECONDARY : BG_TERTIARY);
                }
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // ScrollPane styling
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0x282835)));
        scrollPane.getViewport().setBackground(BG_SECONDARY);
        scrollPane.getVerticalScrollBar().setBackground(BG_SECONDARY);
        scrollPane.getHorizontalScrollBar().setBackground(BG_SECONDARY);
    }

    // Custom Rounded Panel Class
    public static class RoundedPanel extends JPanel {
        private int cornerRadius = 15;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bg) {
            super();
            cornerRadius = radius;
            backgroundColor = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draws the rounded panel with borders.
            if (backgroundColor != null) {
                graphics.setColor(backgroundColor);
            } else {
                graphics.setColor(getBackground());
            }
            graphics.fill(new RoundRectangle2D.Float(0, 0, width - 1, height - 1, arcs.width, arcs.height));
        }
    }

    // Custom Styled Button with Rounded Border Painting
    public static class StyledButton extends JButton {
        private Color bg;
        private Color fg;
        private int radius;

        public StyledButton(String text, Color bg, Color fg, int radius) {
            super(text);
            this.bg = bg;
            this.fg = fg;
            this.radius = radius;
            styleButton(this, bg, fg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
