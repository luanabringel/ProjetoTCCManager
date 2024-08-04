package com.ufcg.psoft.tccmanager.service.aluno;

import com.ufcg.psoft.tccmanager.dto.aluno.AlunoDTO;
import com.ufcg.psoft.tccmanager.dto.aluno.AlunoInteresseAreaResponseDTO;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorGetInteressadosPorAlunoDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCProfessorAlunoGetAllDTO;
import com.ufcg.psoft.tccmanager.events.EventManager;
import com.ufcg.psoft.tccmanager.exception.AlunoNotFoundException;
import com.ufcg.psoft.tccmanager.model.Aluno;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import com.ufcg.psoft.tccmanager.model.Professor;
import com.ufcg.psoft.tccmanager.repository.AlunoRepository;
import com.ufcg.psoft.tccmanager.service.areaDeEstudo.AreaDeEstudoServiceImp;
import com.ufcg.psoft.tccmanager.service.professor.ProfessorServiceImp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AlunoServiceImp implements AlunoService {

    @Autowired
    AlunoRepository alunoRepository;

    @Autowired
    @Lazy
    ProfessorServiceImp professorServiceImp;

    @Autowired
    AreaDeEstudoServiceImp areaDeEstudoServiceImp;

    @Autowired
    ModelMapper modelMapper;

    @Lazy
    @Autowired
    EventManager eventManager;

    @Override
    public Aluno criarAluno(AlunoDTO alunoDto) {
        return this.alunoRepository.save(modelMapper.map(alunoDto, Aluno.class));
    }

    @Override
    public Aluno buscarAlunoId(Long id) {
        return this.alunoRepository
                .findById(id)
                .orElseThrow(AlunoNotFoundException::new);
    }

    @Override
    public Collection<Aluno> listarTodosAlunos() {
        return this.alunoRepository.findAll();
    }

    @Override
    public Aluno modificarAluno(Long id, AlunoDTO alunoDTO) {
        if (!this.alunoRepository.existsById(id)){
            throw new AlunoNotFoundException();
        }
        Aluno novoAluno = modelMapper.map(alunoDTO, Aluno.class);
        novoAluno.setId(id);
        return modelMapper.map(alunoRepository.saveAndFlush(novoAluno), Aluno.class);
    }

    @Override
    public void removerAluno(Long id) {
        Aluno alunoRemove = this.alunoRepository
                .findById(id)
                .orElseThrow(AlunoNotFoundException::new);
        this.alunoRepository.delete(alunoRemove);
    }

    @Override
    public AlunoInteresseAreaResponseDTO adicionarAreaDeEstudoInteressada(Long alunoId, Long areaDeEstudoId) {
        Aluno aluno = this.buscarAlunoId(alunoId);
        AreaDeEstudo areaDeEstudo = this.areaDeEstudoServiceImp.buscarAreaDeEstudoId(areaDeEstudoId);

        aluno.getAreasDeEstudoInteressadas().add(areaDeEstudo);
        this.eventManager.inscreverAlunoAreaDeEstudo(areaDeEstudo, aluno);
        return AlunoInteresseAreaResponseDTO.builder()
                .areasDeEstudoInteressadas(aluno.getAreasDeEstudoInteressadas())
                .build();
    }

    @Override
    public Collection<ProfessorGetInteressadosPorAlunoDTO> listarProfessoresInteressados(Long alunoId) {
        Aluno aluno = this.buscarAlunoId(alunoId);
        Collection<AreaDeEstudo> areasDeEstudoInteressadasAluno = aluno.getAreasDeEstudoInteressadas();

        List<Professor> professoresInteressados = new ArrayList<Professor>();
        for (Professor professor : this.professorServiceImp.listarTodosProfessores()) {
            if (!professorServiceImp.verificaQuota(professor)) {
                continue;
            }
            for (AreaDeEstudo areaDeEstudo : professor.getAreasDeEstudoInteressadas()) {
                if (areasDeEstudoInteressadasAluno.contains(areaDeEstudo)) {
                    professoresInteressados.add(professor);
                    break;
                }
            }
        }

        return professoresInteressados.stream()
                .map(professor -> modelMapper.map(professor, ProfessorGetInteressadosPorAlunoDTO.class))
                .toList();
    }

    @Override
    public Collection<TemaTCCProfessorAlunoGetAllDTO> listarTemasTCCCadastradosPorProfessor() {
        Collection<TemaTCCProfessorAlunoGetAllDTO> temasTCCProfessores = new ArrayList<>();
        for (Professor professor : this.professorServiceImp.listarTodosProfessores()) {
            Collection<TemaTCCProfessorAlunoGetAllDTO> temaTCCDtos = professor.getTemasTCCCadastrados().stream()
                    .map(temaTCC -> modelMapper.map(temaTCC, TemaTCCProfessorAlunoGetAllDTO.class))
                    .toList();
            temasTCCProfessores.addAll(temaTCCDtos.stream()
                    .map(temaTCCDto -> {
                        temaTCCDto.setProfessorAutor(professor.getNome());
                        return temaTCCDto;
                    })
                    .toList());
        }

        return temasTCCProfessores;
    }

}