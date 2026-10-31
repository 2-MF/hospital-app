package utu.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Intervencion {

    private int idIntervencion;
    private LocalDate fecha;
    private String descripcion;
    private BigDecimal costo;
    private Equipo equipo;
    private TipoIntervencion tipoIntervencion;
    private Usuario usuario;

    public Intervencion() {}

    public Intervencion(int idIntervencion, LocalDate fecha, String descripcion, BigDecimal costo,
                        Equipo equipo, TipoIntervencion tipoIntervencion, Usuario usuario) {
        this.idIntervencion = idIntervencion;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.costo = costo;
        this.equipo = equipo;
        this.tipoIntervencion = tipoIntervencion;
        this.usuario = usuario;
    }

    public int getIdIntervencion() {
        return idIntervencion;
    }
    public void setIdIntervencion(int idIntervencion) {
        this.idIntervencion = idIntervencion;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public BigDecimal getCosto() {
        return costo;
    }
    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }
    public Equipo getEquipo() {
        return equipo;
    }
    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }
    public TipoIntervencion getTipoIntervencion() {
        return tipoIntervencion;
    }
    public void setTipoIntervencion(TipoIntervencion tipoIntervencion) {
        this.tipoIntervencion = tipoIntervencion;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Intervencion{" +
                "idIntervencion=" + idIntervencion +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                ", costo=" + costo +
                ", equipo=" + equipo +
                ", tipoIntervencion=" + tipoIntervencion +
                ", usuario=" + usuario +
                '}';
    }
}
