package com.ufcg.psoft.tccmanager.service.solicitacao;

import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.AlterarStatusDTO;
import com.ufcg.psoft.tccmanager.events.EventManager;
import com.ufcg.psoft.tccmanager.model.TemaTCC;
import com.ufcg.psoft.tccmanager.model.Solicitacao;
import com.ufcg.psoft.tccmanager.model.enums.SolicitacaoEnum;
import com.ufcg.psoft.tccmanager.repository.SolicitacaoRepository;
import com.ufcg.psoft.tccmanager.service.aluno.AlunoServiceImp;
import com.ufcg.psoft.tccmanager.service.professor.ProfessorServiceImp;
import com.ufcg.psoft.tccmanager.service.temaTCC.TemaTCCAlunoServiceImp;
import com.ufcg.psoft.tccmanager.service.temaTCC.TemaTCCProfessorServiceImp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SolicitacaoServiceImp implements SolicitacaoService {

    @Autowired
    AlunoServiceImp alunoServiceImp;

    @Autowired
    ProfessorServiceImp professorServiceImp;

    @Autowired
    TemaTCCAlunoServiceImp temaTCCAlunoServiceImp;

    @Autowired
    TemaTCCProfessorServiceImp temaTCCProfessorServiceImp;

    @Autowired
    SolicitacaoRepository solicitacaoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EventManager eventManager;

    @Override
    public Solicitacao criarSolicitacaoTCCCadastradoPeloAluno(SolicitacaoDTO requestDTO) {
        Solicitacao solicitacao = Solicitacao.builder()
                .aluno(alunoServiceImp.buscarAlunoId(requestDTO.getAlunoId()))
                .professor(professorServiceImp.buscarProfessorId(requestDTO.getProfessorId()))
                .temaTCC(temaTCCAlunoServiceImp.buscarTemaTCCId(requestDTO.getTemaTCCId()))
                .status(SolicitacaoEnum.PENDENTE)
                .build();

        this.alterarStatusTemaTccAluno(requestDTO.getTemaTCCId());

        this.eventManager.notificarProfessoresSolicitacoesDeAlunos(requestDTO.getProfessorId(),
                                                                   requestDTO.getTemaTCCId(),
                                                                   requestDTO.getAlunoId());

        return solicitacaoRepository.save(solicitacao);
    }

    private void alterarStatusTemaTccAluno(Long temaTccId) {
        TemaTCC temaTccAluno = temaTCCAlunoServiceImp.buscarTemaTCCId(temaTccId);
        AlterarStatusDTO alterarStatusDTO = AlterarStatusDTO.builder()
                .temaTCC(temaTccAluno)
                .build();
        temaTccAluno.getStatus().alterarStatus(alterarStatusDTO);
    }

    @Override
    public Solicitacao criarSolicitacaoTCCCadastradoPeloProfessor(SolicitacaoDTO requestDTO) {
        Solicitacao solicitacao = Solicitacao.builder()
                .aluno(alunoServiceImp.buscarAlunoId(requestDTO.getAlunoId()))
                .professor(professorServiceImp.buscarProfessorId(requestDTO.getProfessorId()))
                .temaTCC(temaTCCProfessorServiceImp.buscarTemaTCCId(requestDTO.getTemaTCCId()))
                .status(SolicitacaoEnum.PENDENTE)
                .build();

        this.eventManager.notificarProfessoresSolicitacoesDeAlunos(requestDTO.getProfessorId(),
                                                                   requestDTO.getTemaTCCId(),
                                                                   requestDTO.getAlunoId());

        return solicitacaoRepository.save(solicitacao);
    }

    @Override
    public Collection<Solicitacao> listarTodasSolicitacoes() {
        return this.solicitacaoRepository.findAll();
    }
}