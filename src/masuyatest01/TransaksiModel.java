package masuyatest01;

import java.sql.*;
import java.util.*;

/**
 *
 * @author badri
 */
public class TransaksiModel {
    public void simpanFaktur(Faktur faktur) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO customer.faktur (noFaktur, total, kodeCustomer) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, faktur.noFaktur);
        ps.setInt(2, faktur.total);
        ps.setString(3, faktur.kodeCustomer);
        ps.executeUpdate();
        ps.close();
    }

    public void simpanTransaksiDariPending(String noFaktur, String kodeCustomer) throws Exception {
        Connection conn = DBConnection.getConnection();

        String sqlSelect = "SELECT * FROM customer.pending_transaksi WHERE kodeCustomer = ?";
        PreparedStatement psSelect = conn.prepareStatement(sqlSelect);
        psSelect.setString(1, kodeCustomer);
        ResultSet rs = psSelect.executeQuery();

        List<Transaksi> transaksiList = new ArrayList<>();

        while (rs.next()) {
            Transaksi t = new Transaksi();
            t.idTransaksi = UUID.randomUUID().toString();
            t.qty = rs.getInt("qty");
            t.diskon1 = rs.getInt("diskon1");
            t.diskon2 = rs.getInt("diskon2");
            t.diskon3 = rs.getInt("diskon3");
            t.hargaUnit = rs.getInt("hargaUnit");
            t.hargaTotal = rs.getInt("hargaTotal");
            t.noFaktur = noFaktur;
            t.kodeProduk = rs.getString("kodeProduk");

            transaksiList.add(t);
        }

        rs.close();
        psSelect.close();

        String sqlInsert = "INSERT INTO customer.transaksi (id_transaksi, qty, diskon1, diskon2, diskon3, hargaUnit, hargaTotal, noFaktur, kodeProduk) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement psInsert = conn.prepareStatement(sqlInsert);

        for (Transaksi t : transaksiList) {
            psInsert.setString(1, t.idTransaksi);
            psInsert.setInt(2, t.qty);
            psInsert.setInt(3, t.diskon1);
            psInsert.setInt(4, t.diskon2);
            psInsert.setInt(5, t.diskon3);
            psInsert.setInt(6, t.hargaUnit);
            psInsert.setInt(7, t.hargaTotal);
            psInsert.setString(8, t.noFaktur);
            psInsert.setString(9, t.kodeProduk);
            psInsert.addBatch();

            // Kurangi stok
            kurangiStok(t.kodeProduk, t.qty);
        }

        psInsert.executeBatch();
        psInsert.close();

        // Hapus dari pending
        String sqlDelete = "DELETE FROM customer.pending_transaksi WHERE kodeCustomer = ?";
        PreparedStatement psDelete = conn.prepareStatement(sqlDelete);
        psDelete.setString(1, kodeCustomer);
        psDelete.executeUpdate();
        psDelete.close();
    }

    public void tambahKePending(Transaksi t) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO customer.pending_transaksi (id_antrian, qty, diskon1, diskon2, diskon3, hargaUnit, hargaTotal, kodeProduk, kodeCustomer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, t.idTransaksi); // UUID
        ps.setInt(2, t.qty);
        ps.setInt(3, t.diskon1);
        ps.setInt(4, t.diskon2);
        ps.setInt(5, t.diskon3);
        ps.setInt(6, t.hargaUnit);
        ps.setInt(7, t.hargaTotal);
        ps.setString(8, t.kodeProduk);
        ps.setString(9, t.kodeCustomer);
        ps.executeUpdate();
        ps.close();
    }

    public List<Transaksi> getPendingTransaksiByCustomer(String kodeCustomer) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM customer.pending_transaksi WHERE kodeCustomer = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, kodeCustomer);
        ResultSet rs = ps.executeQuery();

        List<Transaksi> list = new ArrayList<>();
        while (rs.next()) {
            Transaksi t = new Transaksi();
            t.idTransaksi = rs.getString("id_antrian");
            t.qty = rs.getInt("qty");
            t.diskon1 = rs.getInt("diskon1");
            t.diskon2 = rs.getInt("diskon2");
            t.diskon3 = rs.getInt("diskon3");
            t.hargaUnit = rs.getInt("hargaUnit");
            t.hargaTotal = rs.getInt("hargaTotal");
            t.kodeProduk = rs.getString("kodeProduk");
            t.kodeCustomer = rs.getString("kodeCustomer");
            list.add(t);
        }

        rs.close();
        ps.close();
        return list;
    }

    public void kurangiStok(String kodeProduk, int jumlah) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE produk.barang SET stok = stok - ? WHERE kodeProduk = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, jumlah);
        ps.setString(2, kodeProduk);
        ps.executeUpdate();
        ps.close();
    }

    public int getHargaProduk(String kodeProduk) throws Exception {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT harga FROM produk.barang WHERE kodeProduk = ?");
        ps.setString(1, kodeProduk);
        ResultSet rs = ps.executeQuery();
        int harga = 0;
        if (rs.next()) harga = rs.getInt(1);
        rs.close(); ps.close();
        return harga;
    }
    public String generateNoFakturOtomatis() throws Exception {
        Connection conn = DBConnection.getConnection();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR) % 100; // ambil 2 digit terakhir
        int month = cal.get(Calendar.MONTH) + 1; // Januari = 0, jadi +1
        String yymm = String.format("%02d%02d", year, month); // misal 2508

        String prefix = "INV/" + yymm + "/";
        String sql = "SELECT MAX(noFaktur) AS maxFaktur FROM customer.faktur WHERE noFaktur LIKE ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, prefix + "%");
        ResultSet rs = ps.executeQuery();

        int nextUrutan = 1;
        if (rs.next() && rs.getString("maxFaktur") != null) {
            String maxFaktur = rs.getString("maxFaktur"); // contoh: INV/2508/0012
            String[] parts = maxFaktur.split("/");
            nextUrutan = Integer.parseInt(parts[2]) + 1;
        }

        rs.close();
        ps.close();

        String finalFaktur = prefix + String.format("%04d", nextUrutan); // jadi INV/2508/0001
        return finalFaktur;
    }
}
