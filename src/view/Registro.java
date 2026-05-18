package view;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import model.Cliente;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Diálogo modal para registrar nuevos usuarios o clientes del gimnasio.
 * Muestra campos adicionales dinámicamente si el rol seleccionado es CLIENTE.
 */
public class Registro extends JDialog {

    // ── Campos comunes ─────────────────────────────────────────────
    private final JTextField      txtUsername   = new JTextField(20);
    private final JPasswordField  txtPassword   = new JPasswordField(20);
    private final JTextField      txtEmail      = new JTextField(20);
    private final JTextField      txtNombre     = new JTextField(20);
    private final JTextField      txtApellidos  = new JTextField(20);
    private final JTextField      txtDni        = new JTextField(20);
    private final JComboBox<String> cmbRol      = new JComboBox<>(new String[]{"CLIENTE", "ADMIN"});

    // ── Campos exclusivos de Cliente ───────────────────────────────
    private final JTextField      txtPeso       = new JTextField(20);
    private final JTextField      txtAltura     = new JTextField(20);
    private final JTextField      txtFecha      = new JTextField("dd/MM/yyyy", 20);
    private final JComboBox<String> cmbObjetivo = new JComboBox<>(new String[]{
        "Pérdida de peso", "Ganancia muscular",
        "Resistencia cardiovascular", "Flexibilidad", "Mantenimiento general"
    });
    private final JComboBox<String> cmbGenero   = new JComboBox<>(new String[]{
        "Masculino", "Femenino", "Otro"
    });

    // Filas del panel cliente (para mostrar/ocultar)
    private final JLabel lblPeso      = new JLabel("Peso (kg) *:");
    private final JLabel lblAltura    = new JLabel("Altura (cm) *:");
    private final JLabel lblFecha     = new JLabel("Fecha nacimiento *:");
    private final JLabel lblObjetivo  = new JLabel("Objetivo *:");
    private final JLabel lblGenero    = new JLabel("Género:");

    // Panel principal con GridBagLayout
    private final JPanel panelForm = new JPanel(new GridBagLayout());

    // Flag de resultado
    private boolean registroExitoso = false;

    private final UsuarioDAO usuarioDAO;

    // ─────────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────────
    public Registro(Frame parent) {
        super(parent, "Registrar Nuevo Usuario", true);
        usuarioDAO = new UsuarioDAOImpl();

        construirFormulario();
        configurarRolListener();

        // Añadir panel con scroll al content pane
        JScrollPane scroll = new JScrollPane(panelForm,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        getContentPane().add(scroll, BorderLayout.CENTER);

        // Botones
        getContentPane().add(crearPanelBotones(), BorderLayout.SOUTH);

        // Ajustar tamaño, centrar y mostrar
        setMinimumSize(new Dimension(450, 200));
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // ─────────────────────────────────────────────────────────────
    //  Construcción del formulario con GridBagLayout
    // ─────────────────────────────────────────────────────────────

    private void construirFormulario() {
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets   = new Insets(5, 5, 5, 5);
        g.anchor   = GridBagConstraints.WEST;
        g.fill     = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ── Título de sección ──────────────────────────────────────
        row = addSectionTitle(panelForm, g, row, "— Datos de la cuenta —");

        // ── Campos comunes ─────────────────────────────────────────
        row = addRow(panelForm, g, row, new JLabel("Usuario *:"),    txtUsername);
        row = addRow(panelForm, g, row, new JLabel("Contraseña *:"), txtPassword);
        row = addRow(panelForm, g, row, new JLabel("Email *:"),      txtEmail);
        row = addRow(panelForm, g, row, new JLabel("Nombre *:"),     txtNombre);
        row = addRow(panelForm, g, row, new JLabel("Apellidos *:"),  txtApellidos);
        row = addRow(panelForm, g, row, new JLabel("DNI *:"),        txtDni);
        row = addRow(panelForm, g, row, new JLabel("Rol *:"),        cmbRol);

        // ── Separador ─────────────────────────────────────────────
        g.gridx = 0; g.gridy = row; g.gridwidth = 2; g.insets = new Insets(8, 5, 8, 5);
        panelForm.add(new JSeparator(), g);
        g.gridwidth = 1; g.insets = new Insets(5, 5, 5, 5);
        row++;

        // ── Título sección cliente ─────────────────────────────────
        row = addSectionTitle(panelForm, g, row, "— Datos físicos del cliente —");

        // ── Campos de cliente (se muestran/ocultan) ───────────────
        row = addRow(panelForm, g, row, lblObjetivo, cmbObjetivo);
        row = addRow(panelForm, g, row, lblPeso,     txtPeso);
        row = addRow(panelForm, g, row, lblAltura,   txtAltura);
        row = addRow(panelForm, g, row, lblFecha,    txtFecha);
        row = addRow(panelForm, g, row, lblGenero,   cmbGenero);
    }

    /** Añade una fila etiqueta + campo y devuelve el siguiente índice de fila. */
    private int addRow(JPanel panel, GridBagConstraints g, int row, JLabel label, JComponent field) {
        Font f = new Font("Segoe UI", Font.PLAIN, 13);
        label.setFont(f);
        field.setFont(f);

        g.gridx = 0; g.gridy = row; g.weightx = 0.35;
        panel.add(label, g);

        g.gridx = 1; g.weightx = 0.65;
        panel.add(field, g);

        return row + 1;
    }

    /** Añade una fila de título de sección (texto centrado, negrita). */
    private int addSectionTitle(JPanel panel, GridBagConstraints g, int row, String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(70, 70, 70));

        g.gridx = 0; g.gridy = row; g.gridwidth = 2; g.weightx = 1.0;
        panel.add(lbl, g);
        g.gridwidth = 1;
        return row + 1;
    }

    // ─────────────────────────────────────────────────────────────
    //  Mostrar / ocultar campos de cliente según el rol
    // ─────────────────────────────────────────────────────────────

    private void configurarRolListener() {
        // Estado inicial
        actualizarCamposCliente();

        cmbRol.addActionListener(e -> actualizarCamposCliente());
    }

    private void actualizarCamposCliente() {
        boolean esCliente = "CLIENTE".equals(cmbRol.getSelectedItem());

        // Mostrar/ocultar etiquetas y campos del bloque cliente
        Component[] componentesCliente = {
            lblObjetivo, cmbObjetivo,
            lblPeso,     txtPeso,
            lblAltura,   txtAltura,
            lblFecha,    txtFecha,
            lblGenero,   cmbGenero
        };
        for (Component c : componentesCliente) {
            c.setVisible(esCliente);
        }

        // Reajustar tamaño del diálogo
        pack();
        setLocationRelativeTo(getOwner());
    }

    // ─────────────────────────────────────────────────────────────
    //  Panel de botones
    // ─────────────────────────────────────────────────────────────

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        JButton btnGuardar = new JButton("  Guardar  ");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(0, 122, 255));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> accionGuardar());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnGuardar);
        panel.add(btnCancelar);
        return panel;
    }

    // ─────────────────────────────────────────────────────────────
    //  Lógica de guardado y validación
    // ─────────────────────────────────────────────────────────────

    private void accionGuardar() {
        if (!validarComunes()) return;

        boolean exito;
        String rol = (String) cmbRol.getSelectedItem();

        if ("CLIENTE".equals(rol)) {
            if (!validarCliente()) return;
            Cliente c = construirCliente();
            if (c == null) return;
            exito = usuarioDAO.registrar(c);
        } else {
            Usuario u = new Usuario();
            u.setUsername(txtUsername.getText().trim());
            u.setPassword(new String(txtPassword.getPassword()));
            u.setEmail(txtEmail.getText().trim());
            u.setNombre(txtNombre.getText().trim());
            u.setApellidos(txtApellidos.getText().trim());
            u.setDni(txtDni.getText().trim());
            u.setRol(rol);
            exito = usuarioDAO.insertar(u);
        }

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "Usuario registrado correctamente.",
                    "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            registroExitoso = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar. Comprueba que el usuario o DNI no estén ya en uso.",
                    "Error al Guardar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarComunes() {
        if (txtUsername.getText().trim().isEmpty())
            return error("El campo 'Usuario' es obligatorio.");
        if (new String(txtPassword.getPassword()).isEmpty())
            return error("El campo 'Contraseña' es obligatorio.");
        if (!txtEmail.getText().contains("@"))
            return error("Introduce un email válido.");
        if (txtNombre.getText().trim().isEmpty())
            return error("El campo 'Nombre' es obligatorio.");
        if (txtApellidos.getText().trim().isEmpty())
            return error("El campo 'Apellidos' es obligatorio.");
        if (txtDni.getText().trim().isEmpty())
            return error("El campo 'DNI' es obligatorio.");
        return true;
    }

    private boolean validarCliente() {
        if (txtPeso.getText().trim().isEmpty())
            return error("El campo 'Peso' es obligatorio.");
        if (txtAltura.getText().trim().isEmpty())
            return error("El campo 'Altura' es obligatorio.");
        if (txtFecha.getText().trim().isEmpty() || txtFecha.getText().equals("dd/MM/yyyy"))
            return error("La fecha de nacimiento es obligatoria (dd/MM/yyyy).");
        return true;
    }

    private Cliente construirCliente() {
        try {
            double     peso    = Double.parseDouble(txtPeso.getText().trim().replace(',', '.'));
            int        altura  = Integer.parseInt(txtAltura.getText().trim());
            LocalDate  fecha   = LocalDate.parse(txtFecha.getText().trim(),
                                     DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Cliente c = new Cliente();
            c.setUsername(txtUsername.getText().trim());
            c.setPassword(new String(txtPassword.getPassword()));
            c.setEmail(txtEmail.getText().trim());
            c.setNombre(txtNombre.getText().trim());
            c.setApellidos(txtApellidos.getText().trim());
            c.setDni(txtDni.getText().trim());
            c.setRol("CLIENTE");
            c.setObjetivoFitness((String) cmbObjetivo.getSelectedItem());
            c.setPesoInicial(peso);
            c.setAltura(altura);
            c.setFechaNacimiento(fecha);
            c.setGenero((String) cmbGenero.getSelectedItem());
            return c;
        } catch (NumberFormatException ex) {
            error("Peso y Altura deben ser números válidos.");
        } catch (DateTimeParseException ex) {
            error("La fecha debe tener el formato dd/MM/yyyy  (ej: 15/03/1995).");
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────
    //  Utilidades
    // ─────────────────────────────────────────────────────────────

    /** Muestra un aviso de error y devuelve false para cortar la validación. */
    private boolean error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Datos incorrectos", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    /** @return true si el registro fue completado con éxito. */
    public boolean isRegistroExitoso() {
        return registroExitoso;
    }
}
