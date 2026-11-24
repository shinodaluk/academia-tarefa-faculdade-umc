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
<title>Matrículas — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="css/modernist.css">
<link rel="stylesheet" href="css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>

<script type="application/json" id="dados-matriculas">${matriculasJson}</script>
<script type="application/json" id="dados-aviso">"${aviso}"</script>

<div id="app" class="layout" v-cloak>

  <aside class="sidebar">
    <div class="sidebar-brand"><strong>Academia Vértice</strong><div class="kicker">Gestão</div></div>
    <nav class="sidebar-nav">
      <a href="${pageContext.request.contextPath}/aluno">Alunos</a>
      <a href="${pageContext.request.contextPath}/plano">Planos</a>
      <a class="ativo" href="${pageContext.request.contextPath}/matricula">Matrículas</a>
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
        <h1>Matrículas</h1>
      </div>
      <!-- Filtro no servidor: GET síncrono para o servlet de listagem -->
      <form class="acoes-head" method="get" action="${pageContext.request.contextPath}/matricula">
        <input type="hidden" name="acao" value="listar" />
        <select class="input" style="width:200px" name="situacao" v-model="filtroSituacao">
          <option value="">Todas as situações</option>
          <option value="Ativa">Ativas</option>
          <option value="Suspensa">Suspensas</option>
          <option value="Cancelada">Canceladas</option>
        </select>
        <button type="submit" class="btn btn-secondary">Filtrar</button>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/matricula?acao=criar">Nova matrícula</a>
      </form>
    </header>

    <div class="aviso" v-if="aviso">
      <span>{{ aviso }}</span>
      <button type="button" @click="aviso = ''">Fechar</button>
    </div>

    <div class="painel">
      <table class="table" v-if="linhas.length">
        <thead>
          <tr><th>Aluno</th><th>Plano</th><th>Início</th><th>Próx. vencimento</th><th>Mensalidade</th><th>Situação</th><th></th></tr>
        </thead>
        <tbody>
          <tr v-for="l in linhas" :key="l.id">
            <td style="font-weight:600">{{ l.aluno }}</td>
            <td>{{ l.plano }}</td>
            <td>{{ dataBR(l.dataInicio) }}</td>
            <td>{{ l.vencimento }}</td>
            <td style="font-weight:700">{{ moeda(l.valorFinal) }}</td>
            <td><span class="tag" :class="l.tagClass">{{ l.rotuloPagamento }}</span></td>
            <td>
              <div class="acoes-linha">
                <a class="btn btn-ghost" :href="'${pageContext.request.contextPath}/matricula?acao=editar&id=' + l.id">Editar</a>
                <!-- Exclusão: POST síncrono; o servlet redireciona de volta para a lista -->
                <form method="post" action="${pageContext.request.contextPath}/matricula?acao=excluir"
                      @submit="confirmarExclusao($event, 'a matrícula de ' + l.aluno)">
                  <input type="hidden" name="id" :value="l.id">
                  <button type="submit" class="btn btn-ghost">Excluir</button>
                </form>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p class="vazio" v-else>Nenhuma matrícula nesse filtro.</p>
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
      matriculas: A.dados('dados-matriculas'),
      aviso: A.dados('dados-aviso', ''),
      filtroSituacao: new URLSearchParams(location.search).get('situacao') || ''
    };
  },
  computed: {
    linhas() {
      return this.matriculas.map(m => {
        const venc = A.proximoVencimento(m.diaVencimento);
        const dias = Math.round((venc - new Date().setHours(0, 0, 0, 0)) / 86400000);
        let rotuloPagamento = 'Em dia', tagClass = 'tag-neutral';
        if (m.situacao !== 'Ativa') { rotuloPagamento = m.situacao; tagClass = 'tag-outline'; }
        else if (m.diasAtraso > 0) { rotuloPagamento = m.diasAtraso + ' dias em atraso'; tagClass = 'tag-accent'; }
        else if (dias <= 5) { rotuloPagamento = dias === 0 ? 'Vence hoje' : 'Vence em ' + dias + ' dias'; tagClass = 'tag-outline'; }
        return Object.assign({}, m, { vencimento: venc.toLocaleDateString('pt-BR'), rotuloPagamento, tagClass });
      });
    }
  }
}).mount('#app');
</script>
</body>
</html>
