
package model;

public class Tesis extends Documento {
    private String carrera;
    private String universidad;
    private String asesorAcademico;

    public Tesis(int id, String titulo, String autor, int anioPublicacion, String carrera, String universidad, String asesorAcademico) {
        super(id, titulo, autor, anioPublicacion, "tesis");
        this.carrera = carrera;
        this.universidad = universidad;
        this.asesorAcademico = asesorAcademico;
    }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getUniversidad() { return universidad; }
    public void setUniversidad(String universidad) { this.universidad = universidad; }

    public String getAsesorAcademico() { return asesorAcademico; }
    public void setAsesorAcademico(String asesorAcademico) { this.asesorAcademico = asesorAcademico; }
}
