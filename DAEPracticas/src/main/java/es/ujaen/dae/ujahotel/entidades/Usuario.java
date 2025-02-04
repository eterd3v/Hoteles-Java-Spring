/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.ujaen.dae.ujahotel.entidades;

import es.ujaen.dae.ujahotel.utils.Codificador;
import es.ujaen.dae.ujahotel.utils.ExprReg;
import es.ujaen.dae.ujahotel.utils.Rol;

import es.ujaen.dae.ujahotel.rest.dto.DTOUsuario;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Usuario  {
    
    @Id
    @NotNull
    @Size(min=9, max=9)
    @Pattern(regexp=ExprReg.DNI)
    private String dni;

    @Email
    private String correo;

    @NotBlank
    private String nombre;

    @NotNull
    private String clave;

    @NotBlank
    private String direccion;

    @NotNull
    @Size(min=9, max=13)
    @Pattern(regexp=ExprReg.TLF)
    private String telefono;

    @NotNull
    private Rol rol;

    
    public Usuario(){
        this.dni = "12345678A";
        this.correo = "uncorreo@gmail.com";
        this.nombre = "Usuario Defecto";
        this.clave = "user";
        this.direccion = "Una Direccion";
        this.telefono = "000000000";
        this.rol = Rol.CLIENTE;
    }
    
    
    public Usuario(String dni, String correo, String nombre, String clave,
                    String direccion, String telefono, Rol rol){

        this.dni = dni;
        this.correo = correo;
        this.nombre = nombre;
        this.clave = (clave != null ? Codificador.codificar(clave) : null);
        this.direccion = direccion;
        this.telefono = telefono;
        this.rol = rol;
    }
    
    /*
    public Usuario(Usuario orig){
        this.correo = orig.correo;
        this.nombre = orig.nombre;
        this.contrasenia = orig.contrasenia;
        this.direccion = orig.direccion;
        this.telefono = orig.telefono;
        this.rol = orig.rol;
    }
    */
    public String getDni(){ return dni; }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
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

    public Rol getRol() {
        return rol;
    }
    
    public boolean claveValida(String unaClave){
        return Codificador.igual(unaClave, clave);
        //return clave.equals(CodificadorMd5.codificar(unaClave));
    }

    /*
    public void setRol(Rol rol) {
        this.rol = rol;
    }
    */    
}
