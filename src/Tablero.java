package src;

import java.util.Arrays;

public class Tablero {

    public static final int VACIO = -1;
    public static final int INVALIDO = -2;
    public static final int TAMANIO = 17;

    private int[][] matriz;

    public Tablero() {
        matriz = new int[TAMANIO][TAMANIO];

        for (int i = 0; i < TAMANIO; i++) {
            Arrays.fill(matriz[i], INVALIDO);
        }

        crearEstructuraEstrella();
    }

    private void crearEstructuraEstrella() {
        int[][] configFilas = {
            {9, 1}, {8, 2}, {7, 3}, {6, 4},
            {0, 16}, {1, 14}, {2, 12}, {3, 10}, {4, 9},
            {3, 10}, {2, 12}, {1, 14}, {0, 16},
            {6, 4}, {7, 3}, {8, 2}, {9, 1}
        };

        for (int f = 0; f < TAMANIO; f++) {
            int inicio = configFilas[f][0];
            int cantidad = configFilas[f][1];

            for (int c = inicio; c < inicio + cantidad; c++) {
                matriz[f][c] = VACIO;
            }
        }
    }

    public int getFicha(int f, int c) {
        if (f < 0 || f >= TAMANIO || c < 0 || c >= TAMANIO) {
            return INVALIDO;
        }

        return matriz[f][c];
    }

    public void setFicha(int f, int c, int val) {
        if (f < 0 || f >= TAMANIO || c < 0 || c >= TAMANIO) {
            return;
        }

        if (matriz[f][c] == INVALIDO) {
            return;
        }

        matriz[f][c] = val;
    }

    public boolean esValida(int f, int c) {
        return f >= 0 && f < TAMANIO &&
               c >= 0 && c < TAMANIO &&
               matriz[f][c] != INVALIDO;
    }

    public int[][] getMatriz() {
        int[][] copia = new int[TAMANIO][TAMANIO];

        for (int i = 0; i < TAMANIO; i++) {
            copia[i] = Arrays.copyOf(matriz[i], TAMANIO);
        }

        return copia;
    }

    public boolean moverFicha(int fOrigen, int cOrigen, int fDestino, int cDestino) {
        if (!esValida(fOrigen, cOrigen) || !esValida(fDestino, cDestino)) {
            return false;
        }

        int jugador = getFicha(fOrigen, cOrigen);

        if (jugador < 0) {
            return false;
        }

        setFicha(fOrigen, cOrigen, VACIO);
        setFicha(fDestino, cDestino, jugador);

        return true;
    }
}