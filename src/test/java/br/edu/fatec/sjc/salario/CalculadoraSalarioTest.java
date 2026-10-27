package br.edu.fatec.sjc.salario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Exercicio 3 - CalculadoraSalario")
class CalculadoraSalarioTest {

    private static final double DELTA = 0.01;

    private CalculadoraSalario cut;

    @BeforeEach
    void setup() {
        this.cut = new CalculadoraSalario();
    }

    private Funcionario funcionario(Cargo cargo, double salarioBase) {
        return new Funcionario("Joao Andrade", "joao@exemplo.com", salarioBase, cargo);
    }

    @Nested
    @DisplayName("DESENVOLVEDOR - 20% a partir de 3000,00; 10% abaixo disso")
    class Desenvolvedor {

        @ParameterizedTest(name = "base {0} -> liquido {1}")
        @CsvSource({
                " 1000.00,  900.00",   // abaixo da faixa: 10%
                " 2999.99, 2699.99",   // fronteira inferior: ainda 10%
                " 3000.00, 2400.00",   // fronteira exata: ja 20%
                " 3000.01, 2400.01",   // acima da fronteira: 20%
                "10000.00, 8000.00"
        })
        void deveAplicarDescontoDoDesenvolvedor(double base, double esperado) {
            assertEquals(esperado, cut.calcularSalarioLiquido(funcionario(Cargo.DESENVOLVEDOR, base)), DELTA);
        }
    }

    @Nested
    @DisplayName("DBA - 25% a partir de 2000,00; 15% abaixo disso")
    class Dba {

        @ParameterizedTest(name = "base {0} -> liquido {1}")
        @CsvSource({
                " 1000.00,  850.00",   // 15%
                " 1999.99, 1699.99",   // fronteira inferior: ainda 15%
                " 2000.00, 1500.00",   // fronteira exata: ja 25%
                " 2000.01, 1500.01",
                " 8000.00, 6000.00"
        })
        void deveAplicarDescontoDoDba(double base, double esperado) {
            assertEquals(esperado, cut.calcularSalarioLiquido(funcionario(Cargo.DBA, base)), DELTA);
        }
    }

    @Nested
    @DisplayName("TESTADOR - 25% a partir de 2000,00; 15% abaixo disso")
    class Testador {

        @ParameterizedTest(name = "base {0} -> liquido {1}")
        @CsvSource({
                " 1000.00,  850.00",
                " 1999.99, 1699.99",
                " 2000.00, 1500.00",
                " 2000.01, 1500.01",
                " 8000.00, 6000.00"
        })
        void deveAplicarDescontoDoTestador(double base, double esperado) {
            assertEquals(esperado, cut.calcularSalarioLiquido(funcionario(Cargo.TESTADOR, base)), DELTA);
        }
    }

    @Nested
    @DisplayName("GERENTE - 30% a partir de 5000,00; 20% abaixo disso")
    class Gerente {

        @ParameterizedTest(name = "base {0} -> liquido {1}")
        @CsvSource({
                " 3000.00,  2400.00",  // 20%
                " 4999.99,  3999.99",  // fronteira inferior: ainda 20%
                " 5000.00,  3500.00",  // fronteira exata: ja 30%
                " 5000.01,  3500.01",
                "12000.00,  8400.00"
        })
        void deveAplicarDescontoDoGerente(double base, double esperado) {
            assertEquals(esperado, cut.calcularSalarioLiquido(funcionario(Cargo.GERENTE, base)), DELTA);
        }
    }

    @Nested
    @DisplayName("O cargo e o que diferencia o calculo")
    class ComparacaoEntreCargos {

        @Test
        @DisplayName("Mesmo salario base produz liquidos diferentes por cargo")
        void deveDiferenciarPorCargoComMesmoSalarioBase() {
            double base = 6000.00;

            assertEquals(4800.00, cut.calcularSalarioLiquido(funcionario(Cargo.DESENVOLVEDOR, base)), DELTA);
            assertEquals(4500.00, cut.calcularSalarioLiquido(funcionario(Cargo.DBA, base)), DELTA);
            assertEquals(4500.00, cut.calcularSalarioLiquido(funcionario(Cargo.TESTADOR, base)), DELTA);
            assertEquals(4200.00, cut.calcularSalarioLiquido(funcionario(Cargo.GERENTE, base)), DELTA);
        }
    }

    @Nested
    @DisplayName("Modelo Funcionario")
    class ModeloFuncionario {

        @Test
        @DisplayName("Funcionario carrega nome, email, salario-base e cargo")
        void deveCarregarOsDadosInformados() {
            Funcionario f = new Funcionario("Joao Andrade", "joao@exemplo.com", 4200.00, Cargo.DBA);

            assertEquals("Joao Andrade", f.getNome());
            assertEquals("joao@exemplo.com", f.getEmail());
            assertEquals(4200.00, f.getSalarioBase(), DELTA);
            assertEquals(Cargo.DBA, f.getCargo());
        }
    }
}
