package br.edu.fatec.sjc.salario;

/**
 * Cada cargo conhece a propria faixa de desconto:
 * um salario de corte, o desconto aplicado a partir dele (inclusive)
 * e o desconto aplicado abaixo dele.
 */
public enum Cargo {

    DESENVOLVEDOR(3000.00, 0.20, 0.10),
    DBA          (2000.00, 0.25, 0.15),
    TESTADOR     (2000.00, 0.25, 0.15),
    GERENTE      (5000.00, 0.30, 0.20);

    private final double salarioDeCorte;
    private final double descontoAPartirDoCorte;
    private final double descontoAbaixoDoCorte;

    Cargo(double salarioDeCorte, double descontoAPartirDoCorte, double descontoAbaixoDoCorte) {
        this.salarioDeCorte = salarioDeCorte;
        this.descontoAPartirDoCorte = descontoAPartirDoCorte;
        this.descontoAbaixoDoCorte = descontoAbaixoDoCorte;
    }

    public double descontoPara(double salarioBase) {
        return salarioBase >= this.salarioDeCorte
                ? this.descontoAPartirDoCorte
                : this.descontoAbaixoDoCorte;
    }
}
