package es.ujaen.dae.ujahotel.rest.dto;

import es.ujaen.dae.ujahotel.rest.dto.*;
import es.ujaen.dae.ujahotel.entidades.Hotel;
import es.ujaen.dae.ujahotel.utils.Direccion;

public record DTOHotel(
        String cif,
        String nombre,
        String pais,
        String ciudad,
        String calle,
        int habsHtlSimple,
        int habsHtlDoble) {

    public DTOHotel(Hotel hotel ){
        this(hotel.getCif(), hotel.getNombre(), hotel.getDireccion().getPais(), hotel.getDireccion().getCiudad(), hotel.getDireccion().getCalle(), hotel.getHabsHtlSimple(), hotel.getHabsHtlDoble());
    }
    
    public Hotel aHotel(){
        Direccion dir = new Direccion( pais,  ciudad,  calle);
        return new Hotel( cif,  nombre,  dir,  habsHtlSimple,  habsHtlDoble);
    }

}
