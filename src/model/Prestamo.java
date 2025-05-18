package model;

import java.time.LocalDate;


public class Prestamo {
    private int id;
    private int idUsuario;
    private String nombreUsuario;
    private String carnetUsuario;
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

    public Prestamo() {
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUsuario() {return idUsuario;}

    public void setIdUsuario(int idUsuario) {this.idUsuario = idUsuario;}
    public void setIdDocumento(int idDocumento) {this.idDocumento = idDocumento;}


    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }


    public boolean isDevuelto() { return devuelto; }
    public void setDevuelto(boolean devuelto) { this.devuelto = devuelto; }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {this.fechaPrestamo = fechaPrestamo;}
    public void setFechaDevolucion(LocalDate fechaDevolucion) {this.fechaDevolucion = fechaDevolucion;}

    public double getMontoMora() {return montoMora;}
    public void setMontoMora(double montoMora) {this.montoMora = montoMora;}

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getCarnetUsuario() {return carnetUsuario;}
    public void setCarnetUsuario(String carnetUsuario) {this.carnetUsuario = carnetUsuario;}

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