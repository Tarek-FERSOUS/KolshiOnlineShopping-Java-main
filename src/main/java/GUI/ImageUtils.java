package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Utility class for loading and scaling images from resources.
 */
public class ImageUtils {
        /**
         * Load an image from resources and scale it to fit within the specified bounding box, preserving aspect ratio.
         * If the image is not found, returns a solid color placeholder.
         */
        public static ImageIcon loadImagePreserveAspect(String imagePath, int maxWidth, int maxHeight, Color placeholderColor) {
            try {
                URL imageUrl = ImageUtils.class.getResource("/images/" + imagePath);
                if (imageUrl != null) {
                    BufferedImage img = ImageIO.read(imageUrl);
                    if (img != null) {
                        int imgW = img.getWidth();
                        int imgH = img.getHeight();
                        double scale = Math.min((double) maxWidth / imgW, (double) maxHeight / imgH);
                        int newW = Math.max(1, (int) (imgW * scale));
                        int newH = Math.max(1, (int) (imgH * scale));
                        Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaled);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
            }
            // Return placeholder if image not found
            return createPlaceholderImage(maxWidth, maxHeight, placeholderColor);
        }
    
    /**
     * Load an image from resources and scale it to the specified dimensions.
     * If the image is not found, returns a solid color placeholder.
     */
    public static ImageIcon loadImage(String imagePath, int width, int height, Color placeholderColor) {
        try {
            URL imageUrl = ImageUtils.class.getResource("/images/" + imagePath);
            if (imageUrl != null) {
                BufferedImage img = ImageIO.read(imageUrl);
                if (img != null) {
                    Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
        }
        // Return placeholder if image not found
        return createPlaceholderImage(width, height, placeholderColor);
    }
    
    /**
     * Create a solid color placeholder image.
     */
    public static ImageIcon createPlaceholderImage(int width, int height, Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return new ImageIcon(img);
    }
    
    /**
     * Load a background image and tile it to fill a panel.
     */
    public static BufferedImage createTiledBackgroundImage(String imagePath, int panelWidth, int panelHeight) {
        try {
            URL imageUrl = ImageUtils.class.getResource("/images/" + imagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                Image img = icon.getImage();
                
                BufferedImage background = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = background.createGraphics();
                
                int tileWidth = img.getWidth(null);
                int tileHeight = img.getHeight(null);
                
                // Tile the image across the background
                for (int x = 0; x < panelWidth; x += tileWidth) {
                    for (int y = 0; y < panelHeight; y += tileHeight) {
                        g2d.drawImage(img, x, y, tileWidth, tileHeight, null);
                    }
                }
                g2d.dispose();
                return background;
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + imagePath + " - " + e.getMessage());
        }
        // Return solid color background if image not found
        BufferedImage fallback = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = fallback.createGraphics();
        g2d.setColor(UIColors.SURFACE);
        g2d.fillRect(0, 0, panelWidth, panelHeight);
        g2d.dispose();
        return fallback;
    }

    /**
     * Load a background image as an Image object for painting.
     */
    public static Image loadBackgroundImage(String imagePath) {
        try {
            URL imageUrl = ImageUtils.class.getResource("/images/" + imagePath);
            if (imageUrl != null) {
                BufferedImage img = ImageIO.read(imageUrl);
                if (img != null) {
                    return img;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading background image: " + imagePath + " - " + e.getMessage());
        }
        return null;
    }

    /**
     * Apply a blur effect to an image.
     */
    public static Image blurImage(Image img, int radius) {
        if (img == null) return null;
        
        BufferedImage buffered = new BufferedImage(
            img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = buffered.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();

        // Apply Gaussian blur using a simple kernel convolution
        int size = radius * 2 + 1;
        float[] kernel = new float[size * size];
        float sum = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                float val = (float) Math.exp(-((i - radius) * (i - radius) + (j - radius) * (j - radius)) / 
                            (2.0 * radius * radius));
                kernel[i * size + j] = val;
                sum += val;
            }
        }
        for (int i = 0; i < kernel.length; i++) kernel[i] /= sum;

        java.awt.image.ConvolveOp op = new java.awt.image.ConvolveOp(
            new java.awt.image.Kernel(size, size, kernel));
        BufferedImage blurred = op.filter(buffered, null);
        return blurred;
    }
}
