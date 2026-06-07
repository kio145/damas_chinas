package src;

import java.util.Arrays;

public class Tablero {

    public static final int VACIO    = -1;
    public static final int INVALIDO = -2;

    private int[][] matriz;

    public Tablero() {
        matriz = new int[17][17];
        for (int i = 0; i < 17; i++) {
            Arrays.fill(matriz[i], INVALIDO);
        }
        crearEstructuraEstrella();
    }

    private void crearEstructuraEstrella() {
        int[][] configFilas = {
            {9, 1}, {8, 2}, {7, 3}, {6, 4},
            {0, 16}, {1, 14}, {2, 12}, {3, 10}, {4, 9}, {3, 10}, {2, 12}, {1, 14}, {0, 16},
            {6, 4}, {7, 3}, {8, 2}, {9, 1}
        };
        for (int f = 0; f < 17; f++) {
            int inicio   = configFilas[f][0];
            int cantidad = configFilas[f][1];
            for (int c = inicio; c < inicio + cantidad; c++) {
                matriz[f][c] = VACIO;
            }
        }
    }

    /** Devuelve el valor en la celda (f, c): jugador 0-5, VACIO o INVALIDO. */
    public int getFicha(int f, int c) {
        if (f < 0 || f >= 17 || c < 0 || c >= 17) return INVALIDO;
        return matriz[f][c];
    }

    /** Coloca el valor val en la celda (f, c) si es una casilla válida del tablero. */
    public void setFicha(int f, int c, int val) {
        if (f < 0 || f >= 17 || c < 0 || c >= 17) return;
        if (matriz[f][c] == INVALIDO) return;   // no se puede escribir fuera del tablero
        matriz[f][c] = val;
    }

    /** Indica si la posición existe dentro de la estrella. */
    public boolean esValida(int f, int c) {
        return f >= 0 && f < 17 && c >= 0 && c < 17 && matriz[f][c] != INVALIDO;
    }

    /** Devuelve una copia de la matriz (para que nadie modifique el estado interno). */
    public int[][] getMatriz() {
        int[][] copia = new int[17][17];
        for (int i = 0; i < 17; i++) {
            copia[i] = Arrays.copyOf(matriz[i], 17);
        }
        return copia;
    }

    public boolean moverFicha(int fOrigen, int cOrigen, int fDestino, int cDestino) {
    if (!esValida(fOrigen, cOrigen) || !esValida(fDestino, cDestino)) {
        return false;
    }
    int jugador = getFicha(fOrigen, cOrigen);
    if (jugador < 0) return false;

    setFicha(fOrigen, cOrigen, VACIO);
    setFicha(fDestino, cDestino, jugador);
    return true;
}

}
