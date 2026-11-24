<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Adicionais Matrícula — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/modernist.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>
<script type="application/json" id="dados-matriculas">${matriculasJson}</script>
<script type="application/json" id="dados-servicos">${servicosJson}</script>
<script type="application/json" id="dados-aviso">${avisoJson}</script>

<div id="app" class="layout" v-cloak>
  <aside class="sidebar">
    <div class="sidebar-brand"><strong>Academia Vértice</strong><div class="kicker">Gestão</div></div>
    <nav class="sidebar-nav">
      <a href="${pageContext.request.contextPath}/aluno">Alunos</a>
      <a href="${pageContext.request.contextPath}/plano">Planos</a>
      <a href="${pageContext.request.contextPath}/matricula">Matrículas</a>
      <a class="ativo" href="${pageContext.request.contextPath}/matricula?acao=adicionais">Adicionais Matrícula</a>
      <a href="${pageContext.request.contextPath}/instrutor">Instrutores</a>
      <a href="${pageContext.request.contextPath}/ficha">Fichas de treino</a>
    </nav>
    <div class="sidebar-foot">Valores mensais calculados<br>pelos decorators no servidor.</div>
  </aside>

  <main class="conteudo">
    <header class="pagina-head">
      <div>
        <div class="kicker">Matrículas</div>
        <h1>Adicionais Matrícula</h1>
      </div>
    </header>

    <div class="aviso" v-if="aviso">
      <span>{{ aviso }}</span>
      <button type="button" @click="aviso = ''">Fechar</button>
    </div>

    <div class="painel adicionais-painel">
      <div class="adicionais-selecao">
        <div class="field">
          <label for="matricula">Matrícula</label>
          <select id="matricula" class="input" v-model.number="matriculaId" @change="calcular">
            <option :value="null">Selecione uma matrícula</option>
            <option v-for="m in matriculas" :key="m.id" :value="m.id">
              Matrícula {{ m.id }} — {{ m.aluno }} · {{ m.plano }}
            </option>
          </select>
        </div>

        <fieldset class="adicionais-lista">
          <legend>Serviços adicionais mensais</legend>
          <label class="adicional-opcao" v-for="servico in servicos" :key="servico.codigo">
            <input type="checkbox" :value="servico.codigo" v-model="selecionados"
                   :disabled="!matriculaId" @change="calcular">
            <span class="adicional-nome">{{ servico.descricao }}</span>
            <span class="adicional-preco">{{ moeda(servico.valorMensal) }} / mês</span>
          </label>
          <p class="vazio" v-if="!servicos.length">Nenhum serviço adicional disponível.</p>
        </fieldset>

        <p class="adicionais-ajuda">A seleção é apenas para simulação e não altera a matrícula.</p>
      </div>

      <section class="adicionais-resultado" aria-live="polite">
        <div class="resultado-cabecalho">
          <div class="kicker">Composição mensal</div>
          <span class="tag tag-neutral" v-if="carregando">Calculando…</span>
        </div>
        <p class="erro" role="alert" v-if="erro">{{ erro }}</p>
        <template v-if="resultado && !carregando">
          <div class="resultado-linha">
            <span>{{ resultado.descricaoBase }} (plano)</span>
            <strong>{{ moeda(resultado.custoBase) }}</strong>
          </div>
          <div class="resultado-linha" v-for="adicional in resultado.adicionais" :key="adicional.codigo">
            <span>{{ adicional.descricao }}</span>
            <strong>{{ moeda(adicional.valorMensal) }}</strong>
          </div>
          <div class="resultado-descricao">
            <div class="kicker">Descrição composta</div>
            <p>{{ resultado.descricao }}</p>
          </div>
          <div class="resultado-total">
            <span>Total mensal</span>
            <strong>{{ moeda(resultado.totalMensal) }}</strong>
          </div>
        </template>
        <p class="vazio" v-else-if="!erro && !carregando">
          Selecione uma matrícula para consultar o valor base e calcular os adicionais.
        </p>
      </section>
    </div>
  </main>
</div>

<script src="${pageContext.request.contextPath}/js/comum.js"></script>
<script>
const A = window.Academia;

Vue.createApp({
  data() {
    return {
      matriculas: A.dados('dados-matriculas'),
      servicos: A.dados('dados-servicos'),
      aviso: A.dados('dados-aviso', ''),
      matriculaId: null,
      selecionados: [],
      resultado: null,
      erro: '',
      carregando: false,
      requisicao: 0
    };
  },
  methods: {
    moeda(valor) {
      return Number(valor || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    },
    async calcular() {
      const atual = ++this.requisicao;
      this.erro = '';
      this.resultado = null;
      if (!this.matriculaId) {
        this.carregando = false;
        return;
      }

      this.carregando = true;
      try {
        const resposta = await fetch(window.CONTEXTO + '/matricula?acao=calcularAdicionais', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ id: this.matriculaId, adicionais: this.selecionados })
        });
        const dados = await resposta.json();
        if (atual !== this.requisicao) return;
        if (!resposta.ok) throw new Error(dados.error || 'Não foi possível calcular os adicionais.');
        this.resultado = dados;
      } catch (ex) {
        if (atual === this.requisicao) this.erro = ex.message || 'Falha ao consultar o servidor.';
      } finally {
        if (atual === this.requisicao) this.carregando = false;
      }
    }
  }
}).mount('#app');
</script>
</body>
</html>
