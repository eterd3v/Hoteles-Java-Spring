/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.ujaen.dae.ujahotel.rest;
import es.ujaen.dae.ujahotel.entidades.Reserva;
import es.ujaen.dae.ujahotel.excepciones.*;
import es.ujaen.dae.ujahotel.rest.dto.DTOReserva;
import es.ujaen.dae.ujahotel.rest.dto.DTOUsuario;
import es.ujaen.dae.ujahotel.servicios.GestorHoteles;
import es.ujaen.dae.ujahotel.entidades.Hotel;
import jdk.jfr.Unsigned;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import es.ujaen.dae.ujahotel.entidades.Usuario;
import es.ujaen.dae.ujahotel.rest.dto.DTOHotel;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
/**
 *
 * @author pc
 */

@RestController
@RequestMapping("/ujahotel")
public class ControladorREST {
    
    @Autowired
    GestorHoteles servicios;


    @ExceptionHandler(UsuarioNoRegistrado.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handlerUsuarioNoRegistrado(UsuarioNoRegistrado e){}
    
    
    //Creación de usuarios
    @PostMapping("/usuarios")
    ResponseEntity<Void> altaUsuario(@RequestBody DTOUsuario usuario){
        try{
            servicios.altaUsuario(usuario.aUsuario());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (UsuarioYaRegistrado e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Login de clientes (temporal hasta incluir autenticación mediante Spring Security
     */
    /*
    @GetMapping("/usuarios/{dni}")
    ResponseEntity<DTOUsuario> verUsuario(@PathVariable String dni){
        Optional<Usuario> usr = servicios.verUsuario(dni);

        if(usr.isEmpty())    //El usuario no está en el sistema
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        DTOUsuario dtoUsuario = new DTOUsuario(usr.get());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(dtoUsuario);
    }*/
    
    
    @GetMapping("/usuarios/{dni}")
    ResponseEntity<DTOUsuario> verCliente(@PathVariable String dni) {
        Optional<Usuario> usuario = servicios.verUsuario(dni);
        return usuario
                .map(c -> ResponseEntity.ok(new DTOUsuario(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    
    //Administrador  - Alta Hotel
    @PostMapping("/usuarios/{dni}/hoteles")
    ResponseEntity<Void> altaHotel(@PathVariable String dni, @RequestBody DTOHotel hotel){
        try{
            servicios.altaHotel(dni, hotel.aHotel());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch(HotelYaCreado e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }catch(AdministradorInvalido e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    @GetMapping("/hoteles")
    ResponseEntity<List<DTOHotel> > consultaHoteles(
        @RequestParam(required = true) String pais,
        @RequestParam(required = true) String ciudad,
        @RequestParam(required = true) String desde,
        @RequestParam(required = true) String hasta,
        @RequestParam(required = false, defaultValue = "0") @Positive int nSimples,
        @RequestParam(required = false, defaultValue = "1") @Positive int nDobles,
        @RequestParam(required = false, defaultValue = "1") @Positive int pag,
        @RequestParam(required = false, defaultValue = "10") @Positive int num)
    {
        
        LocalDate fechaIni;
        LocalDate fechaFin;
        try {
            fechaIni = desde != null ? LocalDate.parse(desde) : null;
            fechaFin = hasta != null ? LocalDate.parse(hasta) : null;
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Hotel> hotelesDisponibles = servicios.consultaHoteles(pais, 
                                                                    ciudad, 
                                                                    fechaIni, 
                                                                    fechaFin, 
                                                                    nSimples, 
                                                                    nDobles);
        
        return ResponseEntity.ok(hotelesDisponibles.stream()
                .skip((pag - 1) * num)
                .limit(num)
                .map(m -> new DTOHotel(m))
                .toList());

    }

    @PostMapping("/usuarios/{dni}/hoteles/{cif}/reserva")
    ResponseEntity<DTOReserva> reservar(@PathVariable String dni,
                                        @PathVariable String cif,
                                        @RequestBody DTOReserva reserva){

        try{
            int idReserva = servicios.reservar(cif,dni,reserva.aReserva());
            return ResponseEntity.status(HttpStatus.CREATED).body(
                new DTOReserva(idReserva,reserva.fechaIni(),reserva.fechaFin(),reserva.habsReservaSimple(),reserva.habsReservaDoble(),dni));
        }catch (UsuarioNoRegistrado u){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }catch (HotelNoEncontrado h){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }
    
}
