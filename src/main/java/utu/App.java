package utu;

import utu.vista.Login;

public class App {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
