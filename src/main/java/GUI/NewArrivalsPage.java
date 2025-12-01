package GUI;

import org.example.Product;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class NewArrivalsPage extends JPanel {
    private ArrayList<Product> products;
    private ProductGrid productGrid;

    public NewArrivalsPage(ArrayList<Product> products) {
        this.products = products;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBackground(new Color(0,0,0,0));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UIColors.MUTED);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel();
        titleLabel.setText("<html><span style='font-family:Segoe UI Emoji; font-size:22px;'>🆕</span> <span style='font-family:Segoe UI; font-size:22px;'>New Arrivals</span></html>");
        titleLabel.setForeground(UIColors.PRIMARY);

        JLabel descLabel = new JLabel("Check out our latest products added to our store!");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(UIColors.TEXT_SECONDARY);

        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(descLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Products Grid
        productGrid = new ProductGrid();
        ArrayList<Product> sortedProducts = new ArrayList<>(products);
        // Sort by ID (latest products typically have higher IDs)
        sortedProducts.sort((a, b) -> b.getProductID().compareTo(a.getProductID()));
        productGrid.displayProducts(sortedProducts, () -> {});

        add(productGrid, BorderLayout.CENTER);

        // Info message
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(UIColors.HOVER_LIGHT);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel infoLabel = new JLabel();
        infoLabel.setText("<html><span style='font-family:Segoe UI Emoji; font-size:12px;'>ℹ️</span> <span style='font-family:Segoe UI; font-size:12px;'>These are our most recently added products!</span></html>");
        infoPanel.add(infoLabel);

        add(infoPanel, BorderLayout.SOUTH);
    }

    public ProductGrid getProductGrid() {
        return productGrid;
    }

    // Shared background painted by BackgroundPanel
}
