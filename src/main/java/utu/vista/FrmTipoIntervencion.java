package utu.vista;

import javax.swing.JTextField;

import utu.dao.TipoIntervencionDAO;
import utu.modelo.TipoIntervencion;

public class FrmTipoIntervencion extends FrmBase {

    private final TipoIntervencionDAO tipoIntervencionDAO = new TipoIntervencionDAO();
    private final JTextField txtNombre = campoTexto("Nombre");
    private final JTextField txtDescripcion = campoTexto("Descripción");

    public FrmTipoIntervencion() {
        super("Mantenimiento de Tipos de Intervención", "tipo de intervención", false, 1000, 700,
                "ID", "Nombre", "Descripción");
        iniciar();
    }

    @Override
    protected void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (TipoIntervencion t : tipoIntervencionDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{t.getIdTipoIntervencion(), t.getNombre(), t.getDescripcion()});
        }
    }

    @Override
    protected void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
    }

    @Override
    protected void mostrar(int fila) {
        txtNombre.setText(celda(fila, 1));
        txtDescripcion.setText(celda(fila, 2));
    }

    private TipoIntervencion tipo() {
        TipoIntervencion tipo = new TipoIntervencion();
        tipo.setIdTipoIntervencion(idActual());
        tipo.setNombre(txtNombre.getText());
        tipo.setDescripcion(txtDescripcion.getText());
        return tipo;
    }

    @Override
    protected String validar(boolean nuevo) {
        if (txtNombre.getText().isBlank()) {
            return "Ingrese el nombre.";
        }
        if (nuevo && tipoIntervencionDAO.existe(txtNombre.getText().trim())) {
            return "Ya existe un tipo de intervención con ese nombre.";
        }
        return null;
    }

    @Override
    protected String preguntaEliminar() {
        return "¿Eliminar el tipo de intervención? Esta acción no se puede deshacer.";
    }

    @Override
    protected boolean insertar() {
        return tipoIntervencionDAO.insertar(tipo());
    }

    @Override
    protected boolean actualizar() {
        return tipoIntervencionDAO.actualizar(tipo());
    }

    @Override
    protected boolean borrar(int id) {
        return tipoIntervencionDAO.eliminar(id);
    }
}
