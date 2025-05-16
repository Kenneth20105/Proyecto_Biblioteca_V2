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
    public void prestarDocumento(int idUsuario, int idDocumento) throws SQLException {
        String sql = "INSERT INTO prestamos (usuario_id, documento_id, fecha_prestamo, devuelto) VALUES (?, ?, ?, false)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idDocumento);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();
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
    public void devolverDocumento(int idPrestamo) throws SQLException {
        String sql = "UPDATE prestamos SET devuelto = true, fecha_devolucion = CURRENT_DATE WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            stmt.executeUpdate();
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

    public List<Prestamo> obtenerPrestamosActivos() throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE devuelto = FALSE";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate fechaPrestamo = rs.getDate("fecha_prestamo").toLocalDate();

                LocalDate fechaDevolucion = null;
                java.sql.Date sqlDevolucion = rs.getDate("fecha_devolucion");
                if (sqlDevolucion != null) {
                    fechaDevolucion = sqlDevolucion.toLocalDate();
                }

                Prestamo p = new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("usuario_id"),
                        rs.getInt("documento_id"),
                        fechaPrestamo,
                        fechaDevolucion,
                        rs.getBoolean("devuelto")
                );
                lista.add(p);
            }
        }
        return lista;
    }


    //  Contar préstamos activos por usuario
    public int contarPrestamosActivos(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE usuario_id = ? AND devuelto = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
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

    public List<Prestamo> obtenerMorasDeUsuarioPorCarnet(String carnet) throws SQLException {
        List<Prestamo> moras = new ArrayList<>();
        String sql = "SELECT p.*, c.mora_diaria " +
                "FROM prestamos p " +
                "JOIN usuarios u ON u.id = p.id_usuario " +
                "JOIN config_mora c ON YEAR(p.fecha_prestamo) = c.anio " +
                "WHERE u.carnet = ? AND p.devuelto = TRUE " +
                "AND DATEDIFF(p.fecha_devolucion, p.fecha_prestamo) > 7";
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
                        rs.getInt("usuario_id"),
                        rs.getInt("documento_id"),
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


}