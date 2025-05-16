
package model;

public class PDF extends Documento {
    private String tema;
    private int numeroPaginas;
    private String autorOriginal;

    public PDF(int id, String titulo, String autor, int anioPublicacion, String tema, int numeroPaginas, String autorOriginal) {
        super(id, titulo, autor, anioPublicacion, "pdf");
        this.tema = tema;
        this.numeroPaginas = numeroPaginas;
        this.autorOriginal = autorOriginal;
    }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public int getNumeroPaginas() { return numeroPaginas; }
    public void setNumeroPaginas(int numeroPaginas) { this.numeroPaginas = numeroPaginas; }

    public String getAutorOriginal() { return autorOriginal; }
    public void setAutorOriginal(String autorOriginal) { this.autorOriginal = autorOriginal; }
}
