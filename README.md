# Sistema de Academia

Aplicação web para gestão de uma academia: cadastro de alunos, planos, instrutores,
matrículas e fichas de treino, com cálculo de serviços adicionais da matrícula.

Projeto acadêmico desenvolvido em Java (Servlet + JSP), sem framework de aplicação.

## Funcionalidades

- **Alunos** — cadastro, edição, exclusão, busca e filtro por status, indicadores de alunos ativos.
- **Planos** — cadastro com duração e valor mensal, ativação/desativação (plano com matrícula não pode ser excluído).
- **Instrutores** — cadastro com CREF, especialidade e status.
- **Matrículas** — vínculo aluno × plano, fidelidade, desconto estudante e dia de vencimento.
- **Fichas de treino** — ficha por aluno/instrutor com lista de exercícios (séries, repetições, carga).
- **Adicionais da matrícula** — soma de serviços extras (Bioimpedância, Cadeira de massagem, Pré-treino) ao valor do plano.

## Tecnologias

| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 24 |
| Web | Servlet / JSP (`javax.servlet`) |
| Servidor | Apache Tomcat 9 |
| Banco de dados | MySQL 8 (via Docker) |
| JSON | Gson 2.10.1 |
| Utilitários | Apache Commons Lang3 3.20.0, Commons Text 1.15.0 |
| Front-end | Vue 3 via CDN (sem build) |
| IDE / build | NetBeans + Ant |
| Testes | JUnit 5.11.4 (Jupiter) |

## Estrutura

```
src/java/
├── controller/   Servlets por recurso (AlunoController, PlanoController, ...)
├── factory/      Ações por recurso (Command): CriarAction, EditarAction, SalvarAction, ...
├── dao/          Acesso ao banco com JDBC e PreparedStatement
├── model/        Entidades (com Builder) e decorators de serviços adicionais
├── util/         Conexão com o banco e filtro de encoding
└── docs/
    ├── database/          Scripts SQL
    ├── docker-compose.yml MySQL para desenvolvimento
    ├── htmls/             Protótipos das telas
    └── jar/               Bibliotecas usadas pelo projeto
test/             Testes unitários (JUnit 5)
lib/              JUnit Console Standalone
web/              Páginas JSP, CSS e JS
```

## Padrões de projeto

### Decorator (implementado sem framework)

Usado para compor o valor mensal da matrícula com serviços adicionais.

| Papel | Classe |
| --- | --- |
| Componente | `model.Servico` (`getDescricao()`, `custo()`) |
| Componente concreto | `model.Matricula` |
| Decorator abstrato | `model.AdicionaisMatricula` |
| Decorators concretos | `model.Bioimpedancia`, `model.CadeiraMassagem`, `model.PreTreino` |

```java
Servico servico = matricula;
servico = new Bioimpedancia(servico, bioimpedancia);
servico = new PreTreino(servico, preTreino);

servico.custo();        // valor do plano + bioimpedância + pré-treino
servico.getDescricao(); // "Mensal, Bioimpedância, Pré-treino"
```

Cada decorator recebe o seu `ServicoAdicional` (código, descrição e valor) pelo construtor. Em produção
esses dados vêm da tabela `servico_adicional`; nos testes, são criados em memória. O decorator não acessa
o banco e rejeita um adicional de código diferente do seu.

### Outros padrões

- **Builder** — criação das entidades (`Plano.getBuilder().comNome(...).constroi()`).
- **Command** — cada ação implementa `factory.ICommand` e é escolhida pelo parâmetro `acao`.
- **DAO** — uma classe de acesso a dados por entidade, isolando o SQL.

## Como executar

### Pré-requisitos

- JDK 24
- Apache Tomcat 9
- Docker e Docker Compose
- NetBeans (recomendado)

### 1. Subir o banco de dados

```bash
cd src/java/docs
docker compose up -d
```

O MySQL fica disponível em `localhost:3307` (usuário `root`, senha `sistema_root_dev` — apenas para desenvolvimento).

### 2. Criar o esquema

Execute os scripts na ordem:

```bash
docker exec -i academia-mysql mysql -uroot -psistema_root_dev < src/java/docs/database/01_esquema.sql
docker exec -i academia-mysql mysql -uroot -psistema_root_dev < src/java/docs/database/03_procedimentos_e_dados.sql
docker exec -i academia-mysql mysql -uroot -psistema_root_dev < src/java/docs/database/04_servicos_adicionais.sql
```

`02_consultas_parametrizadas.sql` é referência das consultas usadas pelos DAOs, não precisa ser executado.

### 3. Rodar a aplicação

1. Abra o projeto no NetBeans.
2. Em **Properties → Libraries**, aponte as bibliotecas para os `.jar` de `src/java/docs/jar/`.
3. Configure o Tomcat 9 como servidor do projeto.
4. Execute (**Run**). A aplicação abre em `http://localhost:8080/WebApplication1`.

A conexão com o banco é configurada em `src/java/util/DBConnector.java`.

## Testes

Testes unitários em `test/`, com JUnit 5. Não dependem de banco nem de servidor.

| Classe | O que cobre |
| --- | --- |
| `model.AdicionaisMatriculaTest` | Cada decorator soma seu valor e sua descrição, encadeamento de vários decorators, ordem da descrição, soma sem erro de ponto flutuante, base intacta e validação dos argumentos |
| `model.MatriculaTest` | `Matricula` como componente concreto do decorator e erro quando o plano não foi carregado |

Para rodar pelo terminal (PowerShell, precisa só do JDK):

```powershell
.	estes.ps1
```

O script compila o pacote `model` e os testes em `build/testes` e roda a suíte com o JUnit Console.
No NetBeans, os testes ficam em **Test Packages** e o JUnit já está no classpath de testes do projeto.

## Rotas

Cada recurso tem um servlet; a ação é definida pelo parâmetro `acao`.

| Recurso | URL | Ações |
| --- | --- | --- |
| Alunos | `/aluno` | `listar`, `criar`, `editar`, `salvar`, `excluir` |
| Planos | `/plano` | `listar`, `criar`, `editar`, `salvar`, `excluir` |
| Instrutores | `/instrutor` | `listar`, `criar`, `editar`, `salvar`, `excluir` |
| Matrículas | `/matricula` | `listar`, `criar`, `editar`, `salvar`, `excluir`, `adicionais`, `calcularAdicionais` |
| Fichas | `/ficha` | `listar`, `criar`, `editar`, `salvar`, `excluir` |

### Cálculo de adicionais (JSON)

`POST /matricula?acao=calcularAdicionais`

```json
{ "id": 1, "adicionais": ["BIOIMPEDANCIA", "PRE_TREINO"] }
```

Resposta `200 OK` (matrícula no plano Mensal, sem desconto):

```json
{
  "matriculaId": 1,
  "descricaoBase": "Mensal",
  "custoBase": 129.90,
  "adicionais": [
    { "codigo": "BIOIMPEDANCIA", "descricao": "Bioimpedância", "valorMensal": 20.00 },
    { "codigo": "PRE_TREINO", "descricao": "Pré-treino", "valorMensal": 12.30 }
  ],
  "descricao": "Mensal, Bioimpedância, Pré-treino",
  "totalMensal": 162.20
}
```

Erros retornam `{ "error": "mensagem" }` com status `400` (entrada inválida), `404` (matrícula
não encontrada), `405` (método diferente de POST) ou `500`.
