package dao;

import java.util.List;
import model.Usuario;

public interface UsuarioDAO {
    
    /**
     * Valida las credenciales de un usuario.
     * @return El objeto Usuario si las credenciales son correctas, null en caso contrario.
     */
    Usuario validar(String username, String password);

    /**
     * Inserta un nuevo usuario en la base de datos.
     * @return true si se insertó correctamente, false en caso contrario.
     */
    boolean insertar(Usuario u);

    /**
     * Devuelve una lista con todos los usuarios de la base de datos.
     * @return List<Usuario> con los registros encontrados.
     */
    List<Usuario> listarTodos();

    /**
     * Actualiza los datos de un usuario existente.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    boolean actualizar(Usuario u);

    /**
     * Elimina un usuario por su ID.
     * @return true si se eliminó correctamente, false en caso contrario.
     */
    boolean eliminar(int id);

    /**
     * Registra un nuevo Cliente realizando inserción en usuarios y clientes mediante una transacción manual.
     * @return true si el registro de ambas tablas fue exitoso.
     */
    boolean registrar(model.Cliente c);
}
