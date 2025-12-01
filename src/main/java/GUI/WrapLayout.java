package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * FlowLayout subclass that fully supports wrapping of components like CSS flexbox.
 * Components wrap to the next line when they don't fit in the available width.
 */
public class WrapLayout extends FlowLayout {
    private Dimension preferredLayoutSize;

    /**
     * Constructs a new WrapLayout with a default 5-unit horizontal and vertical gap.
     */
    public WrapLayout() {
        super();
    }

    /**
     * Constructs a new WrapLayout with the specified alignment and a default 5-unit
     * horizontal and vertical gap.
     * @param align the alignment
     */
    public WrapLayout(int align) {
        super(align);
    }

    /**
     * Constructs a new WrapLayout with the specified alignment and gaps.
     * @param align the alignment
     * @param hgap the horizontal gap
     * @param vgap the vertical gap
     */
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getWidth();
            Container container = target;

            while (container.getSize().width == 0 && container.getParent() != null) {
                container = container.getParent();
            }

            int maxWidth = container.getWidth();
            if (maxWidth == 0) {
                maxWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGaps = insets.left + insets.right + (hgap * 2);
            int maxWidthWithoutInsets = maxWidth - horizontalInsetsAndGaps;

            int x = 0;
            int y = insets.top + vgap;
            int rowHeight = 0;

            int maxHeight = insets.top + insets.bottom + vgap * 2;

            int componentCount = target.getComponentCount();

            for (int i = 0; i < componentCount; i++) {
                Component m = target.getComponent(i);

                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (x == 0) {
                        maxHeight += d.height;
                    }

                    if (x + d.width > maxWidthWithoutInsets && x != 0) {
                        x = 0;
                        y += vgap + rowHeight;
                        rowHeight = 0;
                        maxHeight += vgap + d.height;
                    }

                    x += hgap + d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }

            return new Dimension(maxWidth, maxHeight);
        }
    }

    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int maxWidth = target.getWidth() - (insets.left + insets.right + getHgap() * 2);
            int maxHeight = target.getHeight() - (insets.top + insets.bottom + getVgap() * 2);

            if (maxWidth <= 0 || maxHeight <= 0) {
                return;
            }

            int align = getAlignment();
            int hgap = getHgap();
            int vgap = getVgap();

            Component[] components = target.getComponents();
            int x = insets.left + hgap;
            int y = insets.top + vgap;
            int rowMaxHeight = 0;
            int rowStart = 0;

            for (int i = 0; i < components.length; i++) {
                Component m = components[i];
                if (!m.isVisible()) {
                    continue;
                }

                Dimension d = m.getPreferredSize();

                // Check if we need to wrap to next row
                if (x + d.width > target.getWidth() - (insets.right + hgap) && x != insets.left + hgap) {
                    // Align current row
                    alignRow(target, components, rowStart, i, x, y, maxWidth, align, hgap, insets);
                    y += vgap + rowMaxHeight;
                    x = insets.left + hgap;
                    rowMaxHeight = 0;
                    rowStart = i;
                }

                x += d.width + hgap;
                rowMaxHeight = Math.max(rowMaxHeight, d.height);
            }

            // Align last row
            alignRow(target, components, rowStart, components.length, x, y, maxWidth, align, hgap, insets);
        }
    }

    private void alignRow(Container target, Component[] components, int rowStart, int rowEnd,
                          int x, int y, int maxWidth, int align, int hgap, Insets insets) {
        int rowWidth = 0;
        for (int i = rowStart; i < rowEnd; i++) {
            Component m = components[i];
            if (m.isVisible()) {
                rowWidth += m.getPreferredSize().width + hgap;
            }
        }
        rowWidth -= hgap;

        int rowX = insets.left + hgap;
        switch (align) {
            case LEFT:
                rowX = insets.left + hgap;
                break;
            case CENTER:
                rowX = insets.left + hgap + (maxWidth - rowWidth) / 2;
                break;
            case RIGHT:
                rowX = insets.left + hgap + (maxWidth - rowWidth);
                break;
        }

        for (int i = rowStart; i < rowEnd; i++) {
            Component m = components[i];
            if (m.isVisible()) {
                Dimension d = m.getPreferredSize();
                m.setBounds(rowX, y, d.width, d.height);
                rowX += d.width + hgap;
            }
        }
    }
}
