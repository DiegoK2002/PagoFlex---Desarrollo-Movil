package cl.pagoflex.reportes.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReporte;

    @Column(nullable = false)
    private Long idUsuario;

    private Long idPago;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 150)
    private String asunto;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(length = 2000)
    private String respuesta;

    private Long idAgente;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @Column(nullable = false)
    private Boolean activo = true;

    public Reporte() {
    }

    @PrePersist
    public void antesDeGuardar() {
        fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    public void antesDeActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }

    public Long getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Long idReporte) {
        this.idReporte = idReporte;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public Long getIdAgente() {
        return idAgente;
    }

    public void setIdAgente(Long idAgente) {
        this.idAgente = idAgente;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
