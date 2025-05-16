package model;

import java.time.LocalDate;


public class Prestamo {
    private int id;
    private int idUsuario;
    private String nombreUsuario;
    private int idDocumento;
    private String nombreDocumento;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private double montoMora;
    private boolean devuelto;

    public Prestamo(int id, int idUsuario, int idDocumento, LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean devuelto) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idDocumento = idDocumento;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    public Prestamo(int id, int idUsuario, int idDocumento, LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean devuelto, double montoMora) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idDocumento = idDocumento;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
        this.montoMora = montoMora;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUsuario() { return idUsuario; }
    public int getIdDocumento() { return idDocumento; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }

    public boolean isDevuelto() { return devuelto; }
    public void setDevuelto(boolean devuelto) { this.devuelto = devuelto; }

    public double getMontoMora() {return montoMora;}
    public void setMontoMora(double montoMora) {this.montoMora = montoMora;}

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getNombreDocumento() { return nombreDocumento; }
    public void setNombreDocumento(String nombreDocumento) { this.nombreDocumento = nombreDocumento; }

    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", idUsuario=" + idUsuario +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", idDocumento=" + idDocumento +
                ", nombreDocumento='" + nombreDocumento + '\'' +
                ", fechaPrestamo=" + fechaPrestamo +
                ", fechaDevolucion=" + fechaDevolucion +
                ", devuelto=" + devuelto +
                '}';
    }
}