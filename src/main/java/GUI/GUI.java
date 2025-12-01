package GUI;

import org.example.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GUI extends JPanel implements ActionListener {
    // Background handled externally now
    private User currentUser;

    private ArrayList<Product> products;
    private ShoppingCart shoppingCart;
    private Runnable cartChangeListener;
    private JLabel selectProductCategoryLabel, productDetailsLabel, selectProductLabel;
    private JComboBox<String> selectionBox;
    private JButton viewCartBtn, addToCart;
    private ProductGrid productGrid;
    private JPanel detailsPanel;

    public GUI(ArrayList<Product> products, User currentUser, org.example.ShoppingCart sharedCart) {
        this.products = products;
        this.currentUser = currentUser;
        this.shoppingCart = (sharedCart != null) ? sharedCart : new org.example.ShoppingCart();

        setLayout(new BorderLayout());
        setOpaque(false); // transparent; shared background shows through

        // Top panel with category selector and buttons
        JPanel topPanel = new JPanel(new BorderLayout(20, 13));
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 12, 20));
        // ...existing code...
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(new Color(240, 240, 240));
        selectProductCategoryLabel = new JLabel("Filter by Category:");
        selectProductCategoryLabel.setFont(new Font("Arial", Font.BOLD, 12));
        leftPanel.add(selectProductCategoryLabel);
        selectionBox = new JComboBox<>(new String[]{"All", "Electronics", "Clothing", "Books", "Home & Garden"});
        selectionBox.setSelectedItem("All");
        selectionBox.addActionListener(this);
        // ...existing code...
    



        selectionBox.setPreferredSize(new Dimension(150, 30));
        leftPanel.add(selectionBox);
        
        topPanel.add(leftPanel, BorderLayout.WEST);

        // Right side - shopping cart button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setBackground(new Color(240, 240, 240));
        
        viewCartBtn = new JButton("Shopping Cart");
        viewCartBtn.setFont(new Font("Arial", Font.BOLD, 12));
        viewCartBtn.addActionListener(this);
        viewCartBtn.setPreferredSize(new Dimension(140, 35));
        rightPanel.add(viewCartBtn);
        
        topPanel.add(rightPanel, BorderLayout.EAST);

        // Product grid
        productGrid = new ProductGrid();
        displayProductsInGrid("All");

        // Details panel
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(new Color(250, 250, 250, 200));
        detailsPanel.setOpaque(true);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        detailsPanel.setPreferredSize(new Dimension(250, 200));

        productDetailsLabel = new JLabel("Selected Product - Details");
        productDetailsLabel.setFont(new Font("Arial", Font.BOLD, 12));

        selectProductLabel = new JLabel("<html>Select a product to view details</html>");
        selectProductLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        selectProductLabel.setVerticalAlignment(JLabel.TOP);

        addToCart = new JButton("Add to Cart");
        addToCart.setFont(new Font("Arial", Font.BOLD, 12));
        addToCart.setMaximumSize(new Dimension(200, 35));
        addToCart.setAlignmentX(Component.CENTER_ALIGNMENT);
        addToCart.addActionListener(this);

        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(productDetailsLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(selectProductLabel);
        detailsPanel.add(Box.createVerticalGlue());
        detailsPanel.add(addToCart);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Main split panel - responsive with 80/20 split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, productGrid, detailsPanel);
        splitPane.setResizeWeight(0.8);
        splitPane.setDividerLocation(0.8);
        splitPane.setOpaque(false);
        
    add(topPanel, BorderLayout.NORTH);
    add(splitPane, BorderLayout.CENTER);
    }

/*hna kanet l'erreur ta3 override dakhl constructor */
    // Background painting removed; handled by BackgroundPanel



    public ProductGrid getProductGrid() {
        return productGrid;
    }

    public void refreshProducts() {
        displayProductsInGrid("All");
    }

    private void displayProductsInGrid(String category) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        
        for (Product product : products) {
            if (category.equalsIgnoreCase("All") || 
                product.getProductCategory().equalsIgnoreCase(category)) {
                filteredProducts.add(product);
            }
        }

        productGrid.displayProducts(filteredProducts, () -> updateDetailsPanel());
        
        // Attach action listeners to add-to-cart buttons in cards
        for (ProductCard card : productGrid.getProductCards()) {
            card.getAddToCartButton().addActionListener(e -> {
                if (card.isCardSelected()) {
                    handleAddToCart(card.getProduct());
                }
            });
        }
    }

    public void updateDetailsPanel() {
        ProductCard selectedCard = productGrid.getSelectedCard();
        if (selectedCard != null) {
            Product product = selectedCard.getProduct();
            String details = generateProductDetails(product);
            selectProductLabel.setText(details);
        } else {
            selectProductLabel.setText("<html>Select a product to view details</html>");
        }
    }

    private String generateProductDetails(Product product) {
        String category = product.getProductCategory();
        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<b>Product ID:</b> ").append(product.getProductID()).append("<br/>");
        sb.append("<b>Name:</b> ").append(product.getProductName()).append("<br/>");
        sb.append("<b>Category:</b> ").append(category).append("<br/>");
        sb.append("<b>Price:</b> DZD ").append(String.format("%.2f", product.getPrice())).append("<br/>");
        
        if (category.equalsIgnoreCase("Electronics")) {
            Electronics electronics = (Electronics) product;
            sb.append("<b>Brand:</b> ").append(electronics.getBrand()).append("<br/>");
            sb.append("<b>Warranty:</b> ").append(electronics.getWarrantyPeriod()).append(" months<br/>");
        } else if (category.equalsIgnoreCase("Clothing")) {
            Clothing clothing = (Clothing) product;
            sb.append("<b>Size:</b> ").append(clothing.getSize()).append("<br/>");
            sb.append("<b>Color:</b> ").append(clothing.getColor()).append("<br/>");
        }
        
        sb.append("<b>Stock:</b> ").append(product.getQuantity()).append("<br/>");
        sb.append("</html>");
        return sb.toString();
    }

    private void handleAddToCart(Product product) {
        String input = JOptionPane.showInputDialog("Enter Quantity:");
        
        if (input != null && !input.isEmpty()) {
            try {
                int quantity = Integer.parseInt(input);
                
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(null, "Quantity must be greater than 0!");
                    return;
                }
                
                if (product.getQuantity() < quantity) {
                    JOptionPane.showMessageDialog(null, "Insufficient stock! Available: " + product.getQuantity());
                    return;
                }
                
                product.decreaseQuantity(quantity);
                shoppingCart.addProduct(product, quantity);
                productGrid.updateCardQuantities();
                if (cartChangeListener != null) cartChangeListener.run();
                
                JOptionPane.showMessageDialog(null, product.getProductName() + " added to cart!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number!");
            }
        }
    }

    public void shoppingCartFrame() {
        JFrame frame = new JFrame("Shopping Cart");
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<String> cartModel = new DefaultListModel<>();

        double total = 0;
        boolean threeItems = false;
        boolean firstPurchase = false;
        int electronicsCount = 0;
        int clothingCount = 0;
        double discount = 0;

        HashMap<Product, Integer> map = shoppingCart.getProducts();

        for (Map.Entry<Product, Integer> entry : map.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            String itemStr = product.getProductID() + " - " + product.getProductName() + " (x" + quantity + ") - DZD " + String.format("%.2f", quantity * product.getPrice());
            cartModel.addElement(itemStr);
            total += (quantity * product.getPrice());

            if (product.getProductCategory().equalsIgnoreCase("Electronics")) {
                electronicsCount += quantity;
            } else if (product.getProductCategory().equalsIgnoreCase("Clothing")) {
                clothingCount += quantity;
            }

            if (electronicsCount >= 3 || clothingCount >= 3) {
                threeItems = true;
            }

            if (currentUser.getPurchaseCount() < 1) {
                firstPurchase = true;
            }
        }

        JList<String> cartList = new JList<>(cartModel);
        cartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(cartList);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Order Summary"));

        JLabel totalLabel = new JLabel("Total: DZD " + String.format("%.2f", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 13));
        summaryPanel.add(totalLabel);

        if (threeItems) {
            discount = (total * 0.20);
            JLabel discountLabel = new JLabel("3+ Same Category Discount (20%): -DZD " + String.format("%.2f", discount));
            discountLabel.setForeground(new Color(0, 128, 0));
            summaryPanel.add(discountLabel);
        }

        if (firstPurchase) {
            double firstPurchaseDiscount = (total * 0.10);
            discount = Math.max(discount, firstPurchaseDiscount);
            JLabel firstPurchaseLabel = new JLabel("First Purchase Discount (10%): -DZD " + String.format("%.2f", firstPurchaseDiscount));
            firstPurchaseLabel.setForeground(new Color(0, 128, 0));
            summaryPanel.add(firstPurchaseLabel);
        }

        JLabel finalLabel = new JLabel("Final Total: DZD " + String.format("%.2f", (total - discount)));
        finalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        finalLabel.setForeground(new Color(0, 102, 204));
        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(finalLabel);

        panel.add(summaryPanel, BorderLayout.SOUTH);

        // Checkout button
        JButton checkOutBtn = new JButton("Checkout");
        checkOutBtn.setFont(new Font("Arial", Font.BOLD, 13));
        checkOutBtn.setBackground(new Color(0, 128, 0));
        checkOutBtn.setForeground(Color.WHITE);
        checkOutBtn.addActionListener(e -> {
            clearShoppingCart();
            incrementUserPurchaseCount();
            JOptionPane.showMessageDialog(frame, "Checkout successful!\nYour purchase count has been updated.");
            frame.dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(checkOutBtn);
        panel.add(buttonPanel, BorderLayout.NORTH);

        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewCartBtn) {
            shoppingCartFrame();
        } else if (e.getSource() == addToCart) {
            ProductCard selectedCard = productGrid.getSelectedCard();
            if (selectedCard != null) {
                handleAddToCart(selectedCard.getProduct());
            } else {
                JOptionPane.showMessageDialog(null, "Please select a product first!");
            }
        } else if (e.getActionCommand().equalsIgnoreCase("comboBoxChanged")) {
            String category = (String) selectionBox.getSelectedItem();
            displayProductsInGrid(category);
            productGrid.clearSelection();
            selectProductLabel.setText("<html>Select a product to view details</html>");
        }
    }

    private void clearShoppingCart() {
        shoppingCart.clearCart();
        if (cartChangeListener != null) cartChangeListener.run();
    }

    public void setCartChangeListener(Runnable listener) {
        this.cartChangeListener = listener;
    }

    private void incrementUserPurchaseCount() {
        currentUser.incrementPurchaseCount();
    }
    public JPanel getMainPanel() {
        return this;
    }
}
