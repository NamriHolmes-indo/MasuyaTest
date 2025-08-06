/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package masuyatest01;
import java.awt.BorderLayout;
import javax.swing.*;
/**
 *
 * @author badri
 */
public class MainWindow extends JFrame {
    private JPanel wrapperPanel;
    private ProdukPanel produkPanel;
    private CustomerPanel customerPanel;

    public MainWindow() {
        setTitle("Aplikasi Manajemen");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Jenis Produk", new JenisProdukPanel());

        produkPanel = new ProdukPanel();
        wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(produkPanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.addTab("Tambah Produk", wrapperPanel);
                
        customerPanel = new CustomerPanel();
        wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(customerPanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.addTab("Tambah Customer", wrapperPanel);

        JPanel panelCustomer = new JPanel();
        panelCustomer.add(new JLabel("Form Tambah Customer"));

        JPanel panelTransaksi = new JPanel();
        panelTransaksi.add(new JLabel("Form Tambah Transaksi"));

        tabbedPane.addTab("Tambah Transaksi", panelTransaksi);

        add(tabbedPane);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updatePadding();
            }
        });

        updatePadding(); // Set padding awal sesuai status jendela
    }

    private void updatePadding() {
        int state = getExtendedState();
        if ((state & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
            int top = (int) (getHeight() * 0.10);
            int left = (int) (getWidth() * 0.05);
            int bottom = top;
            int right = left;
            wrapperPanel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        } else {
            wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        }
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }
    
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
