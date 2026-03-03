package com.sistemaboletas.ventanas;

import com.sistemaboletas.datos.GestorDatos;
import com.sistemaboletas.modelos.Evento;

import javax.swing.*;
import java.awt.*;

public class PanelCrearEvento extends JPanel {
    public PanelCrearEvento() {
        setLayout(new GridLayout(6, 2, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(50,50,50,50));

        JTextField txtNombre = new JTextField();
        JTextField txtFecha = new JTextField("DD/MM/AAAA");
        JTextField txtHora = new JTextField("HH:MM");
        JTextField txtLugar = new JTextField();
        JTextField txtPatrocinador = new JTextField();

        add(new JLabel("Nombre del Evento:")); add(txtNombre);
        add(new JLabel("Fecha:")); add(txtFecha);
        add(new JLabel("Hora:")); add(txtHora);
        add(new JLabel("Lugar:")); add(txtLugar);
        add(new JLabel("Patrocinador:")); add(txtPatrocinador);

        JButton btnGuardar = new JButton("Crear Evento");
        btnGuardar.addActionListener(e -> {
            Evento nuevo = new Evento(txtNombre.getText(), txtFecha.getText(), txtHora.getText(), txtLugar.getText(), txtPatrocinador.getText());
            GestorDatos.getInstancia().agregarEvento(nuevo);
            JOptionPane.showMessageDialog(this, "Evento Creado");
            txtNombre.setText("");
        });

        add(new JLabel("")); add(btnGuardar);
    }
}