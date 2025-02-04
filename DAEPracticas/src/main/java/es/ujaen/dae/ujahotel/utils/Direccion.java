/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.ujaen.dae.ujahotel.utils;

import javax.validation.constraints.NotEmpty;

/**
 *
 * @author pc
 */
public class Direccion {
    
    @NotEmpty
    public String pais;
    
    @NotEmpty
    public String ciudad;
    
    @NotEmpty
    public String calle;

    public Direccion() {
        pais=ciudad=calle="";
    }

    public Direccion(String pais, String ciudad, String calle) {
        this.pais = pais;
        this.ciudad = ciudad;
        this.calle = calle;
    }

    public String getPais() { return pais; }

    public String getCiudad() { return ciudad; }

    public String getCalle() { return calle; }
}
