package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionDB {

    // Datos de conexión
    private static final String URL = "jdbc:mysql://localhost:3306/gimnasio_db";
    private static final String USER = "root";
    private static final String PASS = "BaseDeDatos";

    // Constructor privado: nadie instancia esta clase
    private ConexionDB() {}

    // Bloque estático: carga el driver una única vez al arrancar la aplicación
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error fatal: No se encontró el driver de MySQL.");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error Crítico: No se encontró el driver JDBC de MySQL.\n"
                + "Asegúrate de que el .jar esté en la carpeta lib/.\n\n"
                + "Mensaje técnico: " + e.getMessage(),
                "Falta Driver JDBC", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Devuelve una conexión NUEVA cada vez que se llama.
     * Los DAOs la cierran con try-with-resources, lo cual es correcto.
     * NO usar Singleton aquí porque try-with-resources cierra la conexión al salir del bloque.
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Fallo al conectar a la base de datos 'gimnasio_db'.\n\n"
                + "Detalle: " + e.getMessage() + "\n\n"
                + "Posibles causas:\n"
                + "1. MySQL Server no está iniciado.\n"
                + "2. La base de datos 'gimnasio_db' no existe.\n"
                + "3. El usuario o contraseña son incorrectos.",
                "Error de Conexión MySQL", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
