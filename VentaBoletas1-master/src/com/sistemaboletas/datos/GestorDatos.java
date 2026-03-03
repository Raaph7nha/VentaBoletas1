package com.sistemaboletas.datos;

import com.sistemaboletas.modelos.Asiento;
import com.sistemaboletas.modelos.Compra;
import com.sistemaboletas.modelos.EstadoAsiento;
import com.sistemaboletas.modelos.Evento;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class GestorDatos {
    private static GestorDatos instancia;
    private List<Evento> eventos;
    private List<Compra> compras;
    private final String ARCHIVO_EVENTOS = "eventos.dat";
    private final String ARCHIVO_COMPRAS = "compras.dat";

    private GestorDatos() {
        eventos = cargarEventos();
        compras = cargarCompras();
        validarReservasExpiradas();
    }

    public static GestorDatos getInstancia() {
        if (instancia == null) instancia = new GestorDatos();
        return instancia;
    }

    public List<Evento> getEventos() { return eventos; }
    public List<Compra> getCompras() { return compras; }

    public void agregarEvento(Evento e) {
        eventos.add(e);
        guardarDatos();
    }

    public void registrarCompra(Compra c) {
        compras.add(c);
        guardarDatos();
    }

    public void guardarDatos() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO_EVENTOS))) {
            out.writeObject(eventos);
        } catch (IOException e) { e.printStackTrace(); }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO_COMPRAS))) {
            out.writeObject(compras);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private List<Evento> cargarEventos() {
        File f = new File(ARCHIVO_EVENTOS);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Evento>) in.readObject();
        } catch (Exception e) { return new ArrayList<>(); }
    }

    @SuppressWarnings("unchecked")
    private List<Compra> cargarCompras() {
        File f = new File(ARCHIVO_COMPRAS);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Compra>) in.readObject();
        } catch (Exception e) { return new ArrayList<>(); }
    }

    public void validarReservasExpiradas() {
        LocalDateTime ahora = LocalDateTime.now();
        for (Compra c : compras) {
            if (!c.isPagada() && ChronoUnit.HOURS.between(c.getFechaCompra(), ahora) >= 24) {
                Evento evento = eventos.stream().filter(e -> e.getId().equals(c.getId())).findFirst().orElse(null);
                if (evento != null) {
                    for (Asiento asientoCompra : c.getAsientosComprados()) {
                        evento.getAsientos().stream()
                                .filter(a -> a.getZona().equals(asientoCompra.getZona()) &&
                                        a.getFila() == asientoCompra.getFila() &&
                                        a.getNumero() == asientoCompra.getNumero())
                                .findFirst()
                                .ifPresent(a -> a.setEstado(EstadoAsiento.DISPONIBLE));
                    }
                }
            }
        }
        guardarDatos();
    }
}