package utu.vista;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import utu.dao.InstitucionDAO;
import utu.dao.PaisDAO;
import utu.modelo.Institucion;
import utu.modelo.Pais;

public class FrmInstituciones extends FrmBase {

    private final InstitucionDAO institucionDAO = new InstitucionDAO();
    private final JTextField txtNombre = campoTexto("Nombre");
    private final JTextField txtDireccion = campoTexto("Dirección");
    private final JTextField txtTelefono = campoTexto("Teléfono");
    private final JComboBox<Pais> cmbPais = campoCombo("País", Pais::getNombre);
    private final JCheckBox chkActivo = campoActivo();

    public FrmInstituciones() {
        super("Mantenimiento de Instituciones", "institución", true, 1000, 700,
                "ID", "Nombre", "Dirección", "Teléfono", "País", "Activo");
        llenar(cmbPais, new PaisDAO().listarTodos());
        iniciar();
    }

    @Override
    protected void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Institucion i : institucionDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{i.getIdInstitucion(), i.getNombre(), i.getDireccion(), i.getTelefono(),
                    i.getPais() == null ? "" : i.getPais().getNombre(), i.isActivo()});
        }
    }

    @Override
    protected void limpiarCampos() {
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        cmbPais.setSelectedIndex(-1);
        chkActivo.setSelected(true);
    }

    @Override
    protected void mostrar(int fila) {
        Institucion institucion = institucionDAO.buscarPorId(idActual());
        if (institucion == null) {
            return;
        }
        txtNombre.setText(texto(institucion.getNombre()));
        txtDireccion.setText(texto(institucion.getDireccion()));
        txtTelefono.setText(texto(institucion.getTelefono()));
        chkActivo.setSelected(institucion.isActivo());
        int idPais = institucion.getPais() == null ? -1 : institucion.getPais().getIdPais();
        seleccionar(cmbPais, p -> p.getIdPais() == idPais);
    }

    private Institucion institucion() {
        Institucion institucion = new Institucion();
        institucion.setIdInstitucion(idActual());
        institucion.setNombre(txtNombre.getText());
        institucion.setDireccion(txtDireccion.getText());
        institucion.setTelefono(txtTelefono.getText());
        institucion.setPais((Pais) cmbPais.getSelectedItem());
        institucion.setActivo(chkActivo.isSelected());
        return institucion;
    }

    @Override
    protected String validar(boolean nuevo) {
        if (txtNombre.getText().isBlank()) {
            return "Ingrese el nombre.";
        }
        return cmbPais.getSelectedItem() == null ? "Seleccione país." : null;
    }

    @Override
    protected boolean insertar() {
        return institucionDAO.insertar(institucion());
    }

    @Override
    protected boolean actualizar() {
        return institucionDAO.actualizar(institucion());
    }

    @Override
    protected boolean borrar(int id) {
        return institucionDAO.eliminar(id);
    }
}
