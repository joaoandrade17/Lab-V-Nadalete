# Checkout com colaboradores simulados — JUnit 5 + Mockito

Atividade de 09/11

Implementação de `ServicoCheckout.finalizar` por TDD. Os quatro colaboradores
externos (`EstoqueGateway`, `PagamentoGateway`, `PedidoRepository`, `Notificador`)
são mocks do Mockito; o serviço, os records e os `BigDecimal` são reais.
O enunciado completo está em [Enunciado-alunos.md](Enunciado-alunos.md).

| | |
|---|---|
| Java | release 21 |
| Testes | JUnit Jupiter 5.13.4 + Mockito 5.18.0 (`pom.xml` original do professor, sem alterações) |
| Total | **24 testes, todos passando** (11 métodos; 15 entradas inválidas) |

## Como executar

Requer **JDK 21+** (com `javac`; só o JRE não basta) e Maven 3.8+.
Nesta pasta (`checkout-mockito/`):

```bash
mvn test
```

Saída esperada:

```
[INFO] Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Estrutura

| Arquivo | Papel |
|---|---|
| [ServicoCheckout.java](src/main/java/br/edu/checkout/ServicoCheckout.java) | Classe sob teste (única implementada) |
| [ServicoCheckoutTest.java](src/test/java/br/edu/checkout/ServicoCheckoutTest.java) | Suíte de testes |
| [docs/tdd/](docs/tdd) | Saída do `mvn test` gravada em cada passo dos ciclos |

## Mapa da suíte

| # | Família do enunciado | Teste |
|---|---|---|
| 1 | Entrada inválida (15 entradas) | `deveRejeitarSolicitacaoNulaSemConsultarColaboradores` + `deveRejeitarSolicitacaoInvalidaSemConsultarColaboradores` (14 casos parametrizados) |
| 2 | Quantidade mínima | `deveAceitarUmaUnidadeMantendoPrecoComoTotal` |
| 3 | Cálculo usual (3 × 19.90 = 59.70) | `deveConfirmarCompraAprovadaComTotalDeTresUnidades` |
| 4 | Precisão (3 × 0.005 = 0.015) | `deveCalcularTotalSemArredondarPrecoComTresCasas` |
| 5 | Sem estoque | `deveRecusarCompraSemEstoqueSemCobrarRegistrarNotificarNemLiberar` |
| 6 | Pagamento recusado | `deveLiberarReservaUmaVezQuandoPagamentoForRecusado` |
| 7 | Falha técnica no pagamento | `deveLiberarReservaEPropagarAMesmaFalhaTecnicaDePagamento` |
| 8 | Persistência correta (captor) | `devePersistirPedidoComOsSeisCamposCorretos` |
| 9 | Compra aprovada (resultado, notificação, ordem) | `deveConfirmarCompraAprovadaComTotalDeTresUnidades` + `deveReservarCobrarSalvarENotificarNessaOrdemSemLiberarEstoque` |
| 10 | Falha de notificação | `deveManterCompraQuandoNotificacaoFalhar` |

Recursos do Mockito e onde cada um cumpre um papel:

| Recurso | Onde | Para quê |
|---|---|---|
| `when(...).thenReturn(...)` | todos os cenários de fluxo | programar reserva, cobrança e número do pedido |
| `thenThrow(...)` | falha técnica | fazer `cobrar` lançar `FalhaPagamentoException` |
| `doThrow(...)` | falha de notificação | `enviarConfirmacao` é `void`, não aceita `when(...)` |
| `verify(...)` | aprovada, precisão | conferir os argumentos enviados ao notificador e ao pagamento |
| `never()` | sem estoque, aprovada, notificação | proibir `liberar` sem proibir `reservar` |
| `times(1)` | ordem, recusa, falhas | "exatamente uma vez" é regra de R3–R7 |
| `verifyNoInteractions(...)` | entradas inválidas, sem estoque, recusa, falha técnica | efeitos proibidos |
| `ArgumentCaptor` | persistência | inspecionar os seis campos do `PedidoConfirmado` |
| `InOrder` | aprovada, recusa, falha técnica | ordem dos efeitos externos |

Não há stub em `@BeforeEach`: cada teste configura só o que consome, e a classe
roda com `Strictness.STRICT_STUBS`, que reprova stub não utilizado.

## Registro dos ciclos de TDD

O desenvolvimento está no histórico de commits, um commit por passo
(`red`, `green`, `refactor`). A saída do `mvn test` de cada passo foi gravada
no momento da execução em [docs/tdd/](docs/tdd).

| Ciclo | Comportamento | Red | Green | Refactor |
|---|---|---|---|---|
| 1 | Solicitação nula | `59e3183` | `765ea79` | — |
| 2 | Demais entradas inválidas | `9728366` | `a6356d0` | `dfab2e8` |
| 3 | Compra aprovada | `38bc123` | `eb4c2ce` | `7c83f40` |
| 4 | Sem estoque | `7bd6be4` | `0445756` | — |
| 5 | Pagamento recusado | `1697069` | `88240c9` | — |
| 6 | Falha técnica na cobrança | `ee0051c` | `0efed1f` | `b6b1911` |
| 7 | Falha de notificação | `9126b2d` | `fd59370` | `aa8f5fc` |

Os quatro testes do commit `23ecf84` (quantidade mínima, precisão, captor e
ordem) **nasceram verdes**: o ciclo 3 já multiplicava com `BigDecimal` e seguia
a ordem pedida. Eles não são apresentados como ciclos Red–Green; a prova de que
detectam defeito está na seção de mutações.

Três ciclos em detalhe:

### Ciclo 2 — entradas inválidas

- **Teste:** `deveRejeitarSolicitacaoInvalidaSemConsultarColaboradores` (14 casos).
- **Falha observada** ([ciclo2-red.txt](docs/tdd/ciclo2-red.txt)): `Tests run: 15, Failures: 14` —
  `expected: <IllegalArgumentException> but was: <UnsupportedOperationException>`.
- **Mudança mínima:** cinco `if` em sequência dentro de `finalizar`, um por regra.
- **Refatoração:** os três `if` de texto eram idênticos (`== null || isBlank()`);
  viraram `exigirTexto(valor, campo)`, e o bloco inteiro virou `validar(solicitacao)`.
  15 testes verdes antes e depois.

### Ciclo 4 — sem estoque

- **Teste:** `deveRecusarCompraSemEstoqueSemCobrarRegistrarNotificarNemLiberar`.
- **Falha observada** ([ciclo4-red.txt](docs/tdd/ciclo4-red.txt)):
  `expected: <EstoqueInsuficienteException> but was: <NullPointerException>`.
  O serviço ignorava o retorno de `reservar`, seguia para a cobrança, e o mock
  não configurado devolvia `null`.
- **Mudança mínima:** `if (!estoqueGateway.reservar(...)) throw new EstoqueInsuficienteException();`
- **Refatoração:** nenhuma. A mudança foi um `if` em torno de uma chamada que já
  existia; não surgiu duplicação nem nome ruim.

### Ciclo 6 — falha técnica na cobrança

- **Teste:** `deveLiberarReservaEPropagarAMesmaFalhaTecnicaDePagamento`.
- **Falha observada** ([ciclo6-red.txt](docs/tdd/ciclo6-red.txt)):
  `VerificationInOrderFailure — Wanted but not invoked: estoqueGateway.liberar("SKU-1", 3)`.
  A exceção já se propagava sozinha; o que faltava era a compensação. Sem o
  `verify` de interação, este teste teria nascido verde com a reserva presa.
- **Mudança mínima:** `try/catch (FalhaPagamentoException)` em volta de `cobrar`,
  liberando a reserva e relançando a mesma instância.
- **Refatoração:** `liberar(sku, quantidade)` aparecia duas vezes (recusa e falha
  técnica). Extraídos `liberarReserva` e `cobrarOuLiberarReserva`; R4 e R5
  continuam em ramos separados, e `finalizar` voltou a ser uma sequência linear.

## Respostas

**O que foi mockado e por quê?**
As quatro interfaces: `EstoqueGateway`, `PagamentoGateway`, `PedidoRepository` e
`Notificador`. Elas representam sistemas externos (estoque, cobrança, banco,
e-mail): lentos, com efeitos reais e difíceis de forçar a falhar. Com mocks, o
teste decide a resposta de cada um (inclusive as falhas) e observa o que o
serviço pediu a eles. `ServicoCheckout`, os records e os `BigDecimal` são reais,
porque é o comportamento deles que está sendo avaliado.

**Qual teste verifica estado e qual verifica interação?**
*Estado:* `deveAceitarUmaUnidadeMantendoPrecoComoTotal` só faz asserção sobre o
valor devolvido (`resultado.total()`).
*Interação:* `deveReservarCobrarSalvarENotificarNessaOrdemSemLiberarEstoque` não
olha o retorno; verifica apenas quais chamadas ocorreram, em que ordem e quais
não ocorreram. Vários testes combinam os dois, como
`deveManterCompraQuandoNotificacaoFalhar` (resultado com `false` + ausência de
nova cobrança e de liberação).

**Que defeito seria revelado pelo captor?**
Um `PedidoConfirmado` montado errado: campos trocados (e-mail no lugar do
`clienteId`), preço unitário gravado no lugar do total, ou `transacaoId` que não
é o devolvido pelo pagamento. `verify(repositorio).salvar(any())` passaria em
todos esses casos, e o resultado devolvido também, porque o pedido salvo não
aparece nele. Na mutação M5 (`transacaoId` trocado por `clienteId`), o teste do
captor foi o único a falhar.

**Por que verificar a ordem aqui não é apenas verificar um detalhe de implementação?**
Porque a ordem muda o que acontece no mundo externo. Cobrar antes de reservar
pode tirar dinheiro de quem não vai receber o produto; notificar antes de salvar
envia confirmação de um pedido que ainda não tem número e pode nunca existir;
liberar antes de cobrar devolve ao estoque um item em disputa. São regras do
enunciado (R3, R4, R6), observáveis de fora do serviço. Detalhe de implementação
seria, por exemplo, a ordem das validações ou dos métodos privados — e isso a
suíte não verifica.

## Desafio opcional — mutações manuais

Cada alteração abaixo foi aplicada temporariamente em `ServicoCheckout`, a suíte
foi executada e a alteração foi desfeita. Nenhuma está na entrega.

| Mutação | Teste(s) que ficaram vermelhos |
|---|---|
| M1 Notifica antes de salvar | `deveReservarCobrarSalvarENotificarNessaOrdemSemLiberarEstoque` |
| M2 Outro número na notificação | 4 testes (aprovada, precisão, ordem, falha de notificação) |
| M3 Remove a liberação após recusa | `deveLiberarReservaUmaVezQuandoPagamentoForRecusado` |
| M4 Arredonda o total para 2 casas | `deveCalcularTotalSemArredondarPrecoComTresCasas` |
| M5 `transacaoId` trocado por `clienteId` no pedido | `devePersistirPedidoComOsSeisCamposCorretos` |
| M6 Relança outra instância da falha técnica | `deveLiberarReservaEPropagarAMesmaFalhaTecnicaDePagamento` |
| M7 Remove a liberação após falha técnica | `deveLiberarReservaEPropagarAMesmaFalhaTecnicaDePagamento` |
| M8 Reserva estoque antes de validar | `deveRejeitarSolicitacaoInvalidaSemConsultarColaboradores` |
| M9 Libera estoque quando a notificação falha | `deveManterCompraQuandoNotificacaoFalhar` |

As nove foram detectadas; não foi encontrada lacuna. M1, M4 e M5 são pegas por
um único teste cada — justamente três dos quatro que nasceram verdes.
