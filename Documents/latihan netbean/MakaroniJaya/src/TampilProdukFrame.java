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
 * tampilFrame untuk tabel produk: menampilkan tiga varian makaroni
 * (Asin, Jagung Bakar, Balado) beserta harga dan stok.
 */
public class TampilProdukFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private final NumberFormat rupiah = FormatRupiah.buat();
    private final boolean isAdmin;

    /** Dibuka oleh Admin: bisa tambah/hapus. Konstruktor lama tetap ada untuk kompatibilitas. */
    public TampilProdukFrame() {
        this(true);
    }

    /**
     * @param isAdmin jika false (Kasir), tombol Tambah & Hapus disembunyikan
     *                sehingga tampilan hanya bisa melihat data (read-only).
     */
    public TampilProdukFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;

        setTitle("Makaroni Jaya - Data Produk" + (isAdmin ? "" : " (Lihat Saja)"));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(680, 450);
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
        header.setPreferredSize(new Dimension(680, 60));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lbl = new JLabel("\uD83E\uDD64  Data Produk Makaroni" + (isAdmin ? "" : "  (Lihat Saja)"));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(UIStyle.WHITE);
        header.add(lbl, BorderLayout.WEST);
        return header;
    }

    private JScrollPane buildTablePanel() {
        model = new DefaultTableModel(new Object[]{"ID", "Nama Produk", "Jenis", "Harga", "Stok"}, 0) {
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

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return scroll;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyle.CREAM_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        RoundedButton btnRefresh = new RoundedButton("Refresh", UIStyle.ORANGE_DARK);
        RoundedButton btnKembali = new RoundedButton("Kembali", Color.GRAY);

        if (isAdmin) {
            // Hanya Admin yang boleh menambah/menghapus data produk
            RoundedButton btnTambah = new RoundedButton("+ Tambah", UIStyle.GREEN_ACCENT);
            RoundedButton btnHapus = new RoundedButton("Hapus", UIStyle.RED_ACCENT);
            btnTambah.setPreferredSize(new Dimension(120, 38));
            btnHapus.setPreferredSize(new Dimension(120, 38));
            btnTambah.addActionListener(e -> new TambahProdukFrame(this).setVisible(true));
            btnHapus.addActionListener(e -> hapusData());
            panel.add(btnTambah);
            panel.add(btnHapus);
        }

        btnRefresh.setPreferredSize(new Dimension(120, 38));
        btnKembali.setPreferredSize(new Dimension(120, 38));
        btnRefresh.addActionListener(e -> muatData());
        btnKembali.addActionListener(e -> dispose());
        panel.add(btnRefresh);
        panel.add(btnKembali);

        return panel;
    }

    /** Mengambil seluruh data produk dari database dan menampilkannya di tabel. */
    public void muatData() {
        model.setRowCount(0);
        Connection conn = Koneksi.getKoneksi();
        if (conn == null) {
            return;
        }
        String sql = "SELECT id_produk, nama_produk, jenis, harga, stok FROM produk ORDER BY id_produk";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_produk"),
                    rs.getString("nama_produk"),
                    rs.getString("jenis"),
                    rupiah.format(rs.getDouble("harga")),
                    rs.getInt("stok")
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
                "Hapus produk \"" + model.getValueAt(row, 1) + "\"?");
        if (!konfirmasi) {
            return;
        }
        Connection conn = Koneksi.getKoneksi();
        String sql = "DELETE FROM produk WHERE id_produk = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            DialogHelper.sukses(this, "Data berhasil dihapus.");
            muatData();
        } catch (Exception e) {
            DialogHelper.error(this, "Gagal menghapus data (mungkin masih dipakai di penjualan):\n" + e.getMessage());
        }
    }
}
