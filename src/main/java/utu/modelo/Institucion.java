package utu.modelo;

public class Institucion {

    private int idInstitucion;
    private String nombre;
    private String direccion;
    private String telefono;
    private boolean activo;
    private Pais pais;

    public Institucion() {}

    public Institucion(int idInstitucion, String nombre, String direccion, String telefono, boolean activo, Pais pais) {
        this.idInstitucion = idInstitucion;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.activo = activo;
        this.pais = pais;
    }

    public int getIdInstitucion() {
        return idInstitucion;
    }
    public void setIdInstitucion(int idInstitucion) {
        this.idInstitucion = idInstitucion;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    public Pais getPais() {
        return pais;
    }
    public void setPais(Pais pais) {
        this.pais = pais;
    }

    @Override
    public String toString() {
        return "Institucion{" +
                "idInstitucion=" + idInstitucion +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", activo=" + activo +
                ", pais=" + pais +
                '}';
    }
}
