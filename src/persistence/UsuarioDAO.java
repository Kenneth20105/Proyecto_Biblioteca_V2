package persistence;

import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void agregarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, carnet, password, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCarnet());
            stmt.setString(3, usuario.getPassword());
            stmt.setString(4, usuario.getRol());
            stmt.executeUpdate();
        }
    }

    public void registrarMoraPagada(int idPrestamo) throws SQLException {
        String sql = "INSERT INTO moras_pagadas (id_prestamo) VALUES (?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            stmt.executeUpdate();
        }
    }


    public Usuario validarCredencialesPorCarnet(String carnet, String password) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE BINARY carnet = ? AND password = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carnet); // Valida el carnet a como esta en la DB
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("carnet"),
                            rs.getString("password"),
                            rs.getString("rol")
                    );
                }
            }
        }
        return null;
    }
    public Usuario obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("carnet"),
                        rs.getString("password"),
                        rs.getString("rol"),
                        rs.getInt("mora")
                );
            }
        }
        return null;
    }

    public Usuario obtenerPorCarnet(String carnet) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE carnet = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carnet);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("carnet"),
                        rs.getString("password"),
                        rs.getString("rol"),
                        rs.getInt("mora")
                );
            }
        }
        return null;
    }

    public List<Usuario> obtenerTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("carnet"),
                        rs.getString("password"),
                        rs.getString("rol"),
                        rs.getInt("mora")
                );
                usuarios.add(u);
            }
        }
        return usuarios;
    }

    public void modificarUsuario(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, password = ?, rol = ? WHERE carnet = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getRol());
            stmt.setString(4, usuario.getCarnet());
            stmt.executeUpdate();
        }
    }

    public void eliminarUsuario(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();
        }
    }

    public void reducirMoraPorCarnet(String carnet) throws SQLException {
        String sql = "UPDATE usuarios SET mora = GREATEST(mora - 1, 0) WHERE carnet = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carnet);
            stmt.executeUpdate();
        }
    }
}