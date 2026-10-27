package br.edu.fatec.sjc.pessoa;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Exercicio 2 - validacao de Person antes da inclusao.
 *
 * isValidToInclude() devolve a LISTA de erros encontrados (lista vazia =
 * objeto valido). Todas as regras sao avaliadas sempre, sem curto-circuito,
 * para que o chamador receba de uma vez tudo o que precisa corrigir.
 */
public class PersonDAO {

    public static final String ERRO_NOME =
            "Nome deve ser composto por ao menos 2 partes, contendo apenas letras";
    public static final String ERRO_IDADE =
            "Idade deve estar no intervalo [1, 200]";
    public static final String ERRO_SEM_EMAIL =
            "Person deve possuir ao menos um Email associado";
    public static final String ERRO_EMAIL_INVALIDO =
            "Email fora do formato esperado (parte@parte.parte): ";

    private static final int IDADE_MINIMA = 1;
    private static final int IDADE_MAXIMA = 200;

    /** Cada parte do nome deve ter ao menos uma letra (\p{L} aceita acentos). */
    private static final Pattern PARTE_DO_NOME = Pattern.compile("^\\p{L}+$");

    /** Formato ___@___.___ , com cada parte contendo ao menos um caractere. */
    private static final Pattern FORMATO_EMAIL =
            Pattern.compile("^[^@\\s]+@[^@\\s.]+(\\.[^@\\s.]+)+$");

    private final List<Person> persistidos = new ArrayList<>();

    public List<String> isValidToInclude(Person p) {
        List<String> erros = new ArrayList<>();

        if (!temNomeValido(p)) {
            erros.add(ERRO_NOME);
        }

        if (!temIdadeValida(p)) {
            erros.add(ERRO_IDADE);
        }

        if (p.getEmails().isEmpty()) {
            erros.add(ERRO_SEM_EMAIL);
        } else {
            for (Email email : p.getEmails()) {
                if (!temFormatoValido(email)) {
                    erros.add(ERRO_EMAIL_INVALIDO + email.getName());
                }
            }
        }

        return erros;
    }

    public void save(Person p) {
        List<String> erros = isValidToInclude(p);

        if (!erros.isEmpty()) {
            throw new IllegalArgumentException("Person invalido para inclusao: " + erros);
        }

        this.persistidos.add(p);
    }

    public int count() {
        return this.persistidos.size();
    }

    private boolean temNomeValido(Person p) {
        if (p.getName() == null) {
            return false;
        }

        String[] partes = p.getName().trim().split("\\s+");

        if (partes.length < 2) {
            return false;
        }

        for (String parte : partes) {
            if (!PARTE_DO_NOME.matcher(parte).matches()) {
                return false;
            }
        }

        return true;
    }

    private boolean temIdadeValida(Person p) {
        return p.getAge() >= IDADE_MINIMA && p.getAge() <= IDADE_MAXIMA;
    }

    private boolean temFormatoValido(Email email) {
        return email.getName() != null
                && FORMATO_EMAIL.matcher(email.getName()).matches();
    }
}
