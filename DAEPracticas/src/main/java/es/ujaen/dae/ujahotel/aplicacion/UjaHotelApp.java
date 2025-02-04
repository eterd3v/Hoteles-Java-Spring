
package es.ujaen.dae.ujahotel.aplicacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 *
 * @author pc
 */
@SpringBootApplication(scanBasePackages={
    "es.ujaen.dae.ujahotel.servicios",
    "es.ujaen.dae.ujahotel.repositorios",
    "es.ujaen.dae.ujahotel.rest",
    "es.ujaen.dae.ujahotel.seguridad"
})
@EntityScan(basePackages="es.ujaen.dae.ujahotel.entidades")
public class UjaHotelApp {

    public static void main(String[] args) {
        SpringApplication practicaDAE = new SpringApplication(UjaHotelApp.class);
        practicaDAE.run(args);
    }
}