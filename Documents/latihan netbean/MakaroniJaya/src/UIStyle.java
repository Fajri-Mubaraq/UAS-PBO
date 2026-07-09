package makaronijaya;

import java.awt.Color;
import java.awt.Font;

/**
 * Kumpulan konstanta warna dan font agar tampilan seluruh aplikasi konsisten.
 * Tema: hangat & gurih, terinspirasi dari camilan makaroni (krem, oranye, cokelat).
 */
public class UIStyle {

    // Palet warna
    public static final Color CREAM_BG      = new Color(255, 247, 230);
    public static final Color CREAM_PANEL   = new Color(255, 253, 246);
    public static final Color ORANGE        = new Color(230, 126, 34);
    public static final Color ORANGE_DARK   = new Color(196, 97, 18);
    public static final Color BROWN_TEXT    = new Color(74, 44, 23);
    public static final Color GREEN_ACCENT  = new Color(96, 153, 76);
    public static final Color RED_ACCENT    = new Color(192, 57, 43);
    public static final Color WHITE         = new Color(255, 255, 255);
    public static final Color TABLE_STRIPE  = new Color(255, 238, 209);

    // Font
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_FIELD   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_TABLE_BODY   = new Font("Segoe UI", Font.PLAIN, 13);

    private UIStyle() {
        // Kelas utilitas, tidak perlu diinstansiasi.
    }
}
