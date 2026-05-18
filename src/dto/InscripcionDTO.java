package dto;

public class InscripcionDTO {
    private int clienteId;
    private String nombreCliente;
    private int claseId;
    private String nombreClase;
    private String fechaInscripcion;

    public InscripcionDTO() {}

    public InscripcionDTO(int clienteId, String nombreCliente, int claseId, String nombreClase, String fechaInscripcion) {
        this.clienteId = clienteId;
        this.nombreCliente = nombreCliente;
        this.claseId = claseId;
        this.nombreClase = nombreClase;
        this.fechaInscripcion = fechaInscripcion;
    }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public int getClaseId() { return claseId; }
    public void setClaseId(int claseId) { this.claseId = claseId; }
    public String getNombreClase() { return nombreClase; }
    public void setNombreClase(String nombreClase) { this.nombreClase = nombreClase; }
    public String getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(String fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
}
