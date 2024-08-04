package com.ufcg.psoft.tccmanager.service.temaTCC;

import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCDTO;
import com.ufcg.psoft.tccmanager.events.EventManager;
import com.ufcg.psoft.tccmanager.model.enums.StatusTCCEnum;
import com.ufcg.psoft.tccmanager.exception.AreaDeEstudoNotFoundException;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import com.ufcg.psoft.tccmanager.model.TemaTCC;
import com.ufcg.psoft.tccmanager.service.aluno.AlunoServiceImp;
import com.ufcg.psoft.tccmanager.service.professor.ProfessorServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TemaTCCProfessorServiceImp extends TemaTCCServiceImp implements TemaTCCService {

    @Lazy
    @Autowired
    ProfessorServiceImp professorServiceImp;

    @Autowired
    AlunoServiceImp alunoServiceImp;

    @Lazy
    @Autowired
    EventManager eventManager;

    @Override
    public TemaTCC cadastrarTemaTCC(Long professorId, TemaTCCDTO requestDTO) {
        for (AreaDeEstudo areaDeEstudo : requestDTO.getAreasDeEstudo()) {
            if (areaDeEstudo.getId() == null)
                throw new AreaDeEstudoNotFoundException();
            else
                super.areaDeEstudoServiceImp.buscarAreaDeEstudoId(areaDeEstudo.getId());
        }

        TemaTCC temaTCC = super.temaTCCRepository.save(TemaTCC.builder()
                .titulo(requestDTO.getTitulo())
                .descricao(requestDTO.getDescricao())
                .areasDeEstudo(requestDTO.getAreasDeEstudo())
                .status(StatusTCCEnum.NOVO)
                .build());

        professorServiceImp.buscarProfessorId(professorId).getTemasTCCCadastrados().add(temaTCC);

        this.eventManager.notificarAlunosInteressados(requestDTO.getAreasDeEstudo(), professorId);

        return temaTCC;
    }

}