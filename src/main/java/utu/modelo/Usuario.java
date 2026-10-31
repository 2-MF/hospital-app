package utu.modelo;

import java.time.LocalDate;

public class Usuario {

    private int idUsuario;
    private String nombre;
    private String apellido;
    private String cedula;
    private String telefono;
    private String correo;
    private String usuario;
    private String contrasena;

    private LocalDate fechaAlta;
    private LocalDate ultimoAcceso;

    private boolean activo;

    private Perfil perfil;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombre, String apellido, String cedula, String telefono, String correo, String usuario, String contrasena, LocalDate fechaAlta, LocalDate ultimoAcceso, boolean activo, Perfil perfil) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.telefono = telefono;
        this.correo = correo;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.fechaAlta = fechaAlta;
        this.ultimoAcceso = ultimoAcceso;
        this.activo = activo;
        this.perfil = perfil;
    }
    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getCedula() {
        return cedula;
    }
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;   
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario; 
    }
    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;   
    }
    public LocalDate getFechaAlta() {
        return fechaAlta;
    }
    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta; 
    }
    public LocalDate getUltimoAcceso() {
        return ultimoAcceso;
    }
    public void setUltimoAcceso(LocalDate ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;   
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;   
    }
    public Perfil getPerfil() {
        return perfil;
    }
    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;   
    }
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", cedula='" + cedula + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", usuario='" + usuario + '\'' +
                ", fechaAlta=" + fechaAlta +
                ", ultimoAcceso=" + ultimoAcceso +
                ", activo=" + activo +
                ", perfil=" + perfil +
                '}';
    }
    
}
