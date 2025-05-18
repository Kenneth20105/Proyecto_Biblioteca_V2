package persistence;

import model.Prestamo;

import java.sql.*;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    private final Connection conn;

    public PrestamoDAO() throws SQLException {
        this.conn = Conexion.getConnection();
    }

    // ✅ Registrar un nuevo préstamo (fecha actual, sin devolución aún)
    public void prestarDocumentoPorTitulo(String carnet, String titulo) throws SQLException {
        String sql = "INSERT INTO prestamos (id_usuario, id_documento, fecha_prestamo, devuelto) " +
                "VALUES ((SELECT id FROM usuarios WHERE carnet = ?), " +
                "(SELECT id FROM documentos WHERE titulo = ?), CURDATE(), FALSE)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carnet);
            stmt.setString(2, titulo);
            int filas = stmt.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se pudo registrar el préstamo.");
            }
        }
    }

    // ✅ Eliminar un préstamo
    public void eliminarPrestamo(int idPrestamo) throws SQLException {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            stmt.executeUpdate();
        }
    }

    // ✅ Verificar existencia de préstamo
    public boolean existePrestamo(int id) throws SQLException {
        String sql = "SELECT 1 FROM prestamos WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    // ✅ Marcar un préstamo como devuelto
    public void devolverDocumentoPorNombre(String tituloDocumento) throws SQLException {
        String sql = "UPDATE prestamos p " +
                "JOIN documentos d ON p.id_documento = d.id " +
                "SET p.devuelto = TRUE, p.fecha_devolucion = CURRENT_DATE " +
                "WHERE d.titulo = ? AND p.devuelto = FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tituloDocumento);
            int afectados = stmt.executeUpdate();

            if (afectados == 0) {
                throw new SQLException("No se encontró un préstamo activo para el documento: " + tituloDocumento);
            }
        }
    }

    // ✅ Verificar si ya fue devuelto
    public boolean estaDevuelto(int idPrestamo) throws SQLException {
        String sql = "SELECT devuelto FROM prestamos WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("devuelto");
            } else {
                throw new SQLException("El préstamo con ID " + idPrestamo + " no existe.");
            }
        }
    }

    //  Obtener todos los préstamos
    public List<Prestamo> obtenerTodos() throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.id, p.usuario_id, u.nombre AS nombre_usuario, " +
                "p.documento_id, d.titulo AS nombre_documento, " +
                "p.fecha_prestamo, p.fecha_devolucion, p.devuelto " +
                "FROM prestamos p " +
                "JOIN usuarios u ON p.usuario_id = u.id " +
                "JOIN documentos d ON p.documento_id = d.id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int idUsuario = rs.getInt("usuario_id");
                String nombreUsuario = rs.getString("nombre_usuario");
                int idDocumento = rs.getInt("documento_id");
                String nombreDocumento = rs.getString("nombre_documento");
                LocalDate fechaPrestamo = rs.getDate("fecha_prestamo").toLocalDate();

                LocalDate fechaDevolucion = null;
                java.sql.Date sqlDevolucion = rs.getDate("fecha_devolucion");
                if (sqlDevolucion != null) {
                    fechaDevolucion = sqlDevolucion.toLocalDate();
                }

                Prestamo p = new Prestamo(id, idUsuario, idDocumento, fechaPrestamo, fechaDevolucion, rs.getBoolean("devuelto"));
                p.setNombreUsuario(nombreUsuario);
                p.setNombreDocumento(nombreDocumento);

                prestamos.add(p);
            }
        }

        return prestamos;
    }

    public Prestamo obtenerPrestamoActivoPorTituloYCarnet(String titulo, String carnet) throws SQLException {
        String sql = "SELECT p.id, p.fecha_prestamo " +
                "FROM prestamos p " +
                "JOIN documentos d ON p.id_documento = d.id " +
                "JOIN usuarios u ON p.id_usuario = u.id " +
                "WHERE d.titulo = ? AND u.carnet = ? AND p.devuelto = FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titulo);
            stmt.setString(2, carnet);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Prestamo p = new Prestamo();
                p.setId(rs.getInt("id"));
                p.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());
                return p;
            }
        }

        return null;
    }

    public List<Prestamo> obtenerPrestamosActivosPorCarnet(String carnet) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();

        String sql = "SELECT p.id, d.titulo AS nombre_documento, p.fecha_prestamo, u.carnet " +
                "FROM prestamos p " +
                "JOIN usuarios u ON p.id_usuario = u.id " +
                "JOIN documentos d ON p.id_documento = d.id " +
                "WHERE u.carnet = ? AND p.devuelto = FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carnet);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prestamo p = new Prestamo();
                    p.setId(rs.getInt("id"));
                    p.setNombreDocumento(rs.getString("nombre_documento"));
                    p.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());
                    p.setNombreUsuario(rs.getString("carnet")); // carnet del usuario
                    p.setDevuelto(false); // importante para mostrar estado
                    prestamos.add(p);
                }
            }
        }

        return prestamos;
    }


    //  Contar préstamos activos por usuario
    public int contarPrestamosActivos(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND devuelto = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // ✅ Esto ahora devuelve el número correcto de filas
            }
        }
        return 0;
    }
    public int obtenerMoraUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT mora FROM usuarios WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("mora");
            }
        }
        return 0;
    }

    public List<Prestamo> obtenerPrestamosPorUsuario(int idUsuario) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre AS nombreUsuario, d.titulo AS nombreDocumento " +
                "FROM prestamos p " +
                "JOIN usuarios u ON p.id_usuario = u.id " +
                "JOIN documentos d ON p.id_documento = d.id " +
                "WHERE p.id_usuario = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Prestamo p = mapearPrestamo(rs);
                prestamos.add(p);
            }
        }
        return prestamos;
    }

    public List<Prestamo> obtenerMorasDeUsuarioPorCarnet(String carnet) throws SQLException {
        List<Prestamo> moras = new ArrayList<>();
        String sql = "SELECT p.*, c.mora_diaria " +
                "FROM prestamos p " +
                "JOIN usuarios u ON u.id = p.id_usuario " +
                "JOIN config_mora c ON YEAR(p.fecha_prestamo) = c.anio " +
                "LEFT JOIN moras_pagadas mp ON mp.id_prestamo = p.id " +
                "WHERE u.carnet = ? AND p.devuelto = TRUE " +
                "AND DATEDIFF(p.fecha_devolucion, p.fecha_prestamo) > 7 " +
                "AND mp.id_prestamo IS NULL";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, carnet);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate fPrestamo = rs.getDate("fecha_prestamo").toLocalDate();
                LocalDate fDevolucion = rs.getDate("fecha_devolucion").toLocalDate();
                long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(fPrestamo, fDevolucion) - 7;
                double mora = rs.getDouble("mora_diaria") * diasAtraso;

                Prestamo p = new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_usuario"),
                        rs.getInt("id_documento"),
                        fPrestamo,
                        fDevolucion,
                        true,
                        mora
                );
                moras.add(p);
            }
        }

        return moras;
    }

    public void marcarComoDevuelto(int idPrestamo) throws SQLException {
        String sql = "UPDATE prestamos SET devuelto = TRUE, fecha_devolucion = CURDATE() WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            stmt.executeUpdate();
        }
    }

    public List<Prestamo> obtenerPrestamosActivos() throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre AS nombreUsuario, d.titulo AS nombreDocumento " +
                "FROM prestamos p " +
                "JOIN usuarios u ON p.id_usuario = u.id " +
                "JOIN documentos d ON p.id_documento = d.id " +
                "WHERE p.devuelto = false";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Prestamo p = mapearPrestamo(rs);
                prestamos.add(p);
            }
        }
        return prestamos;
    }

    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();

        prestamo.setId(rs.getInt("id"));
        prestamo.setIdUsuario(rs.getInt("id_usuario"));
        prestamo.setIdDocumento(rs.getInt("id_documento"));
        prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());
        prestamo.setFechaDevolucion(rs.getDate("fecha_devolucion") != null
                ? rs.getDate("fecha_devolucion").toLocalDate()
                : null);
        prestamo.setDevuelto(rs.getBoolean("devuelto"));

        // Campos adicionales para mostrar nombre en la tabla
        try {
            prestamo.setNombreUsuario(rs.getString("nombreUsuario"));
            prestamo.setNombreDocumento(rs.getString("nombreDocumento"));
        } catch (SQLException e) {
            // Estos campos podrían no existir en algunos contextos, así que se ignoran si fallan
        }

        return prestamo;


    }
}