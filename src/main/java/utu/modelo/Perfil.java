package utu.modelo;

public class Perfil {
    private int idPerfil;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public Perfil(){}

    public Perfil(int idPerfil, String nombre, String descripcion, boolean activo) {
        this.idPerfil = idPerfil;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }
    public int getIdPerfil() {
        return idPerfil;
    }
    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
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
        return "Perfil{" +
                "idPerfil=" + idPerfil +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                '}';
    }
}
