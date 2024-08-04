package com.ufcg.psoft.tccmanager.controller.coordenador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.tccmanager.dto.coordenador.CoordenadorDTO;
import com.ufcg.psoft.tccmanager.exception.CustomErrorType;
import com.ufcg.psoft.tccmanager.model.Coordenador;
import com.ufcg.psoft.tccmanager.repository.CoordenadorRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Classe de teste de cadastro de coordenador em CoordenadorController")
public class CoordenadorControllerAdministradorTests {

    private final String URI_TEMPLATE = "/coordenador/administrador";

    @Autowired
    MockMvc driver;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CoordenadorRepository coordenadorRepository;

    CoordenadorDTO coordenadorDTO1, coordenadorDTO2;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        coordenadorDTO1 = CoordenadorDTO.builder()
                .nome("Fubica Brasileiro")
                .email("fubica@ccc.ufcg.edu.br")
                .senha("fubica123")
                .build();

        coordenadorDTO2 = CoordenadorDTO.builder()
                .nome("Dalton Serey")
                .email("dalton@ccc.ufcg.edu.br")
                .senha("dalton123")
                .build();
    }

    @AfterEach
    void tearDown() {
        this.coordenadorRepository.deleteAll();
    }

    @Nested
    @DisplayName("Verificações de cadastro de coordenador")
    class VerificacoesCadastroCoordenador {

        @Test
        @DisplayName("Deve cadastar um coordenador válido")
        void cadastrarCoordenadorValidoTest() throws Exception {
            // Arrange
            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(coordenadorDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Coordenador coordenadorCadastrado = objectMapper.readValue(respostaJsonString, Coordenador.class);

            // Assert
            assertNotNull(coordenadorCadastrado.getId());
            assertEquals(coordenadorDTO1.getNome(), coordenadorCadastrado.getNome());
            assertEquals(coordenadorDTO1.getEmail(), coordenadorCadastrado.getEmail());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao cadastrar um coordenador com dados vazios")
        void cadastrarCoordenadorComDadosVaziosTest() throws Exception {
            // Arrange
            CoordenadorDTO requestVazia = CoordenadorDTO.builder()
                    .nome("")
                    .email("")
                    .senha("")
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestVazia)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o nome do coordenador é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o email é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar a senha é obrigatório.")));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao cadastrar um coordenador com dados nulos")
        void cadastrarCoordenadorComDadosNulosTest() throws Exception {
            // Arrange
            CoordenadorDTO requestVazia = CoordenadorDTO.builder()
                    .nome(null)
                    .email(null)
                    .senha(null)
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestVazia)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o nome do coordenador é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o email é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar a senha é obrigatório.")));
        }

        @Test
        @DisplayName("Deve lançar uma exceção cadastar mais de um coordenador no sistema")
        void cadastrarMaisDeUmCoordenadorTest() throws Exception {
            // Arrange
            // Act
            driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(coordenadorDTO1)))
                    .andExpect(status().isCreated());

            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(coordenadorDTO2)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Só pode haver um coordenador cadastrado no sistema.", erroResultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Verificações de alteração do coordenador")
    class VerificacoesAlteracaoCoordenador {

        @Test
        @DisplayName("Deve alterar o coordenador com dados válidos")
        void alterarCoordenadorDadosValidosTest() throws Exception {
            // Arrange
            driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(coordenadorDTO1)))
                    .andExpect(status().isCreated());

            CoordenadorDTO requestDTO = CoordenadorDTO.builder()
                    .nome("Novo Nome da Silva")
                    .email("nome.silva@ccc.ufcg.edu.br")
                    .senha("fulano123")
                    .build();

            // Act
            String novaRespostaJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Coordenador coordenadorModificado = objectMapper.readValue(novaRespostaJsonString, Coordenador.class);

            // Assert
            assertNotNull(coordenadorModificado.getId());
            assertEquals(requestDTO.getNome(), coordenadorModificado.getNome());
            assertEquals(requestDTO.getEmail(), coordenadorModificado.getEmail());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao alterar o coordenador com dados vazios")
        void alterarCoordenadorComDadosVaziosTest() throws Exception {
            // Arrange
            driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(coordenadorDTO2)))
                    .andExpect(status().isCreated());

            CoordenadorDTO requestVazia = CoordenadorDTO.builder()
                    .nome("")
                    .email("")
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestVazia)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o nome do coordenador é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o email é obrigatório.")));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao alterar o coordenador com dados nulos")
        void alterarCoordenadorComDadosNulosTest() throws Exception {
            // Arrange
            driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(coordenadorDTO2)))
                    .andExpect(status().isCreated());

            CoordenadorDTO requestVazia = CoordenadorDTO.builder()
                    .nome(null)
                    .email(null)
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestVazia)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o nome do coordenador é obrigatório.")));
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o email é obrigatório.")));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao alterar um coordenador não cadastrado")
        void alterarCoordenadorNaoCadastradoTest() throws Exception {
            // Arrange
            CoordenadorDTO coordenadorModificado = CoordenadorDTO.builder()
                    .nome("Novo Nome da Silva")
                    .email("nome.silva@ccc.ufcg.edu.br")
                    .senha("fulano123")
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(coordenadorModificado)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O coordenador ainda não foi cadastrado no sistema.", erro.getMessage());
        }
    }
}
