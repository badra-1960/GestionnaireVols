/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestionnairevols;

import java.io.Serializable;

/**
 *
 * @author fabi8
 */
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private int id;
    private String nomPassager;
    private String depart;
    private String destination;
    private String numeroVol;
    
    
    public Reservation(int id) {
        this.id = id;
    }
    
    
    public Reservation(int id, String nomPassager, String depart, String destination, String numeroVol) {
        this.id = id;
        this.nomPassager = nomPassager;
        this.depart = depart;
        this.destination = destination;
        this.numeroVol = numeroVol;
    
    }
   
    
    public int getId()             { return id; }
    public String getNomPassager() { return nomPassager; }
    public String getdepart()      { return depart; }
    public String getDestination() { return destination; }
    public String getNumeroVol()   { return numeroVol; }

    
    public void setNomPassager(String nomPassager) { this.nomPassager = nomPassager; }
    public void setdepart(String depart)           { this.depart = depart; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setNumeroVol(String numeroVol)     { this.numeroVol = numeroVol; }
    
    
} 