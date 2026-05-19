package src;


public class Fichas {
    private int[][] matriz;
     private int[][][] zonasInicio;

    public Fichas(){
        colocarPiezasIniciales();
        cargarZonasInicio();
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
                matriz[f][c] = p; 
            }
        }
    }
    
    //falta metodo fichas
}
