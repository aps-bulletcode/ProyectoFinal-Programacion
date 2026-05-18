package view;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Import explícito del Dashboard para evitar error de compilación
// (ambas clases están en el mismo paquete view, pero dejamos el import por claridad)

public class Login extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnEntrar;
    private JButton btnRegistrarse;

    private UsuarioDAO usuarioDAO;

    public Login() {
        usuarioDAO = new UsuarioDAOImpl();

        setTitle("Gym Management System - Login");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Inicio de Sesión");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(lblUsuario, gbc);

        txtUsuario = new JTextField(15);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(txtUsuario, gbc);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(txtPassword, gbc);

        btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrar.setBackground(new Color(0, 122, 255));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.ipady = 5;
        panel.add(btnEntrar, gbc);

        btnRegistrarse = new JButton("¿No tienes cuenta? Registrarse");
        btnRegistrarse.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRegistrarse.setContentAreaFilled(false);
        btnRegistrarse.setBorderPainted(false);
        btnRegistrarse.setForeground(new Color(0, 102, 204));
        btnRegistrarse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.ipady = 0;
        panel.add(btnRegistrarse, gbc);

        add(panel);
        configurarEventos();
    }

    private void configurarEventos() {
        btnEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 1. Validar la captura de datos
                    String user = txtUsuario.getText().trim();
                    String pass = new String(txtPassword.getPassword());

                    if (user.isEmpty() || pass.isEmpty()) {
                        JOptionPane.showMessageDialog(Login.this, 
                                "Complete todos los campos.", 
                                "Aviso", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // 2. Llamada al DAO para autenticación
                    Usuario logueado = usuarioDAO.validar(user, pass);

                    // 3. Feedback visual
                    if (logueado != null) {
                        JOptionPane.showMessageDialog(Login.this, 
                                "¡Bienvenido, " + logueado.getNombre() + "!", 
                                "Acceso Correcto", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Enrutar según rol
                        dispose();
                        if ("ADMIN".equals(logueado.getRol())) {
                            new Principal().setVisible(true);
                        } else {
                            new ClienteDashboard(logueado).setVisible(true);
                        }
                    } else {
                        JOptionPane.showMessageDialog(Login.this, 
                                "Credenciales incorrectas.", 
                                "Error de Acceso", JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception ex) {
                    // 4. Depuración visual en caso de que el driver falle, BD esté caída o null pointer
                    JOptionPane.showMessageDialog(Login.this, 
                            "Error: " + ex.getMessage(), 
                            "Error Crítico", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        btnRegistrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abre el diálogo de registro real (modal)
                Registro dialogo = new Registro(Login.this);
                dialogo.setVisible(true);
                // Si el registro fue exitoso, informamos al usuario para que inicie sesión
                if (dialogo.isRegistroExitoso()) {
                    JOptionPane.showMessageDialog(Login.this,
                            "Registro completado. Ya puedes iniciar sesión.",
                            "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }
}
