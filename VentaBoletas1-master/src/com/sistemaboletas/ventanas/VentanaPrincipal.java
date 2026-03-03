package com.sistemaboletas.ventanas;

import com.sistemaboletas.datos.GestorDatos;
import com.sistemaboletas.modelos.Asiento;
import com.sistemaboletas.modelos.Compra;
import com.sistemaboletas.modelos.Evento;
import com.sistemaboletas.modelos.EstadoAsiento;
import com.sistemaboletas.modelos.MetodoPago;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private JTabbedPane tabs;

    public VentanaPrincipal() {
        setTitle("Sistema de Gestión de Eventos y Boletas");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabs = new JTabbedPane();
        tabs.addTab("Eventos", new PanelListaEventos(this));
        tabs.addTab("Crear Evento", new PanelCrearEvento());
        tabs.addTab("Reporte Pagos", new PanelReportePagos());
        tabs.addTab("Mis Compras", new PanelHistorial());

        add(tabs);
    }



    public void abrirCompra(Evento ev) {
        JDialog dialog = new JDialog(this, "Compra: " + ev.getNombre(), true);
        dialog.setSize(900, 600);
        dialog.setLayout(new BorderLayout());

        JLabel lblResumen = new JLabel("Seleccionados: 0 | Total: $0");
        lblResumen.setFont(new Font("Arial", Font.BOLD, 16));
        lblResumen.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        PanelMapaAsientos mapa = new PanelMapaAsientos(ev, lblResumen);

        JPanel panelControles = new JPanel(new BorderLayout());
        panelControles.add(lblResumen, BorderLayout.WEST);

        JButton btnContinuar = new JButton("Confirmar Selección");
        btnContinuar.addActionListener(e -> {
            if (mapa.getSeleccionados().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Seleccione al menos un asiento");
                return;
            }
            mostrarFormularioPago(dialog, ev, mapa.getSeleccionados());
        });

        panelControles.add(btnContinuar, BorderLayout.EAST);

        dialog.add(mapa, BorderLayout.CENTER);
        dialog.add(panelControles, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void mostrarFormularioPago(JDialog parent, Evento evento, List<Asiento> asientos) {
        JDialog dialogPago = new JDialog(parent, "Datos del Comprador", true);
        dialogPago.setSize(400, 350);
        dialogPago.setLayout(new GridLayout(6, 2, 10, 10));

        JTextField txtNombre = new JTextField();
        JTextField txtCedula = new JTextField();
        JComboBox<MetodoPago> cmbPago = new JComboBox<>(MetodoPago.values());

        dialogPago.add(new JLabel("Nombre Completo:")); dialogPago.add(txtNombre);
        dialogPago.add(new JLabel("Cédula:")); dialogPago.add(txtCedula);
        dialogPago.add(new JLabel("Método de Pago:")); dialogPago.add(cmbPago);

        JButton btnFinalizar = new JButton("Finalizar Reserva");
        btnFinalizar.addActionListener(e -> {
            if (txtNombre.getText().isEmpty() || txtCedula.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialogPago, "Campos obligatorios");
                return;
            }

            Compra nuevaCompra = new Compra(
                    evento.getId(), evento.getNombre(),
                    txtNombre.getText(), txtCedula.getText(),
                    (MetodoPago) cmbPago.getSelectedItem(), asientos
            );

            asientos.forEach(a -> a.setEstado(EstadoAsiento.RESERVADO));
            GestorDatos.getInstancia().registrarCompra(nuevaCompra);
            GestorDatos.getInstancia().guardarDatos();

            JOptionPane.showMessageDialog(dialogPago, "Reserva exitosa por 24 horas.");
            dialogPago.dispose();
            parent.dispose();
            tabs.setSelectedIndex(3); // Ir a historial
        });

        dialogPago.add(new JLabel(""));
        dialogPago.add(btnFinalizar);
        dialogPago.setLocationRelativeTo(parent);
        dialogPago.setVisible(true);
    }



    class PanelReportePagos extends JPanel {
        private JTable tabla;
        private DefaultTableModel modelo;

        public PanelReportePagos() {
            setLayout(new BorderLayout());
            modelo = new DefaultTableModel(new String[]{"ID Compra", "Cliente", "Evento", "Total", "Estado"}, 0);
            tabla = new JTable(modelo);

            JButton btnPagar = new JButton("Marcar como PAGADA");
            JButton btnRefrescar = new JButton("Refrescar");

            btnRefrescar.addActionListener(e -> cargarDatos());
            btnPagar.addActionListener(e -> marcarPagada());

            JPanel pnlBotones = new JPanel();
            pnlBotones.add(btnRefrescar);
            pnlBotones.add(btnPagar);

            add(new JScrollPane(tabla), BorderLayout.CENTER);
            add(pnlBotones, BorderLayout.SOUTH);
            cargarDatos();
        }

        private void cargarDatos() {
            modelo.setRowCount(0);
            for (Compra c : GestorDatos.getInstancia().getCompras()) {
                if (!c.isPagada()) {
                    modelo.addRow(new Object[]{c.getId(), c.getNombreCliente(), c.getNombreEvento(), c.getTotal(), "RESERVADA"});
                }
            }
        }

        private void marcarPagada() {
            int row = tabla.getSelectedRow();
            if (row == -1) return;
            String id = (String) modelo.getValueAt(row, 0);

            GestorDatos.getInstancia().getCompras().stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst()
                    .ifPresent(c -> {
                        c.setPagada(true);
                        c.getAsientosComprados().forEach(a -> {
                            // Buscar el asiento real en memoria y actualizarlo a VENDIDO
                            GestorDatos.getInstancia().getEventos().forEach(ev ->
                                    ev.getAsientos().stream()
                                            .filter(as -> as.getZona().equals(a.getZona()) && as.getFila() == a.getFila() && as.getNumero() == a.getNumero())
                                            .findFirst()
                                            .ifPresent(real -> real.setEstado(EstadoAsiento.VENDIDO))
                            );
                        });
                    });
            GestorDatos.getInstancia().guardarDatos();
            cargarDatos();
            JOptionPane.showMessageDialog(this, "Pago Registrado");
        }
    }

}