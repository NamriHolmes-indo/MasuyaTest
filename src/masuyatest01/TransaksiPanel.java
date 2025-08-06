package masuyatest01;

import masuyatest01.TransaksiController;
import masuyatest01.TransaksiItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author badri
 */
public class TransaksiPanel extends JPanel {
    private JComboBox<String> cbCustomer, cbProduk;
    private JTextField tfQty, tfD1, tfD2, tfD3, tfHarga;
    private JButton btnTambah, btnSimpan;
    private JTable table;
    private DefaultTableModel tableModel;

    private TransaksiController controller;
    
    public TransaksiPanel() {
        controller = new TransaksiController(this);

        setLayout(new BorderLayout(10, 10));

        cbCustomer = new JComboBox<>();
        cbProduk = new JComboBox<>();
        tfQty = new JTextField("1");
        tfD1 = new JTextField("0");
        tfD2 = new JTextField("0");
        tfD3 = new JTextField("0");
        tfHarga = new JTextField();
        tfHarga.setEditable(false);

        btnTambah = new JButton("Tambah Transaksi");
        btnSimpan = new JButton("Simpan Faktur");

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Customer:")); form.add(cbCustomer);
        form.add(new JLabel("Produk:")); form.add(cbProduk);
        form.add(new JLabel("Harga:")); form.add(tfHarga);
        form.add(new JLabel("Qty:")); form.add(tfQty);
        form.add(new JLabel("Diskon 1 (%):")); form.add(tfD1);
        form.add(new JLabel("Diskon 2 (%):")); form.add(tfD2);
        form.add(new JLabel("Diskon 3 (%):")); form.add(tfD3);
        form.add(btnTambah); form.add(btnSimpan);

        tableModel = new DefaultTableModel(new String[]{
            "Kode", "Nama Produk", "Qty", "Harga", "Diskon1", "Diskon2", "Diskon3", "Total"
        }, 0);
        table = new JTable(tableModel);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadCustomer();
        loadProduk();

        cbProduk.addActionListener(e -> loadHargaProduk());
        btnTambah.addActionListener(e -> tambahTransaksi());
        btnSimpan.addActionListener(e -> simpanFaktur());
    }

    private void loadCustomer() {
        cbCustomer.removeAllItems();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT kodeCustomer, namaCustomer FROM customer.pelanggan")) {
            while (rs.next()) {
                cbCustomer.addItem(rs.getString("kodeCustomer") + " - " + rs.getString("namaCustomer"));
            }
        } catch (Exception e) {
            showError("Gagal load customer: " + e.getMessage());
        }
    }

    private void loadProduk() {
        cbProduk.removeAllItems();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT kodeProduk, namaProduk FROM produk.barang")) {
            while (rs.next()) {
                cbProduk.addItem(rs.getString("kodeProduk") + " - " + rs.getString("namaProduk"));
            }
        } catch (Exception e) {
            showError("Gagal load produk: " + e.getMessage());
        }
    }

    private void loadHargaProduk() {
        String selected = (String) cbProduk.getSelectedItem();
        if (selected == null) return;

        String kode = selected.split(" - ")[0];
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT harga FROM produk.barang WHERE kodeProduk = ?")) {
            ps.setString(1, kode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfHarga.setText(String.valueOf(rs.getInt("harga")));
            }
        } catch (Exception e) {
            showError("Gagal load harga: " + e.getMessage());
        }
    }

    private void tambahTransaksi() {
        String cust = (String) cbCustomer.getSelectedItem();
        String prod = (String) cbProduk.getSelectedItem();
        if (cust == null || prod == null) {
            showError("Pilih customer dan produk terlebih dahulu!");
            return;
        }

        try {
            String kodeCustomer = cust.split(" - ")[0];
            String kodeProduk = prod.split(" - ")[0];
            String namaProduk = prod.split(" - ")[1];
            int qty = Integer.parseInt(tfQty.getText().trim());
            int diskon1 = Integer.parseInt(tfD1.getText().trim());
            int diskon2 = Integer.parseInt(tfD2.getText().trim());
            int diskon3 = Integer.parseInt(tfD3.getText().trim());
            int harga = Integer.parseInt(tfHarga.getText().trim());

//            controller.tambahPendingTransaksi(kodeCustomer, kodeProduk, namaProduk, qty, diskon1, diskon2, diskon3, harga);
            int hargaUnit = Integer.parseInt(tfHarga.getText().trim());
            controller.tambahPendingTransaksi(kodeCustomer, kodeProduk, namaProduk, qty, diskon1, diskon2, diskon3, hargaUnit);
//            controller.tambahPendingTransaksi(kodeCustomer, kodeProduk, namaProduk, qty, diskon1, diskon2, diskon3, hargaUnit);
            controller.simpanFaktur(kodeCustomer);

            int hargaTotal = controller.hitungTotalHarga(qty, harga, diskon1, diskon2, diskon3);

            tableModel.addRow(new Object[]{
                kodeProduk, namaProduk, qty, harga, diskon1, diskon2, diskon3, hargaTotal
            });

        } catch (Exception ex) {
            showError("Input tidak valid: " + ex.getMessage());
        }
    }

    private void simpanFaktur() {
        String selected = (String) cbCustomer.getSelectedItem();
        if (selected == null) {
            showError("Pilih customer terlebih dahulu.");
            return;
        }

        String kodeCustomer = selected.split(" - ")[0];

        try {
            controller.simpanFaktur(kodeCustomer);
            clearForm();
            showSuccess("Faktur berhasil disimpan!");
        } catch (Exception e) {
            showError("Gagal simpan faktur: " + e.getMessage());
        }
    }

    public void clearForm() {
        tableModel.setRowCount(0);
        tfQty.setText("1");
        tfD1.setText("0");
        tfD2.setText("0");
        tfD3.setText("0");
        tfHarga.setText("");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void tambahItem() {
        String cust = (String) cbCustomer.getSelectedItem();
        String sel = (String) cbProduk.getSelectedItem();
        if (cust == null || sel == null) return;

        String kodeCustomer = cust.split(" - ")[0];
        String kodeProduk = sel.split(" - ")[0];
        String namaProduk = sel.split(" - ")[1];

        int qty = Integer.parseInt(tfQty.getText().trim());
        int diskon1 = Integer.parseInt(tfD1.getText().trim());
        int diskon2 = Integer.parseInt(tfD2.getText().trim());
        int diskon3 = Integer.parseInt(tfD3.getText().trim());
        int hargaUnit = Integer.parseInt(tfHarga.getText().trim());

        controller.tambahPendingTransaksi(kodeCustomer, kodeProduk, namaProduk, qty, diskon1, diskon2, diskon3, hargaUnit);

        int total = controller.hitungTotalHarga(qty, hargaUnit, diskon1, diskon2, diskon3);
        tableModel.addRow(new Object[]{kodeProduk, namaProduk, qty, hargaUnit, diskon1, diskon2, diskon3, total});
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
