
package gui;

import model.PDF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AgregarPDFDialog extends JDialog {
    private JTextField campoTitulo, campoAutor, campoAnio, campoTema, campoPaginas, campoAutorOriginal;
    private GestorBiblioteca gestor;

    public AgregarPDFDialog(JFrame parent, GestorBiblioteca gestor) {
        super(parent, "Agregar PDF", true);
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

        add(new JLabel("Tema:"));
        campoTema = new JTextField();
        add(campoTema);

        add(new JLabel("Número de Páginas:"));
        campoPaginas = new JTextField();
        add(campoPaginas);

        add(new JLabel("Autor Original:"));
        campoAutorOriginal = new JTextField();
        add(campoAutorOriginal);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarPDF();
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

    private void guardarPDF() {
        try {
            String titulo = campoTitulo.getText();
            String autor = campoAutor.getText();
            int anio = Integer.parseInt(campoAnio.getText());
            String tema = campoTema.getText();
            int paginas = Integer.parseInt(campoPaginas.getText());
            String autorOriginal = campoAutorOriginal.getText();

            PDF pdf = new PDF(0, titulo, autor, anio, tema, paginas, autorOriginal);
            gestor.agregarDocumento(pdf);
            JOptionPane.showMessageDialog(this, "PDF agregado correctamente.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
