package view;

import dao.ClaseDAOImpl;


import dao.InscripcionDAOImpl;
import dto.ClienteDetalleDTO;
import model.Clase;
import model.Cliente;
import model.Usuario;
import service.CoachService;

import javax.swing.*;
import java.awt.*;

import java.util.List;

/**
 * Ventana principal para usuarios con rol CLIENTE.
 * Muestra únicamente los datos propios del cliente autenticado
 * y le permite editar su perfil, inscribirse en clases y generar su rutina IA.
 * El cliente nunca ve datos de otros usuarios.
 */
public class ClienteDashboard extends JFrame {

    private final Usuario           usuario;
    private final InscripcionDAOImpl inscripcionDAO;
    private final ClaseDAOImpl       claseDAO;
    private final CoachService       coachService;

    private ClienteDetalleDTO dto;          // datos actuales del cliente

    // ── Etiquetas actualizables ─────────────────────────────────────
    private JLabel lblObjetivo;
    private JLabel lblPeso;
    private JLabel lblAltura;
    private JLabel lblGenero;
    private JLabel lblUltimaClase;

    // ───────────────────────────────────────────────────────────────
    //  Constructor
    // ───────────────────────────────────────────────────────────────

    public ClienteDashboard(Usuario usuario) {
        this.usuario        = usuario;
        this.inscripcionDAO = new InscripcionDAOImpl();
        this.claseDAO       = new ClaseDAOImpl();
        this.coachService   = new CoachService();

        this.dto = inscripcionDAO.obtenerClientePorId(usuario.getId());

        setTitle("Mi Panel — " + usuario.getNombre() + " " + usuario.getApellidos());
        setSize(520, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        construirUI();
    }

    // ───────────────────────────────────────────────────────────────
    //  Construcción de la interfaz
    // ───────────────────────────────────────────────────────────────

    private void construirUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(18, 22, 14, 22));
        setContentPane(root);

        // ── Cabecera de bienvenida ─────────────────────────────────
        root.add(buildHeader(), BorderLayout.NORTH);

        // ── Tarjeta de datos del perfil ────────────────────────────
        root.add(buildPerfilPanel(), BorderLayout.CENTER);

        // ── Botonera inferior ──────────────────────────────────────
        root.add(buildBotonesPanel(), BorderLayout.SOUTH);
    }

    /** Cabecera con nombre y subtítulo. */
    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblBienvenido = new JLabel("👋  Hola, " + usuario.getNombre() + "!");
        lblBienvenido.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBienvenido.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Este es tu panel personal del gimnasio.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblBienvenido);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(lblSub);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JSeparator());
        return panel;
    }

    /** Tarjeta central con los datos actuales del cliente. */
    private JPanel buildPerfilPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)), " Mi Perfil "));

        Font bold  = new Font("Segoe UI", Font.BOLD,  13);
        Font plain = new Font("Segoe UI", Font.PLAIN, 13);

        // Datos fijos
        addDataRow(panel, "DNI:",            usuario.getDni(), bold, plain);
        addDataRow(panel, "Email:",          usuario.getEmail(), bold, plain);

        // Datos del DTO (actualizables)
        String objetivo    = dto != null ? dto.getObjetivo()    : "—";
        String peso        = dto != null ? dto.getPeso() + " kg" : "—";
        String altura      = dto != null ? dto.getAltura() + " cm" : "—";
        String genero      = dto != null ? dto.getGenero()      : "—";
        String ultimaClase = dto != null ? dto.getUltimaClase() : "—";

        lblObjetivo  = crearValor(objetivo,    plain);
        lblPeso      = crearValor(peso,         plain);
        lblAltura    = crearValor(altura,       plain);
        lblGenero    = crearValor(genero,       plain);
        lblUltimaClase = crearValor(ultimaClase, plain);

        addLabelRow(panel, "Objetivo:",       lblObjetivo,  bold);
        addLabelRow(panel, "Peso:",           lblPeso,      bold);
        addLabelRow(panel, "Altura:",         lblAltura,    bold);
        addLabelRow(panel, "Género:",         lblGenero,    bold);
        addLabelRow(panel, "Última clase:",   lblUltimaClase, bold);

        return panel;
    }

    /** Panel de botones de acción. */
    private JPanel buildBotonesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton btnEditar = createBtn("✏️  Editar mis datos",   new Color(0, 122, 255));
        JButton btnInscribir = createBtn("📋  Inscribirme en clase", new Color(40, 167, 69));
        JButton btnIA    = createBtn("🤖  Mi Rutina IA",        new Color(88, 86, 214));
        JButton btnSalir = createBtn("🚪  Cerrar sesión",       new Color(108, 117, 125));

        btnEditar.addActionListener(e -> editarPerfil());
        btnInscribir.addActionListener(e -> inscribirseEnClase());
        btnIA.addActionListener(e -> generarRutina());
        btnSalir.addActionListener(e -> cerrarSesion());

        panel.add(btnEditar);
        panel.add(btnInscribir);
        panel.add(btnIA);
        panel.add(btnSalir);
        return panel;
    }

    // ───────────────────────────────────────────────────────────────
    //  Acciones
    // ───────────────────────────────────────────────────────────────

    /** Abre el diálogo de edición de perfil (reutiliza PerfilDialog). */
    private void editarPerfil() {
        if (dto == null) {
            JOptionPane.showMessageDialog(this,
                "No se encontraron tus datos de cliente en el sistema.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        PerfilDialog dlg = new PerfilDialog(this, dto);
        dlg.setVisible(true);
        if (dlg.isCambiosGuardados()) {
            // Recargar DTO y actualizar etiquetas
            dto = inscripcionDAO.obtenerClientePorId(usuario.getId());
            refrescarEtiquetas();
        }
    }

    /** Muestra un diálogo para seleccionar e inscribirse en una clase. */
    private void inscribirseEnClase() {
        List<Clase> clases = claseDAO.listarTodos();
        if (clases.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay clases disponibles en este momento.",
                "Sin clases", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Clase[] arr = clases.toArray(new Clase[0]);
        Clase elegida = (Clase) JOptionPane.showInputDialog(
            this,
            "Selecciona la clase en la que quieres inscribirte:",
            "Inscribirse en clase",
            JOptionPane.PLAIN_MESSAGE,
            null,
            arr,
            arr[0]
        );

        if (elegida == null) return;   // canceló

        if (dto == null) {
            JOptionPane.showMessageDialog(this,
                "No se encontraron tus datos de cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = inscripcionDAO.inscribirCliente(dto.getId(), elegida.getId());
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "¡Te has inscrito en " + elegida.getNombre() + "!",
                "Inscripción exitosa", JOptionPane.INFORMATION_MESSAGE);
            dto = inscripcionDAO.obtenerClientePorId(usuario.getId());
            refrescarEtiquetas();
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo inscribir. Puede que ya estés apuntado a esta clase.",
                "Error de inscripción", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Genera el prompt de la rutina IA con los datos del cliente. */
    private void generarRutina() {
        if (dto == null) {
            JOptionPane.showMessageDialog(this,
                "No se encontraron tus datos para generar la rutina.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Construir objeto Cliente desde el DTO
        String[] partes = dto.getNombreCompleto().split(" ", 2);
        Cliente cliente = new Cliente();
        cliente.setNombre(partes[0]);
        cliente.setApellidos(partes.length > 1 ? partes[1] : "");
        cliente.setPesoInicial(dto.getPeso());
        cliente.setAltura(dto.getAltura());
        cliente.setGenero(dto.getGenero());
        cliente.setObjetivoFitness(dto.getObjetivo());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());

        String recomendacion = coachService.obtenerRecomendacion(cliente);

        JTextArea textArea = new JTextArea(16, 52);
        textArea.setText(recomendacion);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setCaretPosition(0);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setBackground(new Color(245, 248, 255));
        textArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(560, 280));

        JOptionPane.showMessageDialog(this, scroll,
            "🤖 Tu Rutina IA personalizada",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /** Cierra esta ventana y vuelve al Login. */
    private void cerrarSesion() {
        dispose();
        new Login().setVisible(true);
    }

    // ───────────────────────────────────────────────────────────────
    //  Actualizar etiquetas tras cambios
    // ───────────────────────────────────────────────────────────────

    private void refrescarEtiquetas() {
        if (dto == null) return;
        lblObjetivo.setText(dto.getObjetivo());
        lblPeso.setText(dto.getPeso() + " kg");
        lblAltura.setText(dto.getAltura() + " cm");
        lblGenero.setText(dto.getGenero());
        lblUltimaClase.setText(dto.getUltimaClase());
    }

    // ───────────────────────────────────────────────────────────────
    //  Utilidades de construcción de UI
    // ───────────────────────────────────────────────────────────────

    private JButton createBtn(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel crearValor(String texto, Font f) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(f);
        return lbl;
    }

    private void addDataRow(JPanel panel, String label, String value, Font boldFont, Font plainFont) {
        JLabel l = new JLabel(label); l.setFont(boldFont);
        JLabel v = new JLabel(value != null ? value : "—"); v.setFont(plainFont);
        panel.add(l); panel.add(v);
    }

    private void addLabelRow(JPanel panel, String labelText, JLabel valueLabel, Font boldFont) {
        JLabel l = new JLabel(labelText); l.setFont(boldFont);
        panel.add(l); panel.add(valueLabel);
    }
}
