package masuyatest01;
import javax.swing.SwingUtilities;
import masuyatest01.MainWindow;
/**
 *
 * @author badri
 */
public class MasuyaTest01 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
