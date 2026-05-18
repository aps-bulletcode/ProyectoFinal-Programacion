package dto;

import java.time.LocalDate;

public class ClienteDetalleDTO {
    private int id;
    private String nombreCompleto;
    private String dni;
    private String objetivo;
    private String ultimaClase;
    private double peso;
    private int altura;
    private String genero;
    private LocalDate fechaNacimiento;

    public ClienteDetalleDTO() {}

    public ClienteDetalleDTO(int id, String nombreCompleto, String dni, String objetivo, String ultimaClase, double peso, int altura, String genero, LocalDate fechaNacimiento) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.objetivo = objetivo;
        this.ultimaClase = ultimaClase;
        this.peso = peso;
        this.altura = altura;
        this.genero = genero;
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getUltimaClase() { return ultimaClase; }
    public void setUltimaClase(String ultimaClase) { this.ultimaClase = ultimaClase; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
}
