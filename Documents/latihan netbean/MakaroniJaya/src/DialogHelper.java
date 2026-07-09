package makaronijaya;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Pengganti JOptionPane bawaan Swing yang tampilannya kuno (ikon kotak,
 * tombol default sistem operasi). Semua notifikasi di aplikasi ini
 * (sukses, error, peringatan, konfirmasi) memakai dialog custom di sini,
 * dengan tema warna & tombol yang senada dengan seluruh aplikasi.
 */
public class DialogHelper {

    private DialogHelper() {
        // Kelas utilitas, tidak perlu diinstansiasi.
    }

    /** Notifikasi sukses (ikon centang hijau). */
    public static void sukses(Component parent, String pesan) {
        tampilkanNotifikasi(parent, "Berhasil", pesan, UIStyle.GREEN_ACCENT, "\u2713");
    }

    /** Notifikasi error (ikon silang merah). */
    public static void error(Component parent, String pesan) {
        error(parent, "Terjadi Kesalahan", pesan);
    }

    public static void error(Component parent, String judul, String pesan) {
        tampilkanNotifikasi(parent, judul, pesan, UIStyle.RED_ACCENT, "\u2715");
    }

    /** Notifikasi peringatan (ikon seru oranye). */
    public static void peringatan(Component parent, String pesan) {
        tampilkanNotifikasi(parent, "Peringatan", pesan, UIStyle.ORANGE, "!");
    }

    /** Dialog konfirmasi Ya/Tidak (ikon tanda tanya). Mengembalikan true jika user memilih "Ya". */
    public static boolean konfirmasi(Component parent, String pesan) {
        return konfirmasi(parent, "Konfirmasi", pesan);
    }

    public static boolean konfirmasi(Component parent, String judul, String pesan) {
        Window owner = parent != null ? SwingUtilities.getWindowAncestor(parent) : null;
        JDialog dialog = new JDialog(owner, judul, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(UIStyle.CREAM_BG);

        boolean[] hasil = {false};

        JPanel isi = buildIsiPanel(pesan, UIStyle.ORANGE_DARK, "?");
        dialog.add(isi, BorderLayout.CENTER);

        JPanel tombolPanel = new JPanel();
        tombolPanel.setBackground(UIStyle.CREAM_BG);
        tombolPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 18, 15));

        RoundedButton btnYa = new RoundedButton("Ya", UIStyle.GREEN_ACCENT);
        RoundedButton btnTidak = new RoundedButton("Tidak", Color.GRAY);
        btnYa.setPreferredSize(new Dimension(110, 38));
        btnTidak.setPreferredSize(new Dimension(110, 38));

        btnYa.addActionListener(e -> {
            hasil[0] = true;
            dialog.dispose();
        });
        btnTidak.addActionListener(e -> {
            hasil[0] = false;
            dialog.dispose();
        });

        tombolPanel.add(btnTidak);
        tombolPanel.add(btnYa);
        dialog.add(tombolPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(380, dialog.getHeight()));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        return hasil[0];
    }

    /** Membangun & menampilkan dialog notifikasi satu tombol ("Tutup"). */
    private static void tampilkanNotifikasi(Component parent, String judul, String pesan,
            Color warnaIkon, String simbol) {
        Window owner = parent != null ? SwingUtilities.getWindowAncestor(parent) : null;
        JDialog dialog = new JDialog(owner, judul, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(UIStyle.CREAM_BG);

        JPanel isi = buildIsiPanel(pesan, warnaIkon, simbol);
        dialog.add(isi, BorderLayout.CENTER);

        JPanel tombolPanel = new JPanel();
        tombolPanel.setBackground(UIStyle.CREAM_BG);
        tombolPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 18, 15));

        RoundedButton btnTutup = new RoundedButton("Tutup", warnaIkon);
        btnTutup.setPreferredSize(new Dimension(120, 38));
        btnTutup.addActionListener(e -> dialog.dispose());
        tombolPanel.add(btnTutup);
        dialog.add(tombolPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(380, dialog.getHeight()));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /** Panel isi (ikon + pesan) yang dipakai bersama oleh semua jenis dialog. */
    private static JPanel buildIsiPanel(String pesan, Color warnaIkon, String simbol) {
        JPanel isi = new JPanel(new BorderLayout(18, 0));
        isi.setBackground(UIStyle.CREAM_BG);
        isi.setBorder(BorderFactory.createEmptyBorder(24, 24, 20, 24));

        IconBadge ikon = new IconBadge(warnaIkon, simbol);
        isi.add(ikon, BorderLayout.WEST);

        String pesanHtml = "<html><body style='width: 240px'>" + pesan.replace("\n", "<br>") + "</body></html>";
        JLabel lblPesan = new JLabel(pesanHtml);
        lblPesan.setFont(UIStyle.FONT_LABEL);
        lblPesan.setForeground(UIStyle.BROWN_TEXT);
        lblPesan.setVerticalAlignment(SwingConstants.CENTER);
        isi.add(lblPesan, BorderLayout.CENTER);

        return isi;
    }
}
