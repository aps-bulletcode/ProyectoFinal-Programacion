package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.ConexionDB;
import model.Usuario;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario validar(String username, String password) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellidos(rs.getString("apellidos"));
                    usuario.setDni(rs.getString("dni"));
                    usuario.setRol(rs.getString("rol"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return usuario;
    }

    @Override
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
        boolean insertado = false;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getNombre());
            ps.setString(5, u.getApellidos());
            ps.setString(6, u.getDni());
            ps.setString(7, u.getRol());

            if (ps.executeUpdate() > 0) {
                insertado = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return insertado;
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setEmail(rs.getString("email"));
                u.setNombre(rs.getString("nombre"));
                u.setApellidos(rs.getString("apellidos"));
                u.setDni(rs.getString("dni"));
                u.setRol(rs.getString("rol"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET username = ?, password = ?, email = ?, nombre = ?, apellidos = ?, dni = ?, rol = ? WHERE id = ?";
        boolean actualizado = false;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getNombre());
            ps.setString(5, u.getApellidos());
            ps.setString(6, u.getDni());
            ps.setString(7, u.getRol());
            ps.setInt(8, u.getId());

            if (ps.executeUpdate() > 0) {
                actualizado = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return actualizado;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        boolean eliminado = false;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            if (ps.executeUpdate() > 0) {
                eliminado = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return eliminado;
    }

    @Override
    public boolean registrar(model.Cliente c) {
        String sqlUsuario = "INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlCliente = "INSERT INTO clientes (id, objetivo_fitness, peso_inicial, altura, fecha_nacimiento, genero) VALUES (?, ?, ?, ?, ?, ?)";
        boolean exito = false;

        try (Connection conn = ConexionDB.getConnection()) {
            if (conn == null) return false;   // getConnection() ya mostró el error

            conn.setAutoCommit(false);

            try (PreparedStatement psUser = conn.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, c.getUsername());
                psUser.setString(2, c.getPassword());
                psUser.setString(3, c.getEmail());
                psUser.setString(4, c.getNombre());
                psUser.setString(5, c.getApellidos());
                psUser.setString(6, c.getDni());
                psUser.setString(7, c.getRol() != null ? c.getRol() : "CLIENTE");

                if (psUser.executeUpdate() > 0) {
                    try (ResultSet rs = psUser.getGeneratedKeys()) {
                        if (rs.next()) {
                            int idGenerado = rs.getInt(1);
                            c.setId(idGenerado);

                            try (PreparedStatement psCliente = conn.prepareStatement(sqlCliente)) {
                                psCliente.setInt(1, idGenerado);
                                psCliente.setString(2, c.getObjetivoFitness());
                                psCliente.setDouble(3, c.getPesoInicial());
                                psCliente.setInt(4, c.getAltura());

                                if (c.getFechaNacimiento() != null) {
                                    psCliente.setDate(5, java.sql.Date.valueOf(c.getFechaNacimiento()));
                                } else {
                                    psCliente.setNull(5, java.sql.Types.DATE);
                                }
                                psCliente.setString(6, c.getGenero());

                                if (psCliente.executeUpdate() > 0) {
                                    conn.commit();
                                    exito = true;
                                } else {
                                    conn.rollback();
                                }
                            }
                        } else {
                            conn.rollback();
                        }
                    }
                } else {
                    conn.rollback();
                }

            } catch (SQLException ex) {
                conn.rollback();
                // Mostrar el error SQL real en pantalla para facilitar el diagnóstico
                String detalle = ex.getMessage();
                System.err.println("Error en transacción registrar: " + detalle);
                javax.swing.JOptionPane.showMessageDialog(null,
                    "Error SQL al registrar el cliente:\n\n" + detalle,
                    "Error en Base de Datos", javax.swing.JOptionPane.ERROR_MESSAGE);
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Error general en registrar cliente: " + e.getMessage());
            e.printStackTrace();
        }

        return exito;
    }
}