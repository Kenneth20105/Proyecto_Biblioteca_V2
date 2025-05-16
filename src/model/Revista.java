
package model;

public class Revista extends Documento {
    private int numero;
    private String mes;
    private String categoria;
    private String editorial;

    public Revista(int id, String titulo, String autor, int anioPublicacion, int numero, String mes, String categoria, String editorial) {
        super(id, titulo, autor, anioPublicacion, "revista");
        this.numero = numero;
        this.mes = mes;
        this.categoria = categoria;
        this.editorial = editorial;
    }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
}
