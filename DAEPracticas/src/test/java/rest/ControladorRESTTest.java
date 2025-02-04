/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

import es.ujaen.dae.ujahotel.entidades.Hotel;
import es.ujaen.dae.ujahotel.rest.dto.DTOReserva;
import es.ujaen.dae.ujahotel.rest.dto.DTOHotel;
import es.ujaen.dae.ujahotel.rest.dto.DTOUsuario;
import es.ujaen.dae.ujahotel.utils.Normalizer;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import javax.annotation.PostConstruct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;

/**
 *
 * @author pc
 */
@SpringBootTest(classes = es.ujaen.dae.ujahotel.aplicacion.UjaHotelApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"test"})
public class ControladorRESTTest {
    
    @LocalServerPort
    int localPort;
    
    @Autowired
    MappingJackson2HttpMessageConverter springBootJacksonConverter;
    
    TestRestTemplate restTemplate;
    
    @PostConstruct
    void crearRestTemplateBuilder(){
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + localPort + "/ujahotel")
                .additionalMessageConverters(List.of(springBootJacksonConverter));
        
        restTemplate = new TestRestTemplate(restTemplateBuilder);
        
        
        // DATOS PARA LUEGO HACER CONSULTAS
        DTOHotel hotel = new DTOHotel(
            "H11223355",
            "Hotel Torino",
            "Italia",
            "Roma",
            "Trevi",
            15,
            8
        );
        
        //Nos logueamos como ADMIN que ya esta cargado en el sistema y creamos 
        ResponseEntity<DTOHotel> respuesta2 = restTemplate
                .withBasicAuth("77689949C", "lrdf2000")
                .postForEntity(
                "/usuarios/{dni}/hoteles",
                hotel,
                DTOHotel.class,
                "77689949C"
        );
        
    }

    ///TODO: Mirar autenticación withAuthBasic
    @Test
    public void testAltaUsuarioInvalido(){
        
        DTOUsuario usuario = new DTOUsuario(
                "77689949C", //DNI ya registrado
                "uncorreo@gmail.com",
                "Antonio Armenteros",
                "una calle",
                "953112233",
                "ADMIN",
                "secreto"
        );
        
        ResponseEntity<DTOUsuario> respuesta = restTemplate.postForEntity(
                "/usuarios",
                usuario,
                DTOUsuario.class
        );
        
        Assertions.assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    ///TODO: Mirar autenticación withAuthBasic
    @Test
    public void testReservar() {

        LocalDate ini = LocalDate.of(2023, Month.APRIL, 17);
        LocalDate fin = LocalDate.of(2023, Month.APRIL, 18);

        DTOUsuario usuario = new DTOUsuario(
            "77435075D",
            "uncorreo@gmail.com",
            "Daniel",
            "una calle",
            "953112233",
            "CLIENTE",
            "secret0"
        );
        
        ResponseEntity<DTOUsuario> respuesta = restTemplate.postForEntity( //Creamos  una usuario
            "/usuarios",
            usuario,
            DTOUsuario.class
        );
        
        Assertions.assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOHotel[] > respuesta2 = restTemplate.getForEntity( //Consultamos hoteles
            "/hoteles?pais=Italia&ciudad=Roma&desde="+ini.toString()
            +"&hasta="+fin.toString()
            +"&nSimples=4&nDobles=1", // 4 simples y 1 doble
            DTOHotel[].class);
        
        DTOHotel[] hotelesDisponibles = respuesta2.getBody();
        
        DTOReserva reserva = new DTOReserva( //Creamos una reverva con los datos necesarios
            1, // ID basura
            ini, // Las fechas que queremos
            fin, 
            4, // Las habitaciones concuerdan con la consulta previa
            1,
            "77435075D" //DNI del usuario creado
        );
        
        ResponseEntity<DTOReserva> respuesta3 = restTemplate
            .withBasicAuth(usuario.dni(), usuario.clave())
            .postForEntity(
                "/usuarios/{dni}/hoteles/{cif}/reserva",
                reserva,
                DTOReserva.class,
                usuario.dni(),
                hotelesDisponibles[0].cif());//Reservamos en el primer hotel encontrado
        
        Assertions.assertThat(hotelesDisponibles[0].cif()).isEqualTo("H11223355");
        Assertions.assertThat(respuesta3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    ///TODO: Mirar autenticación withAuthBasic
    @Test
    public void testAltaHotel(){
        
        //CAMBIAR: Obtener el admin de la BD
        DTOUsuario usuario = new DTOUsuario(
                "77435076X", //DNI sin registrar
                "uncorreo@gmail.com",
                "Antonio Armenteros",
                "una calle",
                "953112233",
                "ADMIN",
                "secreto"
        );
        
        ResponseEntity<DTOUsuario> respuesta = restTemplate.postForEntity(
                "/usuarios",
                usuario,
                DTOUsuario.class
        );
        
        Assertions.assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        DTOHotel hotel = new DTOHotel(
                "H11223399",
                "Hotel Condestable",
                "España",
                "Jaen",
                "Paseo Estacion",
                50,
                25
        );
        
        ResponseEntity<DTOHotel> respuesta2 = restTemplate
                .withBasicAuth(usuario.dni(), usuario.clave())
                .postForEntity(
                "/usuarios/{dni}/hoteles",
                hotel,
                DTOHotel.class,
                usuario.dni()
        );
        
        Assertions.assertThat(respuesta2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
   
    @Test
    public void testConsultaDisponibilidad(){ 
        
        ResponseEntity<DTOHotel[] > respuesta3 = restTemplate.getForEntity(
            "/hoteles?pais=Italia&ciudad=Roma&desde="+
            LocalDate.of(2022, 12, 1).toString()
            +"&hasta="+LocalDate.of(2022, 12, 3).toString()
            //+"&nSimples=3&nDobles=2"
            ,DTOHotel[].class); //Probamos con los valores por defecto
        
        DTOHotel[] hotelesDisponibles = respuesta3.getBody(); //Obtenemos los hoteles disponibles
        
        int numHoteles = 0;        
        for( DTOHotel h : hotelesDisponibles){            
            numHoteles++; //Contamos los hoteles devueltos
        } 
        
        Assertions.assertThat(numHoteles).isGreaterThan(0); //Nos aseguramos que ha encontrado alguno        
    }
    

    @Test
    public void testAltaYLoginUsuario(){

        DTOUsuario usuario = new DTOUsuario( //Nuevo usuario a dar de alta
                "11223344C",
                "rodrigo@gmail.com",
                "Rodrigo Dominguez",
                "Miguel Romera, 7, 1",
                "953427253",
                "CLIENTE",
                "Futbo1Barsa"
        );

        ResponseEntity<DTOUsuario> respuesta = restTemplate.postForEntity(
                "/usuarios",
                usuario,
                DTOUsuario.class
        );

        Assertions.assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity respuesta2 = restTemplate.
                withBasicAuth(usuario.dni(), usuario.clave()).getForEntity(
                        "/usuarios/{dni}",
                        DTOUsuario.class,
                        usuario.dni());

        Assertions.assertThat(respuesta2.getStatusCode()).isEqualTo(HttpStatus.OK); //Se ha encontrado el usuario

    }
    
    @Test
    public void testHotelYaRegistrado() {

        DTOUsuario usuario = new DTOUsuario(
                "77648131Q", //DNI sin registrar
                "micorreo@gmail.com",
                "Rocio Domingo",
                "mi calle",
                "693123456",
                "ADMIN",
                "secreto"
        );

        ResponseEntity<DTOUsuario> respuesta = restTemplate.postForEntity(
                "/usuarios",
                usuario,
                DTOUsuario.class
        );

        Assertions.assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        DTOHotel hotel = new DTOHotel(  //Este hotel ya se encuentra en la base de datos
                "H00000000",
                "Hotel Defecto",
                "Un Pais",
                "Una Ciudad",
                "Una calle",
                15,
                15
        );

        ResponseEntity<DTOHotel> respuesta2 = restTemplate.withBasicAuth(usuario.dni(), usuario.clave()).
                postForEntity(
                "/usuarios/{dni}/hoteles",
                hotel,
                DTOHotel.class,
                usuario.dni()
        );

        Assertions.assertThat(respuesta2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);   //Éxito si surge un conflicto pues ya está creado

    }

}
