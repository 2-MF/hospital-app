package utu.vista;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import utu.dao.LoginDAO;
import utu.modelo.Usuario;
import utu.util.Sesion;

public class Login extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;

    private JButton btnIngresar;
    private JButton btnSalir;

    public Login() {
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        setTitle("Sistema de Gestión Hospitalario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel principal = new JPanel(new BorderLayout(0, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(20, 30, 15, 30));

        JLabel titulo = new JLabel("Bienvenido al Sistema de Gestión Hospitalario");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 2, 10, 10));
        centro.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField(20);
        centro.add(txtUsuario);
        centro.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField(20);
        centro.add(txtPassword);

        principal.add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        btnIngresar = new JButton("Ingresar");
        btnSalir = new JButton("Salir");
        botones.add(btnIngresar);
        botones.add(btnSalir);
        principal.add(botones, BorderLayout.SOUTH);

        btnIngresar.addActionListener(e -> ingresar());
        btnSalir.addActionListener(e -> System.exit(0));

        add(principal);

        getRootPane().setDefaultButton(btnIngresar);
        pack();
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = String.valueOf(txtPassword.getPassword());

        LoginDAO loginDAO;
        try {
            loginDAO = new LoginDAO();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo conectar a la base de datos.\nVerifique que MySQL/MariaDB esté en ejecución.\n\nDetalle: " + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario usuarioLogueado = loginDAO.iniciarSesion(usuario, contrasena);
        if (usuarioLogueado != null) {
            Sesion.setUsuario(usuarioLogueado);
            JOptionPane.showMessageDialog(
                    this,
                    "Bienvenido " + usuarioLogueado.getNombre(),
                    "Acceso permitido",
                    JOptionPane.INFORMATION_MESSAGE);

            new Principal(usuarioLogueado);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Usuario o contraseña incorrectos",
                    "Acceso denegado",
                    JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}
