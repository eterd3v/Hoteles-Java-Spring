package es.ujaen.dae.ujahotel.entidades;



import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class Reserva {
    private static final AtomicInteger contador = new AtomicInteger(0); //Ayuda a autogenerar un id

    @Id
    @PositiveOrZero
    private int id;
    
    @FutureOrPresent
    private LocalDate fechaIni;
    
    @FutureOrPresent
    private LocalDate fechaFin;
    
    @PositiveOrZero
    private int habsReservaSimple;
    
    @PositiveOrZero
    private int habsReservaDoble;

    @ManyToOne
    private Usuario usrAsignado;

    public Reserva(){
        this.id = contador.incrementAndGet();
        this.fechaIni = LocalDate.now(); //Crea el momento exacto en el que se crea el objeto
        this.fechaFin = LocalDate.now();
        this.habsReservaSimple = 0;
        this.habsReservaDoble = 0;
        this.usrAsignado = new Usuario();
    }

    /**
     * @brief Creador parametrizado de Reserva.
     * Crea un objeto Reserva y permite asignar habitacionesRsv y cambiar sus atributos reserva.
     * @param usrAsignado es el usuario que ha reservado las habitaciones
     * @param fechaIni es la fecha de inicio de la reserva
     * @param fechaFin es la fecha de expiración de la reserva
     */
    public Reserva(Usuario usrAsignado, LocalDate fechaIni, LocalDate fechaFin, int habsReservaSimple, int habsReservaDoble){
        this.id = contador.incrementAndGet();
        //this.id = n;
        this.usrAsignado = usrAsignado; //El usr ya existe en el sistema
        this.fechaIni = fechaIni;
        this.fechaFin = fechaFin;
        this.habsReservaSimple = habsReservaSimple;
        this.habsReservaDoble = habsReservaDoble;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the fechaIni
     */
    public LocalDate getFechaIni() {
        return fechaIni;
    }

    /**
     * @param fechaIni the fechaIni to set
     */
    public void setFechaIni(LocalDate fechaIni) {
        this.fechaIni = fechaIni;
    }

    /**
     * @return the fechaFin
     */
    public LocalDate getFechaFin() {
        return fechaFin;
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Usuario getUsrAsignado() { return usrAsignado; }

    public void setUsrAsignado(Usuario usrAsignado) { this.usrAsignado = usrAsignado; }

    public int getHabsReservaSimple() { return habsReservaSimple; }

    public void setHabsReservaSimple(int habsReservaSimple) { this.habsReservaSimple = habsReservaSimple; }

    public int getHabsReservaDoble() { return habsReservaDoble; }

    public void setHabsReservaDoble(int habsReservaDoble) { this.habsReservaDoble = habsReservaDoble; }

}