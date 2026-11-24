-- =====================================================================
-- Sistema de Academia — Consultas parametrizadas (CRUD)
-- Marcadores nomeados no padrão :parametro (PDO / JDBC nomeado).
-- Para drivers posicionais, troque cada :parametro por ?
-- mantendo a ordem em que aparecem.
-- =====================================================================
USE academia;

-- =====================================================================
-- ALUNOS
-- =====================================================================

-- Listar / buscar por nome ou CPF (:busca pode vir vazio)
SELECT a.id, a.nome, a.cpf, a.telefone, a.email, a.status,
       COALESCE(p.nome, '— sem matrícula') AS plano_atual
FROM aluno a
LEFT JOIN matricula m ON m.aluno_id = a.id AND m.situacao = 'Ativa'
LEFT JOIN plano p     ON p.id = m.plano_id
WHERE (:busca = '' OR a.nome LIKE CONCAT('%', :busca, '%') OR a.cpf LIKE CONCAT('%', :busca, '%'))
  AND (:status = '' OR a.status = :status)
ORDER BY a.nome
LIMIT :limite OFFSET :deslocamento;

-- Obter um aluno
SELECT * FROM aluno WHERE id = :id;

-- Inserir
INSERT INTO aluno (nome, cpf, data_nascimento, telefone, email, status)
VALUES (:nome, :cpf, :data_nascimento, :telefone, :email, :status);

-- Atualizar
UPDATE aluno
   SET nome = :nome, cpf = :cpf, data_nascimento = :data_nascimento,
       telefone = :telefone, email = :email, status = :status
 WHERE id = :id;

-- Excluir
DELETE FROM aluno WHERE id = :id;

-- =====================================================================
-- PLANOS
-- =====================================================================

SELECT id, nome, duracao_meses, valor_mensal, descricao, ativo
FROM plano
WHERE (:apenas_ativos = 0 OR ativo = TRUE)
ORDER BY duracao_meses;

INSERT INTO plano (nome, duracao_meses, valor_mensal, descricao)
VALUES (:nome, :duracao_meses, :valor_mensal, :descricao);

UPDATE plano
   SET nome = :nome, duracao_meses = :duracao_meses,
       valor_mensal = :valor_mensal, descricao = :descricao, ativo = :ativo
 WHERE id = :id;

DELETE FROM plano WHERE id = :id;

-- =====================================================================
-- INSTRUTORES
-- =====================================================================

SELECT i.id, i.nome, i.cref, i.especialidade, i.telefone, i.email, i.status,
       (SELECT COUNT(*) FROM ficha_treino f WHERE f.instrutor_id = i.id) AS fichas_vinculadas
FROM instrutor i
WHERE (:especialidade = '' OR i.especialidade = :especialidade)
  AND (:status = '' OR i.status = :status)
ORDER BY i.nome;

INSERT INTO instrutor (nome, cref, especialidade, telefone, email, status)
VALUES (:nome, :cref, :especialidade, :telefone, :email, :status);

UPDATE instrutor
   SET nome = :nome, cref = :cref, especialidade = :especialidade,
       telefone = :telefone, email = :email, status = :status
 WHERE id = :id;

DELETE FROM instrutor WHERE id = :id;

-- =====================================================================
-- MATRÍCULAS
-- =====================================================================

-- Listagem com valor calculado e situação de pagamento
SELECT v.matricula_id, v.aluno, v.plano,
       m.data_inicio, v.valor_final, v.dia_vencimento, v.situacao,
       (SELECT MIN(me.data_vencimento) FROM mensalidade me
         WHERE me.matricula_id = m.id AND me.data_pagamento IS NULL) AS proximo_vencimento
FROM matricula m
JOIN vw_matricula_valor v ON v.matricula_id = m.id
WHERE (:aluno_id = 0 OR m.aluno_id = :aluno_id)
ORDER BY v.aluno;

INSERT INTO matricula (aluno_id, plano_id, data_inicio, fidelidade_meses,
                       desconto_estudante, dia_vencimento, situacao)
VALUES (:aluno_id, :plano_id, :data_inicio, :fidelidade_meses,
        :desconto_estudante, :dia_vencimento, :situacao);

UPDATE matricula
   SET aluno_id = :aluno_id, plano_id = :plano_id, data_inicio = :data_inicio,
       fidelidade_meses = :fidelidade_meses, desconto_estudante = :desconto_estudante,
       dia_vencimento = :dia_vencimento, situacao = :situacao
 WHERE id = :id;

DELETE FROM matricula WHERE id = :id;

-- Simular o valor da mensalidade antes de gravar
SELECT p.valor_mensal AS valor_base,
       ROUND(p.valor_mensal * (CASE WHEN :fidelidade_meses >= 12 THEN 0.10
                                    WHEN :fidelidade_meses >= 6  THEN 0.05
                                    ELSE 0 END), 2) AS desconto_fidelidade,
       ROUND(p.valor_mensal * (CASE WHEN :desconto_estudante THEN :taxa_estudante ELSE 0 END), 2) AS desconto_estudante,
       ROUND(p.valor_mensal * (1 - CASE WHEN :fidelidade_meses >= 12 THEN 0.10
                                        WHEN :fidelidade_meses >= 6  THEN 0.05
                                        ELSE 0 END
                                 - CASE WHEN :desconto_estudante THEN :taxa_estudante ELSE 0 END), 2) AS valor_final
FROM plano p
WHERE p.id = :plano_id;

-- =====================================================================
-- MENSALIDADES
-- =====================================================================

-- Gerar a parcela de uma competência (idempotente)
INSERT INTO mensalidade (matricula_id, competencia, data_vencimento, valor)
SELECT m.id, :competencia,
       STR_TO_DATE(CONCAT(:competencia, '-', LPAD(m.dia_vencimento, 2, '0')), '%Y-%m-%d'),
       v.valor_final
FROM matricula m
JOIN vw_matricula_valor v ON v.matricula_id = m.id
WHERE m.id = :matricula_id AND m.situacao = 'Ativa'
ON DUPLICATE KEY UPDATE valor = VALUES(valor);

-- Registrar pagamento
UPDATE mensalidade
   SET data_pagamento = :data_pagamento, forma_pagamento = :forma_pagamento
 WHERE id = :id;

-- Inadimplentes até uma data de referência
SELECT a.nome AS aluno, a.telefone, me.competencia, me.data_vencimento, me.valor,
       DATEDIFF(:data_referencia, me.data_vencimento) AS dias_atraso
FROM mensalidade me
JOIN matricula m ON m.id = me.matricula_id
JOIN aluno a     ON a.id = m.aluno_id
WHERE me.data_pagamento IS NULL
  AND me.data_vencimento < :data_referencia
ORDER BY dias_atraso DESC;

-- =====================================================================
-- FICHAS DE TREINO
-- =====================================================================

SELECT f.id, a.nome AS aluno, f.objetivo,
       COALESCE(i.nome, '— não vinculado') AS instrutor,
       COUNT(e.id) AS qtd_exercicios, f.data_criacao
FROM ficha_treino f
JOIN aluno a          ON a.id = f.aluno_id
LEFT JOIN instrutor i ON i.id = f.instrutor_id
LEFT JOIN ficha_exercicio e ON e.ficha_id = f.id
WHERE (:aluno_id = 0 OR f.aluno_id = :aluno_id)
  AND (:objetivo = '' OR f.objetivo = :objetivo)
GROUP BY f.id, a.nome, f.objetivo, i.nome, f.data_criacao
ORDER BY f.data_criacao DESC;

-- Exercícios de uma ficha
SELECT id, ordem, nome, series, repeticoes, carga_kg
FROM ficha_exercicio
WHERE ficha_id = :ficha_id
ORDER BY ordem;

INSERT INTO ficha_treino (aluno_id, instrutor_id, objetivo, data_criacao, observacoes)
VALUES (:aluno_id, :instrutor_id, :objetivo, :data_criacao, :observacoes);

INSERT INTO ficha_exercicio (ficha_id, ordem, nome, series, repeticoes, carga_kg)
VALUES (:ficha_id, :ordem, :nome, :series, :repeticoes, :carga_kg);

UPDATE ficha_exercicio
   SET ordem = :ordem, nome = :nome, series = :series,
       repeticoes = :repeticoes, carga_kg = :carga_kg
 WHERE id = :id;

DELETE FROM ficha_exercicio WHERE id = :id;
DELETE FROM ficha_treino    WHERE id = :id;

-- =====================================================================
-- INDICADORES DO PAINEL
-- =====================================================================
SELECT
  (SELECT COUNT(*) FROM aluno WHERE status = 'Ativo')                    AS alunos_ativos,
  (SELECT COUNT(*) FROM matricula WHERE situacao = 'Ativa')              AS matriculas,
  (SELECT COUNT(*) FROM mensalidade
     WHERE data_pagamento IS NULL AND data_vencimento < :data_referencia) AS mensalidades_vencidas,
  (SELECT COALESCE(SUM(valor_final), 0) FROM vw_matricula_valor
     WHERE situacao = 'Ativa')                                            AS receita_mensal;
