package es.ujaen.dae.ujahotel.rest.dto;

import es.ujaen.dae.ujahotel.entidades.Usuario;
import es.ujaen.dae.ujahotel.rest.dto.*;
import es.ujaen.dae.ujahotel.entidades.Reserva;

import java.time.LocalDate;

public record DTOReserva(
        int id,
        LocalDate fechaIni,
        LocalDate fechaFin,
        int habsReservaSimple,
        int habsReservaDoble,
        String usrAsignado
) {

    public DTOReserva(Reserva reserva) {
        this(reserva.getId(), reserva.getFechaIni(), reserva.getFechaFin(), reserva.getHabsReservaSimple(), reserva.getHabsReservaDoble(), reserva.getUsrAsignado().getDni());
    }

    public Reserva aReserva(){
        return new Reserva(new Usuario(),this.fechaIni,this.fechaFin,this.habsReservaSimple,this.habsReservaDoble);
    }

}