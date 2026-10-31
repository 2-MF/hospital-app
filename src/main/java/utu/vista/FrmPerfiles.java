package utu.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;

import utu.modelo.Perfil;
import utu.dao.PerfilDAO;


public class FrmPerfiles extends JInternalFrame {

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtBuscar;

    private JCheckBox chkActivo;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnCancelar;

    private JTable tabla;

    private PerfilDAO perfilDAO = new PerfilDAO();
    private DefaultTableModel modeloTabla;
    private javax.swing.table.TableRowSorter<TableModel> ordenador;

    public FrmPerfiles() {

        super("Mantenimiento de Perfiles", true, true, true, true);

        iniciarComponentes();

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
            "Descripción",
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

    private void cargarTabla(){
        modeloTabla.setRowCount(0);

        List<Perfil> lista = perfilDAO.listarTodos();
        for(Perfil p : lista){

            modeloTabla.addRow(new Object[]{
                    p.getIdPerfil(),
                    p.getNombre(),
                    p.getDescripcion(),
                    p.isActivo()
            });
        }
    }

    private void limpiar(){
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        chkActivo.setSelected(true);
        txtNombre.requestFocus();
    }

    private Perfil obtenerPerfil(){

        Perfil perfil = new Perfil();

        if(!txtId.getText().isBlank()){
            perfil.setIdPerfil(Integer.parseInt(txtId.getText()));
        }

        perfil.setNombre(txtNombre.getText());
        perfil.setDescripcion(txtDescripcion.getText());
        perfil.setActivo(chkActivo.isSelected());

        return perfil;
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

        if(txtId.getText().isBlank() && perfilDAO.existe(txtNombre.getText().trim())){
            JOptionPane.showMessageDialog(this,
                    "Ya existe un perfil con ese nombre.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(perfilDAO.insertar(obtenerPerfil())){

            JOptionPane.showMessageDialog(this, "Perfil registrado.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el perfil.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar(){

        if(txtId.getText().isBlank()) return;

        if(perfilDAO.actualizar(obtenerPerfil())){

            JOptionPane.showMessageDialog(this, "Perfil modificado.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo modificar el perfil.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar(){

        if(txtId.getText().isBlank()) return;

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el perfil?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if(opcion==JOptionPane.YES_OPTION){

            if(perfilDAO.eliminar(Integer.parseInt(txtId.getText()))){
                JOptionPane.showMessageDialog(this, "Perfil eliminado.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar el perfil.",
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
        txtId.setText(id == null ? "" : id.toString());

        Object nombre = modelo.getValueAt(filaModelo, 1);
        txtNombre.setText(nombre == null ? "" : nombre.toString());

        Object descripcion = modelo.getValueAt(filaModelo, 2);
        txtDescripcion.setText(descripcion == null ? "" : descripcion.toString());

        Object activo = modelo.getValueAt(filaModelo, 3);
        chkActivo.setSelected(Boolean.TRUE.equals(activo));
    }

}