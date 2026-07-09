package makaronijaya;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
 * tampilFrame untuk tabel users: menampilkan seluruh data pengguna
 * dalam bentuk tabel, serta menyediakan aksi tambah, hapus, dan refresh.
 */
public class TampilUsersFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public TampilUsersFrame() {
        setTitle("Makaroni Jaya - Data Users");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 450);
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
        header.setPreferredSize(new Dimension(650, 60));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lbl = new JLabel("\uD83D\uDC64  Data Users");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(UIStyle.WHITE);
        header.add(lbl, BorderLayout.WEST);
        return header;
    }

    private JScrollPane buildTablePanel() {
        model = new DefaultTableModel(new Object[]{"ID", "Username", "Nama Lengkap", "Level"}, 0) {
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
        table.getColumnModel().getColumn(3).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return scroll;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyle.CREAM_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        RoundedButton btnTambah = new RoundedButton("+ Tambah", UIStyle.GREEN_ACCENT);
        RoundedButton btnHapus = new RoundedButton("Hapus", UIStyle.RED_ACCENT);
        RoundedButton btnRefresh = new RoundedButton("Refresh", UIStyle.ORANGE_DARK);
        RoundedButton btnKembali = new RoundedButton("Kembali", Color.GRAY);

        for (RoundedButton b : new RoundedButton[]{btnTambah, btnHapus, btnRefresh, btnKembali}) {
            b.setPreferredSize(new Dimension(120, 38));
            panel.add(b);
        }

        btnTambah.addActionListener(e -> {
            TambahUsersFrame f = new TambahUsersFrame(this);
            f.setVisible(true);
        });
        btnHapus.addActionListener(e -> hapusData());
        btnRefresh.addActionListener(e -> muatData());
        btnKembali.addActionListener(e -> dispose());

        return panel;
    }

    /** Mengambil seluruh data dari tabel users dan menampilkannya di JTable. */
    public void muatData() {
        model.setRowCount(0);
        Connection conn = Koneksi.getKoneksi();
        if (conn == null) {
            return;
        }
        String sql = "SELECT id_user, username, nama_lengkap, level FROM users ORDER BY id_user";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("nama_lengkap"),
                    rs.getString("level")
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
                "Hapus user \"" + model.getValueAt(row, 1) + "\"?");
        if (!konfirmasi) {
            return;
        }
        Connection conn = Koneksi.getKoneksi();
        String sql = "DELETE FROM users WHERE id_user = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            DialogHelper.sukses(this, "Data berhasil dihapus.");
            muatData();
        } catch (Exception e) {
            DialogHelper.error(this, "Gagal menghapus data:\n" + e.getMessage());
        }
    }
}
