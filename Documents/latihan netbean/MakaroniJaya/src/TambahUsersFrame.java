package makaronijaya;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * tambahFrame untuk tabel users: form input data user baru.
 */
public class TambahUsersFrame extends JFrame {

    private final TampilUsersFrame parent;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtNamaLengkap;
    private ModernComboBox<String> cbLevel;

    public TambahUsersFrame(TampilUsersFrame parent) {
        this.parent = parent;

        setTitle("Tambah User Baru");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 380);
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
        JLabel lbl = new JLabel("+ Tambah User");
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

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        txtNamaLengkap = new JTextField();
        cbLevel = new ModernComboBox<>(new String[]{"Admin", "Kasir"});

        addField(form, gbc, 0, "Username", txtUsername);
        addField(form, gbc, 2, "Password", txtPassword);
        addField(form, gbc, 4, "Nama Lengkap", txtNamaLengkap);
        addField(form, gbc, 6, "Level", cbLevel);

        RoundedButton btnSimpan = new RoundedButton("Simpan", UIStyle.GREEN_ACCENT);
        RoundedButton btnBatal = new RoundedButton("Batal", java.awt.Color.GRAY);
        btnSimpan.setPreferredSize(new Dimension(150, 40));
        btnBatal.setPreferredSize(new Dimension(150, 40));

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(UIStyle.CREAM_BG);
        btnPanel.add(btnSimpan);
        btnPanel.add(btnBatal);

        gbc.gridy = 8;
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

        if (field instanceof JTextField) {
            ((JTextField) field).setFont(UIStyle.FONT_FIELD);
            ((JTextField) field).setPreferredSize(new Dimension(280, 34));
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIStyle.ORANGE, 1, true),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        } else if (field instanceof JComboBox) {
            field.setFont(UIStyle.FONT_FIELD);
            field.setPreferredSize(new Dimension(280, 34));
        }
        gbc.gridy = startY + 1;
        form.add(field, gbc);
    }

    private void simpanData() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String namaLengkap = txtNamaLengkap.getText().trim();
        String level = (String) cbLevel.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || namaLengkap.isEmpty()) {
            DialogHelper.peringatan(this, "Semua field wajib diisi!");
            return;
        }

        Connection conn = Koneksi.getKoneksi();
        if (conn == null) {
            return;
        }
        String sql = "INSERT INTO users (username, password, nama_lengkap, level) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, namaLengkap);
            ps.setString(4, level);
            ps.executeUpdate();
            DialogHelper.sukses(this, "User baru berhasil ditambahkan.");
            parent.muatData();
            dispose();
        } catch (Exception e) {
            DialogHelper.error(this, "Gagal menyimpan data:\n" + e.getMessage());
        }
    }
}
