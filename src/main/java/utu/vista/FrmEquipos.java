package utu.vista;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import utu.dao.EquipoDAO;
import utu.dao.ModeloDAO;
import utu.dao.ProveedorDAO;
import utu.dao.TipoEquipoDAO;
import utu.dao.UbicacionDAO;
import utu.modelo.Equipo;
import utu.modelo.Modelo;
import utu.modelo.Proveedor;
import utu.modelo.TipoEquipo;
import utu.modelo.Ubicacion;

public class FrmEquipos extends FrmBase {

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final JTextField txtCodigo = campoTexto("Código");
    private final JTextField txtNombre = campoTexto("Nombre");
    private final JTextField txtSerie = campoTexto("Serie");
    private final JTextField txtFechaCompra = campoTexto("Fecha Compra", 12);
    private final JTextField txtFechaGarantia = campoTexto("Fecha Garantía", 12);
    private final JComboBox<String> cmbEstado =
            agregarCampo("Estado", new JComboBox<>(new String[]{"ACTIVO", "INACTIVO", "MANTENIMIENTO"}));
    private final JTextField txtObservaciones = campoTexto("Observaciones");
    private final JComboBox<Modelo> cmbModelo = campoCombo("Modelo", Modelo::getNombre);
    private final JComboBox<TipoEquipo> cmbTipo = campoCombo("Tipo", TipoEquipo::getNombre);
    private final JComboBox<Proveedor> cmbProveedor = campoCombo("Proveedor", Proveedor::getNombre);
    private final JComboBox<Ubicacion> cmbUbicacion = campoCombo("Ubicación", Ubicacion::getNombre);

    public FrmEquipos() {
        super("Mantenimiento de Equipos", "equipo", false, 1200, 750,
                "ID", "Código", "Nombre", "Serie", "Estado", "Modelo", "Tipo", "Proveedor", "Ubicación");
        llenar(cmbModelo, new ModeloDAO().listarActivas());
        llenar(cmbTipo, new TipoEquipoDAO().listarActivas());
        llenar(cmbProveedor, new ProveedorDAO().listarActivas());
        llenar(cmbUbicacion, new UbicacionDAO().listarTodos());
        iniciar();
    }

    @Override
    protected void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Equipo e : equipoDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{e.getIdEquipo(), e.getCodigo(), e.getNombre(), e.getSerie(), e.getEstado(),
                    e.getModelo() == null ? "" : e.getModelo().getNombre(),
                    e.getTipo() == null ? "" : e.getTipo().getNombre(),
                    e.getProveedor() == null ? "" : e.getProveedor().getNombre(),
                    e.getUbicacion() == null ? "" : e.getUbicacion().getNombre()});
        }
    }

    @Override
    protected void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtSerie.setText("");
        txtFechaCompra.setText("");
        txtFechaGarantia.setText("");
        txtObservaciones.setText("");
        seleccionarPrimero(cmbEstado);
        seleccionarPrimero(cmbModelo);
        seleccionarPrimero(cmbTipo);
        seleccionarPrimero(cmbProveedor);
        seleccionarPrimero(cmbUbicacion);
    }

    @Override
    protected void mostrar(int fila) {
        Equipo e = equipoDAO.buscarPorId(idActual());
        if (e == null) {
            return;
        }
        txtCodigo.setText(texto(e.getCodigo()));
        txtNombre.setText(texto(e.getNombre()));
        txtSerie.setText(texto(e.getSerie()));
        txtFechaCompra.setText(texto(e.getFechaCompra()));
        txtFechaGarantia.setText(texto(e.getFechaGarantia()));
        if (e.getEstado() != null) {
            cmbEstado.setSelectedItem(e.getEstado());
        }
        txtObservaciones.setText(texto(e.getObservaciones()));
        int idModelo = e.getModelo() == null ? -1 : e.getModelo().getIdModelo();
        int idTipo = e.getTipo() == null ? -1 : e.getTipo().getIdTipo();
        int idProveedor = e.getProveedor() == null ? -1 : e.getProveedor().getIdProveedor();
        int idUbicacion = e.getUbicacion() == null ? -1 : e.getUbicacion().getIdUbicacion();
        seleccionar(cmbModelo, m -> m.getIdModelo() == idModelo);
        seleccionar(cmbTipo, t -> t.getIdTipo() == idTipo);
        seleccionar(cmbProveedor, p -> p.getIdProveedor() == idProveedor);
        seleccionar(cmbUbicacion, u -> u.getIdUbicacion() == idUbicacion);
    }

    private Equipo equipo() {
        Equipo equipo = new Equipo();
        equipo.setIdEquipo(idActual());
        equipo.setCodigo(txtCodigo.getText());
        equipo.setNombre(txtNombre.getText());
        equipo.setSerie(txtSerie.getText());
        equipo.setFechaCompra(fecha(txtFechaCompra));
        equipo.setFechaGarantia(fecha(txtFechaGarantia));
        equipo.setEstado((String) cmbEstado.getSelectedItem());
        equipo.setObservaciones(txtObservaciones.getText());
        equipo.setModelo((Modelo) cmbModelo.getSelectedItem());
        equipo.setTipo((TipoEquipo) cmbTipo.getSelectedItem());
        equipo.setProveedor((Proveedor) cmbProveedor.getSelectedItem());
        equipo.setUbicacion((Ubicacion) cmbUbicacion.getSelectedItem());
        return equipo;
    }

    @Override
    protected String validar(boolean nuevo) {
        if (txtCodigo.getText().isBlank()) {
            return "Ingrese el código.";
        }
        if (txtNombre.getText().isBlank()) {
            return "Ingrese el nombre.";
        }
        if (cmbModelo.getSelectedItem() == null) {
            return "Seleccione un modelo.";
        }
        if (cmbTipo.getSelectedItem() == null) {
            return "Seleccione un tipo.";
        }
        if (cmbProveedor.getSelectedItem() == null) {
            return "Seleccione un proveedor.";
        }
        if (cmbUbicacion.getSelectedItem() == null) {
            return "Seleccione una ubicación.";
        }
        if (fechaInvalida(txtFechaCompra)) {
            return "Fecha de compra inválida (formato yyyy-MM-dd).";
        }
        if (fechaInvalida(txtFechaGarantia)) {
            return "Fecha de garantía inválida (formato yyyy-MM-dd).";
        }
        if (nuevo && equipoDAO.existeCodigo(txtCodigo.getText().trim())) {
            return "Ya existe un equipo con ese código.";
        }
        return null;
    }

    @Override
    protected String preguntaEliminar() {
        return "¿Dar de baja el equipo? Su estado pasará a INACTIVO.";
    }

    @Override
    protected boolean insertar() {
        return equipoDAO.insertar(equipo());
    }

    @Override
    protected boolean actualizar() {
        return equipoDAO.actualizar(equipo());
    }

    @Override
    protected boolean borrar(int id) {
        return equipoDAO.eliminar(id);
    }
}
