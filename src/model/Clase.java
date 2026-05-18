package model;

public class Clase {
    private int id;
    private String nombre;
    private String descripcion;
    private int aforoMax;

    public Clase() {}

    public Clase(int id, String nombre, String descripcion, int aforoMax) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.aforoMax = aforoMax;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getAforoMax() { return aforoMax; }
    public void setAforoMax(int aforoMax) { this.aforoMax = aforoMax; }

    @Override
    public String toString() { return nombre + "  (aforo máx: " + aforoMax + ")"; }
}