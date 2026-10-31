package utu.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;

import utu.modelo.*;
import utu.dao.*;

public class FrmAuditoria extends JInternalFrame {

    private JTextField txtBuscar;
    private JButton btnActualizar;

    private JTable tabla;

    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();
    private DefaultTableModel modeloTabla;
    private javax.swing.table.TableRowSorter<TableModel> ordenador;

    public FrmAuditoria() {

        super("Auditoría del Sistema", true, true, true, true);

        iniciarComponentes();

        modeloTabla = (DefaultTableModel) tabla.getModel();

        ordenador = new javax.swing.table.TableRowSorter<>(tabla.getModel());
        tabla.setRowSorter(ordenador);

        cargarTabla();
        eventos();

    }

    private void iniciarComponentes() {

        setSize(1000, 700);
        setLayout(new BorderLayout());

        // ==========================
        // PANEL SUPERIOR
        // ==========================
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnActualizar = new JButton("Actualizar");
        panelSuperior.add(btnActualizar);

        panelSuperior.add(new JLabel("Buscar"));

        txtBuscar = new JTextField(25);
        panelSuperior.add(txtBuscar);

        add(panelSuperior, BorderLayout.NORTH);

        // ==========================
        // TABLA (solo lectura)
        // ==========================
        tabla = new JTable();

        tabla.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "ID",
                "Fecha",
                "Tabla",
                "Operación",
                "Descripción",
                "Usuario"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void cargarTabla(){
        modeloTabla.setRowCount(0);

        List<Auditoria> lista = auditoriaDAO.listarTodos();
        for(Auditoria a : lista){

            modeloTabla.addRow(new Object[]{
                    a.getIdAuditoria(),
                    a.getFecha() == null ? "" : a.getFecha().toString(),
                    a.getTablaAfectada(),
                    a.getOperacion(),
                    a.getDescripcion(),
                    a.getUsuario() == null ? "-" : a.getUsuario().getNombre() + " " + a.getUsuario().getApellido()
            });
        }
    }

    private void eventos(){

        btnActualizar.addActionListener(e->cargarTabla());

        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }
        });
    }

    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            ordenador.setRowFilter(null);
        } else {
            ordenador.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto)));
        }
    }
}
