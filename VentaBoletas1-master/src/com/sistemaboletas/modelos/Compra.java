package com.sistemaboletas.modelos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Compra implements Serializable {
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