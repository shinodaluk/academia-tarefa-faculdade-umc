<%-- 
    Document   : form
    Created on : Sep 28, 2026, 9:43:22 PM
    Author     : Shino
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Cadastro de aluno — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="css/modernist.css">
<link rel="stylesheet" href="css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>

<script type="application/json" id="dados-aluno">${alunoJson}</script>
<script type="application/json" id="dados-erros">{}</script>

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
    <div class="sidebar-foot">Validação no cliente é conveniência;<br>a do servlet é a que vale.</div>
  </aside>

  <main class="conteudo">
    <header class="pagina-head">
      <div>
        <div class="kicker">Alunos</div>
        <h1>{{ modelo.id ? 'Editar aluno' : 'Novo aluno' }}</h1>
      </div>
    </header>

    <div class="aviso" v-if="aviso">
      <span>{{ aviso }}</span>
      <button type="button" @click="aviso = ''">Fechar</button>
    </div>

    <!-- POST síncrono: o servlet valida, grava e redireciona para alunos -->
    <form class="painel-form" method="post" action="aluno?acao=salvar" novalidate
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
          <label for="cpf">CPF</label>
          <input id="cpf" name="cpf" class="input" :class="{ invalido: erros.cpf }" v-model="modelo.cpf"
                 placeholder="000.000.000-00" inputmode="numeric"
                 @input="modelo.cpf = mascaraCPF(modelo.cpf)">
          <span class="erro" v-if="erros.cpf">{{ erros.cpf }}</span>
        </div>

        <div class="field">
          <label for="nascimento">Data de nascimento</label>
          <input id="nascimento" name="dataNascimento" type="date" class="input"
                 :class="{ invalido: erros.dataNascimento }" v-model="modelo.dataNascimento" :max="hoje">
          <span class="erro" v-if="erros.dataNascimento">{{ erros.dataNascimento }}</span>
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
          {{ enviando ? 'Salvando…' : 'Salvar aluno' }}
        </button>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/aluno">Cancelar</a>
      </div>
    </form>

    <!-- 1:1 com o aluno: lida e gravada pela API REST, só depois do aluno existir -->
    <section class="painel-form" v-if="modelo.id" aria-labelledby="titulo-avaliacao"
             style="border-top: 2px solid var(--color-divider)">
      <div class="kicker">Acompanhamento</div>
      <h2 id="titulo-avaliacao" style="margin:0 0 var(--space-6)">Avaliação física</h2>

      <p class="vazio" v-if="avaliacaoCarregando">Carregando avaliação…</p>
      <template v-else>
        <p class="vazio" v-if="!avaliacaoExiste" style="margin-top:0">Este aluno ainda não tem avaliação física.</p>
        <div class="aviso" v-if="avaliacaoAviso" style="margin-bottom:var(--space-6)">
          <span>{{ avaliacaoAviso }}</span>
          <button type="button" @click="avaliacaoAviso = ''">Fechar</button>
        </div>

        <form novalidate @submit.prevent="salvarAvaliacao">
          <div class="grade-form grade-form-3">
            <div class="field">
              <label for="dataAvaliacao">Data da avaliação</label>
              <input id="dataAvaliacao" type="date" class="input" :max="hoje"
                     :class="{ invalido: avaliacaoErros.dataAvaliacao }" v-model="avaliacao.dataAvaliacao">
              <span class="erro" v-if="avaliacaoErros.dataAvaliacao">{{ avaliacaoErros.dataAvaliacao }}</span>
            </div>
            <div class="field">
              <label for="pesoKg">Peso (kg)</label>
              <input id="pesoKg" type="number" step="0.01" min="1" max="400" class="input"
                     :class="{ invalido: avaliacaoErros.pesoKg }" v-model="avaliacao.pesoKg">
              <span class="erro" v-if="avaliacaoErros.pesoKg">{{ avaliacaoErros.pesoKg }}</span>
            </div>
            <div class="field">
              <label for="alturaM">Altura (m)</label>
              <input id="alturaM" type="number" step="0.01" min="0.5" max="2.6" class="input"
                     :class="{ invalido: avaliacaoErros.alturaM }" v-model="avaliacao.alturaM">
              <span class="erro" v-if="avaliacaoErros.alturaM">{{ avaliacaoErros.alturaM }}</span>
            </div>
            <div class="field">
              <label for="percentualGordura">Gordura (%)</label>
              <input id="percentualGordura" type="number" step="0.1" min="0" max="70" class="input"
                     v-model="avaliacao.percentualGordura">
            </div>
            <div class="field">
              <label for="massaMagraKg">Massa magra (kg)</label>
              <input id="massaMagraKg" type="number" step="0.01" min="1" class="input" v-model="avaliacao.massaMagraKg">
            </div>
            <div class="field">
              <label for="cinturaCm">Cintura (cm)</label>
              <input id="cinturaCm" type="number" step="0.1" min="30" class="input" v-model="avaliacao.cinturaCm">
            </div>
            <div class="field">
              <label for="pressaoArterial">Pressão arterial</label>
              <input id="pressaoArterial" class="input" placeholder="120/80" maxlength="7"
                     :class="{ invalido: avaliacaoErros.pressaoArterial }" v-model.trim="avaliacao.pressaoArterial">
              <span class="erro" v-if="avaliacaoErros.pressaoArterial">{{ avaliacaoErros.pressaoArterial }}</span>
            </div>
            <div class="field">
              <label for="frequenciaCardiaca">FC em repouso (bpm)</label>
              <input id="frequenciaCardiaca" type="number" step="1" min="20" max="250" class="input"
                     v-model="avaliacao.frequenciaCardiaca">
            </div>
            <div class="field">
              <label>IMC</label>
              <div class="indicador" style="padding:0; border:0">
                <div class="valor">{{ imc || '—' }}</div>
              </div>
            </div>
            <div class="field col-2">
              <label for="observacoes">Observações</label>
              <input id="observacoes" class="input" maxlength="255" v-model.trim="avaliacao.observacoes">
            </div>
          </div>

          <div class="acoes-form" style="margin-top:var(--space-8)">
            <button type="submit" class="btn btn-primary" :disabled="salvandoAvaliacao">
              {{ salvandoAvaliacao ? 'Salvando…' : 'Salvar avaliação' }}
            </button>
            <button type="button" class="btn btn-secondary" v-if="avaliacaoExiste"
                    :disabled="salvandoAvaliacao" @click="excluirAvaliacao">Excluir avaliação</button>
          </div>
        </form>
      </template>
    </section>
  </main>
</div>

<script src="js/comum.js"></script>
<script>
const A = window.Academia;

Vue.createApp({
  mixins: [A.comum],
  data() {
    return {
      hoje: A.hojeISO(),
      aviso: "${aviso}",
      modelo: A.dados('dados-aluno', {}),
      erros: A.dados('dados-erros', {}),   // erros vindos do servlet
      avaliacao: {},
      avaliacaoExiste: false,
      avaliacaoCarregando: false,
      avaliacaoErros: {},
      avaliacaoAviso: '',
      salvandoAvaliacao: false
    };
  },
  computed: {
    // prévia; o valor oficial é o imc devolvido pela API
    imc() {
      const peso = Number(this.avaliacao.pesoKg), altura = Number(this.avaliacao.alturaM);
      return peso > 0 && altura > 0 ? (peso / (altura * altura)).toFixed(1).replace('.', ',') : '';
    }
  },
  mounted() {
    if (this.modelo.id) this.carregarAvaliacao();
  },
  methods: {
    urlAvaliacao() {
      return window.CONTEXTO + '/api/alunos/' + this.modelo.id + '/avaliacao';
    },
    async carregarAvaliacao() {
      this.avaliacaoCarregando = true;
      try {
        const resposta = await fetch(this.urlAvaliacao());
        if (resposta.status === 404) {
          this.avaliacao = { dataAvaliacao: this.hoje };
          this.avaliacaoExiste = false;
          return;
        }
        const dados = await resposta.json();
        if (!resposta.ok) throw new Error(dados.error);
        this.avaliacao = dados;
        this.avaliacaoExiste = true;
      } catch (ex) {
        this.avaliacaoAviso = 'Não foi possível carregar a avaliação física.';
      } finally {
        this.avaliacaoCarregando = false;
      }
    },
    async salvarAvaliacao() {
      this.avaliacaoErros = A.validar(this.avaliacao, {
        dataAvaliacao: [[A.obrigatorio, 'Informe a data da avaliação.']],
        pesoKg: [[A.obrigatorio, 'Informe o peso.'], [v => v >= 1 && v <= 400, 'Peso entre 1 e 400 kg.']],
        alturaM: [[A.obrigatorio, 'Informe a altura.'], [v => v >= 0.5 && v <= 2.6, 'Altura entre 0,5 e 2,6 m.']],
        pressaoArterial: [[v => !v || /^[0-9]{2,3}\/[0-9]{2,3}$/.test(v), 'Use o formato 120/80.']]
      });
      if (Object.keys(this.avaliacaoErros).length) return;

      // campo vazio vai como null, não como texto vazio
      const corpo = {};
      ['dataAvaliacao', 'pesoKg', 'alturaM', 'percentualGordura', 'massaMagraKg', 'cinturaCm',
       'pressaoArterial', 'frequenciaCardiaca', 'observacoes'].forEach(campo => {
        const valor = this.avaliacao[campo];
        corpo[campo] = valor === '' || valor === undefined ? null : valor;
      });

      this.salvandoAvaliacao = true;
      try {
        const resposta = await fetch(this.urlAvaliacao(), {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json; charset=utf-8' },
          body: JSON.stringify(corpo)
        });
        const dados = await resposta.json();
        if (!resposta.ok) throw new Error((dados.detalhes || [dados.error]).join('; '));
        this.avaliacao = dados;
        this.avaliacaoExiste = true;
        this.avaliacaoAviso = resposta.status === 201 ? 'Avaliação cadastrada.' : 'Avaliação atualizada.';
      } catch (ex) {
        this.avaliacaoAviso = ex.message || 'Falha ao salvar a avaliação.';
      } finally {
        this.salvandoAvaliacao = false;
      }
    },
    async excluirAvaliacao() {
      if (!confirm('Excluir a avaliação física? Esta ação não pode ser desfeita.')) return;
      this.salvandoAvaliacao = true;
      try {
        const resposta = await fetch(this.urlAvaliacao(), { method: 'DELETE' });
        if (!resposta.ok && resposta.status !== 404) throw new Error();
        this.avaliacao = { dataAvaliacao: this.hoje };
        this.avaliacaoExiste = false;
        this.avaliacaoAviso = 'Avaliação excluída.';
      } catch (ex) {
        this.avaliacaoAviso = 'Falha ao excluir a avaliação.';
      } finally {
        this.salvandoAvaliacao = false;
      }
    },
    validarFormulario() {
      this.erros = A.validar(this.modelo, {
        nome: [[A.obrigatorio, 'Informe o nome completo.'],
               [v => String(v).trim().split(/\s+/).length >= 2, 'Informe nome e sobrenome.']],
        cpf: [[A.obrigatorio, 'Informe o CPF.'], [A.cpfValido, 'CPF inválido.']],
        dataNascimento: [[A.obrigatorio, 'Informe a data de nascimento.'],
                         [v => v <= this.hoje, 'A data não pode ser futura.'],
                         [v => A.maiorDeIdade(v, 12), 'Idade mínima de 12 anos.']],
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
