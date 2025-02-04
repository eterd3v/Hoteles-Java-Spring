package es.ujaen.dae.ujahotel.repositorios;

import es.ujaen.dae.ujahotel.entidades.Usuario;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Estructura que alamacene a los usuarios y métodos para su gestión y consulta
 * */

@Repository
@Transactional(propagation = Propagation.REQUIRED) //Se ejecuta en una transaccion, la crea si no hay
public class RepositorioUsuarios {
    
    @PersistenceContext
    EntityManager em;
    
    public void guardar(Usuario newUser){
        em.persist(newUser);
    }
    
    //              No necesita de transacción          No realiza cambios 
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Usuario> buscar(String dni){
        return Optional.ofNullable(em.find(Usuario.class, dni));
    }
    
    public void actualizar(Usuario user) {
        em.merge(user);
    }
}
