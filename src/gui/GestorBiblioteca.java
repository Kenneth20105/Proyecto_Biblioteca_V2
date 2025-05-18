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

    public void devolverDocumentoPorNombre(String tituloDocumento, String carnetUsuario) throws SQLException {
        Prestamo prestamo = prestamoDAO.obtenerPrestamoActivoPorTituloYCarnet(tituloDocumento, carnetUsuario);
        if (prestamo == null) {
            throw new SQLException("No se encontró un préstamo activo para ese documento.");
        }

        prestamoDAO.marcarComoDevuelto(prestamo.getId());

        long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(
                prestamo.getFechaPrestamo(),
                java.time.LocalDate.now()
        );

        if (diasAtraso > 7) {
            usuarioDAO.reducirMoraPorCarnet(carnetUsuario);
            usuarioDAO.registrarMoraPagada(prestamo.getId()); // nuevo registro
        }
    }

    public List<Prestamo> obtenerPrestamosActivosPorCarnet(String carnet) throws SQLException {
        return prestamoDAO.obtenerPrestamosActivosPorCarnet(carnet);
    }

    public void eliminarPrestamo(int idPrestamo) throws SQLException {
        prestamoDAO.eliminarPrestamo(idPrestamo);
    }

    public void prestarDocumentoPorTitulo(String carnet, String titulo) throws SQLException {
        Usuario usuario = usuarioDAO.obtenerPorCarnet(carnet);
        if (usuario == null) {
            throw new SQLException("Usuario no encontrado.");
        }

        // ✅ Admins no tienen restricciones
        if (usuario.getRol().trim().equalsIgnoreCase("administrador")) {
            prestamoDAO.prestarDocumentoPorTitulo(carnet, titulo);
            return;
        }

        // Para profesores y alumnos
        int limite = usuario.getRol().equalsIgnoreCase("profesor") ? 6 : 3;
        int prestamosActivos = prestamoDAO.contarPrestamosActivos(usuario.getId());
        if (prestamosActivos >= limite) {
            throw new SQLException("El usuario ha alcanzado el límite de préstamos permitidos.");
        }

        int mora = prestamoDAO.obtenerMoraUsuario(usuario.getId());
        if (mora >= 3) {
            throw new SQLException("No se puede prestar debido a moras acumuladas.");
        }

        prestamoDAO.prestarDocumentoPorTitulo(carnet, titulo);
    }

    public int obtenerMoraDeUsuario(int idUsuario) throws SQLException {
        return prestamoDAO.obtenerMoraUsuario(idUsuario);
    }

    public boolean puedePrestarDocumento(Usuario usuario) throws SQLException {
        // ✅ Admins pueden prestar siempre
        if (usuario.getRol().trim().equalsIgnoreCase("administrador")) {
            return true;
        }

        int prestamosActivos = prestamoDAO.contarPrestamosActivos(usuario.getId());
        int mora = prestamoDAO.obtenerMoraUsuario(usuario.getId());

        int limite = usuario.getRol().equalsIgnoreCase("profesor") ? 6 : 3;
        return prestamosActivos < limite && mora < 3;
    }

    public void devolverDocumentoPorNombre(String tituloDocumento) throws SQLException {
        prestamoDAO.devolverDocumentoPorNombre(tituloDocumento);
    }

    public List<Prestamo> obtenerPrestamos() throws SQLException {
        return prestamoDAO.obtenerTodos();
    }

    public List<Prestamo> obtenerMorasDeUsuarioPorCarnet(String carnet) throws SQLException {
        return prestamoDAO.obtenerMorasDeUsuarioPorCarnet(carnet);
    }

    public List<Prestamo> obtenerPrestamosPorUsuario(int idUsuario) throws SQLException {
        return prestamoDAO.obtenerPrestamosPorUsuario(idUsuario);
    }

    public List<Prestamo> obtenerPrestamosActivos() throws SQLException {
        return prestamoDAO.obtenerPrestamosActivos();
    }

    public List<String[]> obtenerDocumentosParaVista() throws SQLException {
        return documentoDAO.obtenerDocumentosParaVista();
    }

    public List<Prestamo> obtenerTodosLosPrestamos() throws SQLException {
        return prestamoDAO.obtenerTodos();
    }
}