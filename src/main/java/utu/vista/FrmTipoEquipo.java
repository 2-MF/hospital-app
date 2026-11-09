package utu.vista;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import utu.dao.TipoEquipoDAO;
import utu.modelo.TipoEquipo;

public class FrmTipoEquipo extends FrmBase {

    private final TipoEquipoDAO tipoEquipoDAO = new TipoEquipoDAO();
    private final JTextField txtNombre = campoTexto("Nombre");
    private final JTextField txtDescripcion = campoTexto("Descripción");
    private final JCheckBox chkActivo = campoActivo();

    public FrmTipoEquipo() {
        super("Mantenimiento de Tipos de Equipo", "tipo de equipo", false, 1000, 700,
                "ID", "Nombre", "Descripción", "Activo");
        iniciar();
    }

    @Override
    protected void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (TipoEquipo t : tipoEquipoDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{t.getIdTipo(), t.getNombre(), t.getDescripcion(), t.isActivo()});
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

    private TipoEquipo tipo() {
        TipoEquipo tipo = new TipoEquipo();
        tipo.setIdTipo(idActual());
        tipo.setNombre(txtNombre.getText());
        tipo.setDescripcion(txtDescripcion.getText());
        tipo.setActivo(chkActivo.isSelected());
        return tipo;
    }

    @Override
    protected String validar(boolean nuevo) {
        return txtNombre.getText().isBlank() ? "Ingrese el nombre." : null;
    }

    @Override
    protected boolean insertar() {
        return tipoEquipoDAO.insertar(tipo());
    }

    @Override
    protected boolean actualizar() {
        return tipoEquipoDAO.actualizar(tipo());
    }

    @Override
    protected boolean borrar(int id) {
        return tipoEquipoDAO.eliminar(id);
    }
}
