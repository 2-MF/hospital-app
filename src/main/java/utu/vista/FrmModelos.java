package utu.vista;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import utu.dao.MarcaDAO;
import utu.dao.ModeloDAO;
import utu.modelo.Marca;
import utu.modelo.Modelo;

public class FrmModelos extends FrmBase {

    private final ModeloDAO modeloDAO = new ModeloDAO();
    private final JTextField txtNombre = campoTexto("Nombre");
    private final JComboBox<Marca> cmbMarca = campoCombo("Marca", Marca::getNombre);
    private final JCheckBox chkActivo = campoActivo();

    public FrmModelos() {
        super("Mantenimiento de Modelos", "modelo", false, 1000, 700, "ID", "Nombre", "Marca", "Activo");
        llenar(cmbMarca, new MarcaDAO().listarActivas());
        iniciar();
    }

    @Override
    protected void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Modelo m : modeloDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{m.getIdModelo(), m.getNombre(),
                    m.getMarca() == null ? "" : m.getMarca().getNombre(), m.isActivo()});
        }
    }

    @Override
    protected void limpiarCampos() {
        txtNombre.setText("");
        cmbMarca.setSelectedIndex(-1);
        chkActivo.setSelected(true);
    }

    @Override
    protected void mostrar(int fila) {
        Modelo modelo = modeloDAO.buscarPorId(idActual());
        if (modelo == null) {
            return;
        }
        txtNombre.setText(texto(modelo.getNombre()));
        chkActivo.setSelected(modelo.isActivo());
        int idMarca = modelo.getMarca() == null ? -1 : modelo.getMarca().getIdMarca();
        seleccionar(cmbMarca, m -> m.getIdMarca() == idMarca);
    }

    private Modelo modelo() {
        Modelo modelo = new Modelo();
        modelo.setIdModelo(idActual());
        modelo.setNombre(txtNombre.getText());
        modelo.setMarca((Marca) cmbMarca.getSelectedItem());
        modelo.setActivo(chkActivo.isSelected());
        return modelo;
    }

    @Override
    protected String validar(boolean nuevo) {
        if (txtNombre.getText().isBlank()) {
            return "Ingrese el nombre.";
        }
        return cmbMarca.getSelectedItem() == null ? "Seleccione marca." : null;
    }

    @Override
    protected boolean insertar() {
        return modeloDAO.insertar(modelo());
    }

    @Override
    protected boolean actualizar() {
        return modeloDAO.actualizar(modelo());
    }

    @Override
    protected boolean borrar(int id) {
        return modeloDAO.eliminar(id);
    }
}
