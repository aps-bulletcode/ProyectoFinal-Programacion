package view;

import dao.InscripcionDAOImpl;
import dao.UsuarioDAOImpl;
import dto.ClienteDetalleDTO;
import model.Cliente;
import service.CoachService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Principal extends JFrame {

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private InscripcionDAOImpl inscripcionDAO;
    private UsuarioDAOImpl     usuarioDAO;
    private CoachService coachService;
    private List<ClienteDetalleDTO> clientesData;

    public Principal() {
        inscripcionDAO = new InscripcionDAOImpl();
        usuarioDAO     = new UsuarioDAOImpl();
        coachService   = new CoachService();

        setTitle("Dashboard - Sistema de Gestión de Gimnasio");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ── Menú superior ──────────────────────────────────────────
        JMenuBar menuBar   = new JMenuBar();
        JMenu menuArchivo  = new JMenu("Archivo");

        JMenuItem itemNuevo = new JMenuItem("Nuevo Cliente...");
        itemNuevo.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        itemNuevo.addActionListener(e -> abrirRegistro());

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));

        menuArchivo.add(itemNuevo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);

        // ── Panel Lateral ──────────────────────────────────────────
        JPanel panelLateral = new JPanel();
        panelLateral.setLayout(new BoxLayout(panelLateral, BoxLayout.Y_AXIS));
        panelLateral.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelLateral.setPreferredSize(new Dimension(170, 0));

        // Botón Nuevo
        JButton btnNuevo = new JButton("＋ Nuevo");
        btnNuevo.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNuevo.setMaximumSize(new Dimension(150, 40));
        btnNuevo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNuevo.setBackground(new Color(40, 167, 69));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevo.addActionListener(e -> abrirRegistro());

        // Botón Refrescar
        JButton btnRefrescar = new JButton("↺ Refrescar");
        btnRefrescar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRefrescar.setMaximumSize(new Dimension(150, 40));
        btnRefrescar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefrescar.addActionListener(e -> cargarDatos());

        // Botón Generar Rutina IA
        JButton btnGenerarIA = new JButton("🤖 Generar Rutina IA");
        btnGenerarIA.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGenerarIA.setMaximumSize(new Dimension(150, 55));
        btnGenerarIA.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGenerarIA.setBackground(new Color(0, 122, 255));
        btnGenerarIA.setForeground(Color.WHITE);
        btnGenerarIA.setFocusPainted(false);
        btnGenerarIA.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGenerarIA.setToolTipText("Selecciona un cliente en la tabla y pulsa aquí.");
        btnGenerarIA.addActionListener(e -> generarRutina());

        panelLateral.add(btnNuevo);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 10)));
        panelLateral.add(btnRefrescar);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 20)));
        panelLateral.add(new JSeparator(JSeparator.HORIZONTAL));
        panelLateral.add(Box.createRigidArea(new Dimension(0, 12)));

        // Botón Ver / Editar perfil
        JButton btnPerfil = new JButton("✏️  Ver / Editar");
        btnPerfil.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPerfil.setMaximumSize(new Dimension(150, 40));
        btnPerfil.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnPerfil.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPerfil.addActionListener(e -> verEditarPerfil());

        // Botón Eliminar cliente
        JButton btnEliminar = new JButton("🗑️  Eliminar");
        btnEliminar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEliminar.setMaximumSize(new Dimension(150, 40));
        btnEliminar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnEliminar.setForeground(new Color(180, 30, 30));
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(e -> eliminarCliente());

        panelLateral.add(btnPerfil);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 8)));
        panelLateral.add(btnEliminar);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 20)));
        panelLateral.add(new JSeparator(JSeparator.HORIZONTAL));
        panelLateral.add(Box.createRigidArea(new Dimension(0, 12)));
        panelLateral.add(btnGenerarIA);

        add(panelLateral, BorderLayout.WEST);

        // ── Tabla Central ──────────────────────────────────────────
        String[] columnas = {"ID", "Nombre Completo", "DNI", "Objetivo", "Última Clase"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.setRowHeight(26);
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaClientes.setGridColor(new Color(220, 220, 220));
        tablaClientes.setShowGrid(true);

        // Resaltar fila seleccionada en azul claro
        tablaClientes.setSelectionBackground(new Color(173, 216, 230));
        tablaClientes.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // ── Panel inferior de estado ───────────────────────────────
        JLabel lblEstado = new JLabel("  Listo — selecciona un cliente para generar su rutina IA.");
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblEstado.setForeground(Color.GRAY);
        lblEstado.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 0));
        add(lblEstado, BorderLayout.SOUTH);

        // Cargar datos al iniciar
        cargarDatos();
    }

    // ─────────────────────────────────────────────────────────────
    //  Abrir ventana de Registro y refrescar tabla tras éxito
    // ─────────────────────────────────────────────────────────────

    private void abrirRegistro() {
        Registro dialogo = new Registro(this);
        dialogo.setVisible(true);          // bloqueante por ser modal

        // Cuando el diálogo se cierra, revisamos si hubo registro exitoso
        if (dialogo.isRegistroExitoso()) {
            cargarDatos();                 // ← refrescar la tabla automáticamente
            JOptionPane.showMessageDialog(this,
                    "La tabla ha sido actualizada con el nuevo registro.",
                    "Datos Actualizados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Carga de datos en la tabla
    // ─────────────────────────────────────────────────────────────

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        clientesData = inscripcionDAO.obtenerClientesConDetalles();
        for (ClienteDetalleDTO c : clientesData) {
            modeloTabla.addRow(new Object[]{
                c.getId(),
                c.getNombreCompleto(),
                c.getDni(),
                c.getObjetivo(),
                c.getUltimaClase()
            });
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Generar Rutina IA desde fila seleccionada
    // ─────────────────────────────────────────────────────────────

    private void generarRutina() {
        int filaSeleccionada = tablaClientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un cliente de la tabla primero.",
                    "Sin Selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCliente = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        ClienteDetalleDTO clienteSeleccionado = null;
        for (ClienteDetalleDTO c : clientesData) {
            if (c.getId() == idCliente) {
                clienteSeleccionado = c;
                break;
            }
        }

        if (clienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron los datos completos del cliente.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Construir objeto Cliente temporal con los datos del DTO
        String[] partes = clienteSeleccionado.getNombreCompleto().split(" ", 2);
        Cliente cliente = new Cliente();
        cliente.setNombre(partes[0]);
        cliente.setApellidos(partes.length > 1 ? partes[1] : "");
        cliente.setPesoInicial(clienteSeleccionado.getPeso());
        cliente.setAltura(clienteSeleccionado.getAltura());
        cliente.setGenero(clienteSeleccionado.getGenero());
        cliente.setObjetivoFitness(clienteSeleccionado.getObjetivo());
        cliente.setFechaNacimiento(clienteSeleccionado.getFechaNacimiento());

        // Llamar al CoachService
        String recomendacion = coachService.obtenerRecomendacion(cliente);

        // Mostrar resultado en un diálogo con JTextArea scrollable
        JTextArea textArea = new JTextArea(18, 55);
        textArea.setText(recomendacion);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setCaretPosition(0);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setBackground(new Color(245, 248, 255));
        textArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scrollArea = new JScrollPane(textArea);
        scrollArea.setPreferredSize(new Dimension(600, 300));

        JOptionPane.showMessageDialog(this, scrollArea,
                "🤖 Recomendación IA — " + clienteSeleccionado.getNombreCompleto(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────────
    //  Ver / Editar perfil del cliente seleccionado
    // ─────────────────────────────────────────────────────────────

    private void verEditarPerfil() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un cliente en la tabla primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idCliente = (int) modeloTabla.getValueAt(fila, 0);
        ClienteDetalleDTO dto = clientesData.stream()
                .filter(c -> c.getId() == idCliente).findFirst().orElse(null);
        if (dto == null) return;

        PerfilDialog dlg = new PerfilDialog(this, dto);
        dlg.setVisible(true);
        if (dlg.isCambiosGuardados()) cargarDatos();   // refrescar tabla si hubo cambios
    }

    // ─────────────────────────────────────────────────────────────
    //  Eliminar el cliente seleccionado
    // ─────────────────────────────────────────────────────────────

    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un cliente en la tabla primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int    idCliente = (int) modeloTabla.getValueAt(fila, 0);
        String nombre    = (String) modeloTabla.getValueAt(fila, 1);

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + nombre + " y todos sus datos?\n" +
                "Esta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resp == JOptionPane.YES_OPTION) {
            boolean ok = usuarioDAO.eliminar(idCliente);
            if (ok) {
                cargarDatos();
                JOptionPane.showMessageDialog(this,
                        nombre + " ha sido eliminado correctamente.",
                        "Eliminado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar. Inténtalo de nuevo.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
