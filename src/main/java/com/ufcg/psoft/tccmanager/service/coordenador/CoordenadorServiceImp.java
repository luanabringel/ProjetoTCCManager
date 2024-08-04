package com.ufcg.psoft.tccmanager.service.coordenador;

import com.ufcg.psoft.tccmanager.dto.aluno.AlunoDTO;
import com.ufcg.psoft.tccmanager.dto.areaDeEstudo.AreaDeEstudoDTO;
import com.ufcg.psoft.tccmanager.dto.coordenador.CoordenadorDTO;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorDTO;
import com.ufcg.psoft.tccmanager.exception.*;
import com.ufcg.psoft.tccmanager.model.*;
import com.ufcg.psoft.tccmanager.model.enums.SolicitacaoEnum;
import com.ufcg.psoft.tccmanager.repository.*;
import com.ufcg.psoft.tccmanager.security.PasswordEnconder;
import com.ufcg.psoft.tccmanager.service.aluno.AlunoServiceImp;
import com.ufcg.psoft.tccmanager.service.areaDeEstudo.AreaDeEstudoServiceImp;
import com.ufcg.psoft.tccmanager.service.professor.ProfessorServiceImp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class CoordenadorServiceImp implements CoordenadorService {

    @Autowired
    PasswordEnconder passwordEnconder;

    @Autowired
    CoordenadorRepository coordenadorRepository;

    @Autowired
    AlunoRepository alunoRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    TemaTCCRepository temaTCCRepository;

    @Autowired
    SolicitacaoRepository solicitacaoRepository;
    
    @Autowired
    AlunoServiceImp alunoServiceImp;
    
    @Autowired
    ProfessorServiceImp professorServiceImp;
    
    @Autowired
    AreaDeEstudoServiceImp areaDeEstudoServiceImp;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Coordenador criarCoordenador(CoordenadorDTO coordenadorDto) throws NoSuchAlgorithmException {
        if (this.coordenadorRepository.count() > 0)
            throw new CoordenadorAlreadyRegistered();

        CoordenadorDTO requestDTO = CoordenadorDTO.builder()
                .nome(coordenadorDto.getNome())
                .email(coordenadorDto.getEmail())
                .senha(this.passwordEnconder.criptografarSHA256(coordenadorDto.getSenha()))
                .build();
        
        return this.coordenadorRepository.save(modelMapper.map(requestDTO, Coordenador.class));
    }

    @Override
    public Coordenador modificarCoordenador(CoordenadorDTO coordenadorDto) {
        Coordenador coordenador = this.coordenadorRepository.findCoordenador();
        if (coordenador == null) throw new CoordenadorNotRegistered();
        coordenador.setNome(coordenadorDto.getNome());
        coordenador.setEmail(coordenadorDto.getEmail());
        return modelMapper.map(coordenadorRepository.saveAndFlush(coordenador), Coordenador.class);
    }

    @Override
    public Aluno criarAluno(AlunoDTO requestDTO, String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        AlunoDTO newRequestDTO = AlunoDTO.builder()
                .matricula(requestDTO.getMatricula())
                .nome(requestDTO.getNome())
                .periodoConclusao(requestDTO.getPeriodoConclusao())
                .email(requestDTO.getEmail())
                .build();
        return this.alunoServiceImp.criarAluno(newRequestDTO);
    }

    @Override
    public Aluno modificarAluno(Long id, AlunoDTO requestDTO, String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        AlunoDTO newRequestDTO = AlunoDTO.builder()
                .matricula(requestDTO.getMatricula())
                .nome(requestDTO.getNome())
                .periodoConclusao(requestDTO.getPeriodoConclusao())
                .email(requestDTO.getEmail())
                .build();
        return this.alunoServiceImp.modificarAluno(id, newRequestDTO);
    }

    @Override
    public Aluno buscarAlunoId(Long id, String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        return this.alunoServiceImp.buscarAlunoId(id);
    }

    @Override
    public Collection<Aluno> listarTodosAlunos(String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        return this.alunoServiceImp.listarTodosAlunos();
    }

    @Override
    public void removerAluno(Long id, String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        this.alunoServiceImp.removerAluno(id);
    }

    @Override
    public Professor criarProfessor(ProfessorDTO requestDTO, String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        ProfessorDTO newRequestDTO = ProfessorDTO.builder()
                .nome(requestDTO.getNome())
                .email(requestDTO.getEmail())
                .labs(requestDTO.getLabs())
                .build();
        return this.professorServiceImp.criarProfessor(newRequestDTO);
    }

    @Override
    public Professor modificarProfessor(Long id, ProfessorDTO requestDTO, String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        ProfessorDTO newRequestDTO = ProfessorDTO.builder()
                .nome(requestDTO.getNome())
                .email(requestDTO.getEmail())
                .labs(requestDTO.getLabs())
                .build();
        return this.professorServiceImp.modificarProfessor(id, newRequestDTO);
    }

    @Override
    public Professor buscarProfessorId(Long id, String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        return this.professorServiceImp.buscarProfessorId(id);
    }

    @Override
    public Collection<Professor> listarTodosProfessores(String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        return this.professorServiceImp.listarTodosProfessores();
    }

    @Override
    public void removerProfessor(Long id, String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        this.professorServiceImp.removerProfessor(id);
    }

    @Override
    public AreaDeEstudo criarAreaDeEstudo(AreaDeEstudoDTO requestDTO, String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        AreaDeEstudoDTO newRequestDTO = AreaDeEstudoDTO.builder()
                .nome(requestDTO.getNome())
                .build();
        return this.areaDeEstudoServiceImp.criarAreaDeEstudo(newRequestDTO);
    }

    @Override
    public AreaDeEstudo modificarAreaDeEstudo(Long id, AreaDeEstudoDTO requestDTO, String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        AreaDeEstudoDTO newRequestDTO = AreaDeEstudoDTO.builder()
                .nome(requestDTO.getNome())
                .build();
        return this.areaDeEstudoServiceImp.modificarAreaDeEstudo(id, newRequestDTO);
    }

    @Override
    public AreaDeEstudo buscarAreaDeEstudoId(Long id, String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        return this.areaDeEstudoServiceImp.buscarAreaDeEstudoId(id);
    }

    @Override
    public Collection<AreaDeEstudo> listarTodasAreasDeEstudo(String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        return this.areaDeEstudoServiceImp.listarTodasAreasDeEstudo();
    }

    @Override
    public void removerAreaDeEstudo(Long id, String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        this.areaDeEstudoServiceImp.removerAreaDeEstudo(id);
    }

    @Override
    public String gerarRelatorio(String senhaCoordenador) throws NoSuchAlgorithmException {
        this.checkPassword(senhaCoordenador);
        List<Solicitacao> orientacoesEmAndamento = this.solicitacaoRepository.findAll().stream()
                .filter(solicitacao -> solicitacao.getStatus().equals(SolicitacaoEnum.APROVADO)).toList();

        StringBuilder relatorio = new StringBuilder("Relatório de orientações de TCC em curso e alunos sem orientador\nOrientações em curso:\n");
        for (Solicitacao solicitacao : orientacoesEmAndamento) {
            relatorio.append("Aluno: ")
                    .append(solicitacao.getAluno().getNome())
                    .append("\n")
                    .append("Professor: ")
                    .append(solicitacao.getProfessor().getNome())
                    .append("\n")
                    .append("Tema: ")
                    .append(solicitacao.getTemaTCC().getTitulo())
                    .append("\n")
                    .append("Areas de estudo: ")
                    .append(this.areasDeEstudoToString(solicitacao.getTemaTCC().getAreasDeEstudo()))
                    .append("\n\n");
        }

        relatorio.append("Alunos sem orientador:\n");
        List<Solicitacao> solicitacoesSemOrientador = this.solicitacaoRepository.findAll().stream()
                .filter(solicitacao -> solicitacao.getStatus().equals(SolicitacaoEnum.PENDENTE)).toList();

        for (Solicitacao solicitacao : solicitacoesSemOrientador) {
            relatorio.append("Aluno: ")
                    .append(solicitacao.getAluno().getNome())
                    .append("\n");
        }

        System.out.println(relatorio.toString());
        return relatorio.toString();
    }

    private void checkPassword(String password) throws NoSuchAlgorithmException {
        String encondedPassword = this.passwordEnconder.criptografarSHA256(password);
        Coordenador coordenador = this.coordenadorRepository.findAll().stream().findFirst().orElseThrow(CoordenadorNotRegistered::new);
        if (!coordenador.getSenha().equals(encondedPassword))
            throw new IncorrectPassword();
    }

    private String areasDeEstudoToString(Set<AreaDeEstudo> areasDeEstudo) {
        StringBuilder areasDeEstudoString = new StringBuilder();
        for (AreaDeEstudo areaDeEstudo : areasDeEstudo) {
            areasDeEstudoString.append(areaDeEstudo.getNome())
                    .append(", ");
        }
        areasDeEstudoString.delete(areasDeEstudoString.length() - 2, areasDeEstudoString.length() - 1);
        return areasDeEstudoString.toString();
    }
}
