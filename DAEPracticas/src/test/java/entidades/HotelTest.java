package entidades;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import es.ujaen.dae.ujahotel.entidades.Habitacion;
import es.ujaen.dae.ujahotel.entidades.Hotel;
import es.ujaen.dae.ujahotel.entidades.Reserva;
import es.ujaen.dae.ujahotel.entidades.Usuario;
import es.ujaen.dae.ujahotel.excepciones.HabitacionNoEncontrada;
import es.ujaen.dae.ujahotel.excepciones.HotelCreacionReservaOcupada;
import es.ujaen.dae.ujahotel.excepciones.HotelFechasIncorrectas;
import es.ujaen.dae.ujahotel.excepciones.ReservaNoEncontrada;
import es.ujaen.dae.ujahotel.utils.Direccion;
import es.ujaen.dae.ujahotel.utils.Rol;
import es.ujaen.dae.ujahotel.utils.Tipo;


@ActiveProfiles(profiles = {"test"})
public class HotelTest {
    
    Hotel htl;
    int numHabsSimples;
    int numHabsDobles ;
    Tipo tipo;
    
    
    public void cargaDatos(){
        numHabsSimples = 50;
        numHabsDobles = numHabsSimples*2;
        tipo = Tipo.SIMPLE;
        Direccion dirHtl = new Direccion("España","Jaén","Federico García Lorca Nº32");
        htl = new Hotel("CIF","Nombre",dirHtl,numHabsSimples,numHabsDobles);
    }
    
    
    //Antonio
    @Test
    public void testModificarHabitaciones(){
        //Crear varias habitaciones, un hotel y modificarle las habitaciones creadas
        //Comprobar que contiene las habitaciones han cambiado de tipo

        cargaDatos();

        ArrayList<Integer> idHabs = new ArrayList<>();
        for (int i = 0; i < numHabsSimples; i++){
            idHabs.add(htl.addHabitacion(tipo));
        }

        for (int i = 0; i < idHabs.size(); i++) {
            Assertions.assertThat(htl.modificarHabitacion(idHabs.get(i),tipo)).isTrue();
        }

        Assertions.assertThatThrownBy(()->
                htl.modificarHabitacion(idHabs.size()+2,tipo)).isInstanceOf(HabitacionNoEncontrada.class);

    }


    //Antonio
    @Test
    public void testCrearReserva(){
        //Crear varias habitaciones, un hotel y añadirle las habitaciones creadas.
        //Crear varias reservas y añadirlas al hotel.
        //Comprobar que devuelve el id de la reserva y que la reserva se ha añadido al hotel

        cargaDatos();
        for (int i = 1; i <= numHabsSimples; i++){  //Meto habitaciones
            htl.addHabitacion(tipo);
        }

        LocalDate ini = LocalDate.of(2023, Month.APRIL, 17);
        LocalDate fin = LocalDate.of(2023, Month.APRIL, 18);

        Usuario usr = new Usuario(12345678 + "X","correo@gmail.com","Antonio","clavesecreta","Campus Las Lagunillas","953538747", Rol.CLIENTE);

        int idRsv = htl.crearReserva(usr,ini,fin,numHabsSimples,numHabsDobles);
        Assertions.assertThat(idRsv).isNotNull();
        Assertions.assertThat(htl.getReserva(idRsv).getHabsReservaSimple()).isEqualTo(numHabsSimples);
        Assertions.assertThat(htl.getReserva(idRsv).getHabsReservaDoble()).isEqualTo(numHabsDobles);

        ///Al siguiente día, no hay reserva
        LocalDate ini2 = fin;
        LocalDate fin2 = LocalDate.of(2023,Month.APRIL,19);

        Assertions.assertThatThrownBy(()->
                htl.crearReserva(usr,ini2,fin2,numHabsSimples,numHabsDobles)).isInstanceOf(HotelCreacionReservaOcupada.class);

    }

    //Antonio
    //Consulta, no solo código (en lugar de HotelPorLocalidad que devuelva List<Hotel>)
    //listaResevaHtl(ini,fin)
    
    @Test
    public void testModificarReserva(){
        //Crear varias habitaciones, un hotel y añadirle las habitaciones creadas.
        //Crear varias reservas y añadirlas al hotel.
        //Modificar las reservas del hotel (sin conflictos con las habitaciones y las fechas de las reservas)
        //Comprobar que tanto las habitaciones como las reservas del hotel se han modificado de forma satisfactoria

        cargaDatos();
        ArrayList<Integer> arrayHabs = new ArrayList<>();
        for (int i = 1; i <= numHabsSimples; i++){
            arrayHabs.add(htl.addHabitacion(tipo));
        }

        LocalDate ini = LocalDate.of(2023, Month.APRIL, 17);
        LocalDate fin = LocalDate.of(2023, Month.APRIL, 18);

        Usuario usr = new Usuario(12345678+"X","correo@gmail.com","Antonio","clavesecreta","Campus Las Lagunillas","953538747", Rol.CLIENTE);

        int miReserva = htl.crearReserva(usr,ini,fin,numHabsSimples/3, numHabsDobles/3); //17, 100
        
        Reserva rsvAux = new Reserva(usr,ini,fin,numHabsSimples/3,numHabsDobles);

        //TESTS
        Assertions.assertThat(htl.modificarReserva(miReserva,ini,fin,numHabsSimples/2,tipo)).isTrue();
        Assertions.assertThat(htl.modificarReserva(miReserva,ini,fin,numHabsDobles/2,Tipo.DOBLE)).isTrue();
        Assertions.assertThat(htl.modificarReserva(miReserva,ini,fin,numHabsSimples*2,tipo)).isFalse();
        Assertions.assertThatThrownBy(()->
                htl.modificarReserva(miReserva+7,ini,fin,numHabsSimples,tipo)).isInstanceOf(ReservaNoEncontrada.class);
        Assertions.assertThat(htl.getReserva(miReserva).getHabsReservaSimple()).isNotEqualTo(rsvAux.getHabsReservaSimple());
        Assertions.assertThat(htl.getReserva(miReserva).getHabsReservaDoble()).isNotEqualTo(rsvAux.getHabsReservaDoble());

    }

    //Antonio
    @Test
    public void testGetHabitacionesLibres(){
        //Crear varias habitaciones, un hotel y añadirle las habitaciones creadas
        //Comprobar que hotel.getHabitacionesLibres(fechaIni,fechaFin) da todas las habitaciones libres

        cargaDatos();

        LocalDate ini = LocalDate.of(2023, Month.APRIL, 17);
        LocalDate fin = LocalDate.of(2023, Month.APRIL, 18);

        Assertions.assertThat(htl.getHabitacionesLibres(ini,fin,Tipo.SIMPLE)).isEqualTo(htl.getHabsHtlSimple());
        Assertions.assertThat(htl.getHabitacionesLibres(ini,fin,Tipo.DOBLE)).isEqualTo(htl.getHabsHtlDoble());

        Usuario usr = new Usuario(12345678+"X","correo@gmail.com","Antonio","clavesecreta","Campus Las Lagunillas","953538747", Rol.CLIENTE);

        int auxHabsSimples = 5*numHabsSimples/7, auxHabsDobles = 5*numHabsDobles/7;
        ArrayList<Integer> arrayHabsSimples = new ArrayList<>();
        for (int i = 1; i <= auxHabsSimples; i++){ // 100*5/7 = 71 habs simples
            arrayHabsSimples.add(htl.addHabitacion(Tipo.SIMPLE));
        }
        int auxRsv1 = htl.crearReserva(usr,ini,fin,auxHabsSimples,0);

        ArrayList<Integer> arrayHabsDobles = new ArrayList<>();
        for (int i = 1; i <= auxHabsDobles; i++){ // 100*5/7 = 10 habs dobles
            arrayHabsSimples.add(htl.addHabitacion(Tipo.DOBLE));
        }
        int auxRsv2 = htl.crearReserva(usr,ini,fin,0,auxHabsDobles);


        Assertions.assertThatThrownBy(()->
                htl.getHabitacionesLibres(fin,ini,Tipo.SIMPLE)).isInstanceOf(HotelFechasIncorrectas.class);
        Assertions.assertThat(numHabsSimples - htl.getHabitacionesLibres(ini,fin,Tipo.SIMPLE)).isEqualTo(auxHabsSimples);
        Assertions.assertThat(numHabsDobles - htl.getHabitacionesLibres(ini,fin,Tipo.DOBLE)).isEqualTo(auxHabsDobles);

    }

    //Luis
    @Test
    public void testQuitarReserva(){
        cargaDatos();

        Usuario usrReserva = new Usuario();
        LocalDate ini = LocalDate.of(2023, Month.MARCH, 4);
        LocalDate fin = LocalDate.of(2023, Month.MARCH, 7);
        int habsSolicitadas = 4;

        //Añadimos reserva ficticia
        int id = htl.crearReserva(usrReserva, ini, fin, habsSolicitadas, 0);

        //Comprobamos que la reserva eliminada es la correcta
        Assertions.assertThat(htl.borrarReserva(id)).isTrue();

        //Si intentamos borrarla de nuevo debería devolver una excepción
        Assertions.assertThatThrownBy(()->{
                    htl.borrarReserva(id);})
                .isInstanceOf(ReservaNoEncontrada.class);
    }




    @Test
    public void testAddHabitaciones(){ //Rocío

        Direccion dirHtl = new Direccion("España","Jaén","Federico García Lorca Nº32");
        Hotel htl = new Hotel("CIF","Nombre",dirHtl,8,10);

        Habitacion hab1 = new Habitacion(1, Tipo.DOBLE);
        Habitacion hab2 = new Habitacion(2, Tipo.SIMPLE);

        ArrayList<Habitacion> habList = new ArrayList<>();
        habList.add(hab1);
        habList.add(hab2);

        htl.addHabitacion(Tipo.DOBLE);
        htl.addHabitacion(Tipo.SIMPLE);

        Assertions.assertThat(htl.getMisHabitaciones().size()).isEqualTo(habList.size());

    }

    @Test
    public void testQuitarHabitaciones() { //Rocío

        Direccion dirHtl = new Direccion("España","Jaén","Federico García Lorca Nº32");
        Hotel htl = new Hotel("CIF","Nombre",dirHtl,8,10);

        Habitacion hab1 = new Habitacion(1, Tipo.DOBLE);
        Habitacion hab2 = new Habitacion(2, Tipo.SIMPLE);
        Habitacion hab3 = new Habitacion(3, Tipo.DOBLE);
        Habitacion hab4 = new Habitacion(4, Tipo.SIMPLE);
        ArrayList<Habitacion> habList = new ArrayList<>();
        habList.add(hab1);
        habList.add(hab2);
        habList.add(hab3);
        habList.add(hab4);

        htl.addHabitacion(Tipo.DOBLE);
        htl.addHabitacion(Tipo.SIMPLE);
        htl.addHabitacion(Tipo.DOBLE);
        htl.addHabitacion(Tipo.SIMPLE);

        htl.quitarHabitacion(1);
        habList.remove(hab4);

        Assertions.assertThat(htl.getMisHabitaciones().size()).isEqualTo(habList.size());

    }

}

