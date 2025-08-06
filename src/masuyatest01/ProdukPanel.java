package masuyatest01;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

/**
 *
 * @author badri
 */
public class ProdukPanel extends javax.swing.JPanel {
    private JTextField tfKode, tfNama, tfHarga, tfStok;
    private JComboBox<String> cbJenis;
    private JTable table;
    private DefaultTableModel tableModel;

    public ProdukPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        tfKode = new JTextField(20);
        tfNama = new JTextField(20);
        tfHarga = new JTextField(10);
        tfStok = new JTextField(5);
        cbJenis = new JComboBox<>();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Kode Produk:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfKode, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nama Produk:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfNama, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Harga:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfHarga, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Stok:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfStok, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Jenis Produk:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbJenis, gbc);

        JButton btnTambah = new JButton("Tambah Produk");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(btnTambah, gbc);

        tableModel = new DefaultTableModel(new Object[]{"Kode", "Jenis", "Nama", "Harga", "Stok", "Tambah", "Kurangi", "Hapus"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return column >= 5;
            }
        };

        table = new JTable(tableModel);
        table.getColumn("Tambah").setCellRenderer(new ButtonRenderer());
        table.getColumn("Kurangi").setCellRenderer(new ButtonRenderer());
        table.getColumn("Hapus").setCellRenderer(new ButtonRenderer());

        table.getColumn("Tambah").setCellEditor(new ButtonEditor(new JCheckBox(), this, "+"));
        table.getColumn("Kurangi").setCellEditor(new ButtonEditor(new JCheckBox(), this, "-"));
        table.getColumn("Hapus").setCellEditor(new ButtonEditor(new JCheckBox(), this, "hapus"));

        JScrollPane scrollPane = new JScrollPane(table);

        // Panel pembungkus untuk padding
        JPanel contentPanel = new JPanel(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, scrollPane);
        contentPanel.add(splitPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        loadJenisProduk();

        tfNama.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { generateKodeProduk(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { generateKodeProduk(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });

        cbJenis.addActionListener(e -> generateKodeProduk());

        btnTambah.addActionListener(e -> tambahProduk());

        loadProduk();
        autoResizeTableColumns(table);
    }
    
    private void autoResizeTableColumns(JTable table) {
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // minimal width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 10, width);
            }
            table.getColumnModel().getColumn(column).setPreferredWidth(width);
        }
    }

    private void generateKodeProduk() {
        String nama = tfNama.getText().trim();
        String selected = (String) cbJenis.getSelectedItem();

        if (nama.isEmpty() || selected == null) return;

        String kodeJenis = selected.split(" - ")[0];
        String inisial = "";
        for (String word : nama.split("\\s+")) {
            if (!word.isEmpty()) {
                inisial += word.substring(0, 1).toUpperCase();
                if (word.length() > 1 && Character.isUpperCase(word.charAt(1))) {
                    inisial += word.substring(1);
                }
            }
        }

        int urutan = hitungUrutanProdukUntukJenis(kodeJenis);
        String urutanStr = String.format("%02d", urutan + 1);

        tfKode.setText(kodeJenis + "-" + urutanStr + "-" + inisial);
    }

    private int hitungUrutanProdukUntukJenis(String kodeJenis) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM produk.barang WHERE kodeJenis = ?");
            ps.setString(1, kodeJenis);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            rs.close(); ps.close(); conn.close();
        } catch (Exception ignored) {}
        return 0;
    }

    private void loadJenisProduk() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT kodeJenis, namaJenis FROM produk.JenisProduk");
            while (rs.next()) {
                cbJenis.addItem(rs.getString("kodeJenis") + " - " + rs.getString("namaJenis"));
            }
            rs.close(); st.close(); conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal load jenis: " + ex.getMessage());
        }
    }

    public void loadProduk() {
        try {
            tableModel.setRowCount(0);
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM produk.barang");
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("kodeProduk"));
                row.add(rs.getString("kodeJenis"));
                row.add(rs.getString("namaProduk"));
                row.add(rs.getInt("harga"));
                row.add(rs.getInt("stok"));
                row.add("+");
                row.add("-");
                row.add("Hapus");
                tableModel.addRow(row);
            }
            rs.close(); st.close(); conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal load produk: " + ex.getMessage());
        }
    }

    private void tambahProduk() {
        String kode = tfKode.getText().trim();
        String nama = tfNama.getText().trim();
        String hargaStr = tfHarga.getText().trim();
        String stokStr = tfStok.getText().trim();
        String jenisCombo = (String) cbJenis.getSelectedItem();

        if (kode.isEmpty() || nama.isEmpty() || hargaStr.isEmpty() || stokStr.isEmpty() || jenisCombo == null) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return;
        }

        String kodeJenis = jenisCombo.split(" - ")[0];

        try {
            int harga = Integer.parseInt(hargaStr);
            int stok = Integer.parseInt(stokStr);

            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO produk.barang (kodeProduk, namaProduk, harga, stok, kodeJenis) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, kode);
            ps.setString(2, nama);
            ps.setInt(3, harga);
            ps.setInt(4, stok);
            ps.setString(5, kodeJenis);
            ps.executeUpdate();
            ps.close(); conn.close();

            loadProduk();
            tfKode.setText(""); tfNama.setText(""); tfHarga.setText(""); tfStok.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan Stok harus berupa angka!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal tambah produk: " + ex.getMessage());
        }
    }

    public void ubahStok(String kodeProduk, int delta) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE produk.barang SET stok = stok + ? WHERE kodeProduk = ?");
            ps.setInt(1, delta);
            ps.setString(2, kodeProduk);
            ps.executeUpdate();
            ps.close(); conn.close();
            loadProduk();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal ubah stok: " + ex.getMessage());
        }
    }

    public void hapusProduk(String kodeProduk) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM produk.barang WHERE kodeProduk = ?");
            ps.setString(1, kodeProduk);
            ps.executeUpdate();
            ps.close(); conn.close();
            loadProduk();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal hapus produk: " + ex.getMessage());
        }
    }   
    
    @SuppressWarnings("unchecked")
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

//class ButtonRenderer extends JButton implements TableCellRenderer {
//    public ButtonRenderer() { setOpaque(true); }
//    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//        setText((value == null) ? "" : value.toString());
//        return this;
//    }
//}

//class ButtonEditor extends DefaultCellEditor {
//    private String label;
//    private boolean isPushed;
//    private ProdukPanel panel;
//    private JTable table;
//    private String action;
//
//    public ButtonEditor(JCheckBox checkBox, ProdukPanel panel, String action) {
//        super(checkBox);
//        this.panel = panel;
//        this.action = action;
//    }
//
//    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//        this.table = table;
//        label = (value == null) ? "" : value.toString();
//        JButton button = new JButton(label);
//        button.addActionListener(e -> performAction(row));
//        isPushed = true;
//        return button;
//    }
//
//    private void performAction(int row) {
//        String kodeProduk = table.getValueAt(row, 0).toString();
//        if ("+".equals(action)) {
//            panel.ubahStok(kodeProduk, 1);
//        } else if ("-".equals(action)) {
//            panel.ubahStok(kodeProduk, -1);
//        } else if ("hapus".equals(action)) {
//            panel.hapusProduk(kodeProduk);
//        }
//        fireEditingStopped();
//    }
//
//    public Object getCellEditorValue() {
//        return label;
//    }
//
//    public boolean stopCellEditing() {
//        isPushed = false;
//        return super.stopCellEditing();
//    }
//
//    protected void fireEditingStopped() {
//        super.fireEditingStopped();
//    }
//}