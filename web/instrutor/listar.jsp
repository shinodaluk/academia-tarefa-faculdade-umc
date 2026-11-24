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
<title>Instrutores — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="css/modernist.css">
<link rel="stylesheet" href="css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>

<script type="application/json" id="dados-instrutores">${instrutoresJson}</script>
<script type="application/json" id="dados-aviso">"${aviso}"</script>

<div id="app" class="layout" v-cloak>

  <aside class="sidebar">
    <div class="sidebar-brand"><strong>Academia Vértice</strong><div class="kicker">Gestão</div></div>
    <nav class="sidebar-nav">
      <a href="${pageContext.request.contextPath}/aluno">Alunos</a>
      <a href="${pageContext.request.contextPath}/plano">Planos</a>
      <a href="${pageContext.request.contextPath}/matricula">Matrículas</a>
      <a href="${pageContext.request.contextPath}/matricula?acao=adicionais">Adicionais Matrícula</a>
      <a class="ativo" href="${pageContext.request.contextPath}/instrutor">Instrutores</a>
      <a href="${pageContext.request.contextPath}/ficha">Fichas de treino</a>
    </nav>
    <div class="sidebar-foot">Servlet + JSP<br>Vue 3 só na camada de tela.</div>
  </aside>

  <main class="conteudo">
    <header class="pagina-head">
      <div>
        <div class="kicker">Cadastro</div>
        <h1>Instrutores</h1>
      </div>
      <!-- Filtro no servidor: GET síncrono para o servlet de listagem -->
      <form class="acoes-head" method="get" action="${pageContext.request.contextPath}/instrutor">
        <input type="hidden" name="acao" value="listar" />
        <select class="input" style="width:200px" name="especialidade" v-model="filtroEspecialidade">
          <option value="">Todas as especialidades</option>
          <option v-for="e in especialidades" :key="e" :value="e">{{ e }}</option>
        </select>
        <button type="submit" class="btn btn-secondary">Filtrar</button>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/instrutor?acao=criar">Novo instrutor</a>
      </form>
    </header>

    <div class="aviso" v-if="aviso">
      <span>{{ aviso }}</span>
      <button type="button" @click="aviso = ''">Fechar</button>
    </div>

    <div class="painel">
      <table class="table" v-if="instrutores.length">
        <thead>
          <tr><th>Nome</th><th>CREF</th><th>Especialidade</th><th>Telefone</th><th>Fichas</th><th>Status</th><th></th></tr>
        </thead>
        <tbody>
          <tr v-for="i in instrutores" :key="i.id">
            <td style="font-weight:600">{{ i.nome }}</td>
            <td>{{ i.cref }}</td>
            <td>{{ i.especialidade }}</td>
            <td>{{ i.telefone }}</td>
            <td>{{ i.fichasVinculadas }} fichas</td>
            <td><span class="tag" :class="i.status === 'Ativo' ? 'tag-neutral' : 'tag-outline'">{{ i.status }}</span></td>
            <td>
              <div class="acoes-linha">
                <a class="btn btn-ghost" :href="'${pageContext.request.contextPath}/instrutor?acao=editar&id=' + i.id">Editar</a>
                <!-- Exclusão: POST síncrono; o servlet redireciona de volta para a lista -->
                <form method="post" action="${pageContext.request.contextPath}/instrutor?acao=excluir" @submit="validarExclusao($event, i)">
                  <input type="hidden" name="id" :value="i.id">
                  <button type="submit" class="btn btn-ghost">Excluir</button>
                </form>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p class="vazio" v-else>Nenhum instrutor nesse filtro.</p>
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
      instrutores: A.dados('dados-instrutores'),
      aviso: A.dados('dados-aviso', ''),
      especialidades: ['Musculação', 'Funcional', 'Condicionamento', 'Pilates'],
      filtroEspecialidade: new URLSearchParams(location.search).get('especialidade') || ''
    };
  },
  methods: {
    validarExclusao(evento, instrutor) {
      if (instrutor.fichasVinculadas > 0) {
        if (!confirm('O instrutor ' + instrutor.nome + ' tem ' + instrutor.fichasVinculadas +
                     ' ficha(s) vinculada(s); elas ficarão sem responsável. Continuar?')) {
          evento.preventDefault();
        }
        return;
      }
      this.confirmarExclusao(evento, 'o instrutor ' + instrutor.nome);
    }
  }
}).mount('#app');
</script>
</body>
</html>
