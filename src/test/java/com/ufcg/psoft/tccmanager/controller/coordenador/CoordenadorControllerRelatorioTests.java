package com.ufcg.psoft.tccmanager.controller.coordenador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.tccmanager.dto.coordenador.CoordenadorDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoAprovacaoRequestDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCDTO;
import com.ufcg.psoft.tccmanager.model.Aluno;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import com.ufcg.psoft.tccmanager.model.Professor;
import com.ufcg.psoft.tccmanager.model.TemaTCC;
import com.ufcg.psoft.tccmanager.repository.*;
import com.ufcg.psoft.tccmanager.service.professor.ProfessorServiceImp;
import com.ufcg.psoft.tccmanager.service.solicitacao.SolicitacaoServiceImp;
import com.ufcg.psoft.tccmanager.service.temaTCC.TemaTCCAlunoServiceImp;
import com.ufcg.psoft.tccmanager.service.temaTCC.TemaTCCProfessorServiceImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Classe de teste de relatorios em CoordenadorController")
public class CoordenadorControllerRelatorioTests {
    private final String URI = "/coordenador/relatorio";

    @Autowired
    MockMvc driver;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProfessorRepository professorRepository;
    @Autowired
    AlunoRepository alunoRepository;
    @Autowired
    AreaDeEstudoRepository areaDeEstudoRepository;
    @Autowired
    CoordenadorRepository coordenadorRepository;
    @Autowired
    TemaTCCRepository temaTCCRepository;
    @Autowired
    SolicitacaoRepository solicitacaoRepository;
    @Autowired
    TemaTCCProfessorServiceImp temaTCCProfessorServiceImp;
    @Autowired
    TemaTCCAlunoServiceImp temaTCCAlunoServiceImp;
    @Autowired
    SolicitacaoServiceImp solicitacaoServiceImp;
    @Autowired
    ProfessorServiceImp professorServiceImp;
    Professor professor1, professor2;
    Aluno aluno1, aluno2;
    AreaDeEstudo areaDeEstudo1, areaDeEstudo2;
    TemaTCCDTO temaTccDTO1, temaTccDTO2;
    TemaTCC temaTcc1, temaTcc2;
    CoordenadorDTO coordenadorDTO;

    @BeforeEach
    public void setup() throws Exception {
        this.objectMapper = new ObjectMapper();
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


        areaDeEstudo1 = areaDeEstudoRepository.save(AreaDeEstudo.builder()
                .nome("Banco de Dados")
                .build());

        areaDeEstudo2 = areaDeEstudoRepository.save(AreaDeEstudo.builder()
                .nome("Inteligência Artificial")
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

        String tema1Str = driver.perform(MockMvcRequestBuilders
                        .post("/professores/" + professor1.getId() + "/cadastrar-tema-tcc")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(temaTccDTO1)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String tema2Str = driver.perform(MockMvcRequestBuilders
                        .post("/professores/" + professor1.getId() + "/cadastrar-tema-tcc")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(temaTccDTO2)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        temaTcc1 = objectMapper.readValue(tema1Str, TemaTCC.class);
        temaTcc2 = objectMapper.readValue(tema2Str, TemaTCC.class);
    }

    @AfterEach
    void tearDown() {
        solicitacaoRepository.deleteAll();
        coordenadorRepository.deleteAll();
        alunoRepository.deleteAll();
        professorRepository.deleteAll();
        temaTCCRepository.deleteAll();
    }

    @Test
    @DisplayName("Teste de relatorio para 1 aluno orientado")
    void testRelatorio1Aluno() throws Exception {
        this.solicitacaoServiceImp.criarSolicitacaoTCCCadastradoPeloAluno(SolicitacaoDTO.builder()
                        .alunoId(aluno1.getId())
                        .professorId(professor1.getId())
                        .temaTCCId(temaTcc1.getId())
                        .build());
        this.professorServiceImp.definirAprovacaoSolicitacao(SolicitacaoAprovacaoRequestDTO.builder()
                .professorId(professor1.getId())
                .alunoId(aluno1.getId())
                .temaTccId(temaTcc1.getId())
                .aprovacao(true)
                .build());

        String responseJsonString = driver.perform(get(URI)
                        .header("senhaCoordenador", coordenadorDTO.getSenha()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Relatório de orientações de TCC em curso e alunos sem orientador\n" +
                "Orientações em curso:\n" +
                "Aluno: Antonio Santos\n" +
                "Professor: Raimundo Nonato\n" +
                "Tema: Otimização de Consultas em Banco de Dados Distribuídos\n" +
                "Areas de estudo: Banco de Dados, Inteligência Artificial \n" +
                "\n" +
                "Alunos sem orientador:"
                + "\n", responseJsonString);
    }

    @Test
    @DisplayName("Teste de relatorio para 2 alunos orientados")
    void testeRelatorio2Alunos() throws Exception {
        this.solicitacaoServiceImp.criarSolicitacaoTCCCadastradoPeloAluno(SolicitacaoDTO.builder()
                        .alunoId(aluno1.getId())
                        .professorId(professor1.getId())
                        .temaTCCId(temaTcc1.getId())
                        .build());
        this.professorServiceImp.definirAprovacaoSolicitacao(SolicitacaoAprovacaoRequestDTO.builder()
                .professorId(professor1.getId())
                .alunoId(aluno1.getId())
                .temaTccId(temaTcc1.getId())
                .aprovacao(true)
                .build());

        this.temaTCCProfessorServiceImp.cadastrarTemaTCC(professor1.getId(), temaTccDTO2);
        this.solicitacaoServiceImp.criarSolicitacaoTCCCadastradoPeloAluno(SolicitacaoDTO.builder()
                        .alunoId(aluno2.getId())
                        .professorId(professor1.getId())
                        .temaTCCId(temaTcc2.getId())
                        .build());
        this.professorServiceImp.definirAprovacaoSolicitacao(SolicitacaoAprovacaoRequestDTO.builder()
                .professorId(professor1.getId())
                .alunoId(aluno2.getId())
                .temaTccId(temaTcc2.getId())
                .aprovacao(true)
                .build());

        String responseJsonString = driver.perform(get(URI)
                        .header("senhaCoordenador", coordenadorDTO.getSenha()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Relatório de orientações de TCC em curso e alunos sem orientador\n" +
                "Orientações em curso:\n" +
                "Aluno: Antonio Santos\n" +
                "Professor: Raimundo Nonato\n" +
                "Tema: Otimização de Consultas em Banco de Dados Distribuídos\n" +
                "Areas de estudo: Banco de Dados, Inteligência Artificial \n" +
                "\n" +
                "Aluno: José Ferreira\n" +
                "Professor: Raimundo Nonato\n" +
                "Tema: Aplicação de Redes Neurais Convolucionais para Reconhecimento...\n" +
                "Areas de estudo: Banco de Dados, Inteligência Artificial \n" +
                "\n" +
                "Alunos sem orientador:"
                + "\n", responseJsonString);
    }

    @Test
    @DisplayName("Teste de relatorio para 1 aluno orientado e 1 sem orientador")
    void testeRelatorio2Alunos1SemOrientador() throws Exception {
        this.solicitacaoServiceImp.criarSolicitacaoTCCCadastradoPeloAluno(SolicitacaoDTO.builder()
                .alunoId(aluno1.getId())
                .professorId(professor1.getId())
                .temaTCCId(temaTcc1.getId())
                .build());
        this.professorServiceImp.definirAprovacaoSolicitacao(SolicitacaoAprovacaoRequestDTO.builder()
                .professorId(professor1.getId())
                .alunoId(aluno1.getId())
                .temaTccId(temaTcc1.getId())
                .aprovacao(true)
                .build());

        this.solicitacaoServiceImp.criarSolicitacaoTCCCadastradoPeloAluno(SolicitacaoDTO.builder()
                .alunoId(aluno2.getId())
                .professorId(professor1.getId())
                .temaTCCId(temaTcc2.getId())
                .build());

        String responseJsonString = driver.perform(get(URI)
                        .header("senhaCoordenador", coordenadorDTO.getSenha()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Relatório de orientações de TCC em curso e alunos sem orientador\n" +
                "Orientações em curso:\n" +
                "Aluno: Antonio Santos\n" +
                "Professor: Raimundo Nonato\n" +
                "Tema: Otimização de Consultas em Banco de Dados Distribuídos\n" +
                "Areas de estudo: Banco de Dados, Inteligência Artificial \n" +
                "\n" +
                "Alunos sem orientador:\n"
                + "Aluno: José Ferreira\n", responseJsonString);
    }

    @Test
    @DisplayName("Teste de relatorio para 2 alunos sem orientador")
    void testeRelatorio2AlunosSemOrientador() throws Exception {
        this.solicitacaoServiceImp.criarSolicitacaoTCCCadastradoPeloAluno(SolicitacaoDTO.builder()
                .alunoId(aluno1.getId())
                .professorId(professor1.getId())
                .temaTCCId(temaTcc1.getId())
                .build());

        this.solicitacaoServiceImp.criarSolicitacaoTCCCadastradoPeloAluno(SolicitacaoDTO.builder()
                .alunoId(aluno2.getId())
                .professorId(professor1.getId())
                .temaTCCId(temaTcc2.getId())
                .build());

        String responseJsonString = driver.perform(get(URI)
                        .header("senhaCoordenador", coordenadorDTO.getSenha()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Relatório de orientações de TCC em curso e alunos sem orientador\n" +
                "Orientações em curso:\n" +
                "Alunos sem orientador:\n" +
                "Aluno: Antonio Santos\n" +
                "Aluno: José Ferreira\n", responseJsonString);
    }

    @Test
    @DisplayName("Teste para 0 alunos orientados e 0 alunos sem orientador (nenhum aluno solicitou orientação)")
    void testeRelatorioSemAlunos() throws Exception {
        String responseJsonString = driver.perform(get(URI)
                        .header("senhaCoordenador", coordenadorDTO.getSenha()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Relatório de orientações de TCC em curso e alunos sem orientador\n" +
                "Orientações em curso:\n" +
                "Alunos sem orientador:\n", responseJsonString);
    }
}
