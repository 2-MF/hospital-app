package utu.vista;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JOptionPane;
import utu.dao.LoginDAO;
import utu.modelo.Usuario;
import utu.util.Sesion;

public class Login extends JFrame{
    
    private javax.swing.JTextField txtUsuario;
    private javax.swing.JPasswordField txtPassword;

    private JButton btnIngresar;
    private JButton btnSalir;

    public Login() {
        iniciarComponentes();
    }
    private void iniciarComponentes(){
        
        setTitle("Sistema de Gestión Hospitalario");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        JPanel principal = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Bienvenido al Sistema de Gestión Hospitalario");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD,20));
        principal.add(titulo, BorderLayout.NORTH);
        JPanel centro = new JPanel(new GridLayout(2, 2, 10, 10));
        centro.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        centro.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        centro.add(txtUsuario);
        centro.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
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
            
            Principal principal = new Principal(usuarioLogueado);
            principal.setLocationRelativeTo(null);
            principal.setVisible(true);
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
