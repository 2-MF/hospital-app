package utu.modelo;

public class Funcionalidad {

    private int idFuncionalidad;
    private String nombre;
    private String descripcion;

    public Funcionalidad() {}

    public Funcionalidad(int idFuncionalidad, String nombre, String descripcion) {
        this.idFuncionalidad = idFuncionalidad;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdFuncionalidad() {
        return idFuncionalidad;
    }
    public void setIdFuncionalidad(int idFuncionalidad) {
        this.idFuncionalidad = idFuncionalidad;
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
        return "Funcionalidad{" +
                "idFuncionalidad=" + idFuncionalidad +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
