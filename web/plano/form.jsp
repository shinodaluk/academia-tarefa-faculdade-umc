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
<title>Cadastro de plano — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="css/modernist.css">
<link rel="stylesheet" href="css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>

<script type="application/json" id="dados-plano">${planoJson}</script>
<script type="application/json" id="dados-erros">{}</script>

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
    <div class="sidebar-foot">Validação no cliente é conveniência;<br>a do servlet é a que vale.</div>
  </aside>

  <main class="conteudo">
    <header class="pagina-head">
      <div>
        <div class="kicker">Planos</div>
        <h1>{{ modelo.id ? 'Editar plano' : 'Novo plano' }}</h1>
      </div>
    </header>

    <div class="aviso" v-if="aviso">
      <span>{{ aviso }}</span>
      <button type="button" @click="aviso = ''">Fechar</button>
    </div>

    <!-- POST síncrono: o servlet grava e redireciona para a edição -->
    <form class="painel-form" method="post" action="plano?acao=salvar" novalidate
          @submit="enviarFormulario($event, validarFormulario())">
      <input type="hidden" name="id" :value="modelo.id || ''">

      <div class="grade-form">
        <div class="field">
          <label for="nome">Nome do plano</label>
          <input id="nome" name="nome" class="input" :class="{ invalido: erros.nome }"
                 v-model.trim="modelo.nome" maxlength="60">
          <span class="erro" v-if="erros.nome">{{ erros.nome }}</span>
        </div>

        <div class="field">
          <label for="duracao">Duração (meses)</label>
          <input id="duracao" name="duracaoMeses" type="number" min="1" max="36" step="1" class="input"
                 :class="{ invalido: erros.duracaoMeses }" v-model.number="modelo.duracaoMeses">
          <span class="erro" v-if="erros.duracaoMeses">{{ erros.duracaoMeses }}</span>
        </div>

        <div class="field">
          <label for="valor">Valor mensal (R$)</label>
          <input id="valor" name="valorMensal" type="number" min="0" step="0.01" class="input"
                 :class="{ invalido: erros.valorMensal }" v-model.number="modelo.valorMensal">
          <span class="erro" v-if="erros.valorMensal">{{ erros.valorMensal }}</span>
        </div>

        <div class="field">
          <label for="descricao">Descrição</label>
          <input id="descricao" name="descricao" class="input" :class="{ invalido: erros.descricao }"
                 v-model.trim="modelo.descricao" maxlength="180">
          <span class="erro" v-if="erros.descricao">{{ erros.descricao }}</span>
        </div>

        <div class="field col-2">
          <label style="display:flex; align-items:center; gap:var(--space-3); cursor:pointer">
            <input type="checkbox" name="ativo" value="true" v-model="modelo.ativo"
                   style="width:18px; height:18px; accent-color:var(--color-accent)">
            Plano disponível para novas matrículas
          </label>
        </div>
      </div>

      <div class="hr" style="margin:var(--space-8) 0; max-width:820px"></div>

      <p style="font-size:13px; color:var(--color-neutral-700); max-width:820px; margin:0 0 var(--space-6)">
        Total do período: <strong>{{ moeda(totalPeriodo) }}</strong> —
        com fidelidade de 12 meses sai por <strong>{{ moeda(comFidelidade) }}</strong>/mês.
      </p>

      <div class="acoes-form">
        <button type="submit" class="btn btn-primary" :disabled="enviando">
          {{ enviando ? 'Salvando…' : 'Salvar plano' }}
        </button>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/plano">Cancelar</a>
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
      modelo: A.dados('dados-plano', {}),
      erros: A.dados('dados-erros', {})   // erros vindos do servlet
    };
  },
  computed: {
    totalPeriodo() { return (Number(this.modelo.valorMensal) || 0) * (Number(this.modelo.duracaoMeses) || 0); },
    comFidelidade() { return A.calcularMensalidade(this.modelo.valorMensal, 12, false).total; }
  },
  methods: {
    validarFormulario() {
      this.erros = A.validar(this.modelo, {
        nome: [[A.obrigatorio, 'Informe o nome do plano.'],
               [v => String(v).trim().length >= 3, 'Use ao menos 3 caracteres.']],
        duracaoMeses: [[v => Number(v) >= 1, 'A duração mínima é 1 mês.'],
                       [v => Number(v) <= 36, 'A duração máxima é 36 meses.'],
                       [v => Number.isInteger(Number(v)), 'Use um número inteiro de meses.']],
        valorMensal: [[v => v !== null && v !== undefined && v !== '', 'Informe o valor mensal.'],
                      [v => Number(v) > 0, 'O valor deve ser maior que zero.'],
                      [v => Number(v) <= 9999.99, 'Valor acima do limite permitido.']],
        descricao: [[A.obrigatorio, 'Descreva o que o plano inclui.']]
      });
      return Object.keys(this.erros).length === 0;
    }
  }
}).mount('#app');
</script>
</body>
</html>
