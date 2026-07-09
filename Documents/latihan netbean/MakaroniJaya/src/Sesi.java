package makaronijaya;

/**
 * Menyimpan info user yang sedang login (session sederhana),
 * supaya nama kasir bisa ditampilkan di nota / struk transaksi.
 */
public class Sesi {

    private static String namaLengkap = "-";
    private static String username = "-";
    private static String level = "-";

    private Sesi() {
        // Kelas utilitas, tidak perlu diinstansiasi.
    }

    public static void login(String username, String namaLengkap, String level) {
        Sesi.username = username;
        Sesi.namaLengkap = namaLengkap;
        Sesi.level = level;
    }

    public static void logout() {
        username = "-";
        namaLengkap = "-";
        level = "-";
    }

    public static String getNamaLengkap() {
        return namaLengkap;
    }

    public static String getUsername() {
        return username;
    }

    public static String getLevel() {
        return level;
    }
}
