package com.ufcg.psoft.tccmanager.controller.coordenador;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.tccmanager.dto.coordenador.CoordenadorDTO;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorDTO;
import com.ufcg.psoft.tccmanager.exception.CustomErrorType;
import com.ufcg.psoft.tccmanager.model.Professor;
import com.ufcg.psoft.tccmanager.repository.CoordenadorRepository;
import com.ufcg.psoft.tccmanager.repository.ProfessorRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Classe de teste de Professor em CoordenadorController")
public class CoordenadorControllerProfessorTests {

    private final String URI_TEMPLATE = "/coordenador/professores";

    @Autowired
    MockMvc driver;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    CoordenadorRepository coordenadorRepository;

    ProfessorDTO professorDTO1, professorDTO2, professorDTO3;

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

        List<String> laboratorios = new ArrayList<>();
        laboratorios.add("LSD");
        laboratorios.add("SPLAB");

        professorDTO1 = ProfessorDTO.builder()
                .nome("Raimundo Nonato")
                .email("raimundo.nonato@gmail.com")
                .labs(laboratorios)
                .build();

        professorDTO2 = ProfessorDTO.builder()
                .nome("Charles Xavier")
                .email("charles.xavier@gmail.com")
                .labs(laboratorios)
                .build();

        professorDTO3 = ProfessorDTO.builder()
                .nome("Albus Dumbledore ")
                .email("albus.dumbledore@gmail.com")
                .labs(laboratorios)
                .build();
    }

    @AfterEach
    public void tearDown() {
        this.professorRepository.deleteAll();
        this.coordenadorRepository.deleteAll();
    }

    @Nested
    @DisplayName("Verificações de cadastro de professor por parte do coordenador")
    class VerificacoesCadastroProfessor {

        @Test
        @DisplayName("Deve cadastar um professor válido")
        void cadastrarProfessorValidoTest() throws Exception {
            // Arrange
            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorCadastrado = objectMapper.readValue(respostaJsonString, Professor.class);

            // Assert
            assertNotNull(professorCadastrado.getId());
            assertEquals(professorDTO1.getNome(), professorCadastrado.getNome());
            assertEquals(professorDTO1.getEmail(), professorCadastrado.getEmail());
            assertEquals(professorDTO1.getLabs(), professorCadastrado.getLabs());
        }

        @Test
        @DisplayName("Deve cadastrar um professor sem laboratórios")
        void cadastrarProfessorSemLaboratoriosTest() throws Exception {
            // Arrange
            ProfessorDTO novoProfessorDTO = ProfessorDTO.builder()
                    .nome("Mestre dos Magos")
                    .email("mestre.magos@gmail.com")
                    .build();

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(novoProfessorDTO))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorCadastrado = objectMapper.readValue(respostaJsonString, Professor.class);

            // Assert
            assertNotNull(professorCadastrado.getId());
            assertEquals(novoProfessorDTO.getNome(), professorCadastrado.getNome());
            assertEquals(novoProfessorDTO.getEmail(), professorCadastrado.getEmail());
            assertTrue(professorCadastrado.getLabs().isEmpty());
        }

        @Test
        @DisplayName("Deve cadastar mais de um professor válido")
        void cadastrarMaisDeUmProfessorValidoTest() throws Exception {
            // Arrange
            // Act
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO3))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorCadastrado1 = objectMapper.readValue(respostaJsonString1, Professor.class);
            Professor professorCadastrado2 = objectMapper.readValue(respostaJsonString2, Professor.class);
            Professor professorCadastrado3 = objectMapper.readValue(respostaJsonString3, Professor.class);

            // Assert
            assertNotNull(professorCadastrado1.getId());
            assertEquals(professorDTO1.getNome(), professorCadastrado1.getNome());
            assertEquals(professorDTO1.getEmail(), professorCadastrado1.getEmail());
            assertEquals(professorDTO1.getLabs(), professorCadastrado1.getLabs());

            assertNotNull(professorCadastrado2.getId());
            assertEquals(professorDTO2.getNome(), professorCadastrado2.getNome());
            assertEquals(professorDTO2.getEmail(), professorCadastrado2.getEmail());
            assertEquals(professorDTO2.getLabs(), professorCadastrado1.getLabs());

            assertNotNull(professorCadastrado3.getId());
            assertEquals(professorDTO3.getNome(), professorCadastrado3.getNome());
            assertEquals(professorDTO3.getEmail(), professorCadastrado3.getEmail());
            assertEquals(professorDTO3.getLabs(), professorCadastrado1.getLabs());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao cadastrar um professor com dados vazios")
        void cadastrarProfessorComDadosVaziosTest() throws Exception {
            // Arrange
            ProfessorDTO requestVazia = ProfessorDTO.builder()
                    .nome("")
                    .email("")
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
                    (msg) -> msg.contains("Informar o nome do professor é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o email do professor é obrigatório.")));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao cadastrar um professor com dados nulos")
        void cadastrarProfessorComDadosNulosTest() throws Exception {
            // Arrange
            ProfessorDTO requestNula = ProfessorDTO.builder()
                    .nome(null)
                    .email(null)
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
                    (msg) -> msg.contains("Informar o nome do professor é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o email do professor é obrigatório.")));
        }
    }

    @Nested
    @DisplayName("Verificações de alteração do professor por parte do coordenador")
    class VerificacoesAlteracaoProfessor {

        @Test
        @DisplayName("Deve alterar um professor com dados válidos")
        void alterarProfessorDadosValidosTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorCriado = objectMapper.readValue(respostaJsonString, Professor.class);

            ProfessorDTO requestDTO = ProfessorDTO.builder()
                    .nome("Novo Nome da Silva")
                    .email("nome.silva@ccc.ufcg.edu.br")
                    .build();

            // Act
            String novaRespostaJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + professorCriado.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorModificado = objectMapper.readValue(novaRespostaJsonString, Professor.class);

            // Assert
            assertNotNull(professorModificado.getId());
            assertEquals(requestDTO.getNome(), professorModificado.getNome());
            assertEquals(requestDTO.getEmail(), professorModificado.getEmail());
            assertEquals(requestDTO.getLabs(), professorModificado.getLabs());
        }

        @Test
        @DisplayName("Deve alterar mais de um professor com dados válidos")
        void alterarMaisDeUmProfessorDadosValidosTest() throws Exception {
            // Arrange
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO3))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorCriado1 = objectMapper.readValue(respostaJsonString1, Professor.class);
            Professor professorCriado2 = objectMapper.readValue(respostaJsonString2, Professor.class);
            Professor professorCriado3 = objectMapper.readValue(respostaJsonString3, Professor.class);

            ProfessorDTO requestDTO1 = ProfessorDTO.builder()
                    .nome("Novo Nome da Silva")
                    .email("nome.silva@ccc.ufcg.edu.br")
                    .build();

            ProfessorDTO requestDTO2 = ProfessorDTO.builder()
                    .nome("Novo Nome da Silva")
                    .email("nome.silva@ccc.ufcg.edu.br")
                    .build();

            ProfessorDTO requestDTO3 = ProfessorDTO.builder()
                    .nome("Novo Nome da Silva")
                    .email("nome.silva@ccc.ufcg.edu.br")
                    .build();

            // Act
            String novaRespostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + professorCriado1.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String novaRespostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + professorCriado2.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String novaRespostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + professorCriado3.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO3))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorModificado1 = objectMapper.readValue(novaRespostaJsonString1, Professor.class);
            Professor professorModificado2 = objectMapper.readValue(novaRespostaJsonString2, Professor.class);
            Professor professorModificado3 = objectMapper.readValue(novaRespostaJsonString3, Professor.class);

            // Assert
            assertNotNull(professorModificado1.getId());
            assertEquals(requestDTO1.getNome(), professorModificado1.getNome());
            assertEquals(requestDTO1.getEmail(), professorModificado1.getEmail());
            assertEquals(requestDTO1.getLabs(), professorModificado1.getLabs());

            assertNotNull(professorModificado2.getId());
            assertEquals(requestDTO2.getNome(), professorModificado2.getNome());
            assertEquals(requestDTO2.getEmail(), professorModificado2.getEmail());
            assertEquals(requestDTO2.getLabs(), professorModificado2.getLabs());

            assertNotNull(professorModificado3.getId());
            assertEquals(requestDTO3.getNome(), professorModificado3.getNome());
            assertEquals(requestDTO3.getEmail(), professorModificado3.getEmail());
            assertEquals(requestDTO3.getLabs(), professorModificado3.getLabs());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao alterar um professor com dados vazios")
        void alterarProfessorComDadosVaziosTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorCriado = objectMapper.readValue(respostaJsonString, Professor.class);

            ProfessorDTO requestVazia = ProfessorDTO.builder()
                    .nome("")
                    .email("")
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + professorCriado.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestVazia))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o nome do professor é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o email do professor é obrigatório.")));
        }


        @Test
        @DisplayName("Deve lançar uma exceção ao alterar um professor com dados nulos")
        void alterarProfessorComDadosNulosTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorCriado = objectMapper.readValue(respostaJsonString, Professor.class);

            ProfessorDTO requestNula = ProfessorDTO.builder()
                    .nome(null)
                    .email(null)
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + professorCriado.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestNula))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o nome do professor é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o email do professor é obrigatório.")));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao alterar um professor não cadastrado")
        void alterarProfessorNaoCadastradoTest() throws Exception {
            // Arrange
            ProfessorDTO professorModificado = ProfessorDTO.builder()
                    .nome("Novo Nome da Silva")
                    .email("nome.silva@ccc.ufcg.edu.br")
                    .build();

            String uri = URI_TEMPLATE + "/" + "99";

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .put(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorModificado))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Professor não encontrado!", erro.getMessage());
        }

    }

    @Nested
    @DisplayName("Verificações de busca de professor por parte do coordenador")
    class VerificacoesBuscaProfessor {

        @Test
        @DisplayName("Deve buscar um professor cadastrado")
        void buscarProfessorTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorCriado = objectMapper.readValue(respostaJsonString, Professor.class);

            String uri = URI_TEMPLATE + "/" + professorCriado.getId();

            // Act
            String novaRespostaJsonString = driver.perform(get(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorResultado = objectMapper.readValue(novaRespostaJsonString, Professor.class);

            // Assert
            assertEquals(professorResultado.getId(), professorCriado.getId());
        }

        @Test
        @DisplayName("Deve buscar todos os professores cadastrados")
        void buscarTodosProfessoresTest() throws Exception {
            // Arrange
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders.
                            post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorCriado1 = objectMapper.readValue(respostaJsonString1, Professor.class);
            Professor professorCriado2 = objectMapper.readValue(respostaJsonString2, Professor.class);

            // Act
            String respostaJsonString3 = driver.perform(get(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Collection<Professor> professoresResultado = objectMapper.readValue(respostaJsonString3, new TypeReference<>() {
            });

            // Asserts
            assertTrue(professoresResultado
                    .stream()
                    .map(Professor::getId)
                    .anyMatch(id -> id.equals(professorCriado1.getId())));

            assertTrue(professoresResultado
                    .stream()
                    .map(Professor::getId)
                    .anyMatch(id -> id.equals(professorCriado2.getId())));
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia quando não há professores cadastrados e se busca por todos")
        void buscarTodosProfessoresListaVazia() throws Exception {
            // Arrange
            // Act
            String respostaJsonString = driver.perform(get(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Set<Professor> professoresResultado = objectMapper.readValue(respostaJsonString, new TypeReference<>() {
            });

            // Asserts
            assertEquals(new HashSet<>(), professoresResultado);
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao buscar por um professor não cadastrado")
        void buscarProfessorNaoCadastradoTest() throws Exception {
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
            assertEquals("Professor não encontrado!", erro.getMessage());
        }

    }

    @Nested
    @DisplayName("Verificações de exclusão de professor por parte do coordenador")
    class VerificacoesExclusaoProfessor {

        @Test
        @DisplayName("Deve excluir um professor cadastrado")
        void excluirProfessorCadastradoTest() throws Exception {
            // Arrage
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorCriado = objectMapper.readValue(respostaJsonString, Professor.class);

            String uri = URI_TEMPLATE + "/" + professorCriado.getId();

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
        @DisplayName("Deve excluir mais de um professor cadastrado")
        void excluirMaisDeUmProfessorCadastradoTest() throws Exception {
            // Arrage
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(professorDTO3))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorCriado1 = objectMapper.readValue(respostaJsonString1, Professor.class);
            Professor professorCriado2 = objectMapper.readValue(respostaJsonString2, Professor.class);
            Professor professorCriado3 = objectMapper.readValue(respostaJsonString3, Professor.class);

            String uri1 = URI_TEMPLATE + "/" + professorCriado1.getId();
            String uri2 = URI_TEMPLATE + "/" + professorCriado2.getId();
            String uri3 = URI_TEMPLATE + "/" + professorCriado3.getId();

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
        @DisplayName("Deve lançar uma exceção ao exluir um professor não cadastrado")
        void excluirProfessorNaoCadastradoTest() throws Exception {
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
            assertEquals(erro.getMessage(), "Professor não encontrado!");
        }

    }
}
