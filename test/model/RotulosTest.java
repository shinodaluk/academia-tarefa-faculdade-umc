package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RotulosTest {

    private final Gson gson = new Gson();

    @Test
    @DisplayName("Converte o rótulo do banco/tela no enum")
    void converteRotulo() {
        assertEquals(StatusCadastro.ATIVO, Rotulos.de(StatusCadastro.class, "Ativo"));
        assertEquals(SituacaoMatricula.SUSPENSA, Rotulos.de(SituacaoMatricula.class, "Suspensa"));
        assertEquals(ObjetivoTreino.EMAGRECIMENTO, Rotulos.de(ObjetivoTreino.class, "Emagrecimento"));
        assertEquals(Especialidade.MUSCULACAO, Rotulos.de(Especialidade.class, "Musculação"));
    }

    @Test
    @DisplayName("Rótulo desconhecido é rejeitado com a lista de valores aceitos")
    void rejeitaDesconhecido() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> Rotulos.de(StatusCadastro.class, "Talvez"));

        assertTrue(erro.getMessage().contains("[Ativo, Inativo]"), erro.getMessage());
    }

    @Test
    @DisplayName("Nulo continua nulo (a validação decide se é obrigatório)")
    void nulo() {
        assertNull(Rotulos.de(StatusCadastro.class, null));
    }

    @Test
    @DisplayName("JSON usa o rótulo, com acento, nos dois sentidos")
    void json() {
        assertEquals("\"Musculação\"", gson.toJson(Especialidade.MUSCULACAO));
        assertEquals(Especialidade.MUSCULACAO, gson.fromJson("\"Musculação\"", Especialidade.class));
    }

    @Test
    @DisplayName("JSON com rótulo desconhecido é rejeitado, não vira nulo em silêncio")
    void jsonDesconhecido() {
        assertThrows(IllegalArgumentException.class, () -> gson.fromJson("\"Natação\"", Especialidade.class));
    }

    @Test
    @DisplayName("Entidades guardam o enum e o builder converte o texto do formulário")
    void builders() {
        assertEquals(StatusCadastro.INATIVO, Aluno.getBuilder().comStatus("Inativo").constroi().getStatus());
        assertEquals(Especialidade.PILATES, Instrutor.getBuilder().comEspecialidade("Pilates").constroi().getEspecialidade());
        assertEquals(SituacaoMatricula.CANCELADA, Matricula.getBuilder().comSituacao("Cancelada").constroi().getSituacao());
        assertEquals(ObjetivoTreino.HIPERTROFIA, FichaTreino.getBuilder().comObjetivo("Hipertrofia").constroi().getObjetivo());
        assertThrows(IllegalArgumentException.class, () -> Aluno.getBuilder().comStatus("Talvez"));
    }

    @Test
    @DisplayName("Entidade serializa o enum como o texto que as telas comparam")
    void entidadeNoJson() {
        String json = gson.toJson(Matricula.getBuilder().doSituacaoAtiva().constroi());

        assertTrue(json.contains("\"situacao\":\"Ativa\""), json);
    }
}
