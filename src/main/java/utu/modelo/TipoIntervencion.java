package utu.modelo;

public class TipoIntervencion {

    private int idTipoIntervencion;
    private String nombre;
    private String descripcion;

    public TipoIntervencion() {}

    public TipoIntervencion(int idTipoIntervencion, String nombre, String descripcion) {
        this.idTipoIntervencion = idTipoIntervencion;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdTipoIntervencion() {
        return idTipoIntervencion;
    }
    public void setIdTipoIntervencion(int idTipoIntervencion) {
        this.idTipoIntervencion = idTipoIntervencion;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "TipoIntervencion{" +
                "idTipoIntervencion=" + idTipoIntervencion +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
