package utu.vista;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import utu.dao.ProveedorDAO;
import utu.modelo.Proveedor;

public class FrmProveedores extends FrmBase {

    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final JTextField txtNombre = campoTexto("Nombre");
    private final JTextField txtDireccion = campoTexto("Dirección");
    private final JTextField txtTelefono = campoTexto("Teléfono");
    private final JTextField txtEmail = campoTexto("Email");
    private final JCheckBox chkActivo = campoActivo();

    public FrmProveedores() {
        super("Mantenimiento de Proveedores", "proveedor", false, 1000, 700,
                "ID", "Nombre", "Dirección", "Teléfono", "Email", "Activo");
        iniciar();
    }

    @Override
    protected void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Proveedor p : proveedorDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{p.getIdProveedor(), p.getNombre(), p.getDireccion(), p.getTelefono(),
                    p.getEmail(), p.isActivo()});
        }
    }

    @Override
    protected void limpiarCampos() {
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        chkActivo.setSelected(true);
    }

    @Override
    protected void mostrar(int fila) {
        txtNombre.setText(celda(fila, 1));
        txtDireccion.setText(celda(fila, 2));
        txtTelefono.setText(celda(fila, 3));
        txtEmail.setText(celda(fila, 4));
        chkActivo.setSelected(Boolean.TRUE.equals(modeloTabla.getValueAt(fila, 5)));
    }

    private Proveedor proveedor() {
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(idActual());
        proveedor.setNombre(txtNombre.getText());
        proveedor.setDireccion(txtDireccion.getText());
        proveedor.setTelefono(txtTelefono.getText());
        proveedor.setEmail(txtEmail.getText());
        proveedor.setActivo(chkActivo.isSelected());
        return proveedor;
    }

    @Override
    protected String validar(boolean nuevo) {
        return txtNombre.getText().isBlank() ? "Ingrese el nombre." : null;
    }

    @Override
    protected boolean insertar() {
        return proveedorDAO.insertar(proveedor());
    }

    @Override
    protected boolean actualizar() {
        return proveedorDAO.actualizar(proveedor());
    }

    @Override
    protected boolean borrar(int id) {
        return proveedorDAO.eliminar(id);
    }
}
