package es.ujaen.dae.ujahotel.repositorios;

import es.ujaen.dae.ujahotel.entidades.Hotel;
import es.ujaen.dae.ujahotel.entidades.Usuario;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

/**
 * Estructura que alamacene a los hoteles y métodos para su gestión y consulta
 * */


@Repository
@Transactional(propagation = Propagation.REQUIRED)//Se ejecuta en una transaccion, la crea si no hay
public class RepositorioHoteles {

    @PersistenceContext
    EntityManager em;

    
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)//La operación no requiere transacción
    public Optional<Hotel> buscar(String cif){

        return Optional.ofNullable(em.find(Hotel.class, cif));
    }
    
    
    //@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)//La operación no requiere transacción
    public List<Hotel> getHoteles(String unpais, String unaciudad){
        

        return em.createQuery("select h "
                            + "from Hotel h "
                            + "where paisNormaliced = :param1 "
                            + "and ciudadNormaliced = :param2 ", Hotel.class)
                .setParameter("param1", unpais)
                .setParameter("param2", unaciudad)
                .getResultList();
    }

    
    public void guardar(Hotel hotel) {
        em.persist(hotel);
    }
    
    public void actualizar(Hotel hotel) {
        em.merge(hotel);
    }


}
