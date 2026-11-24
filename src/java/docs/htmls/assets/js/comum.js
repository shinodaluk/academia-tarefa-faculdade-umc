/* =====================================================================
   comum.js — utilitários compartilhados (Vue 3 via CDN, sem build)
   Modelo NÃO-SPA: o servlet renderiza o JSON dentro da página e a
   navegação/gravação acontece por requisições síncronas (link e form POST).
   Este arquivo não faz nenhuma chamada HTTP.
   ===================================================================== */
(function (global) {
  'use strict';

  /* Contexto da aplicação. No JSP, antes deste script:
     <script>window.CONTEXTO = '${pageContext.request.contextPath}';</script> */
  var CONTEXTO = global.CONTEXTO || '';

  /* ---------- Dados embutidos pelo servlet ----------
     <script type="application/json" id="dados-alunos">${alunosJson}</script>  */
  function dados(id, padrao) {
    var el = document.getElementById(id);
    if (!el) return padrao === undefined ? [] : padrao;
    var texto = el.textContent.trim();
    if (!texto) return padrao === undefined ? [] : padrao;
    try {
      return JSON.parse(texto);
    } catch (e) {
      console.error('[academia] JSON inválido em #' + id, e);
      return padrao === undefined ? [] : padrao;
    }
  }

  /* ---------- Formatação ---------- */
  function moeda(v) {
    return (Number(v) || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
  function dataBR(iso) {
    if (!iso) return '—';
    var p = String(iso).substring(0, 10).split('-');
    return p.length === 3 ? p[2] + '/' + p[1] + '/' + p[0] : iso;
  }
  function hojeISO() { return new Date().toISOString().substring(0, 10); }

  /* ---------- Máscaras ---------- */
  function mascaraCPF(valor) {
    var d = String(valor || '').replace(/\D/g, '').slice(0, 11);
    return d.replace(/(\d{3})(\d)/, '$1.$2')
            .replace(/(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
            .replace(/\.(\d{3})(\d{1,2})$/, '.$1-$2');
  }
  function mascaraTelefone(valor) {
    var d = String(valor || '').replace(/\D/g, '').slice(0, 11);
    if (d.length <= 10) return d.replace(/(\d{2})(\d)/, '($1) $2').replace(/(\d{4})(\d)/, '$1-$2');
    return d.replace(/(\d{2})(\d)/, '($1) $2').replace(/(\d{5})(\d)/, '$1-$2');
  }
  function mascaraCREF(valor) {
    return String(valor || '').toUpperCase().replace(/[^0-9A-Z\-\/]/g, '').slice(0, 14);
  }

  /* ---------- Validações (repita todas no servlet) ---------- */
  function cpfValido(cpf) {
    var d = String(cpf || '').replace(/\D/g, '');
    if (d.length !== 11 || /^(\d)\1{10}$/.test(d)) return false;
    var soma = 0, resto, i;
    for (i = 1; i <= 9; i++) soma += parseInt(d.substring(i - 1, i), 10) * (11 - i);
    resto = (soma * 10) % 11; if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(d.substring(9, 10), 10)) return false;
    soma = 0;
    for (i = 1; i <= 10; i++) soma += parseInt(d.substring(i - 1, i), 10) * (12 - i);
    resto = (soma * 10) % 11; if (resto === 10 || resto === 11) resto = 0;
    return resto === parseInt(d.substring(10, 11), 10);
  }
  function emailValido(v) { return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(String(v || '').trim()); }
  function telefoneValido(v) { var d = String(v || '').replace(/\D/g, ''); return d.length === 10 || d.length === 11; }
  function maiorDeIdade(iso, minimo) {
    if (!iso) return false;
    var n = new Date(iso + 'T00:00:00'), h = new Date();
    var anos = h.getFullYear() - n.getFullYear();
    if (h.getMonth() < n.getMonth() || (h.getMonth() === n.getMonth() && h.getDate() < n.getDate())) anos--;
    return anos >= (minimo == null ? 12 : minimo);
  }

  /* Mapa { campo: [ [teste, mensagem], ... ] } → { campo: mensagem } */
  function validar(modelo, regras) {
    var erros = {};
    Object.keys(regras).forEach(function (campo) {
      for (var i = 0; i < regras[campo].length; i++) {
        var teste = regras[campo][i][0], msg = regras[campo][i][1];
        if (!teste(modelo[campo], modelo)) { erros[campo] = msg; break; }
      }
    });
    return erros;
  }
  var obrigatorio = function (v) { return String(v == null ? '' : v).trim() !== ''; };

  /* ---------- Cálculo da mensalidade (espelha vw_matricula_valor) ---------- */
  function taxaFidelidade(meses) { var m = Number(meses) || 0; return m >= 12 ? 0.10 : m >= 6 ? 0.05 : 0; }
  function calcularMensalidade(valorBase, fidelidadeMeses, estudante, taxaEstudante) {
    var base = Number(valorBase) || 0;
    var tf = taxaFidelidade(fidelidadeMeses);
    var te = estudante ? (taxaEstudante == null ? 0.15 : taxaEstudante) : 0;
    return { base: base, descontoFidelidade: base * tf, descontoEstudante: base * te, total: base * (1 - tf - te) };
  }
  function proximoVencimento(dia, referencia) {
    var d = Math.min(Math.max(Number(dia) || 5, 1), 28);
    var h = referencia ? new Date(referencia) : new Date();
    var base = new Date(h.getFullYear(), h.getMonth(), h.getDate());
    var venc = new Date(h.getFullYear(), h.getMonth(), d);
    if (venc < base) venc = new Date(h.getFullYear(), h.getMonth() + 1, d);
    return venc;
  }

  /* ---------- Mixin com o que toda página usa ---------- */
  var comum = {
    data: function () { return { erros: {}, enviando: false }; },
    methods: {
      moeda: moeda, dataBR: dataBR,
      mascaraCPF: mascaraCPF, mascaraTelefone: mascaraTelefone, mascaraCREF: mascaraCREF,
      /* Submit nativo: valida e, se estiver tudo certo, deixa o POST seguir
         para o servlet chamando form.submit() (não dispara o listener). */
      enviarFormulario: function (evento, valido) {
        evento.preventDefault();
        if (!valido) { this.aviso = 'Corrija os campos destacados antes de continuar.'; return; }
        this.enviando = true;
        evento.target.submit();
      },
      confirmarExclusao: function (evento, rotulo) {
        if (!confirm('Excluir ' + rotulo + '? Esta ação não pode ser desfeita.')) evento.preventDefault();
      }
    }
  };

  global.Academia = {
    CONTEXTO: CONTEXTO, dados: dados, comum: comum,
    moeda: moeda, dataBR: dataBR, hojeISO: hojeISO,
    mascaraCPF: mascaraCPF, mascaraTelefone: mascaraTelefone, mascaraCREF: mascaraCREF,
    cpfValido: cpfValido, emailValido: emailValido, telefoneValido: telefoneValido,
    maiorDeIdade: maiorDeIdade, validar: validar, obrigatorio: obrigatorio,
    calcularMensalidade: calcularMensalidade, taxaFidelidade: taxaFidelidade,
    proximoVencimento: proximoVencimento
  };
})(window);
