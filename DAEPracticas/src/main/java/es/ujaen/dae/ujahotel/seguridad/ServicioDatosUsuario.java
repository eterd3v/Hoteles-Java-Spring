package es.ujaen.dae.ujahotel.seguridad;

import es.ujaen.dae.ujahotel.entidades.Usuario;
import es.ujaen.dae.ujahotel.servicios.GestorHoteles;
import es.ujaen.dae.ujahotel.utils.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class ServicioDatosUsuario implements UserDetailsService {

    @Autowired
    GestorHoteles gestor;

    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {

        Usuario usuario = gestor.verUsuario(dni).orElseThrow(()->new UsernameNotFoundException("Usuario " + dni + " no encontrado."));
        
        
        if(usuario.getRol().equals(Rol.ADMIN))
            return User.withUsername(usuario.getDni())
                .roles("ADMIN").password(usuario.getClave())
                .build();
        
        return User.withUsername(usuario.getDni())
                .roles("CLIENTE").password(usuario.getClave())
                .build();

    }

}
