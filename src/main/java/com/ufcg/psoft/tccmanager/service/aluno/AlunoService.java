package com.ufcg.psoft.tccmanager.service.aluno;

import com.ufcg.psoft.tccmanager.dto.aluno.AlunoDTO;
import com.ufcg.psoft.tccmanager.dto.aluno.AlunoInteresseAreaResponseDTO;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorGetInteressadosPorAlunoDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCProfessorAlunoGetAllDTO;
import com.ufcg.psoft.tccmanager.model.Aluno;

import java.util.Collection;

public interface AlunoService {
    Aluno criarAluno(AlunoDTO alunoDto);
    Aluno modificarAluno(Long id, AlunoDTO alunoDto);
    Aluno buscarAlunoId(Long id);
    Collection<Aluno> listarTodosAlunos();
    void removerAluno(Long id);

    AlunoInteresseAreaResponseDTO adicionarAreaDeEstudoInteressada(Long alunoId, Long areaDeEstudoId);
    Collection<ProfessorGetInteressadosPorAlunoDTO> listarProfessoresInteressados(Long alunoId);
    Collection<TemaTCCProfessorAlunoGetAllDTO> listarTemasTCCCadastradosPorProfessor();
}
