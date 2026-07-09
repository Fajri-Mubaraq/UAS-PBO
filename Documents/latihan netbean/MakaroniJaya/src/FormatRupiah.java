package makaronijaya;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilitas format mata uang Rupiah yang dipakai bersama di seluruh aplikasi,
 * supaya tampilannya konsisten dan TANPA angka desimal (",00") di belakang,
 * karena harga makaroni selalu bilangan bulat.
 */
public class FormatRupiah {

    private FormatRupiah() {
        // Kelas utilitas, tidak perlu diinstansiasi.
    }

    /** Membuat NumberFormat Rupiah baru tanpa desimal, mis. "Rp8.000" (bukan "Rp8.000,00"). */
    public static NumberFormat buat() {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        format.setMaximumFractionDigits(0);
        format.setMinimumFractionDigits(0);
        return format;
    }
}
