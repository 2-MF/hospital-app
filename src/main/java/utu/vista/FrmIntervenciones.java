package utu.vista;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import utu.dao.EquipoDAO;
import utu.dao.IntervencionDAO;
import utu.dao.TipoIntervencionDAO;
import utu.dao.UsuarioDAO;
import utu.modelo.Equipo;
import utu.modelo.Intervencion;
import utu.modelo.TipoIntervencion;
import utu.modelo.Usuario;

public class FrmIntervenciones extends FrmBase {

    private final IntervencionDAO intervencionDAO = new IntervencionDAO();
    private final JTextField txtFecha = campoTexto("Fecha (yyyy-MM-dd)", 12);
    private final JComboBox<Equipo> cmbEquipo =
            campoCombo("Equipo", e -> e.getCodigo() + " - " + e.getNombre());
    private final JComboBox<TipoIntervencion> cmbTipo = campoCombo("Tipo Intervención", TipoIntervencion::getNombre);
    private final JComboBox<Usuario> cmbUsuario =
            campoCombo("Usuario", u -> u.getNombre() + " " + u.getApellido());
    private final JTextField txtCosto = campoTexto("Costo", 12);
    private final JTextField txtDescripcion = campoTexto("Descripción");

    public FrmIntervenciones() {
        super("Registro de Intervenciones", "intervención", true, 1100, 700,
                "ID", "Fecha", "Equipo", "Tipo Intervención", "Usuario", "Costo");
        llenar(cmbEquipo, new EquipoDAO().listarTodos());
        llenar(cmbTipo, new TipoIntervencionDAO().listarTodos());
        llenar(cmbUsuario, new UsuarioDAO().listarTodos());
        iniciar();
    }

    @Override
    protected void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Intervencion i : intervencionDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{i.getIdIntervencion(), texto(i.getFecha()),
                    i.getEquipo() == null ? "" : i.getEquipo().getNombre(),
                    i.getTipoIntervencion() == null ? "" : i.getTipoIntervencion().getNombre(),
                    i.getUsuario() == null ? "" : i.getUsuario().getNombre() + " " + i.getUsuario().getApellido(),
                    i.getCosto()});
        }
    }

    @Override
    protected void limpiarCampos() {
        txtFecha.setText("");
        txtCosto.setText("");
        txtDescripcion.setText("");
        seleccionarPrimero(cmbEquipo);
        seleccionarPrimero(cmbTipo);
        seleccionarPrimero(cmbUsuario);
    }

    @Override
    protected void mostrar(int fila) {
        Intervencion i = intervencionDAO.buscarPorId(idActual());
        if (i == null) {
            return;
        }
        txtFecha.setText(texto(i.getFecha()));
        txtCosto.setText(texto(i.getCosto()));
        txtDescripcion.setText(texto(i.getDescripcion()));
        int idEquipo = i.getEquipo() == null ? -1 : i.getEquipo().getIdEquipo();
        int idTipo = i.getTipoIntervencion() == null ? -1 : i.getTipoIntervencion().getIdTipoIntervencion();
        int idUsuario = i.getUsuario() == null ? -1 : i.getUsuario().getIdUsuario();
        seleccionar(cmbEquipo, e -> e.getIdEquipo() == idEquipo);
        seleccionar(cmbTipo, t -> t.getIdTipoIntervencion() == idTipo);
        seleccionar(cmbUsuario, u -> u.getIdUsuario() == idUsuario);
    }

    private Intervencion intervencion() {
        Intervencion intervencion = new Intervencion();
        intervencion.setIdIntervencion(idActual());
        intervencion.setFecha(fecha(txtFecha));
        intervencion.setCosto(decimal(txtCosto));
        intervencion.setEquipo((Equipo) cmbEquipo.getSelectedItem());
        intervencion.setTipoIntervencion((TipoIntervencion) cmbTipo.getSelectedItem());
        intervencion.setUsuario((Usuario) cmbUsuario.getSelectedItem());
        intervencion.setDescripcion(txtDescripcion.getText());
        return intervencion;
    }

    @Override
    protected String validar(boolean nuevo) {
        if (cmbEquipo.getSelectedItem() == null) {
            return "Seleccione un equipo.";
        }
        if (cmbTipo.getSelectedItem() == null) {
            return "Seleccione el tipo de intervención.";
        }
        if (cmbUsuario.getSelectedItem() == null) {
            return "Seleccione un usuario.";
        }
        if (txtFecha.getText().isBlank()) {
            return "Ingrese la fecha.";
        }
        if (fechaInvalida(txtFecha)) {
            return "Fecha inválida (formato yyyy-MM-dd).";
        }
        if (txtCosto.getText().isBlank()) {
            return "Ingrese el costo.";
        }
        return decimal(txtCosto) == null ? "Costo inválido." : null;
    }

    @Override
    protected String preguntaEliminar() {
        return "¿Eliminar la intervención? Esta acción no se puede deshacer.";
    }

    @Override
    protected boolean insertar() {
        return intervencionDAO.insertar(intervencion());
    }

    @Override
    protected boolean actualizar() {
        return intervencionDAO.actualizar(intervencion());
    }

    @Override
    protected boolean borrar(int id) {
        return intervencionDAO.eliminar(id);
    }
}
