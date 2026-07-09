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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Frame login aplikasi Makaroni Jaya. Login diverifikasi ke tabel users.
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("Makaroni Jaya - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIStyle.CREAM_BG);
        setLayout(new BorderLayout());

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildFormPanel(), BorderLayout.CENTER);
    }

    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel();
        header.setBackground(UIStyle.ORANGE);
        header.setPreferredSize(new Dimension(420, 130));
        header.setLayout(new javax.swing.BoxLayout(header, javax.swing.BoxLayout.Y_AXIS));

        JLabel lblEmoji = new JLabel("\uD83E\uDD64", SwingConstants.CENTER); // emoji makaroni-ish
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblEmoji.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("MAKARONI JAYA", SwingConstants.CENTER);
        lblTitle.setFont(UIStyle.FONT_TITLE);
        lblTitle.setForeground(UIStyle.WHITE);
        lblTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Asin \u2022 Jagung Bakar \u2022 Balado", SwingConstants.CENTER);
        lblSubtitle.setFont(UIStyle.FONT_SUBTITLE);
        lblSubtitle.setForeground(UIStyle.WHITE);
        lblSubtitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        header.add(javax.swing.Box.createVerticalStrut(12));
        header.add(lblEmoji);
        header.add(lblTitle);
        header.add(lblSubtitle);
        header.add(javax.swing.Box.createVerticalStrut(10));
        return header;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyle.CREAM_BG);
        form.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIStyle.FONT_LABEL);
        lblUser.setForeground(UIStyle.BROWN_TEXT);
        gbc.gridy = 0;
        form.add(lblUser, gbc);

        txtUsername = new JTextField();
        styleField(txtUsername);
        gbc.gridy = 1;
        form.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIStyle.FONT_LABEL);
        lblPass.setForeground(UIStyle.BROWN_TEXT);
        gbc.gridy = 2;
        form.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        styleField(txtPassword);
        gbc.gridy = 3;
        form.add(txtPassword, gbc);

        RoundedButton btnLogin = new RoundedButton("MASUK", UIStyle.ORANGE_DARK);
        btnLogin.setPreferredSize(new Dimension(200, 42));
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 8, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        form.add(btnLogin, gbc);

        JLabel lblHint = new JLabel("<html><center>Default: admin / admin123</center></html>");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(Color.GRAY);
        gbc.gridy = 5;
        gbc.insets = new Insets(4, 0, 0, 0);
        form.add(lblHint, gbc);

        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());

        return form;
    }

    private void styleField(JTextField field) {
        field.setFont(UIStyle.FONT_FIELD);
        field.setPreferredSize(new Dimension(260, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.ORANGE, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            DialogHelper.peringatan(this, "Username dan password wajib diisi!");
            return;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        Connection conn = Koneksi.getKoneksi();
        if (conn == null) {
            return;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String namaLengkap = rs.getString("nama_lengkap");
                String level = rs.getString("level");
                Sesi.login(username, namaLengkap, level);
                DialogHelper.sukses(this, "Selamat datang, " + namaLengkap + "!");
                new MenuUtamaFrame(namaLengkap, level).setVisible(true);
                dispose();
            } else {
                DialogHelper.error(this, "Login Gagal", "Username atau password salah!");
            }
        } catch (Exception e) {
            DialogHelper.error(this, "Terjadi kesalahan:\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
