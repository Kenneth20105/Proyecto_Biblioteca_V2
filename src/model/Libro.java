
package model;

public class Libro extends Documento {
    private String isbn;
    private String editorial;
    private int numeroPaginas;

    public Libro(int id, String titulo, String autor, int anioPublicacion, String isbn, String editorial, int numeroPaginas) {
        super(id, titulo, autor, anioPublicacion, "libro");
        this.isbn = isbn;
        this.editorial = editorial;
        this.numeroPaginas = numeroPaginas;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public int getNumeroPaginas() { return numeroPaginas; }
    public void setNumeroPaginas(int numeroPaginas) { this.numeroPaginas = numeroPaginas; }
}
