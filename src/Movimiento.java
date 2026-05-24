package src;

import java.util.*;

public class Movimiento {
    
    private static final int[][] DIRS = {
        {-1, -1}, {-1, 0},  
        { 0, -1}, { 0, 1}, 
        {  1,  0}, { 1, 1}  
    };

    private final Tablero tablero;
    private final Fichas  fichas;

    public Movimiento(Tablero tablero, Fichas fichas) {
        this.tablero = tablero;
        this.fichas  = fichas;
    }

    
    public List<int[]> destinosLegales(int fO, int cO) {
        List<int[]> destinos = new ArrayList<>();

        if (!tablero.esValida(fO, cO) || tablero.getFicha(fO, cO) == Tablero.VACIO) {
            return destinos;
        }

        for (int[] d : DIRS) {
            int nf = fO + d[0];
            int nc = cO + d[1];
            if (tablero.esValida(nf, nc) && tablero.getFicha(nf, nc) == Tablero.VACIO) {
                destinos.add(new int[]{nf, nc});
            }
        }

        Set<String> visitados = new HashSet<>();
        Queue<int[]> cola = new LinkedList<>();

        visitados.add(fO + "," + cO);
        cola.add(new int[]{fO, cO});

        while (!cola.isEmpty()) {
            int[] actual = cola.poll();

            for (int[] d : DIRS) {
                int mf = actual[0] + d[0];     
                int mc = actual[1] + d[1];
                int df = actual[0] + (2 * d[0]); 
                int dc = actual[1] + (2 * d[1]);

                if (!tablero.esValida(mf, mc) || tablero.getFicha(mf, mc) == Tablero.VACIO) {
                    continue;
                }

                if (!tablero.esValida(df, dc) || tablero.getFicha(df, dc) != Tablero.VACIO) {
                    continue;
                }

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
}