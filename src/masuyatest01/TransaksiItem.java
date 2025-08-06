package masuyatest01;

/**
 *
 * @author badri
 */
public class TransaksiItem {
    public String kodeProduk;
    public String namaProduk;
    public int qty, diskon1, diskon2, diskon3, hargaUnit;

    public TransaksiItem(String kodeProduk, String namaProduk, int qty,
                         int diskon1, int diskon2, int diskon3, int hargaUnit) {
        this.kodeProduk = kodeProduk;
        this.namaProduk = namaProduk;
        this.qty = qty;
        this.diskon1 = diskon1;
        this.diskon2 = diskon2;
        this.diskon3 = diskon3;
        this.hargaUnit = hargaUnit;
    }

    public int getHargaTotal() {
        int total = hargaUnit * qty;
        total -= total * diskon1 / 100;
        total -= total * diskon2 / 100;
        total -= total * diskon3 / 100;
        return total;
    }
}
