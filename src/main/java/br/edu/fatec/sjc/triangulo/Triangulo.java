package br.edu.fatec.sjc.triangulo;

/**
 * Exercicio 1 - classificacao de triangulos.
 *
 * Le tres valores inteiros que representam os lados de um triangulo e
 * informa se eles formam um triangulo isosceles, escaleno ou equilatero.
 *
 * Condicao de existencia: a soma de dois lados tem que ser MAIOR que o
 * terceiro lado (desigualdade triangular). Soma igual ao terceiro lado
 * produz um triangulo degenerado e, portanto, invalido.
 */
public class Triangulo {

    public enum Tipo {
        EQUILATERO,
        ISOSCELES,
        ESCALENO,
        INVALIDO
    }

    public Tipo classificar(int a, int b, int c) {
        if (!saoLadosPositivos(a, b, c)) {
            return Tipo.INVALIDO;
        }

        if (!respeitaDesigualdadeTriangular(a, b, c)) {
            return Tipo.INVALIDO;
        }

        if (a == b && b == c) {
            return Tipo.EQUILATERO;
        }

        if (a == b || b == c || a == c) {
            return Tipo.ISOSCELES;
        }

        return Tipo.ESCALENO;
    }

    private boolean saoLadosPositivos(int a, int b, int c) {
        return a > 0 && b > 0 && c > 0;
    }

    private boolean respeitaDesigualdadeTriangular(int a, int b, int c) {
        return a + b > c
                && a + c > b
                && b + c > a;
    }
}
