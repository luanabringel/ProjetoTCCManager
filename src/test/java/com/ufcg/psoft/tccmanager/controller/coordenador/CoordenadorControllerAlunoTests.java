package com.ufcg.psoft.tccmanager.controller.coordenador;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.tccmanager.dto.aluno.AlunoDTO;
import com.ufcg.psoft.tccmanager.dto.coordenador.CoordenadorDTO;
import com.ufcg.psoft.tccmanager.exception.CustomErrorType;
import com.ufcg.psoft.tccmanager.model.Aluno;
import com.ufcg.psoft.tccmanager.repository.AlunoRepository;
import com.ufcg.psoft.tccmanager.repository.CoordenadorRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Classe de teste de Aluno em CoordenadorController")
public class CoordenadorControllerAlunoTests {

    private final String URI_TEMPLATE = "/coordenador/alunos";

    @Autowired
    MockMvc driver;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AlunoRepository alunoRepository;

    @Autowired
    CoordenadorRepository coordenadorRepository;

    AlunoDTO alunoDTO1, alunoDTO2, alunoDTO3;

    CoordenadorDTO coordenadorDTO;

    @BeforeEach
    public void setup() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        coordenadorDTO = CoordenadorDTO.builder()
                .nome("Dalton Serey")
                .email("dalton@ccc.ufcg.edu.br")
                .senha("dalton123")
                .build();

        driver.perform(MockMvcRequestBuilders
                        .post("/coordenador/administrador")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(coordenadorDTO)))
                .andExpect(status().isCreated());

        alunoDTO1 = AlunoDTO.builder()
                .matricula("121210999")
                .nome("Fulano da Silva")
                .periodoConclusao("2023.2")
                .email("fulano.silva@ccc.ufcg.edu.br")
                .build();

        alunoDTO2 = AlunoDTO.builder()
                .matricula("121210888")
                .nome("Flavinho do Pneu")
                .periodoConclusao("2025.2")
                .email("flavinho.pneu@ccc.ufcg.edu.br")
                .build();

        alunoDTO3 = AlunoDTO.builder()
                .matricula("121210777")
                .nome("Jorginho Alemão")
                .periodoConclusao("2023.1")
                .email("jorginho.alemao@ccc.ufcg.edu.br")
                .build();
    }

    @AfterEach
    public void tearDown() {
        this.alunoRepository.deleteAll();
        this.coordenadorRepository.deleteAll();
    }

    @Nested
    @DisplayName("Verificações de cadastro de aluno por parte do coordenador")
    class VerificacoesCadastroAluno {

        @Test
        @DisplayName("Deve cadastar um aluno válido")
        void cadastrarAlunoValidoTest() throws Exception {
            // Arrange
            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoCadastrado = objectMapper.readValue(respostaJsonString, Aluno.class);

            // Assert
            assertNotNull(alunoCadastrado.getId());
            assertEquals(alunoDTO1.getMatricula(), alunoCadastrado.getMatricula());
            assertEquals(alunoDTO1.getNome(), alunoCadastrado.getNome());
            assertEquals(alunoDTO1.getPeriodoConclusao(), alunoCadastrado.getPeriodoConclusao());
            assertEquals(alunoDTO1.getEmail(), alunoCadastrado.getEmail());
        }

        @Test
        @DisplayName("Deve cadastar mais de um aluno válido")
        void cadastrarMaisDeUmAlunoValidoTest() throws Exception {
            // Arrange
            // Act
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO3))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoCadastrado1 = objectMapper.readValue(respostaJsonString1, Aluno.class);
            Aluno alunoCadastrado2 = objectMapper.readValue(respostaJsonString2, Aluno.class);
            Aluno alunoCadastrado3 = objectMapper.readValue(respostaJsonString3, Aluno.class);

            // Assert
            assertNotNull(alunoCadastrado1.getId());
            assertEquals(alunoDTO1.getMatricula(), alunoCadastrado1.getMatricula());
            assertEquals(alunoDTO1.getNome(), alunoCadastrado1.getNome());
            assertEquals(alunoDTO1.getPeriodoConclusao(), alunoCadastrado1.getPeriodoConclusao());
            assertEquals(alunoDTO1.getEmail(), alunoCadastrado1.getEmail());

            assertNotNull(alunoCadastrado2.getId());
            assertEquals(alunoDTO2.getMatricula(), alunoCadastrado2.getMatricula());
            assertEquals(alunoDTO2.getNome(), alunoCadastrado2.getNome());
            assertEquals(alunoDTO2.getPeriodoConclusao(), alunoCadastrado2.getPeriodoConclusao());
            assertEquals(alunoDTO2.getEmail(), alunoCadastrado2.getEmail());

            assertNotNull(alunoCadastrado3.getId());
            assertEquals(alunoDTO3.getMatricula(), alunoCadastrado3.getMatricula());
            assertEquals(alunoDTO3.getNome(), alunoCadastrado3.getNome());
            assertEquals(alunoDTO3.getPeriodoConclusao(), alunoCadastrado3.getPeriodoConclusao());
            assertEquals(alunoDTO3.getEmail(), alunoCadastrado3.getEmail());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao cadastrar um aluno com dados vazios")
        void cadastrarAlunoComDadosVaziosTest() throws Exception {
            // Arrange
            AlunoDTO requestVazia = AlunoDTO.builder()
                    .nome("")
                    .email("")
                    .matricula("")
                    .periodoConclusao("")
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestVazia))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar a matrícula é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o nome do aluno é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o período de conclusão é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o email e obrigatorio.")));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao cadastrar um aluno com dados nulos")
        void cadastrarAlunoComDadosNulosTest() throws Exception {
            // Arrange
            AlunoDTO requestNula = AlunoDTO.builder()
                    .nome(null)
                    .email(null)
                    .matricula(null)
                    .periodoConclusao(null)
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestNula))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar a matrícula é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o nome do aluno é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o período de conclusão é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o email e obrigatorio.")));
        }

    }

    @Nested
    @DisplayName("Verificações de alteração do aluno por parte do coordenador")
    class VerificacoesAlteracaoAluno {

        @Test
        @DisplayName("Deve alterar um aluno com dados válidos")
        void alterarAlunoDadosValidosTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoCriado = objectMapper.readValue(respostaJsonString, Aluno.class);

            AlunoDTO requestDTO = AlunoDTO.builder()
                    .matricula("121210555")
                    .nome("Novo Nome da Silva")
                    .periodoConclusao("2027.2")
                    .email("nome.silva@ccc.ufcg.edu.br")
                    .build();

            // Act
            String novaRespostaJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + alunoCriado.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoModificado = objectMapper.readValue(novaRespostaJsonString, Aluno.class);

            // Assert
            assertNotNull(alunoModificado.getId());
            assertEquals(requestDTO.getMatricula(), alunoModificado.getMatricula());
            assertEquals(requestDTO.getNome(), alunoModificado.getNome());
            assertEquals(requestDTO.getPeriodoConclusao(), alunoModificado.getPeriodoConclusao());
            assertEquals(requestDTO.getEmail(), alunoModificado.getEmail());
        }

        @Test
        @DisplayName("Deve alterar mais de um aluno com dados válidos")
        void alterarMaisDeUmAlunoDadosValidosTest() throws Exception {
            // Arrange
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO3))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoCriado1 = objectMapper.readValue(respostaJsonString1, Aluno.class);
            Aluno alunoCriado2 = objectMapper.readValue(respostaJsonString2, Aluno.class);
            Aluno alunoCriado3 = objectMapper.readValue(respostaJsonString3, Aluno.class);

            AlunoDTO requestDTO1 = AlunoDTO.builder()
                    .matricula("121210555")
                    .nome("Novo Nome da Silva")
                    .periodoConclusao("2027.2")
                    .email("nome.silva@ccc.ufcg.edu.br")
                    .build();

            AlunoDTO requestDTO2 = AlunoDTO.builder()
                    .matricula("121210444")
                    .nome("Novo Nome da Silva")
                    .periodoConclusao("2027.2")
                    .email("nome.silva@ccc.ufcg.edu.br")
                    .build();

            AlunoDTO requestDTO3 = AlunoDTO.builder()
                    .matricula("121210333")
                    .nome("Novo Nome da Silva")
                    .periodoConclusao("2027.2")
                    .email("nome.silva@ccc.ufcg.edu.br")
                    .build();

            // Act
            String novaRespostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + alunoCriado1.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String novaRespostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + alunoCriado2.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String novaRespostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + alunoCriado3.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO3))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoModificado1 = objectMapper.readValue(novaRespostaJsonString1, Aluno.class);
            Aluno alunoModificado2 = objectMapper.readValue(novaRespostaJsonString2, Aluno.class);
            Aluno alunoModificado3 = objectMapper.readValue(novaRespostaJsonString3, Aluno.class);

            // Assert
            assertNotNull(alunoModificado1.getId());
            assertEquals(requestDTO1.getMatricula(), alunoModificado1.getMatricula());
            assertEquals(requestDTO1.getNome(), alunoModificado1.getNome());
            assertEquals(requestDTO1.getPeriodoConclusao(), alunoModificado1.getPeriodoConclusao());
            assertEquals(requestDTO1.getEmail(), alunoModificado1.getEmail());

            assertNotNull(alunoModificado2.getId());
            assertEquals(requestDTO2.getMatricula(), alunoModificado2.getMatricula());
            assertEquals(requestDTO2.getNome(), alunoModificado2.getNome());
            assertEquals(requestDTO2.getPeriodoConclusao(), alunoModificado2.getPeriodoConclusao());
            assertEquals(requestDTO2.getEmail(), alunoModificado2.getEmail());

            assertNotNull(alunoModificado3.getId());
            assertEquals(requestDTO3.getMatricula(), alunoModificado3.getMatricula());
            assertEquals(requestDTO3.getNome(), alunoModificado3.getNome());
            assertEquals(requestDTO3.getPeriodoConclusao(), alunoModificado3.getPeriodoConclusao());
            assertEquals(requestDTO3.getEmail(), alunoModificado3.getEmail());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao alterar um aluno com dados vazios")
        void alterarAlunoComDadosVaziosTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoCriado = objectMapper.readValue(respostaJsonString, Aluno.class);

            AlunoDTO requestVazia = AlunoDTO.builder()
                    .nome("")
                    .email("")
                    .matricula("")
                    .periodoConclusao("")
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + alunoCriado.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestVazia))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar a matrícula é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o nome do aluno é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o período de conclusão é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o email e obrigatorio.")));
        }


        @Test
        @DisplayName("Deve lançar uma exceção ao alterar um aluno com dados nulos")
        void alterarAlunoComDadosNulosTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoCriado = objectMapper.readValue(respostaJsonString, Aluno.class);

            AlunoDTO requestNula = AlunoDTO.builder()
                    .nome(null)
                    .email(null)
                    .matricula(null)
                    .periodoConclusao(null)
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + alunoCriado.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestNula))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar a matrícula é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o nome do aluno é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o período de conclusão é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o email e obrigatorio.")));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao alterar um aluno não cadastrado")
        void alterarAlunoNaoCadastradoTest() throws Exception {
            // Arrange
            AlunoDTO alunoModificado = AlunoDTO.builder()
                    .matricula("121210111")
                    .nome("Novo Nome da Silva")
                    .periodoConclusao("2045.2")
                    .email("nome.silva@ccc.ufcg.edu.br")
                    .build();

            String uri = URI_TEMPLATE + "/" + "99";

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .put(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoModificado))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Aluno não encontrado!", erro.getMessage());
        }

    }

    @Nested
    @DisplayName("Verificações de busca de aluno por parte do coordenador")
    class VerificacoesBuscaAluno {

        @Test
        @DisplayName("Deve buscar um aluno cadastrado")
        void buscarAlunoTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoCriado = objectMapper.readValue(respostaJsonString, Aluno.class);

            String uri = URI_TEMPLATE + "/" + alunoCriado.getId();

            // Act
            String novaRespostaJsonString = driver.perform(get(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoResultado = objectMapper.readValue(novaRespostaJsonString, Aluno.class);

            // Assert
            assertEquals(alunoResultado.getId(), alunoCriado.getId());
        }

        @Test
        @DisplayName("Deve buscar todos os alunos cadastrados")
        void buscarTodosAlunosTest() throws Exception {
            // Arrange
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders.
                            post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoCriado1 = objectMapper.readValue(respostaJsonString1, Aluno.class);
            Aluno alunoCriado2 = objectMapper.readValue(respostaJsonString2, Aluno.class);

            // Act
            String respostaJsonString3 = driver.perform(get(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Collection<Aluno> alunosResultado = objectMapper.readValue(respostaJsonString3, new TypeReference<>() {
            });

            // Asserts
            assertTrue(alunosResultado
                    .stream()
                    .map(Aluno::getId)
                    .anyMatch(id -> id.equals(alunoCriado1.getId())));

            assertTrue(alunosResultado
                    .stream()
                    .map(Aluno::getId)
                    .anyMatch(id -> id.equals(alunoCriado2.getId())));
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia quando não há alunos cadastrados e se busca por todos")
        void buscarTodosAlunosListaVazia() throws Exception {
            // Arrange
            // Act
            String respostaJsonString = driver.perform(get(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Set<Aluno> alunosResultado = objectMapper.readValue(respostaJsonString, new TypeReference<>() {
            });

            // Asserts
            assertEquals(new HashSet<>(), alunosResultado);
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao buscar por um aluno não cadastrado")
        void buscarAlunoNaoCadastradoTest() throws Exception {
            // Arrange
            String uri = URI_TEMPLATE + "/" + "99";

            // Act
            String respostaErroJsonString = driver.perform(get(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Aluno não encontrado!", erro.getMessage());
        }

    }

    @Nested
    @DisplayName("Verificações de exclusão de aluno por parte do coordenador")
    class VerificacoesExclusaoAluno {

        @Test
        @DisplayName("Deve excluir um aluno cadastrado")
        void excluirAlunoCadastradoTest() throws Exception {
            // Arrage
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoCriado = objectMapper.readValue(respostaJsonString, Aluno.class);

            String uri = URI_TEMPLATE + "/" + alunoCriado.getId();

            // Act
            String novaRespostaJsonString = driver.perform(delete(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(novaRespostaJsonString.isBlank());
        }

        @Test
        @DisplayName("Deve excluir mais de um aluno cadastrado")
        void excluirMaisDeUmAlunoCadastradoTest() throws Exception {
            // Arrage
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(alunoDTO3))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Aluno alunoCriado1 = objectMapper.readValue(respostaJsonString1, Aluno.class);
            Aluno alunoCriado2 = objectMapper.readValue(respostaJsonString2, Aluno.class);
            Aluno alunoCriado3 = objectMapper.readValue(respostaJsonString3, Aluno.class);

            String uri1 = URI_TEMPLATE + "/" + alunoCriado1.getId();
            String uri2 = URI_TEMPLATE + "/" + alunoCriado2.getId();
            String uri3 = URI_TEMPLATE + "/" + alunoCriado3.getId();

            // Act
            String novaRespostaJsonString1 = driver.perform(delete(uri1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String novaRespostaJsonString2 = driver.perform(delete(uri2)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String novaRespostaJsonString3 = driver.perform(delete(uri3)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(novaRespostaJsonString1.isBlank());
            assertTrue(novaRespostaJsonString2.isBlank());
            assertTrue(novaRespostaJsonString3.isBlank());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao exluir um aluno não cadastrado")
        void excluirAlunoNaoCadastradoTest() throws Exception {
            // Arrage
            String uri = URI_TEMPLATE + "/" + "99";

            // Act
            String respostaErroJsonString = driver.perform(delete(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertEquals(erro.getMessage(), "Aluno não encontrado!");
        }

    }

}
