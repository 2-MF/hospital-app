package utu.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;

import utu.modelo.*;
import utu.dao.*;

public class FrmUsuarios extends JInternalFrame {

    private JTextField txtId;
    private JTextField txtUsuario;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCedula;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JTextField txtBuscar;

    private JComboBox<Perfil> cmbPerfil;

    private JCheckBox chkActivo;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnCancelar;

    private JTable tabla;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private PerfilDAO perfilDAO = new PerfilDAO();
    private DefaultTableModel modeloTabla;
    private javax.swing.table.TableRowSorter<TableModel> ordenador;

    public FrmUsuarios() {

        super("Mantenimiento de Usuarios", true, true, true, true);

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
        formulario.add(new JLabel("Usuario"), gbc);

        gbc.gridx = 1;
        txtUsuario = new JTextField(25);
        formulario.add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Nombre"), gbc);

        gbc.gridx = 1;
        txtNombre = new JTextField(25);
        formulario.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Apellido"), gbc);

        gbc.gridx = 1;
        txtApellido = new JTextField(25);
        formulario.add(txtApellido, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Cédula"), gbc);

        gbc.gridx = 1;
        txtCedula = new JTextField(25);
        formulario.add(txtCedula, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Teléfono"), gbc);

        gbc.gridx = 1;
        txtTelefono = new JTextField(25);
        formulario.add(txtTelefono, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Correo"), gbc);

        gbc.gridx = 1;
        txtCorreo = new JTextField(25);
        formulario.add(txtCorreo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Contraseña"), gbc);

        gbc.gridx = 1;
        txtContrasena = new javax.swing.JPasswordField(25);
        formulario.add(txtContrasena, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(new JLabel("Perfil"), gbc);

        gbc.gridx = 1;
        cmbPerfil = new javax.swing.JComboBox<>();
        cmbPerfil.setRenderer(new javax.swing.DefaultListCellRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof utu.modelo.Perfil p){ setText(p.getNombre()); }
                return this;
            }
        });
        formulario.add(cmbPerfil, gbc);

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
                "Usuario",
                "Nombre",
                "Apellido",
                "Cédula",
                "Teléfono",
                "Correo",
                "Perfil",
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

        cmbPerfil.removeAllItems();
        for(Perfil p : perfilDAO.listarActivas()){
            cmbPerfil.addItem(p);
        }
    }

    private void cargarTabla(){
        modeloTabla.setRowCount(0);

        List<Usuario> lista = usuarioDAO.listarTodos();
        for(Usuario u : lista){

            modeloTabla.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getUsuario(),
                    u.getNombre(),
                    u.getApellido(),
                    u.getCedula(),
                    u.getTelefono(),
                    u.getCorreo(),
                    u.getPerfil() == null ? "" : u.getPerfil().getNombre(),
                    u.isActivo()
            });
        }
    }

    private void limpiar(){
        txtId.setText("");
        txtUsuario.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtCedula.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        if(cmbPerfil.getItemCount() > 0){
            cmbPerfil.setSelectedIndex(0);
        }
        chkActivo.setSelected(true);
        txtUsuario.requestFocus();
    }

    private Usuario obtenerUsuario(){

        Usuario usuario = new Usuario();

        if(!txtId.getText().isBlank()){
            usuario.setIdUsuario(Integer.parseInt(txtId.getText()));
        }

        usuario.setUsuario(txtUsuario.getText().trim());
        usuario.setNombre(txtNombre.getText());
        usuario.setApellido(txtApellido.getText());
        usuario.setCedula(txtCedula.getText());
        usuario.setTelefono(txtTelefono.getText());
        usuario.setCorreo(txtCorreo.getText());

        // La contrasena solo se setea si se escribio algo.
        String contrasena = String.valueOf(txtContrasena.getPassword());
        if(!contrasena.isBlank()){
            usuario.setContrasena(contrasena);
        }

        usuario.setPerfil((Perfil) cmbPerfil.getSelectedItem());
        usuario.setActivo(chkActivo.isSelected());

        return usuario;
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

        if(txtUsuario.getText().isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese el usuario.");
            return;
        }

        if(txtNombre.getText().isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese el nombre.");
            return;
        }

        if(txtApellido.getText().isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese el apellido.");
            return;
        }

        if(cmbPerfil.getSelectedItem() == null){
            JOptionPane.showMessageDialog(this,
                    "Seleccione un perfil.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(txtId.getText().isBlank()){

            if(String.valueOf(txtContrasena.getPassword()).isBlank()){
                JOptionPane.showMessageDialog(this,
                        "Ingrese la contraseña.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if(usuarioDAO.existeUsuario(txtUsuario.getText().trim())){
                JOptionPane.showMessageDialog(this,
                        "Ya existe ese nombre de usuario.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if(usuarioDAO.insertar(obtenerUsuario())){

            JOptionPane.showMessageDialog(this, "Usuario registrado.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el usuario.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar(){

        if(txtId.getText().isBlank()) return;

        if(usuarioDAO.actualizar(obtenerUsuario())){

            JOptionPane.showMessageDialog(this, "Usuario modificado.");

            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo modificar el usuario.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar(){

        if(txtId.getText().isBlank()) return;

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el usuario?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if(opcion==JOptionPane.YES_OPTION){

            if(usuarioDAO.eliminar(Integer.parseInt(txtId.getText()))){
                JOptionPane.showMessageDialog(this, "Usuario eliminado.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar el usuario.",
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

        Usuario u = usuarioDAO.buscarPorId(Integer.parseInt(String.valueOf(idValor)));
        if (u == null) {
            return;
        }

        txtId.setText(String.valueOf(u.getIdUsuario()));
        txtUsuario.setText(u.getUsuario() == null ? "" : u.getUsuario());
        txtNombre.setText(u.getNombre() == null ? "" : u.getNombre());
        txtApellido.setText(u.getApellido() == null ? "" : u.getApellido());
        txtCedula.setText(u.getCedula() == null ? "" : u.getCedula());
        txtTelefono.setText(u.getTelefono() == null ? "" : u.getTelefono());
        txtCorreo.setText(u.getCorreo() == null ? "" : u.getCorreo());

        // La contraseña nunca se muestra al seleccionar: el campo queda vacío.
        txtContrasena.setText("");

        seleccionarPerfil(u.getPerfil() == null ? -1 : u.getPerfil().getIdPerfil());
        chkActivo.setSelected(u.isActivo());
    }

    private void seleccionarPerfil(int id){

        for(int i = 0; i < cmbPerfil.getItemCount(); i++){
            Object item = cmbPerfil.getItemAt(i);
            if(item instanceof utu.modelo.Perfil p && p.getIdPerfil() == id){
                cmbPerfil.setSelectedIndex(i);
                return;
            }
        }

        cmbPerfil.setSelectedIndex(-1);
    }
}
