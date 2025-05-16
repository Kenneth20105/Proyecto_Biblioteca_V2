package gui;

import model.*;
import persistence.DocumentoDAO;
import persistence.PrestamoDAO;
import persistence.UsuarioDAO;

import java.sql.SQLException;
import java.util.List;

public class GestorBiblioteca {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final DocumentoDAO documentoDAO;
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    public GestorBiblioteca() throws SQLException {
        documentoDAO = new DocumentoDAO();
    }

    public Usuario validarCredencialesPorCarnet(String carnet, String password) throws SQLException {
        return usuarioDAO.validarCredencialesPorCarnet(carnet, password);
    }

    public void agregarUsuario(Usuario usuario) throws SQLException {
        usuarioDAO.agregarUsuario(usuario);
    }

    public List<Usuario> obtenerUsuarios() throws SQLException {
        return usuarioDAO.obtenerTodos();
    }

    public Usuario obtenerUsuarioPorCarnet(String carnet) throws SQLException {
        return usuarioDAO.obtenerPorCarnet(carnet);
    }

    public void modificarUsuario(Usuario usuario) throws SQLException {
        usuarioDAO.modificarUsuario(usuario);
    }

    public void eliminarUsuario(int idUsuario) throws SQLException {
        usuarioDAO.eliminarUsuario(idUsuario);
    }

    public void agregarDocumento(Documento doc) throws SQLException {
        documentoDAO.agregarDocumento(doc);
    }

    public List<Documento> obtenerDocumentos() throws SQLException {
        return documentoDAO.obtenerTodos();
    }

    public List<Prestamo> obtenerPrestamosActivos() throws SQLException {
        return prestamoDAO.obtenerPrestamosActivos();
    }

    public void eliminarDocumento(int id) throws SQLException {
        documentoDAO.eliminarDocumento(id);
    }

    public void eliminarPrestamo(int idPrestamo) throws SQLException {
        prestamoDAO.eliminarPrestamo(idPrestamo);
    }

    public void prestarDocumento(int idUsuario, int idDocumento) throws SQLException {
        int limite;
        Usuario usuario = usuarioDAO.obtenerPorId(idUsuario);
        if (usuario == null) {
            throw new SQLException("Usuario no encontrado.");
        }

        switch (usuario.getRol()) {
            case "profesor":
                limite = 6;
                break;
            case "alumno":
                limite = 3;
                break;
            default:
                limite = Integer.MAX_VALUE; // sin límite para admin u otros
        }

        int prestamosActivos = prestamoDAO.contarPrestamosActivos(idUsuario);

        if (prestamosActivos >= limite) {
            throw new SQLException("El usuario ha alcanzado el límite de préstamos permitidos.");
        }

        prestamoDAO.prestarDocumento(idUsuario, idDocumento);
    }

    public int obtenerMoraDeUsuario(int idUsuario) throws SQLException {
        return prestamoDAO.obtenerMoraUsuario(idUsuario);
    }

    public boolean puedePrestarDocumento(Usuario usuario) throws SQLException {
        int prestamosActivos = prestamoDAO.contarPrestamosActivos(usuario.getId());
        int mora = prestamoDAO.obtenerMoraUsuario(usuario.getId());

        int limite = usuario.getRol().equalsIgnoreCase("profesor") ? 6 : 3;
        return prestamosActivos < limite && mora < 3; // Ej: máximo 2 moras permitidas
    }

    public void devolverDocumento(int idPrestamo) throws SQLException {
        prestamoDAO.devolverDocumento(idPrestamo);
    }

    public List<Prestamo> obtenerPrestamos() throws SQLException {
        return prestamoDAO.obtenerTodos();
    }

    public List<Prestamo> obtenerMorasDeUsuarioPorCarnet(String carnet) throws SQLException {
        return prestamoDAO.obtenerMorasDeUsuarioPorCarnet(carnet);
    }

    public List<String[]> obtenerDocumentosParaVista() throws SQLException {
        return documentoDAO.obtenerDocumentosParaVista();
    }
}