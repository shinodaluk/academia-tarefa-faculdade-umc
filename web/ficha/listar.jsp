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
<title>Fichas de treino — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="css/modernist.css">
<link rel="stylesheet" href="css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>

<script type="application/json" id="dados-fichas">${fichasJson}</script>
<script type="application/json" id="dados-aviso">"${aviso}"</script>

<div id="app" class="layout" v-cloak>

  <aside class="sidebar">
    <div class="sidebar-brand"><strong>Academia Vértice</strong><div class="kicker">Gestão</div></div>
    <nav class="sidebar-nav">
      <a href="${pageContext.request.contextPath}/aluno">Alunos</a>
      <a href="${pageContext.request.contextPath}/plano">Planos</a>
      <a href="${pageContext.request.contextPath}/matricula">Matrículas</a>
      <a href="${pageContext.request.contextPath}/matricula?acao=adicionais">Adicionais Matrícula</a>
      <a href="${pageContext.request.contextPath}/instrutor">Instrutores</a>
      <a class="ativo" href="${pageContext.request.contextPath}/ficha">Fichas de treino</a>
    </nav>
    <div class="sidebar-foot">Servlet + JSP<br>Vue 3 só na camada de tela.</div>
  </aside>

  <main class="conteudo">
    <header class="pagina-head">
      <div>
        <div class="kicker">Cadastro</div>
        <h1>Fichas de treino</h1>
      </div>
      <!-- Filtro no servidor: GET síncrono para o servlet de listagem -->
      <form class="acoes-head" method="get" action="${pageContext.request.contextPath}/ficha">
        <input type="hidden" name="acao" value="listar" />
        <select class="input" style="width:200px" name="objetivo" v-model="filtroObjetivo">
          <option value="">Todos os objetivos</option>
          <option v-for="o in objetivos" :key="o" :value="o">{{ o }}</option>
        </select>
        <button type="submit" class="btn btn-secondary">Filtrar</button>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/ficha?acao=criar">Nova ficha</a>
      </form>
    </header>

    <div class="aviso" v-if="aviso">
      <span>{{ aviso }}</span>
      <button type="button" @click="aviso = ''">Fechar</button>
    </div>

    <div class="painel">
      <table class="table" v-if="fichas.length">
        <thead>
          <tr><th>Aluno</th><th>Objetivo</th><th>Instrutor</th><th>Exercícios</th><th>Criada em</th><th></th></tr>
        </thead>
        <tbody>
          <tr v-for="f in fichas" :key="f.id">
            <td style="font-weight:600">{{ f.aluno }}</td>
            <td><span class="tag tag-outline">{{ f.objetivo }}</span></td>
            <td>{{ f.instrutor || '— não vinculado' }}</td>
            <td>{{ f.qtdExercicios }} exercícios</td>
            <td>{{ dataBR(f.dataCriacao) }}</td>
            <td>
              <div class="acoes-linha">
                <a class="btn btn-ghost" :href="'${pageContext.request.contextPath}/ficha?acao=editar&id=' + f.id">Editar</a>
                <!-- Exclusão: POST síncrono; o servlet redireciona de volta para a lista -->
                <form method="post" action="${pageContext.request.contextPath}/ficha?acao=excluir"
                      @submit="confirmarExclusao($event, 'a ficha de ' + f.aluno)">
                  <input type="hidden" name="id" :value="f.id">
                  <button type="submit" class="btn btn-ghost">Excluir</button>
                </form>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p class="vazio" v-else>Nenhuma ficha nesse filtro.</p>
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
      fichas: A.dados('dados-fichas'),
      aviso: A.dados('dados-aviso', ''),
      objetivos: ['Hipertrofia', 'Emagrecimento', 'Condicionamento'],
      filtroObjetivo: new URLSearchParams(location.search).get('objetivo') || ''
    };
  }
}).mount('#app');
</script>
</body>
</html>
