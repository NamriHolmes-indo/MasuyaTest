/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masuyatest01;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import masuyatest01.DBConnection;
import masuyatest01.JenisProduk;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
/**
 *
 * @author badri
 */
public class JenisProdukPanel extends JPanel {
    private JTextField tfKode, tfNama;
    private DefaultListModel<JenisProduk> listModel;
    private JList<JenisProduk> list;
    private ArrayList<JenisProduk> dataList = new ArrayList<>();
    private boolean kodeDiisiManual = false;
    private JPanel contentPanel;

    public JenisProdukPanel() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        tfKode = new JTextField(20);
        tfNama = new JTextField(20);

        tfNama.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { autoGenerateKode(); }
            public void removeUpdate(DocumentEvent e) { autoGenerateKode(); }
            public void changedUpdate(DocumentEvent e) {}
        });

        tfKode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                kodeDiisiManual = true;
            }
        });

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Kode Jenis:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfKode, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nama Jenis:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfNama, gbc);

        JButton btnTambah = new JButton("Tambah");
        JButton btnUpdate = new JButton("Update");
        JButton btnHapus = new JButton("Hapus");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(list);

        btnTambah.addActionListener(e -> {
            String kode = tfKode.getText().trim();
            String nama = tfNama.getText().trim();
            if (kode.isEmpty() || nama.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
                return;
            }
            for (JenisProduk jp : dataList) {
                if (jp.getKodeJenis().equalsIgnoreCase(kode)) {
                    JOptionPane.showMessageDialog(this, "Kode jenis sudah ada!");
                    return;
                }
            }
            JenisProduk jp = new JenisProduk(kode, nama);
            insertToDatabase(jp);
            loadDataFromDatabase();
            clearFields();
        });


        btnHapus.addActionListener(e -> {
            int index = list.getSelectedIndex();
            if (index >= 0) {
                String kode = dataList.get(index).getKodeJenis();
                deleteFromDatabase(kode);
                loadDataFromDatabase();
                clearFields();
            }
        });

        btnUpdate.addActionListener(e -> {
            int index = list.getSelectedIndex();
            if (index >= 0) {
                String kode = tfKode.getText().trim();
                String nama = tfNama.getText().trim();
                JenisProduk selected = dataList.get(index);
                
                String kodeLama = selected.getKodeJenis();
                String kodeBaru = tfKode.getText().trim();
                String namaBaru = tfNama.getText().trim();

                if (!selected.getKodeJenis().equalsIgnoreCase(kode)) {
                    for (int i = 0; i < dataList.size(); i++) {
                        if (i != index && dataList.get(i).getKodeJenis().equalsIgnoreCase(kode)) {
                            JOptionPane.showMessageDialog(this, "Kode jenis sudah ada!");
                            return;
                        }
                    }
                }
                
                for (int i = 0; i < dataList.size(); i++) {
                    if (i != index && dataList.get(i).getKodeJenis().equalsIgnoreCase(kodeBaru)) {
                        JOptionPane.showMessageDialog(this, "Kode jenis sudah ada!");
                        return;
                    }
                }

                selected.setKodeJenis(kode);
                selected.setNamaJenis(nama);
                JenisProduk jpBaru = new JenisProduk(kodeBaru, namaBaru);
                updateDatabase(jpBaru, kodeLama);
                loadDataFromDatabase();
                clearFields();
            }
        });

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                JenisProduk selected = list.getSelectedValue();
                if (selected != null) {
                    tfKode.setText(selected.getKodeJenis());
                    tfNama.setText(selected.getNamaJenis());
                }
            }
        });

        contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(listScrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int height = getHeight();

                int horizontalPadding = (int) (width * 0.15);
                int verticalPadding = (int) (height * 0.10);

                contentPanel.setBorder(BorderFactory.createEmptyBorder(
                        verticalPadding, horizontalPadding, verticalPadding, horizontalPadding));
                contentPanel.revalidate();
            }
        });
        loadDataFromDatabase();
    }

    private void clearFields() {
        tfKode.setText("");
        tfNama.setText("");
        kodeDiisiManual = false;
        list.clearSelection();
    }

    private String generateKodeJenis(String namaJenis) {
        String[] words = namaJenis.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return sb.toString();
    }

    private void autoGenerateKode() {
        if (!kodeDiisiManual || tfKode.getText().trim().isEmpty()) {
            String nama = tfNama.getText().trim();
            String kode = generateKodeJenis(nama);
            tfKode.setText(kode);
            kodeDiisiManual = false;
        }
    }
    
    
    private void loadDataFromDatabase() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT kodeJenis, namaJenis FROM produk.JenisProduk");

            dataList.clear();
            listModel.clear();

            while (rs.next()) {
                String kode = rs.getString("kodeJenis");
                String nama = rs.getString("namaJenis");
                JenisProduk jp = new JenisProduk(kode, nama);
                dataList.add(jp);
                listModel.addElement(jp);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + ex.getMessage());
        }
    }

    private void insertToDatabase(JenisProduk jp) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO produk.JenisProduk (kodeJenis, namaJenis) VALUES (?, ?)"
            );
            ps.setString(1, jp.getKodeJenis());
            ps.setString(2, jp.getNamaJenis());
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal insert: " + ex.getMessage());
        }
    }

    private void updateDatabase(JenisProduk jpBaru, String kodeLama) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE produk.JenisProduk SET kodeJenis = ?, namaJenis = ? WHERE kodeJenis = ?"
            );
            ps.setString(1, jpBaru.getKodeJenis()); // kode baru
            ps.setString(2, jpBaru.getNamaJenis());
            ps.setString(3, kodeLama);              // kode lama
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal update: " + ex.getMessage());
        }
    }

    private void deleteFromDatabase(String kode) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM produk.JenisProduk WHERE kodeJenis = ?"
            );
            ps.setString(1, kode);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal hapus: " + ex.getMessage());
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
