package utu.modelo;

public class Marca {
    private int idMarca;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public Marca(){}

    public Marca(int idMarca, String nombre, String descripcion, boolean activo) {
        this.idMarca = idMarca;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }
    public int getIdMarca() {
        return idMarca;
    }
    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
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
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    @Override
    public String toString() {
        return "Marca [idMarca=" + idMarca + ", nombre=" + nombre + ", descripcion=" + descripcion + ", activo="
                + activo + "]";
    }
}
