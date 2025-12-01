package GUI;

import org.example.Product;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductGrid extends JPanel {
    private List<ProductCard> productCards = new ArrayList<>();
    private JPanel gridPanel;
    private JScrollPane scrollPane;
    private static final String[] CATEGORIES = {"All", "Electronics", "Clothing", "Books", "Home & Garden"};
    private Consumer<Product> addToCartHandler;

    public ProductGrid() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        gridPanel = new JPanel();
        // Use custom WrapLayout for flexbox-like behavior
        gridPanel.setLayout(new WrapLayout(WrapLayout.CENTER, 20, 20));
        gridPanel.setOpaque(false);
        gridPanel.setBackground(new Color(255, 255, 255, 0));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        scrollPane = new JScrollPane(gridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        add(scrollPane, BorderLayout.CENTER);
        setBackground(Color.WHITE);
    }

    public void addProductCard(ProductCard card) {
        productCards.add(card);
        gridPanel.add(card);
        // attach add-to-cart action if handler is set
        if (addToCartHandler != null) {
            card.getAddToCartButton().addActionListener(e -> addToCartHandler.accept(card.getProduct()));
        }
    }

    public void clearCards() {
        gridPanel.removeAll();
        productCards.clear();
    }

    public void displayProducts(List<Product> products, Runnable onSelectionChanged) {
        clearCards();
        for (Product product : products) {
            ProductCard card = new ProductCard(product, onSelectionChanged, this);
            addProductCard(card);
        }
        gridPanel.revalidate();
        gridPanel.repaint();
        
        // Reset scroll position to top so newly displayed products are visible
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    public void setAddToCartHandler(Consumer<Product> handler) {
        this.addToCartHandler = handler;
    }

    public List<ProductCard> getProductCards() {
        return new ArrayList<>(productCards);
    }

    public ProductCard getSelectedCard() {
        for (ProductCard card : productCards) {
            if (card.isCardSelected()) {
                return card;
            }
        }
        return null;
    }

    public void clearSelection() {
        for (ProductCard card : productCards) {
            if (card.isCardSelected()) {
                card.setSelected(false);
            }
        }
    }

    public void updateCardQuantities() {
        for (ProductCard card : productCards) {
            card.updateQuantity();
        }
    }
}
