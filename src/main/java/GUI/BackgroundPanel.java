package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * A panel that paints a shared background image (blurred or clear) behind all pages.
 * Pages (cards) are added as children with setOpaque(false) to avoid individual background painting flicker.
 */
public class BackgroundPanel extends JPanel {
    private Image clearImage;
    private Image blurredImage;
    private boolean useBlur = false;

    public BackgroundPanel(Image clear, Image blurred) {
        this.clearImage = clear;
        this.blurredImage = blurred;
        setLayout(new BorderLayout());
        setOpaque(true); // we paint our own background fully
    }

    public void setBlurred(boolean blurred) {
        this.useBlur = blurred;
        repaint();
    }

    public void setBlurredImage(Image blurred) {
        this.blurredImage = blurred;
        if (useBlur) repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image img = useBlur ? blurredImage : clearImage;
        if (img != null) {
            int w = getWidth();
            int h = getHeight();
            g.drawImage(img, 0, 0, w, h, this);
        } else {
            g.setColor(UIColors.SURFACE);
            g.fillRect(0,0,getWidth(),getHeight());
        }
    }
}