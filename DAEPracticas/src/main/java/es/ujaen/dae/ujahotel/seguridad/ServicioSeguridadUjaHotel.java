package es.ujaen.dae.ujahotel.seguridad;

import es.ujaen.dae.ujahotel.utils.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ServicioSeguridadUjaHotel {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Desactivar cualquier soporte de sesión
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.csrf().disable();

        // Activar seguridad HTTP Basic
        httpSecurity.httpBasic();

        // Definir protección por URL
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/ujahotel/usuarios").permitAll();
//        httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET, "/ujahotel/usuarios/{dni}").authenticated();
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET, "/ujahotel/usuarios/{dni}").authenticated();

        httpSecurity.authorizeRequests()
            .antMatchers(HttpMethod.GET, "/ujahotel/hoteles").permitAll();
        
        httpSecurity.authorizeRequests()
            .antMatchers(HttpMethod.POST, "/ujahotel/usuarios/{dni}/hoteles")
            .access("hasRole('ADMIN')");
        
        httpSecurity.authorizeRequests()
            .antMatchers(HttpMethod.POST, "/usuarios/{dni}/hoteles/{cif}/reserva")
            .authenticated();
        
        return httpSecurity.build();
    }



}
