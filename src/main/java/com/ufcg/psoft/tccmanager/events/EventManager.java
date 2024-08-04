package com.ufcg.psoft.tccmanager.events;

import com.ufcg.psoft.tccmanager.model.Aluno;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import com.ufcg.psoft.tccmanager.model.Professor;
import com.ufcg.psoft.tccmanager.model.TemaTCC;
import com.ufcg.psoft.tccmanager.service.aluno.AlunoServiceImp;
import com.ufcg.psoft.tccmanager.service.professor.ProfessorServiceImp;
import com.ufcg.psoft.tccmanager.service.temaTCC.TemaTCCServiceImp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Builder
@Configuration
@NoArgsConstructor
@AllArgsConstructor
public class EventManager {

    @Lazy
    @Autowired
    AlunoServiceImp alunoServiceImp;

    @Lazy
    @Autowired
    ProfessorServiceImp professorServiceImp;

    @Lazy
    @Autowired
    TemaTCCServiceImp temaTCCServiceImp;

    @Builder.Default
    HashMap<AreaDeEstudo, HashSet<Aluno>> alunosInteressados = new HashMap<>();

    public void cadastrarAreaDeEstudo(AreaDeEstudo areaDeEstudo) {
        this.alunosInteressados.put(areaDeEstudo, new HashSet<>());
    }

    public void removerAreaDeEstudo(AreaDeEstudo areaDeEstudo) {
        this.alunosInteressados.remove(areaDeEstudo);
    }

    public void inscreverAlunoAreaDeEstudo(AreaDeEstudo areaDeEstudo, Aluno aluno) {
        this.alunosInteressados.get(areaDeEstudo).add(aluno);
    }

    public void desinscreverAlunoAreaDeEstudo(AreaDeEstudo areaDeEstudo, Aluno aluno) {
        this.alunosInteressados.get(areaDeEstudo).remove(aluno);
    }

    public void notificarAlunosInteressados(Set<AreaDeEstudo> areasDeEstudo, Long professorId) {
        Professor professor = this.professorServiceImp.buscarProfessorId(professorId);

        for (Aluno aluno : this.listarAlunosInteressadosEmAreasDeEstudo(areasDeEstudo)) {
            StringBuilder notificacao = new StringBuilder("Há um novo tema de TCC cadastrado que está em suas áreas de interesse!");
            notificacao.append("\n")
                    .append("Olá, ")
                    .append(aluno.getNome())
                    .append(".")
                    .append("\n")
                    .append("O professor ")
                    .append(professor.getNome())
                    .append(" cadastrou um novo tema de TCC com a área ");
                    areasDeEstudo.stream()
                            .forEach(areaDeEstudo -> notificacao.append(areaDeEstudo.getNome() + ", "));
                    notificacao.append("na qual você demonstrou interesse!")
                    .append("\n");

            aluno.getNotificacoes().add(notificacao.toString());
            System.out.println(notificacao);
        }
    }

    private Set<Aluno> listarAlunosInteressadosEmAreasDeEstudo(Set<AreaDeEstudo> areaDeEstudos) {
        Set<Aluno> alunosInteressados = new HashSet<>();
        for (Aluno aluno : this.alunoServiceImp.listarTodosAlunos()) {
            for (AreaDeEstudo areaDeEstudo : areaDeEstudos) {
                if (aluno.getAreasDeEstudoInteressadas().contains(areaDeEstudo)) {
                    alunosInteressados.add(aluno);
                }
            }
        }
        return alunosInteressados;
    }

    public void notificarProfessoresSolicitacoesDeAlunos(Long professorId, Long tematccId, Long alunoId) {
        Professor professor = this.professorServiceImp.buscarProfessorId(professorId);
        TemaTCC tematcc = this.temaTCCServiceImp.buscarTemaTCCId(tematccId);
        Aluno aluno = this.alunoServiceImp.buscarAlunoId(alunoId);

        StringBuilder notificacao = new StringBuilder("Há uma nova solicitação de orientação de TCC!");
        notificacao.append("\n")
                .append("Olá, ")
                .append(professor.getNome())
                .append(".")
                .append("\n")
                .append("O(a) aluno(a) ")
                .append(aluno.getNome())
                .append(" solicitou a sua orientação no TCC com o tema: ")
                .append(tematcc.getTitulo())
                .append(".")
                .append("\n");

        professor.getNotificacoes().add(notificacao.toString());
        System.out.println(notificacao);
    }

}
