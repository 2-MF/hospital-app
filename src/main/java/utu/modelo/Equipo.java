package utu.modelo;

import java.time.LocalDate;

public class Equipo {

    private int idEquipo;
    private String codigo;
    private String nombre;
    private String serie;
    private LocalDate fechaCompra;
    private LocalDate fechaGarantia;
    private String estado;
    private String observaciones;
    private Modelo modelo;
    private TipoEquipo tipo;
    private Proveedor proveedor;
    private Ubicacion ubicacion;

    public Equipo() {}

    public Equipo(int idEquipo, String codigo, String nombre, String serie, LocalDate fechaCompra,
                  LocalDate fechaGarantia, String estado, String observaciones, Modelo modelo,
                  TipoEquipo tipo, Proveedor proveedor, Ubicacion ubicacion) {
        this.idEquipo = idEquipo;
        this.codigo = codigo;
        this.nombre = nombre;
        this.serie = serie;
        this.fechaCompra = fechaCompra;
        this.fechaGarantia = fechaGarantia;
        this.estado = estado;
        this.observaciones = observaciones;
        this.modelo = modelo;
        this.tipo = tipo;
        this.proveedor = proveedor;
        this.ubicacion = ubicacion;
    }

    public int getIdEquipo() {
        return idEquipo;
    }
    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getSerie() {
        return serie;
    }
    public void setSerie(String serie) {
        this.serie = serie;
    }
    public LocalDate getFechaCompra() {
        return fechaCompra;
    }
    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
    public LocalDate getFechaGarantia() {
        return fechaGarantia;
    }
    public void setFechaGarantia(LocalDate fechaGarantia) {
        this.fechaGarantia = fechaGarantia;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public Modelo getModelo() {
        return modelo;
    }
    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }
    public TipoEquipo getTipo() {
        return tipo;
    }
    public void setTipo(TipoEquipo tipo) {
        this.tipo = tipo;
    }
    public Proveedor getProveedor() {
        return proveedor;
    }
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
    public Ubicacion getUbicacion() {
        return ubicacion;
    }
    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public String toString() {
        return "Equipo{" +
                "idEquipo=" + idEquipo +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", serie='" + serie + '\'' +
                ", fechaCompra=" + fechaCompra +
                ", fechaGarantia=" + fechaGarantia +
                ", estado='" + estado + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", modelo=" + modelo +
                ", tipo=" + tipo +
                ", proveedor=" + proveedor +
                ", ubicacion=" + ubicacion +
                '}';
    }
}
