package makaronijaya;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/**
 * UI delegate custom untuk JComboBox supaya tampilannya flat & modern
 * (border melengkung, tanpa bevel/shadow bawaan Swing yang terkesan kuno),
 * dengan warna senada tema oranye-krem aplikasi.
 */
public class ModernComboBoxUI extends BasicComboBoxUI {

    @Override
    protected JButton createArrowButton() {
        JButton button = new JButton("\u25BC"); // simbol panah bawah
        button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        button.setForeground(UIStyle.ORANGE_DARK);
        button.setBackground(UIStyle.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 14, 14);
        g2.setColor(UIStyle.ORANGE);
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawRoundRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 14, 14);
        g2.dispose();
    }

    @Override
    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup(comboBox) {
            @Override
            protected void configureList() {
                super.configureList();
                list.setSelectionBackground(UIStyle.ORANGE);
                list.setSelectionForeground(Color.WHITE);
                list.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                list.setBackground(Color.WHITE);
            }
        };
        popup.setBorder(BorderFactory.createLineBorder(UIStyle.ORANGE, 1));
        return popup;
    }
}
