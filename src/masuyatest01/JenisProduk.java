/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masuyatest01;

/**
 *
 * @author badri
 */
public class JenisProduk {
    private String kodeJenis;
    private String namaJenis;

    public JenisProduk(String kodeJenis, String namaJenis) {
        this.kodeJenis = kodeJenis;
        this.namaJenis = namaJenis;
    }

    public String getKodeJenis() {
        return kodeJenis;
    }

    public void setKodeJenis(String kodeJenis) {
        this.kodeJenis = kodeJenis;
    }

    public String getNamaJenis() {
        return namaJenis;
    }

    public void setNamaJenis(String namaJenis) {
        this.namaJenis = namaJenis;
    }

    @Override
    public String toString() {
        return kodeJenis + " - " + namaJenis;
    }
}
