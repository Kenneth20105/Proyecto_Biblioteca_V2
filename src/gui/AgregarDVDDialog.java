
package gui;

import model.DVD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AgregarDVDDialog extends JDialog {
    private JTextField campoTitulo, campoAutor, campoAnio, campoDirector, campoDuracion, campoProductora;
    private GestorBiblioteca gestor;

    public AgregarDVDDialog(JFrame parent, GestorBiblioteca gestor) {
        super(parent, "Agregar DVD", true);
        this.gestor = gestor;

        setLayout(new GridLayout(7, 2, 5, 5));

        add(new JLabel("Título:"));
        campoTitulo = new JTextField();
        add(campoTitulo);

        add(new JLabel("Autor:"));
        campoAutor = new JTextField();
        add(campoAutor);

        add(new JLabel("Año de Publicación:"));
        campoAnio = new JTextField();
        add(campoAnio);

        add(new JLabel("Director:"));
        campoDirector = new JTextField();
        add(campoDirector);

        add(new JLabel("Duración (hh:mm:ss):"));
        campoDuracion = new JTextField();
        add(campoDuracion);

        add(new JLabel("Productora:"));
        campoProductora = new JTextField();
        add(campoProductora);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarDVD();
            }
        });

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        add(btnGuardar);
        add(btnCancelar);

        pack();
        setLocationRelativeTo(parent);
    }

    private void guardarDVD() {
        try {
            String titulo = campoTitulo.getText();
            String autor = campoAutor.getText();
            int anio = Integer.parseInt(campoAnio.getText());
            String director = campoDirector.getText();
            String duracion = campoDuracion.getText();
            String productora = campoProductora.getText();

            DVD dvd = new DVD(0, titulo, autor, anio, director, duracion, productora);
            gestor.agregarDocumento(dvd);
            JOptionPane.showMessageDialog(this, "DVD agregado correctamente.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
