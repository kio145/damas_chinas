import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class Main extends JFrame {

    // Instancia del tablero
    private Board tablero;
    
    // Componentes de la ventana
    private JPanel panelDibujo;
    private JLabel txtTurno;
    private JLabel[] txtJugadores;

    // Variables de control del juego (6 jugadores)
    private int jugadorActual = 0;
    private String[] nombres = {"Rojo", "Azul", "Verde", "Amarillo", "Blanco" , "Morado"};
    private boolean[] yaGano = new boolean[6];
    
    // Colores estándar de los jugadores
    public static final Color[] COLORES = {
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.YELLOW,
        Color.WHITE,
        new Color(128, 0, 128) // Morado estándar
       
    };

    // Variables para la pieza que el usuario selecciona haciendo clic
    private int filaSel = -1;
    private int colSel = -1;
    private List<int[]> jugadasPosibles = null;

    public Main() {
        tablero = new Board();
        
        setTitle("Juego de Damas Chinas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        // 1. Panel Superior: Muestra el turno
        JPanel panelArriba = new JPanel();
        panelArriba.setBackground(Color.DARK_GRAY);
        txtTurno = new JLabel("Turno: Rojo");
        txtTurno.setForeground(Color.RED);
        txtTurno.setFont(new Font("Arial", Font.BOLD, 16));
        panelArriba.add(txtTurno);
        add(panelArriba, BorderLayout.NORTH);

        // 2. Panel Lateral: Lista de jugadores
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new GridLayout(7, 1, 5, 5));
        panelDerecho.setBackground(Color.LIGHT_GRAY);
        
        JLabel titulo = new JLabel(" JUGADORES: ", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 12));
        panelDerecho.add(titulo);

        txtJugadores = new JLabel[6];
        for (int i = 0; i < 6; i++) {
            txtJugadores[i] = new JLabel("  " + nombres[i], JLabel.LEFT);
            txtJugadores[i].setFont(new Font("Arial", Font.PLAIN, 13));
            txtJugadores[i].setForeground(COLORES[i]);
            panelDerecho.add(txtJugadores[i]);
        }
        add(panelDerecho, BorderLayout.EAST);

        // 3. Panel Central: El lienzo donde se dibuja el tablero hexagonal
        panelDibujo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarTodoElTablero(g);
            }
        };
        panelDibujo.setBackground(new Color(240, 240, 240));
        panelDibujo.setPreferredSize(new Dimension(540, 540));
        
        // Escuchador de clics del mouse
        panelDibujo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                gestionarClicUsuario(e.getX(), e.getY());
            }
        });
        
        add(panelDibujo, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        actualizarLetreros();
    }

    // Calcula la posición en pantalla (X, Y) basándose en las coordenadas de la matriz (fila, columna)
    private Point calcularPixel(int f, int c) {
        int centroX = panelDibujo.getWidth() / 2;
        int centroY = panelDibujo.getHeight() / 2;

        // Fórmulas matemáticas estándar para la proyección de cuadrícula hexagonal
        int dist = 26; 
        double x = (c - f) * dist * Math.sqrt(3) / 2.0;
        double y = (f + c - 16) * dist * 0.75;

        return new Point(centroX + (int)Math.round(x), centroY + (int)Math.round(y));
    }

    private void dibujarTodoElTablero(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar las líneas de conexión entre celdas vecinas
        g2.setColor(Color.LIGHT_GRAY);
        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                if (tablero.esValida(f, c)) {
                    Point p1 = calcularPixel(f, c);
                    // Conectar con vecino derecho, inferior e inferior derecho
                    int[][] vecinos = {{0,1}, {1,0}, {1,1}};
                    for (int[] v : vecinos) {
                        int nf = f + v[0];
                        int nc = c + v[1];
                        if (tablero.esValida(nf, nc)) {
                            Point p2 = calcularPixel(nf, nc);
                            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                        }
                    }
                }
            }
        }

        // Dibujar los círculos de las posiciones
        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                if (tablero.esValida(f, c)) {
                    Point p = calcularPixel(f, c);
                    int contenido = tablero.obtenerCelda(f, c);

                    if (contenido >= 0) { // Hay una ficha de un jugador
                        g2.setColor(COLORES[contenido]);
                        g2.fillOval(p.x - 10, p.y - 10, 20, 20);
                        
                        // Si es la ficha seleccionada le ponemos borde negro grueso
                        if (f == filaSel && c == colSel) {
                            g2.setColor(Color.BLACK);
                            g2.setStroke(new BasicStroke(2));
                            g2.drawOval(p.x - 10, p.y - 10, 20, 20);
                        } else {
                            g2.setColor(Color.DARK_GRAY);
                            g2.setStroke(new BasicStroke(1));
                            g2.drawOval(p.x - 10, p.y - 10, 20, 20);
                        }
                    } else { // Celda vacía disponible para jugar
                        g2.setColor(Color.WHITE);
                        g2.fillOval(p.x - 5, p.y - 5, 10, 10);
                        g2.setColor(Color.GRAY);
                        g2.drawOval(p.x - 5, p.y - 5, 10, 10);
                    }
                }
            }
        }

        // Resaltar los destinos posibles en verde si hay una pieza seleccionada
        if (jugadasPosibles != null) {
            g2.setColor(Color.GREEN);
            g2.setStroke(new BasicStroke(2));
            for (int[] pos : jugadasPosibles) {
                Point p = calcularPixel(pos[0], pos[1]);
                g2.drawOval(p.x - 11, p.y - 11, 22, 22);
            }
        }
    }

    private void gestionarClicUsuario(int mx, int my) {
        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                if (tablero.esValida(f, c)) {
                    Point p = calcularPixel(f, c);
                    
                    // Comprobar si el clic cayó cerca del círculo (usando el teorema de Pitágoras simple)
                    if (Math.hypot(mx - p.x, my - p.y) <= 12) {
                        
                        // Intento de mover a un destino válido resaltado
                        if (jugadasPosibles != null) {
                            for (int[] destino : jugadasPosibles) {
                                if (destino[0] == f && destino[1] == c) {
                                    tablero.moverPieza(filaSel, colSel, f, c);
                                    comprobarGanadorActual();
                                    pasarSiguienteTurno();
                                    return;
                                }
                            }
                        }

                        // Intentar seleccionar una pieza que le pertenezca al jugador del turno activo
                        if (tablero.obtenerCelda(f, c) == jugadorActual) {
                            filaSel = f;
                            colSel = c;
                            jugadasPosibles = tablero.calcularMovimientos(f, c);
                            panelDibujo.repaint();
                            return;
                        }
                    }
                }
            }
        }
        // Clic fuera resetea la selección actual
        filaSel = -1;
        colSel = -1;
        jugadasPosibles = null;
        panelDibujo.repaint();
    }

    private void comprobarGanadorActual() {
        if (tablero.revisarVictoria(jugadorActual) && !yaGano[jugadorActual]) {
            yaGano[jugadorActual] = true;
            JOptionPane.showMessageDialog(this, "¡El jugador " + nombres[jugadorActual] + " ha ganado!");
        }
    }

    private void pasarSiguienteTurno() {
        // Cambiar de turno saltándose a los que ya terminaron el juego
        do {
            jugadorActual = (jugadorActual + 1) % 6;
        } while (yaGano[jugadorActual]);

        filaSel = -1;
        colSel = -1;
        jugadasPosibles = null;
        actualizarLetreros();
    }

    private void actualizarLetreros() {
        txtTurno.setText("Turno: " + nombres[jugadorActual]);
        txtTurno.setForeground(COLORES[jugadorActual]);

        for (int i = 0; i < 5; i++) {
            if (yaGano[i]) {
                txtJugadores[i].setText("  " + nombres[i] + " [¡Ganó!]");
            } else if (i == jugadorActual) {
                txtJugadores[i].setText("  " + nombres[i] + " <<");
            } else {
                txtJugadores[i].setText("  " + nombres[i]);
            }
        }
        panelDibujo.repaint();
    }

    public static void main(String[] args) {
        new Main();
    }
}