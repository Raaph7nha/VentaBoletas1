package com.sistemaboletas.ventanas;

import com.sistemaboletas.datos.GestorDatos;

import javax.swing.*;
import java.awt.*;

public class PanelListaEventos extends JPanel {

    private VentanaPrincipal ventana;

    public PanelListaEventos(VentanaPrincipal ventana) {
        this.ventana = ventana;

        setLayout(new BorderLayout());
        JButton btnRefrescar = new JButton("Actualizar Lista");
        btnRefrescar.addActionListener(e -> cargarEventos());

        add(btnRefrescar, BorderLayout.NORTH);
        cargarEventos();
    }

    private void cargarEventos() {
        JPanel grid = new JPanel(new GridLayout(0, 3, 10, 10));

        GestorDatos.getInstancia().getEventos().forEach(ev -> {

            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            card.setBackground(Color.WHITE);

            JButton btnComprar = new JButton("Comprar Boletas");
            btnComprar.addActionListener(e -> ventana.abrirCompra(ev));

            card.add(btnComprar, BorderLayout.SOUTH);
            grid.add(card);
        });

        removeAll();
        add(new JScrollPane(grid), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}