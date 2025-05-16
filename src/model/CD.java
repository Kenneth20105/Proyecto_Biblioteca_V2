
package model;

public class CD extends Documento {
    private String genero;
    private String duracion;
    private String artista;

    public CD(int id, String titulo, String autor, int anioPublicacion, String genero, String duracion, String artista) {
        super(id, titulo, autor, anioPublicacion, "cd");
        this.genero = genero;
        this.duracion = duracion;
        this.artista = artista;
    }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }
}
