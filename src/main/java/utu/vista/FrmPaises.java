package utu.vista;

import javax.swing.JTextField;

import utu.dao.PaisDAO;
import utu.modelo.Pais;

public class FrmPaises extends FrmBase {

    private final PaisDAO paisDAO = new PaisDAO();
    private final JTextField txtNombre = campoTexto("Nombre");

    public FrmPaises() {
        super("Mantenimiento de Países", "país", false, 700, 550, "ID", "Nombre");
        iniciar();
    }

    @Override
    protected void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Pais p : paisDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{p.getIdPais(), p.getNombre()});
        }
    }

    @Override
    protected void limpiarCampos() {
        txtNombre.setText("");
    }

    @Override
    protected void mostrar(int fila) {
        txtNombre.setText(celda(fila, 1));
    }

    @Override
    protected String validar(boolean nuevo) {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            return "Ingrese el nombre.";
        }
        if (nombre.length() > 80) {
            return "El nombre no puede superar los 80 caracteres.";
        }
        if (paisDAO.existe(nombre, idActual())) {
            return "Ya existe un país con ese nombre.";
        }
        return null;
    }

    @Override
    protected String bloqueoEliminar(int id) {
        return paisDAO.tieneInstituciones(id)
                ? "No se puede eliminar: hay instituciones registradas en este país."
                : null;
    }

    @Override
    protected String preguntaEliminar() {
        return "¿Eliminar el país \"" + txtNombre.getText().trim() + "\"?";
    }

    @Override
    protected boolean insertar() {
        return paisDAO.insertar(new Pais(0, txtNombre.getText().trim()));
    }

    @Override
    protected boolean actualizar() {
        return paisDAO.actualizar(new Pais(idActual(), txtNombre.getText().trim()));
    }

    @Override
    protected boolean borrar(int id) {
        return paisDAO.eliminar(id);
    }
}
