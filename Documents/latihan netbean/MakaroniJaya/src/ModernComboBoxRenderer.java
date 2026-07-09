package makaronijaya;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renderer custom untuk item di dalam dropdown ModernComboBox, supaya
 * setiap pilihan punya padding yang lega dan warna highlight yang senada
 * dengan tema aplikasi (bukan biru/abu-abu bawaan Swing yang terkesan kuno).
 */
public class ModernComboBoxRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setOpaque(true);
        if (isSelected) {
            label.setBackground(UIStyle.ORANGE);
            label.setForeground(Color.WHITE);
        } else {
            label.setBackground(Color.WHITE);
            label.setForeground(UIStyle.BROWN_TEXT);
        }
        return label;
    }
}
