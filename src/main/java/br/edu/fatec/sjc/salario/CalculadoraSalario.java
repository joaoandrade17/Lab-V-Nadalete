package br.edu.fatec.sjc.salario;

/**
 * Exercicio 3 - fase REFACTOR.
 *
 * A calculadora nao conhece mais as regras de cada cargo: ela so pergunta
 * ao Cargo qual o desconto para aquele salario base. Incluir um novo cargo
 * passa a ser uma linha no enum, sem alterar esta classe.
 */
public class CalculadoraSalario {

    public double calcularSalarioLiquido(Funcionario funcionario) {
        double base = funcionario.getSalarioBase();
        double desconto = funcionario.getCargo().descontoPara(base);

        return base * (1.0 - desconto);
    }
}
