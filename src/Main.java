import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.Login;

public class Main {
    public static void main(String[] args) {
        // Metal L&F: respeta setBackground/setForeground en botones (el L&F nativo de Windows los ignora)
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            System.err.println("No se pudo configurar el Look & Feel: " + e.getMessage());
        }

        // Iniciar la ventana de Login
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
        });
    }
}
