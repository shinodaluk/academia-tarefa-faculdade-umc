package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Contrato JSON que as telas (form.jsp, listar.jsp) e a API esperam do aluno. */
class AlunoJsonTest {

    private final Gson gson = new Gson();

    @Test
    @DisplayName("Serializa dataNascimento em ISO e cpf com máscara")
    void serializa() {
        Aluno aluno = Aluno.getBuilder().comNome("Ana").comCpf("52998224725").comDataNascimento("1996-04-12").constroi();

        String json = gson.toJson(aluno);

        assertTrue(json.contains("\"dataNascimento\":\"1996-04-12\""), json);
        assertTrue(json.contains("\"cpf\":\"529.982.247-25\""), json);
    }

    @Test
    @DisplayName("Lê dataNascimento e cpf do JSON")
    void desserializa() {
        Aluno aluno = gson.fromJson("{\"cpf\":\"52998224725\",\"dataNascimento\":\"1996-04-12\"}", Aluno.class);

        assertEquals(Cpf.de("529.982.247-25"), aluno.getCpf());
        assertEquals(Datas.deIso("1996-04-12"), aluno.getDataNascimento());
    }

    @Test
    @DisplayName("Data inexistente no JSON é rejeitada")
    void dataInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> gson.fromJson("{\"dataNascimento\":\"2024-02-30\"}", Aluno.class));
    }

    @Test
    @DisplayName("Builder rejeita data inválida em vez de gravar nulo")
    void builderRejeitaDataInvalida() {
        assertThrows(IllegalArgumentException.class, () -> Aluno.getBuilder().comDataNascimento("31/12/2000"));
    }
}
