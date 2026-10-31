package utu.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;

import utu.modelo.Modelo;
import utu.modelo.Marca;
import utu.dao.ModeloDAO;
import utu.dao.MarcaDAO;


public class FrmModelos extends JInternalFrame {

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtBuscar;

    private JCheckBox chkActivo;

    private JComboBox<Marca> cmbMarca;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnCancelar;

    private JTable tabla;

    private ModeloDAO modeloDAO = new ModeloDAO();
    private MarcaDAO marcaDAO = new MarcaDAO();
    private DefaultTableModel modeloTabla;
    private javax.swing.table.TableRowSorter<TableModel> ordenador;

    public FrmModelos() {

        super("Mantenimiento de Modelos", true, true, true, true);

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
    formulario.add(new JLabel("Marca"), gbc);

    gbc.gridx = 1;
    cmbMarca = new JComboBox<>();
    cmbMarca.setRenderer(new DefaultListCellRenderer(){
        @Override
        public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if(value instanceof Marca m){ setText(m.getNombre()); }
            return this;
        }
    });
    formulario.add(cmbMarca, gbc);

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
            "Marca",
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
        cmbMarca.removeAllItems();
        for(Marca m : marcaDAO.listarActivas()){
            cmbMarca.addItem(m);
        }
    }

    private void seleccionarMarca(int id){
        for(int i=0;i<cmbMarca.getItemCount();i++){
            Marca m = cmbMarca.getItemAt(i);
            if(m != null && m.getIdMarca() == id){ cmbMarca.setSelectedIndex(i); return; }
        }
        cmbMarca.setSelectedIndex(-1);
    }

    private void cargarTabla(){
        modeloTabla.setRowCount(0);

        List<Modelo> lista = modeloDAO.listarTodos();
        for(Modelo m : lista){

            modeloTabla.addRow(new Object[]{
                    m.getIdModelo(),
                    m.getNombre(),
                    m.getMarca() == null ? "" : m.getMarca().getNombre(),
                    m.isActivo()
            });
        }
    }

    private void limpiar(){
        txtId.setText("");
        txtNombre.setText("");
        cmbMarca.setSelectedIndex(-1);
        chkActivo.setSelected(true);
        txtNombre.requestFocus();
    }

    private Modelo obtenerModelo(){

        Modelo modelo = new Modelo();

        if(!txtId.getText().isBlank()){
            modelo.setIdModelo(Integer.parseInt(txtId.getText()));
        }

        modelo.setNombre(txtNombre.getText());
        modelo.setMarca((Marca) cmbMarca.getSelectedItem());
        modelo.setActivo(chkActivo.isSelected());

        return modelo;
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

        if(cmbMarca.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione marca.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(modeloDAO.insertar(obtenerModelo())){

            JOptionPane.showMessageDialog(this, "Modelo registrado.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el modelo.",
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

        if(cmbMarca.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione marca.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(modeloDAO.actualizar(obtenerModelo())){

            JOptionPane.showMessageDialog(this, "Modelo modificado.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo modificar el modelo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar(){

        if(txtId.getText().isBlank()) return;

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el modelo?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if(opcion==JOptionPane.YES_OPTION){

            if(modeloDAO.eliminar(Integer.parseInt(txtId.getText()))){
                JOptionPane.showMessageDialog(this, "Modelo eliminado.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar el modelo.",
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

        Modelo mod = modeloDAO.buscarPorId(Integer.parseInt(id.toString()));

        if (mod == null) {
            return;
        }

        txtId.setText(String.valueOf(mod.getIdModelo()));

        txtNombre.setText(mod.getNombre() == null ? "" : mod.getNombre());

        chkActivo.setSelected(mod.isActivo());

        seleccionarMarca(mod.getMarca() == null ? -1 : mod.getMarca().getIdMarca());
    }

}