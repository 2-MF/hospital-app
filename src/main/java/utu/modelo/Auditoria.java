package utu.modelo;

import java.time.LocalDateTime;

public class Auditoria {

    private int idAuditoria;
    private LocalDateTime fecha;
    private String tablaAfectada;
    private String operacion;
    private String descripcion;
    private Usuario usuario;

    public Auditoria() {}

    public Auditoria(int idAuditoria, LocalDateTime fecha, String tablaAfectada, String operacion,
                     String descripcion, Usuario usuario) {
        this.idAuditoria = idAuditoria;
        this.fecha = fecha;
        this.tablaAfectada = tablaAfectada;
        this.operacion = operacion;
        this.descripcion = descripcion;
        this.usuario = usuario;
    }

    public int getIdAuditoria() {
        return idAuditoria;
    }
    public void setIdAuditoria(int idAuditoria) {
        this.idAuditoria = idAuditoria;
    }
    public LocalDateTime getFecha() {
        return fecha;
    }
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    public String getTablaAfectada() {
        return tablaAfectada;
    }
    public void setTablaAfectada(String tablaAfectada) {
        this.tablaAfectada = tablaAfectada;
    }
    public String getOperacion() {
        return operacion;
    }
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Auditoria{" +
                "idAuditoria=" + idAuditoria +
                ", fecha=" + fecha +
                ", tablaAfectada='" + tablaAfectada + '\'' +
                ", operacion='" + operacion + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", usuario=" + usuario +
                '}';
    }
}
