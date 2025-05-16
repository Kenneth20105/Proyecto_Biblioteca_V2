package gui;

import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class AgregarUsuarioDialog extends JDialog {
    private JTextField campoNombre;
    private JTextField campoCarnet;
    private JPasswordField campoPassword;
    private JComboBox<String> comboRol;
    private GestorBiblioteca gestor;

    public AgregarUsuarioDialog(Frame owner, GestorBiblioteca gestor) {
        super(owner, "Agregar Usuario", true);
        this.gestor = gestor;

        setSize(350, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel formulario = new JPanel(new GridLayout(4, 2, 10, 10));
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formulario.add(new JLabel("Nombre:"));
        campoNombre = new JTextField();
        formulario.add(campoNombre);

        formulario.add(new JLabel("Carnet:")); // reemplaza username
        campoCarnet = new JTextField();
        formulario.add(campoCarnet);

        formulario.add(new JLabel("Contraseña:"));
        campoPassword = new JPasswordField();
        formulario.add(campoPassword);

        formulario.add(new JLabel("Rol:"));
        comboRol = new JComboBox<>(new String[] { "administrador", "profesor", "alumno" });
        formulario.add(comboRol);

        add(formulario, BorderLayout.CENTER);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarUsuario();
            }
        });

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnRegistrar);
        add(panelBoton, BorderLayout.SOUTH);
    }

    private void registrarUsuario() {
        String nombre = campoNombre.getText().trim();
        String carnet = campoCarnet.getText().trim(); // uso de carnet
        String password = new String(campoPassword.getPassword());
        String rol = (String) comboRol.getSelectedItem();

        if (nombre.isEmpty() || carnet.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }

        Usuario nuevo = new Usuario(nombre, carnet, password, rol);
        try {
            gestor.agregarUsuario(nuevo);
            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente.");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar usuario: " + ex.getMessage());
        }
    }
}