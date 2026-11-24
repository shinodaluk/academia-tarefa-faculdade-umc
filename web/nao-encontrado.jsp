<%-- 
    Document   : nao-encontrado
    Created on : Sep 28, 2026, 9:41:41 PM
    Author     : Shino
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="robots" content="noindex">
<title>Página não encontrada — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/modernist.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>

<script type="application/json" id="dados-erro">
{"status":${pageContext.errorData.statusCode}, "message": ${pageContext.errorData.throwable.message}}
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
        <div class="kicker">Erro {{ erro.status || 404 }}</div>
        <h1>Página não encontrada</h1>
      </div>
    </header>

    <section class="bloco-erro">
      <div class="codigo-erro">{{ erro.status || 404 }}</div>
      <div class="texto-erro">
        <p class="lead-erro">{{ erro.status || "O endereço acessado não existe ou o registro foi removido."}}</p>
        <p class="nota-erro">Confira o link ou volte para uma das listas. Se o registro foi excluído, ele não aparece mais nas buscas.</p>
        <div class="acoes-form">
          <a class="btn btn-primary" href="${pageContext.request.contextPath}/aluno">Ir para alunos</a>
          <button type="button" class="btn btn-secondary" v-if="podeVoltar" @click="voltar">Voltar à página anterior</button>
        </div>
      </div>
    </section>

    <section class="atalhos-erro">
      <a v-for="a in atalhos" :key="a.href" :href="a.href">
        <span class="kicker">{{ a.kicker }}</span>
        <strong>{{ a.rotulo }}</strong>
      </a>
    </section>
  </main>
</div>

<script src="${pageContext.request.contextPath}/js/comum.js"></script>
<script>
const A = window.Academia;

Vue.createApp({
  mixins: [A.comum],
  data() {
    return {
      erro: A.dados('dados-erro', {}),
      podeVoltar: history.length > 1,
      atalhos: [
        { href: CONTEXTO + '/aluno', kicker: 'Cadastro', rotulo: 'Alunos' },
        { href: CONTEXTO + '/plano', kicker: 'Cadastro', rotulo: 'Planos' },
        { href: CONTEXTO + '/matricula', kicker: 'Operação', rotulo: 'Matrículas' },
        { href: CONTEXTO + '/ficha', kicker: 'Treino', rotulo: 'Fichas de treino' }
      ]
    };
  },
  methods: {
    voltar() { history.back(); }
  }
}).mount('#app');
</script>
</body>
</html>
