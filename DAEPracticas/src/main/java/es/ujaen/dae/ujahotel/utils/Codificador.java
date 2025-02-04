/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.ujaen.dae.ujahotel.utils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


/**
 *
 * @author pc
 */
public class Codificador {

    static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private Codificador() {
    }

    public static String codificar(String cadena) {
//        String cadenaCodificada = null;
//
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            md.update(cadena.getBytes());
//            cadenaCodificada = Base64.getEncoder().withoutPadding().encodeToString(md.digest());
//        }
//        catch(NoSuchAlgorithmException e) {
//            // No debe ocurrir puesto que MD5 es un algoritmo que existe en la
//            // implementación Java estándar
//        }
//        return cadenaCodificada;
        return encoder.encode(cadena);
    }
    
    public static boolean igual(String password, String passwordCodificado) {
        return encoder.matches(password, passwordCodificado);
    }

}
