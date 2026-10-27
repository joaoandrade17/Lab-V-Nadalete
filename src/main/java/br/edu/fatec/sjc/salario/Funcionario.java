package br.edu.fatec.sjc.salario;

public class Funcionario {

    private final String nome;
    private final String email;
    private final double salarioBase;
    private final Cargo cargo;

    public Funcionario(String nome, String email, double salarioBase, Cargo cargo) {
        this.nome = nome;
        this.email = email;
        this.salarioBase = salarioBase;
        this.cargo = cargo;
    }

    public String getNome() {
        return this.nome;
    }

    public String getEmail() {
        return this.email;
    }

    public double getSalarioBase() {
        return this.salarioBase;
    }

    public Cargo getCargo() {
        return this.cargo;
    }
}
