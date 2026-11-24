# Export web — Sistema Academia (Servlet + JSP, Vue 3 via CDN, sem build)

Dez páginas independentes. **Não é SPA**: o servlet renderiza o JSON dentro da
página, a navegação é por link e a gravação é por `POST` síncrono. Nenhuma
chamada `fetch`/AJAX — o Vue cuida só de máscaras, validação, cálculos e
comportamento de tela (linhas dinâmicas, filtro instantâneo, confirmações).

## Arquivos

```
export-web/
├── alunos.html          matriculas.html        instrutores.html    fichas.html
├── aluno-form.html      matricula-form.html    instrutor-form.html ficha-form.html
├── planos.html          plano-form.html
└── assets/
    ├── css/modernist.css   design system (tokens + componentes)
    ├── css/app.css         layout das páginas
    └── js/comum.js         máscaras, validações, cálculo e mixin Vue (sem HTTP)
```

## Como o servlet entrega os dados

Cada página lê blocos JSON inertes pelo `id`. Em JSP:

```jsp
<script type="application/json" id="dados-alunos">${alunosJson}</script>
<script type="application/json" id="dados-indicadores">${indicadoresJson}</script>
<script type="application/json" id="dados-aviso">"${aviso}"</script>
```

No Java, serialize com Gson/Jackson e exponha como atributo do request antes do
`forward`. Escape `</` como `<\/` na serialização para não fechar a tag `<script>`.

IDs esperados por página:

| Página | Blocos JSON |
| --- | --- |
| alunos | `dados-alunos`, `dados-indicadores`, `dados-aviso` |
| aluno-form | `dados-aluno`, `dados-erros` |
| planos | `dados-planos`, `dados-aviso` |
| plano-form | `dados-plano`, `dados-erros` |
| matriculas | `dados-matriculas`, `dados-aviso` |
| matricula-form | `dados-matricula`, `dados-alunos`, `dados-planos`, `dados-erros` |
| instrutores | `dados-instrutores`, `dados-aviso` |
| instrutor-form | `dados-instrutor`, `dados-erros` |
| fichas | `dados-fichas`, `dados-aviso` |
| ficha-form | `dados-ficha`, `dados-alunos`, `dados-instrutores`, `dados-erros` |

`dados-erros` recebe o mapa `{ "campo": "mensagem" }` produzido pela validação do
servlet — as mensagens aparecem sob os campos ao reexibir o formulário.
`dados-aviso` é a mensagem do padrão POST-Redirect-GET (ex.: “Aluno excluído.”).

## Mapeamentos esperados

| Método | Caminho | Ação |
| --- | --- | --- |
| GET | `/alunos` (`?busca=`) | lista, aceita filtro |
| GET | `/alunos/form` (`?id=`) | formulário de inclusão/edição |
| POST | `/alunos/salvar` | grava e redireciona para a lista |
| POST | `/alunos/excluir` | exclui (campo `id`) e redireciona |

O mesmo quarteto para `planos`, `instrutores`, `matriculas` e `fichas`.
Os filtros de cada lista são `GET` com os campos `busca`, `somenteAtivos`,
`situacao`, `especialidade`, `objetivo`.

Nos arquivos exportados os `action`/`href` apontam para caminhos relativos
(`alunos.html`, `alunos/salvar`) para que a pasta abra direto no navegador.
Ao portar para JSP, troque por `${pageContext.request.contextPath}/alunos` etc.,
e mantenha o `window.CONTEXTO` do topo de cada página.

## Campos enviados no POST

Nomes iguais aos das colunas em camelCase: `id`, `nome`, `cpf`, `dataNascimento`,
`telefone`, `email`, `status`, `duracaoMeses`, `valorMensal`, `descricao`, `ativo`,
`cref`, `especialidade`, `alunoId`, `planoId`, `dataInicio`, `fidelidadeMeses`,
`descontoEstudante`, `diaVencimento`, `situacao`, `objetivo`, `instrutorId`,
`dataCriacao`.

Os exercícios da ficha vão indexados para binding direto:
`exercicios[0].nome`, `exercicios[0].series`, `exercicios[0].repeticoes`,
`exercicios[0].cargaKg`, `exercicios[0].ordem`, …

Checkboxes (`ativo`, `descontoEstudante`) só chegam quando marcados — trate a
ausência como `false`.

## Validações no cliente (repita-as no servlet)

- **Aluno** — nome com sobrenome, CPF com dígito verificador, nascimento não futuro e idade ≥ 12, telefone com 10–11 dígitos, e-mail.
- **Plano** — nome ≥ 3 caracteres, duração inteira de 1 a 36 meses, valor > 0 e ≤ 9.999,99, descrição obrigatória.
- **Instrutor** — CREF no formato `000000-G/UF`, telefone, e-mail.
- **Matrícula** — aluno ativo, plano obrigatório, fidelidade em {0, 6, 12} e nunca maior que a duração do plano, dia de vencimento entre 1 e 28.
- **Ficha** — aluno e instrutor obrigatórios, mínimo de um exercício, nome e repetições preenchidos, séries de 1 a 10.

Exclusões bloqueadas na tela: plano com matrícula vinculada (impede o POST) e
instrutor com fichas (pede confirmação extra). Revalide as duas no servlet.

Regra de preço, idêntica à view `vw_matricula_valor`:
fidelidade 6 meses = 5%, 12 meses = 10%, estudante = 15% — descontos somados
sobre o valor base. As consultas estão em `sql/02_consultas_parametrizadas.sql`
(sempre com `PreparedStatement`).
