package com.sistemaboletas.modelos;

import java.io.Serializable;

public class Asiento implements Serializable {
    private String zona;
    private int fila;
    private int numero;
    private double precio;
    private EstadoAsiento estado;



    public Asiento(String zona, int fila, int numero, double precio) {
        this.zona = zona;
        this.fila = fila;
        this.numero = numero;
        this.precio = precio;
        this.estado = EstadoAsiento.DISPONIBLE;
    }

    public String getZona() { return zona; }
    public int getFila() { return fila; }
    public int getNumero() { return numero; }
    public double getPrecio() { return precio; }
    public EstadoAsiento getEstado() { return estado; }
    public void setEstado(EstadoAsiento estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Zona " + zona + " - Fila " + fila + " - Asiento " + numero;
    }
}