/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.ujaen.dae.ujahotel.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author pc
 */
public class HotelesPorLocalidad {
    
    //              Clave   Hab.Simples   Hab.Dobles 
    private HashMap<String, Pair<Integer, Integer>> hoteles;
    
    public HotelesPorLocalidad(){
        hoteles = new HashMap<>();
    }
    
    
    public void addHotel(String id){              
        
        if (!hoteles.containsKey(id))
            hoteles.put(id, Pair.of(0, 0));
                    
    }
    
    //Para iterar
    public String getHotel(int i){
        int cont =0;
        for ( String key : hoteles.keySet() ) {
            if (cont == i)
                return key;
            cont++;
        }
        return "";
    }
    
    
    public void putHabitaciones (String id, Pair<Integer, Integer> habs){
        if (hoteles.containsKey(id)){
            hoteles.put(id, habs);
        }
    }
    
    
    public void setHabitacionesHotel(String id, int nSimples, int nDobles){
        if (hoteles.containsKey(id)){
            hoteles.get(id).first=nSimples;
            hoteles.get(id).second=nDobles;
        }
    }
    
    
    public Pair<Integer, Integer> getHabitaciones(String id) {
        if(hoteles.containsKey(id)){
            return hoteles.get(id);
        }
        return Pair.of(0, 0);
    }
    
    
    public int getHabsSimples(String id){
        if(hoteles.containsKey(id)){
            return hoteles.get(id).first;
        }
        return 0;
    }
    
    public int getHabsDobles(String id){
        if(hoteles.containsKey(id)){
            return hoteles.get(id).second;
        }
        return 0;
    }
    
    public int size(){ 
        return hoteles.size();
    } 
}
