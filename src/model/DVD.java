
package model;

public class DVD extends Documento {
    private String director;
    private String duracion;
    private String productora;

    public DVD(int id, String titulo, String autor, int anioPublicacion, String director, String duracion, String productora) {
        super(id, titulo, autor, anioPublicacion, "dvd");
        this.director = director;
        this.duracion = duracion;
        this.productora = productora;
    }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public String getProductora() { return productora; }
    public void setProductora(String productora) { this.productora = productora; }
}
