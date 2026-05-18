package model;

import java.time.LocalDate;

public class Cliente extends Usuario {
    private String objetivoFitness;
    private double pesoInicial;
    private int altura;
    private LocalDate fechaNacimiento;
    private String genero;

    public Cliente() { super(); }

    public Cliente(int id, String username, String password, String email, String nombre, String apellidos, 
                   String dni, String rol, String objetivoFitness, double pesoInicial, 
                   int altura, LocalDate fechaNacimiento, String genero) {
        super(id, username, password, email, nombre, apellidos, dni, rol);
        this.objetivoFitness = objetivoFitness;
        this.pesoInicial = pesoInicial;
        this.altura = altura;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
    }

    // Getters y Setters
    public String getObjetivoFitness() { return objetivoFitness; }
    public void setObjetivoFitness(String objetivoFitness) { this.objetivoFitness = objetivoFitness; }
    
    public double getPesoInicial() { return pesoInicial; }
    public void setPesoInicial(double pesoInicial) { this.pesoInicial = pesoInicial; }

    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
}
