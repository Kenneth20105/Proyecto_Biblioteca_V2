import gui.LoginWindow;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginWindow login;
            login = new LoginWindow();
            login.setVisible(true);
        });
    }
}