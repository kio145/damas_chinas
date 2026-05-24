package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class Main extends JFrame {

    // ─── Colores de los 6 jugadores ──────────────────────────────────────────
    static final Color[] COLORES = {
        Color.RED,
        Color.BLUE,
        new Color(0, 180, 0),       // Verde
        new Color(220, 180, 0),     // Amarillo
        Color.WHITE,
        new Color(128, 0, 128)      // Morado
    };
    static final String[] NOMBRES = {"Rojo", "Azul", "Verde", "Amarillo", "Blanco", "Morado"};

    // ─── Modelo ───────────────────────────────────────────────────────────────
    private final Tablero    tablero;
    private final Fichas     fichas;
    private final Movimiento movimiento;

    // ─── Estado de la UI ─────────────────────────────────────────────────────
    private int turnoActual = 0;          // jugador cuyo turno es
    private int selFila     = -1;         // ficha seleccionada (-1 = ninguna)
    private int selCol      = -1;
    private List<int[]> movimientosLegales = null;
    private boolean juegoTerminado = false;
    private int ganadorJugador = -1; // jugador que ganó, para dibujar overlay

    // ─── Componentes UI (Sin 'final' para evitar errores de compilación) ──────
   // private JLabel lblTurno = null;
    private JPanel canvas;
    //private JPanel indicadorTurno = null; 

    // ─── Constructor ─────────────────────────────────────────────────────────
    public Main() {
        tablero    = new Tablero();
        fichas     = new Fichas(tablero);
        movimiento = new Movimiento(tablero, fichas);

        setTitle("Damas Chinas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));
        getContentPane().setBackground(new Color(30, 30, 40));

        // ── Título ────────────────────────────────────────────────────────────
        JLabel titulo = new JLabel("DAMAS CHINAS", JLabel.CENTER);
        titulo.setFont(new Font("Georgia", Font.BOLD, 22));
        titulo.setForeground(new Color(255, 215, 0));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 2, 0));
        add(titulo, BorderLayout.NORTH);

        // ── Tablero (canvas) ─────────────────────────────────────────────────
        canvas = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujar((Graphics2D) g);
            }
        };
        canvas.setPreferredSize(new Dimension(600, 580));
        canvas.setBackground(new Color(30, 30, 40));

        // Clic en el tablero
        canvas.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                manejarClic(e.getX(), e.getY());
            }
        });
        add(canvas, BorderLayout.CENTER);

        // ── Panel inferior ELIMINADO COMPLETAMENTE ───────────────────────────
        // (Toda la sección de panelSur y leyendas fue removida de la UI)

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ─── Lógica de interacción ───────────────────────────────────────────────

    private void manejarClic(int px, int py) {
        if (juegoTerminado) return;

        int[] celda = pixelACelda(px, py, 320, 320);
        if (celda == null) return;
        int f = celda[0], c = celda[1];

        if (!tablero.esValida(f, c)) return;

        // ¿Hay ficha seleccionada? → intentar mover
        if (selFila != -1) {
            if (esMovimientoLegal(f, c)) {
                movimiento.mover(selFila, selCol, f, c);
                deseleccionar();

                // Verificar victoria
                int ganador = movimiento.ganador();
                if (ganador != -1) {
                    juegoTerminado = true;
                    ganadorJugador = ganador;
                    canvas.repaint();
                    return;
                }

                // LA LÓGICA DE TURNOS SIGUE ACTIVA INTERNAMENTE AQUÍ:
                if (turnoActual == 0) {
                    turnoActual = 3; // Pasa al Amarillo
                } else {
                    turnoActual = 0; // Vuelve al Rojo
                }
                
                canvas.repaint();
                return;
            }
            // Clic en otra ficha propia o espacio inválido → deseleccionar
            deseleccionar();
        }

        // Seleccionar ficha propia
        int val = tablero.getFicha(f, c);
        if (val == turnoActual) {
            selFila = f;
            selCol  = c;
            movimientosLegales = movimiento.destinosLegales(f, c);
        }
        canvas.repaint();
    }

    private boolean esMovimientoLegal(int f, int c) {
        if (movimientosLegales == null) return false;
        for (int[] m : movimientosLegales) {
            if (m[0] == f && m[1] == c) return true;
        }
        return false;
    }

    private void deseleccionar() {
        selFila = -1;
        selCol  = -1;
        movimientosLegales = null;
    }

   

    // ─── Conversión pixel ↔ celda ────────────────────────────────────────────

    private Point pixel(int f, int c, int cx, int cy) {
        int d = 27;
        double x = (c - f) * d * Math.sqrt(3) / 2.0;
        double y = (f + c - 16) * d * 0.75;
        return new Point(cx + (int) Math.round(x), cy + (int) Math.round(y));
    }

    private int[] pixelACelda(int px, int py, int cx, int cy) {
        int[] mejor = null;
        double minDist = 14;   
        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                if (!tablero.esValida(f, c)) continue;
                Point p = pixel(f, c, cx, cy);
                double dist = Math.hypot(px - p.x, py - p.y);
                if (dist < minDist) {
                    minDist = dist;
                    mejor   = new int[]{f, c};
                }
            }
        }
        return mejor;
    }

    // ─── Dibujo principal ────────────────────────────────────────────────────

    private void dibujar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = 320, cy = 320;
        int radio = 10;
        int[][] dirs = {{0,1},{1,0},{1,1},{1,-1}};

        // 1) Líneas de conexión
        g.setColor(new Color(80, 80, 100));
        g.setStroke(new BasicStroke(1f));
        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                if (!tablero.esValida(f, c)) continue;
                Point p1 = pixel(f, c, cx, cy);
                for (int[] dir : dirs) {
                    int nf = f + dir[0], nc = c + dir[1];
                    if (tablero.esValida(nf, nc)) {
                        g.drawLine(p1.x, p1.y, pixel(nf, nc, cx, cy).x, pixel(nf, nc, cx, cy).y);
                    }
                }
            }
        }

        // [SECCIÓN ELIMINADA: Ya no se resaltan los destinos legales visualmente]

        // 3) Celdas y fichas
        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                int val = tablero.getFicha(f, c);
                if (val == Tablero.INVALIDO) continue;
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
                        g.drawOval(p.x - radio - 3, p.y - radio - 3, (radio + 3) * 2, (radio + 3) * 2);
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

        // ── Overlay de Victoria ───────────────────────────────────────────────
        if (juegoTerminado && ganadorJugador != -1) {
            dibujarVictoria(g, ganadorJugador);
        }
    }

    private void dibujarVictoria(Graphics2D g, int jugador) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        Color colorJugador = COLORES[jugador];
        String nombre = NOMBRES[jugador];

        // Fondo semi-transparente oscuro
        g.setColor(new Color(10, 10, 20, 200));
        g.fillRoundRect(w / 2 - 210, h / 2 - 110, 420, 220, 30, 30);

        // Borde con el color del ganador (doble para efecto glow)
        g.setColor(colorJugador.brighter());
        g.setStroke(new BasicStroke(4f));
        g.drawRoundRect(w / 2 - 210, h / 2 - 110, 420, 220, 30, 30);
        g.setColor(new Color(colorJugador.getRed(), colorJugador.getGreen(), colorJugador.getBlue(), 80));
        g.setStroke(new BasicStroke(10f));
        g.drawRoundRect(w / 2 - 210, h / 2 - 110, 420, 220, 30, 30);
        g.setStroke(new BasicStroke(1f));

        // Estrella decorativa
        g.setFont(new Font("Serif", Font.PLAIN, 48));
        FontMetrics fmT = g.getFontMetrics();
        String trofeo = "★";
        int tx = w / 2 - fmT.stringWidth(trofeo) / 2;
        g.setColor(new Color(255, 215, 0));
        g.drawString(trofeo, tx, h / 2 - 48);

        // Texto "¡VICTORIA!"
        g.setFont(new Font("Georgia", Font.BOLD, 38));
        FontMetrics fmV = g.getFontMetrics();
        String txtVictoria = "¡VICTORIA!";
        int vx = w / 2 - fmV.stringWidth(txtVictoria) / 2;
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(txtVictoria, vx + 2, h / 2 + 2);
        g.setColor(new Color(255, 215, 0));
        g.drawString(txtVictoria, vx, h / 2);

        // Nombre del ganador con su color
        g.setFont(new Font("Georgia", Font.BOLD | Font.ITALIC, 26));
        FontMetrics fmN = g.getFontMetrics();
        String txtNombre = "Gana el jugador " + nombre;
        int nx = w / 2 - fmN.stringWidth(txtNombre) / 2;
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(txtNombre, nx + 2, h / 2 + 42);
        g.setColor(colorJugador.brighter());
        g.drawString(txtNombre, nx, h / 2 + 40);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}