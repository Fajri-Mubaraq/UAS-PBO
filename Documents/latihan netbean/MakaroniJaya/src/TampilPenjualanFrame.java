package makaronijaya;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * tampilFrame untuk tabel penjualan: menampilkan riwayat transaksi
 * penjualan makaroni beserta produk dan total harga.
 */
public class TampilPenjualanFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private final NumberFormat rupiah = FormatRupiah.buat();

    public TampilPenjualanFrame() {
        setTitle("Makaroni Jaya - Data Penjualan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(780, 460);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIStyle.CREAM_BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        muatData();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.ORANGE);
        header.setPreferredSize(new Dimension(780, 60));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lbl = new JLabel("\uD83E\uDDFE  Data Penjualan");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(UIStyle.WHITE);
        header.add(lbl, BorderLayout.WEST);
        return header;
    }

    private JScrollPane buildTablePanel() {
        model = new DefaultTableModel(new Object[]{
            "ID", "Produk", "Jenis", "Nama Pembeli", "Jumlah", "Total Harga", "Tanggal"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(UIStyle.FONT_TABLE_BODY);
        table.getTableHeader().setFont(UIStyle.FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(UIStyle.ORANGE_DARK);
        table.getTableHeader().setForeground(UIStyle.WHITE);
        table.setSelectionBackground(UIStyle.ORANGE);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(230, 210, 180));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(6).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return scroll;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyle.CREAM_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        RoundedButton btnTambah = new RoundedButton("+ Tambah", UIStyle.GREEN_ACCENT);
        RoundedButton btnCetak = new RoundedButton("\uD83D\uDDA8 Cetak Nota", UIStyle.ORANGE_DARK);
        RoundedButton btnHapus = new RoundedButton("Hapus", UIStyle.RED_ACCENT);
        RoundedButton btnRefresh = new RoundedButton("Refresh", UIStyle.ORANGE_DARK);
        RoundedButton btnKembali = new RoundedButton("Kembali", Color.GRAY);

        for (RoundedButton b : new RoundedButton[]{btnTambah, btnCetak, btnHapus, btnRefresh, btnKembali}) {
            b.setPreferredSize(new Dimension(125, 38));
            panel.add(b);
        }

        btnTambah.addActionListener(e -> new TambahPenjualanFrame(this).setVisible(true));
        btnCetak.addActionListener(e -> cetakUlangNota());
        btnHapus.addActionListener(e -> hapusData());
        btnRefresh.addActionListener(e -> muatData());
        btnKembali.addActionListener(e -> dispose());

        return panel;
    }

    /** Mencetak ulang nota untuk transaksi yang dipilih pada tabel riwayat. */
    private void cetakUlangNota() {
        int row = table.getSelectedRow();
        if (row == -1) {
            DialogHelper.peringatan(this, "Pilih transaksi yang ingin dicetak nota-nya.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String namaProduk = (String) model.getValueAt(row, 1);
        String jenis = (String) model.getValueAt(row, 2);
        String pembeli = (String) model.getValueAt(row, 3);
        int jumlah = (int) model.getValueAt(row, 4);
        double totalHarga = parseRupiahKeAngka((String) model.getValueAt(row, 5));
        double hargaSatuan = jumlah > 0 ? totalHarga / jumlah : totalHarga;

        java.util.List<NotaPanel.ItemNota> itemNota = new java.util.ArrayList<>();
        itemNota.add(new NotaPanel.ItemNota(namaProduk + " (" + jenis + ")", jumlah, hargaSatuan, totalHarga));

        String noTransaksi = "TRX-" + String.format("%06d", id);
        new NotaFrame(noTransaksi, pembeli, itemNota, totalHarga).setVisible(true);
    }

    /** Mengubah teks hasil format rupiah (mis. "Rp40.000,00") kembali menjadi angka double. */
    private double parseRupiahKeAngka(String teksRupiah) {
        String bersih = teksRupiah.replaceAll("[^0-9,]", "").replace(",", ".");
        try {
            return Double.parseDouble(bersih);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Mengambil data penjualan (join dengan produk) dan menampilkannya di tabel. */
    public void muatData() {
        model.setRowCount(0);
        Connection conn = Koneksi.getKoneksi();
        if (conn == null) {
            return;
        }
        String sql = "SELECT pj.id_penjualan, p.nama_produk, p.jenis, pj.nama_pembeli, "
                + "pj.jumlah, pj.total_harga, pj.tanggal "
                + "FROM penjualan pj JOIN produk p ON pj.id_produk = p.id_produk "
                + "ORDER BY pj.id_penjualan";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_penjualan"),
                    rs.getString("nama_produk"),
                    rs.getString("jenis"),
                    rs.getString("nama_pembeli"),
                    rs.getInt("jumlah"),
                    rupiah.format(rs.getDouble("total_harga")),
                    rs.getDate("tanggal")
                });
            }
        } catch (Exception e) {
            DialogHelper.error(this, "Gagal memuat data:\n" + e.getMessage());
        }
    }

    private void hapusData() {
        int row = table.getSelectedRow();
        if (row == -1) {
            DialogHelper.peringatan(this, "Pilih data yang ingin dihapus terlebih dahulu.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        boolean konfirmasi = DialogHelper.konfirmasi(this, "Konfirmasi Hapus",
                "Hapus transaksi #" + id + "?\n"
                + "Catatan: stok produk TIDAK akan dikembalikan.");
        if (!konfirmasi) {
            return;
        }

        Connection conn = Koneksi.getKoneksi();
        if (conn == null) {
            return;
        }
        String sql = "DELETE FROM penjualan WHERE id_penjualan = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            DialogHelper.sukses(this, "Data transaksi berhasil dihapus.");
            muatData();
        } catch (Exception e) {
            DialogHelper.error(this, "Gagal menghapus data:\n" + e.getMessage());
        }
    }
}
