package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.ConexionDB;
import model.Clase;

public class ClaseDAOImpl {
    
    public boolean insertar(Clase clase) {
        String sql = "INSERT INTO clases (nombre, descripcion, aforo_max) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clase.getNombre());
            ps.setString(2, clase.getDescripcion());
            ps.setInt(3, clase.getAforoMax());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Clase> listarTodos() {
        List<Clase> lista = new ArrayList<>();
        String sql = "SELECT * FROM clases";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Clase(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion"), rs.getInt("aforo_max")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
