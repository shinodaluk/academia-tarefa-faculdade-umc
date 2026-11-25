-- Execute em bancos academia já existentes para criar a avaliação física (1:1 com aluno).
USE academia;

CREATE TABLE IF NOT EXISTS avaliacao_fisica (
  id                  INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  aluno_id            INT UNSIGNED  NOT NULL UNIQUE,
  data_avaliacao      DATE          NOT NULL,
  peso_kg             DECIMAL(5,2)  NOT NULL,
  altura_m            DECIMAL(3,2)  NOT NULL,
  percentual_gordura  DECIMAL(4,1)  NULL,
  massa_magra_kg      DECIMAL(5,2)  NULL,
  cintura_cm          DECIMAL(5,1)  NULL,
  pressao_arterial    VARCHAR(7)    NULL,
  frequencia_cardiaca SMALLINT UNSIGNED NULL,
  observacoes         VARCHAR(255)  NULL,
  atualizado_em       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_avaliacao_aluno FOREIGN KEY (aluno_id) REFERENCES aluno(id) ON DELETE CASCADE,
  CONSTRAINT ck_avaliacao_peso CHECK (peso_kg > 0),
  CONSTRAINT ck_avaliacao_altura CHECK (altura_m > 0)
) ENGINE=InnoDB;

-- busca pelo CPF para não depender dos ids gerados
INSERT INTO avaliacao_fisica (aluno_id, data_avaliacao, peso_kg, altura_m, percentual_gordura,
                              massa_magra_kg, cintura_cm, pressao_arterial, frequencia_cardiaca, observacoes)
SELECT a.id, d.data_avaliacao, d.peso_kg, d.altura_m, d.percentual_gordura,
       d.massa_magra_kg, d.cintura_cm, d.pressao_arterial, d.frequencia_cardiaca, d.observacoes
FROM (
  SELECT '277.540.918-01' AS cpf, DATE '2026-02-10' AS data_avaliacao, 78.40 AS peso_kg, 1.76 AS altura_m,
         18.5 AS percentual_gordura, 63.90 AS massa_magra_kg, 84.0 AS cintura_cm, '120/80' AS pressao_arterial,
         64 AS frequencia_cardiaca, 'Sem restrições' AS observacoes
  UNION ALL
  SELECT '412.889.100-29', DATE '2026-03-05', 61.20, 1.63, 24.1, 46.45, 72.5, '110/70', 70, NULL
  UNION ALL
  SELECT '905.331.277-34', DATE '2026-04-18', 92.70, 1.81, 27.8, 66.93, 98.0, '130/85', 78, 'Dor lombar leve'
) AS d
JOIN aluno a ON a.cpf = d.cpf
ON DUPLICATE KEY UPDATE data_avaliacao = VALUES(data_avaliacao);
