package makaronijaya;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * Tombol dengan sudut melengkung dan efek hover, supaya tampilan
 * lebih menarik dibanding JButton bawaan.
 */
public class RoundedButton extends JButton {

    private Color baseColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color currentColor;

    public RoundedButton(String text, Color baseColor) {
        super(text);
        this.baseColor = baseColor;
        this.hoverColor = baseColor.darker();
        this.pressedColor = baseColor.darker().darker();
        this.currentColor = baseColor;

        setForeground(UIStyle.WHITE);
        setFont(UIStyle.FONT_BUTTON);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentColor = baseColor;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                currentColor = pressedColor;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
        g2.dispose();
        super.paintComponent(g);
    }
}
