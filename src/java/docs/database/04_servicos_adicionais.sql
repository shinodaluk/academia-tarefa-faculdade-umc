-- Execute este script em bancos academia já existentes para habilitar os decorators.
USE academia;

CREATE TABLE IF NOT EXISTS servico_adicional (
  codigo       VARCHAR(40)  PRIMARY KEY,
  descricao    VARCHAR(120) NOT NULL,
  valor_mensal DECIMAL(10,2) NOT NULL,
  CONSTRAINT ck_servico_adicional_valor CHECK (valor_mensal >= 0)
) ENGINE=InnoDB;

INSERT INTO servico_adicional (codigo, descricao, valor_mensal) VALUES
 ('BIOIMPEDANCIA', 'Bioimpedância', 20.00),
 ('CADEIRA_MASSAGEM', 'Cadeira de Massagem', 30.00),
 ('PRE_TREINO', 'Pré-treino', 12.30)
ON DUPLICATE KEY UPDATE
  descricao = VALUES(descricao),
  valor_mensal = VALUES(valor_mensal);
