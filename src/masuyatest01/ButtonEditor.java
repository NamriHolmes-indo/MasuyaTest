/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masuyatest01;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author badri
 */
public class ButtonEditor extends DefaultCellEditor {
    private String label;
    private boolean isPushed;
    private JTable table;
    private String action;
    private Object panel; // Bisa ProdukPanel atau CustomerPanel

    public ButtonEditor(JCheckBox checkBox, Object panel, String action) {
        super(checkBox);
        this.panel = panel;
        this.action = action;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.table = table;
        label = (value == null) ? "" : value.toString();
        JButton button = new JButton(label);
        button.addActionListener(e -> performAction(row));
        isPushed = true;
        return button;
    }

    private void performAction(int row) {
        String kode = table.getValueAt(row, 0).toString();
        try {
            if (panel instanceof ProdukPanel) {
                ProdukPanel produkPanel = (ProdukPanel) panel;
                if ("+".equals(action)) {
                    produkPanel.ubahStok(kode, 1);
                } else if ("-".equals(action)) {
                    produkPanel.ubahStok(kode, -1);
                } else if ("hapus".equals(action)) {
                    produkPanel.hapusProduk(kode);
                }
            } else if (panel instanceof CustomerPanel) {
                CustomerPanel customerPanel = (CustomerPanel) panel;
                if ("hapusCustomer".equals(action)) {
                    customerPanel.hapusCustomer(kode);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal melakukan aksi: " + e.getMessage());
        }
        fireEditingStopped();
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
