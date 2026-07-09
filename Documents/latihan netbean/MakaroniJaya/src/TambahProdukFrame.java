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
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * tambahFrame untuk tabel produk: form input jenis makaroni baru
 * (Asin, Jagung Bakar, atau Balado) beserta harga dan stok.
 */
public class TambahProdukFrame extends JFrame {

    private final TampilProdukFrame parent;
    private ModernComboBox<String> cbJenis;
    private JTextField txtHarga;
    private JTextField txtStok;

    public TambahProdukFrame(TampilProdukFrame parent) {
        this.parent = parent;

        setTitle("Tambah Produk Baru");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 360);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UIStyle.CREAM_BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(UIStyle.ORANGE);
        header.setPreferredSize(new Dimension(400, 55));
        JLabel lbl = new JLabel("+ Tambah Produk");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setForeground(UIStyle.WHITE);
        header.add(lbl);
        return header;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyle.CREAM_BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;

        // Jenis makaroni SEKALIGUS menjadi nama produk (nama_produk = "Makaroni " + jenis),
        // jadi tidak perlu lagi field nama produk terpisah yang isinya cuma mengulang jenis.
        cbJenis = new ModernComboBox<>(new String[]{"Asin", "Jagung Bakar", "Balado"});
        txtHarga = new JTextField();
        txtStok = new JTextField();

        addField(form, gbc, 0, "Jenis Makaroni", cbJenis);
        addField(form, gbc, 2, "Harga (Rp)", txtHarga);
        addField(form, gbc, 4, "Stok", txtStok);

        RoundedButton btnSimpan = new RoundedButton("Simpan", UIStyle.GREEN_ACCENT);
        RoundedButton btnBatal = new RoundedButton("Batal", Color.GRAY);
        btnSimpan.setPreferredSize(new Dimension(150, 40));
        btnBatal.setPreferredSize(new Dimension(150, 40));

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(UIStyle.CREAM_BG);
        btnPanel.add(btnSimpan);
        btnPanel.add(btnBatal);

        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 0, 0);
        form.add(btnPanel, gbc);

        btnSimpan.addActionListener(e -> simpanData());
        btnBatal.addActionListener(e -> dispose());

        return form;
    }

    private void addField(JPanel form, GridBagConstraints gbc, int startY, String label, java.awt.Component field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyle.FONT_LABEL);
        lbl.setForeground(UIStyle.BROWN_TEXT);
        gbc.gridy = startY;
        form.add(lbl, gbc);

        field.setFont(UIStyle.FONT_FIELD);
        field.setPreferredSize(new Dimension(280, 34));
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIStyle.ORANGE, 1, true),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        }
        gbc.gridy = startY + 1;
        form.add(field, gbc);
    }

    private void simpanData() {
        String jenis = (String) cbJenis.getSelectedItem();
        String namaProduk = "Makaroni " + jenis;
        String hargaStr = txtHarga.getText().trim();
        String stokStr = txtStok.getText().trim();

        if (hargaStr.isEmpty() || stokStr.isEmpty()) {
            DialogHelper.peringatan(this, "Semua field wajib diisi!");
            return;
        }

        double harga;
        int stok;
        try {
            harga = Double.parseDouble(hargaStr);
            stok = Integer.parseInt(stokStr);
        } catch (NumberFormatException ex) {
            DialogHelper.peringatan(this, "Harga dan stok harus berupa angka!");
            return;
        }

        Connection conn = Koneksi.getKoneksi();
        if (conn == null) {
            return;
        }
        String sql = "INSERT INTO produk (nama_produk, jenis, harga, stok) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaProduk);
            ps.setString(2, jenis);
            ps.setDouble(3, harga);
            ps.setInt(4, stok);
            ps.executeUpdate();
            DialogHelper.sukses(this, "Produk baru berhasil ditambahkan.");
            parent.muatData();
            dispose();
        } catch (Exception e) {
            DialogHelper.error(this, "Gagal menyimpan data:\n" + e.getMessage());
        }
    }
}
