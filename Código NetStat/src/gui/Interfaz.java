package gui;

import logic.Controlador;
import logic.ScanTimeOutException;
import logic.IPRangeOutofBoundsException;
import util.IPv4;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Interfaz extends JFrame {

    private JTextField campoInicioIP, campoFinIP, campoTiempo;
    private JLabel estadoInicioIP, estadoFinIP;
    private JTable resultadoTabla;
    private JProgressBar barraProgreso;
    
    private JComboBox<String> sortFilterCombo;
    private TableRowSorter<DefaultTableModel> ordenador;
    
    private Controlador controlador ;
    private JButton btnGuardado, btnParar,btnLimpiar, btnEscanear;  

    public Interfaz() {
        super("🌐 Escáner de Red");

        // Colores base
        Color bgColor = new Color(240, 248, 255);
        Color panelColor = new Color(200, 220, 240);
        Color buttonScan = new Color(100, 200, 150);
        Color buttonClear = new Color(220, 100, 100);


        setLayout(new BorderLayout());
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(bgColor);

        // Panel superior con GridBagLayout
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(panelColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        campoInicioIP = new JTextField();
        campoFinIP = new JTextField();
        campoTiempo = new JTextField();

        // Labels de estado
        estadoInicioIP = new JLabel(" ");
        estadoFinIP = new JLabel(" ");
        
        estadoInicioIP.setForeground(Color.GRAY);
        estadoFinIP.setForeground(Color.GRAY);
        btnEscanear = new JButton("🔍 Escanear");
        btnEscanear.setBackground(buttonScan);
        btnEscanear.setForeground(Color.WHITE);
        btnEscanear.setFocusPainted(false);

        btnParar = new JButton("⏹ Parar");
        btnParar.setBackground(new Color(255, 165, 0));
        btnParar.setForeground(Color.WHITE);
        btnParar.setFocusPainted(false);

        btnLimpiar = new JButton("🗑 Limpiar");
        btnLimpiar.setBackground(buttonClear);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);

        btnGuardado = new JButton("💾 Guardar"); 
        btnGuardado.setBackground(new Color(70,130,180));
        btnGuardado.setForeground(Color.WHITE);
        btnGuardado.setFocusPainted(false);

        String[] opciones = {
                "Ordenar IP ascendente",
                "Ordenar IP descendente",
                "Mostrar solo 'No Conectado'",
                "Mostrar solo 'Conectado'"
        };
        
        sortFilterCombo = new JComboBox<>(opciones);
        sortFilterCombo.setEnabled(false);

        barraProgreso = new JProgressBar();
        barraProgreso.setStringPainted(true);
        barraProgreso.setForeground(new Color(100, 149, 237));

        int y = 0;

     // Fila 1: Inicio
        gbc.gridy = y;
        gbc.gridx = 0; topPanel.add(new JLabel("IP Inicio:"), gbc);
        gbc.gridx = 1; topPanel.add(campoInicioIP, gbc);
        gbc.gridx = 2; topPanel.add(estadoInicioIP, gbc);
        y++;

        // Fila 2: Fin
        gbc.gridy = y;
        gbc.gridx = 0; topPanel.add(new JLabel("IP Fin:"), gbc);
        gbc.gridx = 1; topPanel.add(campoFinIP, gbc);
        gbc.gridx = 2; topPanel.add(estadoFinIP, gbc);
        y++;

        // Fila 3: Tiempo + Iniciar
        gbc.gridy = y;
        gbc.gridx = 0; topPanel.add(new JLabel("Tiempo:"), gbc);
        gbc.gridx = 1; topPanel.add(campoTiempo, gbc);
        y++;

        // Fila 4: Ordenar + Parar
        gbc.gridy = y;
        gbc.gridx = 0; topPanel.add(new JLabel("Ordenar/Filtrar:"), gbc);
        gbc.gridx = 1; topPanel.add(sortFilterCombo, gbc);
        gbc.gridx = 2; topPanel.add(btnEscanear, gbc);
        y++;

        // Fila 5: Botones
        gbc.gridy = y;
        gbc.gridx = 0; topPanel.add(btnParar, gbc);
        gbc.gridx = 1; topPanel.add(btnLimpiar, gbc);
        gbc.gridx = 2; topPanel.add(btnGuardado, gbc); 

        add(topPanel, BorderLayout.NORTH);
        add(barraProgreso, BorderLayout.SOUTH);
       
        
        // Tabla personalizada
        resultadoTabla = new JTable(new DefaultTableModel(
                new Object[]{"IP", "Nombre", "Conectado", "Tiempo (ms)"}, 0));

        resultadoTabla.setRowHeight(25);
        resultadoTabla.setSelectionBackground(new Color(173, 216, 230));

        DefaultTableCellRenderer lectordeCelda = new DefaultTableCellRenderer() {
        	   @Override
               
               public Component getTableCellRendererComponent(JTable table, Object value,
                   boolean isSelected, boolean hasFocus,
                   int row, int column) {
                   Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                   if (!isSelected) {
                       c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                   }
                   return c;
               }
        };
        for (int i = 0; i < resultadoTabla.getColumnCount(); i++) {
            resultadoTabla.getColumnModel().getColumn(i).setCellRenderer(lectordeCelda);
        }

        JTableHeader encabezado = resultadoTabla.getTableHeader();
        encabezado.setBackground(new Color(100, 149, 237));
        encabezado.setForeground(Color.WHITE);
        encabezado.setFont(encabezado.getFont().deriveFont(Font.BOLD));

        add(new JScrollPane(resultadoTabla), BorderLayout.CENTER);

        DefaultTableModel model = (DefaultTableModel) resultadoTabla.getModel();
        ordenador = new TableRowSorter<>(model);
        resultadoTabla.setRowSorter(ordenador);

        ordenador.setComparator(0, (a, b) -> IPv4.compararIPs(a.toString(), b.toString()));

        sortFilterCombo.addActionListener(e -> aplicarOrdenFiltro());
        
        controlador = new Controlador();
        
        btnEscanear.addActionListener(e -> startScan());
        btnParar.addActionListener(e -> controlador.stopScan());
        btnLimpiar.addActionListener(e -> clearTable());
        btnGuardado.addActionListener(e -> controlador.guardarResultados(resultadoTabla));
        

        // 🔹 Validación en vivo
        addValidation(campoInicioIP, estadoInicioIP);
        addValidation(campoFinIP, estadoFinIP);
        
    }
    

    private void addValidation(JTextField field, JLabel statusLabel) {
        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void validateIP() {
                String text = field.getText().trim();
                if (text.isEmpty()) {
                    field.setForeground(Color.BLACK);
                    statusLabel.setText(" ");
                    statusLabel.setForeground(Color.GRAY);}
                
                else if (IPv4.validarIP(text)) {
                    field.setForeground(new Color(0,128,0));
                    statusLabel.setText("✔ Válida");
                    statusLabel.setForeground(new Color(0,128,0));}
                
                else {
                    field.setForeground(Color.RED);
                    statusLabel.setText("✘ Inválida");
                    statusLabel.setForeground(Color.RED);}
                
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { validateIP(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { validateIP(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { validateIP(); }
        });
    }

    private void startScan() {
        String IPinicio = campoInicioIP.getText();
        String IPfin = campoFinIP.getText();
        String time = campoTiempo.getText();

        if (!IPv4.validarIP(IPinicio) || !IPv4.validarIP(IPfin)) {
            JOptionPane.showMessageDialog(this, "IP inválida", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (IPv4.validarMayorIp(IPinicio, IPfin)) {
            JOptionPane.showMessageDialog(this, "La IP inicial es mayor que la final.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int tiempoNumerico;
        try {
            tiempoNumerico = Integer.parseInt(time);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El tiempo debe ser numérico", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tiempoNumerico < 3) {
            JOptionPane.showMessageDialog(this, "El tiempo de espera debe ser mayor a 2s", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<String> ips = new ArrayList<>();
        try {
            ips.addAll(IPv4.generarRangoIP(IPinicio, IPfin));
        } catch (IPRangeOutofBoundsException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        barraProgreso.setMaximum(ips.size());
        DefaultTableModel model = (DefaultTableModel) resultadoTabla.getModel();
        model.setRowCount(0);

        new Thread(() -> {
            try {
                controlador.escanearRango(ips, (tiempoNumerico * 1000), model,barraProgreso);
            } catch (ScanTimeOutException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();

        sortFilterCombo.setEnabled(true);
        sortFilterCombo.setSelectedIndex(0);
    }

    private void clearTable() {
        ((DefaultTableModel) resultadoTabla.getModel()).setRowCount(0);
        barraProgreso.setValue(0);
        barraProgreso.setString("");
        sortFilterCombo.setEnabled(false);
    }

    private void aplicarOrdenFiltro() {
        String seleccion = (String) sortFilterCombo.getSelectedItem();
        ordenador.setRowFilter(null);
        ordenador.setSortKeys(null);

        if ("Ordenar IP ascendente".equals(seleccion)) {
            ordenador.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        } else if ("Ordenar IP descendente".equals(seleccion)) {
            ordenador.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
        } else if ("Mostrar solo 'No Conectado'".equals(seleccion)) {
            ordenador.setRowFilter(RowFilter.regexFilter("^No Conectado$", 2));
        } else if ("Mostrar solo 'Conectado'".equals(seleccion)) {
            ordenador.setRowFilter(RowFilter.regexFilter("^Conectado$", 2));
        }
    }

    public JTextField getcampoInicioIP() {return campoInicioIP;}
    public JTextField getcampoFinIP() {return campoFinIP;}
    public JTextField getcampoTiempo() {return campoTiempo;}
    public JProgressBar getbarraProgreso() {return barraProgreso;}
    public JComboBox<String> getSortFilterCombo() {return sortFilterCombo;}
}
