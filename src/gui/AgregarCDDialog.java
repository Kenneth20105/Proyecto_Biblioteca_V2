
package gui;

import model.CD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AgregarCDDialog extends JDialog {
    private JTextField campoTitulo, campoAutor, campoAnio, campoGenero, campoDuracion, campoArtista;
    private GestorBiblioteca gestor;

    public AgregarCDDialog(JFrame parent, GestorBiblioteca gestor) {
        super(parent, "Agregar CD", true);
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

        add(new JLabel("Género:"));
        campoGenero = new JTextField();
        add(campoGenero);

        add(new JLabel("Duración (hh:mm:ss):"));
        campoDuracion = new JTextField();
        add(campoDuracion);

        add(new JLabel("Artista:"));
        campoArtista = new JTextField();
        add(campoArtista);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarCD();
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

    private void guardarCD() {
        try {
            String titulo = campoTitulo.getText();
            String autor = campoAutor.getText();
            int anio = Integer.parseInt(campoAnio.getText());
            String genero = campoGenero.getText();
            String duracion = campoDuracion.getText();
            String artista = campoArtista.getText();

            CD cd = new CD(0, titulo, autor, anio, genero, duracion, artista);
            gestor.agregarDocumento(cd);
            JOptionPane.showMessageDialog(this, "CD agregado correctamente.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
