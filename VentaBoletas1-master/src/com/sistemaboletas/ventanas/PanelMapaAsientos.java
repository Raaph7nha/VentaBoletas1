package com.sistemaboletas.ventanas;

import com.sistemaboletas.modelos.Asiento;
import com.sistemaboletas.modelos.Evento;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelMapaAsientos extends JPanel {
    private Evento evento;
    private List<Asiento> seleccionados;
    private JLabel lblResumen;
    private final int MAX_SELECCION = 10;

    public PanelMapaAsientos(Evento evento, JLabel lblResumen) {
        this.evento = evento;
        this.lblResumen = lblResumen;
        this.seleccionados = new ArrayList<>();
        setLayout(new BorderLayout());

        JPanel panelScroll = new JPanel();
        panelScroll.setLayout(new BoxLayout(panelScroll, BoxLayout.Y_AXIS));

        panelScroll.add(crearPanelZona("ZONA A - Preferencial ($100.000)", "A", 10, 20));
        panelScroll.add(Box.createRigidArea(new Dimension(0, 20)));
        panelScroll.add(crearPanelZona("ZONA B - General ($75.000)", "B", 5, 20)); // Filas 11-15 (5 filas)
        panelScroll.add(Box.createRigidArea(new Dimension(0, 20)));
        panelScroll.add(crearPanelZona("ZONA C - Localidad ($50.000)", "C", 3, 18)); // Filas 16-18 (3 filas)

        JScrollPane scroll = new JScrollPane(panelScroll);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        JPanel leyenda = new JPanel();
        leyenda.add(crearIndicador(Color.GREEN, "Disponible"));
        leyenda.add(crearIndicador(Color.CYAN, "Tu Selección"));
        leyenda.add(crearIndicador(Color.ORANGE, "Reservado"));
        leyenda.add(crearIndicador(Color.RED, "Vendido"));
        add(leyenda, BorderLayout.SOUTH);
    }

    private JPanel crearIndicador(Color c, String texto) {
        JPanel p = new JPanel();
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setBackground(c);
        p.add(colorBox);
        p.add(new JLabel(texto));
        return p;
    }

    private JPanel crearPanelZona(String titulo, String zonaNombre, int filas, int cols) {
        JPanel panelZona = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        panelZona.add(lblTitulo, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(filas, cols, 2, 2));

        int filaInicio = zonaNombre.equals("A") ? 1 : (zonaNombre.equals("B") ? 11 : 16);

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                int numFila = filaInicio + i;
                int numAsiento = j + 1;

                Asiento asientoReal = buscarAsiento(zonaNombre, numFila, numAsiento);
                JButton btn = new JButton(String.valueOf(numAsiento));
                btn.setPreferredSize(new Dimension(45, 30));
                btn.setMargin(new Insets(2,2,2,2));
                btn.setFont(new Font("Arial", Font.PLAIN, 10));

                configurarBoton(btn, asientoReal);
                grid.add(btn);
            }
        }

        JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flow.add(grid);
        panelZona.add(flow, BorderLayout.CENTER);
        return panelZona;
    }

    private Asiento buscarAsiento(String zona, int f, int n) {
        return evento.getAsientos().stream()
                .filter(a -> a.getZona().equals(zona) && a.getFila() == f && a.getNumero() == n)
                .findFirst().orElse(null);
    }

    private void configurarBoton(JButton btn, Asiento asiento) {
        if (asiento == null) return;

        switch (asiento.getEstado()) {
            case DISPONIBLE: btn.setBackground(Color.GREEN); break;
            case SELECCIONADO: btn.setBackground(Color.CYAN); break; // Solo visual local
            case RESERVADO: btn.setBackground(Color.ORANGE); btn.setEnabled(false); break;
            case VENDIDO: btn.setBackground(Color.RED); btn.setEnabled(false); break;
        }

        btn.addActionListener(e -> {
            if (seleccionados.contains(asiento)) {
                seleccionados.remove(asiento);
                btn.setBackground(Color.GREEN);
            } else {
                if (seleccionados.size() >= MAX_SELECCION) {
                    JOptionPane.showMessageDialog(this, "Máximo 10 boletas por compra.");
                    return;
                }
                seleccionados.add(asiento);
                btn.setBackground(Color.CYAN);
            }
            actualizarResumen();
        });
    }

    private void actualizarResumen() {
        double total = seleccionados.stream().mapToDouble(Asiento::getPrecio).sum();
        lblResumen.setText("Seleccionados: " + seleccionados.size() + " | Total: $" + total);
    }

    public List<Asiento> getSeleccionados() {
        return seleccionados;
    }
}