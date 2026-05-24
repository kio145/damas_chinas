package src;

import java.util.*;

public class Movimiento {

    private static final int[] OPUESTO = {3, 4, 5, 0, 1, 2};
    private static final int[][] DIRS = {
        {-1, 0}, {-1, 1},   // arriba-izquierda, arriba-derecha
        { 0,-1}, { 0, 1},   // izquierda, derecha
        { 1,-1}, { 1, 0},   // abajo-izquierda, abajo-derecha
        {-1,-1}, { 1, 1}    // diagonal vertical: arriba y abajo
    };

    private final Tablero tablero;
    private final Fichas  fichas;

    public Movimiento(Tablero tablero, Fichas fichas) {
        this.tablero = tablero;
        this.fichas  = fichas;
    }

    
    public List<int[]> destinosLegales(int fO, int cO) {
        List<int[]> destinos = new ArrayList<>();

        // 1) Pasos simples
        for (int[] d : DIRS) {
            int nf = fO + d[0], nc = cO + d[1];
            if (tablero.esValida(nf, nc) && tablero.getFicha(nf, nc) == Tablero.VACIO) {
                destinos.add(new int[]{nf, nc});
            }
        }

        // 2) Saltos encadenados (BFS)
        Set<String> visitados = new HashSet<>();
        visitados.add(fO + "," + cO);
        Queue<int[]> cola = new LinkedList<>();
        cola.add(new int[]{fO, cO});

        while (!cola.isEmpty()) {
            int[] actual = cola.poll();
            for (int[] d : DIRS) {
                int mf = actual[0] + d[0];   // celda intermedia
                int mc = actual[1] + d[1];
                int df = actual[0] + 2 * d[0]; // celda destino
                int dc = actual[1] + 2 * d[1];

                if (!tablero.esValida(mf, mc)) continue;
                if (tablero.getFicha(mf, mc) == Tablero.VACIO) continue; // debe haber ficha
                if (!tablero.esValida(df, dc)) continue;
                if (tablero.getFicha(df, dc) != Tablero.VACIO) continue; // destino libre

                String clave = df + "," + dc;
                if (!visitados.contains(clave)) {
                    visitados.add(clave);
                    destinos.add(new int[]{df, dc});
                    cola.add(new int[]{df, dc});
                }
            }
        }

        return destinos;
    }

    public boolean mover(int fO, int cO, int fD, int cD) {
        List<int[]> legales = destinosLegales(fO, cO);
        for (int[] dest : legales) {
            if (dest[0] == fD && dest[1] == cD) {
                return fichas.moverFicha(fO, cO, fD, cD);
            }
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Victoria
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Comprueba si el jugador indicado ha ganado:
     * sus 10 fichas deben ocupar (al menos) todas las casillas de la
     * zona de inicio del jugador opuesto.
     *
     * @param jugador índice 0-5
     * @return true si ganó
     */
    public boolean haGanado(int jugador) {
        int opuesto = OPUESTO[jugador];
        int[][] zonaObjetivo = fichas.getZonaInicio(opuesto);
        for (int[] pos : zonaObjetivo) {
            if (tablero.getFicha(pos[0], pos[1]) != jugador) return false;
        }
        return true;
    }

    /**
     * Devuelve el índice del jugador ganador (0 o 3), o -1 si nadie ha ganado aún.
     */
    public int ganador() {
        int[] jugadoresActivos = {0, 3};
        for (int j : jugadoresActivos) {
            if (haGanado(j)) return j;
        }
        return -1;
    }
}
