package makaronijaya;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Jendela nota/struk transaksi dengan tampilan modern (dibangun di atas
 * NotaPanel) dan tombol untuk mencetak nota ke printer.
 */
public class NotaFrame extends JFrame implements Printable {

    private final NotaPanel notaPanel;

    public NotaFrame(String noTransaksi, String namaPembeli,
            List<NotaPanel.ItemNota> items, double totalKeseluruhan) {

        notaPanel = new NotaPanel(noTransaksi, namaPembeli, Sesi.getNamaLengkap(), items, totalKeseluruhan);

        setTitle("Nota Transaksi - " + noTransaksi);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 640);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIStyle.CREAM_BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildNotaScroll(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(UIStyle.ORANGE);
        header.setPreferredSize(new Dimension(420, 50));
        JLabel lbl = new JLabel("\uD83E\uDDFE  Nota Transaksi");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(UIStyle.WHITE);
        header.add(lbl);
        return header;
    }

    private JScrollPane buildNotaScroll() {
        JPanel wrapper = new JPanel();
        wrapper.setBackground(UIStyle.CREAM_BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        // Bingkai tipis + sedikit bayangan di sekitar nota supaya terlihat seperti "kartu"
        JPanel cardBorder = new JPanel(new BorderLayout());
        cardBorder.setBackground(UIStyle.CREAM_BG);
        cardBorder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(225, 205, 175), 1, true),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        cardBorder.add(notaPanel, BorderLayout.CENTER);

        wrapper.add(cardBorder);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(UIStyle.CREAM_BG);
        return scroll;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(UIStyle.CREAM_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        RoundedButton btnCetak = new RoundedButton("\uD83D\uDDA8  Cetak Nota", UIStyle.ORANGE_DARK);
        RoundedButton btnTutup = new RoundedButton("Tutup", java.awt.Color.GRAY);
        btnCetak.setPreferredSize(new Dimension(160, 42));
        btnTutup.setPreferredSize(new Dimension(120, 42));

        btnCetak.addActionListener(e -> cetakNota());
        btnTutup.addActionListener(e -> dispose());

        footer.add(btnCetak);
        footer.add(btnTutup);
        return footer;
    }

    private void cetakNota() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        if (job.printDialog()) {
            try {
                job.print();
                DialogHelper.sukses(this, "Nota berhasil dikirim ke printer.");
            } catch (PrinterException e) {
                DialogHelper.error(this, "Error Cetak", "Gagal mencetak nota:\n" + e.getMessage());
            }
        }
    }

    /** Implementasi Printable: menggambar ulang isi nota yang sama persis ke halaman printer. */
    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        notaPanel.gambarNota(g2, notaPanel.getLebarNota());
        return Printable.PAGE_EXISTS;
    }
}
