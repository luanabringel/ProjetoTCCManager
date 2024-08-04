package com.ufcg.psoft.tccmanager.service.professor;

import com.ufcg.psoft.tccmanager.dto.professor.ProfessorAtualizaQuotaDto;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorDTO;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorInteresseAreaResponseDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoAprovacaoResponseDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.AlterarStatusDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCProfessorGetAllDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCAlunoGetAllDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoAprovacaoRequestDTO;
import com.ufcg.psoft.tccmanager.exception.AreaDeEstudoNotFoundException;
import com.ufcg.psoft.tccmanager.exception.ProfessorNotFoundException;
import com.ufcg.psoft.tccmanager.exception.SolicitacaoNotFoundException;
import com.ufcg.psoft.tccmanager.model.*;
import com.ufcg.psoft.tccmanager.model.enums.SolicitacaoEnum;
import com.ufcg.psoft.tccmanager.repository.*;
import com.ufcg.psoft.tccmanager.service.aluno.AlunoServiceImp;
import com.ufcg.psoft.tccmanager.service.temaTCC.TemaTCCServiceImp;
import com.ufcg.psoft.tccmanager.service.solicitacao.SolicitacaoServiceImp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class ProfessorServiceImp implements ProfessorService {

    @Autowired
    @Lazy
    AlunoServiceImp alunoServiceImp;

    @Autowired
    @Lazy
    SolicitacaoServiceImp solicitacaoServiceImp;

    @Autowired
    TemaTCCServiceImp temaTCCServiceImp;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    TemaTCCRepository temaTCCRepository;

    @Autowired
    AreaDeEstudoRepository areaDeEstudoRepository;

    @Autowired
    AlunoRepository alunoRepository;

    @Autowired
    SolicitacaoRepository solicitacaoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Professor criarProfessor(ProfessorDTO professorDto) {
        return this.professorRepository.save(modelMapper.map(professorDto, Professor.class));
    }

    @Override
    public Professor buscarProfessorId(Long id) {
        return this.professorRepository
                .findById(id)
                .orElseThrow(ProfessorNotFoundException::new);
    }

    @Override
    public Collection<Professor> listarTodosProfessores() {
        return this.professorRepository.findAll();
    }

    @Override
    public Professor modificarProfessor(Long id, ProfessorDTO professorDto) {
        if (!this.professorRepository.existsById(id)){
            throw new ProfessorNotFoundException();
        }
        Professor novoProf = modelMapper.map(professorDto, Professor.class);
        novoProf.setId(id);

        return modelMapper.map(this.professorRepository.saveAndFlush(novoProf), Professor.class);
    }

    @Override
    public void removerProfessor(Long id) {
        Professor professorRemove = this.professorRepository
                .findById(id)
                .orElseThrow(ProfessorNotFoundException::new);
        this.professorRepository.delete(professorRemove);
    }

    @Override
    public ProfessorInteresseAreaResponseDTO adicionarAreaDeEstudoInteressada(Long professorId, Long areaDeEstudoId) {
        Professor professor = this.professorRepository.findById(professorId).orElseThrow(ProfessorNotFoundException::new);
        AreaDeEstudo areaDeEstudo = this.areaDeEstudoRepository.findById(areaDeEstudoId).orElseThrow(AreaDeEstudoNotFoundException::new);

        professor.getAreasDeEstudoInteressadas().add(areaDeEstudo);

        return ProfessorInteresseAreaResponseDTO.builder()
                .areasDeEstudoInteressadas(professor.getAreasDeEstudoInteressadas())
                .build();
    }

    @Override
    public boolean verificaQuota(Professor professor) {
        return professor.getTemasTCCOrientados().size() < professor.getQuota();
    }

    @Override
    public Professor atualizaQuota(Long professorId, ProfessorAtualizaQuotaDto novaQuota) {
        Professor professor = this.buscarProfessorId(professorId);
        professor.setQuota(novaQuota.getQuota());

        return modelMapper.map(this.professorRepository.saveAndFlush(professor), Professor.class);
    }

    @Override
    public Collection<TemaTCCProfessorGetAllDTO> listarTemasTccCadastradosPorProfessor(Long professorId) {
        Professor professor = this.professorRepository.findById(professorId).orElseThrow(ProfessorNotFoundException::new);
        return professor.getTemasTCCCadastrados().stream()
                .map(temaTCC -> modelMapper.map(temaTCC, TemaTCCProfessorGetAllDTO.class))
                .toList();
    }

    @Override
    public Collection<TemaTCCAlunoGetAllDTO> listarTemasTccCadastradosPorAlunos() {
        Collection<TemaTCCAlunoGetAllDTO> temasTCCAlunos = new ArrayList<>();
        for (Aluno aluno : alunoRepository.findAll()) {
            Collection<TemaTCCAlunoGetAllDTO> temaTCCDtos = aluno.getTemasTCCCadastrados().stream()
                    .map(temaTCC -> modelMapper.map(temaTCC, TemaTCCAlunoGetAllDTO.class))
                    .toList();
            temasTCCAlunos.addAll(temaTCCDtos);
        }

        return temasTCCAlunos;
    }

    @Override
    public Collection<SolicitacaoDTO> listarSolicitacoesDeOrientacaoPorProfessor(Long professorId) {
        return solicitacaoServiceImp.listarTodasSolicitacoes().stream()
                .filter(solicitacao -> solicitacao.getProfessor().getId().equals(professorId))
                .map(solicitacao -> modelMapper.map(solicitacao, SolicitacaoDTO.class))
                .toList();
    }

    @Override
    public SolicitacaoAprovacaoResponseDTO definirAprovacaoSolicitacao(SolicitacaoAprovacaoRequestDTO aprovacaoDTO) {
        Professor professor = this.buscarProfessorId(aprovacaoDTO.getProfessorId());
        Aluno aluno = this.alunoServiceImp.buscarAlunoId(aprovacaoDTO.getAlunoId());
        TemaTCC temaTCC = this.temaTCCServiceImp.buscarTemaTCCId(aprovacaoDTO.getTemaTccId());

        TemaTCC temaTCCDaSolicitacao = this.buscarTemaTccNasSolicitacoes(professor, aluno, temaTCC, aprovacaoDTO.getAprovacao());
        AlterarStatusDTO alterarStatusDTO = AlterarStatusDTO.builder()
                .temaTCC(temaTCCDaSolicitacao)
                .aprovacao(aprovacaoDTO.getAprovacao())
                .build();
        temaTCCDaSolicitacao.getStatus().alterarStatus(alterarStatusDTO);
        this.temaTCCRepository.save(temaTCCDaSolicitacao);

        String notificacaoCoordeador = this.notificarCoordeandor(aprovacaoDTO.getAprovacao(), temaTCCDaSolicitacao, professor);
        String mensagemDecisao = this.formatarMensagemDecisao(aluno, aprovacaoDTO.getMensagemDecisao(), aprovacaoDTO.getAprovacao());

        return SolicitacaoAprovacaoResponseDTO.builder()
                .professorId(professor.getId())
                .alunoId(aluno.getId())
                .temaTCC(temaTCCDaSolicitacao)
                .aprovacao(aprovacaoDTO.getAprovacao())
                .notificacaoCoordenador(notificacaoCoordeador)
                .mensagemDecisao(mensagemDecisao)
                .build();
    }

    private TemaTCC buscarTemaTccNasSolicitacoes(Professor professor, Aluno aluno, TemaTCC temaTCC, Boolean aprovacao) {
        Solicitacao solicitacao = this.solicitacaoRepository.findByProfessorAndAlunoAndTemaTCC(professor, aluno, temaTCC);
        if (solicitacao == null)
            throw new SolicitacaoNotFoundException();
        if (aprovacao) {
            solicitacao.setStatus(SolicitacaoEnum.APROVADO);
            this.solicitacaoRepository.save(solicitacao);
        }
        return solicitacao.getTemaTCC();
    }

    private String notificarCoordeandor(Boolean aprovacao, TemaTCC temaTCC, Professor professor) {
        if (aprovacao) {
            StringBuilder notificacao = new StringBuilder("Mensagem para o Coordenador: ")
                    .append("\n")
                    .append("O tema de TCC de título: ")
                    .append(temaTCC.getTitulo())
                    .append("\n")
                    .append("foi APROVADO pelo professor ")
                    .append(professor.getNome());
            System.out.println(notificacao);
            return notificacao.toString();
        }
        else return "";
    }

    private String formatarMensagemDecisao(Aluno aluno, String mensagemDecisao, Boolean aprovacao) {
        StringBuilder mensagem = new StringBuilder("Caro aluno ")
                .append(aluno.getNome())
                .append(".")
                .append("\n")
                .append("Sua solicitação foi ");
        if (aprovacao) mensagem.append("APROVADA");
        else mensagem.append("NEGADA");
        mensagem.append("\n")
                .append("\n")
                .append("Resposta do professor:")
                .append("\n")
                .append(mensagemDecisao);
        System.out.println(mensagem);
        return mensagem.toString();
    }

}