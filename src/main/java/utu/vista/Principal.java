package utu.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.*;

import utu.dao.FuncionalidadDAO;
import utu.modelo.Usuario;

public class Principal extends JFrame {

    private final Usuario usuarioLogeado;
    private final JDesktopPane escritorio = new JDesktopPane();

    public Principal(Usuario usuario) {
        this.usuarioLogeado = usuario;
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        setTitle("Sistema de Gestión Hospitalaria");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(escritorio, BorderLayout.CENTER);
        crearMenu();
        crearBarraEstado();
        setVisible(true);
    }

    private void crearMenu() {

        // Funcionalidades que tiene asignadas el perfil del usuario
        List<String> permisos = new FuncionalidadDAO()
                .nombresPorPerfil(usuarioLogeado.getPerfil().getIdPerfil());

        JMenuBar barra = new JMenuBar();

        JMenu archivo = new JMenu("Archivo");
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> System.exit(0));
        archivo.add(salir);
        barra.add(archivo);

        JMenu seguridad = new JMenu("Seguridad");
        agregarItem(seguridad, permisos, "Usuarios");
        agregarItem(seguridad, permisos, "Perfiles");
        agregarItem(seguridad, permisos, "Auditoría");
        if (seguridad.getMenuComponentCount() > 0) {
            barra.add(seguridad);
        }

        JMenu mantenimiento = new JMenu("Mantenimiento");
        agregarItem(mantenimiento, permisos, "Marcas");
        agregarItem(mantenimiento, permisos, "Modelos");
        agregarItem(mantenimiento, permisos, "Proveedores");
        agregarItem(mantenimiento, permisos, "Equipos");
        mantenimiento.addSeparator();
        agregarItem(mantenimiento, permisos, "Instituciones");
        agregarItem(mantenimiento, permisos, "Ubicaciones");
        mantenimiento.addSeparator();
        agregarItem(mantenimiento, permisos, "Tipos de Equipo");
        agregarItem(mantenimiento, permisos, "Tipos de Intervención");
        if (mantenimiento.getMenuComponentCount() > 0) {
            barra.add(mantenimiento);
        }

        JMenu procesos = new JMenu("Procesos");
        agregarItem(procesos, permisos, "Intervenciones");
        if (procesos.getMenuComponentCount() > 0) {
            barra.add(procesos);
        }

        JMenu ayuda = new JMenu("Ayuda");
        JMenuItem acercaDe = new JMenuItem("Acerca de");
        acercaDe.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Sistema de Gestión Hospitalaria\nGestión de equipamiento biomédico\n\nJava 21 + Swing + MySQL",
                "Acerca de",
                JOptionPane.INFORMATION_MESSAGE));
        ayuda.add(acercaDe);
        barra.add(ayuda);

        setJMenuBar(barra);
    }

    /** Agrega un ítem de menú solo si el perfil tiene esa funcionalidad. */
    private void agregarItem(JMenu menu, List<String> permisos, String funcionalidad) {
        if (!permisos.contains(funcionalidad)) {
            return;
        }
        JMenuItem item = new JMenuItem(funcionalidad);
        item.addActionListener(e -> abrirFormularioSeguro(crearFormulario(funcionalidad)));
        menu.add(item);
    }

    private JInternalFrame crearFormulario(String funcionalidad) {
        return switch (funcionalidad) {
            case "Usuarios" -> new FrmUsuarios();
            case "Perfiles" -> new FrmPerfiles();
            case "Auditoría" -> new FrmAuditoria();
            case "Marcas" -> new FrmMarca();
            case "Modelos" -> new FrmModelos();
            case "Proveedores" -> new FrmProveedores();
            case "Equipos" -> new FrmEquipos();
            case "Instituciones" -> new FrmInstituciones();
            case "Ubicaciones" -> new FrmUbicaciones();
            case "Tipos de Equipo" -> new FrmTipoEquipo();
            case "Tipos de Intervención" -> new FrmTipoIntervencion();
            default -> new FrmIntervenciones();
        };
    }

    private void crearBarraEstado() {
        JPanel estado = new JPanel(new BorderLayout());
        estado.setPreferredSize(new Dimension(100, 30));

        JLabel lblUsuario = new JLabel("Usuario: " + usuarioLogeado.getNombre() + " " + usuarioLogeado.getApellido());
        JLabel lblPerfil = new JLabel("Perfil: " + usuarioLogeado.getPerfil().getNombre() + " ");

        estado.add(lblUsuario, BorderLayout.WEST);
        estado.add(lblPerfil, BorderLayout.EAST);
        add(estado, BorderLayout.SOUTH);
    }

    /** Abre un formulario interno controlando errores de conexión a la BD. */
    private void abrirFormularioSeguro(JInternalFrame formulario) {
        try {
            for (JInternalFrame ventana : escritorio.getAllFrames()) {
                if (ventana.getClass().equals(formulario.getClass())) {
                    ventana.setSelected(true);
                    ventana.toFront();
                    return;
                }
            }
            escritorio.add(formulario);
            formulario.setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo abrir la ventana.\nVerifique la conexión con la base de datos.\n\nDetalle: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
