package gui;

import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginWindow extends JFrame {
    private JTextField campoCarnet;
    private JPasswordField campoPassword;
    private GestorBiblioteca gestor;

    public LoginWindow() {
        setTitle("Inicio de Sesión - Biblioteca Don Bosco");
        setSize(480, 520); // tamaño como el de la imagen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centra la ventana
        setResizable(false);

        try {
            gestor = new GestorBiblioteca();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        construirUI();
    }

    private void construirUI() {
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Panel superior: título y logo
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));

        JLabel tituloLabel = new JLabel("Biblioteca Amigos de Don Bosco");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 20));
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panelSuperior.add(tituloLabel, BorderLayout.NORTH);

        JLabel imagenLabel = new JLabel();
        ImageIcon icono = new ImageIcon("Don-Bosco imagen.png"); // <- asegúrate que esta imagen exista
        Image imagen = icono.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        imagenLabel.setIcon(new ImageIcon(imagen));
        imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panelSuperior.add(imagenLabel, BorderLayout.CENTER);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // Formulario de campos
        JPanel formulario = new JPanel(new GridLayout(4, 1, 10, 5));

        JLabel labelCarnet = new JLabel("Carnet:");
        labelCarnet.setHorizontalAlignment(SwingConstants.LEFT);
        campoCarnet = new JTextField();

        JLabel labelPassword = new JLabel("Contraseña:");
        labelPassword.setHorizontalAlignment(SwingConstants.LEFT);
        campoPassword = new JPasswordField();

        formulario.add(labelCarnet);
        formulario.add(campoCarnet);
        formulario.add(labelPassword);
        formulario.add(campoPassword);

        panelPrincipal.add(formulario, BorderLayout.CENTER);

        // Botón de inicio
        JButton btnIniciar = new JButton("Iniciar Sesión");
        btnIniciar.setPreferredSize(new Dimension(150, 30));
        btnIniciar.addActionListener(e -> iniciarSesion());

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.add(btnIniciar);
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void iniciarSesion() {
        String carnet = campoCarnet.getText();
        String password = new String(campoPassword.getPassword());

        try {
            Usuario usuario = gestor.validarCredencialesPorCarnet(carnet, password);
            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "Bienvenido " + usuario.getNombre() + " (" + usuario.getRol() + ")");
                new VentanaPrincipal(usuario).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Carnet o contraseña incorrectos.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al validar credenciales: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}