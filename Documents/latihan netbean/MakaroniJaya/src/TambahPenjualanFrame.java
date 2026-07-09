package makaronijaya;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * tambahFrame untuk tabel penjualan: form input transaksi penjualan.
 * Jumlah barang dipilih lewat tombol +/- (bukan diketik), dan satu
 * transaksi bisa berisi lebih dari satu jenis produk sekaligus
 * (menggunakan sistem keranjang / cart) sebelum akhirnya disimpan.
 */
public class TambahPenjualanFrame extends JFrame {

    /** Menyimpan satu baris item di dalam keranjang. */
    private static class CartItem {
        int idProduk;
        String namaProduk;
        String jenis;
        double harga;
        int jumlah;

        double subtotal() {
            return harga * jumlah;
        }
    }

    private final TampilPenjualanFrame parent;
    private ModernComboBox<String> cbProduk;
    private JTextField txtPembeli;
    private JLabel lblQty;
    private JLabel lblStokTersisa;
    private JLabel lblTotalKeseluruhan;
    private JTable tabelKeranjang;
    private DefaultTableModel modelKeranjang;
    private RoundedButton btnMinus;
    private RoundedButton btnPlus;
    private RoundedButton btnTambahKeranjang;

    // label kombo -> [id_produk, harga, stok asli di database, jenis]
    private final Map<String, Object[]> dataProduk = new LinkedHashMap<>();
    private final List<CartItem> keranjang = new ArrayList<>();
    private final NumberFormat rupiah = FormatRupiah.buat();
    private int currentQty = 1;

    public TambahPenjualanFrame(TampilPenjualanFrame parent) {
        this.parent = parent;

        setTitle("Tambah Transaksi Penjualan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 680);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UIStyle.CREAM_BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        muatDaftarProduk();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(UIStyle.ORANGE);
        header.setPreferredSize(new Dimension(520, 55));
        JLabel lbl = new JLabel("+ Tambah Penjualan");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setForeground(UIStyle.WHITE);
        header.add(lbl);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new javax.swing.BoxLayout(body, javax.swing.BoxLayout.Y_AXIS));
        body.setBackground(UIStyle.CREAM_BG);
        body.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        body.add(buildPembeliPanel());
        body.add(javax.swing.Box.createVerticalStrut(12));
        body.add(buildPilihProdukPanel());
        body.add(javax.swing.Box.createVerticalStrut(14));
        body.add(buildKeranjangLabel());
        body.add(javax.swing.Box.createVerticalStrut(6));
        body.add(buildKeranjangTable());

        return body;
    }

    private JPanel buildPembeliPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIStyle.CREAM_BG);
        panel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);

        JLabel lbl = new JLabel("Nama Pembeli");
        lbl.setFont(UIStyle.FONT_LABEL);
        lbl.setForeground(UIStyle.BROWN_TEXT);
        panel.add(lbl, gbc);

        txtPembeli = new JTextField();
        txtPembeli.setFont(UIStyle.FONT_FIELD);
        txtPembeli.setPreferredSize(new Dimension(440, 34));
        txtPembeli.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.ORANGE, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        gbc.gridy = 1;
        panel.add(txtPembeli, gbc);

        return panel;
    }

    private JPanel buildPilihProdukPanel() {
        JPanel outer = new JPanel();
        outer.setLayout(new javax.swing.BoxLayout(outer, javax.swing.BoxLayout.Y_AXIS));
        outer.setBackground(UIStyle.CREAM_PANEL);
        outer.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.ORANGE, 2, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JLabel lblPilih = new JLabel("Pilih Produk");
        lblPilih.setFont(UIStyle.FONT_LABEL);
        lblPilih.setForeground(UIStyle.BROWN_TEXT);
        lblPilih.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        cbProduk = new ModernComboBox<>();
        cbProduk.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbProduk.setMaximumSize(new Dimension(460, 38));
        cbProduk.setPreferredSize(new Dimension(460, 38));
        cbProduk.setAlignmentX(ModernComboBox.LEFT_ALIGNMENT);
        cbProduk.addActionListener(e -> onProdukDipilih());

        lblStokTersisa = new JLabel("Stok tersedia: -");
        lblStokTersisa.setFont(UIStyle.FONT_SUBTITLE);
        lblStokTersisa.setForeground(Color.GRAY);
        lblStokTersisa.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        // --- Stepper jumlah: tombol minus, angka, tombol plus ---
        JPanel stepperPanel = new JPanel();
        stepperPanel.setBackground(UIStyle.CREAM_PANEL);
        stepperPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        btnMinus = new RoundedButton("-", UIStyle.ORANGE_DARK);
        btnMinus.setPreferredSize(new Dimension(42, 40));
        btnMinus.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnMinus.addActionListener(e -> ubahJumlah(-1));

        lblQty = new JLabel("1", SwingConstants.CENTER);
        lblQty.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblQty.setForeground(UIStyle.BROWN_TEXT);
        lblQty.setPreferredSize(new Dimension(60, 40));
        lblQty.setHorizontalAlignment(SwingConstants.CENTER);
        lblQty.setBorder(BorderFactory.createLineBorder(new Color(230, 210, 180), 1));

        btnPlus = new RoundedButton("+", UIStyle.ORANGE_DARK);
        btnPlus.setPreferredSize(new Dimension(42, 40));
        btnPlus.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnPlus.addActionListener(e -> ubahJumlah(1));

        stepperPanel.add(btnMinus);
        stepperPanel.add(lblQty);
        stepperPanel.add(btnPlus);

        btnTambahKeranjang = new RoundedButton("+ Tambah ke Keranjang", UIStyle.GREEN_ACCENT);
        btnTambahKeranjang.setFont(UIStyle.FONT_BUTTON);
        btnTambahKeranjang.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        btnTambahKeranjang.setMaximumSize(new Dimension(220, 40));
        btnTambahKeranjang.setPreferredSize(new Dimension(220, 40));
        btnTambahKeranjang.addActionListener(e -> tambahKeKeranjang());

        outer.add(lblPilih);
        outer.add(javax.swing.Box.createVerticalStrut(4));
        outer.add(cbProduk);
        outer.add(javax.swing.Box.createVerticalStrut(4));
        outer.add(lblStokTersisa);
        outer.add(javax.swing.Box.createVerticalStrut(10));
        outer.add(stepperPanel);
        outer.add(javax.swing.Box.createVerticalStrut(10));
        outer.add(btnTambahKeranjang);

        return outer;
    }

    private JLabel buildKeranjangLabel() {
        JLabel lbl = new JLabel("Keranjang Belanja");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(UIStyle.BROWN_TEXT);
        lbl.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        return lbl;
    }

    private JScrollPane buildKeranjangTable() {
        modelKeranjang = new DefaultTableModel(new Object[]{"Produk", "Jumlah", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelKeranjang = new JTable(modelKeranjang);
        tabelKeranjang.setRowHeight(26);
        tabelKeranjang.setFont(UIStyle.FONT_TABLE_BODY);
        tabelKeranjang.getTableHeader().setFont(UIStyle.FONT_TABLE_HEADER);
        tabelKeranjang.getTableHeader().setBackground(UIStyle.ORANGE_DARK);
        tabelKeranjang.getTableHeader().setForeground(UIStyle.WHITE);
        tabelKeranjang.setSelectionBackground(UIStyle.ORANGE);
        tabelKeranjang.setSelectionForeground(Color.WHITE);
        tabelKeranjang.setGridColor(new Color(230, 210, 180));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tabelKeranjang.getColumnModel().getColumn(1).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(tabelKeranjang);
        scroll.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);
        scroll.setPreferredSize(new Dimension(460, 150));
        scroll.setMaximumSize(new Dimension(460, 150));
        return scroll;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new javax.swing.BoxLayout(footer, javax.swing.BoxLayout.Y_AXIS));
        footer.setBackground(UIStyle.CREAM_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 25, 18, 25));

        RoundedButton btnHapusItem = new RoundedButton("Hapus Item Terpilih", UIStyle.RED_ACCENT);
        btnHapusItem.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        btnHapusItem.setMaximumSize(new Dimension(180, 34));
        btnHapusItem.setPreferredSize(new Dimension(180, 34));
        btnHapusItem.addActionListener(e -> hapusItemKeranjang());

        lblTotalKeseluruhan = new JLabel("Total Keseluruhan: Rp 0");
        lblTotalKeseluruhan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalKeseluruhan.setForeground(UIStyle.ORANGE_DARK);
        lblTotalKeseluruhan.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(UIStyle.CREAM_BG);
        btnPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        RoundedButton btnSimpan = new RoundedButton("Simpan Transaksi", UIStyle.GREEN_ACCENT);
        RoundedButton btnBatal = new RoundedButton("Batal", Color.GRAY);
        btnSimpan.setPreferredSize(new Dimension(180, 42));
        btnBatal.setPreferredSize(new Dimension(120, 42));
        btnSimpan.addActionListener(e -> simpanTransaksi());
        btnBatal.addActionListener(e -> dispose());
        btnPanel.add(btnSimpan);
        btnPanel.add(btnBatal);

        footer.add(btnHapusItem);
        footer.add(javax.swing.Box.createVerticalStrut(10));
        footer.add(lblTotalKeseluruhan);
        footer.add(javax.swing.Box.createVerticalStrut(10));
        footer.add(btnPanel);

        return footer;
    }

    /** Mengambil daftar produk dari database (beserta stok asli) untuk mengisi combo box. */
    private void muatDaftarProduk() {
        cbProduk.removeAllItems();
        dataProduk.clear();
        Connection conn = Koneksi.getKoneksi();
        if (conn == null) {
            return;
        }
        String sql = "SELECT id_produk, nama_produk, jenis, harga, stok FROM produk ORDER BY id_produk";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String namaProduk = rs.getString("nama_produk");
                String jenis = rs.getString("jenis");
                double harga = rs.getDouble("harga");
                String label = emojiUntukJenis(jenis) + "  " + namaProduk + "   -   " + rupiah.format(harga);
                dataProduk.put(label, new Object[]{
                    rs.getInt("id_produk"), harga, rs.getInt("stok"), jenis, namaProduk
                });
                cbProduk.addItem(label);
            }
        } catch (Exception e) {
            DialogHelper.error(this, "Gagal memuat daftar produk:\n" + e.getMessage());
        }
        onProdukDipilih();
    }

    /** Memilih emoji yang mewakili tiap varian makaroni, supaya lebih mudah dikenali di dropdown. */
    private String emojiUntukJenis(String jenis) {
        switch (jenis) {
            case "Asin":
                return "\uD83E\uDDC2"; // 🧂
            case "Jagung Bakar":
                return "\uD83C\uDF3D"; // 🌽
            case "Balado":
                return "\uD83C\uDF36\uFE0F"; // 🌶️
            default:
                return "\uD83E\uDD64"; // 🥔
        }
    }

    /** Menghitung berapa stok yang masih tersedia untuk id produk tertentu, setelah dikurangi isi keranjang. */
    private int stokTersisaUntuk(int idProduk, int stokAsli) {
        int sudahDiKeranjang = 0;
        for (CartItem item : keranjang) {
            if (item.idProduk == idProduk) {
                sudahDiKeranjang += item.jumlah;
            }
        }
        return stokAsli - sudahDiKeranjang;
    }

    private void onProdukDipilih() {
        String selected = (String) cbProduk.getSelectedItem();
        if (selected == null || !dataProduk.containsKey(selected)) {
            lblStokTersisa.setText("Stok tersedia: -");
            currentQty = 0;
            lblQty.setText("0");
            btnTambahKeranjang.setEnabled(false);
            return;
        }
        Object[] info = dataProduk.get(selected);
        int idProduk = (int) info[0];
        int stokAsli = (int) info[2];
        int tersisa = stokTersisaUntuk(idProduk, stokAsli);

        lblStokTersisa.setText("Stok tersedia: " + tersisa);
        currentQty = tersisa > 0 ? 1 : 0;
        lblQty.setText(String.valueOf(currentQty));
        btnTambahKeranjang.setEnabled(tersisa > 0);
        btnMinus.setEnabled(currentQty > 1);
        btnPlus.setEnabled(currentQty < tersisa);
    }

    private void ubahJumlah(int delta) {
        String selected = (String) cbProduk.getSelectedItem();
        if (selected == null || !dataProduk.containsKey(selected)) {
            return;
        }
        Object[] info = dataProduk.get(selected);
        int idProduk = (int) info[0];
        int stokAsli = (int) info[2];
        int maksimal = stokTersisaUntuk(idProduk, stokAsli);

        int baru = currentQty + delta;
        if (baru < 1) {
            baru = 1;
        }
        if (baru > maksimal) {
            baru = maksimal;
        }
        currentQty = baru;
        lblQty.setText(String.valueOf(currentQty));
        btnMinus.setEnabled(currentQty > 1);
        btnPlus.setEnabled(currentQty < maksimal);
    }

    private void tambahKeKeranjang() {
        String selected = (String) cbProduk.getSelectedItem();
        if (selected == null || !dataProduk.containsKey(selected)) {
            DialogHelper.peringatan(this, "Pilih produk terlebih dahulu.");
            return;
        }
        if (currentQty <= 0) {
            DialogHelper.peringatan(this, "Stok produk ini sudah habis.");
            return;
        }

        Object[] info = dataProduk.get(selected);
        int idProduk = (int) info[0];
        double harga = (double) info[1];
        String jenis = (String) info[3];
        String namaProduk = (String) info[4];

        // Gabungkan jika produk yang sama sudah ada di keranjang
        CartItem existing = null;
        for (CartItem item : keranjang) {
            if (item.idProduk == idProduk) {
                existing = item;
                break;
            }
        }
        if (existing != null) {
            existing.jumlah += currentQty;
        } else {
            CartItem item = new CartItem();
            item.idProduk = idProduk;
            item.namaProduk = namaProduk;
            item.jenis = jenis;
            item.harga = harga;
            item.jumlah = currentQty;
            keranjang.add(item);
        }

        refreshTabelKeranjang();
        onProdukDipilih(); // reset stepper & label stok tersedia untuk produk yang sama
    }

    private void hapusItemKeranjang() {
        int row = tabelKeranjang.getSelectedRow();
        if (row == -1) {
            DialogHelper.peringatan(this, "Pilih item keranjang yang ingin dihapus.");
            return;
        }
        keranjang.remove(row);
        refreshTabelKeranjang();
        onProdukDipilih();
    }

    private void refreshTabelKeranjang() {
        modelKeranjang.setRowCount(0);
        double totalKeseluruhan = 0;
        for (CartItem item : keranjang) {
            modelKeranjang.addRow(new Object[]{
                item.namaProduk + " (" + item.jenis + ")",
                item.jumlah,
                rupiah.format(item.subtotal())
            });
            totalKeseluruhan += item.subtotal();
        }
        lblTotalKeseluruhan.setText("Total Keseluruhan: " + rupiah.format(totalKeseluruhan));
    }

    private void simpanTransaksi() {
        String pembeli = txtPembeli.getText().trim();
        if (pembeli.isEmpty()) {
            DialogHelper.peringatan(this, "Nama pembeli wajib diisi!");
            return;
        }
        if (keranjang.isEmpty()) {
            DialogHelper.peringatan(this, "Keranjang masih kosong. Tambahkan produk terlebih dahulu.");
            return;
        }

        Connection conn = Koneksi.getKoneksi();
        if (conn == null) {
            return;
        }

        String sqlInsert = "INSERT INTO penjualan (id_produk, nama_pembeli, jumlah, total_harga, tanggal) "
                + "VALUES (?, ?, ?, ?, CURDATE())";
        String sqlUpdateStok = "UPDATE produk SET stok = stok - ? WHERE id_produk = ? AND stok >= ?";

        int idTransaksiPertama = -1;

        try {
            conn.setAutoCommit(false);

            for (CartItem item : keranjang) {
                // Simpan satu baris transaksi per produk di keranjang
                try (PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, item.idProduk);
                    ps.setString(2, pembeli);
                    ps.setInt(3, item.jumlah);
                    ps.setDouble(4, item.subtotal());
                    ps.executeUpdate();

                    if (idTransaksiPertama == -1) {
                        ResultSet keys = ps.getGeneratedKeys();
                        if (keys.next()) {
                            idTransaksiPertama = keys.getInt(1);
                        }
                    }
                }

                // Kurangi stok produk terkait; kondisi stok >= jumlah mencegah stok minus
                int barisTerupdate;
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateStok)) {
                    ps.setInt(1, item.jumlah);
                    ps.setInt(2, item.idProduk);
                    ps.setInt(3, item.jumlah);
                    barisTerupdate = ps.executeUpdate();
                }

                if (barisTerupdate == 0) {
                    conn.rollback();
                    DialogHelper.peringatan(this,
                            "Stok untuk \"" + item.namaProduk + "\" tidak mencukupi (mungkin baru saja berubah).\n"
                            + "Transaksi dibatalkan, silakan periksa kembali keranjang.");
                    return;
                }
            }

            conn.commit();

            // Bangun daftar item untuk nota, lalu tampilkan jendela nota (siap dicetak)
            String noTransaksi = "TRX-" + String.format("%06d", idTransaksiPertama);
            List<NotaPanel.ItemNota> itemNota = new ArrayList<>();
            double totalUntukNota = 0;
            for (CartItem item : keranjang) {
                itemNota.add(new NotaPanel.ItemNota(
                        item.namaProduk + " (" + item.jenis + ")", item.jumlah, item.harga, item.subtotal()));
                totalUntukNota += item.subtotal();
            }

            parent.muatData();
            new NotaFrame(noTransaksi, pembeli, itemNota, totalUntukNota).setVisible(true);
            dispose();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception ignored) {
                // koneksi mungkin sudah tertutup, abaikan
            }
            DialogHelper.error(this, "Gagal menyimpan data:\n" + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception ignored) {
                // abaikan jika koneksi sudah tertutup
            }
        }
    }
}
