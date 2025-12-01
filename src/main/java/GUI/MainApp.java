package GUI;

import org.example.Product;
import org.example.User;

import com.asprise.ocr.sample.util.RoundedBorder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.example.ShoppingCart;

public class MainApp extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private HomePage homePage;
    private GUI shoppingPageGui;
    private NewArrivalsPage newArrivalsPage;
    private DealsPage dealsPage;
    private FeedbackPage feedbackPage;
    private JPanel navigationPanel;
    private JButton activeNavButton;
    private Map<String, JButton> navButtons = new HashMap<>();
    private JTextField searchField;
    private JComboBox<String> sortBox;
    private JComboBox<String> categoryFilterBox;
    private JLabel cartSummaryLabel;
    private JButton cartButton;
    private JLabel cartBadge;
    private JPopupMenu cartPopupMenu;
    private ArrayList<Product> products;
    private User currentUser;
    private ShoppingCart sharedCart;

    public MainApp(ArrayList<Product> products, User currentUser) {
        this.products = products;
        this.currentUser = currentUser;

        // Apply a global UI font so the app looks consistent.
        // Use Segoe UI for general text; emoji-specific components will use Segoe UI Emoji.
        applyGlobalFont(new Font("Segoe UI", Font.PLAIN, 13));

        setTitle("Kolshi Shopping Center");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Background + cards (unified to reduce flicker)
        Image baseBg = ImageUtils.loadBackgroundImage("backgrounds/bg1.jpg");
        BackgroundPanel backgroundPanel = new BackgroundPanel(baseBg, null);
        // Compute blur off-EDT to avoid slow launch
        new javax.swing.SwingWorker<Image, Void>() {
            @Override protected Image doInBackground() {
                return ImageUtils.blurImage(baseBg, 8);
            }
            @Override protected void done() {
                try { backgroundPanel.setBlurredImage(get()); } catch (Exception ignored) {}
            }
        }.execute();
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        backgroundPanel.add(cardPanel, BorderLayout.CENTER);

        // Create shared cart and all pages
        sharedCart = new ShoppingCart();
        homePage = new HomePage(() -> navigateTo("shopping"));
        shoppingPageGui = new GUI(products, currentUser, sharedCart);
        newArrivalsPage = new NewArrivalsPage(products);
        dealsPage = new DealsPage(products);
        feedbackPage = new FeedbackPage();

        // Add pages to card panel
        cardPanel.add(homePage, "home");
        cardPanel.add(shoppingPageGui.getMainPanel(), "shopping");
        cardPanel.add(newArrivalsPage, "arrivals");
        cardPanel.add(dealsPage, "deals");
        cardPanel.add(feedbackPage, "feedback");

        // Wire add-to-cart handlers so all pages use the shared cart
        shoppingPageGui.getProductGrid().setAddToCartHandler(product -> handleAddToCart(product, sharedCart));
        newArrivalsPage.getProductGrid().setAddToCartHandler(product -> handleAddToCart(product, sharedCart));
        dealsPage.getProductGrid().setAddToCartHandler(product -> handleAddToCart(product, sharedCart));

        // Refresh displays so handlers attach to created cards
        shoppingPageGui.refreshProducts();
        // Lazy load arrivals and deals when first opened to reduce startup cost

        // Create navigation
        navigationPanel = createNavigationPanel();

        // Wire cart change callback so nav summary updates when cart changes
        shoppingPageGui.setCartChangeListener(() -> updateCartSummary(sharedCart));
        // initial summary
        updateCartSummary(sharedCart);

        // Add to frame
        add(navigationPanel, BorderLayout.NORTH);
        add(backgroundPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private boolean arrivalsLoaded = false;
    private boolean dealsLoaded = false;

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(UIColors.NAVBAR_BG);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        navPanel.setPreferredSize(new Dimension(0, 65));

        // Left: logo + main nav buttons
        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        leftNav.setOpaque(false);
        JLabel logo = new JLabel("Kolshi");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        leftNav.add(logo);
        JButton homeBtn = createNavButton("🏠", "home", "Home");
        JButton shoppingBtn = createNavButton("🛍️", "shopping", "Shop");
        JButton arrivalsBtn = createNavButton("🆕", "arrivals", "Arrivals");
        JButton dealsBtn = createNavButton("💰", "deals", "Deals");
        JButton feedbackBtn = createNavButton("💬", "feedback", "Feedback");
        // register buttons so programmatic navigation can update the active highlight
        navButtons.put("home", homeBtn);
        navButtons.put("shopping", shoppingBtn);
        navButtons.put("arrivals", arrivalsBtn);
        navButtons.put("deals", dealsBtn);
        navButtons.put("feedback", feedbackBtn);
        leftNav.add(homeBtn);
        leftNav.add(shoppingBtn);
        leftNav.add(arrivalsBtn);
        leftNav.add(dealsBtn);
        leftNav.add(feedbackBtn);
        setActiveNavButton(homeBtn); // mark home active

        // Center: search, category, sort
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        centerPanel.setOpaque(false);
        searchField = new JTextField(14);
        // placeholder behavior
        String placeholder = "Search products...";
        searchField.setText(placeholder);
        searchField.setForeground(new Color(255, 255, 255, 90));
        // apply a pill-shaped rounded border (use invokeLater so this wins over later setBorder calls)
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.border.Border rounded = new RoundedButtonBorder(18, new Color(255,255,255,180));
            javax.swing.border.Border padding = BorderFactory.createEmptyBorder(5, 10, 5, 10);
            searchField.setBorder(BorderFactory.createCompoundBorder(rounded, padding));
            searchField.setOpaque(false);
            searchField.setBackground(new Color(255,255,255,230));
            searchField.setCaretColor(Color.WHITE);
        });
        // Use the compound/line+empty border set later; avoid dependency on an undefined RoundedBorder constructor
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setForeground(new Color(120, 120, 120));
                    searchField.setText(placeholder);
                }
            }
        });
        searchField.setMaximumSize(new Dimension(220, 30));
        // Rounded border and padding for cleaner look on navbar
        javax.swing.border.Border defaultSearchBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        );
        javax.swing.border.Border focusSearchBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIColors.ACCENT, 2, true),
            BorderFactory.createEmptyBorder(5, 9, 5, 9)
        );
        searchField.setBorder(defaultSearchBorder);
        searchField.setBackground(new Color(255,255,255));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { searchField.setBorder(focusSearchBorder); }
            @Override public void focusLost(java.awt.event.FocusEvent e) { searchField.setBorder(defaultSearchBorder); }
        });
        sortBox = new JComboBox<>(new String[]{"Default", "Price: Low → High", "Price: High → Low", "Name A-Z", "Name Z-A"});
        categoryFilterBox = new JComboBox<>(new String[]{"All", "Electronics", "Clothing", "Books", "Home & Garden"});
        categoryFilterBox.setPreferredSize(new Dimension(110, 26));
        sortBox.setPreferredSize(new Dimension(110, 26));
        JLabel searchLabel = new JLabel("🔎");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        centerPanel.add(searchLabel);
        centerPanel.add(searchField);
        JLabel catLabel = new JLabel("Category:");
        catLabel.setForeground(Color.WHITE);
        catLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        centerPanel.add(catLabel);
        centerPanel.add(categoryFilterBox);
        JLabel sortLabel = new JLabel("Sort:");
        sortLabel.setForeground(Color.WHITE);
        sortLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        centerPanel.add(sortLabel);
        centerPanel.add(sortBox);

        // Right: user and cart summary
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        rightPanel.setOpaque(false);
        cartSummaryLabel = new JLabel();
        cartSummaryLabel.setForeground(Color.WHITE);
        cartSummaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cartSummaryLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Create cart popup for hover mini-cart
        cartPopupMenu = new JPopupMenu();
        cartPopupMenu.setFocusable(false);
        
        // Cart button as icon with badge overlay
        cartButton = new JButton("🛒");
        cartButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        cartButton.setFocusPainted(false);
        cartButton.setContentAreaFilled(false);
        cartButton.setBackground(new Color(0, 102, 204));
        cartButton.setForeground(Color.WHITE);
        cartButton.setBorder(new RoundedButtonBorder(18, new Color(0,0,0,40)));
        cartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cartButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { cartButton.setBackground(new Color(0, 120, 220)); cartButton.repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { cartButton.setBackground(new Color(0, 102, 204)); cartButton.repaint(); }
            @Override public void mousePressed(java.awt.event.MouseEvent e) { cartButton.setBackground(new Color(0, 90, 185)); cartButton.repaint(); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { cartButton.setBackground(new Color(0, 120, 220)); cartButton.repaint(); }
        });
        cartButton.addActionListener(e -> {
            cartPopupMenu.setVisible(false);
            shoppingPageGui.shoppingCartFrame();
        });
        cartButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                updateMiniCartPopup();
                cartPopupMenu.show(cartButton, 0, cartButton.getHeight());
            }
        });
        
        // Improved popup dismiss logic - use Timer instead of complex mouse tracking
        javax.swing.Timer dismissTimer = new javax.swing.Timer(5000, e -> cartPopupMenu.setVisible(false));
        dismissTimer.setRepeats(false);
        
        cartPopupMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                dismissTimer.stop();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                dismissTimer.restart();
            }
        });
        
        cartButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                dismissTimer.restart();
            }
        });

        // Create a small layered panel so we can overlay a badge on the cart button
        JPanel cartPanel = new JPanel(null);
        cartPanel.setOpaque(false);
        Dimension btnSize = new Dimension(90, 36);
        cartButton.setPreferredSize(btnSize);
        cartButton.setBounds(0, 0, btnSize.width, btnSize.height);
        cartPanel.setPreferredSize(new Dimension(btnSize.width + 12, btnSize.height + 8));

        cartBadge = new JLabel();
        cartBadge.setHorizontalAlignment(SwingConstants.CENTER);
        cartBadge.setVerticalAlignment(SwingConstants.CENTER);
        cartBadge.setForeground(Color.WHITE);
        cartBadge.setOpaque(true);
        cartBadge.setBackground(UIColors.DANGER);
        cartBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        cartBadge.setBorder(BorderFactory.createLineBorder(UIColors.DANGER, 1));
        cartBadge.setVisible(false);
        // small circular badge size
        int b = 20;
        // Shift badge slightly left and a bit down for better balance
        cartBadge.setBounds(btnSize.width - 14, 0, b, b);
        cartBadge.setPreferredSize(new Dimension(b, b));

        cartPanel.add(cartButton);
        cartPanel.add(cartBadge);

        rightPanel.add(cartSummaryLabel);
        rightPanel.add(cartPanel);

        navPanel.add(leftNav, BorderLayout.WEST);
        navPanel.add(centerPanel, BorderLayout.CENTER);
        navPanel.add(rightPanel, BorderLayout.EAST);

        // Listeners to trigger filtering
        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilters(); }
        };
        searchField.getDocument().addDocumentListener(dl);
        sortBox.addActionListener(e -> applyFilters());
        categoryFilterBox.addActionListener(e -> applyFilters());

        return navPanel;
    }

    private JButton createNavButton(String text, String page) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // use PLAIN to avoid size change on selection
        btn.setBackground(UIColors.SURFACE);
        btn.setOpaque(true);
        btn.setForeground(UIColors.PRIMARY);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(6, 12, 6, 12));
        btn.setPreferredSize(new Dimension(105, 32)); // consistent size
        btn.addActionListener(e -> { navigateTo(page); });
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(UIColors.HOVER_LIGHT); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { if (btn != activeNavButton) btn.setBackground(UIColors.SURFACE); }
        });
        return btn;
    }

    // Overload that accepts a separate emoji and label so we can render emoji with
    // an emoji-capable font at plain weight while keeping the label in the UI font.
    private JButton createNavButton(String emoji, String page, String label) {
        // Use HTML so we can assign different fonts to emoji and text.
        String html = String.format("<html>" +
            "<span style='font-family:%s; font-size:13px;'>%s</span> " +
            "<span style='font-family:%s; font-size:11px;'>%s</span>" +
            "</html>", "Segoe UI Emoji", emoji, "Segoe UI", label);
        JButton btn = new JButton(html);
        // High-contrast pill: light surface with slight transparency outline on primary navbar
        btn.setBackground(new Color(255,255,255,235));
        btn.setForeground(new Color(255,255,255,70));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setMargin(new Insets(6, 10, 6, 10));
        btn.setPreferredSize(new Dimension(110, 34));
        btn.setBorder(new RoundedButtonBorder(16, new Color(255,255,255,70)));
        btn.addActionListener(e -> { navigateTo(page); });
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(250,250,250)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { if (btn != activeNavButton) btn.setBackground(new Color(255,255,255,235)); }
            @Override public void mousePressed(java.awt.event.MouseEvent e) { btn.setBackground(new Color(235,235,235)); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { btn.setBackground(new Color(250,250,250)); }
        });
        return btn;
    }

    private void setActiveNavButton(JButton btn) {
        if (activeNavButton != null) {
            // reset previous button to default rounded border (consistent contrast in all states)
            activeNavButton.setBorder(new RoundedButtonBorder(16, new Color(255,255,255,70)));
        }
        activeNavButton = btn;
        // Keep the same pill background; add an accent underline while preserving the rounded border
        activeNavButton.setBorder(BorderFactory.createCompoundBorder(
                new RoundedButtonBorder(16, new Color(255,255,255,170)),
                BorderFactory.createMatteBorder(0, 0, 3, 0, UIColors.ACCENT)
        ));
    }

    private void switchPage(String page) {
        cardLayout.show(cardPanel, page);
        // Blur for all except home
        Container parent = cardPanel.getParent();
        if (parent instanceof BackgroundPanel bp) {
            bp.setBlurred(!"home".equals(page));
        }
        // Lazy build product grids for arrivals and deals on first visit
        if ("arrivals".equals(page) && !arrivalsLoaded) {
            newArrivalsPage.getProductGrid().displayProducts(products, () -> {});
            arrivalsLoaded = true;
        } else if ("deals".equals(page) && !dealsLoaded) {
            dealsPage.getProductGrid().displayProducts(products, () -> {});
            dealsLoaded = true;
        }
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    /**
     * Navigate to a page and update the navbar highlighted button.
     */
    private void navigateTo(String page) {
        // show the requested card
        switchPage(page);
        // update active button if we have a mapping
        JButton btn = navButtons.get(page);
        if (btn != null) setActiveNavButton(btn);
    }

    private void updateCartSummary(ShoppingCart cart) {
        int items = 0;
        double total = 0.0;
        for (java.util.Map.Entry<org.example.Product, Integer> e : cart.getProducts().entrySet()) {
            items += e.getValue();
            total += e.getKey().getPrice() * e.getValue();
        }
        cartSummaryLabel.setText(String.format("Cart: %d items | %d DZD", items, (int)total));
        // update badge with animation
        if (cartBadge != null) {
            if (items > 0) {
                String txt = items > 99 ? "99+" : String.valueOf(items);
                cartBadge.setText(txt);
                if (!cartBadge.isVisible()) {
                    cartBadge.setVisible(true);
                    animateBadge();
                }
                // ensure circular look
                cartBadge.setBorder(BorderFactory.createLineBorder(new Color(200,30,30)));
            } else {
                cartBadge.setVisible(false);
            }
        }
    }

    private void animateBadge() {
        // Smooth scale/pop animation using Timer
        final float[] scale = {0.5f}; // Start at 50% size
        final javax.swing.Timer timer = new javax.swing.Timer(10, null);
        timer.addActionListener(e -> {
            scale[0] += 0.1f; // Increment scale by 10% each frame
            if (scale[0] >= 1.0f) {
                scale[0] = 1.0f; // Cap at 100%
                timer.stop();
            }
            // Update font size based on scale (10pt to 12pt)
            int fontSize = (int)(10 + (scale[0] * 2));
            cartBadge.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        });
        timer.start();
    }

    private void updateMiniCartPopup() {
        cartPopupMenu.removeAll();
        if (sharedCart.getProducts().isEmpty()) {
            JMenuItem empty = new JMenuItem("Cart is empty");
            empty.setEnabled(false);
            cartPopupMenu.add(empty);
        } else {
            java.util.List<java.util.Map.Entry<org.example.Product, Integer>> items = 
                new ArrayList<>(sharedCart.getProducts().entrySet());
            int toShow = Math.min(5, items.size());
            for (int i = 0; i < toShow; i++) {
                java.util.Map.Entry<org.example.Product, Integer> e = items.get(i);
                String label = String.format("%s x%d (%d DZD)", 
                    e.getKey().getProductName(), 
                    e.getValue(), 
                    (int)(e.getKey().getPrice() * e.getValue()));
                JMenuItem item = new JMenuItem(label);
                item.setEnabled(false);
                cartPopupMenu.add(item);
            }
            if (items.size() > 5) {
                cartPopupMenu.addSeparator();
                JMenuItem more = new JMenuItem("... " + (items.size() - 5) + " more items");
                more.setEnabled(false);
                cartPopupMenu.add(more);
            }
            cartPopupMenu.addSeparator();
            JMenuItem viewAll = new JMenuItem("View Full Cart");
            viewAll.addActionListener(e -> {
                cartPopupMenu.setVisible(false);
                shoppingPageGui.shoppingCartFrame();
            });
            cartPopupMenu.add(viewAll);
        }
    }

    private void applyFilters() {
        // Get search text and check if it's the placeholder
        String searchText = searchField.getText().trim();
        String placeholder = "Search products...";
        // If it's the placeholder text or empty, treat as no search
        if (searchText.isEmpty() || searchText.equals(placeholder)) {
            searchText = "";
        }
        String search = searchText.toLowerCase();
        String sort = (String) sortBox.getSelectedItem();
        String category = (String) categoryFilterBox.getSelectedItem();
        java.util.List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            if (!category.equalsIgnoreCase("All") && !p.getProductCategory().equalsIgnoreCase(category)) continue;
            if (!search.isEmpty()) {
                String hay = (p.getProductName() + " " + p.getProductID()).toLowerCase();
                if (!hay.contains(search)) continue;
            }
            filtered.add(p);
        }
        // Sorting
        switch (sort) {
            case "Price: Low → High":
                filtered.sort((a,b) -> Double.compare(a.getPrice(), b.getPrice()));
                break;
            case "Price: High → Low":
                filtered.sort((a,b) -> Double.compare(b.getPrice(), a.getPrice()));
                break;
            case "Name A-Z":
                filtered.sort((a,b) -> a.getProductName().compareToIgnoreCase(b.getProductName()));
                break;
            case "Name Z-A":
                filtered.sort((a,b) -> b.getProductName().compareToIgnoreCase(a.getProductName()));
                break;
            default:
                break;
        }

        // Update only the main shopping page grid with the details panel callback
        // The callback will trigger updateDetailsPanel() in GUI.java
        shoppingPageGui.getProductGrid().displayProducts(filtered, () -> shoppingPageGui.updateDetailsPanel());
    }

    private void handleAddToCart(Product product, ShoppingCart cart) {
        String input = JOptionPane.showInputDialog(this, "Enter quantity to add:", "1");
        if (input == null || input.isEmpty()) return;
        try {
            int qty = Integer.parseInt(input.trim());
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0");
                return;
            }
            if (product.getQuantity() < qty) {
                JOptionPane.showMessageDialog(this, "Insufficient stock. Available: " + product.getQuantity());
                return;
            }
            product.decreaseQuantity(qty);
            cart.addProduct(product, qty);
            // update grids
            shoppingPageGui.getProductGrid().updateCardQuantities();
            newArrivalsPage.getProductGrid().updateCardQuantities();
            dealsPage.getProductGrid().updateCardQuantities();
            updateCartSummary(cart);
            animateBadge(); // animate on add
            JOptionPane.showMessageDialog(this, product.getProductName() + " added to cart (x" + qty + ")");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number");
        }
    }

    public JPanel getMainPanel() {
        return cardPanel;
    }

    // Helper to set a global font for all UI components that respect UI defaults.
    private void applyGlobalFont(Font f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, f);
            }
        }
        // Also set common specific defaults
        UIManager.put("Label.font", f);
        UIManager.put("Button.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("Table.font", f);
        UIManager.put("List.font", f);
    }

    /**
     * Rounded button border for navbar buttons.
     */
    private static class RoundedButtonBorder extends javax.swing.border.AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedButtonBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 12, 6, 12);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 12;
            insets.top = insets.bottom = 6;
            return insets;
        }
    }
}
