package makaronijaya;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;

/**
 * JComboBox dengan tampilan flat & modern (border melengkung, warna
 * senada tema aplikasi), pengganti JComboBox bawaan yang terlihat kuno.
 * Cukup gunakan seperti JComboBox biasa di seluruh aplikasi.
 */
public class ModernComboBox<T> extends JComboBox<T> {

    public ModernComboBox() {
        super();
        terapkanGaya();
    }

    public ModernComboBox(T[] items) {
        super(items);
        terapkanGaya();
    }

    private void terapkanGaya() {
        setUI(new ModernComboBoxUI());
        setRenderer(new ModernComboBoxRenderer());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 2));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setBackground(Color.WHITE);
        setForeground(UIStyle.BROWN_TEXT);
        setFocusable(true);
    }
}
