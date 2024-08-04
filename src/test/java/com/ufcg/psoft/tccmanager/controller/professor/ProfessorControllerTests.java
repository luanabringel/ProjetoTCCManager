package com.ufcg.psoft.tccmanager.controller.professor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorAtualizaQuotaDto;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorInteresseAreaResponseDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoAprovacaoResponseDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoAprovacaoRequestDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCProfessorGetAllDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCAlunoGetAllDTO;
import com.ufcg.psoft.tccmanager.events.EventManager;
import com.ufcg.psoft.tccmanager.exception.AlunoNotFoundException;
import com.ufcg.psoft.tccmanager.exception.ProfessorNotFoundException;
import com.ufcg.psoft.tccmanager.model.Aluno;
import com.ufcg.psoft.tccmanager.model.enums.StatusTCCEnum;
import com.ufcg.psoft.tccmanager.exception.CustomErrorType;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import com.ufcg.psoft.tccmanager.model.Professor;
import com.ufcg.psoft.tccmanager.model.TemaTCC;
import com.ufcg.psoft.tccmanager.repository.*;
import com.ufcg.psoft.tccmanager.service.solicitacao.SolicitacaoService;
import net.minidev.json.annotate.JsonIgnore;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Classe de teste de ProfessorController")
public class ProfessorControllerTests {

    private final String URI_TEMPLATE = "/professores";

    @Autowired
    MockMvc driver;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    TemaTCCRepository temaTCCRepository;

    @Autowired
    AreaDeEstudoRepository areaDeEstudoRepository;

    @Autowired
    SolicitacaoRepository solicitacaoRepository;

    @Autowired
    AlunoRepository alunoRepository;

    @Autowired
    EventManager eventManager;

    Professor professor1, professor2, professor3;

    Aluno aluno1, aluno2;

    AreaDeEstudo areaDeEstudo1, areaDeEstudo2, areaDeEstudo3;

    TemaTCCDTO temaTccDTO1, temaTccDTO2, temaTccDTO3;

    SolicitacaoDTO solicitacaoDTO1, solicitacaoDTO2;

    @BeforeEach
    void setup() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        aluno1 = alunoRepository.save(Aluno.builder()
                .nome("Antonio Santos")
                .matricula("121210999")
                .email("antonio.santos@gmail.com")
                .periodoConclusao("2023.1")
                .build());

        aluno2 = alunoRepository.save(Aluno.builder()
                .nome("José Ferreira")
                .matricula("121210888")
                .email("jose.ferreira@gmail.com")
                .periodoConclusao("2022.2")
                .build());

        professor1 = professorRepository.save(Professor.builder()
                .nome("Raimundo Nonato")
                .email("raimundo.nonato@gmail.com")
                .quota(1)
                .build());

        professor2 = professorRepository.save(Professor.builder()
                .nome("Charles Xavier")
                .email("charles.xavier@gmail.com")
                .quota(2)
                .build());

        professor3 = professorRepository.save(Professor.builder()
                .nome("Albus Dumbledore")
                .email("albus.dumbledore@gmail.com")
                .quota(3)
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
    }

    @Nested
    @DisplayName("Verificações de cadastro de tema de TCC por parte do professor")
    class VerificacoesCadastroTemaTCC {

        @Test
        @DisplayName("Deve cadastrar um tema de TCC com uma área de estudo")
        void cadastrarTemaTccComUmaArea() throws Exception {
            // Arrange
            Set<AreaDeEstudo> areasDeEstudo = new HashSet<>();
            areasDeEstudo.add(areaDeEstudo1);

            temaTccDTO1.setAreasDeEstudo(areasDeEstudo);

            String uri = URI_TEMPLATE + "/" + professor1.getId() + "/cadastrar-tema-tcc";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCC = objectMapper.readValue(respostaJsonString, TemaTCC.class);
            Professor professorCadastrado = professorRepository.findById(professor1.getId()).orElseThrow(ProfessorNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), temaTCC.getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), temaTCC.getDescricao());
            assertEquals(areasDeEstudo, temaTCC.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC.getStatus());

            assertTrue(professorCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC.getId())));
        }

        @Test
        @DisplayName("Deve cadastrar um tema de TCC com uma área de estudo para vários professores")
        void cadastrarTemaTccComUmaAreaVariosProfessores() throws Exception {
            // Arrange
            Set<AreaDeEstudo> areasDeEstudo = new HashSet<>();
            areasDeEstudo.add(areaDeEstudo1);

            temaTccDTO1.setAreasDeEstudo(areasDeEstudo);
            temaTccDTO2.setAreasDeEstudo(areasDeEstudo);
            temaTccDTO3.setAreasDeEstudo(areasDeEstudo);

            String uri1 = URI_TEMPLATE + "/" + professor1.getId() + "/cadastrar-tema-tcc";
            String uri2 = URI_TEMPLATE + "/" + professor2.getId() + "/cadastrar-tema-tcc";
            String uri3 = URI_TEMPLATE + "/" + professor3.getId() + "/cadastrar-tema-tcc";

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
            Professor professorCadastrado1 = professorRepository.findById(professor1.getId()).orElseThrow(ProfessorNotFoundException::new);
            Professor professorCadastrado2 = professorRepository.findById(professor2.getId()).orElseThrow(ProfessorNotFoundException::new);
            Professor professorCadastrado3 = professorRepository.findById(professor3.getId()).orElseThrow(ProfessorNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), temaTCC1.getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), temaTCC1.getDescricao());
            assertEquals(areasDeEstudo, temaTCC1.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC1.getStatus());

            assertTrue(professorCadastrado1.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC1.getId())));

            assertEquals(temaTccDTO2.getTitulo(), temaTCC2.getTitulo());
            assertEquals(temaTccDTO2.getDescricao(), temaTCC2.getDescricao());
            assertEquals(areasDeEstudo, temaTCC2.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC2.getStatus());

            assertTrue(professorCadastrado2.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC2.getId())));

            assertEquals(temaTccDTO3.getTitulo(), temaTCC3.getTitulo());
            assertEquals(temaTccDTO3.getDescricao(), temaTCC3.getDescricao());
            assertEquals(areasDeEstudo, temaTCC3.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC3.getStatus());

            assertTrue(professorCadastrado3.getTemasTCCCadastrados()
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

            String uri = URI_TEMPLATE + "/" + professor1.getId() + "/cadastrar-tema-tcc";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCC = objectMapper.readValue(respostaJsonString, TemaTCC.class);
            Professor professorCadastrado = professorRepository.findById(professor1.getId()).orElseThrow(ProfessorNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), temaTCC.getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), temaTCC.getDescricao());
            assertEquals(areasDeEstudo, temaTCC.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC.getStatus());

            assertTrue(professorCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC.getId())));
        }

        @Test
        @DisplayName("Deve cadastrar um tema de TCC com mais de uma área de estudo para vários professores")
        void cadastrarTemaTccMaisDeUmaAreaVariosProfessores() throws Exception {
            // Arrange
            Set<AreaDeEstudo> areasDeEstudo = new HashSet<>();
            areasDeEstudo.add(areaDeEstudo1);
            areasDeEstudo.add(areaDeEstudo2);
            areasDeEstudo.add(areaDeEstudo3);

            temaTccDTO1.setAreasDeEstudo(areasDeEstudo);
            temaTccDTO2.setAreasDeEstudo(areasDeEstudo);
            temaTccDTO3.setAreasDeEstudo(areasDeEstudo);

            String uri1 = URI_TEMPLATE + "/" + professor1.getId() + "/cadastrar-tema-tcc";
            String uri2 = URI_TEMPLATE + "/" + professor2.getId() + "/cadastrar-tema-tcc";
            String uri3 = URI_TEMPLATE + "/" + professor3.getId() + "/cadastrar-tema-tcc";

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
            Professor professorCadastrado1 = professorRepository.findById(professor1.getId()).orElseThrow(ProfessorNotFoundException::new);
            Professor professorCadastrado2 = professorRepository.findById(professor2.getId()).orElseThrow(ProfessorNotFoundException::new);
            Professor professorCadastrado3 = professorRepository.findById(professor3.getId()).orElseThrow(ProfessorNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), temaTCC1.getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), temaTCC1.getDescricao());
            assertEquals(areasDeEstudo, temaTCC1.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC1.getStatus());

            assertTrue(professorCadastrado1.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC1.getId())));

            assertEquals(temaTccDTO2.getTitulo(), temaTCC2.getTitulo());
            assertEquals(temaTccDTO2.getDescricao(), temaTCC2.getDescricao());
            assertEquals(areasDeEstudo, temaTCC2.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC2.getStatus());

            assertTrue(professorCadastrado2.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC2.getId())));

            assertEquals(temaTccDTO3.getTitulo(), temaTCC3.getTitulo());
            assertEquals(temaTccDTO3.getDescricao(), temaTCC3.getDescricao());
            assertEquals(areasDeEstudo, temaTCC3.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC3.getStatus());

            assertTrue(professorCadastrado3.getTemasTCCCadastrados()
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

            String uri = URI_TEMPLATE + "/" + professor1.getId() + "/cadastrar-tema-tcc";

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
            Professor professorCadastrado = professorRepository.findById(professor1.getId()).orElseThrow(ProfessorNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), temaTCC1.getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), temaTCC1.getDescricao());
            assertEquals(areasDeEstudo, temaTCC1.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC1.getStatus());

            assertTrue(professorCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC1.getId())));

            assertEquals(temaTccDTO2.getTitulo(), temaTCC2.getTitulo());
            assertEquals(temaTccDTO2.getDescricao(), temaTCC2.getDescricao());
            assertEquals(areasDeEstudo, temaTCC2.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC2.getStatus());

            assertTrue(professorCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC2.getId())));

            assertEquals(temaTccDTO3.getTitulo(), temaTCC3.getTitulo());
            assertEquals(temaTccDTO3.getDescricao(), temaTCC3.getDescricao());
            assertEquals(areasDeEstudo, temaTCC3.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC3.getStatus());

            assertTrue(professorCadastrado.getTemasTCCCadastrados()
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

            String uri = URI_TEMPLATE + "/" + professor1.getId() + "/cadastrar-tema-tcc";

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
            Professor professorCadastrado = professorRepository.findById(professor1.getId()).orElseThrow(ProfessorNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), temaTCC1.getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), temaTCC1.getDescricao());
            assertEquals(areasDeEstudo1, temaTCC1.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC1.getStatus());

            assertTrue(professorCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC1.getId())));

            assertEquals(temaTccDTO2.getTitulo(), temaTCC2.getTitulo());
            assertEquals(temaTccDTO2.getDescricao(), temaTCC2.getDescricao());
            assertEquals(areasDeEstudo2, temaTCC2.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC2.getStatus());

            assertTrue(professorCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC2.getId())));

            assertEquals(temaTccDTO3.getTitulo(), temaTCC3.getTitulo());
            assertEquals(temaTccDTO3.getDescricao(), temaTCC3.getDescricao());
            assertEquals(areasDeEstudo3, temaTCC3.getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, temaTCC3.getStatus());

            assertTrue(professorCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(temaTCC3.getId())));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao cadastrar um tema de TCC com um professor não cadastrado")
        void cadastrarTemaTccProfessorInvalido() throws Exception {
            professorRepository.deleteAll();
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
            assertEquals("Professor não encontrado!", erro.getMessage());
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

            String uri = URI_TEMPLATE + "/" + professor1.getId() + "/cadastrar-tema-tcc";

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
    @DisplayName("Verificações de adição de área de estudo interessada por parte do professor")
    class VerificacoesAdicaoAreaDeEstudo {

        @Test
        @DisplayName("Deve adicionar uma área de estudo interessada")
        void adicionarAreaInteressada() throws Exception {
            // Arrange
            Set<AreaDeEstudo> expected = new HashSet<>();
            expected.add(areaDeEstudo1);

            String uri = URI_TEMPLATE + "/" + professor1.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .patch(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ProfessorInteresseAreaResponseDTO responseDTO = objectMapper.readValue(respostaJsonString, ProfessorInteresseAreaResponseDTO.class);

            // Assert
            assertEquals(expected, responseDTO.getAreasDeEstudoInteressadas());
        }

        @Test
        @DisplayName("Deve adicionar uma mesma área de estudo interessada para vários professores")
        void adicionarMesmaAreaInteressadaVariosProfessores() throws Exception {
            // Arrange
            Set<AreaDeEstudo> expected = new HashSet<>();
            expected.add(areaDeEstudo1);

            String uri1 = URI_TEMPLATE + "/" + professor1.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";
            String uri2 = URI_TEMPLATE + "/" + professor2.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";
            String uri3 = URI_TEMPLATE + "/" + professor3.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";

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

            ProfessorInteresseAreaResponseDTO responseDTO1 = objectMapper.readValue(respostaJsonString1, ProfessorInteresseAreaResponseDTO.class);
            ProfessorInteresseAreaResponseDTO responseDTO2 = objectMapper.readValue(respostaJsonString2, ProfessorInteresseAreaResponseDTO.class);
            ProfessorInteresseAreaResponseDTO responseDTO3 = objectMapper.readValue(respostaJsonString3, ProfessorInteresseAreaResponseDTO.class);

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

            String uri1 = URI_TEMPLATE + "/" + professor1.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";
            String uri2 = URI_TEMPLATE + "/" + professor1.getId() + "/" + areaDeEstudo2.getId() + "/adicionar-area-de-estudo-interessada";
            String uri3 = URI_TEMPLATE + "/" + professor1.getId() + "/" + areaDeEstudo3.getId() + "/adicionar-area-de-estudo-interessada";

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

            ProfessorInteresseAreaResponseDTO responseDTO = objectMapper.readValue(respostaJsonString, ProfessorInteresseAreaResponseDTO.class);

            // Assert
            assertEquals(expected, responseDTO.getAreasDeEstudoInteressadas());
        }

        @Test
        @DisplayName("Deve adicionar áreas de estudo interessadas em vários professores")
        void adicionarAreasDeEstudoInteressadasVariosProfessores() throws Exception {
            // Arrange
            Set<AreaDeEstudo> expected1 = new HashSet<>();
            expected1.add(areaDeEstudo1);
            expected1.add(areaDeEstudo2);

            Set<AreaDeEstudo> expected2 = new HashSet<>();
            expected2.add(areaDeEstudo2);

            Set<AreaDeEstudo> expected3 = new HashSet<>();
            expected3.add(areaDeEstudo2);
            expected3.add(areaDeEstudo3);

            String uri1 = URI_TEMPLATE + "/" + professor1.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";
            String uri2 = URI_TEMPLATE + "/" + professor1.getId() + "/" + areaDeEstudo2.getId() + "/adicionar-area-de-estudo-interessada";

            String uri3 = URI_TEMPLATE + "/" + professor2.getId() + "/" + areaDeEstudo2.getId() + "/adicionar-area-de-estudo-interessada";

            String uri4 = URI_TEMPLATE + "/" + professor3.getId() + "/" + areaDeEstudo2.getId() + "/adicionar-area-de-estudo-interessada";
            String uri5 = URI_TEMPLATE + "/" + professor3.getId() + "/" + areaDeEstudo3.getId() + "/adicionar-area-de-estudo-interessada";

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


            ProfessorInteresseAreaResponseDTO responseDTO1 = objectMapper.readValue(respostaJsonString1, ProfessorInteresseAreaResponseDTO.class);
            ProfessorInteresseAreaResponseDTO responseDTO2 = objectMapper.readValue(respostaJsonString2, ProfessorInteresseAreaResponseDTO.class);
            ProfessorInteresseAreaResponseDTO responseDTO3 = objectMapper.readValue(respostaJsonString3, ProfessorInteresseAreaResponseDTO.class);

            // Assert
            assertEquals(expected1, responseDTO1.getAreasDeEstudoInteressadas());
            assertEquals(expected2, responseDTO2.getAreasDeEstudoInteressadas());
            assertEquals(expected3, responseDTO3.getAreasDeEstudoInteressadas());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao adicionar uma área de estudo interessada com um professor não cadastrado")
        void adicionarAreaInteressadaProfessorNaoCadastrado() throws Exception {
            // Arrange
            professorRepository.deleteAll();
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
            assertEquals("Professor não encontrado!", erro.getMessage());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao adicionar uma área de estudo não cadastrada")
        void adicionarAreaInteressadaNaoCadastrada() throws Exception {
            // Arrange
            String uri = URI_TEMPLATE + "/" + professor1.getId() + "/" + "999999" + "/adicionar-area-de-estudo-interessada";

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
    @DisplayName("Verificações de busca dos temas de TCC cadastrados pelo professor")
    class VerificacoesBuscaTemasTcc {

        @Test
        @DisplayName("Deve listar os temas de TCC cadastrados por um professor")
        void listarTemasTccCadastradosPorUmProfessor() throws Exception {
            // Arrange
            String uri = URI_TEMPLATE + "/" + professor1.getId() + "/cadastrar-tema-tcc";
            String uriGet = URI_TEMPLATE + "/" + professor1.getId() + "/temas-tcc-cadastrados";

            driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO2)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .get(uriGet)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ArrayList<TemaTCCProfessorGetAllDTO> temasTcc = objectMapper.readValue(respostaJsonString, new TypeReference<ArrayList<TemaTCCProfessorGetAllDTO>>() {
            });

            // Assert
            assertTrue(temasTcc.stream()
                    .map(TemaTCCProfessorGetAllDTO::getTitulo)
                    .anyMatch(titulo -> titulo.equals(temaTccDTO1.getTitulo())));
            assertTrue(temasTcc.stream()
                    .map(TemaTCCProfessorGetAllDTO::getTitulo)
                    .anyMatch(titulo -> titulo.equals(temaTccDTO2.getTitulo())));
            assertTrue(temasTcc.stream()
                    .map(TemaTCCProfessorGetAllDTO::getAreasDeEstudo)
                    .anyMatch(areasDeEstudo -> areasDeEstudo.equals(temaTccDTO1.getAreasDeEstudo())));
            assertTrue(temasTcc.stream()
                    .map(TemaTCCProfessorGetAllDTO::getAreasDeEstudo)
                    .anyMatch(areasDeEstudo -> areasDeEstudo.equals(temaTccDTO2.getAreasDeEstudo())));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao buscar os temas de TCC cadastrados por um professor não cadastrado")
        void buscarTemasTccCadastradosProfessorNaoCadastrado() throws Exception {
            // Arrange
            String uri = URI_TEMPLATE + "/" + 1234 + "/temas-tcc-cadastrados";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .get(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Professor não encontrado!", erro.getMessage());
        }

        @Test
        @DisplayName("Deve listar nenhum tema ao buscar os temas de TCC cadastrados de um professor sem temas cadastrados")
        void buscarTemasTccCadastradosSemTemas() throws Exception {
            // Arrange
            String uri = URI_TEMPLATE + "/" + professor1.getId() + "/temas-tcc-cadastrados";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .get(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ArrayList<TemaTCCProfessorGetAllDTO> temasTcc = objectMapper.readValue(respostaJsonString, new TypeReference<ArrayList<TemaTCCProfessorGetAllDTO>>() {
            });

            // Assert
            assertTrue(temasTcc.isEmpty());
        }
    }

    @Nested
    @DisplayName("Verificações para notificações de e-mail via mensagem no terminal")
    class VerificacoesNotificacaoEmail {

        Aluno aluno1;

        Set<AreaDeEstudo> areasDeEstudo;

        @BeforeEach
        void setup() {
            aluno1 = alunoRepository.save(Aluno.builder()
                    .nome("Fada Bloom")
                    .matricula("121210999")
                    .email("bloom.winx@gmail.com")
                    .periodoConclusao("2024.2")
                    .build());


            areasDeEstudo = new HashSet<>();
            areasDeEstudo.add(areaDeEstudo1);
        }

        @Test
        @DisplayName("Deve notificar o aluno com uma mensagem no terminal quando um professor cadastrar novo TCC nas áreas de interesse do aluno")
        void enviarNotificacaoAlunoNovoTccComTemaInteressado() throws Exception {
            // Arrange
            String notificacaoEsperada = """
                    Há um novo tema de TCC cadastrado que está em suas áreas de interesse!
                    Olá, Fada Bloom.
                    O professor Charles Xavier cadastrou um novo tema de TCC com a área Banco de Dados, na qual você demonstrou interesse!
                    """;

            String uriInteresseAluno = "/alunos/" + aluno1.getId() + "/" + areaDeEstudo1.getId() + "/adicionar-area-de-estudo-interessada";

            driver.perform(MockMvcRequestBuilders
                            .patch(uriInteresseAluno)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk());

            temaTccDTO1.setAreasDeEstudo(areasDeEstudo);

            String uri = URI_TEMPLATE + "/" + professor2.getId() + "/cadastrar-tema-tcc";

            // Act
            driver.perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated());

            Aluno alunoNotificado = alunoRepository.findById(aluno1.getId()).orElseThrow(AlunoNotFoundException::new);

            // Assert
            assertTrue(alunoNotificado.getNotificacoes().contains(notificacaoEsperada));
        }

        @Test
        @DisplayName("Deve notificar o professor com uma mensagem no terminal quando um aluno solicitar orientação para um tema de tcc criado pelo aluno")
        void enviarNotificacaoProfessorSolicitacaoDeOrientacaoTemaTccCriadoPorAluno() throws Exception {
            // Arrange
            String notificacaoEsperada = """
                    Há uma nova solicitação de orientação de TCC!
                    Olá, Albus Dumbledore.
                    O(a) aluno(a) Fada Bloom solicitou a sua orientação no TCC com o tema: Otimização de Consultas em Banco de Dados Distribuídos.
                    """;

            String uriCadastrarTema = "/alunos/" + aluno1.getId() + "/cadastrar-tema-tcc";
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCAluno = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            String uriSolicitarOrientacao = "/alunos" + "/solicitar-orientacao-tcc-aluno";

            SolicitacaoDTO solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor3.getId())
                    .temaTCCId(temaTCCAluno.getId())
                    .build();
            // Act
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            Professor professorNotificado = professorRepository.findById(professor3.getId()).orElseThrow(ProfessorNotFoundException::new);

            // Assert
            assertTrue(professorNotificado.getNotificacoes().contains(notificacaoEsperada));
        }

        @Test
        @DisplayName("Deve notificar o professor com uma mensagem no terminal quando um aluno solicitar orientação para um tema de tcc criado pelo professor")
        void enviarNotificacaoProfessorSolicitacaoDeOrientacaoTemaTccCriadoPorProfessor() throws Exception {
            // Arrange
            String notificacaoEsperada = """
                    Há uma nova solicitação de orientação de TCC!
                    Olá, Charles Xavier.
                    O(a) aluno(a) Fada Bloom solicitou a sua orientação no TCC com o tema: Otimização de Consultas em Banco de Dados Distribuídos.
                    """;

            String uriCadastrarTema = URI_TEMPLATE + "/" + professor2.getId() + "/cadastrar-tema-tcc";
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCAluno = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            String uriSolicitarOrientacao = "/alunos" + "/solicitar-orientacao-tcc-professor";

            SolicitacaoDTO solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor2.getId())
                    .temaTCCId(temaTCCAluno.getId())
                    .build();
            // Act
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            Professor professorNotificado = professorRepository.findById(professor2.getId()).orElseThrow(ProfessorNotFoundException::new);

            // Assert
            assertTrue(professorNotificado.getNotificacoes().contains(notificacaoEsperada));
        }
    }

    @Nested
    @DisplayName("Verificações da quota do professor")
    class VerificacoesQuota {
        @Test
        @DisplayName("Professor deve ser capaz de atualizar sua quota")
        void atualizarQuotaProfessor() throws Exception {
            String uri = URI_TEMPLATE + "/" + professor1.getId() + "/atualizar-quota";
            ProfessorAtualizaQuotaDto novaQuota = ProfessorAtualizaQuotaDto.builder()
                    .quota(5)
                    .build();

            String respostaJsonString = driver.perform(MockMvcRequestBuilders.patch(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(novaQuota)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Professor professorAtualizado = objectMapper.readValue(respostaJsonString, Professor.class);

            assertEquals(professorAtualizado.getQuota(), novaQuota.getQuota());
        }
    }

    @Nested
    @DisplayName("Verificações de buscar todos os temas de TCC cadastrados pelos alunos")
    class VerificacoesProfessorBuscarTodosTemasTccCadastradosPelosAlunos {

        @Test
        @DisplayName("Deve listar Todos os temas de TCC cadastrados pelos alunos")
        void listarTodosTemasTccCadastradosPorAlunos() throws Exception {
            // Arrange
            String uriCadastrarTemaAluno1 = "/alunos" + "/" + aluno1.getId() + "/cadastrar-tema-tcc";
            String uriCadastrarTemaAluno2 = "/alunos" + "/" + aluno2.getId() + "/cadastrar-tema-tcc";
            String uriListarTemasAlunos = URI_TEMPLATE + "/temas-tcc-cadastrados-alunos";

            driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTemaAluno1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTemaAluno2)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO2)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTemaAluno1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO3)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .get(uriListarTemasAlunos)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ArrayList<TemaTCCAlunoGetAllDTO> temasTcc = objectMapper.readValue(respostaJsonString, new TypeReference<ArrayList<TemaTCCAlunoGetAllDTO>>() {
            });

            // Assert
            assertEquals(temasTcc.size(), 3);
            assertTrue(temasTcc.stream()
                    .map(TemaTCCAlunoGetAllDTO::getTitulo)
                    .anyMatch(titulo -> titulo.equals(temaTccDTO1.getTitulo())));
            assertTrue(temasTcc.stream()
                    .map(TemaTCCAlunoGetAllDTO::getTitulo)
                    .anyMatch(titulo -> titulo.equals(temaTccDTO2.getTitulo())));
            assertTrue(temasTcc.stream()
                    .map(TemaTCCAlunoGetAllDTO::getTitulo)
                    .anyMatch(titulo -> titulo.equals(temaTccDTO3.getTitulo())));
            assertTrue(temasTcc.stream()
                    .map(TemaTCCAlunoGetAllDTO::getAreasDeEstudo)
                    .anyMatch(areasDeEstudo -> areasDeEstudo.equals(temaTccDTO1.getAreasDeEstudo())));
            assertTrue(temasTcc.stream()
                    .map(TemaTCCAlunoGetAllDTO::getAreasDeEstudo)
                    .anyMatch(areasDeEstudo -> areasDeEstudo.equals(temaTccDTO2.getAreasDeEstudo())));
            assertTrue(temasTcc.stream()
                    .map(TemaTCCAlunoGetAllDTO::getAreasDeEstudo)
                    .anyMatch(areasDeEstudo -> areasDeEstudo.equals(temaTccDTO3.getAreasDeEstudo())));

        }

        @Test
        @DisplayName("Deve listar nenhum tema ao buscar todos os temas de TCC cadastrados por alunos, se nenhum aluno tiver cadastrado algum tema de TCC")
        void listarTodosTemasTccCadastradosPorAlunosSemTemas() throws Exception {
            // Arrange
            String uriListarTemasAlunos = URI_TEMPLATE + "/temas-tcc-cadastrados-alunos";

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .get(uriListarTemasAlunos)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ArrayList<TemaTCCAlunoGetAllDTO> temasTcc = objectMapper.readValue(respostaJsonString, new TypeReference<ArrayList<TemaTCCAlunoGetAllDTO>>() {
            });

            // Assert
            assertTrue(temasTcc.isEmpty());
        }
    }

    @Nested
    @DisplayName("Verificações de aprovação de solicitações de tema de TCC de alunos por parte do professor")
    class VerificacoesAprovacaoSolicitacoesTemaTccAluno {

        @Test
        @DisplayName("Deve aprovar uma solicitação de um tema de TCC cadastrado por um aluno")
        void aprovaSolicitacaoFeitaPorAluno() throws Exception {
            // Arrange
            StringBuilder notificacaoExpected = new StringBuilder("Mensagem para o Coordenador: ")
                    .append("\n")
                    .append("O tema de TCC de título: ")
                    .append(temaTccDTO1.getTitulo())
                    .append("\n")
                    .append("foi APROVADO pelo professor ")
                    .append(professor1.getNome());

            StringBuilder mensagemExpected = new StringBuilder("Caro aluno ")
                    .append(aluno1.getNome())
                    .append(".")
                    .append("\n")
                    .append("Sua solicitação foi ")
                    .append("APROVADA")
                    .append("\n")
                    .append("\n")
                    .append("Resposta do professor:")
                    .append("\n")
                    .append("Esse tema é bom demais.");

            String uriCadastrarTema = "/alunos/" + aluno1.getId() + "/cadastrar-tema-tcc";
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCAluno = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            SolicitacaoDTO solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCAluno.getId())
                    .build();

            String uriSolicitarOrientacao = "/alunos/solicitar-orientacao-tcc-aluno";
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isCreated());

            // Act
            SolicitacaoAprovacaoRequestDTO aprovacaoDto = SolicitacaoAprovacaoRequestDTO.builder()
                    .professorId(professor1.getId())
                    .alunoId(aluno1.getId())
                    .temaTccId(temaTCCAluno.getId())
                    .mensagemDecisao("Esse tema é bom demais.")
                    .aprovacao(true)
                    .build();

            String uriAprovacaoTema = URI_TEMPLATE + "/definir-aprovacao-tema-tcc";
            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .patch(uriAprovacaoTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(aprovacaoDto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SolicitacaoAprovacaoResponseDTO aprovacaoResposta = objectMapper.readValue(respostaJsonString2, SolicitacaoAprovacaoResponseDTO.class);
            Aluno alunoCadastrado = alunoRepository.findById(aluno1.getId()).orElseThrow(AlunoNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), aprovacaoResposta.getTemaTCC().getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), aprovacaoResposta.getTemaTCC().getDescricao());
            assertEquals(temaTccDTO1.getAreasDeEstudo(), aprovacaoResposta.getTemaTCC().getAreasDeEstudo());
            assertEquals(StatusTCCEnum.ALOCADO, aprovacaoResposta.getTemaTCC().getStatus());
            assertEquals(professor1.getId(), aprovacaoResposta.getProfessorId());
            assertEquals(aluno1.getId(), aprovacaoResposta.getAlunoId());
            assertEquals(true, aprovacaoResposta.getAprovacao());
            assertEquals(notificacaoExpected.toString(), aprovacaoResposta.getNotificacaoCoordenador());
            assertEquals(mensagemExpected.toString(), aprovacaoResposta.getMensagemDecisao());
            assertTrue(alunoCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(aprovacaoResposta.getTemaTCC().getId())));
        }

        @Test
        @DisplayName("Deve desaprovar uma solicitação de um tema de TCC cadastrado por um aluno")
        void desaprovaSolicitacaoFeitaPorAluno() throws Exception {
            // Arrange
            StringBuilder mensagemExpected = new StringBuilder("Caro aluno ")
                    .append(aluno1.getNome())
                    .append(".")
                    .append("\n")
                    .append("Sua solicitação foi ")
                    .append("NEGADA")
                    .append("\n")
                    .append("\n")
                    .append("Resposta do professor:")
                    .append("\n")
                    .append("Esse tema é ruim que dói.");

            String uriCadastrarTema = "/alunos/" + aluno1.getId() + "/cadastrar-tema-tcc";
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCAluno = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            SolicitacaoDTO solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCAluno.getId())
                    .build();

            String uriSolicitarOrientacao = "/alunos/solicitar-orientacao-tcc-aluno";
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isCreated());

            // Act
            SolicitacaoAprovacaoRequestDTO aprovacaoDto = SolicitacaoAprovacaoRequestDTO.builder()
                    .professorId(professor1.getId())
                    .alunoId(aluno1.getId())
                    .temaTccId(temaTCCAluno.getId())
                    .mensagemDecisao("Esse tema é ruim que dói.")
                    .aprovacao(false)
                    .build();

            String uriAprovacaoTema = URI_TEMPLATE + "/definir-aprovacao-tema-tcc";
            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .patch(uriAprovacaoTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(aprovacaoDto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SolicitacaoAprovacaoResponseDTO aprovacaoResposta = objectMapper.readValue(respostaJsonString2, SolicitacaoAprovacaoResponseDTO.class);
            Aluno alunoCadastrado = alunoRepository.findById(aluno1.getId()).orElseThrow(AlunoNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), aprovacaoResposta.getTemaTCC().getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), aprovacaoResposta.getTemaTCC().getDescricao());
            assertEquals(temaTccDTO1.getAreasDeEstudo(), aprovacaoResposta.getTemaTCC().getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, aprovacaoResposta.getTemaTCC().getStatus());
            assertEquals(professor1.getId(), aprovacaoResposta.getProfessorId());
            assertEquals(aluno1.getId(), aprovacaoResposta.getAlunoId());
            assertEquals(false, aprovacaoResposta.getAprovacao());
            assertEquals(mensagemExpected.toString(), aprovacaoResposta.getMensagemDecisao());
            assertTrue(alunoCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(aprovacaoResposta.getTemaTCC().getId())));
        }

        @Test
        @DisplayName("Deve aprovar duas solicitações de dois temas de TCC cadastrados por dois alunos")
        void aprovaDuasSolicitacoesFeitasPorAlunos() throws Exception {
            // Arrange
            StringBuilder notificacaoExpected1 = new StringBuilder("Mensagem para o Coordenador: ")
                    .append("\n")
                    .append("O tema de TCC de título: ")
                    .append(temaTccDTO1.getTitulo())
                    .append("\n")
                    .append("foi APROVADO pelo professor ")
                    .append(professor1.getNome());

            StringBuilder notificacaoExpected2 = new StringBuilder("Mensagem para o Coordenador: ")
                    .append("\n")
                    .append("O tema de TCC de título: ")
                    .append(temaTccDTO2.getTitulo())
                    .append("\n")
                    .append("foi APROVADO pelo professor ")
                    .append(professor1.getNome());

            StringBuilder mensagemExpected1 = new StringBuilder("Caro aluno ")
                    .append(aluno1.getNome())
                    .append(".")
                    .append("\n")
                    .append("Sua solicitação foi ")
                    .append("APROVADA")
                    .append("\n")
                    .append("\n")
                    .append("Resposta do professor:")
                    .append("\n")
                    .append("Esse tema é bom demais.");

            StringBuilder mensagemExpected2 = new StringBuilder("Caro aluno ")
                    .append(aluno2.getNome())
                    .append(".")
                    .append("\n")
                    .append("Sua solicitação foi ")
                    .append("APROVADA")
                    .append("\n")
                    .append("\n")
                    .append("Resposta do professor:")
                    .append("\n")
                    .append("Esse tema é bom demais.");

            String uriCadastrarTema1 = "/alunos/" + aluno1.getId() + "/cadastrar-tema-tcc";
            String uriCadastrarTema2 = "/alunos/" + aluno2.getId() + "/cadastrar-tema-tcc";
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema2)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO2)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCAluno1 = objectMapper.readValue(respostaJsonString1, TemaTCC.class);
            TemaTCC temaTCCAluno2 = objectMapper.readValue(respostaJsonString2, TemaTCC.class);

            SolicitacaoDTO solicitacaoDto1 = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCAluno1.getId())
                    .build();

            SolicitacaoDTO solicitacaoDto2 = SolicitacaoDTO.builder()
                    .alunoId(aluno2.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCAluno2.getId())
                    .build();

            String uriSolicitarOrientacao = "/alunos/solicitar-orientacao-tcc-aluno";
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto1)))
                    .andExpect(status().isCreated());

            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto2)))
                    .andExpect(status().isCreated());

            // Act
            SolicitacaoAprovacaoRequestDTO aprovacaoDto1 = SolicitacaoAprovacaoRequestDTO.builder()
                    .professorId(professor1.getId())
                    .alunoId(aluno1.getId())
                    .temaTccId(temaTCCAluno1.getId())
                    .mensagemDecisao("Esse tema é bom demais.")
                    .aprovacao(true)
                    .build();

            SolicitacaoAprovacaoRequestDTO aprovacaoDto2 = SolicitacaoAprovacaoRequestDTO.builder()
                    .professorId(professor1.getId())
                    .alunoId(aluno2.getId())
                    .temaTccId(temaTCCAluno2.getId())
                    .mensagemDecisao("Esse tema é bom demais.")
                    .aprovacao(true)
                    .build();

            String uriAprovacaoTema = URI_TEMPLATE + "/definir-aprovacao-tema-tcc";
            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .patch(uriAprovacaoTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(aprovacaoDto1)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString4 = driver.perform(MockMvcRequestBuilders
                            .patch(uriAprovacaoTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(aprovacaoDto2)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SolicitacaoAprovacaoResponseDTO aprovacaoResposta1 = objectMapper.readValue(respostaJsonString3, SolicitacaoAprovacaoResponseDTO.class);
            SolicitacaoAprovacaoResponseDTO aprovacaoResposta2 = objectMapper.readValue(respostaJsonString4, SolicitacaoAprovacaoResponseDTO.class);
            Aluno alunoCadastrado1 = alunoRepository.findById(aluno1.getId()).orElseThrow(AlunoNotFoundException::new);
            Aluno alunoCadastrado2 = alunoRepository.findById(aluno2.getId()).orElseThrow(AlunoNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), aprovacaoResposta1.getTemaTCC().getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), aprovacaoResposta1.getTemaTCC().getDescricao());
            assertEquals(temaTccDTO1.getAreasDeEstudo(), aprovacaoResposta1.getTemaTCC().getAreasDeEstudo());
            assertEquals(StatusTCCEnum.ALOCADO, aprovacaoResposta1.getTemaTCC().getStatus());
            assertEquals(professor1.getId(), aprovacaoResposta1.getProfessorId());
            assertEquals(aluno1.getId(), aprovacaoResposta1.getAlunoId());
            assertEquals(true, aprovacaoResposta1.getAprovacao());
            assertEquals(notificacaoExpected1.toString(), aprovacaoResposta1.getNotificacaoCoordenador());
            assertEquals(mensagemExpected1.toString(), aprovacaoResposta1.getMensagemDecisao());
            assertTrue(alunoCadastrado1.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(aprovacaoResposta1.getTemaTCC().getId())));

            assertEquals(temaTccDTO2.getTitulo(), aprovacaoResposta2.getTemaTCC().getTitulo());
            assertEquals(temaTccDTO2.getDescricao(), aprovacaoResposta2.getTemaTCC().getDescricao());
            assertEquals(temaTccDTO2.getAreasDeEstudo(), aprovacaoResposta2.getTemaTCC().getAreasDeEstudo());
            assertEquals(StatusTCCEnum.ALOCADO, aprovacaoResposta2.getTemaTCC().getStatus());
            assertEquals(professor1.getId(), aprovacaoResposta2.getProfessorId());
            assertEquals(aluno2.getId(), aprovacaoResposta2.getAlunoId());
            assertEquals(true, aprovacaoResposta2.getAprovacao());
            assertEquals(notificacaoExpected2.toString(), aprovacaoResposta2.getNotificacaoCoordenador());
            assertEquals(mensagemExpected2.toString(), aprovacaoResposta2.getMensagemDecisao());
            assertTrue(alunoCadastrado2.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(aprovacaoResposta2.getTemaTCC().getId())));
        }

        @Test
        @DisplayName("Deve desaprovar duas solicitações de dois temas de TCC cadastrados por dois alunos")
        void desaprovaDuasSolicitacoesFeitasPorAlunos() throws Exception {
            // Arrange
            StringBuilder mensagemExpected1 = new StringBuilder("Caro aluno ")
                    .append(aluno1.getNome())
                    .append(".")
                    .append("\n")
                    .append("Sua solicitação foi ")
                    .append("NEGADA")
                    .append("\n")
                    .append("\n")
                    .append("Resposta do professor:")
                    .append("\n")
                    .append("Esse tema é ruim que dói.");

            StringBuilder mensagemExpected2 = new StringBuilder("Caro aluno ")
                    .append(aluno2.getNome())
                    .append(".")
                    .append("\n")
                    .append("Sua solicitação foi ")
                    .append("NEGADA")
                    .append("\n")
                    .append("\n")
                    .append("Resposta do professor:")
                    .append("\n")
                    .append("Esse tema é ruim que dói.");

            String uriCadastrarTema1 = "/alunos/" + aluno1.getId() + "/cadastrar-tema-tcc";
            String uriCadastrarTema2 = "/alunos/" + aluno2.getId() + "/cadastrar-tema-tcc";
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema2)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO2)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCAluno1 = objectMapper.readValue(respostaJsonString1, TemaTCC.class);
            TemaTCC temaTCCAluno2 = objectMapper.readValue(respostaJsonString2, TemaTCC.class);

            SolicitacaoDTO solicitacaoDto1 = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCAluno1.getId())
                    .build();

            SolicitacaoDTO solicitacaoDto2 = SolicitacaoDTO.builder()
                    .alunoId(aluno2.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCAluno2.getId())
                    .build();

            String uriSolicitarOrientacao = "/alunos/solicitar-orientacao-tcc-aluno";
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto1)))
                    .andExpect(status().isCreated());

            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto2)))
                    .andExpect(status().isCreated());

            // Act
            SolicitacaoAprovacaoRequestDTO aprovacaoDto1 = SolicitacaoAprovacaoRequestDTO.builder()
                    .professorId(professor1.getId())
                    .alunoId(aluno1.getId())
                    .temaTccId(temaTCCAluno1.getId())
                    .mensagemDecisao("Esse tema é ruim que dói.")
                    .aprovacao(false)
                    .build();

            SolicitacaoAprovacaoRequestDTO aprovacaoDto2 = SolicitacaoAprovacaoRequestDTO.builder()
                    .professorId(professor1.getId())
                    .alunoId(aluno2.getId())
                    .temaTccId(temaTCCAluno2.getId())
                    .mensagemDecisao("Esse tema é ruim que dói.")
                    .aprovacao(false)
                    .build();

            String uriAprovacaoTema = URI_TEMPLATE + "/definir-aprovacao-tema-tcc";
            String respostaJsonString3 = driver.perform(MockMvcRequestBuilders
                            .patch(uriAprovacaoTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(aprovacaoDto1)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJsonString4 = driver.perform(MockMvcRequestBuilders
                            .patch(uriAprovacaoTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(aprovacaoDto2)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SolicitacaoAprovacaoResponseDTO aprovacaoResposta1 = objectMapper.readValue(respostaJsonString3, SolicitacaoAprovacaoResponseDTO.class);
            SolicitacaoAprovacaoResponseDTO aprovacaoResposta2 = objectMapper.readValue(respostaJsonString4, SolicitacaoAprovacaoResponseDTO.class);
            Aluno alunoCadastrado1 = alunoRepository.findById(aluno1.getId()).orElseThrow(AlunoNotFoundException::new);
            Aluno alunoCadastrado2 = alunoRepository.findById(aluno2.getId()).orElseThrow(AlunoNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), aprovacaoResposta1.getTemaTCC().getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), aprovacaoResposta1.getTemaTCC().getDescricao());
            assertEquals(temaTccDTO1.getAreasDeEstudo(), aprovacaoResposta1.getTemaTCC().getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, aprovacaoResposta1.getTemaTCC().getStatus());
            assertEquals(professor1.getId(), aprovacaoResposta1.getProfessorId());
            assertEquals(aluno1.getId(), aprovacaoResposta1.getAlunoId());
            assertEquals(false, aprovacaoResposta1.getAprovacao());
            assertEquals(mensagemExpected1.toString(), aprovacaoResposta1.getMensagemDecisao());
            assertTrue(alunoCadastrado1.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(aprovacaoResposta1.getTemaTCC().getId())));

            assertEquals(temaTccDTO2.getTitulo(), aprovacaoResposta2.getTemaTCC().getTitulo());
            assertEquals(temaTccDTO2.getDescricao(), aprovacaoResposta2.getTemaTCC().getDescricao());
            assertEquals(temaTccDTO2.getAreasDeEstudo(), aprovacaoResposta2.getTemaTCC().getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, aprovacaoResposta2.getTemaTCC().getStatus());
            assertEquals(professor1.getId(), aprovacaoResposta2.getProfessorId());
            assertEquals(aluno2.getId(), aprovacaoResposta2.getAlunoId());
            assertEquals(false, aprovacaoResposta2.getAprovacao());
            assertEquals(mensagemExpected2.toString(), aprovacaoResposta2.getMensagemDecisao());
            assertTrue(alunoCadastrado2.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(aprovacaoResposta2.getTemaTCC().getId())));
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao aprovar uma solicitação de um tema de TCC não cadastrado por um aluno")
        void aprovaSolicitacaoTemaNaoCadastradoAluno() throws Exception {
            // Arrange
            String uriCadastrarTema = "/alunos/" + aluno1.getId() + "/cadastrar-tema-tcc";
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCAluno = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            SolicitacaoDTO solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCAluno.getId())
                    .build();

            String uriSolicitarOrientacao = "/alunos/solicitar-orientacao-tcc-aluno";
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isCreated());

            // Act
            SolicitacaoAprovacaoRequestDTO aprovacaoDto = SolicitacaoAprovacaoRequestDTO.builder()
                    .professorId(professor1.getId())
                    .alunoId(aluno1.getId())
                    .temaTccId(123L)
                    .mensagemDecisao("Esse tema é bom demais.")
                    .aprovacao(true)
                    .build();

            String uriAprovacaoTema = URI_TEMPLATE + "/definir-aprovacao-tema-tcc";
            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .patch(uriAprovacaoTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(aprovacaoDto)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString2, CustomErrorType.class);

            // Assert
            assertEquals("Tema não encontrado!", erro.getMessage());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao aprovar uma solicitação de um tema de TCC de um aluno não cadastrado")
        void aprovaSolicitacaoAlunoNaoCadastrado() throws Exception {
            // Arrange
            String uriCadastrarTema = "/alunos/" + aluno1.getId() + "/cadastrar-tema-tcc";
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCAluno = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            SolicitacaoDTO solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCAluno.getId())
                    .build();

            String uriSolicitarOrientacao = "/alunos/solicitar-orientacao-tcc-aluno";
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isCreated());

            // Act
            SolicitacaoAprovacaoRequestDTO aprovacaoDto = SolicitacaoAprovacaoRequestDTO.builder()
                    .professorId(professor1.getId())
                    .alunoId(123L)
                    .temaTccId(temaTCCAluno.getId())
                    .mensagemDecisao("Esse tema é bom demais.")
                    .aprovacao(true)
                    .build();

            String uriAprovacaoTema = URI_TEMPLATE + "/definir-aprovacao-tema-tcc";
            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .patch(uriAprovacaoTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(aprovacaoDto)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString2, CustomErrorType.class);

            // Assert
            assertEquals("Aluno não encontrado!", erro.getMessage());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao aprovar uma solicitação de um tema de TCC com um professor não cadastrado")
        void aprovaSolicitacaoProfessorNaoCadastrado() throws Exception {
            // Arrange
            String uriCadastrarTema = "/alunos/" + aluno1.getId() + "/cadastrar-tema-tcc";
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCAluno = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            SolicitacaoDTO solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCAluno.getId())
                    .build();

            String uriSolicitarOrientacao = "/alunos/solicitar-orientacao-tcc-aluno";
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isCreated());

            // Act
            SolicitacaoAprovacaoRequestDTO aprovacaoDto = SolicitacaoAprovacaoRequestDTO.builder()
                    .professorId(123L)
                    .alunoId(aluno1.getId())
                    .temaTccId(temaTCCAluno.getId())
                    .mensagemDecisao("Esse tema é bom demais.")
                    .aprovacao(true)
                    .build();

            String uriAprovacaoTema = URI_TEMPLATE + "/definir-aprovacao-tema-tcc";
            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .patch(uriAprovacaoTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(aprovacaoDto)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType erro = objectMapper.readValue(respostaJsonString2, CustomErrorType.class);

            // Assert
            assertEquals("Professor não encontrado!", erro.getMessage());
        }

    }

    @Nested
    @DisplayName("Verificações de aprovação de solicitações de tema de TCC do professor por parte do professor")
    class VerificacoesAprovacaoSolicitacoesTemaTccProfessor {

        @Test
        @DisplayName("Deve aprovar uma solicitação de um tema de TCC cadastrado por um professor")
        void aprovaSolicitacaoFeitaPorAluno() throws Exception {
            // Arrange
            StringBuilder notificacaoExpected = new StringBuilder("Mensagem para o Coordenador: ")
                    .append("\n")
                    .append("O tema de TCC de título: ")
                    .append(temaTccDTO1.getTitulo())
                    .append("\n")
                    .append("foi APROVADO pelo professor ")
                    .append(professor1.getNome());

            StringBuilder mensagemExpected = new StringBuilder("Caro aluno ")
                    .append(aluno1.getNome())
                    .append(".")
                    .append("\n")
                    .append("Sua solicitação foi ")
                    .append("APROVADA")
                    .append("\n")
                    .append("\n")
                    .append("Resposta do professor:")
                    .append("\n")
                    .append("Você, você é uma boa pessoa Morty, vejo potencial em você *uph*.");

            String uriCadastrarTema = URI_TEMPLATE + "/" + professor1.getId() + "/cadastrar-tema-tcc";
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCProfessor = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            SolicitacaoDTO solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCProfessor.getId())
                    .build();

            String uriSolicitarOrientacao = "/alunos/solicitar-orientacao-tcc-professor";
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isCreated());

            // Act
            SolicitacaoAprovacaoRequestDTO aprovacaoDto = SolicitacaoAprovacaoRequestDTO.builder()
                    .professorId(professor1.getId())
                    .alunoId(aluno1.getId())
                    .temaTccId(temaTCCProfessor.getId())
                    .mensagemDecisao("Você, você é uma boa pessoa Morty, vejo potencial em você *uph*.")
                    .aprovacao(true)
                    .build();

            String uriAprovacaoTema = URI_TEMPLATE + "/definir-aprovacao-tema-tcc";
            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .patch(uriAprovacaoTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(aprovacaoDto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SolicitacaoAprovacaoResponseDTO aprovacaoResposta = objectMapper.readValue(respostaJsonString2, SolicitacaoAprovacaoResponseDTO.class);
            Professor professorCadastrado = professorRepository.findById(professor1.getId()).orElseThrow(ProfessorNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), aprovacaoResposta.getTemaTCC().getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), aprovacaoResposta.getTemaTCC().getDescricao());
            assertEquals(temaTccDTO1.getAreasDeEstudo(), aprovacaoResposta.getTemaTCC().getAreasDeEstudo());
            assertEquals(StatusTCCEnum.ALOCADO, aprovacaoResposta.getTemaTCC().getStatus());
            assertEquals(professor1.getId(), aprovacaoResposta.getProfessorId());
            assertEquals(aluno1.getId(), aprovacaoResposta.getAlunoId());
            assertEquals(true, aprovacaoResposta.getAprovacao());
            assertEquals(notificacaoExpected.toString(), aprovacaoResposta.getNotificacaoCoordenador());
            assertEquals(mensagemExpected.toString(), aprovacaoResposta.getMensagemDecisao());
            assertTrue(professorCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(aprovacaoResposta.getTemaTCC().getId())));
        }

        @Test
        @DisplayName("Deve desaprovar uma solicitação de um tema de TCC cadastrado por um professor")
        void desaprovaSolicitacaoFeitaPorAluno() throws Exception {
            // Arrange
            StringBuilder mensagemExpected = new StringBuilder("Caro aluno ")
                    .append(aluno1.getNome())
                    .append(".")
                    .append("\n")
                    .append("Sua solicitação foi ")
                    .append("NEGADA")
                    .append("\n")
                    .append("\n")
                    .append("Resposta do professor:")
                    .append("\n")
                    .append("Mas que porcaria é essa aqui Morty? *uph* Quem, quem você pensa que é?");

            String uriCadastrarTema = URI_TEMPLATE + "/" + professor1.getId() + "/cadastrar-tema-tcc";
            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCCProfessor = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            SolicitacaoDTO solicitacaoDto = SolicitacaoDTO.builder()
                    .alunoId(aluno1.getId())
                    .professorId(professor1.getId())
                    .temaTCCId(temaTCCProfessor.getId())
                    .build();

            String uriSolicitarOrientacao = "/alunos/solicitar-orientacao-tcc-professor";
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDto)))
                    .andExpect(status().isCreated());

            // Act
            SolicitacaoAprovacaoRequestDTO aprovacaoDto = SolicitacaoAprovacaoRequestDTO.builder()
                    .professorId(professor1.getId())
                    .alunoId(aluno1.getId())
                    .temaTccId(temaTCCProfessor.getId())
                    .mensagemDecisao("Mas que porcaria é essa aqui Morty? *uph* Quem, quem você pensa que é?")
                    .aprovacao(false)
                    .build();

            String uriAprovacaoTema = URI_TEMPLATE + "/definir-aprovacao-tema-tcc";
            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .patch(uriAprovacaoTema)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(aprovacaoDto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SolicitacaoAprovacaoResponseDTO aprovacaoResposta = objectMapper.readValue(respostaJsonString2, SolicitacaoAprovacaoResponseDTO.class);
            Professor professorCadastrado = professorRepository.findById(professor1.getId()).orElseThrow(ProfessorNotFoundException::new);

            // Assert
            assertEquals(temaTccDTO1.getTitulo(), aprovacaoResposta.getTemaTCC().getTitulo());
            assertEquals(temaTccDTO1.getDescricao(), aprovacaoResposta.getTemaTCC().getDescricao());
            assertEquals(temaTccDTO1.getAreasDeEstudo(), aprovacaoResposta.getTemaTCC().getAreasDeEstudo());
            assertEquals(StatusTCCEnum.NOVO, aprovacaoResposta.getTemaTCC().getStatus());
            assertEquals(professor1.getId(), aprovacaoResposta.getProfessorId());
            assertEquals(aluno1.getId(), aprovacaoResposta.getAlunoId());
            assertEquals(false, aprovacaoResposta.getAprovacao());
            assertEquals(mensagemExpected.toString(), aprovacaoResposta.getMensagemDecisao());
            assertTrue(professorCadastrado.getTemasTCCCadastrados()
                    .stream()
                    .map(TemaTCC::getId)
                    .anyMatch(id -> id.equals(aprovacaoResposta.getTemaTCC().getId())));
        }
    }

    @Nested
    @DisplayName("Verificações listar as solicitacoes de orientacao dos alunos nos temas de TCC cadastrados pelo professor.")
    class VerificacoesListarSolicitacoesOrientacaoTemasTccProfessor {

        Aluno aluno5;
        Aluno aluno6;
        Professor professor5;
        TemaTCCDTO temaTccDTO5;
        TemaTCCDTO temaTccDTO6;
        AreaDeEstudo areaDeEstudo6;

        Set<AreaDeEstudo> areasDeEstudo;

        @BeforeEach
        void setup() {
            aluno5 = alunoRepository.save(Aluno.builder()
                    .nome("Bruce Wayne")
                    .matricula("121299999")
                    .email("iam.batman@gmail.com")
                    .periodoConclusao("2024.2")
                    .build());

            aluno6 = alunoRepository.save(Aluno.builder()
                    .nome("Fada Flora")
                    .matricula("121266699")
                    .email("flora.winx@gmail.com")
                    .periodoConclusao("2024.2")
                    .build());

            professor5 = professorRepository.save(Professor.builder()
                    .nome("Alan Turing")
                    .email("pai.computacao@gmail.com")
                    .quota(1)
                    .build());

            temaTccDTO6 = TemaTCCDTO.builder()
                    .titulo("Como a magia Winx vai salvar o mundo")
                    .descricao("Este trabalho propõe explorar como a magia das Winxs...")
                    .areasDeEstudo(areasDeEstudo)
                    .build();

            temaTccDTO5 = TemaTCCDTO.builder()
                    .titulo("Alan Turing: Explorando a sua hitória e trajetória")
                    .descricao("Este trabalho propõe explorar como Alan Turing...")
                    .areasDeEstudo(areasDeEstudo)
                    .build();

            areaDeEstudo6 = areaDeEstudoRepository.save(AreaDeEstudo.builder()
                    .nome("Magia Winxs")
                    .build());
            eventManager.cadastrarAreaDeEstudo(areaDeEstudo6);

            areasDeEstudo = new HashSet<>();
            areasDeEstudo.add(areaDeEstudo6);

            temaTccDTO5.setAreasDeEstudo(areasDeEstudo);
            temaTccDTO6.setAreasDeEstudo(areasDeEstudo);
        }

        @Test
        @DisplayName("Deve listar as solicitacoes de orientacao dos alunos, para todos os temas de TCC cadastrados pelo professor")
        void listarSolicitacoesOrientacaoTodosTemasTccProfessor() throws Exception {
            temaTCCRepository.deleteAll();
            solicitacaoRepository.deleteAll();

            // Arrange
            String uriCadastrarTemaProfessor = URI_TEMPLATE + "/" + professor5.getId() + "/cadastrar-tema-tcc";
            String uriListarSolicitacoes = URI_TEMPLATE + "/" + professor5.getId() + "/solicitacoes-orientacao-alunos";

            String respostaJsonString1 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTemaProfessor)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO5)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCC5 = objectMapper.readValue(respostaJsonString1, TemaTCC.class);

            String respostaJsonString2 = driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTemaProfessor)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO6)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            TemaTCC temaTCC6 = objectMapper.readValue(respostaJsonString2, TemaTCC.class);

            solicitacaoDTO1 = SolicitacaoDTO.builder()
                    .alunoId(aluno5.getId())
                    .professorId(professor5.getId())
                    .temaTCCId(temaTCC5.getId())
                    .build();

            solicitacaoDTO2 = SolicitacaoDTO.builder()
                    .alunoId(aluno6.getId())
                    .professorId(professor5.getId())
                    .temaTCCId(temaTCC6.getId())
                    .build();

            String uriSolicitarOrientacao = "/alunos" + "/solicitar-orientacao-tcc-professor";
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDTO1)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
            driver.perform(MockMvcRequestBuilders
                            .post(uriSolicitarOrientacao)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(solicitacaoDTO2)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .get(uriListarSolicitacoes)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            ArrayList<SolicitacaoDTO> solicitacoes = objectMapper.readValue(respostaJsonString, new TypeReference<ArrayList<SolicitacaoDTO>>() {
            });

            // Assert
            assertEquals(solicitacoes.size(), 2);
            assertTrue(solicitacoes.stream()
                    .map(SolicitacaoDTO::getAlunoId)
                    .anyMatch(alunoId -> alunoId.equals(aluno5.getId())));
            assertTrue(solicitacoes.stream()
                    .map(SolicitacaoDTO::getAlunoId)
                    .anyMatch(alunoId -> alunoId.equals(aluno6.getId())));
            assertTrue(solicitacoes.stream()
                    .map(SolicitacaoDTO::getProfessorId)
                    .anyMatch(professorId -> professorId.equals(professor5.getId())));
            assertTrue(solicitacoes.stream()
                    .map(SolicitacaoDTO::getTemaTCCId)
                    .anyMatch(temaTCCId -> temaTCCId.equals(temaTCC5.getId())));
            assertTrue(solicitacoes.stream()
                    .map(SolicitacaoDTO::getTemaTCCId)
                    .anyMatch(temaTCCId -> temaTCCId.equals(temaTCC6.getId())));
        }


        @Test
        @DisplayName("Deve listar nenhuma solicitacao ao buscar as solicitacoes de orientacao dos alunos, " +
                "para todos os temas de TCC cadastrado por um professor, não tendo solicitações a nenhum tema cadastrado por ele")
        void listarSolicitacoesOrientacaoTodosTemasTccProfessorSemSolicitacoes() throws Exception {
            // Arrange
            String uriCadastrarTemaProfessor = URI_TEMPLATE + "/" + professor1.getId() + "/cadastrar-tema-tcc";
            String uriListarSolicitacoes = URI_TEMPLATE + "/" + professor1.getId() + "/solicitacoes-orientacao-alunos";

            driver.perform(MockMvcRequestBuilders
                            .post(uriCadastrarTemaProfessor)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(temaTccDTO1)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Act
            String respostaJsonString = driver.perform(MockMvcRequestBuilders
                            .get(uriListarSolicitacoes)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            ArrayList<SolicitacaoDTO> solicitacoes = objectMapper.readValue(respostaJsonString, new TypeReference<ArrayList<SolicitacaoDTO>>() {
            });

            // Assert
            assertTrue(solicitacoes.isEmpty());
        }
    }
}