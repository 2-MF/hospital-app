package utu.vista;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import utu.dao.MarcaDAO;
import utu.modelo.Marca;

public class FrmMarca extends FrmBase {

    private final MarcaDAO marcaDAO = new MarcaDAO();
    private final JTextField txtNombre = campoTexto("Nombre");
    private final JTextField txtDescripcion = campoTexto("Descripción");
    private final JCheckBox chkActivo = campoActivo();

    public FrmMarca() {
        super("Mantenimiento de Marcas", "marca", true, 1000, 700, "ID", "Nombre", "Descripción", "Activo");
        iniciar();
    }

    @Override
    protected void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Marca m : marcaDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{m.getIdMarca(), m.getNombre(), m.getDescripcion(), m.isActivo()});
        }
    }

    @Override
    protected void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        chkActivo.setSelected(true);
    }

    @Override
    protected void mostrar(int fila) {
        txtNombre.setText(celda(fila, 1));
        txtDescripcion.setText(celda(fila, 2));
        chkActivo.setSelected(Boolean.TRUE.equals(modeloTabla.getValueAt(fila, 3)));
    }

    private Marca marca() {
        return new Marca(idActual(), txtNombre.getText(), txtDescripcion.getText(), chkActivo.isSelected());
    }

    @Override
    protected String validar(boolean nuevo) {
        if (txtNombre.getText().isBlank()) {
            return "Ingrese el nombre.";
        }
        if (nuevo && marcaDAO.existe(txtNombre.getText().trim())) {
            return "Ya existe una marca con ese nombre.";
        }
        return null;
    }

    @Override
    protected boolean insertar() {
        return marcaDAO.insertar(marca());
    }

    @Override
    protected boolean actualizar() {
        return marcaDAO.actualizar(marca());
    }

    @Override
    protected boolean borrar(int id) {
        return marcaDAO.eliminar(id);
    }
}
