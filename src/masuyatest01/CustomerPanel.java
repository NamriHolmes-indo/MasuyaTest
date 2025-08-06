package masuyatest01;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author badri
 */
public class CustomerPanel extends javax.swing.JPanel {

    private JTextField tfKode, tfNama, tfAlamatLengkap, tfKodePos;
    private JComboBox<String> cbProvinsi, cbKota, cbKecamatan, cbKelurahan;
    private JTable table;
    private DefaultTableModel tableModel;
    private boolean isLoadingCombo = false;

    private Map<String, Long> provinsiMap = new HashMap<>();
    private Map<String, Long> kotaMap = new HashMap<>();
    private Map<String, Long> kecamatanMap = new HashMap<>();
    private Map<String, Long> kelurahanMap = new HashMap<>();

    public CustomerPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        tfKode = new JTextField(20);
        tfNama = new JTextField(20);
        tfAlamatLengkap = new JTextField(30);
        tfKodePos = new JTextField(6);

        cbProvinsi = new JComboBox<>();
        cbKota = new JComboBox<>();
        cbKecamatan = new JComboBox<>();
        cbKelurahan = new JComboBox<>();

        cbKota.setEnabled(false);
        cbKecamatan.setEnabled(false);
        cbKelurahan.setEnabled(false);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Kode Customer:"), gbc);
        gbc.gridx = 1; formPanel.add(tfKode, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Nama Customer:"), gbc);
        gbc.gridx = 1; formPanel.add(tfNama, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Alamat Lengkap:"), gbc);
        gbc.gridx = 1; formPanel.add(tfAlamatLengkap, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Provinsi:"), gbc);
        gbc.gridx = 1; formPanel.add(cbProvinsi, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Kota:"), gbc);
        gbc.gridx = 1; formPanel.add(cbKota, gbc);

        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Kecamatan:"), gbc);
        gbc.gridx = 1; formPanel.add(cbKecamatan, gbc);

        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(new JLabel("Kelurahan:"), gbc);
        gbc.gridx = 1; formPanel.add(cbKelurahan, gbc);

        gbc.gridx = 0; gbc.gridy = 7; formPanel.add(new JLabel("Kode Pos:"), gbc);
        gbc.gridx = 1; formPanel.add(tfKodePos, gbc);

        JButton btnTambah = new JButton("Tambah Customer");
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        formPanel.add(btnTambah, gbc);
        
        tfNama.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateKode(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateKode(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}

            private void updateKode() {
                String nama = tfNama.getText().trim();
                if (!nama.isEmpty()) {
                    tfKode.setText(generateKodeCustomer(nama));
                }
            }
        });

        tableModel = new DefaultTableModel(new Object[]{
            "Kode", "Nama", "Alamat", "Provinsi", "Kota", "Kecamatan", "Kelurahan", "Kode Pos", "Aksi"
        }, 0);
        table = new JTable(tableModel);
        table.getColumn("Aksi").setCellRenderer(new ButtonRenderer());
//        table.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox(), this, "hapusCustomer"));
        table.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox(), this, "hapusCustomer"));

        JScrollPane tableScroll = new JScrollPane(table);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tableScroll);
        add(splitPane, BorderLayout.CENTER);

        loadProvinsi();

        cbProvinsi.addActionListener(e -> {
            if (!isLoadingCombo) loadKota();
        });
        cbKota.addActionListener(e -> {
            if (!isLoadingCombo) loadKecamatan();
        });
        cbKecamatan.addActionListener(e -> {
            if (!isLoadingCombo) loadKelurahan();
        });

        btnTambah.addActionListener(e -> tambahCustomer());
        
        loadPelanggan();
    }
    
    private void loadPelanggan() {
        tableModel.setRowCount(0); // Kosongkan semua data tabel

        boolean adaData = false;

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT c.kodeCustomer, c.namaCustomer, c.alamatLengkap, c.kodePos, " +
                 "p.namaProvinsi, k.namaKota, kc.namaKecamatan, kl.namaKelurahan " +
                 "FROM customer.pelanggan c " +
                 "JOIN customer.provinsi p ON c.id_provinsi = p.id_provinsi " +
                 "JOIN customer.kota k ON c.id_kota = k.id_kota " +
                 "JOIN customer.kecamatan kc ON c.id_kecamatan = kc.id_kecamatan " +
                 "JOIN customer.kelurahan kl ON c.id_kelurahan = kl.id_kelurahan"
             )
        ) {
            while (rs.next()) {
                adaData = true;

                String kode = rs.getString("kodeCustomer");
                String nama = rs.getString("namaCustomer");
                String alamat = rs.getString("alamatLengkap");
                String kodePos = rs.getString("kodePos");
                String provinsi = rs.getString("namaProvinsi");
                String kota = rs.getString("namaKota");
                String kecamatan = rs.getString("namaKecamatan");
                String kelurahan = rs.getString("namaKelurahan");

                tableModel.addRow(new Object[]{
                    kode, nama, alamat, provinsi, kota, kecamatan, kelurahan, kodePos, "Hapus"
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load data pelanggan: " + e.getMessage());
        }

        // Jika setelah query, tidak ada data, baru tambahkan dummy
        if (!adaData) {
            tableModel.addRow(new Object[]{
                "Data Tidak ditemukan", "", "", "", "", "", "", "", ""
            });
        }
    }

    public void hapusCustomer(String kodeCustomer) {
        if (kodeCustomer == null || kodeCustomer.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin menghapus customer " + kodeCustomer + "?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "DELETE FROM customer.pelanggan WHERE kodeCustomer = ?"
             )) {
            ps.setString(1, kodeCustomer);
            ps.executeUpdate();
            loadPelanggan(); // Refresh tabel setelah penghapusan
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal menghapus customer: " + e.getMessage()
            );
        }
    }

    private void loadProvinsi() {
        provinsiMap.clear();
        cbProvinsi.removeAllItems();
        isLoadingCombo = true;

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_provinsi, namaProvinsi FROM customer.provinsi")) {

            while (rs.next()) {
                String name = rs.getString("namaProvinsi");
                long id = rs.getLong("id_provinsi");
                cbProvinsi.addItem(name);
                provinsiMap.put(name, id);
            }

            if (cbProvinsi.getItemCount() > 0) {
                cbProvinsi.setSelectedIndex(0);
            }

        } catch (Exception e) {
            showError("Gagal load provinsi: " + e.getMessage());
        } finally {
            isLoadingCombo = false;
            loadKota();
        }
    }

    private void loadKota() {
        kotaMap.clear();
        cbKota.removeAllItems();
        cbKota.setEnabled(false);
        isLoadingCombo = true;

        String selected = (String) cbProvinsi.getSelectedItem();
        if (selected == null) {
            isLoadingCombo = false;
            return;
        }

        long idProvinsi = provinsiMap.getOrDefault(selected, -1L);
        if (idProvinsi == -1) {
            isLoadingCombo = false;
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id_kota, namaKota FROM customer.kota WHERE id_provinsi = ?")) {

            ps.setLong(1, idProvinsi);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("namaKota");
                long id = rs.getLong("id_kota");
                cbKota.addItem(name);
                kotaMap.put(name, id);
            }

            if (cbKota.getItemCount() > 0) {
                cbKota.setSelectedIndex(0); // tidak trigger listener karena sedang loading
            }

            cbKota.setEnabled(true);
        } catch (Exception e) {
            showError("Gagal load kota: " + e.getMessage());
        } finally {
            isLoadingCombo = false;
            loadKecamatan(); // ← jika kamu ingin isi otomatis
        }
    }

    private void loadKecamatan() {
        kecamatanMap.clear();
        cbKecamatan.removeAllItems();
        cbKecamatan.setEnabled(false);

        if (isLoadingCombo) return;
        isLoadingCombo = true;

        String selected = (String) cbKota.getSelectedItem();
        if (selected == null) {
            isLoadingCombo = false;
            return;
        }

        long idKota = kotaMap.getOrDefault(selected, -1L);
        if (idKota == -1) {
            isLoadingCombo = false;
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id_kecamatan, namaKecamatan FROM customer.kecamatan WHERE id_kota = ?")) {

            ps.setLong(1, idKota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("namaKecamatan");
                long id = rs.getLong("id_kecamatan");
                cbKecamatan.addItem(name);
                kecamatanMap.put(name, id);
            }

            cbKecamatan.setEnabled(true);
            if (cbKecamatan.getItemCount() > 0) {
                cbKecamatan.setSelectedIndex(0);
            }

        } catch (Exception e) {
            showError("Gagal load kecamatan: " + e.getMessage());
        } finally {
            isLoadingCombo = false;
            loadKelurahan(); // otomatis panggil loadKelurahan setelah kecamatan terisi
        }
    }

    private void loadKelurahan() {
        kelurahanMap.clear();
        cbKelurahan.removeAllItems();
        cbKelurahan.setEnabled(false);

        if (isLoadingCombo) return;
        isLoadingCombo = true;

        String selected = (String) cbKecamatan.getSelectedItem();
        if (selected == null) {
            isLoadingCombo = false;
            return;
        }

        long idKecamatan = kecamatanMap.getOrDefault(selected, -1L);
        if (idKecamatan == -1) {
            isLoadingCombo = false;
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id_kelurahan, namaKelurahan FROM customer.kelurahan WHERE id_kecamatan = ?")) {

            ps.setLong(1, idKecamatan);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("namaKelurahan");
                long id = rs.getLong("id_kelurahan");
                cbKelurahan.addItem(name);
                kelurahanMap.put(name, id);
            }

            cbKelurahan.setEnabled(true);
            if (cbKelurahan.getItemCount() > 0) {
                cbKelurahan.setSelectedIndex(0);
            }

        } catch (Exception e) {
            showError("Gagal load kelurahan: " + e.getMessage());
        } finally {
            isLoadingCombo = false;
        }
    }


    private void tambahCustomer() {
        String kode = tfKode.getText().trim();
        String nama = tfNama.getText().trim();
        String alamat = tfAlamatLengkap.getText().trim();
        String kodePosStr = tfKodePos.getText().trim();

        if (kode.isEmpty() || nama.isEmpty() || alamat.isEmpty() || kodePosStr.isEmpty()
                || cbProvinsi.getSelectedItem() == null || cbKota.getSelectedItem() == null
                || cbKecamatan.getSelectedItem() == null || cbKelurahan.getSelectedItem() == null) {
            showError("Semua field harus diisi!");
            return;
        }
        
        long kodePos;
        try {
            kodePos = Long.parseLong(kodePosStr);
        } catch (NumberFormatException e) {
            showError("Kode pos harus berupa angka!");
            return;
        }
        
        if (kode.matches("^[a-zA-Z0-9]+$")) {
            System.out.println("Input valid");
        } else {
            JOptionPane.showMessageDialog(this, "Kode customer tidak boleh ada karakter, harus huruf dan angka saja!");
            return;
        }
        
        System.out.println("Provinsi: " + cbProvinsi.getSelectedItem());
        System.out.println("Kota: " + cbKota.getSelectedItem());
        System.out.println("Kecamatan: " + cbKecamatan.getSelectedItem());
        System.out.println("Kelurahan: " + cbKelurahan.getSelectedItem());

        long idProvinsi = provinsiMap.get((String) cbProvinsi.getSelectedItem());
        long idKota = kotaMap.get((String) cbKota.getSelectedItem());
        long idKecamatan = kecamatanMap.get((String) cbKecamatan.getSelectedItem());
        long idKelurahan = kelurahanMap.get((String) cbKelurahan.getSelectedItem());
                
        System.out.println("ID Provinsi: " + idProvinsi);
        System.out.println("ID Kota: " + idKota);
        System.out.println("ID Kecamatan: " + idKecamatan);
        System.out.println("ID Kelurahan: " + idKelurahan);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO customer.pelanggan (kodeCustomer, namaCustomer, alamatLengkap, id_kelurahan, id_kecamatan, id_kota, id_provinsi, kodePos) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, kode);
            ps.setString(2, nama);
            ps.setString(3, alamat);
            ps.setLong(4, idKelurahan);
            ps.setLong(5, idKecamatan);
            ps.setLong(6, idKota);
            ps.setLong(7, idProvinsi);
            ps.setLong(8, kodePos);
            ps.executeUpdate();

            tableModel.addRow(new Object[]{
                    kode, nama, alamat, cbProvinsi.getSelectedItem(),
                    cbKota.getSelectedItem(), cbKecamatan.getSelectedItem(),
                    cbKelurahan.getSelectedItem(), kodePos
            });

            clearForm();
            loadPelanggan();
        } catch (Exception e) {
            showError("Gagal tambah customer: " + e.getMessage());
        }
    }

    private void clearForm() {
        tfKode.setText("");
        tfNama.setText("");
        tfAlamatLengkap.setText("");
        tfKodePos.setText("");
        cbProvinsi.setSelectedIndex(-1);
        cbKota.removeAllItems();
        cbKecamatan.removeAllItems();
        cbKelurahan.removeAllItems();
        cbKota.setEnabled(false);
        cbKecamatan.setEnabled(false);
        cbKelurahan.setEnabled(false);
    }
    
    private String generateKodeCustomer(String nama) {
        String inisial = "";
        for (String word : nama.trim().split("\\s+")) {
            if (!word.isEmpty()) {
                inisial += word.substring(0, 1).toUpperCase();
            }
        }

        String timeStamp = new java.text.SimpleDateFormat("ddMMyyHHmmss").format(new java.util.Date());
        int urutan = getJumlahCustomer(); // Hitung jumlah customer dari DB
        return String.format("CUS%03d%s%s", urutan + 1, inisial, timeStamp);
    }

    private int getJumlahCustomer() {
        int count = 0;
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM customer.pelanggan");
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menghitung jumlah customer: " + e.getMessage());
        }
        return count;
    }


    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg);
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

