package utu.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;

import utu.modelo.*;
import utu.dao.*;

public class FrmEquipos extends JInternalFrame {

    private JTextField txtId;
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtSerie;
    private JTextField txtFechaCompra;
    private JTextField txtFechaGarantia;
    private JTextField txtObservaciones;
    private JTextField txtBuscar;

    private JComboBox<String> cmbEstado;
    private JComboBox<Modelo> cmbModelo;
    private JComboBox<TipoEquipo> cmbTipo;
    private JComboBox<Proveedor> cmbProveedor;
    private JComboBox<Ubicacion> cmbUbicacion;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnCancelar;

    private JTable tabla;

    private EquipoDAO equipoDAO = new EquipoDAO();
    private ModeloDAO modeloDAO = new ModeloDAO();
    private TipoEquipoDAO tipoEquipoDAO = new TipoEquipoDAO();
    private ProveedorDAO proveedorDAO = new ProveedorDAO();
    private UbicacionDAO ubicacionDAO = new UbicacionDAO();
    private DefaultTableModel modeloTabla;
    private javax.swing.table.TableRowSorter<TableModel> ordenador;

    public FrmEquipos() {

        super("Mantenimiento de Equipos", true, true, true, true);

        iniciarComponentes();

        modeloTabla = (DefaultTableModel) tabla.getModel();

        cargarCombos();
        cargarTabla();
        eventos();

    }

    private void iniciarComponentes() {

        setSize(1200, 750);
        setLayout(new BorderLayout());

        // ==========================
        // FORMULARIO
        // ==========================
        JPanel formulario = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formulario.add(new JLabel("ID"), gbc);

        gbc.gridx = 1;
        txtId = new JTextField(8);
        txtId.setEditable(false);
        formulario.add(txtId, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Código"), gbc);

        gbc.gridx = 1;
        txtCodigo = new JTextField(25);
        formulario.add(txtCodigo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Nombre"), gbc);

        gbc.gridx = 1;
        txtNombre = new JTextField(25);
        formulario.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Serie"), gbc);

        gbc.gridx = 1;
        txtSerie = new JTextField(25);
        formulario.add(txtSerie, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Fecha Compra"), gbc);

        gbc.gridx = 1;
        txtFechaCompra = new JTextField(12);
        formulario.add(txtFechaCompra, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Fecha Garantía"), gbc);

        gbc.gridx = 1;
        txtFechaGarantia = new JTextField(12);
        formulario.add(txtFechaGarantia, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Estado"), gbc);

        gbc.gridx = 1;
        cmbEstado = new javax.swing.JComboBox<>();
        cmbEstado.addItem("ACTIVO");
        cmbEstado.addItem("INACTIVO");
        cmbEstado.addItem("MANTENIMIENTO");
        cmbEstado.setSelectedIndex(0);
        formulario.add(cmbEstado, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Observaciones"), gbc);

        gbc.gridx = 1;
        txtObservaciones = new JTextField(25);
        formulario.add(txtObservaciones, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Modelo"), gbc);

        gbc.gridx = 1;
        cmbModelo = new javax.swing.JComboBox<>();
        cmbModelo.setRenderer(new javax.swing.DefaultListCellRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof utu.modelo.Modelo m){ setText(m.getNombre()); }
                return this;
            }
        });
        formulario.add(cmbModelo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Tipo"), gbc);

        gbc.gridx = 1;
        cmbTipo = new javax.swing.JComboBox<>();
        cmbTipo.setRenderer(new javax.swing.DefaultListCellRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof utu.modelo.TipoEquipo t){ setText(t.getNombre()); }
                return this;
            }
        });
        formulario.add(cmbTipo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Proveedor"), gbc);

        gbc.gridx = 1;
        cmbProveedor = new javax.swing.JComboBox<>();
        cmbProveedor.setRenderer(new javax.swing.DefaultListCellRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof utu.modelo.Proveedor p){ setText(p.getNombre()); }
                return this;
            }
        });
        formulario.add(cmbProveedor, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Ubicación"), gbc);

        gbc.gridx = 1;
        cmbUbicacion = new javax.swing.JComboBox<>();
        cmbUbicacion.setRenderer(new javax.swing.DefaultListCellRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof utu.modelo.Ubicacion ub){ setText(ub.getNombre()); }
                return this;
            }
        });
        formulario.add(cmbUbicacion, gbc);

        // ==========================
        // BOTONES
        // ==========================
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));

        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnCancelar = new JButton("Cancelar");

        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnModificar);
        botones.add(btnEliminar);
        botones.add(btnCancelar);

        // ==========================
        // PANEL SUPERIOR
        // ==========================
        JPanel panelSuperior = new JPanel(new BorderLayout());

        panelSuperior.add(formulario, BorderLayout.CENTER);
        panelSuperior.add(botones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        // ==========================
        // TABLA
        // ==========================
        JPanel panelTabla = new JPanel(new BorderLayout());

        JPanel buscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscar.add(new JLabel("Buscar"));

        txtBuscar = new JTextField(25);
        buscar.add(txtBuscar);

        panelTabla.add(buscar, BorderLayout.NORTH);

        tabla = new JTable();

        tabla.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "ID",
                "Código",
                "Nombre",
                "Serie",
                "Estado",
                "Modelo",
                "Tipo",
                "Proveedor",
                "Ubicación"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        ordenador = new javax.swing.table.TableRowSorter<>(tabla.getModel());
        tabla.setRowSorter(ordenador);

        panelTabla.add(new JScrollPane(tabla), BorderLayout.CENTER);

        add(panelTabla, BorderLayout.CENTER);
    }

    private void cargarCombos(){

        cmbModelo.removeAllItems();
        for(Modelo m : modeloDAO.listarActivas()){
            cmbModelo.addItem(m);
        }

        cmbTipo.removeAllItems();
        for(TipoEquipo t : tipoEquipoDAO.listarActivas()){
            cmbTipo.addItem(t);
        }

        cmbProveedor.removeAllItems();
        for(Proveedor p : proveedorDAO.listarActivas()){
            cmbProveedor.addItem(p);
        }

        cmbUbicacion.removeAllItems();
        for(Ubicacion ub : ubicacionDAO.listarTodos()){
            cmbUbicacion.addItem(ub);
        }
    }

    private void cargarTabla(){
        modeloTabla.setRowCount(0);

        List<Equipo> lista = equipoDAO.listarTodos();
        for(Equipo eq : lista){

            modeloTabla.addRow(new Object[]{
                    eq.getIdEquipo(),
                    eq.getCodigo(),
                    eq.getNombre(),
                    eq.getSerie(),
                    eq.getEstado(),
                    eq.getModelo() == null ? "" : eq.getModelo().getNombre(),
                    eq.getTipo() == null ? "" : eq.getTipo().getNombre(),
                    eq.getProveedor() == null ? "" : eq.getProveedor().getNombre(),
                    eq.getUbicacion() == null ? "" : eq.getUbicacion().getNombre()
            });
        }
    }

    private void limpiar(){
        txtId.setText("");
        txtCodigo.setText("");
        txtNombre.setText("");
        txtSerie.setText("");
        txtFechaCompra.setText("");
        txtFechaGarantia.setText("");
        txtObservaciones.setText("");
        if(cmbEstado.getItemCount() > 0){
            cmbEstado.setSelectedIndex(0);
        }
        if(cmbModelo.getItemCount() > 0){
            cmbModelo.setSelectedIndex(0);
        }
        if(cmbTipo.getItemCount() > 0){
            cmbTipo.setSelectedIndex(0);
        }
        if(cmbProveedor.getItemCount() > 0){
            cmbProveedor.setSelectedIndex(0);
        }
        if(cmbUbicacion.getItemCount() > 0){
            cmbUbicacion.setSelectedIndex(0);
        }
        txtCodigo.requestFocus();
    }

    private Equipo obtenerEquipo(java.time.LocalDate fechaCompra, java.time.LocalDate fechaGarantia){
        Equipo equipo = new Equipo();

        if(!txtId.getText().isBlank()){
            equipo.setIdEquipo(Integer.parseInt(txtId.getText()));
        }

        equipo.setCodigo(txtCodigo.getText());
        equipo.setNombre(txtNombre.getText());
        equipo.setSerie(txtSerie.getText());
        equipo.setFechaCompra(fechaCompra);
        equipo.setFechaGarantia(fechaGarantia);
        equipo.setEstado((String) cmbEstado.getSelectedItem());
        equipo.setObservaciones(txtObservaciones.getText());
        equipo.setModelo((Modelo) cmbModelo.getSelectedItem());
        equipo.setTipo((TipoEquipo) cmbTipo.getSelectedItem());
        equipo.setProveedor((Proveedor) cmbProveedor.getSelectedItem());
        equipo.setUbicacion((Ubicacion) cmbUbicacion.getSelectedItem());

        return equipo;
    }

    /** Valida el formato de una fecha; muestra un error y devuelve false si es inválida. */
    private boolean fechaValida(JTextField campo, String nombreCampo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            return true;
        }
        try {
            java.time.LocalDate.parse(texto);
            return true;
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    nombreCampo + " inválida (formato yyyy-MM-dd).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /** Convierte el texto de un campo en fecha (null si está vacío). */
    private java.time.LocalDate leerFecha(JTextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : java.time.LocalDate.parse(texto);
    }

    private void eventos(){

        btnNuevo.addActionListener(e->limpiar());
        btnGuardar.addActionListener(e->guardar());
        btnModificar.addActionListener(e->modificar());
        btnEliminar.addActionListener(e->eliminar());
        btnCancelar.addActionListener(e->limpiar());

        tabla.getSelectionModel().addListSelectionListener(this::seleccionarFila);

        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }
        });
    }

    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            ordenador.setRowFilter(null);
        } else {
            ordenador.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto)));
        }
    }

    private void guardar(){

        if(txtCodigo.getText().isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese el código.");
            return;
        }

        if(txtNombre.getText().isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese el nombre.");
            return;
        }

        if(cmbModelo.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione un modelo.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(cmbTipo.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione un tipo.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(cmbProveedor.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione un proveedor.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(cmbUbicacion.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione una ubicación.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!fechaValida(txtFechaCompra, "Fecha de compra") || !fechaValida(txtFechaGarantia, "Fecha de garantía")) {
            return;
        }

        if(txtId.getText().isBlank() && equipoDAO.existeCodigo(txtCodigo.getText().trim())){
            JOptionPane.showMessageDialog(this,
                    "Ya existe un equipo con ese código.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(equipoDAO.insertar(obtenerEquipo(leerFecha(txtFechaCompra), leerFecha(txtFechaGarantia)))){

            JOptionPane.showMessageDialog(this, "Equipo registrado.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el equipo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar(){

        if(txtId.getText().isBlank()) return;

        if (!fechaValida(txtFechaCompra, "Fecha de compra") || !fechaValida(txtFechaGarantia, "Fecha de garantía")) {
            return;
        }

        if(equipoDAO.actualizar(obtenerEquipo(leerFecha(txtFechaCompra), leerFecha(txtFechaGarantia)))){

            JOptionPane.showMessageDialog(this, "Equipo modificado.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo modificar el equipo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar(){

        if(txtId.getText().isBlank()) return;

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Dar de baja el equipo? Su estado pasará a INACTIVO.",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if(opcion==JOptionPane.YES_OPTION){

            if(equipoDAO.eliminar(Integer.parseInt(txtId.getText()))){
                JOptionPane.showMessageDialog(this, "Equipo dado de baja.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo dar de baja el equipo.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            limpiar();
            cargarTabla();
        }
    }

    private void seleccionarFila(ListSelectionEvent e) {

        if (e.getValueIsAdjusting()) {
            return;
        }

        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            return;
        }

        TableModel modelo = tabla.getModel();

        int filaModelo = tabla.convertRowIndexToModel(fila);

        Object idValor = modelo.getValueAt(filaModelo, 0);
        if (idValor == null) {
            return;
        }

        Equipo eq = equipoDAO.buscarPorId(Integer.parseInt(String.valueOf(idValor)));
        if (eq == null) {
            return;
        }

        txtId.setText(String.valueOf(eq.getIdEquipo()));
        txtCodigo.setText(eq.getCodigo() == null ? "" : eq.getCodigo());
        txtNombre.setText(eq.getNombre() == null ? "" : eq.getNombre());
        txtSerie.setText(eq.getSerie() == null ? "" : eq.getSerie());
        txtFechaCompra.setText(eq.getFechaCompra() == null ? "" : eq.getFechaCompra().toString());
        txtFechaGarantia.setText(eq.getFechaGarantia() == null ? "" : eq.getFechaGarantia().toString());

        if(eq.getEstado() != null){
            cmbEstado.setSelectedItem(eq.getEstado());
        }

        txtObservaciones.setText(eq.getObservaciones() == null ? "" : eq.getObservaciones());

        seleccionarModelo(eq.getModelo() == null ? -1 : eq.getModelo().getIdModelo());
        seleccionarTipo(eq.getTipo() == null ? -1 : eq.getTipo().getIdTipo());
        seleccionarProveedor(eq.getProveedor() == null ? -1 : eq.getProveedor().getIdProveedor());
        seleccionarUbicacion(eq.getUbicacion() == null ? -1 : eq.getUbicacion().getIdUbicacion());
    }

    private void seleccionarModelo(int id){

        for(int i = 0; i < cmbModelo.getItemCount(); i++){
            Object item = cmbModelo.getItemAt(i);
            if(item instanceof utu.modelo.Modelo m && m.getIdModelo() == id){
                cmbModelo.setSelectedIndex(i);
                return;
            }
        }

        cmbModelo.setSelectedIndex(-1);
    }

    private void seleccionarTipo(int id){

        for(int i = 0; i < cmbTipo.getItemCount(); i++){
            Object item = cmbTipo.getItemAt(i);
            if(item instanceof utu.modelo.TipoEquipo t && t.getIdTipo() == id){
                cmbTipo.setSelectedIndex(i);
                return;
            }
        }

        cmbTipo.setSelectedIndex(-1);
    }

    private void seleccionarProveedor(int id){

        for(int i = 0; i < cmbProveedor.getItemCount(); i++){
            Object item = cmbProveedor.getItemAt(i);
            if(item instanceof utu.modelo.Proveedor p && p.getIdProveedor() == id){
                cmbProveedor.setSelectedIndex(i);
                return;
            }
        }

        cmbProveedor.setSelectedIndex(-1);
    }

    private void seleccionarUbicacion(int id){

        for(int i = 0; i < cmbUbicacion.getItemCount(); i++){
            Object item = cmbUbicacion.getItemAt(i);
            if(item instanceof utu.modelo.Ubicacion ub && ub.getIdUbicacion() == id){
                cmbUbicacion.setSelectedIndex(i);
                return;
            }
        }

        cmbUbicacion.setSelectedIndex(-1);
    }
}
