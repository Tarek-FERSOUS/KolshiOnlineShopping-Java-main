package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FeedbackPage extends JPanel {
    private JTextArea feedbackTextArea;
    private JTextField emailField;
    private JComboBox<String> ratingCombo;
    private JButton submitBtn, clearBtn;

    public FeedbackPage() {
        setLayout(new BorderLayout());
        setBackground(UIColors.MUTED);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UIColors.MUTED);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel();
        titleLabel.setText("<html><span style='font-family:Segoe UI Emoji; font-size:20px;'>💬</span> <span style='font-family:Segoe UI; font-size:20px;'>Customer Feedback</span></html>");
        titleLabel.setForeground(UIColors.PRIMARY);

        JLabel descLabel = new JLabel("We'd love to hear from you! Share your feedback and help us improve.");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(UIColors.TEXT_SECONDARY);

        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(descLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(UIColors.SURFACE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Email field
        formPanel.add(createLabeledField("Email Address:", null));
        emailField = new JTextField(30);
        emailField.setFont(new Font("Arial", Font.PLAIN, 12));
        emailField.setPreferredSize(new Dimension(400, 30));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(20));

        // Rating field
        formPanel.add(createLabeledField("Overall Rating:", null));
        ratingCombo = new JComboBox<>(new String[]{"⭐ Excellent", "⭐⭐ Good", "⭐⭐⭐ Average", "⭐⭐⭐⭐ Poor"});
        ratingCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        ratingCombo.setPreferredSize(new Dimension(400, 30));
        formPanel.add(ratingCombo);
        formPanel.add(Box.createVerticalStrut(20));

        // Feedback text area
        formPanel.add(createLabeledField("Your Feedback:", null));
        feedbackTextArea = new JTextArea(10, 50);
        feedbackTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        feedbackTextArea.setLineWrap(true);
        feedbackTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
        formPanel.add(scrollPane);
        formPanel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(UIColors.SURFACE);

        submitBtn = new JButton("Submit Feedback");
        submitBtn.setFont(new Font("Arial", Font.BOLD, 12));
        submitBtn.setPreferredSize(new Dimension(150, 35));
        submitBtn.setBackground(UIColors.PRIMARY);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(e -> submitFeedback());

        clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.setPreferredSize(new Dimension(100, 35));
        clearBtn.addActionListener(e -> clearForm());

        buttonPanel.add(submitBtn);
        buttonPanel.add(clearBtn);
        formPanel.add(buttonPanel);

        // Scroll wrapper
        JScrollPane mainScroll = new JScrollPane(formPanel);
        mainScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(mainScroll, BorderLayout.CENTER);
    }

    private JLabel createLabeledField(String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(UIColors.TEXT_PRIMARY);
        return lbl;
    }

    private void submitFeedback() {
        String email = emailField.getText().trim();
        String rating = (String) ratingCombo.getSelectedItem();
        String feedback = feedbackTextArea.getText().trim();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email address!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (feedback.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your feedback!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save feedback to file
        saveFeedbackToFile(email, rating, feedback);

        JOptionPane.showMessageDialog(this, "Thank you for your feedback! 🙏\nWe appreciate your input and will use it to improve our service.", "Success", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
    }

    private void saveFeedbackToFile(String email, String rating, String feedback) {
        try (FileWriter fw = new FileWriter("feedback.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            out.println("=====================================");
            out.println("Timestamp: " + timestamp);
            out.println("Email: " + email);
            out.println("Rating: " + rating);
            out.println("Feedback: " + feedback);
            out.println("=====================================\n");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving feedback. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        emailField.setText("");
        ratingCombo.setSelectedIndex(0);
        feedbackTextArea.setText("");
    }
}
