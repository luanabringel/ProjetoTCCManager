package com.ufcg.psoft.tccmanager.controller.coordenador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.tccmanager.dto.coordenador.CoordenadorDTO;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import com.ufcg.psoft.tccmanager.repository.CoordenadorRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.core.type.TypeReference;

import com.ufcg.psoft.tccmanager.repository.AreaDeEstudoRepository;
import com.ufcg.psoft.tccmanager.dto.areaDeEstudo.AreaDeEstudoDTO;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.ufcg.psoft.tccmanager.exception.CustomErrorType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Classe de teste de Área de Estudo em CoordenadorController")
public class CoordenadorControllerAreaDeEstudoTests {

    private final String URI_TEMPLATE = "/coordenador/areasDeEstudo";

    @Autowired
    MockMvc driver;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AreaDeEstudoRepository areaDeEstudoRepository;

    @Autowired
    CoordenadorRepository coordenadorRepository;

    AreaDeEstudoDTO areaDeEstudoDTO1, areaDeEstudoDTO2, areaDeEstudoDTO3;

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

        areaDeEstudoDTO1 = AreaDeEstudoDTO.builder()
                .nome("Banco de Dados")
                .build();

        areaDeEstudoDTO2 = AreaDeEstudoDTO.builder()
                .nome("Inteligência Artificial")
                .build();

        areaDeEstudoDTO3 = AreaDeEstudoDTO.builder()
                .nome("Análise de Dados")
                .build();
    }

    @AfterEach
    public void tearDown() {
        this.areaDeEstudoRepository.deleteAll();
        this.coordenadorRepository.deleteAll();
    }


    @Nested
    @DisplayName("Verificações de cadastro de área de estudo por parte do coordenador")
    class VerificacoesCadastroAreaEstudo {

        @Test
        @DisplayName("Deve cadastar uma área de estudo válida")
        void cadastrarAreaValidaTest() throws Exception {
            // Arrange
            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaCadastrada = objectMapper.readValue(respostaJsonString, AreaDeEstudo.class);

            // Assert
            assertNotNull(areaCadastrada.getId());
            assertEquals(areaDeEstudoDTO1.getNome(), areaCadastrada.getNome());
        }


        @Test
        @DisplayName("Deve cadastar mais de uma área de estudo válida")
        void cadastrarMaisDeUmaAreaValidaTest() throws Exception {
            // Arrange
            // Act
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO3))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaCadastrada1 = objectMapper.readValue(respostaJsonString1, AreaDeEstudo.class);
            AreaDeEstudo areaCadastrada2 = objectMapper.readValue(respostaJsonString2, AreaDeEstudo .class);
            AreaDeEstudo areaCadastrada3 = objectMapper.readValue(respostaJsonString3, AreaDeEstudo .class);

            // Assert
            assertNotNull(areaCadastrada1.getId());
            assertEquals(areaDeEstudoDTO1.getNome(), areaCadastrada1.getNome());

            assertNotNull(areaCadastrada2.getId());
            assertEquals(areaDeEstudoDTO2.getNome(), areaCadastrada2.getNome());

            assertNotNull(areaCadastrada3.getId());
            assertEquals(areaDeEstudoDTO3.getNome(), areaCadastrada3.getNome());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao cadastrar uma área de estudo com dados vazios")
        void cadastrarAreaComDadosVaziosTest() throws Exception {
            // Arrange
            AreaDeEstudoDTO requestVazia = AreaDeEstudoDTO.builder()
                    .nome("")
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
                    (msg) -> msg.contains("Informar o nome da área de estudo é obrigatório.")));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao cadastrar uma área de estudo com dados nulos")
        void cadastrarAreaComDadosNulosTest() throws Exception {
            // Arrange
            AreaDeEstudoDTO requestNula = AreaDeEstudoDTO.builder()
                    .nome(null)
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
                    (msg) -> msg.contains("Informar o nome da área de estudo é obrigatório.")));
        }

    }

    @Nested
    @DisplayName("Verificações de alteração da área de estudo por parte do coordenador")
    class VerificacoesAlteracaoAreaDeEstudo {

        @Test
        @DisplayName("Deve alterar uma área de estudo com dados válidos")
        void alterarAreaDadosValidosTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaCriada = objectMapper.readValue(respostaJsonString, AreaDeEstudo.class);

            AreaDeEstudoDTO requestDTO = AreaDeEstudoDTO.builder()
                    .nome("Novo Nome da Silva")
                    .build();

            // Act
            String novaRespostaJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + areaCriada.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaModificada = objectMapper.readValue(novaRespostaJsonString, AreaDeEstudo.class);

            // Assert
            assertNotNull(areaModificada.getId());
            assertEquals(requestDTO.getNome(), areaModificada.getNome());
        }

        @Test
        @DisplayName("Deve alterar mais de uma área de estudo com dados válidos")
        void alterarMaisDeUmaAreaDadosValidosTest() throws Exception {
            // Arrange
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO3))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaCriada1 = objectMapper.readValue(respostaJsonString1, AreaDeEstudo.class);
            AreaDeEstudo areaCriada2 = objectMapper.readValue(respostaJsonString2, AreaDeEstudo.class);
            AreaDeEstudo areaCriada3 = objectMapper.readValue(respostaJsonString3, AreaDeEstudo.class);

            AreaDeEstudoDTO requestDTO1 = AreaDeEstudoDTO.builder()
                    .nome("Novo Nome da Silva")
                    .build();

            AreaDeEstudoDTO requestDTO2 = AreaDeEstudoDTO.builder()
                    .nome("Novo Nome da Silva")
                    .build();

            AreaDeEstudoDTO requestDTO3 = AreaDeEstudoDTO.builder()
                    .nome("Novo Nome da Silva")
                    .build();

            // Act
            String novaRespostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + areaCriada1.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String novaRespostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + areaCriada2.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String novaRespostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + areaCriada3.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestDTO3))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaModificada1 = objectMapper.readValue(novaRespostaJsonString1, AreaDeEstudo.class);
            AreaDeEstudo areaModificada2 = objectMapper.readValue(novaRespostaJsonString2, AreaDeEstudo.class);
            AreaDeEstudo areaModificada3 = objectMapper.readValue(novaRespostaJsonString3, AreaDeEstudo.class);

            // Assert
            assertNotNull(areaModificada1.getId());
            assertEquals(requestDTO1.getNome(), areaModificada1.getNome());

            assertNotNull(areaModificada2.getId());
            assertEquals(requestDTO2.getNome(), areaModificada2.getNome());

            assertNotNull(areaModificada3.getId());
            assertEquals(requestDTO3.getNome(), areaModificada3.getNome());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao alterar uma área de estudo com dados vazios")
        void alterarAreaComDadosVaziosTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaCriada = objectMapper.readValue(respostaJsonString, AreaDeEstudo.class);

            AreaDeEstudoDTO requestVazia = AreaDeEstudoDTO.builder()
                    .nome("")
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + areaCriada.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestVazia))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o nome da área de estudo é obrigatório.")));
        }


        @Test
        @DisplayName("Deve lançar uma exceção ao alterar uma área de estudo com dados nulos")
        void alterarAreaComDadosNulosTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaCriada = objectMapper.readValue(respostaJsonString, AreaDeEstudo.class);

            AreaDeEstudoDTO requestNula = AreaDeEstudoDTO.builder()
                    .nome(null)
                    .build();

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .put(URI_TEMPLATE + "/" + areaCriada.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestNula))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType erroResultado = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertTrue(erroResultado.getErrors().stream().anyMatch(
                    (msg) -> msg.contains("Informar o nome da área de estudo é obrigatório.")));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao alterar uma área de estudo não cadastrada")
        void alterarAreaNaoCadastradaTest() throws Exception {
            // Arrange
            AreaDeEstudoDTO areaModificada = AreaDeEstudoDTO.builder()
                    .nome("Novo Nome da Silva")
                    .build();

            String uri = URI_TEMPLATE + "/" + "99";

            // Act
            String respostaErroJsonString = driver.perform(MockMvcRequestBuilders
                            .put(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaModificada))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaErroJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Área de Estudo não encontrada!", erro.getMessage());
        }

    }

    @Nested
    @DisplayName("Verificações de busca de área de estudo por parte do coordenador")
    class VerificacoesBuscaAreaDeEstudo {

        @Test
        @DisplayName("Deve buscar uma área de estudo cadastrada")
        void buscarAreaTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaCriada = objectMapper.readValue(respostaJsonString, AreaDeEstudo.class);

            String uri = URI_TEMPLATE + "/" + areaCriada.getId();

            // Act
            String novaRespostaJsonString = driver.perform(get(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaResultado = objectMapper.readValue(novaRespostaJsonString, AreaDeEstudo.class);

            // Assert
            assertEquals(areaResultado.getId(), areaCriada.getId());
        }

        @Test
        @DisplayName("Deve buscar todas as áreas de estudo cadastradas")
        void buscarTodasAreasTest() throws Exception {
            // Arrange
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders.
                            post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaCriada1 = objectMapper.readValue(respostaJsonString1, AreaDeEstudo.class);
            AreaDeEstudo areaCriada2 = objectMapper.readValue(respostaJsonString2, AreaDeEstudo.class);

            // Act
            String respostaJsonString3 = driver.perform(get(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Collection<AreaDeEstudo> areasResultado = objectMapper.readValue(respostaJsonString3, new TypeReference<>() {
            });

            // Asserts
            assertTrue(areasResultado
                    .stream()
                    .map(AreaDeEstudo::getId)
                    .anyMatch(id -> id.equals(areaCriada1.getId())));

            assertTrue(areasResultado
                    .stream()
                    .map(AreaDeEstudo::getId)
                    .anyMatch(id -> id.equals(areaCriada2.getId())));
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia quando não há áreas de estudo cadastradas e se busca por todas")
        void buscarTodasAreasListaVazia() throws Exception {
            // Arrange
            // Act
            String respostaJsonString = driver.perform(get(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Set<AreaDeEstudo> areasResultado = objectMapper.readValue(respostaJsonString, new TypeReference<>() {
            });

            // Asserts
            assertEquals(new HashSet<>(), areasResultado);
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao buscar por uma área de estudo não cadastrada")
        void buscarAreaNaoCadastradaTest() throws Exception {
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
            assertEquals("Área de Estudo não encontrada!", erro.getMessage());
        }

    }

    @Nested
    @DisplayName("Verificações de exclusão de área de estudo por parte do coordenador")
    class VerificacoesExclusaoAreaDeEstudo {

        @Test
        @DisplayName("Deve excluir uma área de estudo cadastrada")
        void excluirAreaCadastradaTest() throws Exception {
            // Arrage
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaCriada = objectMapper.readValue(respostaJsonString, AreaDeEstudo.class);

            String uri = URI_TEMPLATE + "/" + areaCriada.getId();

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
        @DisplayName("Deve excluir mais de uma área de estudo cadastrada")
        void excluirMaisDeUmaAreaCadastradaTest() throws Exception {
            // Arrage
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO1))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO2))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(areaDeEstudoDTO3))
                            .header("senhaCoordenador", coordenadorDTO.getSenha()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AreaDeEstudo areaCriada1 = objectMapper.readValue(respostaJsonString1, AreaDeEstudo.class);
            AreaDeEstudo areaCriada2 = objectMapper.readValue(respostaJsonString2, AreaDeEstudo.class);
            AreaDeEstudo areaCriada3 = objectMapper.readValue(respostaJsonString3, AreaDeEstudo.class);

            String uri1 = URI_TEMPLATE + "/" + areaCriada1.getId();
            String uri2 = URI_TEMPLATE + "/" + areaCriada2.getId();
            String uri3 = URI_TEMPLATE + "/" + areaCriada3.getId();

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
        @DisplayName("Deve lançar uma exceção ao exluir uma área de estudo não cadastrada")
        void excluirAreaNaoCadastradaTest() throws Exception {
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
            assertEquals(erro.getMessage(), "Área de Estudo não encontrada!");
        }

    }

}
