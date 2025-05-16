
package gui;

import model.Libro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AgregarLibroDialog extends JDialog {
    private JTextField campoTitulo, campoAutor, campoAnio, campoISBN, campoEditorial, campoPaginas;
    private GestorBiblioteca gestor;

    public AgregarLibroDialog(JFrame parent, GestorBiblioteca gestor) {
        super(parent, "Agregar Libro", true);
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

        add(new JLabel("ISBN:"));
        campoISBN = new JTextField();
        add(campoISBN);

        add(new JLabel("Editorial:"));
        campoEditorial = new JTextField();
        add(campoEditorial);

        add(new JLabel("Número de Páginas:"));
        campoPaginas = new JTextField();
        add(campoPaginas);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarLibro();
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

    private void guardarLibro() {
        try {
            String titulo = campoTitulo.getText();
            String autor = campoAutor.getText();
            int anio = Integer.parseInt(campoAnio.getText());
            String isbn = campoISBN.getText();
            String editorial = campoEditorial.getText();
            int paginas = Integer.parseInt(campoPaginas.getText());

            Libro libro = new Libro(0, titulo, autor, anio, isbn, editorial, paginas);
            gestor.agregarDocumento(libro);
            JOptionPane.showMessageDialog(this, "Libro agregado correctamente.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
