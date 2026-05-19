package src;

import java.util.Arrays;

public class Tablero {

    public static final int VACIO = -1;
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
            int inicio = configFilas[f][0];
            int cantidad = configFilas[f][1];
            for (int c = inicio; c < inicio + cantidad; c++) {
                matriz[f][c] = VACIO;
            }
        }
    }

    

    
    

}