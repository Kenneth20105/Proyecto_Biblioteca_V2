
package persistence;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentoDAO {
    private final Connection connection;

    public DocumentoDAO() throws SQLException {
        this.connection = Conexion.getConnection();
    }

    public int agregarDocumentoBase(Documento doc) throws SQLException {
        String sql = "INSERT INTO documentos (titulo, autor, anio_publicacion, tipo, ubicacion_fisica) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, doc.getTitulo());
            stmt.setString(2, doc.getAutor());
            stmt.setInt(3, doc.getAnioPublicacion());
            stmt.setString(4, doc.getTipo());
            stmt.setString(5, doc.getUbicacionFisica());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado.");
                }
            }
        }
    }

    public void agregarDocumento(Documento doc) throws SQLException {
        int id = agregarDocumentoBase(doc);
        String tipo = doc.getTipo();

        switch (tipo) {
            case "libro":
                Libro libro = (Libro) doc;
                String sqlLibro = "INSERT INTO libros (id, isbn, editorial, numero_paginas) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sqlLibro)) {
                    stmt.setInt(1, id);
                    stmt.setString(2, libro.getIsbn());
                    stmt.setString(3, libro.getEditorial());
                    stmt.setInt(4, libro.getNumeroPaginas());
                    stmt.executeUpdate();
                }
                break;
            case "revista":
                Revista revista = (Revista) doc;
                String sqlRevista = "INSERT INTO revistas (id, numero, mes, categoria, editorial) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sqlRevista)) {
                    stmt.setInt(1, id);
                    stmt.setInt(2, revista.getNumero());
                    stmt.setString(3, revista.getMes());
                    stmt.setString(4, revista.getCategoria());
                    stmt.setString(5, revista.getEditorial());
                    stmt.executeUpdate();
                }
                break;
            case "cd":
                CD cd = (CD) doc;
                String sqlCD = "INSERT INTO cds (id, genero, duracion, artista) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sqlCD)) {
                    stmt.setInt(1, id);
                    stmt.setString(2, cd.getGenero());
                    stmt.setString(3, cd.getDuracion());
                    stmt.setString(4, cd.getArtista());
                    stmt.executeUpdate();
                }
                break;
            case "dvd":
                DVD dvd = (DVD) doc;
                String sqlDVD = "INSERT INTO dvds (id, director, duracion, productora) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sqlDVD)) {
                    stmt.setInt(1, id);
                    stmt.setString(2, dvd.getDirector());
                    stmt.setString(3, dvd.getDuracion());
                    stmt.setString(4, dvd.getProductora());
                    stmt.executeUpdate();
                }
                break;
            case "pdf":
                PDF pdf = (PDF) doc;
                String sqlPDF = "INSERT INTO pdfs (id, tema, numero_paginas, autor_original) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sqlPDF)) {
                    stmt.setInt(1, id);
                    stmt.setString(2, pdf.getTema());
                    stmt.setInt(3, pdf.getNumeroPaginas());
                    stmt.setString(4, pdf.getAutorOriginal());
                    stmt.executeUpdate();
                }
                break;
            case "tesis":
                Tesis tesis = (Tesis) doc;
                String sqlTesis = "INSERT INTO tesis (id, carrera, universidad, asesor_academico) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sqlTesis)) {
                    stmt.setInt(1, id);
                    stmt.setString(2, tesis.getCarrera());
                    stmt.setString(3, tesis.getUniversidad());
                    stmt.setString(4, tesis.getAsesorAcademico());
                    stmt.executeUpdate();
                }
                break;
        }
    }
    public void eliminarDocumento(int id) throws SQLException {
        String sql = "DELETE FROM documentos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Documento> obtenerTodos() throws SQLException {
        List<Documento> lista = new ArrayList<>();

        String sql =
                "SELECT d.*, " +
                        "l.isbn, l.editorial AS editorial_libro, l.numero_paginas, " +
                        "r.numero AS numero_revista, r.mes, r.categoria, r.editorial AS editorial_revista, " +
                        "c.genero AS genero_cd, c.duracion AS duracion_cd, c.artista, " +
                        "dv.director, dv.duracion AS duracion_dvd, dv.productora, " +
                        "p.tema AS tema_pdf, p.numero_paginas AS paginas_pdf, p.autor_original, " +
                        "t.carrera, t.universidad, t.asesor_academico " +
                        "FROM documentos d " +
                        "LEFT JOIN libros l ON d.id = l.id " +
                        "LEFT JOIN revistas r ON d.id = r.id " +
                        "LEFT JOIN cds c ON d.id = c.id " +
                        "LEFT JOIN dvds dv ON d.id = dv.id " +
                        "LEFT JOIN pdfs p ON d.id = p.id " +
                        "LEFT JOIN tesis t ON d.id = t.id";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Documento doc = new Documento();
                doc.setId(rs.getInt("id"));
                doc.setTipo(rs.getString("tipo"));
                doc.setTitulo(rs.getString("titulo"));
                doc.setAutor(rs.getString("autor"));
                doc.setAnioPublicacion(rs.getInt("anio_publicacion"));
                doc.setUbicacionFisica(rs.getString("ubicacion_fisica")); // << NUEVA LÍNEA

                String tipo = doc.getTipo().toLowerCase();

                switch (tipo) {
                    case "libro":
                        doc.setEditorial(rs.getString("editorial_libro"));
                        doc.setNumeroPaginas(rs.getInt("numero_paginas"));
                        break;
                    case "revista":
                        doc.setNumeroRevista(rs.getInt("numero_revista"));
                        doc.setMes(rs.getString("mes"));
                        doc.setEditorial(rs.getString("editorial_revista"));
                        break;
                    case "cd":
                        doc.setGenero(rs.getString("genero_cd"));
                        doc.setDuracion(rs.getString("duracion_cd"));
                        doc.setTema(rs.getString("artista"));
                        break;
                    case "dvd":
                        doc.setTema(rs.getString("director"));
                        doc.setDuracion(rs.getString("duracion_dvd"));
                        doc.setUniversidad(rs.getString("productora"));
                        break;
                    case "pdf":
                        doc.setTema(rs.getString("tema_pdf"));
                        doc.setNumeroPaginas(rs.getInt("paginas_pdf"));
                        doc.setAutor(rs.getString("autor_original"));
                        break;
                    case "tesis":
                        doc.setCarrera(rs.getString("carrera"));
                        doc.setUniversidad(rs.getString("universidad"));
                        doc.setAsesorAcademico(rs.getString("asesor_academico"));
                        break;
                }

                lista.add(doc);
            }
        }
        return lista;
    }

            public List<String[]> obtenerDocumentosParaVista ()throws SQLException {
                List<String[]> documentos = new ArrayList<>();
                String sql =
                        "SELECT d.titulo, d.autor, d.anio_publicacion, d.tipo, " +
                                "l.editorial AS editorial_libro, l.numero_paginas, " +
                                "r.numero AS numero_revista, r.mes, r.categoria, r.editorial AS editorial_revista, " +
                                "c.genero AS genero_cd, c.duracion AS duracion_cd, c.artista, " +
                                "dv.director, dv.duracion AS duracion_dvd, dv.productora, " +
                                "t.carrera, t.universidad, t.asesor_academico " +
                                "FROM documentos d " +
                                "LEFT JOIN libros l ON d.id = l.id " +
                                "LEFT JOIN revistas r ON d.id = r.id " +
                                "LEFT JOIN cds c ON d.id = c.id " +
                                "LEFT JOIN dvds dv ON d.id = dv.id " +
                                "LEFT JOIN tesis t ON d.id = t.id";

                try (Connection conn = Conexion.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        String[] fila = new String[10];
                        fila[0] = rs.getString("titulo");
                        fila[1] = rs.getString("autor");
                        fila[2] = String.valueOf(rs.getInt("anio_publicacion"));
                        fila[3] = rs.getString("tipo");

                        String tipo = fila[3].toLowerCase();

                        // Rellenar según el tipo de documento
                        switch (tipo) {
                            case "libro":
                                fila[4] = rs.getString("editorial_libro");
                                fila[5] = String.valueOf(rs.getInt("numero_paginas"));
                                break;
                            case "revista":
                                fila[4] = rs.getString("editorial_revista");
                                fila[5] = String.valueOf(rs.getInt("numero_revista"));
                                fila[6] = rs.getString("mes");
                                fila[7] = rs.getString("categoria");
                                break;
                            case "cd":
                                fila[4] = rs.getString("genero_cd");
                                fila[5] = rs.getString("duracion_cd");
                                fila[6] = rs.getString("artista");
                                break;
                            case "dvd":
                                fila[4] = rs.getString("director");
                                fila[5] = rs.getString("duracion_dvd");
                                fila[6] = rs.getString("productora");
                                break;
                            case "tesis":
                                fila[4] = rs.getString("carrera");
                                fila[5] = rs.getString("universidad");
                                fila[6] = rs.getString("asesor_academico");
                                break;
                            default:
                                fila[4] = "";
                                fila[5] = "";
                                fila[6] = "";

                        }


                        documentos.add(fila);
                    }
                }

                return documentos;
            }
        }

