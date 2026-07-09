package makaronijaya;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JPanel;

/**
 * Panel yang menggambar tampilan nota/struk secara custom (bukan pakai
 * JTable/JLabel bawaan), supaya bisa dipakai untuk tampilan di layar
 * MAUPUN untuk dicetak (Printable) dengan hasil identik.
 * Gaya visual: minimalis modern (garis putus-putus tipis, aksen warna,
 * tanpa bingkai tebal bergaya struk kasir jadul).
 */
public class NotaPanel extends JPanel {

    /** Baris item pada nota. */
    public static class ItemNota {
        public final String namaProduk;
        public final int jumlah;
        public final double hargaSatuan;
        public final double subtotal;

        public ItemNota(String namaProduk, int jumlah, double hargaSatuan, double subtotal) {
            this.namaProduk = namaProduk;
            this.jumlah = jumlah;
            this.hargaSatuan = hargaSatuan;
            this.subtotal = subtotal;
        }
    }

    private static final int LEBAR = 340;
    private static final int PADDING = 22;

    private final String noTransaksi;
    private final String namaPembeli;
    private final String namaKasir;
    private final List<ItemNota> items;
    private final double totalKeseluruhan;
    private final NumberFormat rupiah = FormatRupiah.buat();
    private final SimpleDateFormat formatTanggal = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));

    public NotaPanel(String noTransaksi, String namaPembeli, String namaKasir,
            List<ItemNota> items, double totalKeseluruhan) {
        this.noTransaksi = noTransaksi;
        this.namaPembeli = namaPembeli;
        this.namaKasir = namaKasir;
        this.items = items;
        this.totalKeseluruhan = totalKeseluruhan;

        int tinggi = hitungTinggi();
        setPreferredSize(new java.awt.Dimension(LEBAR, tinggi));
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    private int hitungTinggi() {
        // perkiraan tinggi konten berdasarkan jumlah baris item
        int tinggiPerItem = 40;
        return 300 + (items.size() * tinggiPerItem);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gambarNota(g2, LEBAR);
        g2.dispose();
    }

    /**
     * Menggambar seluruh isi nota pada Graphics2D yang diberikan.
     * Dipakai bersama oleh paintComponent (layar) dan proses cetak (printer),
     * supaya tampilan di layar dan hasil cetak selalu identik.
     */
    public void gambarNota(Graphics2D g2, int lebar) {
        int x = PADDING;
        int y = 28;
        int lebarKonten = lebar - (PADDING * 2);

        // --- Header toko ---
        g2.setColor(UIStyle.BROWN_TEXT);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        y = drawCentered(g2, "MAKARONI JAYA", lebar / 2, y);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(Color.GRAY);
        y += 4;
        y = drawCentered(g2, "Asin \u2022 Jagung Bakar \u2022 Balado", lebar / 2, y);
        y += 14;

        y = drawDashedLine(g2, x, y, lebarKonten);
        y += 18;

        // --- Info transaksi ---
        g2.setFont(new Font("Consolas", Font.PLAIN, 12));
        g2.setColor(UIStyle.BROWN_TEXT);
        y = drawInfoRow(g2, x, y, lebarKonten, "No. Transaksi", noTransaksi);
        y = drawInfoRow(g2, x, y, lebarKonten, "Tanggal", formatTanggal.format(new Date()));
        y = drawInfoRow(g2, x, y, lebarKonten, "Kasir", namaKasir);
        y = drawInfoRow(g2, x, y, lebarKonten, "Pembeli", namaPembeli);
        y += 6;

        y = drawDashedLine(g2, x, y, lebarKonten);
        y += 20;

        // --- Daftar item ---
        for (ItemNota item : items) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(UIStyle.BROWN_TEXT);
            g2.drawString(item.namaProduk, x, y);
            y += 18;

            g2.setFont(new Font("Consolas", Font.PLAIN, 12));
            g2.setColor(new Color(110, 110, 110));
            String kiri = item.jumlah + " x " + rupiah.format(item.hargaSatuan);
            String kanan = rupiah.format(item.subtotal);
            g2.drawString(kiri, x, y);
            drawRightAligned(g2, kanan, x + lebarKonten, y);
            y += 22;
        }

        y += 4;
        y = drawDashedLine(g2, x, y, lebarKonten);
        y += 12;

        // --- Total (dengan pita highlight warna) ---
        g2.setColor(new Color(255, 238, 209));
        g2.fillRoundRect(x - 6, y - 20, lebarKonten + 12, 36, 10, 10);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setColor(UIStyle.ORANGE_DARK);
        g2.drawString("TOTAL", x, y + 4);
        drawRightAligned(g2, rupiah.format(totalKeseluruhan), x + lebarKonten, y + 4);
        y += 40;

        y = drawDashedLine(g2, x, y, lebarKonten);
        y += 22;

        // --- Footer ---
        g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        g2.setColor(UIStyle.BROWN_TEXT);
        y = drawCentered(g2, "Terima kasih telah berbelanja!", lebar / 2, y);
        y += 16;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(Color.GRAY);
        drawCentered(g2, "Simpan nota ini sebagai bukti pembelian yang sah", lebar / 2, y);
    }

    private int drawInfoRow(Graphics2D g2, int x, int y, int lebarKonten, String label, String value) {
        g2.setColor(Color.GRAY);
        g2.drawString(label, x, y);
        g2.setColor(UIStyle.BROWN_TEXT);
        drawRightAligned(g2, value, x + lebarKonten, y);
        return y + 18;
    }

    private int drawCentered(Graphics2D g2, String text, int centerX, int y) {
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(text, g2);
        int textX = (int) (centerX - bounds.getWidth() / 2);
        g2.drawString(text, textX, y);
        return (int) (y + bounds.getHeight());
    }

    private void drawRightAligned(Graphics2D g2, String text, int rightX, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2.drawString(text, rightX - textWidth, y);
    }

    private int drawDashedLine(Graphics2D g2, int x, int y, int lebar) {
        Graphics2D dashed = (Graphics2D) g2.create();
        dashed.setColor(new Color(220, 200, 170));
        dashed.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{4f, 4f}, 0));
        dashed.drawLine(x, y, x + lebar, y);
        dashed.dispose();
        return y;
    }

    public int getLebarNota() {
        return LEBAR;
    }
}
