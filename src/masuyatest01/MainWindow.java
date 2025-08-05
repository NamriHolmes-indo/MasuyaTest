/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package masuyatest01;
import javax.swing.*;
/**
 *
 * @author badri
 */
public class MainWindow extends JFrame {

    /** Creates new form MainWindow */
    public MainWindow() {
        setTitle("Aplikasi Manajemen");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Jenis Produk", new JenisProdukPanel());

        JPanel panelProduk = new JPanel();
        panelProduk.add(new JLabel("Form Tambah Produk"));

        JPanel panelCustomer = new JPanel();
        panelCustomer.add(new JLabel("Form Tambah Customer"));

        JPanel panelTransaksi = new JPanel();
        panelTransaksi.add(new JLabel("Form Tambah Transaksi"));

        tabbedPane.addTab("Tambah Produk", new ProdukPanel());
        tabbedPane.addTab("Tambah Customer", panelCustomer);
        tabbedPane.addTab("Tambah Transaksi", panelTransaksi);

        add(tabbedPane);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
