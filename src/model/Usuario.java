package model;

public class Usuario {
    private int id;
    private int mora;
    private String nombre;
    private String carnet;
    private String password;
    private String rol;

    // Constructor completo con ID (usado al validar credenciales o cargar usuarios)
    public Usuario(int id, String nombre, String carnet, String password, String rol, int mora) {
        this.id = id;
        this.nombre = nombre;
        this.carnet = carnet;
        this.password = password;
        this.rol = rol;
        this.mora = mora;
    }

    // Constructor sin ID (usado para agregar nuevos usuarios)
    public Usuario(String nombre, String carnet, String password, String rol) {
        this.nombre = nombre;
        this.carnet = carnet;
        this.password = password;
        this.rol = rol;
    }
    public Usuario(int id, String nombre, String carnet, String password, String rol) {
        this(id, nombre, carnet, password, rol, 0);
    }


    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCarnet() { return carnet; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }
    public int getMora() { return mora; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCarnet(String carnet) { this.carnet = carnet; }
    public void setPassword(String password) { this.password = password; }
    public void setRol(String rol) { this.rol = rol; }
    public void setMora(int mora) { this.mora = mora;}



    @Override
    public String toString() {
        return nombre + " (" + rol + ")";
    }
}