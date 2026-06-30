package restaurant.ui;

import restaurant.model.Order;
import restaurant.model.OrderItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Custom Swing Dialog that displays a beautifully formatted, printer-friendly bill receipt.
 */
public class ReceiptDialog extends JDialog {

    public ReceiptDialog(Frame parent, Order order) {
        super(parent, "Receipt - Order #" + order.getOrderId(), true);
        setSize(400, 550);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Monospace text area to display receipt
        JTextArea txtReceipt = new JTextArea();
        txtReceipt.setEditable(false);
        txtReceipt.setBackground(Theme.BG_SECONDARY);
        txtReceipt.setForeground(Theme.TEXT_PRIMARY);
        txtReceipt.setFont(Theme.FONT_MONO);
        txtReceipt.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Construct receipt content
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("          LUXE DINE RESTAURANT           \n");
        sb.append("      Taste the Premium Experience       \n");
        sb.append("=========================================\n");
        sb.append(String.format("Receipt ID : #%-24d\n", order.getOrderId()));
        sb.append(String.format("Date       : %-25s\n", order.getOrderDate()));
        sb.append(String.format("Customer   : %-25s\n", order.getCustomerName()));
        sb.append("-----------------------------------------\n");
        sb.append(String.format("%-22s %-5s %11s\n", "Item Name", "Qty", "Amount (INR)"));
        sb.append("-----------------------------------------\n");

        for (OrderItem item : order.getItems()) {
            String name = item.getMenuItem().getName();
            // Truncate name if it's too long
            if (name.length() > 21) {
                name = name.substring(0, 18) + "...";
            }
            sb.append(String.format("%-22s x%-4d %11.2f\n", 
                    name, 
                    item.getQuantity(), 
                    item.getSubtotal()));
        }

        sb.append("-----------------------------------------\n");
        sb.append(String.format("%-28s %11.2f\n", "Subtotal", order.getSubtotal()));
        sb.append(String.format("%-28s %11.2f\n", "GST (18%)", order.getGst()));
        sb.append("-----------------------------------------\n");
        sb.append(String.format("%-28s %11.2f\n", "GRAND TOTAL", order.getTotal()));
        sb.append("=========================================\n");
        sb.append("      Thank you! Visit us again soon.    \n");
        sb.append("=========================================\n");

        txtReceipt.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(txtReceipt);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0x3E3E52)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton printBtn = new Theme.StyledButton("Print", Theme.ACCENT, Theme.BG_PRIMARY, 8);
        JButton closeBtn = new Theme.StyledButton("Close", Theme.BG_TERTIARY, Theme.TEXT_PRIMARY, 8);

        controlPanel.add(printBtn);
        controlPanel.add(closeBtn);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Action Listeners
        closeBtn.addActionListener(e -> dispose());
        printBtn.addActionListener(e -> {
            try {
                boolean complete = txtReceipt.print();
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Receipt sent to printer.", "Printing Complete", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Printing failed: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
