package makaronijaya;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Menu utama setelah login berhasil. Menjadi pusat navigasi ke
 * tampilFrame masing-masing tabel (users, produk, penjualan).
 */
public class MenuUtamaFrame extends JFrame {

    private final String namaLengkap;
    private final String level;

    public MenuUtamaFrame(String namaLengkap, String level) {
        this.namaLengkap = namaLengkap;
        this.level = level;

        setTitle("Makaroni Jaya - Menu Utama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 480);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIStyle.CREAM_BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMenuGrid(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.ORANGE);
        header.setPreferredSize(new Dimension(720, 90));
        header.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        JLabel lblTitle = new JLabel("Selamat datang, " + namaLengkap);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(UIStyle.WHITE);

        JLabel lblRole = new JLabel("Level: " + level);
        lblRole.setFont(UIStyle.FONT_SUBTITLE);
        lblRole.setForeground(UIStyle.WHITE);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblRole);

        RoundedButton btnLogout = new RoundedButton("Keluar", UIStyle.RED_ACCENT);
        btnLogout.setPreferredSize(new Dimension(100, 36));
        btnLogout.addActionListener(e -> logout());

        header.add(textPanel, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMenuGrid() {
        boolean isAdmin = "Admin".equalsIgnoreCase(level);

        JPanel grid = new JPanel(new GridLayout(1, isAdmin ? 3 : 2, 20, 20));
        grid.setBackground(UIStyle.CREAM_BG);
        grid.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Data Users: hanya Admin yang bisa membuka
        if (isAdmin) {
            grid.add(buildMenuCard("\uD83D\uDC64", "Data Users",
                    "Kelola akun pengguna aplikasi", () -> new TampilUsersFrame().setVisible(true)));
        }

        // Data Produk: Kasir hanya bisa melihat (read-only), Admin bisa kelola penuh
        String descProduk = isAdmin ? "Kelola jenis makaroni & harga" : "Lihat stok & harga makaroni";
        grid.add(buildMenuCard("\uD83E\uDD64", "Data Produk",
                descProduk, () -> new TampilProdukFrame(isAdmin).setVisible(true)));

        // Data Penjualan: Admin & Kasir sama-sama bisa mencatat transaksi
        grid.add(buildMenuCard("\uD83E\uDDFE", "Data Penjualan",
                "Catat & lihat transaksi penjualan", () -> new TampilPenjualanFrame().setVisible(true)));

        return grid;
    }

    private JPanel buildMenuCard(String emoji, String title, String desc, Runnable onOpen) {
        JPanel card = new JPanel();
        card.setLayout(new javax.swing.BoxLayout(card, javax.swing.BoxLayout.Y_AXIS));
        card.setBackground(UIStyle.CREAM_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.ORANGE, 2, true),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)));

        JLabel lblEmoji = new JLabel(emoji, SwingConstants.CENTER);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        lblEmoji.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(UIStyle.BROWN_TEXT);
        lblTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><center>" + desc + "</center></html>", SwingConstants.CENTER);
        lblDesc.setFont(UIStyle.FONT_SUBTITLE);
        lblDesc.setForeground(java.awt.Color.GRAY);
        lblDesc.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        RoundedButton btnOpen = new RoundedButton("Buka", UIStyle.ORANGE_DARK);
        btnOpen.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        btnOpen.setMaximumSize(new Dimension(120, 38));
        btnOpen.addActionListener(e -> onOpen.run());

        card.add(lblEmoji);
        card.add(javax.swing.Box.createVerticalStrut(10));
        card.add(lblTitle);
        card.add(javax.swing.Box.createVerticalStrut(6));
        card.add(lblDesc);
        card.add(javax.swing.Box.createVerticalStrut(15));
        card.add(btnOpen);

        return card;
    }

    private void logout() {
        boolean pilih = DialogHelper.konfirmasi(this, "Yakin ingin keluar?");
        if (pilih) {
            Sesi.logout();
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}
