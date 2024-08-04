package com.ufcg.psoft.tccmanager.controller.aluno;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.tccmanager.dto.aluno.AlunoInteresseAreaResponseDTO;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorGetInteressadosPorAlunoDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCProfessorAlunoGetAllDTO;
import com.ufcg.psoft.tccmanager.events.EventManager;
import com.ufcg.psoft.tccmanager.exception.AlunoNotFoundException;
import com.ufcg.psoft.tccmanager.model.enums.StatusTCCEnum;
import com.ufcg.psoft.tccmanager.exception.CustomErrorType;
import com.ufcg.psoft.tccmanager.exception.TemaNotFoundException;
import com.ufcg.psoft.tccmanager.model.*;
import com.ufcg.psoft.tccmanager.repository.AlunoRepository;
import com.ufcg.psoft.tccmanager.repository.AreaDeEstudoRepository;
import com.ufcg.psoft.tccmanager.repository.ProfessorRepository;
import com.ufcg.psoft.tccmanager.repository.TemaTCCRepository;
import com.ufcg.psoft.tccmanager.repository.SolicitacaoRepository;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Classe de teste de AlunoController")
public class AlunoControllerTests {

    private final String URI_TEMPLATE = "/alunos";

    @Autowired
    MockMvc driver;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AlunoRepository alunoRepository;

    @Autowired
    TemaTCCRepository temaTCCRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    AreaDeEstudoRepository areaDeEstudoRepository;

    @Autowired
    SolicitacaoRepository solicitacaoRepository;

    @Autowired
    EventManager eventManager;

    Aluno aluno1, aluno2, aluno3;

    AreaDeEstudo areaDeEstudo1, areaDeEstudo2, areaDeEstudo3;

    TemaTCCDTO temaTccDTO1, temaTccDTO2, temaTccDTO3;

    Professor professor1, professor2;

    SolicitacaoDTO solicitacaoDto;

    @BeforeEach
    void setup() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        aluno1 = alunoRepository.save(Aluno.builder()
                .nome("Fulano da Silva")
                .matricula("121210999")
                .email("fulano.silva@gmail.com")
                .periodoConclusao("2024.2")
                .build());

        aluno2 = alunoRepository.save(Aluno.builder()
                .nome("Flavinho do Pneu")
                .matricula("121210888")
                .email("flavinho.pneu@gmail.com")
                .periodoConclusao("2027.1")
                .build());

        aluno3 = alunoRepository.save(Aluno.builder()
                .nome("Jorginho Alemão")
                .matricula("121210777")
                .email("jorginho.alemao@gmail.com")
                .periodoConclusao("2025.2")
                .build());

        areaDeEstudo1 = areaDeEstudoRepository.save(AreaDeEstudo.builder()
                .nome("Banco de Dados")
                .build());
        eventManager.cadastrarAreaDeEstudo(areaDeEstudo1);

        areaDeEstudo2 = areaDeEstudoRepository.save(AreaDeEstudo.builder()
                .nome("Inteligência Artificial")
                .build());
        eventManager.cadastrarAreaDeEstudo(areaDeEstudo2);

        areaDeEstudo3 = areaDeEstudoRepository.save(AreaDeEstudo.builder()
                .nome("Análise de Dados")
                .build());
        eventManager.cadastrarAreaDeEstudo(areaDeEstudo3);

        professor1 = professorRepository.save(Professor.builder()
                .nome("Mestre dos Magos")
                .quota(5)
                .email("mestredosmagos@ccc.ufcg.edu.br")
                .build());

        professor2 = professorRepository.save(Professor.builder()
                .nome("Massoni")
                .quota(3)
                .email("massoni@ccc.ufcg.edu.br")
                .build());

        Set<AreaDeEstudo> areasDeEstudo = new HashSet<>();
        areasDeEstudo.add(areaDeEstudo1);
        areasDeEstudo.add(areaDeEstudo2);

        temaTccDTO1 = TemaTCCDTO.builder()
                .titulo("Otimização de Consultas em Banco de Dados Distribuídos")
                .descricao("Este trabalho propõe explorar como algoritmos...")
                .areasDeEstudo(areasDeEstudo)
                .build();

        temaTccDTO2 = TemaTCCDTO.builder()
                .titulo("Aplicação de Redes Neurais Convolucionais para Reconhecimento...")
                .descricao("Este estudo tem como objetivo investigar o uso de redes...")
                .areasDeEstudo(areasDeEstudo)
                .build();

        temaTccDTO3 = TemaTCCDTO.builder()
                .titulo("Mineração de Texto em Redes Sociais para Detecção...")
                .descricao("Este projeto propõe o uso de técnicas de mineração de...")
                .areasDeEstudo(areasDeEstudo)
                .build();
    }

    @AfterEach
    void tearDown() {
        solicitacaoRepository.deleteAll();
        alunoRepository.deleteAll();
        professorRepository.deleteAll();
        temaTCCRepository.deleteAll();
        areaDeEstudoRepository.deleteAll();
    }

    @Nested
    @DisplayName("Verificações de cadastro de tema de TCC por parte do aluno")
    class VerificacoesCadastroTemaTCC {

        @Test
        @DisplayName("Deve cadastrar um tema de TCC com uma área de estudo")
        void cadastrarTemaTccComUmaArea() throws Exception {
            // Arrange
            Set<AreaDeEstudo> areasDeEstudo = new HashSet<>();
            areasDeEstudo.add(areaDeEstudo1);

            temaTccDTO1.setAreasDeEstudo(areasDeEstudo);

            String uri = URI_TEMPLATE + "/" + aluno1.getId() + "/cadastrar-tema-tcc";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCC = objectMapper.readValue(respostaJsonString, TemaTCC.class);
            Aluno alunoCadastrado = alunoRepository.findById(aluno1.getId()).orElseThrow(AlunoNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), temaTCC.getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), temaTCC.getDescricao());
            assertEquals(areasDeEstudo, temaTCC.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC.getStatus());

            assertTrue(alunoCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC.getId())));
        }

        @Test
        @DisplayName("Deve cadastrar um tema de TCC com uma área de estudo para vários alunos")
        void cadastrarTemaTccComUmaAreaVariosAlunos() throws Exception {
            // Arrange
            Set<AreaDeEstudo> areasDeEstudo = new HashSet<>();
            areasDeEstudo.add(areaDeEstudo1);

            temaTccDTO1.setAreasDeEstudo(areasDeEstudo);
            temaTccDTO2.setAreasDeEstudo(areasDeEstudo);
            temaTccDTO3.setAreasDeEstudo(areasDeEstudo);

            String uri1 = URI_TEMPLATE + "/" + aluno1.getId() + "/cadastrar-tema-tcc";
            String uri2 = URI_TEMPLATE + "/" + aluno2.getId() + "/cadastrar-tema-tcc";
            String uri3 = URI_TEMPLATE + "/" + aluno3.getId() + "/cadastrar-tema-tcc";

            // Act
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uri1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(uri2)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO2)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(uri3)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO3)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCC1 = objectMapper.readValue(respostaJsonString1, TemaTCC.class);
            TemaTCC temaTCC2 = objectMapper.readValue(respostaJsonString2, TemaTCC.class);
            TemaTCC temaTCC3 = objectMapper.readValue(respostaJsonString3, TemaTCC.class);
            Aluno alunoCadastrado1 = alunoRepository.findById(aluno1.getId()).orElseThrow(AlunoNotFoundException::new);
            Aluno alunoCadastrado2 = alunoRepository.findById(aluno2.getId()).orElseThrow(AlunoNotFoundException::new);
            Aluno alunoCadastrado3 = alunoRepository.findById(aluno3.getId()).orElseThrow(AlunoNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), temaTCC1.getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), temaTCC1.getDescricao());
            assertEquals(areasDeEstudo, temaTCC1.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC1.getStatus());

            assertTrue(alunoCadastrado1.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC1.getId())));

            assertEquals(temaTccDTO2.getTitulo(), temaTCC2.getTitulo());
            assertEquals(temaTccDTO2.getDescricao(), temaTCC2.getDescricao());
            assertEquals(areasDeEstudo, temaTCC2.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC2.getStatus());

            assertTrue(alunoCadastrado2.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC2.getId())));

            assertEquals(temaTccDTO3.getTitulo(), temaTCC3.getTitulo());
            assertEquals(temaTccDTO3.getDescricao(), temaTCC3.getDescricao());
            assertEquals(areasDeEstudo, temaTCC3.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC3.getStatus());

            assertTrue(alunoCadastrado3.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC3.getId())));
        }

        @Test
        @DisplayName("Deve cadastrar um tema de TCC com mais de uma área de estudo")
        void cadastrarTemaTccMaisDeUmaArea() throws Exception {
            // Arrange
            Set<AreaDeEstudo> areasDeEstudo = new HashSet<>();
            areasDeEstudo.add(areaDeEstudo1);
            areasDeEstudo.add(areaDeEstudo2);
            areasDeEstudo.add(areaDeEstudo3);

            temaTccDTO1.setAreasDeEstudo(areasDeEstudo);

            String uri = URI_TEMPLATE + "/" + aluno1.getId() + "/cadastrar-tema-tcc";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCC = objectMapper.readValue(respostaJsonString, TemaTCC.class);
            Aluno alunoCadastrado = alunoRepository.findById(aluno1.getId()).orElseThrow(AlunoNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), temaTCC.getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), temaTCC.getDescricao());
            assertEquals(areasDeEstudo, temaTCC.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC.getStatus());

            assertTrue(alunoCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC.getId())));
        }

        @Test
        @DisplayName("Deve cadastrar um tema de TCC com mais de uma área de estudo para vários alunos")
        void cadastrarTemaTccMaisDeUmaAreaVariosAlunos() throws Exception {
            // Arrange
            Set<AreaDeEstudo> areasDeEstudo = new HashSet<>();
            areasDeEstudo.add(areaDeEstudo1);
            areasDeEstudo.add(areaDeEstudo2);
            areasDeEstudo.add(areaDeEstudo3);

            temaTccDTO1.setAreasDeEstudo(areasDeEstudo);
            temaTccDTO2.setAreasDeEstudo(areasDeEstudo);
            temaTccDTO3.setAreasDeEstudo(areasDeEstudo);

            String uri1 = URI_TEMPLATE + "/" + aluno1.getId() + "/cadastrar-tema-tcc";
            String uri2 = URI_TEMPLATE + "/" + aluno2.getId() + "/cadastrar-tema-tcc";
            String uri3 = URI_TEMPLATE + "/" + aluno3.getId() + "/cadastrar-tema-tcc";

            // Act
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uri1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(uri2)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO2)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(uri3)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO3)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCC1 = objectMapper.readValue(respostaJsonString1, TemaTCC.class);
            TemaTCC temaTCC2 = objectMapper.readValue(respostaJsonString2, TemaTCC.class);
            TemaTCC temaTCC3 = objectMapper.readValue(respostaJsonString3, TemaTCC.class);
            Aluno alunoCadastrado1 = alunoRepository.findById(aluno1.getId()).orElseThrow(AlunoNotFoundException::new);
            Aluno alunoCadastrado2 = alunoRepository.findById(aluno2.getId()).orElseThrow(AlunoNotFoundException::new);
            Aluno alunoCadastrado3 = alunoRepository.findById(aluno3.getId()).orElseThrow(AlunoNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), temaTCC1.getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), temaTCC1.getDescricao());
            assertEquals(areasDeEstudo, temaTCC1.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC1.getStatus());

            assertTrue(alunoCadastrado1.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC1.getId())));

            assertEquals(temaTccDTO2.getTitulo(), temaTCC2.getTitulo());
            assertEquals(temaTccDTO2.getDescricao(), temaTCC2.getDescricao());
            assertEquals(areasDeEstudo, temaTCC2.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC2.getStatus());

            assertTrue(alunoCadastrado2.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC2.getId())));

            assertEquals(temaTccDTO3.getTitulo(), temaTCC3.getTitulo());
            assertEquals(temaTccDTO3.getDescricao(), temaTCC3.getDescricao());
            assertEquals(areasDeEstudo, temaTCC3.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC3.getStatus());

            assertTrue(alunoCadastrado3.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC3.getId())));
        }

        @Test
        @DisplayName("Deve cadastrar mais de um tema de TCC com a mesma área de estudo")
        void cadastrarMaisDeUmTemaTccComUmaArea() throws Exception {
            // Arrange
            Set<AreaDeEstudo> areasDeEstudo = new HashSet<>();
            areasDeEstudo.add(areaDeEstudo1);

            temaTccDTO1.setAreasDeEstudo(areasDeEstudo);
            temaTccDTO2.setAreasDeEstudo(areasDeEstudo);
            temaTccDTO3.setAreasDeEstudo(areasDeEstudo);

            String uri = URI_TEMPLATE + "/" + aluno1.getId() + "/cadastrar-tema-tcc";

            // Act
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO2)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO3)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCC1 = objectMapper.readValue(respostaJsonString1, TemaTCC.class);
            TemaTCC temaTCC2 = objectMapper.readValue(respostaJsonString2, TemaTCC.class);
            TemaTCC temaTCC3 = objectMapper.readValue(respostaJsonString3, TemaTCC.class);
            Aluno alunoCadastrado = alunoRepository.findById(aluno1.getId()).orElseThrow(AlunoNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), temaTCC1.getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), temaTCC1.getDescricao());
            assertEquals(areasDeEstudo, temaTCC1.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC1.getStatus());

            assertTrue(alunoCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC1.getId())));

            assertEquals(temaTccDTO2.getTitulo(), temaTCC2.getTitulo());
            assertEquals(temaTccDTO2.getDescricao(), temaTCC2.getDescricao());
            assertEquals(areasDeEstudo, temaTCC2.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC2.getStatus());

            assertTrue(alunoCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC2.getId())));

            assertEquals(temaTccDTO3.getTitulo(), temaTCC3.getTitulo());
            assertEquals(temaTccDTO3.getDescricao(), temaTCC3.getDescricao());
            assertEquals(areasDeEstudo, temaTCC3.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC3.getStatus());

            assertTrue(alunoCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC3.getId())));
        }

        @Test
        @DisplayName("Deve cadastrar mais de um tema de TCC com áreas de estudo diferentes")
        void cadastrarMaisDeUmTemaTccComAreaDiferente() throws Exception {
            // Arrange
            Set<AreaDeEstudo> areasDeEstudo1 = new HashSet<>();
            areasDeEstudo1.add(areaDeEstudo1);
            areasDeEstudo1.add(areaDeEstudo2);

            Set<AreaDeEstudo> areasDeEstudo2 = new HashSet<>();
            areasDeEstudo1.add(areaDeEstudo3);

            Set<AreaDeEstudo> areasDeEstudo3 = new HashSet<>();
            areasDeEstudo1.add(areaDeEstudo1);
            areasDeEstudo1.add(areaDeEstudo3);

            temaTccDTO1.setAreasDeEstudo(areasDeEstudo1);
            temaTccDTO2.setAreasDeEstudo(areasDeEstudo2);
            temaTccDTO3.setAreasDeEstudo(areasDeEstudo3);

            String uri = URI_TEMPLATE + "/" + aluno1.getId() + "/cadastrar-tema-tcc";

            // Act
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO2)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO3)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCC1 = objectMapper.readValue(respostaJsonString1, TemaTCC.class);
            TemaTCC temaTCC2 = objectMapper.readValue(respostaJsonString2, TemaTCC.class);
            TemaTCC temaTCC3 = objectMapper.readValue(respostaJsonString3, TemaTCC.class);
            Aluno alunoCadastrado = alunoRepository.findById(aluno1.getId()).orElseThrow(AlunoNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), temaTCC1.getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), temaTCC1.getDescricao());
            assertEquals(areasDeEstudo1, temaTCC1.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC1.getStatus());

            assertTrue(alunoCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC1.getId())));

            assertEquals(temaTccDTO2.getTitulo(), temaTCC2.getTitulo());
            assertEquals(temaTccDTO2.getDescricao(), temaTCC2.getDescricao());
            assertEquals(areasDeEstudo2, temaTCC2.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC2.getStatus());

            assertTrue(alunoCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC2.getId())));

            assertEquals(temaTccDTO3.getTitulo(), temaTCC3.getTitulo());
            assertEquals(temaTccDTO3.getDescricao(), temaTCC3.getDescricao());
            assertEquals(areasDeEstudo3, temaTCC3.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC3.getStatus());

            assertTrue(alunoCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC3.getId())));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao cadastrar um tema de TCC com um aluno não cadastrado")
        void cadastrarTemaTccAlunoInvalido() throws Exception {
            // Arrange
            Set<AreaDeEstudo> areasDeEstudo = new HashSet<>();
            areasDeEstudo.add(areaDeEstudo1);
            temaTccDTO1.setAreasDeEstudo(areasDeEstudo);

            String uri = URI_TEMPLATE + "/" + "99" + "/cadastrar-tema-tcc";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Aluno não encontrado!", erro.getMessage());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao cadastrar um tema de TCC com uma área de estudo não cadastrada")
        void cadastrarTemaTccAreaNaoCadastrada() throws Exception {
            // Arrange
            AreaDeEstudo areaNaoCadastrada = AreaDeEstudo.builder()
                    .nome("Segurança da Informação")
                    .build();

            Set<AreaDeEstudo> areasDeEstudo = new HashSet<>();
            areasDeEstudo.add(areaNaoCadastrada);

            temaTccDTO1.setAreasDeEstudo(areasDeEstudo);

            String uri = URI_TEMPLATE + "/" + aluno1.getId() + "/cadastrar-tema-tcc";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Área de Estudo não encontrada!", erro.getMessage());
        }
    }

    @Nested
    @DisplayName("Verificações de adição de área de estudo interessada por parte do aluno")
    class VerificacoesAdicaoAreaDeEstudo {

        @Test
        @DisplayName("Deve adicionar uma área de estudo interessada")
        void adicionarAreaInteressada() throws Exception {
            // Arrange
            Set<AreaDeEstudo> expected = new HashSet<>();
            expected.add(areaDeEstudo1);

            String uri = URI_TEMPLATE + "/" + aluno1.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .patch(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AlunoInteresseAreaResponseDTO responseDTO = objectMapper.readValue(respostaJsonString, AlunoInteresseAreaResponseDTO.class);

            // Assert
            assertEquals(expected, responseDTO.getAreasDeEstudoInteressadas());
        }

        @Test
        @DisplayName("Deve adicionar uma mesma área de estudo interessada para vários alunos")
        void adicionarMesmaAreaInteressadaVariosAlunos() throws Exception {
            // Arrange
            Set<AreaDeEstudo> expected = new HashSet<>();
            expected.add(areaDeEstudo1);

            String uri1 = URI_TEMPLATE + "/" + aluno1.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";
            String uri2 = URI_TEMPLATE + "/" + aluno2.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";
            String uri3 = URI_TEMPLATE + "/" + aluno3.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";

            // Act
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .patch(uri1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .patch(uri2)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .patch(uri3)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AlunoInteresseAreaResponseDTO responseDTO1 = objectMapper.readValue(respostaJsonString1, AlunoInteresseAreaResponseDTO.class);
            AlunoInteresseAreaResponseDTO responseDTO2 = objectMapper.readValue(respostaJsonString2, AlunoInteresseAreaResponseDTO.class);
            AlunoInteresseAreaResponseDTO responseDTO3 = objectMapper.readValue(respostaJsonString3, AlunoInteresseAreaResponseDTO.class);

            // Assert
            assertEquals(expected, responseDTO1.getAreasDeEstudoInteressadas());
            assertEquals(expected, responseDTO2.getAreasDeEstudoInteressadas());
            assertEquals(expected, responseDTO3.getAreasDeEstudoInteressadas());
        }

        @Test
        @DisplayName("Deve adicionar mais de uma área de estudo interessada")
        void adicionarMaisDeUmaAreaInteressada() throws Exception {
            // Arrange
            Set<AreaDeEstudo> expected = new HashSet<>();
            expected.add(areaDeEstudo1);
            expected.add(areaDeEstudo2);
            expected.add(areaDeEstudo3);

            String uri1 = URI_TEMPLATE + "/" + aluno1.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";
            String uri2 = URI_TEMPLATE + "/" + aluno1.getId() + "/" + areaDeEstudo2.getId() + "/adicionar-area-de-estudo-interessada";
            String uri3 = URI_TEMPLATE + "/" + aluno1.getId() + "/" + areaDeEstudo3.getId() + "/adicionar-area-de-estudo-interessada";

            // Act
            driver.perform(MockMvcRequestBuilders
                            .patch(uri1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print());

            driver.perform(MockMvcRequestBuilders
                            .patch(uri2)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print());

            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .patch(uri3)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AlunoInteresseAreaResponseDTO responseDTO = objectMapper.readValue(respostaJsonString, AlunoInteresseAreaResponseDTO.class);

            // Assert
            assertEquals(expected, responseDTO.getAreasDeEstudoInteressadas());
        }

        @Test
        @DisplayName("Deve adicionar áreas de estudo interessadas em vários alunos")
        void adicionarAreasDeEstudoInteressadasVariosAlunos() throws Exception {
            // Arrange
            Set<AreaDeEstudo> expected1 = new HashSet<>();
            expected1.add(areaDeEstudo1);
            expected1.add(areaDeEstudo2);

            Set<AreaDeEstudo> expected2 = new HashSet<>();
            expected2.add(areaDeEstudo2);

            Set<AreaDeEstudo> expected3 = new HashSet<>();
            expected3.add(areaDeEstudo2);
            expected3.add(areaDeEstudo3);

            String uri1 = URI_TEMPLATE + "/" + aluno1.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";
            String uri2 = URI_TEMPLATE + "/" + aluno1.getId() + "/" + areaDeEstudo2.getId() + "/adicionar-area-de-estudo-interessada";

            String uri3 = URI_TEMPLATE + "/" + aluno2.getId() + "/" + areaDeEstudo2.getId() + "/adicionar-area-de-estudo-interessada";

            String uri4 = URI_TEMPLATE + "/" + aluno3.getId() + "/" + areaDeEstudo2.getId() + "/adicionar-area-de-estudo-interessada";
            String uri5 = URI_TEMPLATE + "/" + aluno3.getId() + "/" + areaDeEstudo3.getId() + "/adicionar-area-de-estudo-interessada";

            // Act
            driver.perform(MockMvcRequestBuilders
                            .patch(uri1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print());

            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .patch(uri2)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .patch(uri3)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            driver.perform(MockMvcRequestBuilders.patch(uri4)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print());

            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .patch(uri5)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            AlunoInteresseAreaResponseDTO responseDTO1 = objectMapper.readValue(respostaJsonString1, AlunoInteresseAreaResponseDTO.class);
            AlunoInteresseAreaResponseDTO responseDTO2 = objectMapper.readValue(respostaJsonString2, AlunoInteresseAreaResponseDTO.class);
            AlunoInteresseAreaResponseDTO responseDTO3 = objectMapper.readValue(respostaJsonString3, AlunoInteresseAreaResponseDTO.class);

            // Assert
            assertEquals(expected1, responseDTO1.getAreasDeEstudoInteressadas());
            assertEquals(expected2, responseDTO2.getAreasDeEstudoInteressadas());
            assertEquals(expected3, responseDTO3.getAreasDeEstudoInteressadas());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao adicionar uma área de estudo interessada com um aluno não cadastrado")
        void adicionarAreaInteressadaAlunoNaoCadastrado() throws Exception {
            // Arrange
            String uri = URI_TEMPLATE + "/" + 99 + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .patch(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Aluno não encontrado!", erro.getMessage());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao adicionar uma área de estudo não cadastrada")
        void adicionarAreaInteressadaNaoCadastrada() throws Exception {
            // Arrange
            String uri = URI_TEMPLATE + "/" + aluno1.getId() + "/" + "99" + "/adicionar-area-de-estudo-interessada";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders.patch(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Área de Estudo não encontrada!", erro.getMessage());
        }

    }

    @Nested
    @DisplayName("Verificações de listagem de professores interessados nas mesmas áreas de interesse do aluno")
    class VerificacoesListagemProfessoresMesmasAreasAluno {

        @Test
        @DisplayName("Deve listar vários professores interessados nas mesmas áreas de estudo de um aluno")
        void listarProfessoresInteressadosTest() throws Exception {
            //Arrange
            driver.perform(MockMvcRequestBuilders.patch(URI_TEMPLATE + "/" +
                                    aluno1.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            driver.perform(MockMvcRequestBuilders.patch(URI_TEMPLATE + "/" +
                                    aluno1.getId() + "/" + areaDeEstudo2.getId() + "/adicionar-area-de-estudo-interessada")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            driver.perform(MockMvcRequestBuilders.patch("/professores/" +
                                    professor1.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            driver.perform(MockMvcRequestBuilders.patch("/professores/" +
                                    professor2.getId() + "/" + areaDeEstudo2.getId() + "/adicionar-area-de-estudo-interessada")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/" +
                                    aluno1.getId() + "/professores-interessados-areas-de-estudo")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ArrayList<ProfessorGetInteressadosPorAlunoDTO> professoresInteressados = objectMapper.readValue(respostaJsonString,
                    new TypeReference<ArrayList<ProfessorGetInteressadosPorAlunoDTO>>() {
            });

            // Assert
            assertEquals(2, professoresInteressados.size());
            assertTrue(professoresInteressados.stream().map(ProfessorGetInteressadosPorAlunoDTO::getNome)
                    .anyMatch(nome -> nome.equals(professor1.getNome())));
            assertTrue(professoresInteressados.stream().map(ProfessorGetInteressadosPorAlunoDTO::getNome)
                    .anyMatch(nome -> nome.equals(professor2.getNome())));
        }

        @Test
        @DisplayName("Deve listar nenhum professor quando não há professores interessados nas áreas de estudo de um aluno")
        void listarNenhumProfessoresInteressadosTest() throws Exception {
            //Arrange
            driver.perform(MockMvcRequestBuilders.patch(URI_TEMPLATE + "/" +
                                    aluno1.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            driver.perform(MockMvcRequestBuilders.patch(URI_TEMPLATE + "/" +
                                    aluno1.getId() + "/" + areaDeEstudo2.getId() + "/adicionar-area-de-estudo-interessada")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/" + aluno1.getId() + "/professores-interessados-areas-de-estudo")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ArrayList<ProfessorGetInteressadosPorAlunoDTO> professoresInteressados = objectMapper.readValue(respostaJsonString,
                    new TypeReference<ArrayList<ProfessorGetInteressadosPorAlunoDTO>>() {
            });

            // Assert
            assertEquals(0, professoresInteressados.size());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao listar professores interessados nas áreas de estudo de um aluno não cadastrado")
        void listarProfessoresInteressadosAlunoNaoCadastradoTest() throws Exception {
            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/" + 99 + "/professores-interessados-areas-de-estudo")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Aluno não encontrado!", erro.getMessage());
        }

    }

    @Nested
    @DisplayName("Verificações de listagem de Temas de TCC cadastrados pelos professores por parte do aluno")
    class VerificacoesListagemTemasTCCCadastrados {

        @Test
        @DisplayName("Deve listar os temas de TCC cadastrados por todos os professores")
        void listarTemasTCCCadastradosPorTodosProfessoresTest() throws Exception {
            // Arrange
            String uri1 = "/professores/" + professor1.getId() + "/cadastrar-tema-tcc";
            String uri2 = "/professores/" + professor2.getId() + "/cadastrar-tema-tcc";

            driver.perform(MockMvcRequestBuilders.post(uri1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            driver.perform(MockMvcRequestBuilders.post(uri1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO2)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            driver.perform(MockMvcRequestBuilders.post(uri2)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO3)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString = driver.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/temas-tcc-cadastrados")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ArrayList<TemaTCCProfessorAlunoGetAllDTO> temasTccCadastrados = objectMapper.readValue(respostaJsonString, new TypeReference<ArrayList<TemaTCCProfessorAlunoGetAllDTO>>() {
            });

            assertEquals(temasTccCadastrados.size(), 3);
            assertTrue(temasTccCadastrados.stream().map(TemaTCCProfessorAlunoGetAllDTO::getTitulo)
                    .anyMatch(titulo -> titulo.equals(temaTccDTO1.getTitulo())));
            assertTrue(temasTccCadastrados.stream().map(TemaTCCProfessorAlunoGetAllDTO::getTitulo)
                    .anyMatch(titulo -> titulo.equals(temaTccDTO2.getTitulo())));
            assertTrue(temasTccCadastrados.stream().map(TemaTCCProfessorAlunoGetAllDTO::getTitulo)
                    .anyMatch(titulo -> titulo.equals(temaTccDTO3.getTitulo())));
            assertTrue(temasTccCadastrados.stream().map(TemaTCCProfessorAlunoGetAllDTO::getProfessorAutor)
                    .anyMatch(professorAutor -> professorAutor.equals(professor1.getNome())));
            assertTrue(temasTccCadastrados.stream().map(TemaTCCProfessorAlunoGetAllDTO::getProfessorAutor)
                    .anyMatch(professorAutor -> professorAutor.equals(professor2.getNome())));
        }

        @Test
        @DisplayName("Deve listar nenhum tema ao listar os temas de TCC cadastrados por professores e não há temas cadastrados")
        void listarNenhumTemaTCCCadastradoPorProfessorTest() throws Exception {
            // Arrange
            String respostaJsonString = driver.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/temas-tcc-cadastrados")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ArrayList<TemaTCCProfessorAlunoGetAllDTO> temasTccCadastrados = objectMapper.readValue(respostaJsonString, new TypeReference<ArrayList<TemaTCCProfessorAlunoGetAllDTO>>() {
            });

            // Assert
            assertEquals(0, temasTccCadastrados.size());
        }

    }

    @Nested
    @DisplayName("Verificações de solicitação de orientação em um tema de TCC cadastrado pelo aluno a um professor, por parte do aluno")
    class VerificacoesSolicitacaoOrientacaoTemaDoAluno {
        @Test
        @DisplayName("Deve solicitar orientação em um tema de TCC cadastrado pelo aluno e alterar o status para PENDENTE")
        void solicitarOrientacaoEmTemaTCCDoAluno() throws Exception {
            // Arrange
            String uriCadastrarTemaTCC = URI_TEMPLATE + "/" + aluno1.getId() + "/cadastrar-tema-tcc";

            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTemaTCC)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCNovo = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCNovo.getId())
                    .build();

            // Act
            String uriSolicitarOrientacao = URI_TEMPLATE + "/solicitar-orientacao-tcc-aluno";
            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
            Solicitacao solicitacaoResposta = objectMapper.readValue(respostaJsonString2, Solicitacao.class);
            TemaTCC temaTCCAtualizado = temaTCCRepository.findById(temaTCCNovo.getId()).orElseThrow(TemaNotFoundException::new);

            // Assert
            assertEquals(solicitacaoDto.getAlunoId(), solicitacaoResposta.getAluno().getId());
            assertEquals(solicitacaoDto.getProfessorId(), solicitacaoResposta.getProfessor().getId());
            assertEquals(solicitacaoDto.getTemaTCCId(), solicitacaoResposta.getTemaTCC().getId());
            assertEquals(StatusTCCEnum.PENDENTE, temaTCCAtualizado.getStatus());
        }

        @Test
        @DisplayName("Deve retornar erro ao criar solicitação a um tema de TCC cadastrado pelo aluno com aluno inexistente")
        void criarSolicitacaoTemaDoAlunoComAlunoInexistente() throws Exception {
            // Arrange
            String uriCadastrarTemaTCC = URI_TEMPLATE + "/" + aluno1.getId() + "/cadastrar-tema-tcc";

            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTemaTCC)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCNovo = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            Long alunoIdInexistente = 999L;

            solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(alunoIdInexistente)
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCNovo.getId())
                    .build();

            // Act
            String uriSolicitarOrientacao = URI_TEMPLATE + "/solicitar-orientacao-tcc-aluno";
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Aluno não encontrado!", erro.getMessage());
        }

        @Test
        @DisplayName("Deve retornar erro ao criar solicitação a um tema de TCC cadastrado pelo aluno com professor inexistente")
        void criarSolicitacaoTemaDoAlunoComProfessorInexistente() throws Exception {
            // Arrange
            String uriCadastrarTemaTCC = URI_TEMPLATE + "/" + aluno1.getId() + "/cadastrar-tema-tcc";

            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTemaTCC)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCNovo = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            Long professorIdInexistente = 999L;

            solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professorIdInexistente)
                    .temaTCCId(temaTCCNovo.getId())
                    .build();

            // Act
            String uriSolicitarOrientacao = URI_TEMPLATE + "/solicitar-orientacao-tcc-aluno";
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Professor não encontrado!", erro.getMessage());
        }

        @Test
        @DisplayName("Deve retornar erro ao criar solicitação a um tema de TCC cadastrado pelo aluno com TemaTcc inexistente")
        void criarSolicitacaoTemaDoAlunoComTemaInexistente() throws Exception {
            // Arrange

            Long temaTccIdInexistente = 999L;

            solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTccIdInexistente)
                    .build();

            // Act
            String uriSolicitarOrientacao = URI_TEMPLATE + "/solicitar-orientacao-tcc-aluno";
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Tema não encontrado!", erro.getMessage());
        }
    }

    @Nested
    @DisplayName("Verificações de solicitação de orientação em um tema de TCC cadastrado pelo professor a um professor, por parte do aluno")
    class VerificacoesSolicitacaoOrientacaoTemaDoProfessor {

        @Test
        @DisplayName("Deve solicitar orientação em um tema de TCC cadastrado pelo professor, o status permanece NOVO")
        void solicitarOrientacaoEmTemaTCCDoProfessor() throws Exception {
            // Arrange
            String uri = "/professores" + "/" + professor1.getId() + "/cadastrar-tema-tcc";

            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCNovo = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCNovo.getId())
                    .build();
            // Act
            String uriSolicitarOrientacao = URI_TEMPLATE + "/solicitar-orientacao-tcc-professor";
            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            Solicitacao solicitacaoResposta = objectMapper.readValue(respostaJsonString2, Solicitacao.class);
            TemaTCC temaTCCAtualizado = temaTCCRepository.findById(temaTCCNovo.getId()).orElseThrow(TemaNotFoundException::new);

            // Assert
            assertEquals(solicitacaoDto.getAlunoId(), solicitacaoResposta.getAluno().getId());
            assertEquals(solicitacaoDto.getProfessorId(), solicitacaoResposta.getProfessor().getId());
            assertEquals(solicitacaoDto.getTemaTCCId(), solicitacaoResposta.getTemaTCC().getId());
            assertEquals(StatusTCCEnum.NOVO, temaTCCAtualizado.getStatus());
        }

        @Test
        @DisplayName("Deve retornar erro ao criar solicitação a um tema de TCC cadastrado pelo professor com aluno inexistente")
        void criarSolicitacaoTemaDoProfessorComAlunoInexistente() throws Exception {
            // Arrange
            String uri = "/professores" + "/" + professor1.getId() + "/cadastrar-tema-tcc";

            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCNovo = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            Long alunoIdInexistente = 999L;

            solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(alunoIdInexistente)
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCNovo.getId())
                    .build();

            // Act
            String uriSolicitarOrientacao = URI_TEMPLATE + "/solicitar-orientacao-tcc-professor";
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Aluno não encontrado!", erro.getMessage());
        }

        @Test
        @DisplayName("Deve retornar erro ao criar solicitação a um tema de TCC cadastrado pelo professor com professor inexistente")
        void criarSolicitacaoTemaDoProfessorComProfessorInexistente() throws Exception {
            // Arrange
            String uri = "/professores" + "/" + professor1.getId() + "/cadastrar-tema-tcc";

            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCNovo = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            Long professorIdInexistente = 999L;

            solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professorIdInexistente)
                    .temaTCCId(temaTCCNovo.getId())
                    .build();

            // Act
            String uriSolicitarOrientacao = URI_TEMPLATE + "/solicitar-orientacao-tcc-professor";
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Professor não encontrado!", erro.getMessage());
        }

        @Test
        @DisplayName("Deve retornar erro ao criar solicitação a um tema de TCC cadastrado pelo professor com Tema de TCC inexistente")
        void criarSolicitacaoTemaDoProfessorComTemaInexistente() throws Exception {
            // Arrange
            Long temaTccIdInexistente = 999L;

            solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTccIdInexistente)
                    .build();

            // Act
            String uriSolicitarOrientacao = URI_TEMPLATE + "/solicitar-orientacao-tcc-professor";
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Tema não encontrado!", erro.getMessage());
        }
    }
}
