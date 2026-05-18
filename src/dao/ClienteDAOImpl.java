package dao;

import db.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DAO de acceso a la tabla clientes.
 * Centraliza las operaciones de actualización de datos físicos del cliente.
 */
public class ClienteDAOImpl {

    /**
     * Actualiza los datos físicos y el objetivo de entrenamiento de un cliente.
     *
     * @param id              ID del cliente (FK que coincide con usuarios.id).
     * @param objetivoFitness Nuevo objetivo de entrenamiento.
     * @param peso            Nuevo peso en kg.
     * @param altura          Nueva altura en cm.
     * @param genero          Género actualizado.
     * @return true si la fila fue actualizada correctamente.
     */
    public boolean actualizarDatosFisicos(int id, String objetivoFitness,
                                          double peso, int altura, String genero) {
        String sql = "UPDATE clientes SET objetivo_fitness = ?, peso_inicial = ?, " +
                     "altura = ?, genero = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, objetivoFitness);
            ps.setDouble(2, peso);
            ps.setInt(3, altura);
            ps.setString(4, genero);
            ps.setInt(5, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar datos físicos del cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
