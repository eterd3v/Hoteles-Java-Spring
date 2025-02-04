/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.ujaen.dae.ujahotel.utils;

/**
 *
 * @author pc
 */
public class Normalizer {
    
    public static String normalize(String orig){
        String normaliceStr = java.text.Normalizer.normalize(orig, java.text.Normalizer.Form.NFD);
        normaliceStr = normaliceStr.replaceAll("[^\\p{ASCII}]", "");
        return normaliceStr.toLowerCase();
    }
    
}
