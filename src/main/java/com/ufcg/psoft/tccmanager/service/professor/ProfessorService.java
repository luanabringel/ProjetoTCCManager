package com.ufcg.psoft.tccmanager.service.professor;

import com.ufcg.psoft.tccmanager.dto.professor.ProfessorAtualizaQuotaDto;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorDTO;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorInteresseAreaResponseDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoAprovacaoResponseDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCAlunoGetAllDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCProfessorGetAllDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoAprovacaoRequestDTO;
import com.ufcg.psoft.tccmanager.model.Professor;

import java.util.Collection;

public interface ProfessorService {
    Professor criarProfessor(ProfessorDTO professorDto);
    Professor modificarProfessor(Long id, ProfessorDTO professorDto);
    Professor buscarProfessorId(Long id);
    Collection<Professor> listarTodosProfessores();
    void removerProfessor(Long id);


    ProfessorInteresseAreaResponseDTO adicionarAreaDeEstudoInteressada(Long professorId, Long areaDeEstudoId);
    boolean verificaQuota(Professor professor);
    Collection<TemaTCCProfessorGetAllDTO> listarTemasTccCadastradosPorProfessor(Long professorId);
    Professor atualizaQuota(Long professorId, ProfessorAtualizaQuotaDto novaQuota);
    Collection<TemaTCCAlunoGetAllDTO> listarTemasTccCadastradosPorAlunos();
    Collection<SolicitacaoDTO> listarSolicitacoesDeOrientacaoPorProfessor(Long professorId);
    SolicitacaoAprovacaoResponseDTO definirAprovacaoSolicitacao(SolicitacaoAprovacaoRequestDTO requestDTO);
}