package makaronijaya;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Kelas untuk mengelola koneksi ke database MySQL.
 * Sesuaikan DB_USER dan DB_PASS dengan konfigurasi MySQL di komputer masing-masing.
 */
public class Koneksi {

    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "db_makaroni";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static final String URL =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Makassar";

    private static Connection koneksi;

    /**
     * Mengambil koneksi aktif ke database. Jika belum ada, koneksi baru akan dibuat.
     */
    public static Connection getKoneksi() {
        try {
            if (koneksi == null || koneksi.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                koneksi = DriverManager.getConnection(URL, DB_USER, DB_PASS);
            }
        } catch (ClassNotFoundException e) {
            DialogHelper.error(null, "Driver Tidak Ditemukan",
                    "Driver MySQL (mysql-connector-j) belum ditambahkan ke project.\n"
                    + "Klik kanan project > Properties > Libraries > Add JAR/Folder.");
        } catch (Exception e) {
            DialogHelper.error(null, "Koneksi Gagal",
                    "Gagal terhubung ke database:\n" + e.getMessage());
        }
        return koneksi;
    }
}
