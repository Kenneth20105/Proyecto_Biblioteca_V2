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

    // Registrar un nuevo préstamo (fecha actual, sin devolución aún)
    public void prestarDocumentoPorTitulo(String carnet, String titulo) throws SQLException {
        // Verificar existencia del documento
        String sqlDoc = "SELECT id FROM documentos WHERE titulo = ?";
        int idDocumento;

        try (PreparedStatement stmtDoc = conn.prepareStatement(sqlDoc)) {
            stmtDoc.setString(1, titulo);
            try (ResultSet rs = stmtDoc.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("No se encontró el documento con título: " + titulo);
                }
                idDocumento = rs.getInt("id");
            }
        }

        // Insertar préstamo solo si el documento existe
        String sqlInsert = "INSERT INTO prestamos (id_usuario, id_documento, fecha_prestamo, devuelto) " +
                "VALUES ((SELECT id FROM usuarios WHERE carnet = ?), ?, CURDATE(), FALSE)";

        try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
            stmtInsert.setString(1, carnet);
            stmtInsert.setInt(2, idDocumento);
            int filas = stmtInsert.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se pudo registrar el préstamo.");
            }
        }
    }

    // Eliminar un préstamo
    public void eliminarPrestamo(int idPrestamo) throws SQLException {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            stmt.executeUpdate();
        }
    }

    // Marcar un préstamo como devuelto
    public void devolverDocumentoPorNombre(String titulo, String carnet) throws SQLException {
        String sql = "UPDATE prestamos p " +
                "JOIN documentos d ON p.id_documento = d.id " +
                "JOIN usuarios u ON p.id_usuario = u.id " +
                "SET p.devuelto = TRUE, p.fecha_devolucion = CURDATE() " +
                "WHERE d.titulo = ? AND u.carnet = ? AND p.devuelto = FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titulo);
            stmt.setString(2, carnet);
            stmt.executeUpdate();
        }
    }

    //  Obtener todos los préstamos
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

     //  Contar préstamos activos por usuario
    public int contarPrestamosActivos(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND devuelto = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Esto ahora devuelve el número correcto de filas
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

    public void marcarMoraComoPagada(int idPrestamo) throws SQLException {
        String sql = "INSERT INTO moras_pagadas (id_prestamo, fecha_pago) VALUES (?, CURDATE())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            stmt.executeUpdate();
        }
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

    public void registrarPagoMora(int idUsuario, int idPrestamo, double monto) throws SQLException {
        String sql = "INSERT INTO moras_pagadas (id_usuario, monto, fecha_pago, id_prestamo) VALUES (?, ?, CURDATE(), ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setDouble(2, monto);
            stmt.setInt(3, idPrestamo);
            stmt.executeUpdate();
        }
    }

    public double obtenerMoraDiaria(int anio) throws SQLException {
        String sql = "SELECT mora_diaria FROM config_mora WHERE anio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, anio);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("mora_diaria");
            }
        }
        return 0.0;
    }

    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();

        prestamo.setId(rs.getInt("id"));
        prestamo.setIdUsuario(rs.getInt("id_usuario"));
        prestamo.setIdDocumento(rs.getInt("id_documento"));
        prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());

        Date fechaDev = rs.getDate("fecha_devolucion");
        prestamo.setFechaDevolucion(fechaDev != null ? fechaDev.toLocalDate() : null);

        prestamo.setDevuelto(rs.getBoolean("devuelto"));

        // Alias opcionales con try para evitar fallos si no están presentes
        try {
            prestamo.setNombreUsuario(rs.getString("nombreUsuario"));
        } catch (SQLException ignore) {}

        try {
            prestamo.setNombreDocumento(rs.getString("nombreDocumento"));
        } catch (SQLException ignore) {}

        return prestamo;
    }

    public List<Prestamo> obtenerTodasLasMorasPorCarnet(String carnet) throws SQLException {
        List<Prestamo> moras = new ArrayList<>();
        String sql = "SELECT p.*, c.mora_diaria, u.nombre AS nombreUsuario, d.titulo AS nombreDocumento " +
                "FROM prestamos p " +
                "JOIN usuarios u ON u.id = p.id_usuario " +
                "JOIN documentos d ON d.id = p.id_documento " +
                "JOIN config_mora c ON YEAR(p.fecha_prestamo) = c.anio " +
                "LEFT JOIN moras_pagadas mp ON mp.id_prestamo = p.id " +
                "WHERE u.carnet = ? " +
                "AND DATEDIFF(COALESCE(p.fecha_devolucion, CURDATE()), p.fecha_prestamo) > 7 " +
                "AND mp.id_prestamo IS NULL";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, carnet);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate fPrestamo = rs.getDate("fecha_prestamo").toLocalDate();
                LocalDate fDevolucion = rs.getDate("fecha_devolucion") != null
                        ? rs.getDate("fecha_devolucion").toLocalDate()
                        : null;
                long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(fPrestamo,
                        fDevolucion != null ? fDevolucion : LocalDate.now()) - 7;
                double mora = rs.getDouble("mora_diaria") * diasAtraso;

                Prestamo p = new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_usuario"),
                        rs.getInt("id_documento"),
                        fPrestamo,
                        fDevolucion,
                        rs.getBoolean("devuelto"),
                        mora
                );
                p.setNombreUsuario(rs.getString("nombreUsuario"));
                p.setNombreDocumento(rs.getString("nombreDocumento"));
                moras.add(p);
            }
        }

        return moras;
    }
}