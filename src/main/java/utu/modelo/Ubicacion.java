package utu.modelo;

public class Ubicacion {

    private int idUbicacion;
    private String nombre;
    private String descripcion;
    private Institucion institucion;

    public Ubicacion() {}

    public Ubicacion(int idUbicacion, String nombre, String descripcion, Institucion institucion) {
        this.idUbicacion = idUbicacion;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.institucion = institucion;
    }

    public int getIdUbicacion() {
        return idUbicacion;
    }
    public void setIdUbicacion(int idUbicacion) {
        this.idUbicacion = idUbicacion;
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
    public Institucion getInstitucion() {
        return institucion;
    }
    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
    }

    @Override
    public String toString() {
        return "Ubicacion{" +
                "idUbicacion=" + idUbicacion +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", institucion=" + institucion +
                '}';
    }
}
