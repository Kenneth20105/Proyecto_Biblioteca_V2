package gui;

import model.Documento;
import model.Prestamo;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private GestorBiblioteca gestor;
    private Usuario usuario;
    private JTextArea areaSalida;
    private JPanel panelPrincipal;
    private CardLayout cardLayout;

    public VentanaPrincipal(Usuario usuario) {
        this.usuario = usuario;
        try {
            gestor = new GestorBiblioteca();
        } catch (SQLException e) {
            mostrarError("Error al conectar con la base de datos: " + e.getMessage());
            return;
        }

        setTitle("Sistema Biblioteca Don Bosco - [" + usuario.getRol().toUpperCase() + "]");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        construirUI();
    }

    private void construirUI() {
        // 🔝 PANEL SUPERIOR
        // PANEL SUPERIOR
        JPanel panelSuperior = new JPanel(new BorderLayout());

// Banner a la izquierda
        JLabel labelBanner;
        try {
            // Cargar la imagen original
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("Banner donbosco V4.png"));
            // Escalar la imagen
            int anchoDeseado = 1800;  // Por ejemplo
            int altoDeseado = 300;

            Image imagenEscalada = originalIcon.getImage().getScaledInstance(anchoDeseado, altoDeseado, Image.SCALE_SMOOTH);
            ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);
            labelBanner = new JLabel(iconoEscalado);
        } catch (Exception ex) {
            labelBanner = new JLabel("📚 Biblioteca Amigos de Don Bosco");
            labelBanner.setFont(new Font("Arial", Font.BOLD, 16));
        }
        panelSuperior.add(labelBanner, BorderLayout.WEST);
        panelSuperior.add(labelBanner, BorderLayout.WEST);
// Botón de usuario
        JButton botonUsuario = new JButton("👤");
        botonUsuario.setFocusPainted(false);
        botonUsuario.setMargin(new Insets(4, 8, 4, 8));
        botonUsuario.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

     //Menu emergente
        JPopupMenu menuUsuario = new JPopupMenu();

        JMenuItem itemPrestar = new JMenuItem("Prestar");
        itemPrestar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String tituloDocumento = JOptionPane.showInputDialog("Nombre del Documento:");
                    if (tituloDocumento == null || tituloDocumento.trim().isEmpty()) return;

                    int confirmar = JOptionPane.showConfirmDialog(null,
                            "¿Deseas prestar el documento: \"" + tituloDocumento + "\"?",
                            "Confirmar Préstamo",
                            JOptionPane.YES_NO_OPTION);

                    if (confirmar == JOptionPane.YES_OPTION) {
                        if (!gestor.puedePrestarDocumento(usuario)) {
                            mostrarError("No puedes realizar el préstamo: mora o límite alcanzado.");
                            return;
                        }

                        gestor.prestarDocumentoPorTitulo(usuario.getCarnet(), tituloDocumento);
                        mostrarMensaje("Préstamo registrado correctamente.");
                    }
                } catch (Exception ex) {
                    mostrarError("Error al prestar documento: " + ex.getMessage());
                }
            }
        });
        JMenuItem itemMora = new JMenuItem("Mi Mora");
        itemMora.addActionListener(e -> {
            try {
                List<Prestamo> moras = gestor.obtenerTodasLasMoras(usuario.getCarnet());

                if (moras.isEmpty()) {
                    mostrarMensaje("Actualmente no posee moras pendientes.");
                    return;
                }

                String[] columnas = {"ID Préstamo", "Fecha Préstamo", "Fecha Devolución", "Monto Mora"};
                Object[][] datos = new Object[moras.size()][4];
                double total = 0;

                for (int i = 0; i < moras.size(); i++) {
                    Prestamo p = moras.get(i);
                    datos[i][0] = p.getId();
                    datos[i][1] = p.getFechaPrestamo();
                    datos[i][2] = p.getFechaDevolucion();
                    datos[i][3] = String.format("$%.2f", p.getMontoMora());
                    total += p.getMontoMora();
                }

                JTable tabla = new JTable(datos, columnas);
                JScrollPane scroll = new JScrollPane(tabla);
                tabla.setFillsViewportHeight(true);

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(scroll, BorderLayout.CENTER);

                JLabel totalLabel = new JLabel("Monto total pendiente: $" + String.format("%.2f", total));
                totalLabel.setHorizontalAlignment(JLabel.CENTER);
                totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
                panel.add(totalLabel, BorderLayout.SOUTH);

                JOptionPane.showMessageDialog(null, panel, "Detalle de Moras", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                mostrarError("Error al consultar moras: " + ex.getMessage());
            }
        });



        JMenuItem itemCerrar = new JMenuItem("Cerrar Sesión");
        itemCerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginWindow().setVisible(true);
            }
        });

        menuUsuario.add(itemMora);
        menuUsuario.add(itemCerrar);

// Mostrar el menú al hacer clic en el emoji
        botonUsuario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuUsuario.show(botonUsuario, 0, botonUsuario.getHeight());
            }
        });

// Alineación a la derecha
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelDerecho.setOpaque(false);
        panelDerecho.add(botonUsuario);
        panelSuperior.add(panelDerecho, BorderLayout.EAST);

// Añadir panel completo al frame
        add(panelSuperior, BorderLayout.NORTH);

        // 📋 PANEL CENTRAL
        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);

        areaSalida = new JTextArea();
        areaSalida.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaSalida);

        panelPrincipal = new JPanel(cardLayout = new CardLayout());
        panelPrincipal.add(scrollPane, "inicio");

        add(panelPrincipal, BorderLayout.CENTER);

        // ⬇️ PANEL INFERIOR
        JPanel botonera = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnAgregarDocumento = new JButton("Agregar Documento");
        btnAgregarDocumento.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarSeleccionTipo();
            }
        });

        JButton btnMostrarDocumentos = new JButton("Mostrar Documentos");
        btnMostrarDocumentos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JTextField campoTitulo = new JTextField(15);
                String[] tipos = {"Todos", "Libro", "Revista", "CD", "DVD", "PDF", "Tesis"};
                JComboBox<String> comboTipo = new JComboBox<>(tipos);
                JButton btnBuscar = new JButton("Buscar");
                JButton btnLimpiar = new JButton("Limpiar Filtros");

                panelFiltro.add(new JLabel("Título:"));
                panelFiltro.add(campoTitulo);
                panelFiltro.add(new JLabel("Tipo:"));
                panelFiltro.add(comboTipo);
                panelFiltro.add(btnBuscar);
                panelFiltro.add(btnLimpiar);
                 //Edicion de tabla de documentos
                DefaultTableModel modelo = new DefaultTableModel(new String[]{
                        "ID", "Tipo", "Título", "Autor", "Año", "Editorial", "Páginas",
                        "Número", "Mes", "Género","Asesor Academico", "Tema", "Duración", "Ubicación Física"
                }, 0);
                JTable tabla = new JTable(modelo);
                JScrollPane scroll = new JScrollPane(tabla);
                tabla.setFillsViewportHeight(true);

                ActionListener actualizarTabla = new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        modelo.setRowCount(0);
                        try {
                            List<Documento> documentos = gestor.obtenerDocumentos();
                            String filtroTitulo = campoTitulo.getText().trim().toLowerCase();
                            String filtroTipo = comboTipo.getSelectedItem().toString().toLowerCase();

                            for (Documento d : documentos) {
                                boolean coincideTipo = filtroTipo.equals("todos") || d.getTipo().toLowerCase().equals(filtroTipo);
                                boolean coincideTitulo = filtroTitulo.isEmpty() || d.getTitulo().toLowerCase().contains(filtroTitulo);
                                if (coincideTipo && coincideTitulo) {
                                    Object[] fila = new Object[15];
                                    fila[0] = d.getId();
                                    fila[1] = d.getTipo();
                                    fila[2] = d.getTitulo();
                                    fila[3] = d.getAutor();
                                    fila[4] = d.getAnioPublicacion();
                                    fila[13] = d.getUbicacionFisica();

                                    switch (d.getTipo().toLowerCase()) {
                                        case "libro":
                                            fila[5] = d.getEditorial();
                                            fila[6] = d.getNumeroPaginas();
                                            break;
                                        case "revista":
                                            fila[7] = d.getNumeroRevista();
                                            fila[8] = d.getMes();
                                            break;
                                        case "cd":
                                        case "dvd":
                                            fila[9] = d.getGenero();
                                            fila[12] = d.getDuracion();
                                            break;
                                        case "pdf":
                                        case "tesis":
                                            fila[11] = d.getTema();
                                            fila[10] = d.getAsesorAcademico();
                                            break;
                                    }
                                    modelo.addRow(fila);
                                }
                            }
                        } catch (Exception ex) {
                            mostrarError("Error al filtrar documentos: " + ex.getMessage());
                        }
                    }
                };

                btnBuscar.addActionListener(actualizarTabla);
                btnLimpiar.addActionListener(ev -> {
                    campoTitulo.setText("");
                    comboTipo.setSelectedIndex(0);
                    actualizarTabla.actionPerformed(null);
                });
                JPanel panelFinal = new JPanel(new BorderLayout());
                panelFinal.add(panelFiltro, BorderLayout.NORTH);
                panelFinal.add(scroll, BorderLayout.CENTER);

                // Panel inferior (izquierda y derecha)
                JPanel panelInferior = new JPanel(new BorderLayout());

                // IZQUIERDA: botones de acción
                JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JButton btnPrestarDoc = new JButton("Prestar Documento");
                btnPrestarDoc.addActionListener(e2 -> {
                    try {
                        String tituloDocumento = JOptionPane.showInputDialog("Nombre del Documento:");
                        if (tituloDocumento == null || tituloDocumento.trim().isEmpty()) return;

                        int confirmar = JOptionPane.showConfirmDialog(null,
                                "¿Deseas prestar el documento: \"" + tituloDocumento + "\"?",
                                "Confirmar Préstamo",
                                JOptionPane.YES_NO_OPTION);

                        if (confirmar == JOptionPane.YES_OPTION) {
                            if (!gestor.puedePrestarDocumento(usuario)) {
                                mostrarError("No puedes realizar el préstamo: mora o límite alcanzado.");
                                return;
                            }
                            gestor.prestarDocumentoPorTitulo(usuario.getCarnet(), tituloDocumento);
                            mostrarMensaje("Préstamo registrado correctamente.");
                        }
                    } catch (Exception ex) {
                        mostrarError("Error al prestar documento: " + ex.getMessage());
                    }
                });
                panelIzquierdo.add(btnPrestarDoc);
                if (!usuario.getRol().equalsIgnoreCase("alumno")) {
                    JButton btnAgregarDoc = new JButton("Agregar Documento");
                    btnAgregarDoc.addActionListener(ev -> mostrarSeleccionTipo());
                    panelIzquierdo.add(btnAgregarDoc);
                }

                JButton btnDevolverDoc = new JButton("Devolver Documento");
                btnDevolverDoc.addActionListener(ev -> {
                    try {
                        String titulo = JOptionPane.showInputDialog("Ingrese el nombre del documento a devolver:");
                        if (titulo == null || titulo.trim().isEmpty()) return;

                        gestor.devolverDocumentoPorNombre(titulo.trim(), usuario.getCarnet());
                        mostrarMensaje("Documento devuelto correctamente.");
                    } catch (Exception ex) {
                        mostrarError("Error al devolver documento: " + ex.getMessage());
                    }
                });
                panelIzquierdo.add(btnDevolverDoc);

                // DERECHA: volver
                JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton btnVolver = new JButton("Volver al Menú Principal");
                btnVolver.addActionListener(ev -> cardLayout.show(panelPrincipal, "inicio"));
                panelDerecho.add(btnVolver);

                panelInferior.add(panelIzquierdo, BorderLayout.WEST);
                panelInferior.add(panelDerecho, BorderLayout.EAST);

                panelFinal.add(panelInferior, BorderLayout.SOUTH);

                panelPrincipal.add(panelFinal, "todos_docs");
                cardLayout.show(panelPrincipal, "todos_docs");

                actualizarTabla.actionPerformed(null);
            }
        });

        JButton btnAgregarUsuario = new JButton("Agregar Usuario");
        btnAgregarUsuario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField nombreField = new JTextField();
                JTextField carnetField = new JTextField();
                JTextField passwordField = new JTextField();
                String[] roles = {"administrador", "profesor", "alumno"};
                JComboBox<String> rolCombo = new JComboBox<>(roles);

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Nombre:"));
                panel.add(nombreField);
                panel.add(new JLabel("Carnet:"));
                panel.add(carnetField);
                panel.add(new JLabel("Contraseña:"));
                panel.add(passwordField);
                panel.add(new JLabel("Rol:"));
                panel.add(rolCombo);

                int result = JOptionPane.showConfirmDialog(null, panel, "Agregar Nuevo Usuario",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String nombre = nombreField.getText().trim();
                        String carnet = usuario.getCarnet();
                        String password = passwordField.getText().trim();
                        String rol = (String) rolCombo.getSelectedItem();

                        if (nombre.isEmpty() || carnet.isEmpty() || password.isEmpty()) {
                            mostrarError("Todos los campos deben estar completos.");
                            return;
                        }

                        Usuario nuevo = new Usuario(nombre, carnet, password, rol);
                        gestor.agregarUsuario(nuevo);
                        mostrarMensaje("Usuario agregado correctamente.");
                    } catch (Exception ex) {
                        mostrarError("Error al agregar usuario: " + ex.getMessage());
                    }
                }
            }
        });

        JButton btnMostrarUsuarios = new JButton("Mostrar Usuarios");
        btnMostrarUsuarios.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Usuario> usuarios = gestor.obtenerUsuarios();

                    if (usuarios.isEmpty()) {
                        mostrarMensaje("No hay usuarios registrados.");
                        return;
                    }

                    String[] columnas = {"ID", "Nombre", "Carnet", "Rol", "Mora"};
                    Object[][] datos = new Object[usuarios.size()][5];
                    for (int i = 0; i < usuarios.size(); i++) {
                        Usuario u = usuarios.get(i);
                        datos[i][0] = u.getId();
                        datos[i][1] = u.getNombre();
                        datos[i][2] = u.getCarnet();
                        datos[i][3] = u.getRol();
                        datos[i][4] = u.getMora();
                    }

                    JTable tabla = new JTable(datos, columnas);
                    JScrollPane scroll = new JScrollPane(tabla);
                    tabla.setFillsViewportHeight(true);

                    JPanel panelUsuarios = new JPanel(new BorderLayout());
                    panelUsuarios.add(scroll, BorderLayout.CENTER);

                    // 🔽 Panel inferior (izquierda y derecha)
                    JPanel panelInferior = new JPanel(new BorderLayout());

                    // IZQUIERDA
                    JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT));

                    JButton btnAgregar = new JButton("Agregar Usuario");
                    btnAgregar.addActionListener(ev -> {
                        JTextField nombreField = new JTextField();
                        JTextField carnetField = new JTextField();
                        JTextField passwordField = new JTextField();
                        String[] roles = {"administrador", "profesor", "alumno"};
                        JComboBox<String> rolCombo = new JComboBox<>(roles);

                        JPanel panel = new JPanel(new GridLayout(0, 1));
                        panel.add(new JLabel("Nombre:"));
                        panel.add(nombreField);
                        panel.add(new JLabel("Carnet:"));
                        panel.add(carnetField);
                        panel.add(new JLabel("Contraseña:"));
                        panel.add(passwordField);
                        panel.add(new JLabel("Rol:"));
                        panel.add(rolCombo);

                        int result = JOptionPane.showConfirmDialog(null, panel, "Agregar Nuevo Usuario",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                        if (result == JOptionPane.OK_OPTION) {
                            try {
                                String nombre = nombreField.getText().trim();
                                String carnet = carnetField.getText().trim();
                                String password = passwordField.getText().trim();
                                String rol = (String) rolCombo.getSelectedItem();

                                if (nombre.isEmpty() || carnet.isEmpty() || password.isEmpty()) {
                                    mostrarError("Todos los campos deben estar completos.");
                                    return;
                                }

                                Usuario nuevo = new Usuario(nombre, carnet, password, rol);
                                gestor.agregarUsuario(nuevo);
                                mostrarMensaje("Usuario agregado correctamente.");
                            } catch (Exception ex) {
                                mostrarError("Error al agregar usuario: " + ex.getMessage());
                            }
                        }
                    });
                    panelIzquierdo.add(btnAgregar);

                    JButton btnModificar = new JButton("Modificar Usuario");
                    btnModificar.addActionListener(ev -> {
                        String carnet = JOptionPane.showInputDialog("Ingrese el carnet del usuario a modificar:");
                        if (carnet == null || carnet.trim().isEmpty()) return;

                        try {
                            Usuario usuario = gestor.obtenerUsuarioPorCarnet(carnet);
                            if (usuario == null) {
                                mostrarError("Usuario no encontrado con carnet: " + carnet);
                                return;
                            }

                            JTextField nombreField = new JTextField(usuario.getNombre());
                            JTextField passwordField = new JTextField(usuario.getPassword());
                            String[] roles = {"administrador", "profesor", "alumno"};
                            JComboBox<String> rolCombo = new JComboBox<>(roles);
                            rolCombo.setSelectedItem(usuario.getRol());

                            JPanel panel = new JPanel(new GridLayout(0, 1));
                            panel.add(new JLabel("Nombre:"));
                            panel.add(nombreField);
                            panel.add(new JLabel("Contraseña:"));
                            panel.add(passwordField);
                            panel.add(new JLabel("Rol:"));
                            panel.add(rolCombo);

                            int result = JOptionPane.showConfirmDialog(null, panel, "Modificar Usuario",
                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                            if (result == JOptionPane.OK_OPTION) {
                                usuario.setNombre(nombreField.getText().trim());
                                usuario.setPassword(passwordField.getText().trim());
                                usuario.setRol((String) rolCombo.getSelectedItem());

                                gestor.modificarUsuario(usuario);
                                mostrarMensaje("Usuario modificado correctamente.");
                            }

                        } catch (Exception ex) {
                            mostrarError("Error al modificar usuario: " + ex.getMessage());
                        }
                    });
                    panelIzquierdo.add(btnModificar);

                    JButton btnEliminar = new JButton("Eliminar Usuario");
                    btnEliminar.addActionListener(ev -> {
                        String carnet = JOptionPane.showInputDialog("Ingrese el carnet del usuario a eliminar:");
                        if (carnet == null || carnet.trim().isEmpty()) return;

                        int confirm = JOptionPane.showConfirmDialog(null,
                                "¿Estás seguro de eliminar al usuario con carnet: " + carnet + "?",
                                "Confirmar Eliminación",
                                JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                Usuario usuario = gestor.obtenerUsuarioPorCarnet(carnet);
                                if (usuario == null) {
                                    mostrarError("No se encontró usuario con el carnet: " + carnet);
                                    return;
                                }
                                gestor.eliminarUsuario(usuario.getId());
                                mostrarMensaje("Usuario eliminado correctamente.");
                            } catch (Exception ex) {
                                mostrarError("Error al eliminar usuario: " + ex.getMessage());
                            }
                        }
                    });
                    panelIzquierdo.add(btnEliminar);

                    panelInferior.add(panelIzquierdo, BorderLayout.WEST);

                    // DERECHA
                    JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton btnVolver = new JButton("Volver al Menú Principal");
                    btnVolver.addActionListener(evt -> cardLayout.show(panelPrincipal, "inicio"));
                    panelDerecho.add(btnVolver);
                    panelInferior.add(panelDerecho, BorderLayout.EAST);

                    // Agrega la parte inferior al panel principal
                    panelUsuarios.add(panelInferior, BorderLayout.SOUTH);

                    panelPrincipal.add(panelUsuarios, "usuarios");
                    cardLayout.show(panelPrincipal, "usuarios");

                } catch (Exception ex) {
                    mostrarError("Error al obtener usuarios: " + ex.getMessage());
                }
            }
        });
        JButton btnDevolverDocumento = new JButton("Devolver Documento");
        btnDevolverDocumento.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String titulo = JOptionPane.showInputDialog("Ingrese el nombre del documento a devolver:");
                    if (titulo == null || titulo.trim().isEmpty()) return;

                    // Primero devolvemos el documento
                    gestor.devolverDocumentoPorNombre(titulo.trim(), usuario.getCarnet());

                    // Luego buscamos si tenía mora ese préstamo y la marcamos como pagada
                    List<Prestamo> moras = gestor.obtenerMorasDeUsuarioPorCarnet(usuario.getCarnet());
                    for (Prestamo mora : moras) {
                        if (mora.getNombreDocumento().equalsIgnoreCase(titulo.trim())) {
                            gestor.marcarMoraComoPagada(mora.getId());
                            break; // solo una coincidencia es necesaria
                        }
                    }

                    mostrarMensaje("Documento devuelto correctamente.");
                } catch (Exception ex) {
                    mostrarError("Error al devolver documento: " + ex.getMessage());
                }
            }
        });

        JButton btnModificarUsuario = new JButton("Modificar Usuario");
        btnModificarUsuario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String carnet = JOptionPane.showInputDialog("Ingrese el carnet del usuario a modificar:");
                if (carnet == null || carnet.trim().isEmpty()) return;

                try {
                    Usuario usuario = gestor.obtenerUsuarioPorCarnet(carnet);
                    if (usuario == null) {
                        mostrarError("Usuario no encontrado con carnet: " + carnet);
                        return;
                    }

                    JTextField nombreField = new JTextField(usuario.getNombre());
                    JTextField passwordField = new JTextField(usuario.getPassword());
                    String[] roles = {"administrador", "profesor", "alumno"};
                    JComboBox<String> rolCombo = new JComboBox<>(roles);
                    rolCombo.setSelectedItem(usuario.getRol());

                    JPanel panel = new JPanel(new GridLayout(0, 1));
                    panel.add(new JLabel("Nombre:"));
                    panel.add(nombreField);
                    panel.add(new JLabel("Contraseña:"));
                    panel.add(passwordField);
                    panel.add(new JLabel("Rol:"));
                    panel.add(rolCombo);

                    int result = JOptionPane.showConfirmDialog(null, panel, "Modificar Usuario",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        usuario.setNombre(nombreField.getText().trim());
                        usuario.setPassword(passwordField.getText().trim());
                        usuario.setRol((String) rolCombo.getSelectedItem());

                        gestor.modificarUsuario(usuario);
                        mostrarMensaje("Usuario modificado correctamente.");
                    }

                } catch (Exception ex) {
                    mostrarError("Error al modificar usuario: " + ex.getMessage());
                }
            }
        });

        JButton btnEliminarUsuario = new JButton("Eliminar Usuario");
        btnEliminarUsuario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String carnet = JOptionPane.showInputDialog("Ingrese el carnet del usuario a eliminar:");
                if (carnet == null || carnet.trim().isEmpty()) return;

                int confirm = JOptionPane.showConfirmDialog(null,
                        "¿Estás seguro de eliminar al usuario con carnet: " + carnet + "?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Usuario usuario = gestor.obtenerUsuarioPorCarnet(carnet);
                        if (usuario == null) {
                            mostrarError("No se encontró usuario con el carnet: " + carnet);
                            return;
                        }
                        gestor.eliminarUsuario(usuario.getId());
                        mostrarMensaje("Usuario eliminado correctamente.");
                    } catch (Exception ex) {
                        mostrarError("Error al eliminar usuario: " + ex.getMessage());
                    }
                }
            }
        });

        JButton btnEliminarPrestamo = new JButton("Eliminar Préstamo");
        btnEliminarPrestamo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idStr = JOptionPane.showInputDialog("Ingrese el ID del préstamo a eliminar:");
                if (idStr == null || idStr.trim().isEmpty()) return;

                try {
                    int idPrestamo = Integer.parseInt(idStr);

                    int confirm = JOptionPane.showConfirmDialog(null,
                            "¿Seguro que deseas eliminar el préstamo con ID: " + idPrestamo + "?",
                            "Confirmar Eliminación",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        gestor.eliminarPrestamo(idPrestamo);
                        mostrarMensaje("Préstamo eliminado correctamente.");
                    }

                } catch (NumberFormatException ex) {
                    mostrarError("ID inválido.");
                } catch (Exception ex) {
                    mostrarError("Error al eliminar préstamo: " + ex.getMessage());
                }
            }
        });


        JButton btnPrestamosActivos = new JButton("Préstamos Activos");
        btnPrestamosActivos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Prestamo> prestamos = gestor.obtenerPrestamosActivos();
                    mostrarPrestamos(prestamos);
                } catch (SQLException ex) {
                    mostrarError("Error al obtener préstamos activos: " + ex.getMessage());
                }
            }
        });

        JButton btnMisPrestamos = new JButton("Mis Préstamos");
        btnMisPrestamos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Prestamo> prestamos = gestor.obtenerPrestamosPorUsuario(usuario.getId());
                    mostrarPrestamos(prestamos);
                } catch (SQLException ex) {
                    mostrarError("Error al obtener tus préstamos: " + ex.getMessage());
                }
            }
        });

//  // BOTONERA Mostrar botones según rol
        if (usuario.getRol().equalsIgnoreCase("alumno")) {
            botonera.add(btnMostrarDocumentos);
            //botonera.add(btnDevolverDocumento); ahora en MostrarDocumentos
            botonera.add(btnMisPrestamos);
        } else {
           // botonera.add(btnAgregarDocumento); ahora en MostrarDocumentos
            botonera.add(btnMostrarDocumentos);
            // botonera.add(btnAgregarUsuario); ahora en MostrarUsuarios
            // botonera.add(btnModificarUsuario); ahora en MostrarUsuarios
            botonera.add(btnMostrarUsuarios);
            //botonera.add(btnEliminarUsuario); ahora en MostrarUsuarios
            //botonera.add(btnDevolverDocumento); ahora en MostrarDocumentos
            botonera.add(btnEliminarPrestamo);
            botonera.add(btnPrestamosActivos);
        }

        add(botonera, BorderLayout.SOUTH);
    }
    // cierre correcto de construirUI

    private void mostrarPrestamosActivosEnTabla(List<Prestamo> prestamos) {
        String[] columnas = { "ID", "Carnet Usuario", "Documento", "Fecha Préstamo", "Fecha Devolución", "Devuelto" };
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        for (Prestamo p : prestamos) {
            modelo.addRow(new Object[]{
                    p.getId(),
                    p.getNombreUsuario() != null ? p.getNombreUsuario() : "N/A",
                    p.getNombreDocumento() != null ? p.getNombreDocumento() : "N/A",
                    p.getFechaPrestamo(),
                    p.getFechaDevolucion() != null
                            ? p.getFechaDevolucion()
                            : p.getFechaPrestamo().plusDays(7),
                    p.isDevuelto() ? "Sí" : "No"
            });
        }

        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        tabla.setFillsViewportHeight(true);

        JPanel panelPrestamos = new JPanel(new BorderLayout());
        panelPrestamos.add(scroll, BorderLayout.CENTER);

        JButton btnVolver = new JButton("Volver al Menú Principal");
        btnVolver.addActionListener(e -> cardLayout.show(panelPrincipal, "inicio"));
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.add(btnVolver);
        panelPrestamos.add(panelBoton, BorderLayout.SOUTH);

        panelPrincipal.add(panelPrestamos, "prestamos_activos");
        cardLayout.show(panelPrincipal, "prestamos_activos");
    }

    // ✅ Métodos auxiliares fuera del metodo construirUI

    private void mostrarSeleccionTipo() {
        String[] tipos = {"Libro", "Revista", "CD", "DVD", "PDF", "Tesis"};
        String tipoSeleccionado = (String) JOptionPane.showInputDialog(
                this, "Seleccione el tipo de documento:", "Nuevo Documento",
                JOptionPane.PLAIN_MESSAGE, null, tipos, tipos[0]);

        if (tipoSeleccionado == null) return;

        switch (tipoSeleccionado.toLowerCase()) {
            case "libro":
                new AgregarLibroDialog(this, gestor).setVisible(true);
                break;
            case "revista":
                new AgregarRevistaDialog(this, gestor).setVisible(true);
                break;
            case "cd":
                new AgregarCDDialog(this, gestor).setVisible(true);
                break;
            case "dvd":
                new AgregarDVDDialog(this, gestor).setVisible(true);
                break;
            case "pdf":
                new AgregarPDFDialog(this, gestor).setVisible(true);
                break;
            case "tesis":
                new AgregarTesisDialog(this, gestor).setVisible(true);
                break;
        }
    }

    private void mostrarDocumentos() {
        try {
            List<Documento> docs = gestor.obtenerDocumentos();
            areaSalida.setText("");
            for (Documento doc : docs) {
                areaSalida.append(doc.toString() + "\n");
            }
        } catch (SQLException ex) {
            mostrarError("Error al mostrar documentos: " + ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarPrestamos(List<Prestamo> prestamos) {
        String[] columnas = {
                "ID", "Usuario", "Documento", "Fecha Préstamo", "Fecha Límite", "Fecha Devolución", "Devuelto"
        };
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        for (Prestamo p : prestamos) {
            Object[] fila = {
                    p.getId(),
                    p.getNombreUsuario(),
                    p.getNombreDocumento(),
                    p.getFechaPrestamo(),
                    p.getFechaPrestamo().plusDays(7), // ✅ fecha límite calculada
                    p.getFechaDevolucion() != null ? p.getFechaDevolucion() : "No devuelto",
                    p.isDevuelto() ? "Sí" : "No"
            };
            modelo.addRow(fila);
        }

        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        JDialog dialogo = new JDialog(this, "Listado de Préstamos", true);
        dialogo.setSize(800, 400);
        dialogo.setLocationRelativeTo(this);
        dialogo.add(scroll);
        dialogo.setVisible(true);
    }

}
