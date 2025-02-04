/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.ujaen.dae.ujahotel.rest.dto;

import es.ujaen.dae.ujahotel.entidades.Usuario;
import es.ujaen.dae.ujahotel.utils.Rol;

/**
 *
 * @author pc
 */
public record DTOUsuario (    
    String dni,
    String correo,
    String nombre,
    String direccion,
    String tlf,
    String rol,
    String clave
){  
    public DTOUsuario(Usuario user){
        this(user.getDni(),
            user.getCorreo(),
            user.getNombre(),
            user.getDireccion(),
            user.getTelefono(),
            user.getRol().toString(),
            "");
    }
    
    public Usuario aUsuario() {
        Rol _rol;
        if(rol.equals("ADMIN"))
            _rol = Rol.ADMIN;
        else
            _rol = Rol.CLIENTE;
        
        return new Usuario( dni, correo,  nombre,  clave, direccion,  tlf,  _rol);
    }
}
