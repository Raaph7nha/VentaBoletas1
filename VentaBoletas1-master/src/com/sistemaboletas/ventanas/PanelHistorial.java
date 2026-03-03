package com.sistemaboletas.ventanas;

import com.sistemaboletas.datos.GestorDatos;
import com.sistemaboletas.modelos.Asiento;
import com.sistemaboletas.modelos.Compra;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class PanelHistorial extends JPanel {
    private JTextArea area;
    public PanelHistorial() {
        setLayout(new BorderLayout());
        area = new JTextArea();
        area.setEditable(false);
        JButton btn = new JButton("Cargar Mis Compras");
        btn.addActionListener(e -> cargarHistorial());

        add(new JScrollPane(area), BorderLayout.CENTER);
        add(btn, BorderLayout.NORTH);
    }

    private void cargarHistorial() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        for (Compra c : GestorDatos.getInstancia().getCompras()) {
            sb.append("------------------------------------------------\n");
            sb.append("EVENTO: ").append(c.getNombreEvento()).append("\n");
            sb.append("CLIENTE: ").append(c.getNombreCliente()).append("\n");
            sb.append("FECHA COMPRA: ").append(dtf.format(c.getFechaCompra())).append("\n");
            sb.append("ESTADO: ").append(c.isPagada() ? "PAGADA" : "RESERVADA (Pendiente)").append("\n");
            sb.append("ASIENTOS: \n");
            for (Asiento a : c.getAsientosComprados()) {
                sb.append("  - ").append(a.toString()).append("\n");
            }
            sb.append("TOTAL: $").append(c.getTotal()).append("\n");
        }
        area.setText(sb.toString());
    }
}