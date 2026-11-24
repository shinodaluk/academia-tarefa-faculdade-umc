<%-- 
    Document   : lista
    Created on : Sep 28, 2026, 8:46:53 PM
    Author     : Shino
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Alunos — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="css/modernist.css">
<link rel="stylesheet" href="css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>

<script type="application/json" id="dados-alunos">${alunosJson}</script>
 <script type="application/json" id="dados-indicadores">${indicadoresJson}</script>
<script type="application/json" id="dados-aviso">"${aviso}"</script>

<div id="app" class="layout" v-cloak>

  <aside class="sidebar">
    <div class="sidebar-brand"><strong>Academia Vértice</strong><div class="kicker">Gestão</div></div>
    <nav class="sidebar-nav">
      <a class="ativo" href="${pageContext.request.contextPath}/aluno">Alunos</a>
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
        <div class="kicker">Cadastro</div>
        <h1>Alunos</h1>
      </div>
      <!-- Busca no servidor: GET síncrono para o servlet de listagem -->
      <form class="acoes-head" method="get" action="${pageContext.request.contextPath}/aluno">
        <input type="hidden" name="acao" value="listar" />
        <input class="input" style="width:240px" name="busca" v-model="busca" placeholder="Buscar por nome ou CPF">
        <button type="submit" class="btn btn-secondary">Buscar</button>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/aluno?acao=criar">Novo aluno</a>
      </form>
    </header>

    <div class="aviso" v-if="aviso">
      <span>{{ aviso }}</span>
      <button type="button" @click="aviso = ''">Fechar</button>
    </div>

    <div class="faixa-indicadores">
      <div class="indicador">
        <div class="valor">{{ indicadores.alunosAtivos }}</div>
        <div class="rotulo">Alunos ativos</div>
      </div>
      <div class="indicador">
        <div class="valor">{{ indicadores.matriculas }}</div>
        <div class="rotulo">Matrículas</div>
      </div>
      <div class="indicador">
        <div class="valor" :class="{ alerta: indicadores.mensalidadesVencidas > 0 }">{{ indicadores.mensalidadesVencidas }}</div>
        <div class="rotulo">Mensalidades vencidas</div>
      </div>
      <div class="indicador">
        <div class="valor">{{ moeda(indicadores.receitaMensal) }}</div>
        <div class="rotulo">Receita mensal</div>
      </div>
    </div>

    <div class="painel">
      <table class="table" v-if="filtrados.length">
        <thead>
          <tr><th>Nome</th><th>CPF</th><th>Telefone</th><th>Plano atual</th><th>Status</th><th></th></tr>
        </thead>
        <tbody>
          <tr v-for="a in filtrados" :key="a.id">
            <td style="font-weight:600">{{ a.nome }}</td>
            <td>{{ a.cpf }}</td>
            <td>{{ a.telefone }}</td>
            <td>{{ a.planoAtual || '— sem matrícula' }}</td>
            <td><span class="tag" :class="a.status === 'Ativo' ? 'tag-neutral' : 'tag-outline'">{{ a.status }}</span></td>
            <td>
              <div class="acoes-linha">
                <a class="btn btn-ghost" :href="'${pageContext.request.contextPath}/aluno?acao=editar&id=' + a.id">Editar</a>
                <!-- Exclusão: POST síncrono; o servlet redireciona de volta para a lista -->
                <form method="post" action="${pageContext.request.contextPath}/aluno?acao=excluir" @submit="confirmarExclusao($event, 'o aluno ' + a.nome)">
                  <input type="hidden" name="id" :value="a.id">
                  <button type="submit" class="btn btn-ghost">Excluir</button>
                </form>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p class="vazio" v-else>Nenhum aluno encontrado para essa busca.</p>
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
      alunos: A.dados('dados-alunos'),
      indicadores: A.dados('dados-indicadores', {}),
      aviso: A.dados('dados-aviso', ''),
      // mantém o termo já aplicado pelo servidor na caixa de busca
      busca: new URLSearchParams(location.search).get('busca') || ''
    };
  },
  computed: {
    // Filtro instantâneo sobre as linhas já renderizadas; o Buscar continua
    // indo ao servidor para percorrer a base inteira.
    filtrados() {
      const q = this.busca.trim().toLowerCase();
      if (!q) return this.alunos;
      return this.alunos.filter(a =>
        (a.nome || '').toLowerCase().includes(q) || (a.cpf || '').includes(q));
    }
  }
}).mount('#app');
</script>
</body>
</html>
