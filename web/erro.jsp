<%-- 
    Document   : erro
    Created on : Sep 28, 2026, 9:41:45 PM
    Author     : Shino
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="robots" content="noindex">
<title>Erro no sistema — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/modernist.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>

<script type="application/json" id="dados-erro">
    {"status":${requestScope['javax.servlet.error.status_code']}, "message": ${requestScope['javax.servlet.error.message']}}
</script>

<div id="app" class="layout" v-cloak>
  <aside class="sidebar">
    <div class="sidebar-brand"><strong>Academia Vértice</strong><div class="kicker">Gestão</div></div>
    <nav class="sidebar-nav">
      <a href="${pageContext.request.contextPath}/aluno">Alunos</a>
      <a href="${pageContext.request.contextPath}/plano">Planos</a>
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
        <div class="kicker">Erro {{ erro.status || 500 }} · {{ rotuloStatus }}</div>
        <h1>Erro no sistema</h1>
      </div>
    </header>

    <section class="bloco-erro">
      <div class="codigo-erro">{{ erro.status || 500 }}</div>
      <div class="texto-erro">
        <p class="lead-erro">{{ erro.message || "Ocorreu um erro inesperado. Tente novamente em instantes."}} </p>
        <div class="acoes-form">
          <a class="btn btn-secondary" href="${pageContext.request.contextPath}">Ir para home</a>
        </div>
      </div>
    </section>
  </main>
</div>

<script src="${pageContext.request.contextPath}/js/comum.js"></script>
<script>
const A = window.Academia;
const ROTULOS = { 400: 'Requisição inválida', 403: 'Acesso negado', 405: 'Método não permitido', 409: 'Conflito', 500: 'Erro interno', 503: 'Serviço indisponível' };

Vue.createApp({
  mixins: [A.comum],
  data() {
    return {
      erro: A.dados('dados-erro', {}),
      podeVoltar: history.length > 1
    };
  },
  computed: {
    rotuloStatus() { return ROTULOS[this.erro.status] || 'Erro'; }
  },
  methods: {
    voltar() { history.back(); }
  }
}).mount('#app');
</script>
</body>
</html>
