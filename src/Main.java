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

    static final String[] NOMBRES = {"Rojo", "Azul", "Verde", "Amarillo", "Blanco", "Morado"};

    private Tablero tablero;
    private Fichas fichas;
    private Movimiento movimiento;

    private int turnoActual = 0;
    private int selFila = -1;
    private int selCol = -1;

    private List<int[]> movimientosLegales = null;
    private boolean juegoTerminado = false;

    private JPanel canvas;
    private JLabel lblTurno;

    public Main() {
        iniciarObjetosJuego();

        setTitle("Damas Chinas - HU6 Reiniciar Partida");
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

        lblTurno = new JLabel("Turno actual: " + NOMBRES[turnoActual], JLabel.CENTER);
        lblTurno.setFont(new Font("Arial", Font.BOLD, 14));
        lblTurno.setForeground(Color.WHITE);
        lblTurno.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JButton btnReiniciar = new JButton("Reiniciar partida");
        btnReiniciar.setFont(new Font("Arial", Font.BOLD, 14));

        btnReiniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarPartida();
            }
        });

        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(new Color(30, 30, 40));
        panelBoton.add(btnReiniciar);

        panelInferior.add(lblTurno, BorderLayout.NORTH);
        panelInferior.add(panelBoton, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void iniciarObjetosJuego() {
        tablero = new Tablero();
        fichas = new Fichas(tablero);
        movimiento = new Movimiento(tablero, fichas);

        turnoActual = 0;
        selFila = -1;
        selCol = -1;
        movimientosLegales = null;
        juegoTerminado = false;
    }

    private void reiniciarPartida() {
        iniciarObjetosJuego();

        lblTurno.setText("Partida reiniciada. Turno actual: " + NOMBRES[turnoActual]);
        canvas.repaint();

        JOptionPane.showMessageDialog(
            this,
            "La partida fue reiniciada correctamente.",
            "HU6 - Reiniciar partida",
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

                if (turnoActual == 0) {
                    turnoActual = 3;
                } else {
                    turnoActual = 0;
                }

                lblTurno.setText("Turno actual: " + NOMBRES[turnoActual]);
                canvas.repaint();
                return;
            }

            deseleccionar();
        }

        int val = tablero.getFicha(f, c);

        if (val == turnoActual) {
            selFila = f;
            selCol = c;
            movimientosLegales = movimiento.destinosLegales(f, c);
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
        SwingUtilities.invokeLater(Main::new);
    }
}