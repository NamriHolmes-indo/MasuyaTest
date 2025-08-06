package masuyatest01;

import javax.swing.*;
import java.sql.*;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author badri
 */
public class TransaksiController {
    private TransaksiPanel view;
    private TransaksiModel model;

    public TransaksiController(TransaksiPanel view) {
        this.view = view;
        this.model = new TransaksiModel();
    }

    public void tambahPendingTransaksi(String kodeCustomer, String kodeProduk, String namaProduk,
                                       int qty, int diskon1, int diskon2, int diskon3, int hargaUnit) {
        try {
            Transaksi t = new Transaksi();
            t.idTransaksi = UUID.randomUUID().toString();
            t.kodeCustomer = kodeCustomer;
            t.kodeProduk = kodeProduk;
            t.qty = qty;
            t.diskon1 = diskon1;
            t.diskon2 = diskon2;
            t.diskon3 = diskon3;
            t.hargaUnit = hargaUnit;
            t.hargaTotal = hitungTotalHarga(qty, hargaUnit, diskon1, diskon2, diskon3);
            model.tambahKePending(t);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Gagal menambahkan transaksi: " + e.getMessage());
        }
    }

    public void simpanFaktur(String kodeCustomer) throws Exception {
        String noFaktur = model.generateNoFakturOtomatis();
        try {
            List<Transaksi> list = model.getPendingTransaksiByCustomer(kodeCustomer);
            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Tidak ada data transaksi untuk disimpan.");
                return;
            }

            int total = 0;
            for (Transaksi t : list) {
                total += t.hargaTotal;
            }

            Faktur faktur = new Faktur();
            faktur.noFaktur = noFaktur;
            faktur.kodeCustomer = kodeCustomer;
            faktur.total = total;

            model.simpanFaktur(faktur);
            model.simpanTransaksiDariPending(noFaktur, kodeCustomer);

            JOptionPane.showMessageDialog(view, "Faktur berhasil disimpan! No Faktur: " + noFaktur);
            view.clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Gagal simpan faktur: " + e.getMessage());
        }
    }

    public int hitungTotalHarga(int qty, int hargaUnit, int diskon1, int diskon2, int diskon3) {
        double harga = hargaUnit;
        harga -= (harga * diskon1 / 100.0);
        harga -= (harga * diskon2 / 100.0);
        harga -= (harga * diskon3 / 100.0);
        return (int) Math.round(harga * qty);
    }
    
}