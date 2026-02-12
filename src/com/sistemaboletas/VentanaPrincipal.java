package com.sistemaboletas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private JTabbedPane tabs;

    public VentanaPrincipal() {
        setTitle("Sistema de Gestión de Eventos y Boletas");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabs = new JTabbedPane();
        tabs.addTab("Eventos", new PanelListaEventos());
        tabs.addTab("Crear Evento", new PanelCrearEvento());
        tabs.addTab("Reporte Pagos", new PanelReportePagos());
        tabs.addTab("Mis Compras", new PanelHistorial());

        add(tabs);
    }

    class PanelListaEventos extends JPanel {
        public PanelListaEventos() {
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

                String info = "<html><b>" + ev.getNombre() + "</b><br>" +
                        ev.getFecha() + " " + ev.getHora() + "<br>" +
                        ev.getLugar() + "</html>";
                card.add(new JLabel(info, SwingConstants.CENTER), BorderLayout.CENTER);

                JButton btnComprar = new JButton("Comprar Boletas");
                btnComprar.setBackground(new Color(66, 133, 244));
                btnComprar.setForeground(Color.WHITE);
                btnComprar.addActionListener(e -> abrirCompra(ev));

                card.add(btnComprar, BorderLayout.SOUTH);
                grid.add(card);
            });

            removeAll();
            add(new JScrollPane(grid), BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    private void abrirCompra(Evento ev) {
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

    class PanelCrearEvento extends JPanel {
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

    class PanelHistorial extends JPanel {
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

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}