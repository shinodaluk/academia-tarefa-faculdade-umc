-- =====================================================================
-- Sistema de Academia — Procedimentos parametrizados + dados de exemplo
-- =====================================================================
USE academia;

DELIMITER $$

-- Matricular aluno e já gerar a mensalidade do mês de início
DROP PROCEDURE IF EXISTS sp_matricular_aluno $$
CREATE PROCEDURE sp_matricular_aluno (
  IN  p_aluno_id           INT UNSIGNED,
  IN  p_plano_id           INT UNSIGNED,
  IN  p_data_inicio        DATE,
  IN  p_fidelidade_meses   TINYINT UNSIGNED,
  IN  p_desconto_estudante BOOLEAN,
  IN  p_dia_vencimento     TINYINT UNSIGNED,
  OUT p_matricula_id       INT UNSIGNED
)
BEGIN
  DECLARE v_valor DECIMAL(10,2);

  INSERT INTO matricula (aluno_id, plano_id, data_inicio, fidelidade_meses,
                         desconto_estudante, dia_vencimento)
  VALUES (p_aluno_id, p_plano_id, p_data_inicio, p_fidelidade_meses,
          p_desconto_estudante, p_dia_vencimento);

  SET p_matricula_id = LAST_INSERT_ID();

  SELECT valor_final INTO v_valor
    FROM vw_matricula_valor WHERE matricula_id = p_matricula_id;

  INSERT INTO mensalidade (matricula_id, competencia, data_vencimento, valor)
  VALUES (p_matricula_id,
          DATE_FORMAT(p_data_inicio, '%Y-%m'),
          STR_TO_DATE(CONCAT(DATE_FORMAT(p_data_inicio, '%Y-%m'), '-',
                             LPAD(p_dia_vencimento, 2, '0')), '%Y-%m-%d'),
          v_valor);
END $$

-- Gerar as mensalidades de uma competência para todas as matrículas ativas
DROP PROCEDURE IF EXISTS sp_gerar_mensalidades $$
CREATE PROCEDURE sp_gerar_mensalidades (IN p_competencia CHAR(7))
BEGIN
  INSERT INTO mensalidade (matricula_id, competencia, data_vencimento, valor)
  SELECT m.id, p_competencia,
         STR_TO_DATE(CONCAT(p_competencia, '-', LPAD(m.dia_vencimento, 2, '0')), '%Y-%m-%d'),
         v.valor_final
  FROM matricula m
  JOIN vw_matricula_valor v ON v.matricula_id = m.id
  WHERE m.situacao = 'Ativa'
  ON DUPLICATE KEY UPDATE valor = VALUES(valor);
END $$

-- Baixa de pagamento por matrícula + competência
DROP PROCEDURE IF EXISTS sp_registrar_pagamento $$
CREATE PROCEDURE sp_registrar_pagamento (
  IN p_matricula_id   INT UNSIGNED,
  IN p_competencia    CHAR(7),
  IN p_data_pagamento DATE,
  IN p_forma          VARCHAR(20)
)
BEGIN
  UPDATE mensalidade
     SET data_pagamento = p_data_pagamento, forma_pagamento = p_forma
   WHERE matricula_id = p_matricula_id AND competencia = p_competencia;
END $$

DELIMITER ;

-- =====================================================================
-- Dados de exemplo (espelham o protótipo)
-- =====================================================================
INSERT INTO plano (nome, duracao_meses, valor_mensal, descricao) VALUES
 ('Mensal',      1, 129.90, 'Musculação, sem carência'),
 ('Trimestral',  3, 109.90, 'Musculação + aulas coletivas'),
 ('Semestral',   6,  99.90, 'Acesso total + avaliação física'),
 ('Anual',      12,  89.90, 'Acesso total + 2 avaliações');

INSERT INTO servico_adicional (codigo, descricao, valor_mensal) VALUES
 ('BIOIMPEDANCIA', 'Bioimpedância', 20.00),
 ('CADEIRA_MASSAGEM', 'Cadeira de Massagem', 30.00),
 ('PRE_TREINO', 'Pré-treino', 12.30)
ON DUPLICATE KEY UPDATE
  descricao = VALUES(descricao),
  valor_mensal = VALUES(valor_mensal);

INSERT INTO aluno (nome, cpf, data_nascimento, telefone, email, status) VALUES
 ('Ana Beatriz Ramos',   '412.889.100-29', '1996-04-12', '(11) 98812-4410', 'ana.ramos@email.com',  'Ativo'),
 ('Carlos Eduardo Lima', '277.540.918-01', '1988-11-03', '(11) 99671-2088', 'carlos.lima@email.com','Ativo'),
 ('Daniela Prado',       '905.331.277-34', '2001-07-25', '(11) 97440-3319', 'dani.prado@email.com', 'Ativo'),
 ('Eduardo Nakamura',    '188.402.663-05', '1993-02-17', '(11) 96522-7781', 'edu.naka@email.com',   'Inativo'),
 ('Fernanda Quirino',    '650.118.294-80', '1999-09-08', '(11) 98330-5512', 'fe.quirino@email.com', 'Ativo');

INSERT INTO instrutor (nome, cref, especialidade, telefone, email, status) VALUES
 ('Marcos Aurélio Dias', '018342-G/SP', 'Musculação',     '(11) 98110-2244', 'marcos@vertice.com',  'Ativo'),
 ('Juliana Reis',        '027711-G/SP', 'Funcional',      '(11) 97220-8831', 'juliana@vertice.com', 'Ativo'),
 ('Rafael Tonini',       '033905-G/SP', 'Condicionamento','(11) 96044-1177', 'rafael@vertice.com',  'Inativo');

INSERT INTO matricula (aluno_id, plano_id, data_inicio, fidelidade_meses, desconto_estudante, dia_vencimento) VALUES
 (1, 3, '2026-03-10',  6, TRUE,  10),
 (2, 1, '2026-08-05',  0, FALSE,  5),
 (3, 4, '2026-01-20', 12, TRUE,  20),
 (5, 2, '2026-07-28',  0, FALSE, 28);

CALL sp_gerar_mensalidades('2026-09');

INSERT INTO ficha_treino (aluno_id, instrutor_id, objetivo, data_criacao) VALUES
 (1, 1, 'Hipertrofia',     '2026-03-12'),
 (3, 2, 'Emagrecimento',   '2026-02-02'),
 (5, 1, 'Condicionamento', '2026-08-01');

INSERT INTO ficha_exercicio (ficha_id, ordem, nome, series, repeticoes, carga_kg) VALUES
 (1, 1, 'Supino reto',         4, '8-10',   '40'),
 (1, 2, 'Remada curvada',      4, '10',     '35'),
 (1, 3, 'Agachamento livre',   4, '8',      '60'),
 (2, 1, 'Esteira inclinada',   1, '25 min', '-'),
 (2, 2, 'Circuito funcional',  3, '12',     '8'),
 (3, 1, 'Bike',                1, '20 min', '-'),
 (3, 2, 'Burpee',              4, '10',     '-');
