package GUI;

import org.example.Product;
import org.example.Electronics;
import org.example.Clothing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProductCard extends JPanel {
    private Product product;
    private ProductGrid parentGrid;
    private FadeImageLabel imageLabel;
    private ImageIcon frontImage;
    private ImageIcon sideImage;
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JLabel categoryLabel;
    private JLabel quantityLabel;
    private JButton addToCartBtn;
    private boolean isSelected = false;
    private boolean isHovered = false;
    public Color defaultBgColor = UIColors.SURFACE;
    private Color selectedBgColor = UIColors.MUTED;
    private Runnable onSelectionChanged;

    // Rounded corners and shadow painting for the whole card
    @Override
    protected void paintComponent(Graphics g) {
        int arc = 12;
        int shadowOffset = isHovered ? 12 : 6; // increase shadow on hover (lift effect)
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // shadow - more pronounced on hover
        float shadowAlpha = isHovered ? 0.2f : 0.12f;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, shadowAlpha));
        g2.setColor(UIColors.SHADOW);
        g2.fillRoundRect(shadowOffset, shadowOffset, getWidth() - shadowOffset * 2, getHeight() - shadowOffset * 2, arc, arc);

        // background
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - shadowOffset, getHeight() - shadowOffset, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }

    public ProductCard(Product product, Runnable onSelectionChanged) {
        this(product, onSelectionChanged, null);
    }

    public ProductCard(Product product, Runnable onSelectionChanged, ProductGrid parentGrid) {
        this.product = product;
        this.parentGrid = parentGrid;
        this.onSelectionChanged = onSelectionChanged;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // background will be set after we detect category so each card gets its own color
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Set responsive sizing - preferred size with max/min constraints
        Dimension cardSize = new Dimension(200, 320);
        setPreferredSize(cardSize);
        setMaximumSize(new Dimension(250, 350));
        setMinimumSize(new Dimension(180, 300));

        // Image - try to load both front and side view, fallback to colored emoji placeholder
        imageLabel = new FadeImageLabel(180, 120);
        String category = product.getProductCategory().toLowerCase();
        Color categoryColor;
        String categoryEmoji;

        switch(category) {
            case "electronics":
                categoryColor = new Color(100, 150, 200);
                categoryEmoji = "📱";
                // light blue card
                defaultBgColor = new Color(235, 245, 255);
                selectedBgColor = new Color(210, 230, 255);
                break;
            case "clothing":
                categoryColor = new Color(200, 150, 100);
                categoryEmoji = "👕";
                // light peach card
                defaultBgColor = new Color(255, 245, 235);
                selectedBgColor = new Color(255, 235, 215);
                break;
            case "books":
                categoryColor = new Color(150, 100, 80);
                categoryEmoji = "📚";
                // light tan card
                defaultBgColor = new Color(250, 245, 240);
                selectedBgColor = new Color(240, 235, 225);
                break;
            case "home & garden":
                categoryColor = new Color(100, 180, 100);
                categoryEmoji = "🏠";
                // light green card
                defaultBgColor = new Color(235, 255, 235);
                selectedBgColor = new Color(215, 245, 215);
                break;
            default:
                categoryColor = new Color(150, 150, 150);
                categoryEmoji = "📦";
                defaultBgColor = UIColors.SURFACE;
                selectedBgColor = UIColors.MUTED;
        }
        setBackground(defaultBgColor);

        // Load both front and side images
        String basePath = category.replace(" & ", "-") + "/" + product.getProductID().toLowerCase();
        String frontPath = basePath + ".png";
        String sidePath = basePath + "_.png";
        frontImage = ImageUtils.loadImagePreserveAspect(frontPath, 180, 120, categoryColor);
        sideImage = ImageUtils.loadImagePreserveAspect(sidePath, 180, 120, categoryColor);

        boolean hasFront = frontImage != null && frontImage.getImage().getWidth(null) > 1;
        boolean hasSide = sideImage != null && sideImage.getImage().getWidth(null) > 1;

        if (hasFront) {
            imageLabel.setFrontImage(frontImage.getImage());
        }
        if (hasSide) {
            imageLabel.setSideImage(sideImage.getImage());
        }

        if (!hasFront && !hasSide) {
            // Fallback to colored emoji placeholder
            imageLabel.setPlaceholder(categoryEmoji, categoryColor);
        }
        
        imageLabel.setPreferredSize(new Dimension(180, 120));
        imageLabel.setMaximumSize(new Dimension(180, 120));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Product name
        nameLabel = new JLabel(product.getProductName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setMaximumSize(new Dimension(180, 40));

        // Product price
        priceLabel = new JLabel(String.format("%d DZD", (int)product.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLabel.setForeground(new Color(0, 128, 0));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Category
        categoryLabel = new JLabel(product.getProductCategory());
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        categoryLabel.setForeground(new Color(100, 100, 100));
        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Quantity available
        quantityLabel = new JLabel("Stock: " + product.getQuantity());
        quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        quantityLabel.setForeground(new Color(100, 100, 100));
        quantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add to cart button
        addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addToCartBtn.setMaximumSize(new Dimension(160, 34));
        addToCartBtn.setBackground(UIColors.PRIMARY);
        addToCartBtn.setForeground(Color.WHITE);
        addToCartBtn.setFocusPainted(false);
        addToCartBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addToCartBtn.setBorder(new RoundedBorder(14));
        addToCartBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { addToCartBtn.setBackground(UIColors.ACCENT); }
            @Override public void mouseExited(MouseEvent e) { addToCartBtn.setBackground(UIColors.PRIMARY); }
        });

        // Add spacing and components
        add(Box.createVerticalStrut(8));
        add(imageLabel);
        add(Box.createVerticalStrut(8));
        add(nameLabel);
        add(Box.createVerticalStrut(4));
        add(priceLabel);
        add(Box.createVerticalStrut(2));
        add(categoryLabel);
        add(quantityLabel);
        add(Box.createVerticalStrut(8));
        add(addToCartBtn);
        add(Box.createVerticalStrut(8));

        // Mouse listener for selection and hover
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                if (!isSelected) {
                    setBackground(new Color(250, 250, 250));
                }
                imageLabel.fadeToSide();
                repaint(); // repaint to show lift shadow
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                if (!isSelected) {
                    setBackground(defaultBgColor);
                }
                imageLabel.fadeToFront();
                repaint(); // repaint to reset shadow
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setSelected(!isSelected);
            }
        });
    }

    public Product getProduct() {
        return product;
    }

    public JButton getAddToCartButton() {
        return addToCartBtn;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        setBackground(selected ? selectedBgColor : defaultBgColor);
        setBorder(selected 
            ? BorderFactory.createLineBorder(new Color(0, 102, 204), 2)
            : BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        // If selected and we have a parent grid, deselect all other cards
        if (selected && parentGrid != null) {
            for (ProductCard card : parentGrid.getProductCards()) {
                if (card != this && card.isCardSelected()) {
                    card.isSelected = false;
                    card.setBackground(card.defaultBgColor);
                    card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                }
            }
        }
        
        if (onSelectionChanged != null) {
            onSelectionChanged.run();
        }
    }

    public boolean isCardSelected() {
        return isSelected;
    }

    public void updateQuantity() {
        quantityLabel.setText("Stock: " + product.getQuantity());
        quantityLabel.repaint();
    }

    // Custom component that draws two images and crossfades between them.
    private static class FadeImageLabel extends JComponent {
        private Image front;
        private Image side;
        private String emoji;
        private Color placeholderColor;
        private boolean showPlaceholder = false;
        private float alpha = 0f; // 0 => front, 1 => side
        private javax.swing.Timer animTimer;
        private float target = 0f;
        private final int boxW;
        private final int boxH;

        FadeImageLabel(int w, int h) {
            this.boxW = w;
            this.boxH = h;
            setPreferredSize(new Dimension(w, h));
            setMinimumSize(new Dimension(w, h));
            setOpaque(false);
        }

        void setFrontImage(Image img) {
            this.front = img;
            this.showPlaceholder = false;
            repaint();
        }

        void setSideImage(Image img) {
            this.side = img;
            repaint();
        }

        void setPlaceholder(String emoji, Color color) {
            this.emoji = emoji;
            this.placeholderColor = color;
            this.showPlaceholder = true;
            repaint();
        }

        void fadeToSide() {
            startAnimation(1f);
        }

        void fadeToFront() {
            startAnimation(0f);
        }

        private void startAnimation(float t) {
            this.target = t;
            if (animTimer != null && animTimer.isRunning()) animTimer.stop();
            animTimer = new javax.swing.Timer(15, e -> {
                float step = 0.08f;
                if (Math.abs(alpha - target) <= step) {
                    alpha = target;
                    animTimer.stop();
                } else if (alpha < target) {
                    alpha += step;
                } else {
                    alpha -= step;
                }
                repaint();
            });
            animTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (showPlaceholder || front == null) {
                // Draw emoji centered on transparent background (no shadow)
                if (emoji != null) {
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, Math.min(h / 2, 36)));
                    FontMetrics fm = g2.getFontMetrics();
                    int tw = fm.stringWidth(emoji);
                    int th = fm.getAscent();
                    g2.setColor(new Color(60, 60, 60));
                    g2.drawString(emoji, (w - tw) / 2, (h + th) / 2 - 6);
                }
                g2.dispose();
                return;
            }

        // Draw front image with (1 - alpha) so it fades out
        if (front != null && alpha < 1f) {
            int iw = front.getWidth(null);
            int ih = front.getHeight(null);
            if (iw > 0 && ih > 0) {
                int x = (w - iw) / 2;
                int y = (h - ih) / 2;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - alpha));
                g2.drawImage(front, x, y, iw, ih, null);
                g2.setComposite(AlphaComposite.SrcOver);
            }
        }

        // Draw side image with alpha blend on top
        if (side != null && alpha > 0f) {
            int iw = side.getWidth(null);
            int ih = side.getHeight(null);
            if (iw > 0 && ih > 0) {
                int x = (w - iw) / 2;
                int y = (h - ih) / 2;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.drawImage(side, x, y, iw, ih, null);
                g2.setComposite(AlphaComposite.SrcOver);
            }
        }

            g2.dispose();
        }
    }

    // Simple rounded border used for buttons
    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final int radius;
        RoundedBorder(int radius) { this.radius = radius; }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0,0,0,40));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 8;
            insets.top = insets.bottom = 4;
            return insets;
        }
    }
}
