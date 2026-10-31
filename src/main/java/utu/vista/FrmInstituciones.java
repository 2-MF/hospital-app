package utu.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;

import utu.modelo.Institucion;
import utu.modelo.Pais;
import utu.dao.InstitucionDAO;
import utu.dao.PaisDAO;


public class FrmInstituciones extends JInternalFrame {

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtBuscar;

    private JCheckBox chkActivo;

    private JComboBox<Pais> cmbPais;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnCancelar;

    private JTable tabla;

    private InstitucionDAO institucionDAO = new InstitucionDAO();
    private PaisDAO paisDAO = new PaisDAO();
    private DefaultTableModel modeloTabla;
    private javax.swing.table.TableRowSorter<TableModel> ordenador;

    public FrmInstituciones() {

        super("Mantenimiento de Instituciones", true, true, true, true);

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
    formulario.add(new JLabel("Dirección"), gbc);

    gbc.gridx = 1;
    txtDireccion = new JTextField(25);
    formulario.add(txtDireccion, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    formulario.add(new JLabel("Teléfono"), gbc);

    gbc.gridx = 1;
    txtTelefono = new JTextField(25);
    formulario.add(txtTelefono, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    formulario.add(new JLabel("País"), gbc);

    gbc.gridx = 1;
    cmbPais = new JComboBox<>();
    cmbPais.setRenderer(new DefaultListCellRenderer(){
        @Override
        public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if(value instanceof Pais p){ setText(p.getNombre()); }
            return this;
        }
    });
    formulario.add(cmbPais, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    formulario.add(new JLabel("Activo"), gbc);

    gbc.gridx = 1;
    chkActivo = new JCheckBox();
    chkActivo.setSelected(true);
    formulario.add(chkActivo, gbc);

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
            "Dirección",
            "Teléfono",
            "País",
            "Activo"
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
        cmbPais.removeAllItems();
        for(Pais p : paisDAO.listarTodos()){
            cmbPais.addItem(p);
        }
    }

    private void seleccionarPais(int id){
        for(int i=0;i<cmbPais.getItemCount();i++){
            Pais p = cmbPais.getItemAt(i);
            if(p != null && p.getIdPais() == id){ cmbPais.setSelectedIndex(i); return; }
        }
        cmbPais.setSelectedIndex(-1);
    }

    private void cargarTabla(){
        modeloTabla.setRowCount(0);

        List<Institucion> lista = institucionDAO.listarTodos();
        for(Institucion i : lista){

            modeloTabla.addRow(new Object[]{
                    i.getIdInstitucion(),
                    i.getNombre(),
                    i.getDireccion(),
                    i.getTelefono(),
                    i.getPais() == null ? "" : i.getPais().getNombre(),
                    i.isActivo()
            });
        }
    }

    private void limpiar(){
        txtId.setText("");
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        cmbPais.setSelectedIndex(-1);
        chkActivo.setSelected(true);
        txtNombre.requestFocus();
    }

    private Institucion obtenerInstitucion(){

        Institucion institucion = new Institucion();

        if(!txtId.getText().isBlank()){
            institucion.setIdInstitucion(Integer.parseInt(txtId.getText()));
        }

        institucion.setNombre(txtNombre.getText());
        institucion.setDireccion(txtDireccion.getText());
        institucion.setTelefono(txtTelefono.getText());
        institucion.setPais((Pais) cmbPais.getSelectedItem());
        institucion.setActivo(chkActivo.isSelected());

        return institucion;
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

        if(cmbPais.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione país.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(institucionDAO.insertar(obtenerInstitucion())){

            JOptionPane.showMessageDialog(this, "Institución registrada.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar la institución.",
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

        if(cmbPais.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione país.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(institucionDAO.actualizar(obtenerInstitucion())){

            JOptionPane.showMessageDialog(this, "Institución modificada.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo modificar la institución.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar(){

        if(txtId.getText().isBlank()) return;

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la institución?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if(opcion==JOptionPane.YES_OPTION){

            if(institucionDAO.eliminar(Integer.parseInt(txtId.getText()))){
                JOptionPane.showMessageDialog(this, "Institución eliminada.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar la institución.",
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

        Institucion institucion = institucionDAO.buscarPorId(Integer.parseInt(id.toString()));

        if (institucion == null) {
            return;
        }

        txtId.setText(String.valueOf(institucion.getIdInstitucion()));

        txtNombre.setText(institucion.getNombre() == null ? "" : institucion.getNombre());

        txtDireccion.setText(institucion.getDireccion() == null ? "" : institucion.getDireccion());

        txtTelefono.setText(institucion.getTelefono() == null ? "" : institucion.getTelefono());

        chkActivo.setSelected(institucion.isActivo());

        seleccionarPais(institucion.getPais() == null ? -1 : institucion.getPais().getIdPais());
    }

}