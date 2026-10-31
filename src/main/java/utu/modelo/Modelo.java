package utu.modelo;

public class Modelo {

    private int idModelo;
    private String nombre;
    private boolean activo;
    private Marca marca;

    public Modelo() {}

    public Modelo(int idModelo, String nombre, boolean activo, Marca marca) {
        this.idModelo = idModelo;
        this.nombre = nombre;
        this.activo = activo;
        this.marca = marca;
    }

    public int getIdModelo() {
        return idModelo;
    }
    public void setIdModelo(int idModelo) {
        this.idModelo = idModelo;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    public Marca getMarca() {
        return marca;
    }
    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    @Override
    public String toString() {
        return "Modelo{" +
                "idModelo=" + idModelo +
                ", nombre='" + nombre + '\'' +
                ", activo=" + activo +
                ", marca=" + marca +
                '}';
    }
}
