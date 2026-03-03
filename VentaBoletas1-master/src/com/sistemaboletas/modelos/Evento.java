package com.sistemaboletas.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Evento implements Serializable {
    private String id;
    private String nombre;
    private String fecha;
    private String hora;
    private String lugar;
    private String patrocinador;
    private List<Asiento> asientos;
    private final int numerodesillas;

    public Evento(String nombre, String fecha, String hora, String lugar, String patrocinador) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.lugar = lugar;
        this.patrocinador = patrocinador;
        this.asientos = new ArrayList<>();
        inicializarAsientos();
    }

    private void inicializarAsientos() {
        // Zona A: Filas 1-10 (20 asientos)
        for(int f=1; f<=10; f++) {
            for(int n=1; n<=20; n++) asientos.add(new Asiento("A", f, n, 100000));
        }
        // Zona B: Filas 11-15 (20 asientos)
        for(int f=11; f<=15; f++) {
            for(int n=1; n<=20; n++) asientos.add(new Asiento("B", f, n, 75000));
        }
        // Zona C: Filas 16-18 (18 asientos)
        for(int f=16; f<=18; f++) {
            for(int n=1; n<=18; n++) asientos.add(new Asiento("C", f, n, 50000));
        }
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getLugar() { return lugar; }
    public List<Asiento> getAsientos() { return asientos; }

    @Override
    public String toString() { return nombre + " (" + fecha + ")"; }
}