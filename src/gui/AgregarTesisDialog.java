
package gui;

import model.Tesis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AgregarTesisDialog extends JDialog {
    private JTextField campoTitulo, campoAutor, campoAnio, campoCarrera, campoUniversidad, campoAsesor;
    private GestorBiblioteca gestor;

    public AgregarTesisDialog(JFrame parent, GestorBiblioteca gestor) {
        super(parent, "Agregar Tesis", true);
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

        add(new JLabel("Carrera:"));
        campoCarrera = new JTextField();
        add(campoCarrera);

        add(new JLabel("Universidad:"));
        campoUniversidad = new JTextField();
        add(campoUniversidad);

        add(new JLabel("Asesor Académico:"));
        campoAsesor = new JTextField();
        add(campoAsesor);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarTesis();
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

    private void guardarTesis() {
        try {
            String titulo = campoTitulo.getText();
            String autor = campoAutor.getText();
            int anio = Integer.parseInt(campoAnio.getText());
            String carrera = campoCarrera.getText();
            String universidad = campoUniversidad.getText();
            String asesor = campoAsesor.getText();

            Tesis tesis = new Tesis(0, titulo, autor, anio, carrera, universidad, asesor);
            gestor.agregarDocumento(tesis);
            JOptionPane.showMessageDialog(this, "Tesis agregada correctamente.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
