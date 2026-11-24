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
<title>Matrícula — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="css/modernist.css">
<link rel="stylesheet" href="css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>

<script type="application/json" id="dados-matricula">${matriculaJson}</script>
<script type="application/json" id="dados-alunos">${alunosJson}</script>
<script type="application/json" id="dados-planos">${planosJson}</script>
<script type="application/json" id="dados-erros">{}</script>

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
    <div class="sidebar-foot">O valor é recalculado na tela;<br>o banco recalcula ao gravar.</div>
  </aside>

  <main class="conteudo">
    <header class="pagina-head">
      <div>
        <div class="kicker">Matrículas</div>
        <h1>{{ modelo.id ? 'Editar matrícula' : 'Nova matrícula' }}</h1>
      </div>
    </header>

    <div class="aviso" v-if="aviso">
      <span>{{ aviso }}</span>
      <button type="button" @click="aviso = ''">Fechar</button>
    </div>

    <div class="split">
      <!-- POST síncrono: o servlet grava e redireciona para a edição -->
      <form class="painel-form" method="post" action="matricula?acao=salvar" novalidate
            @submit="enviarFormulario($event, validarFormulario())">
        <input type="hidden" name="id" :value="modelo.id || ''">

        <div class="grade-form">
          <div class="field col-2">
            <label for="aluno">Aluno</label>
            <select id="aluno" name="alunoId" class="input" :class="{ invalido: erros.alunoId }" v-model="modelo.alunoId">
              <option value="">Selecione o aluno</option>
              <option v-for="a in alunos" :key="a.id" :value="a.id">{{ a.nome }} — {{ a.cpf }}</option>
            </select>
            <span class="erro" v-if="erros.alunoId">{{ erros.alunoId }}</span>
          </div>

          <div class="field">
            <label for="plano">Plano</label>
            <select id="plano" name="planoId" class="input" :class="{ invalido: erros.planoId }" v-model="modelo.planoId">
              <option value="">Selecione o plano</option>
              <option v-for="p in planosDisponiveis" :key="p.id" :value="p.id">{{ p.nome }} — {{ moeda(p.valorMensal) }}/mês</option>
            </select>
            <span class="erro" v-if="erros.planoId">{{ erros.planoId }}</span>
          </div>

          <div class="field">
            <label for="inicio">Data de início</label>
            <input id="inicio" name="dataInicio" type="date" class="input"
                   :class="{ invalido: erros.dataInicio }" v-model="modelo.dataInicio">
            <span class="erro" v-if="erros.dataInicio">{{ erros.dataInicio }}</span>
          </div>

          <div class="field">
            <label for="fidelidade">Fidelidade</label>
            <select id="fidelidade" name="fidelidadeMeses" class="input"
                    :class="{ invalido: erros.fidelidadeMeses }" v-model.number="modelo.fidelidadeMeses">
              <option :value="0">Sem fidelidade</option>
              <option :value="6">6 meses — 5% off</option>
              <option :value="12">12 meses — 10% off</option>
            </select>
            <span class="erro" v-if="erros.fidelidadeMeses">{{ erros.fidelidadeMeses }}</span>
          </div>

          <div class="field">
            <label for="dia">Dia de vencimento</label>
            <input id="dia" name="diaVencimento" type="number" min="1" max="28" class="input"
                   :class="{ invalido: erros.diaVencimento }" v-model.number="modelo.diaVencimento">
            <span class="erro" v-if="erros.diaVencimento">{{ erros.diaVencimento }}</span>
          </div>

          <div class="field col-2">
            <label style="display:flex; align-items:center; gap:var(--space-3); cursor:pointer">
              <input type="checkbox" name="descontoEstudante" value="true" v-model="modelo.descontoEstudante"
                     style="width:18px; height:18px; accent-color:var(--color-accent)">
              Desconto estudante (15%)
            </label>
          </div>

          <div class="field col-2">
            <label for="situacao">Situação da matrícula</label>
            <select id="situacao" name="situacao" class="input" style="max-width:260px" v-model="modelo.situacao">
              <option value="Ativa">Ativa</option>
              <option value="Suspensa">Suspensa</option>
              <option value="Cancelada">Cancelada</option>
            </select>
          </div>
        </div>

        <div class="hr" style="margin:var(--space-8) 0; max-width:820px"></div>

        <div class="acoes-form">
          <button type="submit" class="btn btn-primary" :disabled="enviando">
            {{ enviando ? 'Salvando…' : 'Salvar matrícula' }}
          </button>
          <a class="btn btn-secondary" href="${pageContext.request.contextPath}/matricula">Cancelar</a>
        </div>
      </form>

      <aside class="resumo">
        <div class="kicker">Mensalidade</div>
        <div class="total">{{ moeda(calculo.total) }}</div>
        <table class="table" style="min-width:0">
          <tbody>
            <tr><td>Valor do plano</td><td style="text-align:right">{{ moeda(calculo.base) }}</td></tr>
            <tr><td>Fidelidade</td><td style="text-align:right">{{ calculo.descontoFidelidade ? '− ' + moeda(calculo.descontoFidelidade) : '—' }}</td></tr>
            <tr><td>Estudante</td><td style="text-align:right">{{ calculo.descontoEstudante ? '− ' + moeda(calculo.descontoEstudante) : '—' }}</td></tr>
            <tr><td style="font-weight:700">Total / mês</td><td style="text-align:right; font-weight:700">{{ moeda(calculo.total) }}</td></tr>
          </tbody>
        </table>
        <p style="font-size:12px; line-height:1.6; color:var(--color-neutral-700); margin-top:var(--space-6)">
          Primeiro vencimento em {{ vencimento }}.
          <span v-if="modelo.fidelidadeMeses">Compromisso de {{ modelo.fidelidadeMeses }} meses:
          total de {{ moeda(calculo.total * modelo.fidelidadeMeses) }}.</span>
        </p>
      </aside>
    </div>
  </main>
</div>

<script src="js/comum.js"></script>
<script>
const A = window.Academia;

Vue.createApp({
  mixins: [A.comum],
  data() {
    const modelo = A.dados('dados-matricula', {});
    if (!modelo.dataInicio) modelo.dataInicio = A.hojeISO();
    // id 0 vindo do Java = nada selecionado
    if (!modelo.alunoId) modelo.alunoId = '';
    if (!modelo.planoId) modelo.planoId = '';
    return {
      aviso: "${aviso}", modelo,
      alunos: A.dados('dados-alunos'),
      planos: A.dados('dados-planos'),
      erros: A.dados('dados-erros', {})
    };
  },
  computed: {
    // plano desativado some das opções, exceto o já gravado nesta matrícula
    planosDisponiveis() { return this.planos.filter(p => p.ativo || String(p.id) === String(this.modelo.planoId)); },
    planoSelecionado() { return this.planos.find(p => String(p.id) === String(this.modelo.planoId)); },
    calculo() {
      return A.calcularMensalidade(this.planoSelecionado ? this.planoSelecionado.valorMensal : 0,
                                   this.modelo.fidelidadeMeses, this.modelo.descontoEstudante);
    },
    // mesma regra do MatriculaDao.cadastrar: mês seguinte ao início
    vencimento() {
      const p = String(this.modelo.dataInicio || A.hojeISO()).split('-').map(Number);
      const dia = Math.min(Math.max(Number(this.modelo.diaVencimento) || 5, 1), 28);
      return new Date(p[0], p[1], dia).toLocaleDateString('pt-BR');
    }
  },
  watch: {
    planoSelecionado(p) {
      if (p && this.modelo.fidelidadeMeses > p.duracaoMeses) this.modelo.fidelidadeMeses = 0;
    }
  },
  methods: {
    validarFormulario() {
      this.erros = A.validar(this.modelo, {
        alunoId: [[A.obrigatorio, 'Selecione o aluno.'],
                  [v => { const a = this.alunos.find(x => String(x.id) === String(v));
                          return !a || a.status === 'Ativo'; },
                   'Aluno inativo — reative o cadastro antes de matricular.']],
        planoId: [[A.obrigatorio, 'Selecione o plano.']],
        dataInicio: [[A.obrigatorio, 'Informe a data de início.']],
        diaVencimento: [[v => Number(v) >= 1 && Number(v) <= 28, 'Escolha um dia entre 1 e 28.']],
        fidelidadeMeses: [[v => [0, 6, 12].includes(Number(v)), 'Fidelidade inválida.'],
                          [v => !this.planoSelecionado || Number(v) <= this.planoSelecionado.duracaoMeses,
                           'A fidelidade não pode exceder a duração do plano.']]
      });
      return Object.keys(this.erros).length === 0;
    }
  }
}).mount('#app');
</script>
</body>
</html>
