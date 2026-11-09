package utu.vista;

import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utu.dao.FuncionalidadDAO;
import utu.dao.PerfilDAO;
import utu.modelo.Funcionalidad;
import utu.modelo.Perfil;
import utu.util.Sesion;

public class FrmPerfiles extends FrmBase {

    private static final String FUNC_PERFILES = "Perfiles";

    private final PerfilDAO perfilDAO = new PerfilDAO();
    private final FuncionalidadDAO funcionalidadDAO = new FuncionalidadDAO();
    private final JTextField txtNombre = campoTexto("Nombre");
    private final JTextField txtDescripcion = campoTexto("Descripción");
    private final JCheckBox chkActivo = campoActivo();
    private final Map<Integer, JCheckBox> chkFuncionalidades = new LinkedHashMap<>();
    private final Map<Integer, String> nombresFuncionalidad = new LinkedHashMap<>();

    public FrmPerfiles() {
        super("Mantenimiento de Perfiles", "perfil", false, 1000, 700, "ID", "Nombre", "Descripción", "Activo");

        JPanel panelPermisos = new JPanel(new GridLayout(0, 3, 10, 2));
        panelPermisos.setBorder(BorderFactory.createTitledBorder(
                "Funcionalidades permitidas (se aplican al volver a iniciar sesión)"));
        for (Funcionalidad f : funcionalidadDAO.listarTodas()) {
            JCheckBox chk = new JCheckBox(f.getNombre());
            chk.setToolTipText(f.getDescripcion());
            chkFuncionalidades.put(f.getIdFuncionalidad(), chk);
            nombresFuncionalidad.put(f.getIdFuncionalidad(), f.getNombre());
            panelPermisos.add(chk);
        }
        colocarAlLado(panelPermisos);
        iniciar();
    }

    @Override
    protected void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Perfil p : perfilDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{p.getIdPerfil(), p.getNombre(), p.getDescripcion(), p.isActivo()});
        }
    }

    @Override
    protected void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        chkActivo.setSelected(true);
        marcarFuncionalidades(Set.of());
    }

    @Override
    protected void mostrar(int fila) {
        txtNombre.setText(celda(fila, 1));
        txtDescripcion.setText(celda(fila, 2));
        chkActivo.setSelected(Boolean.TRUE.equals(modeloTabla.getValueAt(fila, 3)));
        marcarFuncionalidades(funcionalidadDAO.idsPorPerfil(idActual()));
    }

    private void marcarFuncionalidades(Set<Integer> ids) {
        chkFuncionalidades.forEach((id, chk) -> chk.setSelected(ids.contains(id)));
    }

    private List<Integer> funcionalidadesMarcadas() {
        return chkFuncionalidades.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .toList();
    }

    private boolean esPerfilPropio(int idPerfil) {
        return Sesion.getUsuario() != null && Sesion.getUsuario().getPerfil() != null
                && Sesion.getUsuario().getPerfil().getIdPerfil() == idPerfil;
    }

    private boolean dejariaSinAcceso() {
        return !chkActivo.isSelected() || funcionalidadesMarcadas().stream()
                .noneMatch(id -> FUNC_PERFILES.equals(nombresFuncionalidad.get(id)));
    }

    private Perfil perfil() {
        Perfil perfil = new Perfil();
        perfil.setIdPerfil(idActual());
        perfil.setNombre(txtNombre.getText().trim());
        perfil.setDescripcion(txtDescripcion.getText());
        perfil.setActivo(chkActivo.isSelected());
        return perfil;
    }

    @Override
    protected String validar(boolean nuevo) {
        if (txtNombre.getText().isBlank()) {
            return "Ingrese el nombre.";
        }
        String nombre = txtNombre.getText().trim();
        if (nuevo && perfilDAO.existe(nombre)) {
            return "Ya existe un perfil con ese nombre.";
        }
        if (!nuevo && perfilDAO.existe(nombre, idActual())) {
            return "Ya existe otro perfil con ese nombre.";
        }
        if (!nuevo && esPerfilPropio(idActual()) && dejariaSinAcceso()) {
            return "No puede desactivar su propio perfil ni quitarle la funcionalidad \""
                    + FUNC_PERFILES + "\":\nse quedaría sin acceso a esta pantalla.";
        }
        return null;
    }

    @Override
    protected String bloqueoEliminar(int id) {
        return esPerfilPropio(id) ? "No puede eliminar el perfil con el que inició sesión." : null;
    }

    @Override
    protected boolean insertar() {
        Perfil perfil = perfil();
        if (!perfilDAO.insertar(perfil)) {
            return false;
        }
        if (!funcionalidadDAO.asignar(perfil.getIdPerfil(), funcionalidadesMarcadas())) {
            aviso("El perfil se registró, pero no se pudieron guardar sus funcionalidades.\n"
                    + "Selecciónelo y use \"Modificar\" para intentarlo de nuevo.");
        }
        return true;
    }

    @Override
    protected boolean actualizar() {
        Perfil perfil = perfil();
        if (!perfilDAO.actualizar(perfil)) {
            return false;
        }
        if (!funcionalidadDAO.asignar(perfil.getIdPerfil(), funcionalidadesMarcadas())) {
            aviso("Se guardaron los datos del perfil, pero no sus funcionalidades.");
        }
        return true;
    }

    @Override
    protected boolean borrar(int id) {
        return perfilDAO.eliminar(id);
    }
}
