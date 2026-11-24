-- =====================================================================
-- Sistema de Academia — Esquema do banco de dados
-- Compatível com MySQL 8 / MariaDB 10.5+
-- =====================================================================

CREATE DATABASE IF NOT EXISTS academia
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE academia;

-- ---------------------------------------------------------------------
-- Alunos
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS aluno (
  id             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nome           VARCHAR(120)  NOT NULL,
  cpf            CHAR(14)      NOT NULL UNIQUE,      -- 000.000.000-00
  data_nascimento DATE         NULL,
  telefone       VARCHAR(20)   NULL,
  email          VARCHAR(120)  NULL,
  status         ENUM('Ativo','Inativo') NOT NULL DEFAULT 'Ativo',
  criado_em      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  atualizado_em  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_aluno_nome (nome),
  INDEX idx_aluno_status (status)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Planos
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS plano (
  id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nome          VARCHAR(60)    NOT NULL UNIQUE,
  duracao_meses TINYINT UNSIGNED NOT NULL DEFAULT 1,
  valor_mensal  DECIMAL(10,2)  NOT NULL,
  descricao     VARCHAR(180)   NULL,
  ativo         BOOLEAN        NOT NULL DEFAULT TRUE,
  criado_em     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_plano_valor CHECK (valor_mensal >= 0),
  CONSTRAINT ck_plano_duracao CHECK (duracao_meses BETWEEN 1 AND 36)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Serviços adicionais da matrícula
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS servico_adicional (
  codigo       VARCHAR(40)  PRIMARY KEY,
  descricao    VARCHAR(120) NOT NULL,
  valor_mensal DECIMAL(10,2) NOT NULL,
  CONSTRAINT ck_servico_adicional_valor CHECK (valor_mensal >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Instrutores
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS instrutor (
  id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nome          VARCHAR(120) NOT NULL,
  cref          VARCHAR(20)  NOT NULL UNIQUE,        -- 000000-G/SP
  especialidade ENUM('Musculação','Funcional','Condicionamento','Pilates') NOT NULL,
  telefone      VARCHAR(20)  NULL,
  email         VARCHAR(120) NULL,
  status        ENUM('Ativo','Inativo') NOT NULL DEFAULT 'Ativo',
  criado_em     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_instrutor_status (status)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Matrículas
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS matricula (
  id               INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  aluno_id         INT UNSIGNED NOT NULL,
  plano_id         INT UNSIGNED NOT NULL,
  data_inicio      DATE         NOT NULL,
  fidelidade_meses TINYINT UNSIGNED NOT NULL DEFAULT 0,   -- 0, 6 ou 12
  desconto_estudante BOOLEAN    NOT NULL DEFAULT FALSE,
  dia_vencimento   TINYINT UNSIGNED NOT NULL DEFAULT 5,
  situacao         ENUM('Ativa','Cancelada','Suspensa') NOT NULL DEFAULT 'Ativa',
  criado_em        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_matricula_aluno FOREIGN KEY (aluno_id) REFERENCES aluno(id) ON DELETE CASCADE,
  CONSTRAINT fk_matricula_plano FOREIGN KEY (plano_id) REFERENCES plano(id) ON DELETE RESTRICT,
  CONSTRAINT ck_matricula_dia CHECK (dia_vencimento BETWEEN 1 AND 28),
  CONSTRAINT ck_matricula_fid CHECK (fidelidade_meses IN (0,6,12)),
  INDEX idx_matricula_aluno (aluno_id),
  INDEX idx_matricula_plano (plano_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Mensalidades (parcelas geradas a partir da matrícula)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS mensalidade (
  id             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  matricula_id   INT UNSIGNED  NOT NULL,
  competencia    CHAR(7)       NOT NULL,             -- AAAA-MM
  data_vencimento DATE         NOT NULL,
  valor          DECIMAL(10,2) NOT NULL,
  data_pagamento DATE          NULL,
  forma_pagamento ENUM('Dinheiro','Pix','Cartão','Boleto') NULL,
  CONSTRAINT fk_mensalidade_matricula FOREIGN KEY (matricula_id) REFERENCES matricula(id) ON DELETE CASCADE,
  UNIQUE KEY uk_mensalidade (matricula_id, competencia),
  INDEX idx_mensalidade_venc (data_vencimento)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Fichas de treino e seus exercícios
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ficha_treino (
  id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  aluno_id     INT UNSIGNED NOT NULL,
  instrutor_id INT UNSIGNED NULL,
  objetivo     ENUM('Hipertrofia','Emagrecimento','Condicionamento') NOT NULL,
  data_criacao DATE         NOT NULL,
  observacoes  VARCHAR(255) NULL,
  CONSTRAINT fk_ficha_aluno FOREIGN KEY (aluno_id) REFERENCES aluno(id) ON DELETE CASCADE,
  CONSTRAINT fk_ficha_instrutor FOREIGN KEY (instrutor_id) REFERENCES instrutor(id) ON DELETE SET NULL,
  INDEX idx_ficha_aluno (aluno_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ficha_exercicio (
  id        INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  ficha_id  INT UNSIGNED NOT NULL,
  ordem     TINYINT UNSIGNED NOT NULL DEFAULT 1,
  nome      VARCHAR(80)  NOT NULL,
  series    TINYINT UNSIGNED NOT NULL DEFAULT 3,
  repeticoes VARCHAR(20) NOT NULL DEFAULT '12',      -- "8-10", "25 min"
  carga_kg  VARCHAR(20)  NULL,
  CONSTRAINT fk_exercicio_ficha FOREIGN KEY (ficha_id) REFERENCES ficha_treino(id) ON DELETE CASCADE,
  INDEX idx_exercicio_ficha (ficha_id, ordem)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Visão de apoio: mensalidade calculada da matrícula
-- (desconto de fidelidade 6m = 5%, 12m = 10%; estudante = 15%)
-- ---------------------------------------------------------------------
CREATE OR REPLACE VIEW vw_matricula_valor AS
SELECT
  m.id                AS matricula_id,
  a.id                AS aluno_id,
  a.nome              AS aluno,
  p.nome              AS plano,
  p.valor_mensal      AS valor_base,
  CASE WHEN m.fidelidade_meses >= 12 THEN 0.10
       WHEN m.fidelidade_meses >= 6  THEN 0.05
       ELSE 0 END     AS taxa_fidelidade,
  CASE WHEN m.desconto_estudante THEN 0.15 ELSE 0 END AS taxa_estudante,
  ROUND(p.valor_mensal *
        (1 - CASE WHEN m.fidelidade_meses >= 12 THEN 0.10
                  WHEN m.fidelidade_meses >= 6  THEN 0.05 ELSE 0 END
           - CASE WHEN m.desconto_estudante THEN 0.15 ELSE 0 END), 2) AS valor_final,
  m.dia_vencimento,
  m.situacao
FROM matricula m
JOIN aluno a ON a.id = m.aluno_id
JOIN plano p ON p.id = m.plano_id;
