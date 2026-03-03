package com.sistemaboletas;

import com.sistemaboletas.ventanas.VentanaPrincipal;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
