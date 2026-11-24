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
<title>Ficha de treino — Sistema Academia</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800&display=swap">
<link rel="stylesheet" href="css/modernist.css">
<link rel="stylesheet" href="css/app.css">
<script src="https://unpkg.com/vue@3.4.38/dist/vue.global.prod.js"></script>
</head>
<body>

<script>window.CONTEXTO = '${pageContext.request.contextPath}';</script>

<script type="application/json" id="dados-ficha">${fichaJson}</script>
<script type="application/json" id="dados-alunos">${alunosJson}</script>
<script type="application/json" id="dados-instrutores">${instrutoresJson}</script>
<script type="application/json" id="dados-erros">{}</script>

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
    <div class="sidebar-foot">Os exercícios vão no POST<br>como exercicios[i].campo.</div>
  </aside>

  <main class="conteudo">
    <header class="pagina-head">
      <div>
        <div class="kicker">Fichas de treino</div>
        <h1>{{ modelo.id ? 'Editar ficha de treino' : 'Nova ficha de treino' }}</h1>
      </div>
    </header>

    <div class="aviso" v-if="aviso">
      <span>{{ aviso }}</span>
      <button type="button" @click="aviso = ''">Fechar</button>
    </div>

    <!-- POST síncrono: o servlet grava ficha + exercícios e redireciona para a edição -->
    <form class="painel-form" method="post" action="ficha?acao=salvar" novalidate
          @submit="enviarFormulario($event, validarFormulario())">
      <input type="hidden" name="id" :value="modelo.id || ''">
      <input type="hidden" name="dataCriacao" :value="modelo.dataCriacao">

      <div class="grade-form grade-form-3">
        <div class="field">
          <label for="aluno">Aluno</label>
          <select id="aluno" name="alunoId" class="input" :class="{ invalido: erros.alunoId }" v-model="modelo.alunoId">
            <option value="">Selecione o aluno</option>
            <option v-for="a in alunos" :key="a.id" :value="a.id">{{ a.nome }}</option>
          </select>
          <span class="erro" v-if="erros.alunoId">{{ erros.alunoId }}</span>
        </div>

        <div class="field">
          <label for="objetivo">Objetivo</label>
          <select id="objetivo" name="objetivo" class="input" v-model="modelo.objetivo">
            <option v-for="o in objetivos" :key="o" :value="o">{{ o }}</option>
          </select>
        </div>

        <div class="field">
          <label for="instrutor">Instrutor responsável</label>
          <select id="instrutor" name="instrutorId" class="input" :class="{ invalido: erros.instrutorId }"
                  v-model="modelo.instrutorId">
            <option value="">Selecione o instrutor</option>
            <option v-for="i in instrutoresAtivos" :key="i.id" :value="i.id">{{ i.nome }} — {{ i.especialidade }}</option>
          </select>
          <span class="erro" v-if="erros.instrutorId">{{ erros.instrutorId }}</span>
        </div>
      </div>

      <div class="hr" style="margin:var(--space-8) 0 var(--space-6)"></div>

      <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:var(--space-4); max-width:1000px">
        <h2 style="font-family:var(--font-heading); font-weight:800; font-size:20px; margin:0">
          Exercícios <span style="font-weight:400; color:var(--color-neutral-700)">({{ modelo.exercicios.length }})</span>
        </h2>
        <button type="button" class="btn btn-secondary" @click="adicionarExercicio">Adicionar exercício</button>
      </div>

      <span class="erro" v-if="erros.exercicios" style="margin-bottom:var(--space-4)">{{ erros.exercicios }}</span>

      <!-- índices contíguos no name: o SalvarAction lê exercicios[0..n] até faltar um -->
      <table class="table" style="max-width:1000px" v-if="modelo.exercicios.length">
        <thead>
          <tr>
            <th>Exercício</th><th style="width:100px">Séries</th><th style="width:120px">Repetições</th>
            <th style="width:120px">Carga (kg)</th><th style="width:170px"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(e, i) in modelo.exercicios" :key="i">
            <td>
              <input class="input" :class="{ invalido: tentouSalvar && !e.nome.trim() }"
                     :name="'exercicios[' + i + '].nome'" v-model.trim="e.nome" placeholder="Ex.: Supino reto" maxlength="80">
            </td>
            <td><input class="input" type="number" min="1" max="10"
                       :name="'exercicios[' + i + '].series'" v-model.number="e.series"></td>
            <td><input class="input" :name="'exercicios[' + i + '].repeticoes'"
                       v-model.trim="e.repeticoes" placeholder="8-10" maxlength="20"></td>
            <td><input class="input" :name="'exercicios[' + i + '].cargaKg'"
                       v-model.trim="e.cargaKg" placeholder="40" maxlength="20"></td>
            <td>
              <input type="hidden" :name="'exercicios[' + i + '].ordem'" :value="i + 1">
              <div class="acoes-linha">
                <button type="button" class="btn btn-ghost" :disabled="i === 0" @click="mover(i, -1)" title="Subir">↑</button>
                <button type="button" class="btn btn-ghost" :disabled="i === modelo.exercicios.length - 1" @click="mover(i, 1)" title="Descer">↓</button>
                <button type="button" class="btn btn-ghost" @click="remover(i)">Remover</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p class="vazio" v-else>Nenhum exercício na ficha. Use “Adicionar exercício”.</p>

      <div class="hr" style="margin:var(--space-8) 0; max-width:1000px"></div>

      <div class="acoes-form">
        <button type="submit" class="btn btn-primary" :disabled="enviando">
          {{ enviando ? 'Salvando…' : 'Salvar ficha' }}
        </button>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/ficha">Cancelar</a>
        <button type="button" class="btn btn-ghost" @click="imprimir">Imprimir ficha</button>
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
    const modelo = A.dados('dados-ficha', {});
    if (!modelo.dataCriacao) modelo.dataCriacao = A.hojeISO();
    // id 0 / null vindo do Java = nada selecionado
    if (!modelo.alunoId) modelo.alunoId = '';
    if (!modelo.instrutorId) modelo.instrutorId = '';
    if (!Array.isArray(modelo.exercicios) || !modelo.exercicios.length) {
      modelo.exercicios = [{ nome: '', series: 3, repeticoes: '12', cargaKg: '' }];
    }
    modelo.exercicios.forEach(e => { if (e.cargaKg == null) e.cargaKg = ''; });
    return {
      aviso: "${aviso}", tentouSalvar: false, modelo,
      objetivos: ['Hipertrofia', 'Emagrecimento', 'Condicionamento'],
      alunos: A.dados('dados-alunos'),
      instrutores: A.dados('dados-instrutores'),
      erros: A.dados('dados-erros', {})
    };
  },
  computed: {
    // instrutor inativo some das opções, exceto o já vinculado a esta ficha
    instrutoresAtivos() {
      return this.instrutores.filter(i => i.status === 'Ativo' || String(i.id) === String(this.modelo.instrutorId));
    }
  },
  methods: {
    adicionarExercicio() {
      this.modelo.exercicios.push({ nome: '', series: 3, repeticoes: '12', cargaKg: '' });
    },
    remover(i) { this.modelo.exercicios.splice(i, 1); },
    mover(i, passo) {
      const ex = this.modelo.exercicios;
      ex.splice(i + passo, 0, ex.splice(i, 1)[0]);
    },
    imprimir() { window.print(); },
    validarFormulario() {
      this.tentouSalvar = true;
      this.erros = A.validar(this.modelo, {
        alunoId: [[A.obrigatorio, 'Selecione o aluno.']],
        instrutorId: [[A.obrigatorio, 'Selecione o instrutor responsável.']],
        exercicios: [
          [v => v.length > 0, 'Adicione ao menos um exercício.'],
          [v => v.every(e => String(e.nome).trim()), 'Todo exercício precisa de um nome.'],
          [v => v.every(e => Number(e.series) >= 1 && Number(e.series) <= 10), 'Séries devem ficar entre 1 e 10.'],
          [v => v.every(e => String(e.repeticoes).trim()), 'Informe as repetições de cada exercício.']
        ]
      });
      return Object.keys(this.erros).length === 0;
    }
  }
}).mount('#app');
</script>
</body>
</html>
