package br.edu.fatec.sjc.triangulo;

import br.edu.fatec.sjc.triangulo.Triangulo.Tipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Exercicio 1 - Triangulo")
class TrianguloTest {

    private Triangulo cut;

    @BeforeEach
    void setup() {
        this.cut = new Triangulo();
    }

    @Nested
    @DisplayName("Triangulos validos")
    class Validos {

        @Test
        @DisplayName("CT01 - escaleno valido: 3,4,5")
        void deveClassificarComoEscaleno() {
            assertEquals(Tipo.ESCALENO, cut.classificar(3, 4, 5));
        }

        @Test
        @DisplayName("CT02 - isosceles valido: 5,5,8")
        void deveClassificarComoIsosceles() {
            assertEquals(Tipo.ISOSCELES, cut.classificar(5, 5, 8));
        }

        @Test
        @DisplayName("CT03 - equilatero valido: 7,7,7")
        void deveClassificarComoEquilatero() {
            assertEquals(Tipo.EQUILATERO, cut.classificar(7, 7, 7));
        }

        @ParameterizedTest(name = "CT04.{index} - isosceles em {0},{1},{2}")
        @DisplayName("CT04 - isosceles: 3 permutacoes dos mesmos valores")
        @CsvSource({
                "5, 5, 8",
                "5, 8, 5",
                "8, 5, 5"
        })
        void deveClassificarComoIsoscelesEmQualquerPermutacao(int a, int b, int c) {
            assertEquals(Tipo.ISOSCELES, cut.classificar(a, b, c));
        }
    }

    @Nested
    @DisplayName("Valores fora do dominio")
    class ValoresInvalidos {

        @ParameterizedTest(name = "CT05.{index} - zero em {0},{1},{2}")
        @DisplayName("CT05 - um valor igual a zero")
        @CsvSource({
                "0, 4, 5",
                "3, 0, 5",
                "3, 4, 0"
        })
        void deveRejeitarLadoZero(int a, int b, int c) {
            assertEquals(Tipo.INVALIDO, cut.classificar(a, b, c));
        }

        @ParameterizedTest(name = "CT06.{index} - negativo em {0},{1},{2}")
        @DisplayName("CT06 - um valor negativo")
        @CsvSource({
                "-3,  4,  5",
                " 3, -4,  5",
                " 3,  4, -5"
        })
        void deveRejeitarLadoNegativo(int a, int b, int c) {
            assertEquals(Tipo.INVALIDO, cut.classificar(a, b, c));
        }

        @Test
        @DisplayName("CT11 - os tres valores iguais a zero: 0,0,0")
        void deveRejeitarTodosOsLadosZero() {
            assertEquals(Tipo.INVALIDO, cut.classificar(0, 0, 0));
        }
    }

    @Nested
    @DisplayName("Violacao da desigualdade triangular")
    class DesigualdadeTriangular {

        @ParameterizedTest(name = "CT07/08.{index} - soma igual ao terceiro em {0},{1},{2}")
        @DisplayName("CT07 e CT08 - soma de 2 lados IGUAL ao terceiro, em cada permutacao")
        @CsvSource({
                "2, 3, 5",
                "2, 5, 3",
                "5, 2, 3"
        })
        void deveRejeitarQuandoSomaDeDoisLadosEIgualAoTerceiro(int a, int b, int c) {
            assertEquals(Tipo.INVALIDO, cut.classificar(a, b, c));
        }

        @ParameterizedTest(name = "CT09/10.{index} - soma menor que o terceiro em {0},{1},{2}")
        @DisplayName("CT09 e CT10 - soma de 2 lados MENOR que o terceiro, em cada permutacao")
        @CsvSource({
                "1,  2, 10",
                "1, 10,  2",
                "10, 1,  2"
        })
        void deveRejeitarQuandoSomaDeDoisLadosEMenorQueOTerceiro(int a, int b, int c) {
            assertEquals(Tipo.INVALIDO, cut.classificar(a, b, c));
        }
    }
}
