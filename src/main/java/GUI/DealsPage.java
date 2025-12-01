package GUI;

import org.example.Product;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DealsPage extends JPanel {
    private ArrayList<Product> products;
    private ProductGrid productGrid;

    public DealsPage(ArrayList<Product> products) {
        this.products = products;
        setLayout(new BorderLayout());
        setBackground(UIColors.SURFACE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UIColors.MUTED);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel();
        titleLabel.setText("<html><span style='font-family:Segoe UI Emoji; font-size:22px;'>💰</span> <span style='font-family:Segoe UI; font-size:22px;'>Special Deals &amp; Offers</span></html>");
        titleLabel.setForeground(UIColors.DANGER);

        JLabel descLabel = new JLabel("Exclusive discounts and limited-time offers on selected items!");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(UIColors.TEXT_SECONDARY);

        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(descLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Deals info panel
        JPanel dealsInfoPanel = new JPanel();
        dealsInfoPanel.setBackground(UIColors.HOVER_LIGHT);
        dealsInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIColors.DANGER, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel dealsLabel = new JLabel("<html>🎉 <b>Current Offers:</b><br>" +
            "• Buy 3+ Electronics items → Get 20% OFF<br>" +
            "• Buy 3+ Clothing items → Get 20% OFF<br>" +
            "• First Purchase → Get 10% OFF all items<br>" +
            "• Subscribe to newsletter → Extra 5% OFF</html>");
        dealsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        dealsInfoPanel.add(dealsLabel);
        add(dealsInfoPanel, BorderLayout.NORTH);

        // Products Grid - Show all products as they all could have deals
        productGrid = new ProductGrid();
        productGrid.displayProducts(products, () -> {});

        add(productGrid, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(UIColors.HOVER_LIGHT);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel footerLabel = new JLabel();
        footerLabel.setText("<html><span style='font-family:Segoe UI Emoji; font-size:12px;'>⏰</span> <span style='font-family:Segoe UI; font-size:12px;'>Offers valid while stocks last. Don't miss out!</span></html>");
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerPanel.add(footerLabel);

        add(footerPanel, BorderLayout.SOUTH);
    }

    public ProductGrid getProductGrid() {
        return productGrid;
    }
}
