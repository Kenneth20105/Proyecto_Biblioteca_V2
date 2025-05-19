package gui;

import model.Revista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AgregarRevistaDialog extends JDialog {
    private JTextField campoTitulo, campoAutor, campoAnio, campoNumero, campoMes, campoCategoria, campoEditorial, campoUbicacion;
    private GestorBiblioteca gestor;

    public AgregarRevistaDialog(JFrame parent, GestorBiblioteca gestor) {
        super(parent, "Agregar Revista", true);
        this.gestor = gestor;

        setLayout(new GridLayout(9, 2, 5, 5)); // ← aumentamos una fila

        add(new JLabel("Título:"));
        campoTitulo = new JTextField();
        add(campoTitulo);

        add(new JLabel("Autor:"));
        campoAutor = new JTextField();
        add(campoAutor);

        add(new JLabel("Año de Publicación:"));
        campoAnio = new JTextField();
        add(campoAnio);

        add(new JLabel("Número:"));
        campoNumero = new JTextField();
        add(campoNumero);

        add(new JLabel("Mes:"));
        campoMes = new JTextField();
        add(campoMes);

        add(new JLabel("Categoría:"));
        campoCategoria = new JTextField();
        add(campoCategoria);

        add(new JLabel("Editorial:"));
        campoEditorial = new JTextField();
        add(campoEditorial);


        add(new JLabel("Ubicación Física:"));
        campoUbicacion = new JTextField();
        add(campoUbicacion);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarRevista();
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

    private void guardarRevista() {
        try {
            String titulo = campoTitulo.getText();
            String autor = campoAutor.getText();
            int anio = Integer.parseInt(campoAnio.getText());
            int numero = Integer.parseInt(campoNumero.getText());
            String mes = campoMes.getText();
            String categoria = campoCategoria.getText();
            String editorial = campoEditorial.getText();
            String ubicacion = campoUbicacion.getText();

            Revista revista = new Revista(0, titulo, autor, anio, numero, mes, categoria, editorial);
            revista.setUbicacionFisica(ubicacion);

            gestor.agregarDocumento(revista);
            JOptionPane.showMessageDialog(this, "Revista agregada correctamente.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}