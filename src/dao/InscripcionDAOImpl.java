package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.ConexionDB;
import dto.ClienteDetalleDTO;

public class InscripcionDAOImpl {

    public boolean inscribirCliente(int idCliente, int idClase) {
        String sql = "INSERT INTO inscripciones (cliente_id, clase_id, fecha_inscripcion) VALUES (?, ?, CURRENT_DATE)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idClase);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ClienteDetalleDTO> obtenerClientesConDetalles() {
        List<ClienteDetalleDTO> lista = new ArrayList<>();
        String sql = "SELECT u.id, CONCAT(u.nombre, ' ', u.apellidos) AS nombreCompleto, u.dni, c.objetivo_fitness, " +
                     "c.peso_inicial, c.altura, c.genero, c.fecha_nacimiento, " +
                     "(SELECT cl.nombre FROM inscripciones i JOIN clases cl ON i.clase_id = cl.id " +
                     "WHERE i.cliente_id = c.id ORDER BY i.fecha_inscripcion DESC LIMIT 1) AS ultimaClase " +
                     "FROM usuarios u JOIN clientes c ON u.id = c.id";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new ClienteDetalleDTO(
                    rs.getInt("id"),
                    rs.getString("nombreCompleto"),
                    rs.getString("dni"),
                    rs.getString("objetivo_fitness"),
                    rs.getString("ultimaClase") != null ? rs.getString("ultimaClase") : "Ninguna",
                    rs.getDouble("peso_inicial"),
                    rs.getInt("altura"),
                    rs.getString("genero"),
                    rs.getDate("fecha_nacimiento") != null
                        ? rs.getDate("fecha_nacimiento").toLocalDate()
                        : null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Devuelve los datos completos de un único cliente identificado por su userId.
     * Usado por ClienteDashboard para cargar solo la información propia.
     *
     * @param userId ID del usuario autenticado.
     * @return ClienteDetalleDTO del cliente, o null si no existe.
     */
    public ClienteDetalleDTO obtenerClientePorId(int userId) {
        String sql = "SELECT u.id, CONCAT(u.nombre, ' ', u.apellidos) AS nombreCompleto, u.dni, c.objetivo_fitness, " +
                     "c.peso_inicial, c.altura, c.genero, c.fecha_nacimiento, " +
                     "(SELECT cl.nombre FROM inscripciones i JOIN clases cl ON i.clase_id = cl.id " +
                     "WHERE i.cliente_id = c.id ORDER BY i.fecha_inscripcion DESC LIMIT 1) AS ultimaClase " +
                     "FROM usuarios u JOIN clientes c ON u.id = c.id WHERE u.id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ClienteDetalleDTO(
                        rs.getInt("id"),
                        rs.getString("nombreCompleto"),
                        rs.getString("dni"),
                        rs.getString("objetivo_fitness"),
                        rs.getString("ultimaClase") != null ? rs.getString("ultimaClase") : "Ninguna",
                        rs.getDouble("peso_inicial"),
                        rs.getInt("altura"),
                        rs.getString("genero"),
                        rs.getDate("fecha_nacimiento") != null
                            ? rs.getDate("fecha_nacimiento").toLocalDate()
                            : null
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
