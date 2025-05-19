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

    public void devolverDocumentoPorNombre(String tituloDocumento, String carnet) throws SQLException {
        //Obtener el préstamo ANTES de marcar como devuelto
        Prestamo prestamo = prestamoDAO.obtenerPrestamoActivoPorTituloYCarnet(tituloDocumento, carnet);

        // Luego marcar como devuelto
        prestamoDAO.devolverDocumentoPorNombre(tituloDocumento, carnet);

        // Si tenía mora, calcular y registrar
        if (prestamo != null && calcularDiasAtraso(prestamo) > 7) {
            long dias = calcularDiasAtraso(prestamo) - 7;
            double moraDiaria = prestamoDAO.obtenerMoraDiaria(prestamo.getFechaPrestamo().getYear());
            double totalMora = dias * moraDiaria;

            prestamoDAO.registrarPagoMora(prestamo.getIdUsuario(), prestamo.getId(), totalMora);
        }
    }

    public void eliminarPrestamo(int idPrestamo) throws SQLException {
        prestamoDAO.eliminarPrestamo(idPrestamo);
    }

    public void prestarDocumentoPorTitulo(String carnet, String titulo) throws SQLException {
        Usuario usuario = usuarioDAO.obtenerPorCarnet(carnet);
        if (usuario == null) {
            throw new SQLException("Usuario no encontrado.");
        }

        // Admins no tienen restricciones
        if (usuario.getRol().trim().equalsIgnoreCase("administrador")) {
            prestamoDAO.prestarDocumentoPorTitulo(carnet, titulo);
            return;
        }

        // Solo prestamos del mismo usuario
        int prestamosActivos = prestamoDAO.contarPrestamosActivos(usuario.getId());

        int limite = usuario.getRol().equalsIgnoreCase("profesor") ? 6 : 3;

        if (prestamosActivos >= limite) {
            throw new SQLException("El usuario ha alcanzado el límite de préstamos permitidos.");
        }

        int mora = prestamoDAO.obtenerMoraUsuario(usuario.getId());
        if (mora >= 3) {
            throw new SQLException("No se puede prestar debido a moras acumuladas.");
        }

        prestamoDAO.prestarDocumentoPorTitulo(carnet, titulo);
    }


    public boolean puedePrestarDocumento(Usuario usuario) throws SQLException {
        // Admins pueden prestar siempre
        if (usuario.getRol().trim().equalsIgnoreCase("administrador")) {
            return true;
        }

        int prestamosActivos = prestamoDAO.contarPrestamosActivos(usuario.getId());
        int mora = prestamoDAO.obtenerMoraUsuario(usuario.getId());

        int limite = usuario.getRol().equalsIgnoreCase("profesor") ? 6 : 3;
        return prestamosActivos < limite && mora < 3;
    }

    public List<Prestamo> obtenerMorasDeUsuarioPorCarnet(String carnet) throws SQLException {
        return prestamoDAO.obtenerMorasDeUsuarioPorCarnet(carnet);
    }

    public List<Prestamo> obtenerTodasLasMoras(String carnet) throws SQLException {
        return prestamoDAO.obtenerTodasLasMorasPorCarnet(carnet);
    }

    public List<Prestamo> obtenerPrestamosPorUsuario(int idUsuario) throws SQLException {
        return prestamoDAO.obtenerPrestamosPorUsuario(idUsuario);
    }

    public List<Prestamo> obtenerPrestamosActivos() throws SQLException {
        return prestamoDAO.obtenerPrestamosActivos();
    }

    public void marcarMoraComoPagada(int idPrestamo) throws SQLException {
        prestamoDAO.marcarMoraComoPagada(idPrestamo);
    }

    private long calcularDiasAtraso(Prestamo p) {
        if (p.getFechaDevolucion() == null) {return 0;}
        long dias = java.time.temporal.ChronoUnit.DAYS.between(p.getFechaPrestamo(), p.getFechaDevolucion());
        return dias;
    }
}