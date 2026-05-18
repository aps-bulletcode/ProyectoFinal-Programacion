package view;

import dao.ClienteDAOImpl;
import dao.ClaseDAOImpl;
import dao.InscripcionDAOImpl;
import dto.ClienteDetalleDTO;
import model.Clase;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Diálogo modal que muestra el perfil completo de un cliente
 * y permite editarlo e inscribirlo en clases disponibles.
 */
public class PerfilDialog extends JDialog {

    private final ClienteDetalleDTO cliente;
    private final ClienteDAOImpl    clienteDAO;
    private final ClaseDAOImpl      claseDAO;
    private final InscripcionDAOImpl inscripcionDAO;

    /** true si se guardaron cambios o se realizó una inscripción. */
    private boolean cambiosGuardados = false;

    // ── Campos editables ────────────────────────────────────────────
    private final JComboBox<String> cmbObjetivo = new JComboBox<>(new String[]{
        "Pérdida de peso", "Ganancia muscular",
        "Resistencia cardiovascular", "Flexibilidad", "Mantenimiento general"
    });
    private final JTextField        txtPeso   = new JTextField(12);
    private final JTextField        txtAltura = new JTextField(12);
    private final JComboBox<String> cmbGenero = new JComboBox<>(new String[]{
        "Masculino", "Femenino", "Otro"
    });

    // ── Combo de clases ─────────────────────────────────────────────
    private JComboBox<Clase> cmbClases;

    // ───────────────────────────────────────────────────────────────
    //  Constructor
    // ───────────────────────────────────────────────────────────────

    public PerfilDialog(Frame parent, ClienteDetalleDTO cliente) {
        super(parent, "Perfil — " + cliente.getNombreCompleto(), true);
        this.cliente        = cliente;
        this.clienteDAO     = new ClienteDAOImpl();
        this.claseDAO       = new ClaseDAOImpl();
        this.inscripcionDAO = new InscripcionDAOImpl();

        // Pre-rellenar campos con datos actuales
        cmbObjetivo.setSelectedItem(cliente.getObjetivo());
        txtPeso.setText(String.format("%.1f", cliente.getPeso()).replace(',', '.'));
        txtAltura.setText(String.valueOf(cliente.getAltura()));
        cmbGenero.setSelectedItem(cliente.getGenero());

        construirUI();
        pack();
        setMinimumSize(new Dimension(440, 320));
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // ───────────────────────────────────────────────────────────────
    //  Construcción de la UI
    // ───────────────────────────────────────────────────────────────

    private void construirUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 18, 10, 18));
        setContentPane(root);

        // ── Cabecera con datos fijos ───────────────────────────────
        root.add(buildHeader(), BorderLayout.NORTH);

        // ── Pestañas ───────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("✏️  Editar Perfil",      buildEditPanel());
        tabs.addTab("📋  Inscribir en Clase",  buildInscribirPanel());
        root.add(tabs, BorderLayout.CENTER);

        // ── Botones inferiores ─────────────────────────────────────
        root.add(buildBotonesPanel(), BorderLayout.SOUTH);
    }

    /** Panel superior con datos de solo lectura. */
    private JPanel buildHeader() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 4));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)), " Datos del cliente "));

        addInfoRow(panel, "Nombre completo:",  cliente.getNombreCompleto());
        addInfoRow(panel, "DNI:",              cliente.getDni());
        addInfoRow(panel, "Última clase:",     cliente.getUltimaClase());

        return panel;
    }

    /** Pestaña 1: editar objetivo, peso, altura, género. */
    private JPanel buildEditPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(6, 6, 6, 6);
        g.anchor  = GridBagConstraints.WEST;
        g.fill    = GridBagConstraints.HORIZONTAL;

        Font f = new Font("Segoe UI", Font.PLAIN, 13);

        String[]     labels = { "Objetivo fitness:", "Peso (kg):", "Altura (cm):", "Género:" };
        JComponent[] fields = { cmbObjetivo, txtPeso, txtAltura, cmbGenero };

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(f);
            fields[i].setFont(f);
            g.gridx = 0; g.gridy = i; g.weightx = 0.38;
            panel.add(lbl, g);
            g.gridx = 1; g.weightx = 0.62;
            panel.add(fields[i], g);
        }
        return panel;
    }

    /** Pestaña 2: seleccionar clase e inscribirse. */
    private JPanel buildInscribirPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(18, 15, 18, 15));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        // Cargar clases desde la BD
        List<Clase> clases = claseDAO.listarTodos();
        cmbClases = new JComboBox<>();
        for (Clase c : clases) cmbClases.addItem(c);
        cmbClases.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblSel = new JLabel("Clase disponible:");
        lblSel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnInscribir = new JButton("✅  Inscribir en esta clase");
        btnInscribir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnInscribir.setBackground(new Color(0, 122, 255));
        btnInscribir.setForeground(Color.WHITE);
        btnInscribir.setFocusPainted(false);
        btnInscribir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnInscribir.addActionListener(e -> inscribirEnClase());

        g.gridx = 0; g.gridy = 0; panel.add(lblSel,      g);
        g.gridy = 1;               panel.add(cmbClases,   g);
        g.gridy = 2;               panel.add(btnInscribir, g);

        if (clases.isEmpty()) {
            JLabel aviso = new JLabel("No hay clases disponibles en el sistema.");
            aviso.setForeground(Color.GRAY);
            aviso.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            g.gridy = 3; panel.add(aviso, g);
            btnInscribir.setEnabled(false);
        }

        return panel;
    }

    /** Panel de botones Guardar / Cerrar. */
    private JPanel buildBotonesPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        JButton btnGuardar = new JButton("💾  Guardar cambios");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(40, 167, 69));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarCambios());

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dispose());

        panel.add(btnGuardar);
        panel.add(btnCerrar);
        return panel;
    }

    // ───────────────────────────────────────────────────────────────
    //  Lógica de negocio
    // ───────────────────────────────────────────────────────────────

    private void guardarCambios() {
        try {
            double peso   = Double.parseDouble(txtPeso.getText().trim().replace(',', '.'));
            int    altura = Integer.parseInt(txtAltura.getText().trim());
            String obj    = (String) cmbObjetivo.getSelectedItem();
            String gen    = (String) cmbGenero.getSelectedItem();

            if (peso <= 0 || altura <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Peso y Altura deben ser valores positivos.",
                    "Datos incorrectos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = clienteDAO.actualizarDatosFisicos(cliente.getId(), obj, peso, altura, gen);
            if (ok) {
                cambiosGuardados = true;
                JOptionPane.showMessageDialog(this,
                    "Perfil actualizado correctamente.", "Guardado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo guardar. Inténtalo de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Peso y Altura deben ser números válidos (ej: 75.5 y 178).",
                "Datos incorrectos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void inscribirEnClase() {
        if (cmbClases == null || cmbClases.getItemCount() == 0) return;

        Clase clase = (Clase) cmbClases.getSelectedItem();
        boolean ok  = inscripcionDAO.inscribirCliente(cliente.getId(), clase.getId());

        if (ok) {
            cambiosGuardados = true;
            JOptionPane.showMessageDialog(this,
                "¡" + cliente.getNombreCompleto() + " inscrito en " + clase.getNombre() + "!",
                "Inscripción exitosa", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo inscribir. Es posible que ya esté apuntado a esta clase.",
                "Error de inscripción", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ───────────────────────────────────────────────────────────────
    //  Utilidades
    // ───────────────────────────────────────────────────────────────

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel val = new JLabel(value != null ? value : "—");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lbl);
        panel.add(val);
    }

    /** @return true si se guardaron cambios o se realizó alguna inscripción. */
    public boolean isCambiosGuardados() { return cambiosGuardados; }
}
