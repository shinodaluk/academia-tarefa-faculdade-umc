<%--
    Document   : listar
    Created on : Sep 29, 2026
    Author     : Shino
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Planos — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="css/modernist.css">
<link rel="stylesheet" href="css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>

<script type="application/json" id="dados-planos">${planosJson}</script>
<script type="application/json" id="dados-aviso">"${aviso}"</script>

<div id="app" class="layout" v-cloak>

  <aside class="sidebar">
    <div class="sidebar-brand"><strong>Academia Vértice</strong><div class="kicker">Gestão</div></div>
    <nav class="sidebar-nav">
      <a href="${pageContext.request.contextPath}/aluno">Alunos</a>
      <a class="ativo" href="${pageContext.request.contextPath}/plano">Planos</a>
      <a href="${pageContext.request.contextPath}/matricula">Matrículas</a>
      <a href="${pageContext.request.contextPath}/matricula?acao=adicionais">Adicionais Matrícula</a>
      <a href="${pageContext.request.contextPath}/instrutor">Instrutores</a>
      <a href="${pageContext.request.contextPath}/ficha">Fichas de treino</a>
    </nav>
    <div class="sidebar-foot">Servlet + JSP<br>Vue 3 só na camada de tela.</div>
  </aside>

  <main class="conteudo">
    <header class="pagina-head">
      <div>
        <div class="kicker">Cadastro</div>
        <h1>Planos</h1>
      </div>
      <form class="acoes-head" method="get" action="${pageContext.request.contextPath}/plano">
        <input type="hidden" name="acao" value="listar" />
        <label class="tag tag-outline" style="cursor:pointer; display:flex; gap:8px; align-items:center">
          <input type="checkbox" name="somenteAtivos" value="1" v-model="somenteAtivos"
                 style="accent-color:var(--color-accent)"> Somente ativos
        </label>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/plano?acao=criar">Novo plano</a>
      </form>
    </header>

    <div class="aviso" v-if="aviso">
      <span>{{ aviso }}</span>
      <button type="button" @click="aviso = ''">Fechar</button>
    </div>

    <div class="painel">
      <table class="table" v-if="listados.length">
        <thead>
          <tr><th>Plano</th><th>Duração</th><th>Valor mensal</th><th>Total do período</th><th>Matrículas</th><th>Descrição</th><th></th></tr>
        </thead>
        <tbody>
          <tr v-for="p in listados" :key="p.id">
            <td style="font-weight:600">{{ p.nome }}</td>
            <td>{{ p.duracaoMeses }} {{ p.duracaoMeses > 1 ? 'meses' : 'mês' }}</td>
            <td style="font-weight:700">{{ moeda(p.valorMensal) }}</td>
            <td>{{ moeda(p.valorMensal * p.duracaoMeses) }}</td>
            <td>{{ p.matriculasVinculadas }}</td>
            <td style="color:var(--color-neutral-700)">{{ p.descricao }}</td>
            <td>
              <div class="acoes-linha">
                <a class="btn btn-ghost" :href="'${pageContext.request.contextPath}/plano?acao=editar&id=' + p.id">Editar</a>
                <!-- Exclusão: POST síncrono; o servlet redireciona de volta para a lista -->
                <form method="post" action="${pageContext.request.contextPath}/plano?acao=excluir" @submit="validarExclusao($event, p)">
                  <input type="hidden" name="id" :value="p.id">
                  <button type="submit" class="btn btn-ghost">Excluir</button>
                </form>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p class="vazio" v-else>Nenhum plano cadastrado.</p>
    </div>
  </main>
</div>

<script src="js/comum.js"></script>
<script>
const A = window.Academia;

Vue.createApp({
  mixins: [A.comum],
  data() {
    return {
      planos: A.dados('dados-planos'),
      aviso: A.dados('dados-aviso', ''),
      somenteAtivos: new URLSearchParams(location.search).get('somenteAtivos') === '1'
    };
  },
  computed: {
    listados() { return this.somenteAtivos ? this.planos.filter(p => p.ativo !== false) : this.planos; }
  },
  methods: {
    // barra a exclusão antes do servlet quando há matrícula vinculada
    validarExclusao(evento, plano) {
      if (plano.matriculasVinculadas > 0) {
        evento.preventDefault();
        this.aviso = 'O plano "' + plano.nome + '" tem ' + plano.matriculasVinculadas +
                     ' matrícula(s) vinculada(s). Desative-o em vez de excluir.';
        return;
      }
      this.confirmarExclusao(evento, 'o plano ' + plano.nome);
    }
  }
}).mount('#app');
</script>
</body>
</html>
