package utu.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;

import utu.modelo.TipoEquipo;
import utu.dao.TipoEquipoDAO;


public class FrmTipoEquipo extends JInternalFrame {

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

    private TipoEquipoDAO tipoEquipoDAO = new TipoEquipoDAO();
    private DefaultTableModel modeloTabla;
    private javax.swing.table.TableRowSorter<TableModel> ordenador;

    public FrmTipoEquipo() {

        super("Mantenimiento de Tipos de Equipo", true, true, true, true);

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

        List<TipoEquipo> lista = tipoEquipoDAO.listarTodos();
        for(TipoEquipo t : lista){

            modeloTabla.addRow(new Object[]{
                    t.getIdTipo(),
                    t.getNombre(),
                    t.getDescripcion(),
                    t.isActivo()
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

    private TipoEquipo obtenerTipoEquipo(){

        TipoEquipo tipo = new TipoEquipo();

        if(!txtId.getText().isBlank()){
            tipo.setIdTipo(Integer.parseInt(txtId.getText()));
        }

        tipo.setNombre(txtNombre.getText());
        tipo.setDescripcion(txtDescripcion.getText());
        tipo.setActivo(chkActivo.isSelected());

        return tipo;
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

        if(tipoEquipoDAO.insertar(obtenerTipoEquipo())){

            JOptionPane.showMessageDialog(this, "Tipo de equipo registrado.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el tipo de equipo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar(){

        if(txtId.getText().isBlank()) return;

        if(tipoEquipoDAO.actualizar(obtenerTipoEquipo())){

            JOptionPane.showMessageDialog(this, "Tipo de equipo modificado.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo modificar el tipo de equipo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar(){

        if(txtId.getText().isBlank()) return;

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el tipo de equipo?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if(opcion==JOptionPane.YES_OPTION){

            if(tipoEquipoDAO.eliminar(Integer.parseInt(txtId.getText()))){
                JOptionPane.showMessageDialog(this, "Tipo de equipo eliminado.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar el tipo de equipo.",
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