package src;

/**
 * Fichas: administra las posiciones iniciales de las piezas de los 6 jugadores
 * y las coloca en un Tablero.
 */
public class Fichas {

    /** Zonas de inicio de cada jugador (6 jugadores × 10 fichas × [fila, col]). */
    private static final int[][][] ZONAS_INICIO = {
        // Jugador 0 – Arriba
        {{0,9},{1,8},{1,9},{2,7},{2,8},{2,9},{3,6},{3,7},{3,8},{3,9}},
        // Jugador 1 – Arriba Derecha
        {{4,0},{4,1},{4,2},{4,3},{5,1},{5,2},{5,3},{6,2},{6,3},{7,3}},
        // Jugador 2 – Abajo Derecha
        {{9,12},{10,12},{10,13},{11,12},{11,13},{11,14},{12,12},{12,13},{12,14},{12,15}},
        // Jugador 3 – Abajo
        {{13,6},{13,7},{13,8},{13,9},{14,7},{14,8},{14,9},{15,8},{15,9},{16,9}},
        // Jugador 4 – Abajo Izquierda
        {{9,3},{10,2},{10,3},{11,1},{11,2},{11,3},{12,0},{12,1},{12,2},{12,3}},
        // Jugador 5 – Arriba Izquierda
        {{4,15},{4,14},{5,14},{4,13},{5,13},{4,12},{6,12},{7,12},{5,12},{6,13}}
    };

    private final Tablero tablero;

    /**
     * Construye el gestor de fichas y coloca las piezas iniciales en el tablero dado.
     *
     * @param tablero el tablero donde se colocarán las fichas
     */
    public Fichas(Tablero tablero) {
        this.tablero = tablero;
        colocarPiezasIniciales();
    }

   /** Escribe solo las 20 fichas (2 × 10) de los jugadores activos en su posición inicial. */
    private void colocarPiezasIniciales() {
        // Solo colocamos fichas para el Jugador 0 (Arriba) y el Jugador 3 (Abajo)
        int[] jugadoresActivos = {0, 3}; 
        
        for (int jugador : jugadoresActivos) {
            for (int[] pos : ZONAS_INICIO[jugador]) {
                tablero.setFicha(pos[0], pos[1], jugador);
            }
        }
    }

    /**
     * Devuelve las posiciones iniciales del jugador indicado.
     *
     * @param jugador índice de jugador (0-5)
     * @return arreglo de pares [fila, columna]
     */
    public int[][] getZonaInicio(int jugador) {
        return ZONAS_INICIO[jugador];
    }

    /**
     * Mueve una ficha dentro del tablero: libera la celda origen y ocupa la destino.
     * No valida si el movimiento es legal (eso lo hará Movimiento).
     *
     * @param fOrigen  fila origen
     * @param cOrigen  columna origen
     * @param fDestino fila destino
     * @param cDestino columna destino
     * @return true si el movimiento se aplicó, false si las celdas son inválidas
     */
    public boolean moverFicha(int fOrigen, int cOrigen, int fDestino, int cDestino) {
        if (!tablero.esValida(fOrigen, cOrigen) || !tablero.esValida(fDestino, cDestino)) {
            return false;
        }
        int jugador = tablero.getFicha(fOrigen, cOrigen);
        if (jugador < 0) return false;               // celda origen vacía o inválida

        tablero.setFicha(fOrigen, cOrigen, Tablero.VACIO);
        tablero.setFicha(fDestino, cDestino, jugador);
        return true;
    }
}
