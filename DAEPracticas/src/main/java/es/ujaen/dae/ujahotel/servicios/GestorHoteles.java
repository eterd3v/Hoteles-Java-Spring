package es.ujaen.dae.ujahotel.servicios;

import es.ujaen.dae.ujahotel.entidades.Hotel;
import es.ujaen.dae.ujahotel.entidades.Reserva;
import es.ujaen.dae.ujahotel.entidades.Usuario;
import es.ujaen.dae.ujahotel.excepciones.AdministradorInvalido;
import es.ujaen.dae.ujahotel.excepciones.HotelNoEncontrado;
import es.ujaen.dae.ujahotel.excepciones.HotelYaCreado;
import es.ujaen.dae.ujahotel.excepciones.UsuarioNoRegistrado;
import es.ujaen.dae.ujahotel.excepciones.UsuarioYaRegistrado;
import es.ujaen.dae.ujahotel.repositorios.RepositorioHoteles;
import es.ujaen.dae.ujahotel.repositorios.RepositorioUsuarios;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import es.ujaen.dae.ujahotel.utils.Rol;
import java.util.HashMap;
import es.ujaen.dae.ujahotel.utils.Direccion;
import es.ujaen.dae.ujahotel.utils.HotelesPorLocalidad;
import es.ujaen.dae.ujahotel.utils.Normalizer;
import es.ujaen.dae.ujahotel.utils.Tipo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Validated
public class GestorHoteles {
    
    @Autowired
    RepositorioUsuarios repoUsuarios;

    @Autowired
    RepositorioHoteles repoHoteles;

    public GestorHoteles(){}

    @PostConstruct
    void creaAdmin(){
        Usuario admin = new Usuario("77689949C", "lrdf0001@gmail.com",
                        "Luis Dominguez", "lrdf2000", "UnaCalle", 
                       "953124578", Rol.ADMIN);
        
        if(!repoUsuarios.buscar("77689949C").isPresent()){
            repoUsuarios.guardar(admin);
        }

        Direccion dir = new Direccion("Un Pais", "Una Ciudad", "Una calle");

        Hotel hotel = new Hotel("H00000000", "Hotel Defecto", dir, 15, 15);

        if(!repoHoteles.buscar("H00000000").isPresent()){
            repoHoteles.guardar(hotel);
        }
    }

    /*
     * @brief Dar alta a nuevo usuario
     * @param [in] dni          Identificador usuario   (String)
     * @param [in] correo       Mail del usuario        (String)
     * @param [in] nombre       Nombre de usuario       (String)
     * @param [in] clave        Contraseña del usuario  (String)
     * @param [in] dirección    Direccióndel usuario    (String)
     * @param [in] telefono     Telefono del usuario    (String)
     * @return Usuario
     * @throws UsuarioYaRegistrado
     * */
    public Usuario altaUsuario( @NotBlank String dni, @NotBlank String correo, 
                                @NotBlank String nombre, @NotBlank String clave, 
                                @NotBlank String direccion, @NotBlank String telefono){
        if(repoUsuarios.buscar(dni).isPresent()){
            throw new UsuarioYaRegistrado();
        }
        Usuario newUser = new Usuario(dni, correo, nombre, 
                              clave,  direccion, telefono, Rol.CLIENTE);
        repoUsuarios.guardar(newUser);
        return newUser;
    }

    
    public void altaUsuario (@Valid Usuario user){
        Optional<Usuario> test = repoUsuarios.buscar(user.getDni());
        if(test.isPresent()){
            throw new UsuarioYaRegistrado();}
        repoUsuarios.guardar(user); 
    }
   
    
    /**
     * @brief Login de usuario
     * @param [in] _dni      Identificador usuario   (String)
     * @param [in] _clave    Contraseña del usuario  (String)
     * @return Optional<Usuario>
     * */
    public Optional<Usuario> loginUsuario(@NotBlank String _dni, 
                                          @NotBlank String _clave){
        
        Optional<Usuario> userLogin =  repoUsuarios.buscar(_dni)
                                    .filter((usuario)->usuario.claveValida(_clave));
        
        return userLogin;
    }

    /**
     * @brief Login de usuario para REST
     * @param [in] _dni      Identificador usuario   (String)
     * @return Optional<Usuario>
     * */
    @Transactional
    public Optional<Usuario> verUsuario(@NotBlank String _dni){
        return  repoUsuarios.buscar(_dni);
    }
    
    /*
     * @brief Consulta habitaciones disponibles
     * @param [in] ini          Fecha inicio estancia   (String)
     * @param [in] fin          Fecha fin estancia      (String)
     * @param [in] paisDest     Pais destino            (String)
     * @param [in] ciudadDest   Ciudad destino          (String)
     * 
     * @return  HotelesPorLocalidad: Para cada hotel del destino, devuelve
     *          el número de habitaciones libres del tipo especificado
     * */
    public List<Hotel> consultaHoteles( @NotBlank String paisDest, @NotBlank String ciudadDest,
            LocalDate fechaIni, LocalDate fechaFin, int nSimples, int nDobles){
        
        paisDest = Normalizer.normalize(paisDest);
        ciudadDest = Normalizer.normalize(ciudadDest);
        
        List<Hotel> hoteles = repoHoteles.getHoteles(paisDest, ciudadDest);
        
        List<Hotel> hotelesDisponibles = new ArrayList<Hotel>(); //Hoteles que tienen habitaciones disponibles
        int ns; // Numero de habitaciones Simples 
        int nd; // Numero de habitaciones Dobles
        for (int i=0; i< hoteles.size(); i++) { //Recorremos los hoteles
            ns = hoteles.get(i).getHabitacionesLibres(fechaIni, fechaFin, Tipo.SIMPLE);
            nd = hoteles.get(i).getHabitacionesLibres(fechaIni, fechaFin, Tipo.DOBLE);
            
            if (ns >= nSimples && nd >= nDobles)
                hotelesDisponibles.add(hoteles.get(i));
        }
        
        return hotelesDisponibles;
    }
    

    /**
     * @brief Reserva habitaciones
     * @param [in] hotel            Cif hotel donde se va a formalizar la reserva
     * @param [in] usrAsignado      Usuario quien ha pedido la reserva
     * @param [in] fechaIni         Fecha inicial de estancia de la reserva
     * @param [in] fechaFin         Fecha final de estancia de la reserva
     * @param [in] habitacionesRsv  Habitaciones libres que el usrAsignado ha escogido previamente para la reserva
     * @return int Identificador de la nueva reserva creada.
     * @throws HotelNoEncontrado
     */
    public int reservar(@NotBlank String hotel, @Valid Usuario usrAsignado, LocalDate fechaIni,
                                 LocalDate fechaFin, int nSimples, int nDobles){

        if(!repoUsuarios.buscar(usrAsignado.getDni()).isPresent()){
            throw new UsuarioNoRegistrado();
        }

        Hotel hotelAux = repoHoteles.buscar(hotel).orElseThrow(HotelNoEncontrado::new);
        
        int idNuevaReserva = hotelAux.crearReserva(usrAsignado, fechaIni, fechaFin, nSimples, nDobles);
        repoHoteles.actualizar(hotelAux);
        return idNuevaReserva;
        
        //return 0;
    }

    public int reservar(@NotBlank String cifHotel, @Valid String dniUsr, Reserva peticionReserva){

        Optional<Hotel> hotel = repoHoteles.buscar(cifHotel);
        Optional<Usuario> usuario = repoUsuarios.buscar(dniUsr);
        
        
        if(!hotel.isPresent())
            throw new HotelNoEncontrado();

        if(!usuario.isPresent())
            throw new UsuarioNoRegistrado();

        LocalDate ini = peticionReserva.getFechaIni();
        LocalDate fin = peticionReserva.getFechaFin();
        int simples = peticionReserva.getHabsReservaSimple();
        int dobles = peticionReserva.getHabsReservaDoble();

        int idNuevaReserva = hotel.get().crearReserva(usuario.get(),ini,fin,simples,dobles);
        repoHoteles.actualizar(hotel.get());
        return idNuevaReserva;
    }


    /**
     * @brief Dar de alta un hotel
     * @param [in] admin         Administrador del sistema
     * @param [in] nuevoHotel   Hotel nuevo a añadir al gestor
     */
    public Optional<Hotel> altaHotel(@NotNull Usuario admin, @Valid Hotel nuevoHotel){
        
        if(!admin.getRol().equals( Rol.ADMIN)){
            throw new AdministradorInvalido();
        }

        if(repoHoteles.buscar(nuevoHotel.getCif()).isPresent()) {
            throw new HotelYaCreado();
        }

        repoHoteles.guardar(nuevoHotel);
        return repoHoteles.buscar(nuevoHotel.getCif());
       
    }
    
    
    public Optional<Hotel> altaHotel(@NotEmpty String dni, @Valid Hotel nuevoHotel){
        
        Optional<Usuario> usuario = repoUsuarios.buscar(dni);
        
        if(!usuario.isPresent() || !usuario.get().getRol().equals(Rol.ADMIN)){
            throw new AdministradorInvalido();
        }

        if(repoHoteles.buscar(nuevoHotel.getCif()).isPresent()) {
            throw new HotelYaCreado();
        }

        repoHoteles.guardar(nuevoHotel);
        return repoHoteles.buscar(nuevoHotel.getCif());       
    }
    


}

