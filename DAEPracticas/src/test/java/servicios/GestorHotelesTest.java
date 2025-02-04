package servicios;


import java.util.Optional;
import es.ujaen.dae.ujahotel.aplicacion.UjaHotelApp;
import es.ujaen.dae.ujahotel.entidades.Hotel;
import es.ujaen.dae.ujahotel.entidades.Usuario;
import es.ujaen.dae.ujahotel.excepciones.HotelYaCreado;
import es.ujaen.dae.ujahotel.excepciones.UsuarioYaRegistrado;
import es.ujaen.dae.ujahotel.servicios.GestorHoteles;
import es.ujaen.dae.ujahotel.utils.Direccion;
import es.ujaen.dae.ujahotel.utils.HotelesPorLocalidad;
import es.ujaen.dae.ujahotel.utils.Tipo;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;



@SpringBootTest (classes = UjaHotelApp.class)
@ActiveProfiles(profiles = {"test"})//Coge la configuracion de BD para test "-test"
public class GestorHotelesTest {
    
    @Autowired
    GestorHoteles gestorHoteles;
    


    public void cargaDatos(){
        Hotel unHotel = new Hotel("H11223344", "Hotel Torino", 
                                new Direccion("Italia", "Roma", "Trevi"),
                                15, 8);
        
        Optional<Usuario> adminlogin = gestorHoteles.loginUsuario("77689949C", "lrdf2000");
        Usuario admin = adminlogin.get();
        
        try{
            gestorHoteles.altaHotel(admin, unHotel);
        }catch(HotelYaCreado e){
            System.out.println("cargaDatos:: Hotel ya creado");
        }
        
        LocalDate ini = LocalDate.of(2023, 2, 1);        
        LocalDate fin = LocalDate.of(2023, 2, 5);
        
        //unHotel.crearReserva(admin, ini, fin, 3, 1);
        gestorHoteles.reservar("H11223344", admin, ini, fin, 3, 0);
    }


    @Test
    public void testAccesoServicioGestorHoteles(){
        
        Assertions.assertThat(gestorHoteles).isNotNull();
    }
    
   
    //Dar alta a usuario ya registrado
    @Test
    @DirtiesContext
    public void testAltaUsuarioInvalido(){
        
        String dni = "77689949C"; //DNI existente
        String correo = "alguien@gmail.com";
        String nombre = "Rodrigo Dominguez";
        String clave = "Futbo1Barsa";
        String direccion = "Miguel Romera, 7, 1";
        String telefono = "953427253";
        
        Assertions.assertThatThrownBy(()->{
        gestorHoteles.altaUsuario(dni, correo, nombre, clave, direccion, telefono);})
                .isInstanceOf(UsuarioYaRegistrado.class);
    }    
    
    
    //@brief Alta de usuario y login    
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)        
    public void testAltaYLoginUsuario() {
        String dni = "11223344C";
        String correo = "rodrigo@gmail.com";
        String nombre = "Rodrigo Dominguez";
        String clave = "Futbo1Barsa";
        String direccion = "Miguel Romera, 7, 1";
        String telefono = "953427253";
        
        Usuario nuevoUsuario = gestorHoteles.altaUsuario(dni, correo, nombre, clave, direccion, telefono);
        Optional<Usuario> userLogin = gestorHoteles.loginUsuario(nuevoUsuario.getDni(), clave);
        Assertions.assertThat(userLogin.isPresent()).isTrue();
        Assertions.assertThat(userLogin.get().getDni()).isEqualTo(nuevoUsuario.getDni());
    }
    
    
    //Intento login con clave erronea
    @Test
    public void testLoginInvalido(){        
        String dni = "22334455B";
        String correo = "jesus@gmail.com";
        String nombre = "Jesus Rivilla";
        String clave = "unaClave";
        String direccion = "Calle de Jesus, 3, 3";
        String telefono = "953216587";
        
        Usuario nuevoUsuario = gestorHoteles.altaUsuario(dni, correo, nombre, clave, direccion, telefono);
        Optional<Usuario> userLogin = gestorHoteles.loginUsuario(nuevoUsuario.getDni(), "otraClave");
        Assertions.assertThat(userLogin.isPresent()).isFalse();
    }
    
    

    // Dar alta a hotel
    //Preguntar
    @Test
    @DirtiesContext
    public void testAltaHotel() {
        
        cargaDatos();
        
        Optional<Usuario> adminLogin = gestorHoteles.loginUsuario("77689949C", "lrdf2000");
        Usuario admin = adminLogin.get();
        
        String cif = "H00000001";
        String nombreH = "Hotel UJAEN";

        Direccion direccionH = new Direccion();
        direccionH.pais = "España";
        direccionH.ciudad = "Jaén";
        direccionH.calle = "Calle Las Lagunillas 5";
        
        int habsHtlSimple = 10;
        int habsHtlDoble = 5;
        
        Hotel h = new Hotel(cif, nombreH, direccionH, habsHtlSimple, habsHtlDoble);

        Optional<Hotel> hotel = gestorHoteles.altaHotel(admin, h);
 
        Assertions.assertThat(hotel.isPresent()).isTrue();

    }

    // Intentar dar alta aun hotel ya registrado
    @Test
    @DirtiesContext
    public void testAltaHotelRegistrado() {
        
        cargaDatos();
        
        Optional<Usuario> adminLogin = gestorHoteles.loginUsuario("77689949C", "lrdf2000");
        Usuario admin = adminLogin.get();
        
        String cif = "H11223344"; //CIF ya existe
        String nombreH = "Hotel UJAEN";

        Direccion direccionH = new Direccion();
        direccionH.pais = "España";
        direccionH.ciudad = "Jaén";
        direccionH.calle = "Calle Las Lagunillas 5";
        
        int habsHtlSimple = 10;
        int habsHtlDoble = 5;
        
        Hotel h = new Hotel(cif, nombreH, direccionH, habsHtlSimple, habsHtlDoble);

        Assertions.assertThatThrownBy(()->{
            gestorHoteles.altaHotel(admin, h);}).isInstanceOf(HotelYaCreado.class);

    }
    
    
    //@brief Interacción completa de usuario.
    //Consultar los hoteles en una ciudad concreta y su disponibilidad en un
    //rango de dias concretos. Elegir un Hotel y comprobar que se hace la reserva. 
    @Test
    @DirtiesContext
    public void consultayRealizaReserva() {        
        cargaDatos();//Almacenamos algunos datos para las consultas
        
        //Usamos admin porque ya está precargado
        Optional<Usuario> adminLogin = gestorHoteles.loginUsuario("77689949C", "lrdf2000");
        
        LocalDate ini = LocalDate.of(2023, 2, 1);
        
        LocalDate fin = LocalDate.of(2023, 2, 5);
        
        int nhabitacionesSimples = 3;
        
        List<Hotel> hotelesDisponibles = 
            gestorHoteles.consultaHoteles( "Italia", "Roma", ini, fin, nhabitacionesSimples, 0 );
        
        Assertions.assertThat(hotelesDisponibles.size()).isGreaterThan(0);//Hay hoteles en la ciudad
        
        //Comprobamos que está la reserva que se almacenó en el inicio
        Assertions.assertThat(hotelesDisponibles.get(0)
                .getHabitacionesLibres(ini, fin, Tipo.SIMPLE)).isEqualTo(12);
        
        
        //Realizamos la reserva
        Assertions.assertThat(
        gestorHoteles.reservar(hotelesDisponibles.get(0).getCif(), 
                adminLogin.get(), fin, fin, 
                nhabitacionesSimples, 0)).isPositive();                
    }

}
