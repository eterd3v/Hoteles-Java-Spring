/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.ujaen.dae.ujahotel.entidades;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import es.ujaen.dae.ujahotel.utils.Tipo;

@Entity
public class Habitacion {

    @Id
    @NotEmpty
    private int numeroHab;
    @NotEmpty
    private Tipo tipo;

    public Habitacion(){
        this.numeroHab = 0;
        this.tipo = Tipo.SIMPLE;
    }

    public Habitacion(int numeroHab, Tipo tipo){
        this.numeroHab = numeroHab;
        this.tipo = tipo;
    }

    public int getNumeroHab() {
        return numeroHab;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Habitacion{" +
                "numeroHab=" + numeroHab +
                ", tipo=" + tipo +
                '}';
    }
}
