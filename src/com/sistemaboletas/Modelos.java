package com.sistemaboletas;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

enum EstadoAsiento {
    DISPONIBLE, SELECCIONADO, RESERVADO, VENDIDO
}

enum MetodoPago {
    TARJETA_CREDITO, TARJETA_DEBITO, PSE
}

class Asiento implements Serializable {
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

class Evento implements Serializable {
    private String id;
    private String nombre;
    private String fecha;
    private String hora;
    private String lugar;
    private String patrocinador;
    private List<Asiento> asientos;

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

class Compra implements Serializable {
    private String id;
    private String eventoId;
    private String nombreEvento;
    private String nombreCliente;
    private String cedula;
    private MetodoPago metodoPago;
    private List<Asiento> asientosComprados;
    private double total;
    private LocalDateTime fechaCompra;
    private boolean pagada;

    public Compra(String eventoId, String nombreEvento, String nombreCliente, String cedula, MetodoPago metodo, List<Asiento> asientos) {
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.nombreEvento = nombreEvento;
        this.nombreCliente = nombreCliente;
        this.cedula = cedula;
        this.metodoPago = metodo;
        this.asientosComprados = new ArrayList<>(asientos);
        this.fechaCompra = LocalDateTime.now();
        this.pagada = false;
        this.total = asientos.stream().mapToDouble(Asiento::getPrecio).sum();
    }

    public String getId() { return id; }
    public String getNombreEvento() { return nombreEvento; }
    public String getNombreCliente() { return nombreCliente; }
    public String getCedula() { return cedula; }
    public double getTotal() { return total; }
    public boolean isPagada() { return pagada; }
    public void setPagada(boolean pagada) { this.pagada = pagada; }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public List<Asiento> getAsientosComprados() { return asientosComprados; }
    public MetodoPago getMetodoPago() { return metodoPago; }
}