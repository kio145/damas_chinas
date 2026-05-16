import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {

    // Constantes básicas
    public static final int VACIO = -1;
    public static final int INVALIDO = -2;
    
    // Matriz del tablero de juego
    private int[][] matriz;
    
    // Arreglo tridimensional con los triángulos de inicio de los 5 jugadores (10 piezas cada uno)
    private int[][][] zonasInicio;

    public Board() {
        matriz = new int[17][17];
        // Llenar todo como inválido al principio
        for (int i = 0; i < 17; i++) {
            Arrays.fill(matriz[i], INVALIDO);
        }
        
        cargarZonasInicio();
        crearEstructuraEstrella();
        colocarPiezasIniciales();
    }

    private void crearEstructuraEstrella() {
        // Indica cuántas celdas jugables hay por cada fila y en qué columna inician
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

    private void cargarZonasInicio() {
        zonasInicio = new int[6][10][2];

        // Jugador 0 - Arriba
        zonasInicio[0] = new int[][]{{0, 9}, {1, 8}, {1, 9}, {2, 7}, {2, 8}, {2, 9}, {3, 6}, {3, 7}, {3, 8}, {3, 9}};
        // Jugador 1 - Arriba Derecha
        zonasInicio[1] = new int[][]{{4, 0}, {4, 1}, {4, 2}, {4, 3}, {5, 1}, {5, 2}, {5, 3}, {6, 2}, {6, 3}, {7, 3}};
        // Jugador 2 - Abajo Derecha
        zonasInicio[2] = new int[][]{{9, 12}, {10, 12}, {10, 13}, {11, 12}, {11, 13}, {11, 14}, {12, 12}, {12, 13}, {12, 14}, {12, 15}};
        // Jugador 3 - Abajo
        zonasInicio[3] = new int[][]{{13, 6}, {13, 7}, {13, 8}, {13, 9}, {14, 7}, {14, 8}, {14, 9}, {15, 8}, {15, 9}, {16, 9}};
        // Jugador 4 - Abajo Izquierda
        zonasInicio[4] = new int[][]{{9, 3}, {10, 2}, {10, 3}, {11, 1}, {11, 2}, {11, 3}, {12, 0}, {12, 1}, {12, 2}, {12, 3}};
        //jugador5 
        
        
        zonasInicio[5] = new int[][]{ {4, 15},{4, 14}, {5, 14},{4, 13}, {5, 13}, {4, 12},{6, 12}, {7, 12}, {5, 12}, {6, 13} };

}

    private void colocarPiezasIniciales() {
        for (int p = 0; p < 6; p++) {
            for (int i = 0; i < 10; i++) {
                int f = zonasInicio[p][i][0];
                int c = zonasInicio[p][i][1];
                matriz[f][c] = p; // Coloca el número de jugador
            }
        }
    }

    public int[][] obtenerDestinosVictoria(int jugador) {
        // Cada jugador debe viajar al triángulo opuesto del tablero
        switch (jugador) {
           case 0: return zonasInicio[3]; // El 0 va al 3 (Arriba -> Abajo)
        case 1: return zonasInicio[4]; // El 1 va al 4 (Arriba Derecha -> Abajo Izquierda)
        case 2: return zonasInicio[5]; // El 2 va al 5 (Abajo Derecha -> Arriba Izquierda)
        case 3: return zonasInicio[0]; // El 3 va al 0 (Abajo -> Arriba)
        case 4: return zonasInicio[1]; // El 4 va al 1 (Abajo Izquierda -> Arriba Derecha)
        case 5: return zonasInicio[2]; // El 5 va al 2 (Arriba Izquierda -> Abajo Derecha)
        }
        return null;
    }

    public int obtenerCelda(int f, int c) {
        if (f < 0 || f >= 17 || c < 0 || c >= 17) return INVALIDO;
        return matriz[f][c];
    }

    public boolean esValida(int f, int c) {
        return obtenerCelda(f, c) != INVALIDO;
    }

    public boolean estaVacia(int f, int c) {
        return obtenerCelda(f, c) == VACIO;
    }

    public void moverPieza(int f1, int c1, int f2, int c2) {
        matriz[f2][c2] = matriz[f1][c1];
        matriz[f1][c1] = VACIO;
    }

    public List<int[]> calcularMovimientos(int f, int c) {
        List<int[]> lista = new ArrayList<>();
        if (obtenerCelda(f, c) < 0) return lista;

        // Las 6 direcciones de movimiento en un eje de coordenadas de damas chinas
        int[][] dirs = {{-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, 0}, {1, 1}};

        // 1. Movimientos simples paso a paso
        for (int[] d : dirs) {
            int nf = f + d[0];
            int nc = c + d[1];
            if (esValida(nf, nc) && estaVacia(nf, nc)) {
                lista.add(new int[]{nf, nc});
            }
        }

        // 2. Movimientos de saltos encadenados recursivos
        Set<String> visitados = new HashSet<>();
        visitados.add(f + "," + c);
        buscarSaltosRecursivos(f, c, dirs, visitados, lista);

        return lista;
    }

    private void buscarSaltosRecursivos(int f, int c, int[][] dirs, Set<String> visitados, List<int[]> lista) {
        for (int[] d : dirs) {
            int fIntermedia = f + d[0];
            int cIntermedia = c + d[1];
            int fSalto = f + 2 * d[0];
            int cSalto = c + 2 * d[1];

            // Condición para saltar: celda intermedia con ficha y destino final vacío y válido
            if (esValida(fIntermedia, cIntermedia) && !estaVacia(fIntermedia, cIntermedia)) {
                if (esValida(fSalto, cSalto) && estaVacia(fSalto, cSalto)) {
                    String coordClave = fSalto + "," + cSalto;
                    if (!visitados.contains(coordClave)) {
                        visitados.add(coordClave);
                        lista.add(new int[]{fSalto, cSalto});
                        // Llamada recursiva para seguir encadenando saltos continuos
                        buscarSaltosRecursivos(fSalto, cSalto, dirs, visitados, lista);
                    }
                }
            }
        }
    }

    public boolean revisarVictoria(int jugador) {
        int[][] metas = obtenerDestinosVictoria(jugador);
        for (int[] celda : metas) {
            if (matriz[celda[0]][celda[1]] != jugador) {
                return false;
            }
        }
        return true;
    }
}