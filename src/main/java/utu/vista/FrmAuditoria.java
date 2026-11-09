package utu.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import utu.dao.AuditoriaDAO;
import utu.modelo.Auditoria;

public class FrmAuditoria extends JInternalFrame {

    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();
    private final DefaultTableModel modeloTabla =
            Tablas.modeloSoloLectura("ID", "Fecha", "Tabla", "Operación", "Descripción", "Usuario");

    public FrmAuditoria() {
        super("Auditoría del Sistema", true, true, true, true);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        JButton btnActualizar = new JButton("Actualizar");
        JTextField txtBuscar = new JTextField(25);
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(btnActualizar);
        panelSuperior.add(new JLabel("Buscar"));
        panelSuperior.add(txtBuscar);
        add(panelSuperior, BorderLayout.NORTH);

        JTable tabla = new JTable(modeloTabla);
        Tablas.conBuscador(tabla, txtBuscar);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> cargarTabla());
        cargarTabla();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Auditoria a : auditoriaDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{a.getIdAuditoria(), a.getFecha() == null ? "" : a.getFecha().toString(),
                    a.getTablaAfectada(), a.getOperacion(), a.getDescripcion(),
                    a.getUsuario() == null ? "-" : a.getUsuario().getNombre() + " " + a.getUsuario().getApellido()});
        }
    }
}
