package utu.vista;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import utu.dao.PerfilDAO;
import utu.dao.UsuarioDAO;
import utu.modelo.Perfil;
import utu.modelo.Usuario;

public class FrmUsuarios extends FrmBase {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final JTextField txtUsuario = campoTexto("Usuario");
    private final JTextField txtNombre = campoTexto("Nombre");
    private final JTextField txtApellido = campoTexto("Apellido");
    private final JTextField txtCedula = campoTexto("Cédula");
    private final JTextField txtTelefono = campoTexto("Teléfono");
    private final JTextField txtCorreo = campoTexto("Correo");
    private final JPasswordField txtContrasena = agregarCampo("Contraseña", new JPasswordField(25));
    private final JComboBox<Perfil> cmbPerfil = campoCombo("Perfil", Perfil::getNombre);
    private final JCheckBox chkActivo = campoActivo();

    public FrmUsuarios() {
        super("Mantenimiento de Usuarios", "usuario", false, 1100, 700,
                "ID", "Usuario", "Nombre", "Apellido", "Cédula", "Teléfono", "Correo", "Perfil", "Activo");
        llenar(cmbPerfil, new PerfilDAO().listarActivas());
        iniciar();
    }

    @Override
    protected void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Usuario u : usuarioDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{u.getIdUsuario(), u.getUsuario(), u.getNombre(), u.getApellido(),
                    u.getCedula(), u.getTelefono(), u.getCorreo(),
                    u.getPerfil() == null ? "" : u.getPerfil().getNombre(), u.isActivo()});
        }
    }

    @Override
    protected void limpiarCampos() {
        txtUsuario.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtCedula.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        seleccionarPrimero(cmbPerfil);
        chkActivo.setSelected(true);
    }

    @Override
    protected void mostrar(int fila) {
        Usuario u = usuarioDAO.buscarPorId(idActual());
        if (u == null) {
            return;
        }
        txtUsuario.setText(texto(u.getUsuario()));
        txtNombre.setText(texto(u.getNombre()));
        txtApellido.setText(texto(u.getApellido()));
        txtCedula.setText(texto(u.getCedula()));
        txtTelefono.setText(texto(u.getTelefono()));
        txtCorreo.setText(texto(u.getCorreo()));
        txtContrasena.setText("");
        int idPerfil = u.getPerfil() == null ? -1 : u.getPerfil().getIdPerfil();
        seleccionar(cmbPerfil, p -> p.getIdPerfil() == idPerfil);
        chkActivo.setSelected(u.isActivo());
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idActual());
        usuario.setUsuario(txtUsuario.getText().trim());
        usuario.setNombre(txtNombre.getText());
        usuario.setApellido(txtApellido.getText());
        usuario.setCedula(txtCedula.getText());
        usuario.setTelefono(txtTelefono.getText());
        usuario.setCorreo(txtCorreo.getText());
        String contrasena = String.valueOf(txtContrasena.getPassword());
        if (!contrasena.isBlank()) {
            usuario.setContrasena(contrasena);
        }
        usuario.setPerfil((Perfil) cmbPerfil.getSelectedItem());
        usuario.setActivo(chkActivo.isSelected());
        return usuario;
    }

    @Override
    protected String validar(boolean nuevo) {
        if (txtUsuario.getText().isBlank()) {
            return "Ingrese el usuario.";
        }
        if (txtNombre.getText().isBlank()) {
            return "Ingrese el nombre.";
        }
        if (txtApellido.getText().isBlank()) {
            return "Ingrese el apellido.";
        }
        if (cmbPerfil.getSelectedItem() == null) {
            return "Seleccione un perfil.";
        }
        if (nuevo && String.valueOf(txtContrasena.getPassword()).isBlank()) {
            return "Ingrese la contraseña.";
        }
        if (nuevo && usuarioDAO.existeUsuario(txtUsuario.getText().trim())) {
            return "Ya existe ese nombre de usuario.";
        }
        return null;
    }

    @Override
    protected boolean insertar() {
        return usuarioDAO.insertar(usuario());
    }

    @Override
    protected boolean actualizar() {
        return usuarioDAO.actualizar(usuario());
    }

    @Override
    protected boolean borrar(int id) {
        return usuarioDAO.eliminar(id);
    }
}
