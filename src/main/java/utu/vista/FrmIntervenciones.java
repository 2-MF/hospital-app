package utu.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;

import utu.modelo.*;
import utu.dao.*;

public class FrmIntervenciones extends JInternalFrame {

    private JTextField txtId;
    private JTextField txtFecha;
    private JTextField txtCosto;
    private JTextField txtDescripcion;
    private JTextField txtBuscar;

    private JComboBox<Equipo> cmbEquipo;
    private JComboBox<TipoIntervencion> cmbTipo;
    private JComboBox<Usuario> cmbUsuario;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnCancelar;

    private JTable tabla;

    private IntervencionDAO intervencionDAO = new IntervencionDAO();
    private EquipoDAO equipoDAO = new EquipoDAO();
    private TipoIntervencionDAO tipoIntervencionDAO = new TipoIntervencionDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private DefaultTableModel modeloTabla;
    private javax.swing.table.TableRowSorter<TableModel> ordenador;

    public FrmIntervenciones() {

        super("Registro de Intervenciones", true, true, true, true);

        iniciarComponentes();

        modeloTabla = (DefaultTableModel) tabla.getModel();

        cargarCombos();
        cargarTabla();
        eventos();

    }

    private void iniciarComponentes() {

        setSize(1100, 700);
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
        formulario.add(new JLabel("Fecha (yyyy-MM-dd)"), gbc);

        gbc.gridx = 1;
        txtFecha = new JTextField(12);
        formulario.add(txtFecha, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Equipo"), gbc);

        gbc.gridx = 1;
        cmbEquipo = new javax.swing.JComboBox<>();
        cmbEquipo.setRenderer(new javax.swing.DefaultListCellRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof utu.modelo.Equipo eq){ setText(eq.getCodigo() + " - " + eq.getNombre()); }
                return this;
            }
        });
        formulario.add(cmbEquipo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Tipo Intervención"), gbc);

        gbc.gridx = 1;
        cmbTipo = new javax.swing.JComboBox<>();
        cmbTipo.setRenderer(new javax.swing.DefaultListCellRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof utu.modelo.TipoIntervencion t){ setText(t.getNombre()); }
                return this;
            }
        });
        formulario.add(cmbTipo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Usuario"), gbc);

        gbc.gridx = 1;
        cmbUsuario = new javax.swing.JComboBox<>();
        cmbUsuario.setRenderer(new javax.swing.DefaultListCellRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof utu.modelo.Usuario u){ setText(u.getNombre() + " " + u.getApellido()); }
                return this;
            }
        });
        formulario.add(cmbUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Costo"), gbc);

        gbc.gridx = 1;
        txtCosto = new JTextField(12);
        formulario.add(txtCosto, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Descripción"), gbc);

        gbc.gridx = 1;
        txtDescripcion = new JTextField(25);
        formulario.add(txtDescripcion, gbc);

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
                "Fecha",
                "Equipo",
                "Tipo Intervención",
                "Usuario",
                "Costo"
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

        cmbEquipo.removeAllItems();
        for(Equipo eq : equipoDAO.listarTodos()){
            cmbEquipo.addItem(eq);
        }

        cmbTipo.removeAllItems();
        for(TipoIntervencion t : tipoIntervencionDAO.listarTodos()){
            cmbTipo.addItem(t);
        }

        cmbUsuario.removeAllItems();
        for(Usuario u : usuarioDAO.listarTodos()){
            cmbUsuario.addItem(u);
        }
    }

    private void cargarTabla(){
        modeloTabla.setRowCount(0);

        List<Intervencion> lista = intervencionDAO.listarTodos();
        for(Intervencion i : lista){

            modeloTabla.addRow(new Object[]{
                    i.getIdIntervencion(),
                    i.getFecha() == null ? "" : i.getFecha().toString(),
                    i.getEquipo() == null ? "" : i.getEquipo().getNombre(),
                    i.getTipoIntervencion() == null ? "" : i.getTipoIntervencion().getNombre(),
                    i.getUsuario() == null ? "" : i.getUsuario().getNombre() + " " + i.getUsuario().getApellido(),
                    i.getCosto()
            });
        }
    }

    private void limpiar(){
        txtId.setText("");
        txtFecha.setText("");
        txtCosto.setText("");
        txtDescripcion.setText("");
        if(cmbEquipo.getItemCount() > 0){
            cmbEquipo.setSelectedIndex(0);
        }
        if(cmbTipo.getItemCount() > 0){
            cmbTipo.setSelectedIndex(0);
        }
        if(cmbUsuario.getItemCount() > 0){
            cmbUsuario.setSelectedIndex(0);
        }
        txtFecha.requestFocus();
    }

    private Intervencion obtenerIntervencion(java.time.LocalDate fecha, java.math.BigDecimal costo){

        Intervencion intervencion = new Intervencion();

        if(!txtId.getText().isBlank()){
            intervencion.setIdIntervencion(Integer.parseInt(txtId.getText()));
        }

        intervencion.setFecha(fecha);
        intervencion.setCosto(costo);
        intervencion.setEquipo((Equipo) cmbEquipo.getSelectedItem());
        intervencion.setTipoIntervencion((TipoIntervencion) cmbTipo.getSelectedItem());
        intervencion.setUsuario((Usuario) cmbUsuario.getSelectedItem());
        intervencion.setDescripcion(txtDescripcion.getText());

        return intervencion;
    }

    /** Lee la fecha del formulario; muestra un error y devuelve null si es inválida. */
    private java.time.LocalDate leerFecha() {
        String texto = txtFecha.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la fecha.");
            return null;
        }
        try {
            return java.time.LocalDate.parse(texto);
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Fecha inválida (formato yyyy-MM-dd).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /** Lee el costo del formulario; muestra un error y devuelve null si es inválido. */
    private java.math.BigDecimal leerCosto() {
        String texto = txtCosto.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el costo.");
            return null;
        }
        try {
            return new java.math.BigDecimal(texto);
        } catch (java.lang.NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Costo inválido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
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

        if(cmbEquipo.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione un equipo.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(cmbTipo.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione el tipo de intervención.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(cmbUsuario.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione un usuario.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.time.LocalDate fecha = leerFecha();
        if (fecha == null) {
            return;
        }

        java.math.BigDecimal costo = leerCosto();
        if (costo == null) {
            return;
        }

        if(intervencionDAO.insertar(obtenerIntervencion(fecha, costo))){

            JOptionPane.showMessageDialog(this, "Intervención registrada.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar la intervención.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar(){

        if(txtId.getText().isBlank()) return;

        java.time.LocalDate fecha = leerFecha();
        if (fecha == null) {
            return;
        }

        java.math.BigDecimal costo = leerCosto();
        if (costo == null) {
            return;
        }

        if(intervencionDAO.actualizar(obtenerIntervencion(fecha, costo))){

            JOptionPane.showMessageDialog(this, "Intervención modificada.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo modificar la intervención.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar(){

        if(txtId.getText().isBlank()) return;

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la intervención? Esta acción no se puede deshacer.",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if(opcion==JOptionPane.YES_OPTION){

            if(intervencionDAO.eliminar(Integer.parseInt(txtId.getText()))){
                JOptionPane.showMessageDialog(this, "Intervención eliminada.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar la intervención.",
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

        Intervencion i = intervencionDAO.buscarPorId(Integer.parseInt(String.valueOf(idValor)));
        if (i == null) {
            return;
        }

        txtId.setText(String.valueOf(i.getIdIntervencion()));
        txtFecha.setText(i.getFecha() == null ? "" : i.getFecha().toString());
        txtCosto.setText(i.getCosto() == null ? "" : String.valueOf(i.getCosto()));
        txtDescripcion.setText(i.getDescripcion() == null ? "" : i.getDescripcion());

        seleccionarEquipo(i.getEquipo() == null ? -1 : i.getEquipo().getIdEquipo());
        seleccionarTipo(i.getTipoIntervencion() == null ? -1 : i.getTipoIntervencion().getIdTipoIntervencion());
        seleccionarUsuario(i.getUsuario() == null ? -1 : i.getUsuario().getIdUsuario());
    }

    private void seleccionarEquipo(int id){

        for(int i = 0; i < cmbEquipo.getItemCount(); i++){
            Object item = cmbEquipo.getItemAt(i);
            if(item instanceof utu.modelo.Equipo eq && eq.getIdEquipo() == id){
                cmbEquipo.setSelectedIndex(i);
                return;
            }
        }

        cmbEquipo.setSelectedIndex(-1);
    }

    private void seleccionarTipo(int id){

        for(int i = 0; i < cmbTipo.getItemCount(); i++){
            Object item = cmbTipo.getItemAt(i);
            if(item instanceof utu.modelo.TipoIntervencion t && t.getIdTipoIntervencion() == id){
                cmbTipo.setSelectedIndex(i);
                return;
            }
        }

        cmbTipo.setSelectedIndex(-1);
    }

    private void seleccionarUsuario(int id){

        for(int i = 0; i < cmbUsuario.getItemCount(); i++){
            Object item = cmbUsuario.getItemAt(i);
            if(item instanceof utu.modelo.Usuario u && u.getIdUsuario() == id){
                cmbUsuario.setSelectedIndex(i);
                return;
            }
        }

        cmbUsuario.setSelectedIndex(-1);
    }
}
