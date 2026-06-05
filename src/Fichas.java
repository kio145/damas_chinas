package src;

public class Fichas {

    private static final int[][][] ZONAS_INICIO = {
        {{0,9},{1,8},{1,9},{2,7},{2,8},{2,9},{3,6},{3,7},{3,8},{3,9}},
        {{4,0},{4,1},{4,2},{4,3},{5,1},{5,2},{5,3},{6,2},{6,3},{7,3}},
        {{9,12},{10,12},{10,13},{11,12},{11,13},{11,14},{12,12},{12,13},{12,14},{12,15}},
        {{13,6},{13,7},{13,8},{13,9},{14,7},{14,8},{14,9},{15,8},{15,9},{16,9}},
        {{9,3},{10,2},{10,3},{11,1},{11,2},{11,3},{12,0},{12,1},{12,2},{12,3}},
        {{4,15},{4,14},{5,14},{4,13},{5,13},{4,12},{6,12},{7,12},{5,12},{6,13}}
    };

    private static final int[] ZONAS_OBJETIVO = { 3, 4, 5, 0, 1, 2 };

    // -----------------------------------------------------------------------
    // REFACTORIZACIÓN 5: Reemplazar condicional por tabla de datos
    //
    // ANTES: cadena de if (cantidadJugadores == 2) ... else if (== 3) ...
    // AHORA: tabla indexada por cantidad de jugadores (índice 2..6)
    //
    // La lógica de "qué jugadores participan según la cantidad" es pura
    // asignación de datos, no lógica de control. Una tabla lo expresa mejor,
    // es más fácil de mantener y elimina la posibilidad de olvidar un caso.
    // -----------------------------------------------------------------------
    private static final int[][] JUGADORES_POR_CANTIDAD = {
        null,        // índice 0 — no usado
        null,        // índice 1 — no usado
        {0, 3},      // 2 jugadores
        {0, 2, 4},   // 3 jugadores
        {0, 1, 3, 4},// 4 jugadores
        {0, 1, 2, 3, 4},       // 5 jugadores
        {0, 1, 2, 3, 4, 5}     // 6 jugadores
    };

    private final Tablero tablero;
    private int[] jugadoresActivos;

    public Fichas(Tablero tablero, int cantidadJugadores) {
        this.tablero = tablero;
        this.jugadoresActivos = obtenerJugadoresActivos(cantidadJugadores);
        colocarPiezasIniciales();
    }

   private int[] obtenerJugadoresActivos(int cantidadJugadores) {
        if (cantidadJugadores >= 2 && cantidadJugadores <= 6) {
        return JUGADORES_POR_CANTIDAD[cantidadJugadores];
        } else {
        return JUGADORES_POR_CANTIDAD[2];
        }
    }

    private void colocarPiezasIniciales() {
        for (int jugador : jugadoresActivos) {
            for (int[] pos : ZONAS_INICIO[jugador]) {
                tablero.setFicha(pos[0], pos[1], jugador);
            }
        }
    }

    public int[][] getZonaInicio(int jugador) {
        return ZONAS_INICIO[jugador];
    }

    public int[][] getZonaObjetivo(int jugador) {
        return ZONAS_INICIO[ZONAS_OBJETIVO[jugador]];
    }

    public int[] getJugadoresActivos() {
        return jugadoresActivos;
    }

    public boolean esJugadorActivo(int jugador) {
        for (int j : jugadoresActivos) {
            if (j == jugador) return true;
        }
        return false;
    }

    public boolean moverFicha(int fOrigen, int cOrigen, int fDestino, int cDestino) {
        if (!tablero.esValida(fOrigen, cOrigen) || !tablero.esValida(fDestino, cDestino)) {
            return false;
        }
        int jugador = tablero.getFicha(fOrigen, cOrigen);
        if (jugador < 0) return false;

        tablero.setFicha(fOrigen, cOrigen, Tablero.VACIO);
        tablero.setFicha(fDestino, cDestino, jugador);
        return true;
    }
}
