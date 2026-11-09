package utu.vista;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import utu.dao.InstitucionDAO;
import utu.dao.UbicacionDAO;
import utu.modelo.Institucion;
import utu.modelo.Ubicacion;

public class FrmUbicaciones extends FrmBase {

    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();
    private final JTextField txtNombre = campoTexto("Nombre");
    private final JTextField txtDescripcion = campoTexto("Descripción");
    private final JComboBox<Institucion> cmbInstitucion = campoCombo("Institución", Institucion::getNombre);

    public FrmUbicaciones() {
        super("Mantenimiento de Ubicaciones", "ubicación", true, 1000, 700,
                "ID", "Nombre", "Descripción", "Institución");
        llenar(cmbInstitucion, new InstitucionDAO().listarActivas());
        iniciar();
    }

    @Override
    protected void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Ubicacion u : ubicacionDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{u.getIdUbicacion(), u.getNombre(), u.getDescripcion(),
                    u.getInstitucion() == null ? "" : u.getInstitucion().getNombre()});
        }
    }

    @Override
    protected void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        cmbInstitucion.setSelectedIndex(-1);
    }

    @Override
    protected void mostrar(int fila) {
        Ubicacion ubicacion = ubicacionDAO.buscarPorId(idActual());
        if (ubicacion == null) {
            return;
        }
        txtNombre.setText(texto(ubicacion.getNombre()));
        txtDescripcion.setText(texto(ubicacion.getDescripcion()));
        int idInstitucion = ubicacion.getInstitucion() == null ? -1 : ubicacion.getInstitucion().getIdInstitucion();
        seleccionar(cmbInstitucion, i -> i.getIdInstitucion() == idInstitucion);
    }

    private Ubicacion ubicacion() {
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setIdUbicacion(idActual());
        ubicacion.setNombre(txtNombre.getText());
        ubicacion.setDescripcion(txtDescripcion.getText());
        ubicacion.setInstitucion((Institucion) cmbInstitucion.getSelectedItem());
        return ubicacion;
    }

    @Override
    protected String validar(boolean nuevo) {
        if (txtNombre.getText().isBlank()) {
            return "Ingrese el nombre.";
        }
        return cmbInstitucion.getSelectedItem() == null ? "Seleccione institución." : null;
    }

    @Override
    protected String preguntaEliminar() {
        return "¿Eliminar la ubicación? Esta acción no se puede deshacer.";
    }

    @Override
    protected boolean insertar() {
        return ubicacionDAO.insertar(ubicacion());
    }

    @Override
    protected boolean actualizar() {
        return ubicacionDAO.actualizar(ubicacion());
    }

    @Override
    protected boolean borrar(int id) {
        return ubicacionDAO.eliminar(id);
    }
}
