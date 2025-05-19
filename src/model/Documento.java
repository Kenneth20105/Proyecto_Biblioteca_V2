package model;

public class Documento {
    private int id;
    private String tipo;
    private String titulo;
    private String autor;
    private int anioPublicacion;

    // Libros
    private String editorial;
    private int numeroPaginas;

    // Revistas
    private int numeroRevista;
    private String mes;

    // CD/DVD
    private String genero;
    private String duracion;

    // CD específico
    private String artista;

    // DVD específico
    private String director;
    private String productora;

    // Tesis
    private String carrera;
    private String universidad;
    private String asesorAcademico;

    // PDFs
    private String formato;
    private String tema; // añadido para PDF

    private String ubicacionFisica;

    //Constructores
    public Documento() {}
    public Documento(int id, String titulo, String autor, int anioPublicacion, String tipo) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.anioPublicacion = anioPublicacion;
        this.tipo = tipo;
    }

    // Getters y Setters generales
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }

    // Libros
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public int getNumeroPaginas() { return numeroPaginas; }
    public void setNumeroPaginas(int numeroPaginas) { this.numeroPaginas = numeroPaginas; }

    // Revistas
    public int getNumeroRevista() { return numeroRevista; }
    public void setNumeroRevista(int numeroRevista) { this.numeroRevista = numeroRevista; }

    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }

    // CD/DVD
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    // CD
    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    // DVD
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getProductora() { return productora; }
    public void setProductora(String productora) { this.productora = productora; }

    // Tesis
    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getUniversidad() { return universidad; }
    public void setUniversidad(String universidad) { this.universidad = universidad; }

    public String getAsesorAcademico() { return asesorAcademico; }
    public void setAsesorAcademico(String asesorAcademico) { this.asesorAcademico = asesorAcademico; }

    // PDFs
    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    //Ubicacion fisica
    public String getUbicacionFisica() {return ubicacionFisica;}
    public void setUbicacionFisica(String ubicacionFisica) {this.ubicacionFisica = ubicacionFisica;}

    @Override
    public String toString() {
        return "Documento{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", anioPublicacion=" + anioPublicacion +
                ", editorial='" + editorial + '\'' +
                ", numeroPaginas=" + numeroPaginas +
                ", numeroRevista=" + numeroRevista +
                ", mes='" + mes + '\'' +
                ", genero='" + genero + '\'' +
                ", duracion='" + duracion + '\'' +
                ", artista='" + artista + '\'' +
                ", director='" + director + '\'' +
                ", productora='" + productora + '\'' +
                ", carrera='" + carrera + '\'' +
                ", universidad='" + universidad + '\'' +
                ", asesorAcademico='" + asesorAcademico + '\'' +
                ", formato='" + formato + '\'' +
                ", tema='" + tema + '\'' +
                '}';
    }
}