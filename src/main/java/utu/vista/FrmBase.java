package utu.vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

public abstract class FrmBase extends JInternalFrame {

    protected final JTextField txtId = new JTextField(8);
    protected final DefaultTableModel modeloTabla;
    protected final JTable tabla;

    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnModificar = new JButton("Modificar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnCancelar = new JButton("Cancelar");

    private final JPanel formulario = new JPanel(new GridBagLayout());
    private final JPanel datos = new JPanel(new GridBagLayout());
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final GridBagConstraints gbcDatos = new GridBagConstraints();
    private final String entidad;
    private final boolean femenino;
    private int fila;
    private JComponent primerCampo;

    protected FrmBase(String titulo, String entidad, boolean femenino, int ancho, int alto, String... columnas) {
        super(titulo, true, true, true, true);
        this.entidad = entidad;
        this.femenino = femenino;
        setSize(ancho, alto);
        setLayout(new BorderLayout());

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        txtId.setEditable(false);
        agregarFila("ID", txtId);

        gbcDatos.anchor = GridBagConstraints.NORTH;
        gbcDatos.insets = new Insets(0, 10, 0, 10);
        datos.add(formulario, gbcDatos);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        for (JButton boton : List.of(btnNuevo, btnGuardar, btnModificar, btnEliminar, btnCancelar)) {
            botones.add(boton);
        }
        JPanel superior = new JPanel(new BorderLayout());
        superior.add(datos, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH);

        JTextField txtBuscar = new JTextField(25);
        JPanel buscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscar.add(new JLabel("Buscar"));
        buscar.add(txtBuscar);

        modeloTabla = Tablas.modeloSoloLectura(columnas);
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Tablas.conBuscador(tabla, txtBuscar);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.add(buscar, BorderLayout.NORTH);
        panelTabla.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);
    }

    protected abstract void cargarTabla();

    protected abstract void limpiarCampos();

    protected abstract void mostrar(int filaModelo);

    protected abstract boolean insertar();

    protected abstract boolean actualizar();

    protected abstract boolean borrar(int id);

    protected String validar(boolean nuevo) {
        return null;
    }

    protected String bloqueoEliminar(int id) {
        return null;
    }

    protected String preguntaEliminar() {
        return "¿Eliminar " + articulo() + entidad + "?";
    }

    protected final void iniciar() {
        cargarTabla();
        btnNuevo.addActionListener(e -> limpiar());
        btnCancelar.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        tabla.getSelectionModel().addListSelectionListener(this::filaSeleccionada);
    }

    protected <T extends JComponent> T agregarCampo(String etiqueta, T campo) {
        agregarFila(etiqueta, campo);
        if (primerCampo == null) {
            primerCampo = campo;
        }
        return campo;
    }

    protected JTextField campoTexto(String etiqueta) {
        return agregarCampo(etiqueta, new JTextField(25));
    }

    protected JTextField campoTexto(String etiqueta, int columnas) {
        return agregarCampo(etiqueta, new JTextField(columnas));
    }

    protected JCheckBox campoActivo() {
        JCheckBox chkActivo = new JCheckBox();
        chkActivo.setSelected(true);
        return agregarCampo("Activo", chkActivo);
    }

    @SuppressWarnings("unchecked")
    protected <T> JComboBox<T> campoCombo(String etiqueta, Function<T, String> texto) {
        JComboBox<T> combo = new JComboBox<>();
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                    boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                if (valor != null) {
                    setText(texto.apply((T) valor));
                }
                return this;
            }
        });
        return agregarCampo(etiqueta, combo);
    }

    protected void colocarAlLado(JComponent panel) {
        gbcDatos.gridx = 1;
        datos.add(panel, gbcDatos);
    }

    protected void limpiar() {
        tabla.clearSelection();
        txtId.setText("");
        limpiarCampos();
        if (primerCampo != null) {
            primerCampo.requestFocusInWindow();
        }
    }

    protected void guardar() {
        if (!txtId.getText().isBlank()) {
            aviso("Hay un registro seleccionado. Use \"Modificar\" para cambiarlo o \"Nuevo\" para cargar otro.");
            return;
        }
        String problema = validar(true);
        if (problema != null) {
            aviso(problema);
            return;
        }
        terminar(insertar(), adjetivo("registrad"), "registrar");
    }

    protected void modificar() {
        if (idActual() == 0) {
            aviso("Seleccione " + articuloIndefinido() + entidad + " de la tabla.");
            return;
        }
        String problema = validar(false);
        if (problema != null) {
            aviso(problema);
            return;
        }
        terminar(actualizar(), adjetivo("modificad"), "modificar");
    }

    protected void eliminar() {
        int id = idActual();
        if (id == 0) {
            aviso("Seleccione " + articuloIndefinido() + entidad + " de la tabla.");
            return;
        }
        String bloqueo = bloqueoEliminar(id);
        if (bloqueo != null) {
            aviso(bloqueo);
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this, preguntaEliminar(), "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        if (borrar(id)) {
            JOptionPane.showMessageDialog(this, adjetivo("eliminad"));
        } else {
            error("No se pudo eliminar " + articulo() + entidad + ".");
        }
        limpiar();
        cargarTabla();
    }

    protected void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    protected int idActual() {
        return txtId.getText().isBlank() ? 0 : Integer.parseInt(txtId.getText());
    }

    protected String celda(int filaModelo, int columna) {
        return texto(modeloTabla.getValueAt(filaModelo, columna));
    }

    protected static String texto(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    protected static boolean fechaInvalida(JTextField campo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            return false;
        }
        try {
            LocalDate.parse(texto);
            return false;
        } catch (DateTimeParseException e) {
            return true;
        }
    }

    protected static LocalDate fecha(JTextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : LocalDate.parse(texto);
    }

    protected static BigDecimal decimal(JTextField campo) {
        try {
            return new BigDecimal(campo.getText().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static <T> void llenar(JComboBox<T> combo, List<T> items) {
        combo.removeAllItems();
        items.forEach(combo::addItem);
    }

    protected static <T> void seleccionar(JComboBox<T> combo, Predicate<T> coincide) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (coincide.test(combo.getItemAt(i))) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.setSelectedIndex(-1);
    }

    protected static void seleccionarPrimero(JComboBox<?> combo) {
        combo.setSelectedIndex(combo.getItemCount() > 0 ? 0 : -1);
    }

    private void agregarFila(String etiqueta, JComponent campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        formulario.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        formulario.add(campo, gbc);
        fila++;
    }

    private void terminar(boolean ok, String exito, String accion) {
        if (ok) {
            JOptionPane.showMessageDialog(this, exito);
            limpiar();
            cargarTabla();
        } else {
            error("No se pudo " + accion + " " + articulo() + entidad + ".");
        }
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void filaSeleccionada(ListSelectionEvent e) {
        int seleccionada = tabla.getSelectedRow();
        if (e.getValueIsAdjusting() || seleccionada < 0) {
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(seleccionada);
        Object id = modeloTabla.getValueAt(filaModelo, 0);
        if (id != null) {
            txtId.setText(id.toString());
            mostrar(filaModelo);
        }
    }

    private String articulo() {
        return femenino ? "la " : "el ";
    }

    private String articuloIndefinido() {
        return femenino ? "una " : "un ";
    }

    private String adjetivo(String raiz) {
        String nombre = Character.toUpperCase(entidad.charAt(0)) + entidad.substring(1);
        return nombre + " " + raiz + (femenino ? "a." : "o.");
    }
}
