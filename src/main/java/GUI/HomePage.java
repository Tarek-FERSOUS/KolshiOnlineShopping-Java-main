package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class HomePage extends JPanel {
    private JButton startShoppingBtn;

    public HomePage(Runnable navigationCallback) {
        setLayout(new BorderLayout());
        setOpaque(false); // allow shared background to show
        setBackground(new Color(0,0,0,0));

        // Left panel to hold content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100));

        // Logo / App Name
        JLabel appNameLabel = new JLabel("Kolshi Shopping Center");
        appNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        appNameLabel.setForeground(UIColors.PRIMARY);
        appNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(appNameLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to your favorite online shopping destination!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        welcomeLabel.setForeground(UIColors.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Tagline
        JLabel taglineLabel = new JLabel("Discover amazing products with great deals and fast delivery");
        taglineLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        taglineLabel.setForeground(UIColors.TEXT_SECONDARY);
        taglineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(taglineLabel);
        contentPanel.add(Box.createVerticalStrut(40));

        // Start Shopping Button (centered)
        startShoppingBtn = new JButton("Start Shopping");
        startShoppingBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        startShoppingBtn.setPreferredSize(new Dimension(250, 50));
        startShoppingBtn.setMaximumSize(new Dimension(250, 50));
        startShoppingBtn.setBackground(UIColors.PRIMARY);
        startShoppingBtn.setForeground(Color.WHITE);
        startShoppingBtn.setFocusPainted(false);
        startShoppingBtn.setBorderPainted(false);
        startShoppingBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startShoppingBtn.addActionListener(e -> {
            if (navigationCallback != null) navigationCallback.run();
        });
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(startShoppingBtn);
        buttonRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(buttonRow);

        // Reduce gap to bring features up
        contentPanel.add(Box.createVerticalStrut(8));
        JPanel featuresPanel = createFeaturesPanel();
        featuresPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(featuresPanel);
        contentPanel.add(Box.createVerticalGlue());

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createFeaturesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3, 10, 0));
        panel.setBackground(new Color(0,0,0,0));
        panel.setPreferredSize(new Dimension(150, 30));

        // Feature 1
        JPanel feature1 = createFeatureBox("📦", "Wide Selection", "Browse thousands of products\nacross multiple categories");
        panel.add(feature1);

        // Feature 2
        JPanel feature2 = createFeatureBox("💰", "Best Prices", "Get special discounts and\ndeals on your favorite items");
        panel.add(feature2);

        // Feature 3
        JPanel feature3 = createFeatureBox("🚚", "Fast Delivery", "Quick and reliable shipping\nto your doorstep");
        panel.add(feature3);

        return panel;
    }

    private JPanel createFeatureBox(String emoji, String title, String description) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(255,255,255,210));
        box.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
        box.setPreferredSize(new Dimension(90, 30));
        // Render emoji and title separately so emoji uses emoji font at plain weight
        JLabel titleLabel = new JLabel();
        String html = String.format("<html><span style='font-family:%s; font-size:13px;'>%s</span> <span style='font-family:%s; font-size:12px;'>%s</span></html>",
            "Segoe UI Emoji", emoji, "Segoe UI", title);
        titleLabel.setText(html);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(Box.createVerticalStrut(4));
        box.add(titleLabel);
        box.add(Box.createVerticalStrut(4));
        box.add(descLabel);
        box.add(Box.createVerticalStrut(4));

        return box;
    }

    // Background now handled by BackgroundPanel
}
