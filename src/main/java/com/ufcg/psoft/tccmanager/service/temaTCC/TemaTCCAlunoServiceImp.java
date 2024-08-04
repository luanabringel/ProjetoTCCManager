package com.ufcg.psoft.tccmanager.service.temaTCC;

import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCDTO;
import com.ufcg.psoft.tccmanager.model.enums.StatusTCCEnum;
import com.ufcg.psoft.tccmanager.exception.AreaDeEstudoNotFoundException;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import com.ufcg.psoft.tccmanager.model.TemaTCC;
import com.ufcg.psoft.tccmanager.service.aluno.AlunoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemaTCCAlunoServiceImp extends TemaTCCServiceImp implements TemaTCCService {

    @Autowired
    AlunoServiceImp alunoServiceImp;

    @Override
    public TemaTCC cadastrarTemaTCC(Long alunoId, TemaTCCDTO requestDTO) {
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

        alunoServiceImp.buscarAlunoId(alunoId).getTemasTCCCadastrados().add(temaTCC);

        return temaTCC;
    }

}
