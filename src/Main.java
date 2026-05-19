package src;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Main extends JFrame {

    static final int VACIO    = -1;
    static final int INVALIDO = -2;

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

    // ─── Datos del tablero ───────────────────────────────────────────────────
    private final int[][] matriz = new int[17][17];

    // ─── Zonas de inicio de cada jugador ─────────────────────────────────────
    private final int[][][] zonasInicio = {
        // 0 – Arriba
        {{0,9},{1,8},{1,9},{2,7},{2,8},{2,9},{3,6},{3,7},{3,8},{3,9}},
        // 1 – Arriba Derecha
        {{4,0},{4,1},{4,2},{4,3},{5,1},{5,2},{5,3},{6,2},{6,3},{7,3}},
        // 2 – Abajo Derecha
        {{9,12},{10,12},{10,13},{11,12},{11,13},{11,14},{12,12},{12,13},{12,14},{12,15}},
        // 3 – Abajo
        {{13,6},{13,7},{13,8},{13,9},{14,7},{14,8},{14,9},{15,8},{15,9},{16,9}},
        // 4 – Abajo Izquierda
        {{9,3},{10,2},{10,3},{11,1},{11,2},{11,3},{12,0},{12,1},{12,2},{12,3}},
        // 5 – Arriba Izquierda
        {{4,15},{4,14},{5,14},{4,13},{5,13},{4,12},{6,12},{7,12},{5,12},{6,13}}
    };

    // ─── Constructor ─────────────────────────────────────────────────────────
    public Main() {
        inicializarMatriz();
        colocarFichas();

        setTitle("Damas Chinas – Vista Tablero + Fichas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));
        getContentPane().setBackground(new Color(30, 30, 40));

        // Panel superior: título
        JLabel titulo = new JLabel("DAMAS CHINAS", JLabel.CENTER);
        titulo.setFont(new Font("Georgia", Font.BOLD, 22));
        titulo.setForeground(new Color(255, 215, 0));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));
        titulo.setOpaque(false);
        add(titulo, BorderLayout.NORTH);

        // Panel central: tablero
        JPanel canvas = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujar((Graphics2D) g);
            }
        };
        canvas.setPreferredSize(new Dimension(580, 580));
        canvas.setBackground(new Color(30, 30, 40));
        add(canvas, BorderLayout.CENTER);

        // Pie: instrucciones
        JLabel pie = new JLabel("Vista estática: Tablero inicializado con 6 jugadores (10 fichas c/u)", JLabel.CENTER);
        pie.setFont(new Font("SansSerif", Font.ITALIC, 11));
        pie.setForeground(new Color(150, 150, 170));
        pie.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));
        add(pie, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ─── Inicializar la estrella hexagonal ───────────────────────────────────
    private void inicializarMatriz() {
        for (int[] fila : matriz) Arrays.fill(fila, INVALIDO);

        // filas: [columna inicial, cantidad de celdas]
        int[][] cfg = {
            {9,1},{8,2},{7,3},{6,4},
            {0,16},{1,14},{2,12},{3,10},{4,9},{3,10},{2,12},{1,14},{0,16},
            {6,4},{7,3},{8,2},{9,1}
        };
        for (int f = 0; f < 17; f++) {
            int ini = cfg[f][0], cant = cfg[f][1];
            for (int c = ini; c < ini + cant; c++) {
                matriz[f][c] = VACIO;
            }
        }
    }

    // ─── Colocar fichas iniciales ─────────────────────────────────────────────
    private void colocarFichas() {
        for (int p = 0; p < 6; p++) {
            for (int[] pos : zonasInicio[p]) {
                matriz[pos[0]][pos[1]] = p;
            }
        }
    }

    // ─── Helpers tablero ─────────────────────────────────────────────────────
    private boolean esValida(int f, int c) {
        return f >= 0 && f < 17 && c >= 0 && c < 17 && matriz[f][c] != INVALIDO;
    }

    private Point pixel(int f, int c, int cx, int cy) {
        int d = 27;
        double x = (c - f) * d * Math.sqrt(3) / 2.0;
        double y = (f + c - 16) * d * 0.75;
        return new Point(cx + (int) Math.round(x), cy + (int) Math.round(y));
    }

    // ─── Dibujo principal ────────────────────────────────────────────────────
    private void dibujar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = 290, cy = 290;   // centro del panel

        // 1) Líneas de conexión
        g.setColor(new Color(80, 80, 100));
        g.setStroke(new BasicStroke(1f));
        int[][] dirs = {{0,1},{1,0},{1,1}};
        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                if (!esValida(f, c)) continue;
                Point p1 = pixel(f, c, cx, cy);
                for (int[] d : dirs) {
                    int nf = f+d[0], nc = c+d[1];
                    if (esValida(nf, nc)) {
                        Point p2 = pixel(nf, nc, cx, cy);
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }}
        }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
