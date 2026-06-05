package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * REFACTORIZACIÓN 9: Separar SRP — GameView (antes: Main)
 *
 * Main ahora es exclusivamente la vista Swing. No contiene ninguna
 * regla del juego. Toda decisión de estado pasa por GameController
 * y la vista solo reacciona al ResultadoClic que recibe.
 */
public class Main extends JFrame {

    static final Color[] COLORES = {
        Color.RED, Color.BLUE,
        new Color(0, 180, 0), new Color(220, 180, 0),
        Color.WHITE, new Color(128, 0, 128)
    };

    static final String[] NOMBRES = {
        "Rojo", "Azul", "Verde", "Amarillo", "Blanco", "Morado"
    };

    // ---- Controller (toda la lógica del juego vive aquí) ----
    private ControladorJuego controller;

    // ---- Widgets de la vista ----
    private JPanel  canvas;
    private JLabel  lblTurno;
    private JLabel  lblCronometro;
    private JLabel  lblNombreTurno;
    private JLabel  lblMensajeTurno;
    private JPanel  panelColorTurno;
    private JPanel  panelIndicadorTurno;
    private Timer   timerCronometro;
    private int     segundosPartida = 0;

    public Main() {
        int cantidad = pedirCantidadJugadores();
        controller = new ControladorJuego(cantidad);

        configurarVentana();
        iniciarCronometro();
    }

    // ------------------------------------------------------------------
    // Construcción de la UI
    // ------------------------------------------------------------------

    private void configurarVentana() {
        setTitle("Damas Chinas - XP");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));
        getContentPane().setBackground(new Color(30, 30, 40));

        add(crearTitulo(),          BorderLayout.NORTH);
        add(crearCanvas(),          BorderLayout.CENTER);
        add(crearPanelInferior(),   BorderLayout.SOUTH);

        actualizarVistaTurno();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JLabel crearTitulo() {
        JLabel titulo = new JLabel("DAMAS CHINAS", JLabel.CENTER);
        titulo.setFont(new Font("Georgia", Font.BOLD, 22));
        titulo.setForeground(new Color(255, 215, 0));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 2, 0));
        return titulo;
    }

    private JPanel crearCanvas() {
        canvas = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujar((Graphics2D) g);
            }
        };
        canvas.setPreferredSize(new Dimension(600, 580));
        canvas.setBackground(new Color(30, 30, 40));
        canvas.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                manejarClic(e.getX(), e.getY());
            }
        });
        return canvas;
    }

    private JPanel crearPanelInferior() {
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

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(new Color(30, 30, 40));
        panelCentro.add(panelIndicadorTurno, BorderLayout.NORTH);
        panelCentro.add(panelTextos,         BorderLayout.CENTER);
        panelCentro.add(crearPanelBotones(), BorderLayout.SOUTH);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(30, 30, 40));
        panelInferior.add(panelCentro, BorderLayout.CENTER);
        return panelInferior;
    }

    private JPanel crearPanelBotones() {
        JButton btnReiniciar = boton("Reiniciar partida",  e -> reiniciarPartida());
        JButton btnNueva     = boton("Nueva partida",      e -> nuevaPartida());
        JButton btnCambiar   = boton("Cambiar jugadores",  e -> cambiarJugadores());
        JButton btnProbar    = boton("Probar victoria",    e -> probarVictoria());

        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 40));
        panel.add(btnReiniciar);
        panel.add(btnNueva);
        panel.add(btnCambiar);
        panel.add(btnProbar);
        return panel;
    }

    private JButton boton(String texto, ActionListener accion) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.addActionListener(accion);
        return btn;
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
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color color = COLORES[controller.getTurnoActual()];
                g2.setColor(new Color(0, 0, 0, 120)); g2.fillOval(7, 7, 42, 42);
                g2.setColor(color);                    g2.fillOval(4, 4, 42, 42);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3));      g2.drawOval(4, 4, 42, 42);
            }
        };
        panelColorTurno.setPreferredSize(new Dimension(55, 55));
        panelColorTurno.setBackground(new Color(20, 20, 30));

        panel.add(panelColorTurno, BorderLayout.WEST);
        panel.add(panelTexto,      BorderLayout.CENTER);
        return panel;
    }

    // ------------------------------------------------------------------
    // Acciones de los botones — la vista solo pide al controller
    // ------------------------------------------------------------------

    private void reiniciarPartida() {
        controller.iniciarEstado();
        reiniciarCronometro();
        actualizarVistaTurno();
        canvas.repaint();
        JOptionPane.showMessageDialog(this,
            "La partida fue reiniciada correctamente.",
            "HU6 - Reiniciar partida", JOptionPane.INFORMATION_MESSAGE);
    }

    private void nuevaPartida() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Deseas iniciar una nueva partida con la misma cantidad de jugadores?",
            "HU11 - Nueva partida", JOptionPane.YES_NO_CANCEL_OPTION);

        if (respuesta == JOptionPane.CANCEL_OPTION || respuesta == JOptionPane.CLOSED_OPTION) return;
        if (respuesta == JOptionPane.NO_OPTION) {
            controller.setCantidadJugadores(pedirCantidadJugadores());
        } else {
            controller.iniciarEstado();
        }
        reiniciarCronometro();
        actualizarVistaTurno();
        canvas.repaint();
        JOptionPane.showMessageDialog(this,
            "Nueva partida iniciada.\nJugadores: " + controller.getCantidadJugadores(),
            "HU11 - Nueva partida", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cambiarJugadores() {
        controller.setCantidadJugadores(pedirCantidadJugadores());
        reiniciarCronometro();
        actualizarVistaTurno();
        canvas.repaint();
        JOptionPane.showMessageDialog(this,
            "Nueva partida configurada para " + controller.getCantidadJugadores() + " jugadores.",
            "Configuración actualizada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void probarVictoria() {
        controller.probarVictoria();
        canvas.repaint();
        if (controller.isJuegoTerminado()) {
            mostrarVictoria(NOMBRES[controller.getTurnoActual()]);
        }
    }

    // ------------------------------------------------------------------
    // Manejo de clic en el tablero — la vista delega al controller
    // ------------------------------------------------------------------

    private void manejarClic(int px, int py) {
        int[] celda = pixelACelda(px, py, 320, 320);
        if (celda == null) return;

        int turnoAntes = controller.getTurnoActual();
        ControladorJuego.ResultadoClic resultado = controller.procesarClic(celda[0], celda[1]);

        switch (resultado) {
            case VICTORIA:
                canvas.repaint();
                mostrarVictoria(NOMBRES[turnoAntes]);
                break;
            case MOVIO:
                actualizarVistaTurno();
                canvas.repaint();
                break;
            case SELECCIONO:
            case VACIO:
                canvas.repaint();
                break;
            case FICHA_AJENA:
                int val = controller.getTablero().getFicha(celda[0], celda[1]);
                lblMensajeTurno.setText("No es turno de " + NOMBRES[val]
                    + ". Debe jugar " + NOMBRES[controller.getTurnoActual()]);
                canvas.repaint();
                break;
            case IGNORAR:
                break;
        }
    }

    private void mostrarVictoria(String ganador) {
        detenerCronometro();
        lblMensajeTurno.setText("Partida finalizada. Ganador: " + ganador);
        JOptionPane.showMessageDialog(this,
            "¡VICTORIA!\nGanador: " + ganador + "\n" + lblCronometro.getText(),
            "HU5 - Condición de victoria", JOptionPane.INFORMATION_MESSAGE);
    }

    // ------------------------------------------------------------------
    // Actualización de la vista con el estado del controller
    // ------------------------------------------------------------------

    private void actualizarVistaTurno() {
        int turno = controller.getTurnoActual();
        if (lblTurno != null)
            lblTurno.setText("Jugadores: " + controller.getCantidadJugadores()
                + " | Turno actual: " + NOMBRES[turno]);
        if (lblNombreTurno != null) {
            lblNombreTurno.setText(NOMBRES[turno].toUpperCase());
            lblNombreTurno.setForeground(COLORES[turno]);
        }
        if (lblMensajeTurno != null)
            lblMensajeTurno.setText("Debe mover una ficha del color " + NOMBRES[turno]);
        if (panelIndicadorTurno != null)
            panelIndicadorTurno.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLORES[turno], 3),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        if (panelColorTurno != null)
            panelColorTurno.repaint();
    }

    // ------------------------------------------------------------------
    // Cronómetro (responsabilidad exclusiva de la vista)
    // ------------------------------------------------------------------

    private void iniciarCronometro() {
        detenerCronometro();
        segundosPartida = 0;
        actualizarCronometro();
        timerCronometro = new Timer(1000, e -> { segundosPartida++; actualizarCronometro(); });
        timerCronometro.start();
    }

    private void detenerCronometro() {
        if (timerCronometro != null) timerCronometro.stop();
    }

    private void reiniciarCronometro() { iniciarCronometro(); }

    private void actualizarCronometro() {
        if (lblCronometro != null)
            lblCronometro.setText(String.format("Tiempo de partida: %02d:%02d",
                segundosPartida / 60, segundosPartida % 60));
    }

    // ------------------------------------------------------------------
    // Geometría (pertenece a la vista: sabe cómo se dibuja el tablero)
    // ------------------------------------------------------------------

    private Point pixel(int f, int c, int cx, int cy) {
        int d = 27;
        double x = (c - f) * d * Math.sqrt(3) / 2.0;
        double y = (f + c - 16) * d * 0.75;
        return new Point(cx + (int) Math.round(x), cy + (int) Math.round(y));
    }

    private int[] pixelACelda(int px, int py, int cx, int cy) {
        int[] mejor = null;
        double minDist = 14;
        Tablero tablero = controller.getTablero();
        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                if (!tablero.esValida(f, c)) continue;
                Point p = pixel(f, c, cx, cy);
                double dist = Math.hypot(px - p.x, py - p.y);
                if (dist < minDist) { minDist = dist; mejor = new int[]{f, c}; }
            }
        }
        return mejor;
    }

    private void dibujar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int cx = 320, cy = 320, radio = 10;
        int[][] dirs = {{0,1},{1,0},{1,1}};
        Tablero tablero = controller.getTablero();

        g.setColor(new Color(80, 80, 100));
        g.setStroke(new BasicStroke(1f));
        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                if (!tablero.esValida(f, c)) continue;
                Point p1 = pixel(f, c, cx, cy);
                for (int[] dir : dirs) {
                    int nf = f + dir[0], nc = c + dir[1];
                    if (tablero.esValida(nf, nc)) {
                        Point p2 = pixel(nf, nc, cx, cy);
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }
        }

        int filasSel = controller.getFilaSeleccionada();
        int colSel   = controller.getColSeleccionada();

        for (int f = 0; f < 17; f++) {
            for (int c = 0; c < 17; c++) {
                int val = tablero.getFicha(f, c);
                if (val == Tablero.INVALIDO) continue;
                Point p = pixel(f, c, cx, cy);
                boolean seleccionada = (f == filasSel && c == colSel);

                if (val == Tablero.VACIO) {
                    g.setColor(new Color(60, 60, 80));  g.fillOval(p.x-4, p.y-4, 8, 8);
                    g.setColor(new Color(100,100,130)); g.drawOval(p.x-4, p.y-4, 8, 8);
                } else {
                    Color color = COLORES[val];
                    if (seleccionada) {
                        g.setColor(new Color(255,255,255,160));
                        g.setStroke(new BasicStroke(3f));
                        g.drawOval(p.x-radio-3, p.y-radio-3, (radio+3)*2, (radio+3)*2);
                        g.setStroke(new BasicStroke(1f));
                    }
                    g.setColor(new Color(0,0,0,100));    g.fillOval(p.x-radio+2, p.y-radio+2, radio*2, radio*2);
                    g.setColor(color);                    g.fillOval(p.x-radio,   p.y-radio,   radio*2, radio*2);
                    g.setColor(new Color(255,255,255,80));g.fillOval(p.x-radio/2, p.y-radio,   radio,   radio);
                    g.setColor(color.darker());
                    g.setStroke(new BasicStroke(1.5f));   g.drawOval(p.x-radio,   p.y-radio,   radio*2, radio*2);
                    g.setStroke(new BasicStroke(1f));
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Diálogos
    // ------------------------------------------------------------------

    private int pedirCantidadJugadores() {
        Integer[] opciones = {2, 3, 4, 5, 6};
        Integer seleccion = (Integer) JOptionPane.showInputDialog(null,
            "Seleccione la cantidad de jugadores:", "Configuración de partida",
            JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        return (seleccion != null) ? seleccion : 2;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
