public class TestDriver {
    public static void main(String[] args) {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("✅ Driver ditemukan dan berhasil dimuat!");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver TIDAK ditemukan!");
        }
    }
}
