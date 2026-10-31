package utu.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;

import utu.modelo.Ubicacion;
import utu.modelo.Institucion;
import utu.dao.UbicacionDAO;
import utu.dao.InstitucionDAO;


public class FrmUbicaciones extends JInternalFrame {

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtBuscar;

    private JComboBox<Institucion> cmbInstitucion;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnCancelar;

    private JTable tabla;

    private UbicacionDAO ubicacionDAO = new UbicacionDAO();
    private InstitucionDAO institucionDAO = new InstitucionDAO();
    private DefaultTableModel modeloTabla;
    private javax.swing.table.TableRowSorter<TableModel> ordenador;

    public FrmUbicaciones() {

        super("Mantenimiento de Ubicaciones", true, true, true, true);

        iniciarComponentes();

        cargarCombos();

        modeloTabla = (DefaultTableModel) tabla.getModel();

        cargarTabla();
        eventos();

    }

    private void iniciarComponentes() {

    setSize(1000, 700);
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
    formulario.add(new JLabel("Código"), gbc);

    gbc.gridx = 1;
    txtId = new JTextField(8);
    txtId.setEditable(false);
    formulario.add(txtId, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    formulario.add(new JLabel("Nombre"), gbc);

    gbc.gridx = 1;
    txtNombre = new JTextField(25);
    formulario.add(txtNombre, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    formulario.add(new JLabel("Descripción"), gbc);

    gbc.gridx = 1;
    txtDescripcion = new JTextField(25);
    formulario.add(txtDescripcion, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    formulario.add(new JLabel("Institución"), gbc);

    gbc.gridx = 1;
    cmbInstitucion = new JComboBox<>();
    cmbInstitucion.setRenderer(new DefaultListCellRenderer(){
        @Override
        public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if(value instanceof Institucion i){ setText(i.getNombre()); }
            return this;
        }
    });
    formulario.add(cmbInstitucion, gbc);

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
            "Nombre",
            "Descripción",
            "Institución"
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
        cmbInstitucion.removeAllItems();
        for(Institucion i : institucionDAO.listarActivas()){
            cmbInstitucion.addItem(i);
        }
    }

    private void seleccionarInstitucion(int id){
        for(int i=0;i<cmbInstitucion.getItemCount();i++){
            Institucion ins = cmbInstitucion.getItemAt(i);
            if(ins != null && ins.getIdInstitucion() == id){ cmbInstitucion.setSelectedIndex(i); return; }
        }
        cmbInstitucion.setSelectedIndex(-1);
    }

    private void cargarTabla(){
        modeloTabla.setRowCount(0);

        List<Ubicacion> lista = ubicacionDAO.listarTodos();
        for(Ubicacion u : lista){

            modeloTabla.addRow(new Object[]{
                    u.getIdUbicacion(),
                    u.getNombre(),
                    u.getDescripcion(),
                    u.getInstitucion() == null ? "" : u.getInstitucion().getNombre()
            });
        }
    }

    private void limpiar(){
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        cmbInstitucion.setSelectedIndex(-1);
        txtNombre.requestFocus();
    }

    private Ubicacion obtenerUbicacion(){

        Ubicacion ubicacion = new Ubicacion();

        if(!txtId.getText().isBlank()){
            ubicacion.setIdUbicacion(Integer.parseInt(txtId.getText()));
        }

        ubicacion.setNombre(txtNombre.getText());
        ubicacion.setDescripcion(txtDescripcion.getText());
        ubicacion.setInstitucion((Institucion) cmbInstitucion.getSelectedItem());

        return ubicacion;
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

        if(txtNombre.getText().isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese el nombre.");
            return;
        }

        if(cmbInstitucion.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione institución.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(ubicacionDAO.insertar(obtenerUbicacion())){

            JOptionPane.showMessageDialog(this, "Ubicación registrada.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar la ubicación.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar(){

        if(txtId.getText().isBlank()) return;

        if(txtNombre.getText().isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese el nombre.");
            return;
        }

        if(cmbInstitucion.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione institución.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(ubicacionDAO.actualizar(obtenerUbicacion())){

            JOptionPane.showMessageDialog(this, "Ubicación modificada.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo modificar la ubicación.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar(){

        if(txtId.getText().isBlank()) return;

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la ubicación? Esta acción no se puede deshacer.",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if(opcion==JOptionPane.YES_OPTION){

            if(ubicacionDAO.eliminar(Integer.parseInt(txtId.getText()))){
                JOptionPane.showMessageDialog(this, "Ubicación eliminada.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar la ubicación.",
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

        Object id = modelo.getValueAt(filaModelo, 0);

        if (id == null) {
            return;
        }

        Ubicacion ubicacion = ubicacionDAO.buscarPorId(Integer.parseInt(id.toString()));

        if (ubicacion == null) {
            return;
        }

        txtId.setText(String.valueOf(ubicacion.getIdUbicacion()));

        txtNombre.setText(ubicacion.getNombre() == null ? "" : ubicacion.getNombre());

        txtDescripcion.setText(ubicacion.getDescripcion() == null ? "" : ubicacion.getDescripcion());

        seleccionarInstitucion(ubicacion.getInstitucion() == null ? -1 : ubicacion.getInstitucion().getIdInstitucion());
    }

}