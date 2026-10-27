package br.edu.fatec.sjc.pessoa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Exercicio 2 - PersonDAO.isValidToInclude")
class PersonDAOTest {

    private PersonDAO cut;

    @BeforeEach
    void setup() {
        this.cut = new PersonDAO();
    }

    private Person pessoaValida() {
        return new Person(1, "Joao Andrade", 25)
                .addEmail(new Email(1, "joao@exemplo.com"));
    }

    @Test
    @DisplayName("Person totalmente valido nao produz nenhum erro")
    void naoDeveRetornarErrosParaPersonValido() {
        List<String> erros = cut.isValidToInclude(pessoaValida());

        assertTrue(erros.isEmpty(), "esperava lista vazia, veio: " + erros);
    }

    @Nested
    @DisplayName("Regra 1 - nome com ao menos 2 partes, apenas letras")
    class ValidacaoDoNome {

        @ParameterizedTest(name = "nome invalido: \"{0}\"")
        @ValueSource(strings = {
                "Joao",              // uma unica parte
                "Joao Andrade 123",  // contem digitos
                "Joao 2Andrade",     // parte com digito
                "Joao@Andrade",      // caractere nao-letra
                "   "                // apenas espacos
        })
        void deveAcusarNomeInvalido(String nome) {
            Person p = new Person(1, nome, 25).addEmail(new Email("joao@exemplo.com"));

            assertTrue(cut.isValidToInclude(p).contains(PersonDAO.ERRO_NOME));
        }

        @ParameterizedTest(name = "nome nulo ou vazio: \"{0}\"")
        @NullAndEmptySource
        void deveAcusarNomeNuloOuVazio(String nome) {
            Person p = new Person(1, nome, 25).addEmail(new Email("joao@exemplo.com"));

            assertTrue(cut.isValidToInclude(p).contains(PersonDAO.ERRO_NOME));
        }

        @ParameterizedTest(name = "nome valido: \"{0}\"")
        @ValueSource(strings = {
                "Joao Andrade",
                "Joao da Silva Andrade",
                "Joao Goncalves"
        })
        void deveAceitarNomeComDuasOuMaisPartes(String nome) {
            Person p = new Person(1, nome, 25).addEmail(new Email("joao@exemplo.com"));

            assertTrue(cut.isValidToInclude(p).isEmpty());
        }
    }

    @Nested
    @DisplayName("Regra 2 - idade no intervalo [1, 200]")
    class ValidacaoDaIdade {

        @ParameterizedTest(name = "idade fora do intervalo: {0}")
        @ValueSource(ints = {-1, 0, 201, 1000})
        void deveAcusarIdadeForaDoIntervalo(int idade) {
            Person p = new Person(1, "Joao Andrade", idade)
                    .addEmail(new Email("joao@exemplo.com"));

            assertTrue(cut.isValidToInclude(p).contains(PersonDAO.ERRO_IDADE));
        }

        @ParameterizedTest(name = "idade valida (fronteira): {0}")
        @ValueSource(ints = {1, 2, 199, 200})
        void deveAceitarIdadeNasFronteirasDoIntervalo(int idade) {
            Person p = new Person(1, "Joao Andrade", idade)
                    .addEmail(new Email("joao@exemplo.com"));

            assertTrue(cut.isValidToInclude(p).isEmpty());
        }
    }

    @Nested
    @DisplayName("Regra 3 - ao menos um Email associado")
    class ValidacaoDaAssociacao {

        @Test
        @DisplayName("Person sem nenhum Email e invalido")
        void deveAcusarPersonSemEmail() {
            Person p = new Person(1, "Joao Andrade", 25);

            assertTrue(cut.isValidToInclude(p).contains(PersonDAO.ERRO_SEM_EMAIL));
        }

        @Test
        @DisplayName("Person com varios Emails validos e aceito")
        void deveAceitarPersonComVariosEmails() {
            Person p = new Person(1, "Joao Andrade", 25)
                    .addEmail(new Email(1, "joao@exemplo.com"))
                    .addEmail(new Email(2, "contato@fatec.sp.gov"));

            assertTrue(cut.isValidToInclude(p).isEmpty());
        }
    }

    @Nested
    @DisplayName("Regra 4 - formato do Email: ___@___.___")
    class ValidacaoDoFormatoDoEmail {

        @ParameterizedTest(name = "email invalido: \"{0}\"")
        @ValueSource(strings = {
                "joaoexemplo.com",   // sem @
                "joao@exemplocom",   // sem ponto
                "@exemplo.com",      // parte local vazia
                "joao@.com",         // dominio vazio
                "joao@exemplo.",     // extensao vazia
                "joao@@exemplo.com", // dois @
                "joao @exemplo.com"  // espaco
        })
        void deveAcusarEmailForaDoFormato(String email) {
            Person p = new Person(1, "Joao Andrade", 25).addEmail(new Email(email));

            assertTrue(cut.isValidToInclude(p).contains(PersonDAO.ERRO_EMAIL_INVALIDO + email));
        }

        @Test
        @DisplayName("Email com nome nulo e invalido")
        void deveAcusarEmailComNomeNulo() {
            Person p = new Person(1, "Joao Andrade", 25).addEmail(new Email(null));

            assertTrue(cut.isValidToInclude(p).contains(PersonDAO.ERRO_EMAIL_INVALIDO + null));
        }

        @Test
        @DisplayName("Acusa apenas o email invalido quando ha um valido junto")
        void deveAcusarSomenteOEmailInvalido() {
            Person p = new Person(1, "Joao Andrade", 25)
                    .addEmail(new Email("joao@exemplo.com"))
                    .addEmail(new Email("invalido"));

            List<String> erros = cut.isValidToInclude(p);

            assertEquals(1, erros.size());
            assertTrue(erros.contains(PersonDAO.ERRO_EMAIL_INVALIDO + "invalido"));
        }
    }

    @Nested
    @DisplayName("Acumulo de erros")
    class MultiplosErros {

        @Test
        @DisplayName("Person invalido em todas as regras retorna os 3 erros")
        void deveAcumularTodosOsErros() {
            Person p = new Person(1, "Joao1", 0);

            List<String> erros = cut.isValidToInclude(p);

            assertEquals(3, erros.size());
            assertTrue(erros.contains(PersonDAO.ERRO_NOME));
            assertTrue(erros.contains(PersonDAO.ERRO_IDADE));
            assertTrue(erros.contains(PersonDAO.ERRO_SEM_EMAIL));
        }
    }

    @Nested
    @DisplayName("save() usa a validacao como guarda")
    class Persistencia {

        @Test
        @DisplayName("Person valido e persistido")
        void devePersistirPersonValido() {
            cut.save(pessoaValida());

            assertEquals(1, cut.count());
        }

        @Test
        @DisplayName("Person invalido e rejeitado e nada e persistido")
        void deveRejeitarPersonInvalido() {
            Person invalido = new Person(1, "Joao1", 0);

            assertThrows(IllegalArgumentException.class, () -> cut.save(invalido));
            assertEquals(0, cut.count());
        }
    }

    @Nested
    @DisplayName("Modelo Person 1 --- 1..* Email")
    class Modelo {

        @Test
        @DisplayName("Person e Email carregam os dados informados")
        void deveCarregarOsDadosInformados() {
            Email email = new Email(7, "joao@exemplo.com");
            Person p = new Person(3, "Joao Andrade", 25).addEmail(email);

            assertEquals(3, p.getId());
            assertEquals("Joao Andrade", p.getName());
            assertEquals(25, p.getAge());
            assertEquals(1, p.getEmails().size());
            assertEquals(7, p.getEmails().get(0).getId());
            assertEquals("joao@exemplo.com", p.getEmails().get(0).getName());
        }

        @Test
        @DisplayName("Construtores sem id usam zero como padrao")
        void deveUsarZeroComoIdPadrao() {
            assertEquals(0, new Person("Joao Andrade", 25).getId());
            assertEquals(0, new Email("joao@exemplo.com").getId());
        }

        @Test
        @DisplayName("A lista de emails exposta e imutavel")
        void deveExporListaImutavel() {
            Person p = new Person(1, "Joao Andrade", 25);

            assertThrows(UnsupportedOperationException.class,
                    () -> p.getEmails().add(new Email("joao@exemplo.com")));
        }
    }
}
