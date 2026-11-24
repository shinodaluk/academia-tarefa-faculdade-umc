<%--
    Document   : form
    Created on : Sep 29, 2026
    Author     : Shino
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Cadastro de instrutor — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="css/modernist.css">
<link rel="stylesheet" href="css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>

<script type="application/json" id="dados-instrutor">${instrutorJson}</script>
<script type="application/json" id="dados-erros">{}</script>

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
    <div class="sidebar-foot">Validação no cliente é conveniência;<br>a do servlet é a que vale.</div>
  </aside>

  <main class="conteudo">
    <header class="pagina-head">
      <div>
        <div class="kicker">Instrutores</div>
        <h1>{{ modelo.id ? 'Editar instrutor' : 'Novo instrutor' }}</h1>
      </div>
    </header>

    <div class="aviso" v-if="aviso">
      <span>{{ aviso }}</span>
      <button type="button" @click="aviso = ''">Fechar</button>
    </div>

    <!-- POST síncrono: o servlet grava e redireciona para a edição -->
    <form class="painel-form" method="post" action="instrutor?acao=salvar" novalidate
          @submit="enviarFormulario($event, validarFormulario())">
      <input type="hidden" name="id" :value="modelo.id || ''">

      <div class="grade-form">
        <div class="field col-2">
          <label for="nome">Nome completo</label>
          <input id="nome" name="nome" class="input" :class="{ invalido: erros.nome }"
                 v-model.trim="modelo.nome" maxlength="120">
          <span class="erro" v-if="erros.nome">{{ erros.nome }}</span>
        </div>

        <div class="field">
          <label for="cref">CREF</label>
          <input id="cref" name="cref" class="input" :class="{ invalido: erros.cref }" v-model="modelo.cref"
                 placeholder="000000-G/SP" @input="modelo.cref = mascaraCREF(modelo.cref)">
          <span class="erro" v-if="erros.cref">{{ erros.cref }}</span>
        </div>

        <div class="field">
          <label for="especialidade">Especialidade</label>
          <select id="especialidade" name="especialidade" class="input" v-model="modelo.especialidade">
            <option v-for="e in especialidades" :key="e" :value="e">{{ e }}</option>
          </select>
        </div>

        <div class="field">
          <label for="telefone">Telefone</label>
          <input id="telefone" name="telefone" class="input" :class="{ invalido: erros.telefone }"
                 v-model="modelo.telefone" placeholder="(00) 00000-0000" inputmode="tel"
                 @input="modelo.telefone = mascaraTelefone(modelo.telefone)">
          <span class="erro" v-if="erros.telefone">{{ erros.telefone }}</span>
        </div>

        <div class="field">
          <label for="email">E-mail</label>
          <input id="email" name="email" type="email" class="input" :class="{ invalido: erros.email }"
                 v-model.trim="modelo.email">
          <span class="erro" v-if="erros.email">{{ erros.email }}</span>
        </div>

        <div class="field col-2">
          <label for="status">Status</label>
          <select id="status" name="status" class="input" style="max-width:260px" v-model="modelo.status">
            <option value="Ativo">Ativo</option>
            <option value="Inativo">Inativo</option>
          </select>
        </div>
      </div>

      <div class="hr" style="margin:var(--space-8) 0; max-width:820px"></div>

      <div class="acoes-form">
        <button type="submit" class="btn btn-primary" :disabled="enviando">
          {{ enviando ? 'Salvando…' : 'Salvar instrutor' }}
        </button>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/instrutor">Cancelar</a>
      </div>
    </form>
  </main>
</div>

<script src="js/comum.js"></script>
<script>
const A = window.Academia;

Vue.createApp({
  mixins: [A.comum],
  data() {
    return {
      aviso: "${aviso}",
      especialidades: ['Musculação', 'Funcional', 'Condicionamento', 'Pilates'],
      modelo: A.dados('dados-instrutor', {}),
      erros: A.dados('dados-erros', {})   // erros vindos do servlet
    };
  },
  methods: {
    validarFormulario() {
      this.erros = A.validar(this.modelo, {
        nome: [[A.obrigatorio, 'Informe o nome completo.'],
               [v => String(v).trim().split(/\s+/).length >= 2, 'Informe nome e sobrenome.']],
        cref: [[A.obrigatorio, 'Informe o CREF.'],
               [v => /^\d{6}-[A-Z]{1,2}\/[A-Z]{2}$/.test(String(v).trim()), 'Formato esperado: 000000-G/UF.']],
        telefone: [[A.obrigatorio, 'Informe o telefone.'], [A.telefoneValido, 'Telefone incompleto.']],
        email: [[A.obrigatorio, 'Informe o e-mail.'], [A.emailValido, 'E-mail inválido.']]
      });
      return Object.keys(this.erros).length === 0;
    }
  }
}).mount('#app');
</script>
</body>
</html>
