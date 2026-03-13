package client_ttnn.hcmute.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * A reusable claymorphism panel:
 * - pastel fill
 * - large rounded corners
 * - soft outer drop shadow
 * - double inner shadows (top-left highlight + bottom-right depth)
 *
 * Painting is resolution independent and resizes gracefully.
 */
public class ClayPanel extends JPanel {
    private Color fill = new Color(0xE0E5EC);
    private int arc = 28;

    // Outer shadow
    private int shadowSize = 18;      // "blur" thickness
    private int elevation = 7;        // offset down-right
    private Color shadowColor = new Color(120, 130, 145, 70);
    /**
     * If true, the panel reports extra insets so children are laid out away from the shadow area.
     * For sidebars/navigation lists this often feels like everything is "pushed in", so it can be disabled.
     */
    private boolean reserveShadowInsets = true;

    // Inner shadows
    private int insetSize = 14;
    private Color insetHighlight = new Color(255, 255, 255, 210);
    private Color insetShadow = new Color(160, 170, 185, 140);

    // Optional soft stroke to help edges on bright backgrounds
    private boolean drawSoftStroke = true;
    private Color strokeColor = new Color(255, 255, 255, 140);

    public ClayPanel() {
        setOpaque(false);
    }

    public ClayPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public ClayPanel setFill(Color fill) {
        if (fill != null) this.fill = fill;
        repaint();
        return this;
    }

    public Color getFill() {
        return fill;
    }

    public ClayPanel setArc(int arc) {
        this.arc = Math.max(0, arc);
        repaint();
        return this;
    }

    public int getArc() {
        return arc;
    }

    public ClayPanel setShadowSize(int shadowSize) {
        this.shadowSize = Math.max(0, shadowSize);
        revalidate();
        repaint();
        return this;
    }

    public int getShadowSize() {
        return shadowSize;
    }

    public ClayPanel setElevation(int elevation) {
        this.elevation = Math.max(0, elevation);
        revalidate();
        repaint();
        return this;
    }

    public int getElevation() {
        return elevation;
    }

    public ClayPanel setShadowColor(Color shadowColor) {
        if (shadowColor != null) this.shadowColor = shadowColor;
        repaint();
        return this;
    }

    public ClayPanel setReserveShadowInsets(boolean reserveShadowInsets) {
        this.reserveShadowInsets = reserveShadowInsets;
        revalidate();
        repaint();
        return this;
    }

    public ClayPanel setInsetSize(int insetSize) {
        this.insetSize = Math.max(0, insetSize);
        repaint();
        return this;
    }

    public ClayPanel setInsetHighlight(Color insetHighlight) {
        if (insetHighlight != null) this.insetHighlight = insetHighlight;
        repaint();
        return this;
    }

    public ClayPanel setInsetShadow(Color insetShadow) {
        if (insetShadow != null) this.insetShadow = insetShadow;
        repaint();
        return this;
    }

    public ClayPanel setDrawSoftStroke(boolean drawSoftStroke) {
        this.drawSoftStroke = drawSoftStroke;
        repaint();
        return this;
    }

    public ClayPanel setStrokeColor(Color strokeColor) {
        if (strokeColor != null) this.strokeColor = strokeColor;
        repaint();
        return this;
    }

    @Override
    public Insets getInsets() {
        Insets base = super.getInsets();
        if (!reserveShadowInsets) return base;
        int pad = Math.max(0, shadowSize + elevation);
        return new Insets(
            base.top + pad,
            base.left + pad,
            base.bottom + pad,
            base.right + pad
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            int pad = Math.max(0, shadowSize + elevation);
            float x = pad;
            float y = pad;
            float rw = Math.max(1, w - (pad * 2f));
            float rh = Math.max(1, h - (pad * 2f));

            Shape baseShape = new RoundRectangle2D.Float(x, y, rw, rh, arc, arc);

            paintOuterShadow(g2, baseShape, x, y);

            g2.setPaint(fill);
            g2.fill(baseShape);

            paintInnerShadows(g2, baseShape, x, y, rw, rh);

            if (drawSoftStroke) {
                g2.setColor(strokeColor);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(baseShape);
            }
        } finally {
            g2.dispose();
        }

        super.paintComponent(g);
    }

    private void paintOuterShadow(Graphics2D g2, Shape baseShape, float x, float y) {
        if (shadowSize <= 0) return;

        Rectangle2D bounds = baseShape.getBounds2D();
        float rw = (float) bounds.getWidth();
        float rh = (float) bounds.getHeight();

        int steps = Math.max(8, Math.min(18, shadowSize));
        for (int i = steps; i >= 1; i--) {
            float t = i / (float) steps; // 1..0
            int alpha = Math.round(shadowColor.getAlpha() * (t * t) * 0.75f);
            if (alpha <= 0) continue;

            float grow = (shadowSize * (1f - t));
            float ox = x - grow + elevation;
            float oy = y - grow + elevation;
            float ow = rw + (grow * 2f);
            float oh = rh + (grow * 2f);

            Shape s = new RoundRectangle2D.Float(
                ox, oy, ow, oh,
                arc + (grow * 2f), arc + (grow * 2f)
            );
            g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), alpha));
            g2.fill(s);
        }
    }

    private void paintInnerShadows(Graphics2D g2, Shape baseShape, float x, float y, float rw, float rh) {
        if (insetSize <= 0) return;

        Shape oldClip = g2.getClip();
        g2.clip(baseShape);

        try {
            // Top-left highlight inset
            float hlW = rw * 0.9f;
            float hlH = rh * 0.6f;
            LinearGradientPaint highlight = new LinearGradientPaint(
                new Point2D.Float(x, y),
                new Point2D.Float(x + insetSize * 2f, y + insetSize * 2f),
                new float[]{0f, 1f},
                new Color[]{insetHighlight, new Color(insetHighlight.getRed(), insetHighlight.getGreen(), insetHighlight.getBlue(), 0)}
            );
            g2.setPaint(highlight);
            g2.fill(new RoundRectangle2D.Float(
                x + 1f, y + 1f,
                Math.max(1f, hlW), Math.max(1f, hlH),
                arc, arc
            ));

            // Bottom-right depth inset
            float shW = rw * 0.95f;
            float shH = rh * 0.7f;
            LinearGradientPaint depth = new LinearGradientPaint(
                new Point2D.Float(x + rw, y + rh),
                new Point2D.Float(x + rw - insetSize * 2f, y + rh - insetSize * 2f),
                new float[]{0f, 1f},
                new Color[]{insetShadow, new Color(insetShadow.getRed(), insetShadow.getGreen(), insetShadow.getBlue(), 0)}
            );
            g2.setPaint(depth);
            g2.fill(new RoundRectangle2D.Float(
                x + rw - shW - 1f, y + rh - shH - 1f,
                Math.max(1f, shW), Math.max(1f, shH),
                arc, arc
            ));

            // Slight overall inner vignette to enhance depth
            RadialGradientPaint vignette = new RadialGradientPaint(
                new Point2D.Float(x + rw * 0.45f, y + rh * 0.35f),
                Math.max(rw, rh) * 0.9f,
                new float[]{0f, 0.75f, 1f},
                new Color[]{
                    new Color(255, 255, 255, 0),
                    new Color(0, 0, 0, 0),
                    new Color(0, 0, 0, 26)
                }
            );
            g2.setPaint(vignette);
            g2.fill(baseShape);
        } finally {
            g2.setClip(oldClip);
        }
    }
}
