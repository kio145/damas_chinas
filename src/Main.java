package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class Main extends JFrame {

    static final Color[] COLORES = {
        Color.RED,
        Color.BLUE,
        new Color(0, 180, 0),
        new Color(220, 180, 0),
        Color.WHITE,
        new Color(128, 0, 128)
    };

    static final String[] NOMBRES = {
        "Rojo", "Azul", "Verde", "Amarillo", "Blanco", "Morado"
    };

    private Tablero tablero;
    private Fichas fichas;
    private Movimiento movimiento;

    private int cantidadJugadores;
    private int[] jugadoresActivos;
    private int indiceTurno = 0;
    private int turnoActual = 0;

    private int selFila = -1;
    private int selCol = -1;

    private List<int[]> movimientosLegales = null;
    private boolean juegoTerminado = false;

    private JPanel canvas;

    private JLabel lblTurno;
    private JLabel lblCronometro;
    private JLabel lblNombreTurno;
    private JLabel lblMensajeTurno;
    private JPanel panelColorTurno;
    private JPanel panelIndicadorTurno;

    private Timer timerCronometro;
    private int segundosPartida = 0;

    public Main() {
        cantidadJugadores = pedirCantidadJugadores();

        iniciarObjetosJuego();

        setTitle("Damas Chinas - XP");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));
        getContentPane().setBackground(new Color(30, 30, 40));

        JLabel titulo = new JLabel("DAMAS CHINAS", JLabel.CENTER);
        titulo.setFont(new Font("Georgia", Font.BOLD, 22));
        titulo.setForeground(new Color(255, 215, 0));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 2, 0));
        add(titulo, BorderLayout.NORTH);

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujar((Graphics2D) g);
            }
        };

        canvas.setPreferredSize(new Dimension(600, 580));
        canvas.setBackground(new Color(30, 30, 40));

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                manejarClic(e.getX(), e.getY());
            }
        });

        add(canvas, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(30, 30, 40));

        panelIndicadorTurno = crearPanelIndicadorTurno();

        lblTurno = new JLabel("", JLabel.CENTER);
        lblTurno.setFont(new Font("Arial", Font.BOLD, 13));
        lblTurno.setForeground(Color.WHITE);
        lblTurno.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));

        lblCronometro = new JLabel("Tiempo de partida: 00:00", JLabel.CENTER);
        lblCronometro.setFont(new Font("Arial", Font.BOLD, 13));
        lblCronometro.setForeground(Color.WHITE);
        lblCronometro.setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));

        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setBackground(new Color(30, 30, 40));
        panelTextos.add(lblTurno);
        panelTextos.add(lblCronometro);

        JButton btnReiniciar = new JButton("Reiniciar partida");
        btnReiniciar.setFont(new Font("Arial", Font.BOLD, 13));
        btnReiniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarPartida();
            }
        });

        JButton btnNuevaPartida = new JButton("Nueva partida");
        btnNuevaPartida.setFont(new Font("Arial", Font.BOLD, 13));
        btnNuevaPartida.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nuevaPartida();
            }
        });

        JButton btnCambiarJugadores = new JButton("Cambiar jugadores");
        btnCambiarJugadores.setFont(new Font("Arial", Font.BOLD, 13));
        btnCambiarJugadores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarCantidadJugadores();
            }
        });

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(new Color(30, 30, 40));
        panelBotones.add(btnReiniciar);
        panelBotones.add(btnNuevaPartida);
        panelBotones.add(btnCambiarJugadores);

        JPanel panelCentroInferior = new JPanel(new BorderLayout());
        panelCentroInferior.setBackground(new Color(30, 30, 40));
        panelCentroInferior.add(panelIndicadorTurno, BorderLayout.NORTH);
        panelCentroInferior.add(panelTextos, BorderLayout.CENTER);
        panelCentroInferior.add(panelBotones, BorderLayout.SOUTH);

        panelInferior.add(panelCentroInferior, BorderLayout.CENTER);

        add(panelInferior, BorderLayout.SOUTH);

        actualizarTextoTurno();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        iniciarCronometro();
    }

    private JPanel crearPanelIndicadorTurno() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(20, 20, 30));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        JLabel tituloTurno = new JLabel("TURNO ACTUAL");
        tituloTurno.setFont(new Font("Arial", Font.BOLD, 12));
        tituloTurno.setForeground(new Color(255, 215, 0));

        lblNombreTurno = new JLabel("");
        lblNombreTurno.setFont(new Font("Arial", Font.BOLD, 24));
        lblNombreTurno.setForeground(Color.WHITE);

        lblMensajeTurno = new JLabel("");
        lblMensajeTurno.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMensajeTurno.setForeground(new Color(210, 210, 210));

        JPanel panelTexto = new JPanel(new GridLayout(3, 1));
        panelTexto.setBackground(new Color(20, 20, 30));
        panelTexto.add(tituloTurno);
        panelTexto.add(lblNombreTurno);
        panelTexto.add(lblMensajeTurno);

        panelColorTurno = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color = COLORES[turnoActual];

                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillOval(7, 7, 42, 42);

                g2.setColor(color);
                g2.fillOval(4, 4, 42, 42);

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(4, 4, 42, 42);
            }
        };

        panelColorTurno.setPreferredSize(new Dimension(55, 55));
        panelColorTurno.setBackground(new Color(20, 20, 30));

        panel.add(panelColorTurno, BorderLayout.WEST);
        panel.add(panelTexto, BorderLayout.CENTER);

        return panel;
    }

    private int pedirCantidadJugadores() {
        Integer[] opciones = {2, 3, 4, 5, 6};

        Integer seleccion = (Integer) JOptionPane.showInputDialog(
            null,
            "Seleccione la cantidad de jugadores:",
            "Configuración de partida",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        if (seleccion == null) {
            return 2;
        }

        return seleccion.intValue();
    }

    private void iniciarObjetosJuego() {
        tablero = new Tablero();
        fichas = new Fichas(tablero, cantidadJugadores);
        movimiento = new Movimiento(tablero, fichas);

        jugadoresActivos = fichas.getJugadoresActivos();

        indiceTurno = 0;
        turnoActual = jugadoresActivos[indiceTurno];

        selFila = -1;
        selCol = -1;
        movimientosLegales = null;
        juegoTerminado = false;
    }

    private void reiniciarPartida() {
        iniciarObjetosJuego();
        reiniciarCronometro();

        actualizarTextoTurno();
        canvas.repaint();

        JOptionPane.showMessageDialog(
            this,
            "La partida fue reiniciada correctamente.",
            "HU6 - Reiniciar partida",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void nuevaPartida() {
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Deseas iniciar una nueva partida con la misma cantidad de jugadores?",
            "HU11 - Nueva partida",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.CANCEL_OPTION || respuesta == JOptionPane.CLOSED_OPTION) {
            return;
        }

        if (respuesta == JOptionPane.NO_OPTION) {
            cantidadJugadores = pedirCantidadJugadores();
        }

        iniciarObjetosJuego();
        reiniciarCronometro();

        actualizarTextoTurno();
        canvas.repaint();

        JOptionPane.showMessageDialog(
            this,
            "Nueva partida iniciada correctamente.\nJugadores: " + cantidadJugadores,
            "HU11 - Nueva partida",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void cambiarCantidadJugadores() {
        int nuevaCantidad = pedirCantidadJugadores();

        cantidadJugadores = nuevaCantidad;
        iniciarObjetosJuego();
        reiniciarCronometro();

        actualizarTextoTurno();
        canvas.repaint();

        JOptionPane.showMessageDialog(
            this,
            "Nueva partida configurada para " + cantidadJugadores + " jugadores.",
            "Configuración actualizada",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void iniciarCronometro() {
        detenerCronometro();

        segundosPartida = 0;
        actualizarCronometro();

        timerCronometro = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                segundosPartida++;
                actualizarCronometro();
            }
        });

        timerCronometro.start();
    }

    private void detenerCronometro() {
        if (timerCronometro != null) {
            timerCronometro.stop();
        }
    }

    private void reiniciarCronometro() {
        iniciarCronometro();
    }

    private void actualizarCronometro() {
        int minutos = segundosPartida / 60;
        int segundos = segundosPartida % 60;

        String tiempo = String.format("%02d:%02d", minutos, segundos);

        if (lblCronometro != null) {
            lblCronometro.setText("Tiempo de partida: " + tiempo);
        }
    }

    private void actualizarTextoTurno() {
        if (lblTurno != null) {
            lblTurno.setText(
                "Jugadores: " + cantidadJugadores +
                " | Turno actual: " + NOMBRES[turnoActual]
            );
        }

        if (lblNombreTurno != null) {
            lblNombreTurno.setText(NOMBRES[turnoActual].toUpperCase());
            lblNombreTurno.setForeground(COLORES[turnoActual]);
        }

        if (lblMensajeTurno != null) {
            lblMensajeTurno.setText("Debe mover una ficha del color " + NOMBRES[turnoActual]);
        }

        if (panelIndicadorTurno != null) {
            panelIndicadorTurno.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLORES[turnoActual], 3),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
        }

        if (panelColorTurno != null) {
            panelColorTurno.repaint();
        }
    }

    private void cambiarTurno() {
        indiceTurno++;

        if (indiceTurno >= jugadoresActivos.length) {
            indiceTurno = 0;
        }

        turnoActual = jugadoresActivos[indiceTurno];
        actualizarTextoTurno();
    }

    private void finalizarJuego(String ganador) {
        juegoTerminado = true;
        detenerCronometro();

        JOptionPane.showMessageDialog(
            this,
            "Ganador: " + ganador + "\n" + lblCronometro.getText(),
            "Fin de la partida",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void manejarClic(int px, int py) {
        if (juegoTerminado) {
            return;
        }

        int[] celda = pixelACelda(px, py, 320, 320);

        if (celda == null) {
            return;
        }

        int f = celda[0];
        int c = celda[1];

        if (!tablero.esValida(f, c)) {
            return;
        }

        if (selFila != -1) {
            if (esMovimientoLegal(f, c)) {
                movimiento.mover(selFila, selCol, f, c);
                deseleccionar();

                cambiarTurno();

                canvas.repaint();
                return;
            }

            deseleccionar();
        }

        int val = tablero.getFicha(f, c);

        if (val == turnoActual && fichas.esJugadorActivo(val)) {
            selFila = f;
            selCol = c;
            movimientosLegales = movimiento.destinosLegales(f, c);
        } else {
            if (val >= 0) {
                lblMensajeTurno.setText(
                    "No es turno de " + NOMBRES[val] +
                    ". Debe jugar " + NOMBRES[turnoActual]
                );
            }
        }

        canvas.repaint();
    }

    private boolean esMovimientoLegal(int f, int c) {
        if (movimientosLegales == null) {
            return false;
        }

        for (int[] m : movimientosLegales) {
            if (m[0] == f && m[1] == c) {
                return true;
            }
        }

        return false;
    }

    private void deseleccionar() {
        selFila = -1;
        selCol = -1;
        movimientosLegales = null;
    }

    private Point pixel(int f, int c, int cx, int cy) {
        int d = 27;

        double x = (c - f) * d * Math.sqrt(3) / 2.0;
        double y = (f + c - 16) * d * 0.75;

        return new Point(
            cx + (int) Math.round(x),
            cy + (int) Math.round(y)
        );
    }

    private int[] pixelACelda(int px, int py, int cx, int cy) {
        int[] mejor = null;
        double minDist = 14;

        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                if (!tablero.esValida(f, c)) {
                    continue;
                }

                Point p = pixel(f, c, cx, cy);
                double dist = Math.hypot(px - p.x, py - p.y);

                if (dist < minDist) {
                    minDist = dist;
                    mejor = new int[]{f, c};
                }
            }
        }

        return mejor;
    }

    private void dibujar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = 320;
        int cy = 320;
        int radio = 10;

        int[][] dirs = {
            {0, 1},
            {1, 0},
            {1, 1}
        };

        g.setColor(new Color(80, 80, 100));
        g.setStroke(new BasicStroke(1f));

        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                if (!tablero.esValida(f, c)) {
                    continue;
                }

                Point p1 = pixel(f, c, cx, cy);

                for (int[] dir : dirs) {
                    int nf = f + dir[0];
                    int nc = c + dir[1];

                    if (tablero.esValida(nf, nc)) {
                        Point p2 = pixel(nf, nc, cx, cy);
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }
        }

        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                int val = tablero.getFicha(f, c);

                if (val == Tablero.INVALIDO) {
                    continue;
                }

                Point p = pixel(f, c, cx, cy);

                boolean estaSeleccionada = (f == selFila && c == selCol);

                if (val == Tablero.VACIO) {
                    g.setColor(new Color(60, 60, 80));
                    g.fillOval(p.x - 4, p.y - 4, 8, 8);

                    g.setColor(new Color(100, 100, 130));
                    g.drawOval(p.x - 4, p.y - 4, 8, 8);
                } else {
                    Color color = COLORES[val];

                    if (estaSeleccionada) {
                        g.setColor(new Color(255, 255, 255, 160));
                        g.setStroke(new BasicStroke(3f));
                        g.drawOval(
                            p.x - radio - 3,
                            p.y - radio - 3,
                            (radio + 3) * 2,
                            (radio + 3) * 2
                        );
                        g.setStroke(new BasicStroke(1f));
                    }

                    g.setColor(new Color(0, 0, 0, 100));
                    g.fillOval(p.x - radio + 2, p.y - radio + 2, radio * 2, radio * 2);

                    g.setColor(color);
                    g.fillOval(p.x - radio, p.y - radio, radio * 2, radio * 2);

                    g.setColor(new Color(255, 255, 255, 80));
                    g.fillOval(p.x - radio / 2, p.y - radio, radio, radio);

                    g.setColor(color.darker());
                    g.setStroke(new BasicStroke(1.5f));
                    g.drawOval(p.x - radio, p.y - radio, radio * 2, radio * 2);
                    g.setStroke(new BasicStroke(1f));
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }
}