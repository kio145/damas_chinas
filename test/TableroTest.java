package test;
package src;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la clase Tablero.
 *
 * Dependencias (agregar al proyecto):
 *   - JUnit 5 (junit-jupiter-api 5.x + junit-jupiter-engine 5.x)
 *
 * Con Maven añadir en pom.xml:
 *   <dependency>
 *       <groupId>org.junit.jupiter</groupId>
 *       <artifactId>junit-jupiter</artifactId>
 *       <version>5.10.0</version>
 *       <scope>test</scope>
 *   </dependency>
 *
 * Con Gradle añadir en build.gradle:
 *   testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
 */
class TableroTest {

    private Tablero tablero;

    @BeforeEach
    void setUp() {
        // Cada test parte de un tablero limpio y recién construido
        tablero = new Tablero();
    }

    // =========================================================================
    // 1. ESTRUCTURA DE LA ESTRELLA HEXAGONAL
    // =========================================================================
    @Nested
    @DisplayName("1. Estructura del tablero (estrella hexagonal)")
    class EstructuraTablero {

        @Test
        @DisplayName("El tablero se crea sin lanzar excepciones")
        void constructorNoCrash() {
            assertDoesNotThrow(() -> new Tablero());
        }

        @Test
        @DisplayName("Las celdas del centro de la estrella son VACIO")
        void celdasCentralesVacias() {
            // La fila 8 es la fila central con 9 celdas (cols 4..12)
            for (int c = 4; c <= 12; c++) {
                assertEquals(Tablero.VACIO, tablero.obtenerCelda(8, c),
                    "La celda central (" + 8 + "," + c + ") debe ser VACIO");
            }
        }

        @Test
        @DisplayName("Las celdas fuera de la estrella son INVALIDO")
        void celdasFueraDeEstructuraSonInvalidas() {
            // Esquina (0,0) nunca forma parte de la estrella
            assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(0, 0));
            // Esquina opuesta (16,16) tampoco
            assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(16, 16));
            // Fila 0 solo tiene la celda (0,9)
            assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(0, 0));
            assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(0, 8));
            assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(0, 10));
        }

        @Test
        @DisplayName("Los índices fuera de rango devuelven INVALIDO")
        void fueraDeRangeDevuelveInvalido() {
            assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(-1, 0));
            assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(0, -1));
            assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(17, 0));
            assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(0, 17));
        }

        @Test
        @DisplayName("La celda (0,9) es la única celda válida de la fila 0")
        void fila0SoloTieneUnaCelda() {
            assertEquals(Tablero.VACIO, tablero.obtenerCelda(0, 9));
            for (int c = 0; c < 17; c++) {
                if (c != 9) {
                    assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(0, c),
                        "La fila 0, col " + c + " debe ser INVALIDO");
                }
            }
        }

        @Test
        @DisplayName("La celda (16,9) es la única celda válida de la fila 16")
        void fila16SoloTieneUnaCelda() {
            assertEquals(Tablero.VACIO, tablero.obtenerCelda(16, 9));
            for (int c = 0; c < 17; c++) {
                if (c != 9) {
                    assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(16, c),
                        "La fila 16, col " + c + " debe ser INVALIDO");
                }
            }
        }

        @Test
        @DisplayName("La fila 4 tiene 16 celdas válidas (cols 0..15)")
        void fila4TieneCeldasCorrectas() {
            int count = 0;
            for (int c = 0; c < 17; c++) {
                if (tablero.obtenerCelda(4, c) != Tablero.INVALIDO) count++;
            }
            assertEquals(16, count, "La fila 4 debe tener 16 celdas válidas");
        }
    }

    // =========================================================================
    // 2. esValida
    // =========================================================================
    @Nested
    @DisplayName("2. esValida(fila, col)")
    class EsValida {

        @Test
        @DisplayName("Devuelve true para celdas dentro de la estrella")
        void verdaderaDentroDeEstrella() {
            assertTrue(tablero.esValida(0, 9));   // punta superior
            assertTrue(tablero.esValida(16, 9));  // punta inferior
            assertTrue(tablero.esValida(8, 4));   // celda central izquierda
            assertTrue(tablero.esValida(8, 12));  // celda central derecha
        }

        @Test
        @DisplayName("Devuelve false para celdas fuera de la estrella")
        void falsaFueraDeEstrella() {
            assertFalse(tablero.esValida(0, 0));
            assertFalse(tablero.esValida(16, 0));
            assertFalse(tablero.esValida(0, 16));
        }

        @Test
        @DisplayName("Devuelve false para índices negativos o ≥ 17")
        void falsaFueraDeRango() {
            assertFalse(tablero.esValida(-1, 9));
            assertFalse(tablero.esValida(9, -1));
            assertFalse(tablero.esValida(17, 9));
            assertFalse(tablero.esValida(9, 17));
        }
    }

    // =========================================================================
    // 3. estaVacia
    // =========================================================================
    @Nested
    @DisplayName("3. estaVacia(fila, col)")
    class EstaVacia {

        @Test
        @DisplayName("Una celda válida recién creada está vacía")
        void celdaValidaEstaVacia() {
            assertTrue(tablero.estaVacia(0, 9));
            assertTrue(tablero.estaVacia(8, 8));
        }

        @Test
        @DisplayName("Una celda inválida NO está vacía (es INVALIDO, no VACIO)")
        void celdaInvalidaNoEstaVacia() {
            assertFalse(tablero.estaVacia(0, 0));
        }
    }

    // =========================================================================
    // 4. obtenerCelda
    // =========================================================================
    @Nested
    @DisplayName("4. obtenerCelda(fila, col)")
    class ObtenerCelda {

        @Test
        @DisplayName("Celda válida vacía devuelve VACIO")
        void celdaVaciaDevuelveVACIO() {
            assertEquals(Tablero.VACIO, tablero.obtenerCelda(8, 8));
        }

        @Test
        @DisplayName("Celda fuera de la estrella devuelve INVALIDO")
        void celdaInvalidaDevuelveINVALIDO() {
            assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(0, 0));
        }

        @Test
        @DisplayName("Índice fuera de matriz devuelve INVALIDO")
        void indiceExternoDevuelveINVALIDO() {
            assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(100, 100));
            assertEquals(Tablero.INVALIDO, tablero.obtenerCelda(-5, -5));
        }
    }

    // =========================================================================
    // 5. moverPieza
    // =========================================================================
    @Nested
    @DisplayName("5. moverPieza(f1, c1, f2, c2)")
    class MoverPieza {

        @Test
        @DisplayName("La pieza aparece en el destino y el origen queda VACIO")
        void moverDejaCeldaOrigenVacia() {
            // Colocamos manualmente una ficha en (8,8) y la movemos a (8,9)
            tablero.setearCelda(8, 8, 0);   // jugador 0 en origen
            tablero.moverPieza(8, 8, 8, 9);

            assertEquals(0,             tablero.obtenerCelda(8, 9), "La ficha debe estar en el destino");
            assertEquals(Tablero.VACIO, tablero.obtenerCelda(8, 8), "El origen debe quedar VACIO");
        }

        @Test
        @DisplayName("Mover no altera celdas no involucradas")
        void moverNoAfectaOtros() {
            tablero.setearCelda(8, 8, 1);
            tablero.moverPieza(8, 8, 8, 9);

            // Una celda ajena sigue VACIA
            assertEquals(Tablero.VACIO, tablero.obtenerCelda(7, 7));
        }

        @Test
        @DisplayName("Se puede mover en cualquiera de las 6 direcciones válidas")
        void moverEnTodasLasDirecciones() {
            int[][] dirs = {{-1,-1},{-1,0},{0,-1},{0,1},{1,0},{1,1}};
            int fBase = 8, cBase = 8;

            for (int[] d : dirs) {
                tablero = new Tablero(); // tablero limpio por dirección
                int fDest = fBase + d[0];
                int cDest = cBase + d[1];

                if (tablero.esValida(fDest, cDest)) {
                    tablero.setearCelda(fBase, cBase, 2);
                    tablero.moverPieza(fBase, cBase, fDest, cDest);

                    assertEquals(2, tablero.obtenerCelda(fDest, cDest),
                        "Debe moverse en dirección [" + d[0] + "," + d[1] + "]");
                    assertEquals(Tablero.VACIO, tablero.obtenerCelda(fBase, cBase),
                        "El origen debe quedar VACIO tras el movimiento");
                }
            }
        }
    }

    // =========================================================================
    // 6. Constantes públicas
    // =========================================================================
    @Nested
    @DisplayName("6. Constantes VACIO e INVALIDO")
    class Constantes {

        @Test
        @DisplayName("VACIO vale -1")
        void vacioEsMinusUno() {
            assertEquals(-1, Tablero.VACIO);
        }

        @Test
        @DisplayName("INVALIDO vale -2")
        void invalidoEsMinusDos() {
            assertEquals(-2, Tablero.INVALIDO);
        }

        @Test
        @DisplayName("VACIO e INVALIDO son distintos entre sí")
        void vacioDistintoDeInvalido() {
            assertNotEquals(Tablero.VACIO, Tablero.INVALIDO);
        }
    }
}