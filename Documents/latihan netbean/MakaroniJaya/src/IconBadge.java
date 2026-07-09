package makaronijaya;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Lencana ikon berbentuk lingkaran berwarna dengan simbol di tengahnya
 * (centang, silang, seru, tanda tanya), pengganti ikon kotak bawaan
 * Windows/Swing yang terkesan kuno pada JOptionPane.
 */
public class IconBadge extends JPanel {

    private final Color warnaLatar;
    private final String simbol;

    public IconBadge(Color warnaLatar, String simbol) {
        this.warnaLatar = warnaLatar;
        this.simbol = simbol;
        setOpaque(false);
        setPreferredSize(new Dimension(48, 48));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diameter = Math.min(getWidth(), getHeight());
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;

        g2.setColor(warnaLatar);
        g2.fillOval(x, y, diameter, diameter);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, (int) (diameter * 0.5)));
        FontMetrics fm = g2.getFontMetrics();
        int simbolX = x + (diameter - fm.stringWidth(simbol)) / 2;
        int simbolY = y + (diameter + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(simbol, simbolX, simbolY);

        g2.dispose();
    }
}
