package com.ufcg.psoft.tccmanager.service.coordenador;

import com.ufcg.psoft.tccmanager.dto.aluno.AlunoDTO;
import com.ufcg.psoft.tccmanager.dto.areaDeEstudo.AreaDeEstudoDTO;
import com.ufcg.psoft.tccmanager.dto.coordenador.CoordenadorDTO;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorDTO;
import com.ufcg.psoft.tccmanager.model.Aluno;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import com.ufcg.psoft.tccmanager.model.Coordenador;
import com.ufcg.psoft.tccmanager.model.Professor;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;

public interface CoordenadorService {
    Coordenador criarCoordenador(CoordenadorDTO coordenadorDto) throws NoSuchAlgorithmException;
    Coordenador modificarCoordenador(CoordenadorDTO coordenadorDto) throws NoSuchAlgorithmException;

    Aluno criarAluno(AlunoDTO alunoDto, String senhaCoordenador) throws NoSuchAlgorithmException;
    Aluno modificarAluno(Long id, AlunoDTO alunoDto, String senhaCoordenador) throws NoSuchAlgorithmException;
    Aluno buscarAlunoId(Long id, String senhaCoordenador) throws NoSuchAlgorithmException;
    Collection<Aluno> listarTodosAlunos(String senhaCoordenador) throws NoSuchAlgorithmException;
    void removerAluno(Long id, String senhaCoordenador) throws NoSuchAlgorithmException;

    Professor criarProfessor(ProfessorDTO professorDto, String senhaCoordenador) throws NoSuchAlgorithmException;
    Professor modificarProfessor(Long id, ProfessorDTO professorDto, String senhaCoordenador) throws NoSuchAlgorithmException;
    Professor buscarProfessorId(Long id, String senhaCoordenador) throws NoSuchAlgorithmException;
    Collection<Professor> listarTodosProfessores(String senhaCoordenador) throws NoSuchAlgorithmException;
    void removerProfessor(Long id, String senhaCoordenador) throws NoSuchAlgorithmException;

    AreaDeEstudo criarAreaDeEstudo(AreaDeEstudoDTO areaDeEstudoDto, String senhaCoordenador) throws NoSuchAlgorithmException;
    AreaDeEstudo modificarAreaDeEstudo(Long id, AreaDeEstudoDTO areaDeEstudoDto, String senhaCoordenador) throws NoSuchAlgorithmException;
    AreaDeEstudo buscarAreaDeEstudoId(Long id, String senhaCoordenador) throws NoSuchAlgorithmException;
    Collection<AreaDeEstudo> listarTodasAreasDeEstudo(String senhaCoordenador) throws NoSuchAlgorithmException;
    void removerAreaDeEstudo(Long id, String senhaCoordenador) throws NoSuchAlgorithmException;
    String gerarRelatorio(String senhaCoordenador) throws NoSuchAlgorithmException;
}
