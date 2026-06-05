package src;
import java.util.List;

public class ControladorJuego {

    private Tablero tablero;
    private Fichas fichas;
    private Movimiento movimiento;

    private int cantidadJugadores;
    private int[] jugadoresActivos;
    private int indiceTurno;
    private int turnoActual;

    private int filaSeleccionada = -1;
    private int colSeleccionada  = -1;
    private List<int[]> movimientosLegales = null;
    private boolean juegoTerminado = false;

    public ControladorJuego(int cantidadJugadores) {
        this.cantidadJugadores = cantidadJugadores;
        iniciarEstado();
    }

    // ------------------------------------------------------------------
    // Inicialización
    // ------------------------------------------------------------------

    public void iniciarEstado() {
        tablero   = new Tablero();
        fichas    = new Fichas(tablero, cantidadJugadores);
        movimiento = new Movimiento(tablero, fichas);

        jugadoresActivos  = fichas.getJugadoresActivos();
        indiceTurno       = 0;
        turnoActual       = jugadoresActivos[indiceTurno];

        filaSeleccionada  = -1;
        colSeleccionada   = -1;
        movimientosLegales = null;
        juegoTerminado    = false;
    }

    public void setCantidadJugadores(int cantidad) {
        this.cantidadJugadores = cantidad;
        iniciarEstado();
    }

    // ------------------------------------------------------------------
    // Consultas de estado (la vista las usa para dibujar)
    // ------------------------------------------------------------------

    public int getCantidadJugadores()   { return cantidadJugadores; }
    public int getTurnoActual()         { return turnoActual; }
    public int getFilaSeleccionada()    { return filaSeleccionada; }
    public int getColSeleccionada()     { return colSeleccionada; }
    public boolean isJuegoTerminado()   { return juegoTerminado; }
    public Tablero getTablero()         { return tablero; }
    public Fichas getFichas()           { return fichas; }
    public List<int[]> getMovimientosLegales() { return movimientosLegales; }

    // ------------------------------------------------------------------
    // Lógica del juego
    // ------------------------------------------------------------------

    /**
     * Procesa un clic del usuario sobre la celda (fila, col).
     * Devuelve un ResultadoClic con lo que ocurrió para que la vista reaccione.
     */
    public ResultadoClic procesarClic(int fila, int col) {
        if (juegoTerminado) return ResultadoClic.IGNORAR;
        if (!tablero.esValida(fila, col)) return ResultadoClic.IGNORAR;

        if (filaSeleccionada != -1) {
            if (esMovimientoLegal(fila, col)) {
                int jugadorQueMovio = turnoActual;
                movimiento.mover(filaSeleccionada, colSeleccionada, fila, col);
                deseleccionar();

                if (verificarVictoria(jugadorQueMovio)) {
                    juegoTerminado = true;
                    return ResultadoClic.VICTORIA;
                }
                cambiarTurno();
                return ResultadoClic.MOVIO;
            }
            deseleccionar();
        }
        int valor = tablero.getFicha(fila, col);
        if (valor == turnoActual && fichas.esJugadorActivo(valor)) {
            filaSeleccionada   = fila;
            colSeleccionada    = col;
            movimientosLegales = movimiento.destinosLegales(fila, col);
            return ResultadoClic.SELECCIONO;
        }

        return (valor >= 0) ? ResultadoClic.FICHA_AJENA : ResultadoClic.VACIO;
    }

    public void probarVictoria() {
        int jugador = turnoActual;
        for (int[] pos : fichas.getZonaInicio(jugador)) {
            if (tablero.getFicha(pos[0], pos[1]) == jugador)
                tablero.setFicha(pos[0], pos[1], Tablero.VACIO);
        }
        for (int[] pos : fichas.getZonaObjetivo(jugador)) {
            tablero.setFicha(pos[0], pos[1], jugador);
        }
        if (verificarVictoria(jugador)) {
            juegoTerminado = true;
        }
    }

    // ------------------------------------------------------------------
    // Privados
    // ------------------------------------------------------------------

    private boolean verificarVictoria(int jugador) {
        for (int[] pos : fichas.getZonaObjetivo(jugador)) {
            if (tablero.getFicha(pos[0], pos[1]) != jugador) return false;
        }
        return true;
    }

    private void cambiarTurno() {
        indiceTurno = (indiceTurno + 1) % jugadoresActivos.length;
        turnoActual = jugadoresActivos[indiceTurno];
    }

    private boolean esMovimientoLegal(int fila, int col) {
        if (movimientosLegales == null) return false;
        for (int[] m : movimientosLegales) {
            if (m[0] == fila && m[1] == col) return true;
        }
        return false;
    }

    private void deseleccionar() {
        filaSeleccionada   = -1;
        colSeleccionada    = -1;
        movimientosLegales = null;
    }

    public enum ResultadoClic {
        MOVIO,       
        VICTORIA,     
        SELECCIONO,   
        FICHA_AJENA,  
        VACIO,        
        IGNORAR      
    }
}
