package es.ujaen.dae.ujahotel.entidades;

import es.ujaen.dae.ujahotel.excepciones.HotelFechasIncorrectas;
import es.ujaen.dae.ujahotel.excepciones.HabitacionesLlenas;
import es.ujaen.dae.ujahotel.excepciones.ReservaNoEncontrada;
import es.ujaen.dae.ujahotel.excepciones.HabitacionNoEncontrada;
import es.ujaen.dae.ujahotel.excepciones.HotelCreacionReservaOcupada;

import java.util.*;

import es.ujaen.dae.ujahotel.utils.Direccion;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import es.ujaen.dae.ujahotel.utils.Pair;
import es.ujaen.dae.ujahotel.utils.Tipo;
import es.ujaen.dae.ujahotel.utils.Normalizer;

import java.time.LocalDate;



@Entity
public class Hotel {

    @Id
    @NotEmpty
    private String cif;

    @NotEmpty
    private String nombre; ///El nombre del hotel

    @Embedded
    private Direccion direccion; ///La dirección del hotel
    
    @NotEmpty
    private String paisNormaliced; ///Atributo normalizado de la dirección para la busqueda con el repositorio
    
    @NotEmpty
    private String ciudadNormaliced; ///Atributo normalizado de la dirección para la busqueda con el repositorio

    //@OneToMany(cascade = CascadeType.ALL)
    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Map<Integer,Reserva> misReservas;

    @OneToMany(fetch = FetchType.LAZY)
    @NotNull
    private List<Habitacion> misHabitaciones;

    @PositiveOrZero
    private int habsHtlSimple; //Número de habitaciones simples del hotel

    @PositiveOrZero
    private int habsHtlDoble; //Número de habitaciones dobles del hotel



    public Hotel(){

        this.cif = "H00000000";
        this.nombre = "Hotel Defecto";
        this.direccion = new Direccion("Un Pais", "Una Ciudad", "Una calle");
        this.misReservas = new HashMap<>();
        this.habsHtlSimple = 15;
        this.habsHtlDoble = 15;
        this.misHabitaciones = new ArrayList<>();
        
        this.ciudadNormaliced = Normalizer.normalize(direccion.ciudad);
        this.paisNormaliced = Normalizer.normalize(direccion.pais); 
    }



    public Hotel(String cif, String nombre, Direccion direccion, int habsHtlSimple, int habsHtlDoble){
        this.cif = cif;
        this.nombre = nombre;
        this.direccion = direccion;
        this.misReservas = new HashMap<>();
        this.habsHtlSimple = habsHtlSimple;
        this.habsHtlDoble = habsHtlDoble;
        this.misHabitaciones = new ArrayList<>();
        
        this.ciudadNormaliced = Normalizer.normalize(direccion.ciudad);
        this.paisNormaliced = Normalizer.normalize(direccion.pais);
    }

    /**
     * @brief Método que cuenta las habitaciones disponibles en el intervalo de días escogido
     * @param fechaIni es la fecha de inicio del intervalo de la disponibilidad
     * @param fechaFin es la fecha de fin del intervalo de la disponibilidad
     * @param tipo es el tipo de habitación que se quiere contabilizar
     * @return Devuelve la cantidad como entero de habitaciones libres en ese dia
     */
    public int getHabitacionesLibres(@NotEmpty LocalDate fechaIni, @NotEmpty LocalDate fechaFin, @NotEmpty Tipo tipo){
        if(fechaIni.isAfter(fechaFin)){
            throw new HotelFechasIncorrectas();
        }
        int cantidad = 0;
        for (Reserva r : misReservas.values()){
            if( (r.getFechaIni().isBefore(fechaFin) || r.getFechaIni().equals(fechaFin)) || // El inicio de la reserva entra en el intervalo
                (r.getFechaIni().isBefore(fechaIni) && r.getFechaFin().isAfter(fechaFin) ) || // La reserva contiene a fIni y fFin
                (r.getFechaFin().isAfter(fechaIni) || r.getFechaFin().equals(fechaIni))){   // El fin de la reserva entra en el intervalo

                if(tipo.equals(Tipo.SIMPLE)){
                    cantidad += r.getHabsReservaSimple();
                }else{
                    cantidad += r.getHabsReservaDoble();
                }
            }
        }
        if(tipo.equals(Tipo.SIMPLE)){
            return habsHtlSimple - cantidad;
        }
        return habsHtlDoble - cantidad;
    }


    /**
     * @brief Método que cuenta las habitaciones disponibles en el intervalo de días escogido
     * @param fechaIni es la fecha de inicio del intervalo de la disponibilidad
     * @param fechaFin es la fecha de fin del intervalo de la disponibilidad
     * @return Devuelve un Pair con el número de habitaciones simples (first) y dobles (second) libres.
     */
    public Pair<Integer, Integer> getHabitacionesLibresTotales(@NotEmpty LocalDate fechaIni, 
            @NotEmpty LocalDate fechaFin){
        if(fechaIni.isAfter(fechaFin)){
            throw new HotelFechasIncorrectas();
        }
        int cantidadSimples = 0, cantidadDobles = 0;
        for (Reserva r : misReservas.values()){
            if( (r.getFechaIni().isBefore(fechaFin) || r.getFechaIni().equals(fechaFin)) || // El inicio de la reserva entra en el intervalo
                    (r.getFechaIni().isBefore(fechaIni) && r.getFechaFin().isAfter(fechaFin) ) || // La reserva contiene a fIni y fFin
                    (r.getFechaFin().isAfter(fechaIni) || r.getFechaFin().equals(fechaIni))){   // El fin de la reserva entra en el intervalo
                
                    cantidadSimples += r.getHabsReservaSimple();                
                    cantidadDobles += r.getHabsReservaDoble();                
            }
        }

        return  Pair.of(habsHtlSimple-cantidadSimples, habsHtlDoble-cantidadDobles);
    }

    /**
     * @brief Método para crear una reserva y almacenarla dentro del Hotel
     * @param usrReserva es el usuario que ha pedido realizar la reserva
     * @param fechaIni es la fecha de inicio de la que se desea hacer la reserva
     * @param fechaFin es la fecha de finalización de la que se desea hacer la reserva
     * @param habsSolicitadas el vector de habitaciones que se desean reservar
     * @return devuelve el id asociado a la nueva reserva
     */
    public int crearReserva(@NotEmpty Usuario usrReserva,@NotEmpty LocalDate fechaIni, 
                            @NotEmpty LocalDate fechaFin, 
                            @PositiveOrZero int nSimples, @PositiveOrZero int nDobles){

        Pair<Integer, Integer> habsLibres = getHabitacionesLibresTotales(fechaIni,fechaFin);

        if(habsLibres.first < nSimples || habsLibres.second < nDobles  ){
            throw new HotelCreacionReservaOcupada();
        }
        
        
        Reserva reservaAux = new Reserva(usrReserva, fechaIni, fechaFin,0,0);
        if (nSimples > 0) {
            reservaAux.setHabsReservaSimple(nSimples);
        }
        if (nDobles > 0){
            reservaAux.setHabsReservaDoble(nDobles);
        }
        
        misReservas.put(reservaAux.getId(), reservaAux);
        return  reservaAux.getId();
    }

    /**
     * @brief Método para borrar una reserva del HashMap
     * @param idReserva es el identificador de la reserva dentro del HashMap.
     * @return true si la reserva ha sido borrada satisfactoriamente
     */
    public boolean borrarReserva(int idReserva){
        if(misReservas.containsKey(idReserva)) {
            misReservas.remove(idReserva);
            return true;
        }
        throw new ReservaNoEncontrada();
    }

    /**
     * @brief Método para modificar reserva
     * @param idReserva es el identificador de la reserva
     * @param fechaIni es la fecha inicial a modificar
     * @param fechaFin es la fecha final a modificar
     * @param habsSolicitadas son las habitaciones que se buscan reservar
     * @param tipo es el tipo de habitación que se busca modificar de la reserva
     * @return Booleano. True si se modificó satisfactoriamente. False en cualquier otro caso
     */
    public boolean modificarReserva(@PositiveOrZero int idReserva, LocalDate fechaIni, LocalDate fechaFin,@Positive int habsSolicitadas, Tipo tipo){
        if(misReservas.containsKey(idReserva)){
            if(getHabitacionesLibres(fechaIni,fechaFin, tipo) >= habsSolicitadas){
                misReservas.get(idReserva).setFechaIni(fechaIni);
                misReservas.get(idReserva).setFechaFin(fechaFin);
                if(tipo.equals(Tipo.SIMPLE)){
                    misReservas.get(idReserva).setHabsReservaSimple(habsSolicitadas);
                }else{
                    misReservas.get(idReserva).setHabsReservaDoble(habsSolicitadas);
                }
                return true;
            }
            return false;
        }
        throw new ReservaNoEncontrada();
    }

    /**
     * @brief Método para modificar la reserva
     * @param idReserva es la clave de la Reserva a modificar
     * @return objeto de la clase Reserva que se busca.
     */
    public Reserva getReserva(int idReserva){
        if(!(misReservas.containsKey(idReserva))){
            throw new ReservaNoEncontrada();
        }
        return misReservas.get(idReserva);
    }

    /**
     * @brief Método para crear una habitación y añadirla a las habitaciones del hotel.
     * @param tipo es el tipo de habitación
     * @return entero con el id de la habitación
     */
    public int addHabitacion(@Valid Tipo tipo){

        int simples = 0, dobles = 0;
        for(int i = 0; i < misHabitaciones.size(); i++) {
            if(misHabitaciones.get(i).getTipo().equals(Tipo.SIMPLE)) {
                simples++;
            }else{
                dobles++;
            }
        }

        if(simples - 1 >= this.habsHtlSimple || dobles - 1 >= this.habsHtlDoble){  //No le cabe 1 más sea cual sea el tipo
            throw new HabitacionesLlenas();
        }

        misHabitaciones.add(new Habitacion(misHabitaciones.size()+1,tipo));
        return misHabitaciones.size();
    }

    /**
     * @brief Método para modificar una habitación del hotel
     * @param numHab es el identificador de la habitación
     * @param tipo va a ser el nuevo tipo de la habitación con id numeroHab
     */
    public boolean modificarHabitacion(@PositiveOrZero int numHab, @Valid Tipo tipo){
        if(getHabitacion(numHab) == null){
            throw new HabitacionNoEncontrada();
        }
        getHabitacion(numHab).setTipo(tipo);
        return true;
    }

    public Habitacion getHabitacion(@PositiveOrZero int numHab){
        for(int i = 0; i < misHabitaciones.size(); i++) {
            if(misHabitaciones.get(i).getNumeroHab() == numHab) { //Tienes que comprobar que el parámetro con el número cada habitación
                return misHabitaciones.get(i);
            }
        }
        return null;
    }

    /**
     * @brief Método para quitar habitación del hotel
     * @param numeroHab Es el número correspondiente a la habitación a quitar del hotel.
     * @return true si se ha eliminado correctamente. En cualquier otro caso lanza la excepción HabitacionNoEncontrada
     */
    public boolean quitarHabitacion(@PositiveOrZero int numeroHab){
        if(getHabitacion(numeroHab).equals(null)){
            throw new HabitacionNoEncontrada();
        }
        misHabitaciones.remove(getHabitacion(numeroHab));
        return true;
    }

    public String getCif() { return cif; }

    public String getNombre() { return nombre; }

    public void setCif(String cif) {  this.cif = cif; }

    public Direccion getDireccion() { return direccion; }

    public int getHabsHtlSimple() { return habsHtlSimple; }

    public void setHabsHtlSimple(int habsHtlSimple) { this.habsHtlSimple = habsHtlSimple; }

    public int getHabsHtlDoble() { return habsHtlDoble; }

    public void setHabsHtlDoble(int habsHtlDoble) { this.habsHtlDoble = habsHtlDoble; }

    public List<Habitacion> getMisHabitaciones() { return misHabitaciones; }
    
    public int getNumReservas(){ return misReservas.size();}

    /**
     * @return the paisNormaliced
     */
    public String getPaisNormaliced() {
        return paisNormaliced;
    }

    /**
     * @return the ciudadNormaliced
     */
    public String getCiudadNormaliced() {
        return ciudadNormaliced;
    }
}
