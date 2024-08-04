package com.ufcg.psoft.tccmanager.service.temaTCC;

import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCDTO;
import com.ufcg.psoft.tccmanager.exception.TemaNotFoundException;
import com.ufcg.psoft.tccmanager.model.TemaTCC;
import com.ufcg.psoft.tccmanager.repository.TemaTCCRepository;
import com.ufcg.psoft.tccmanager.service.areaDeEstudo.AreaDeEstudoServiceImp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TemaTCCServiceImp {

    @Autowired
    AreaDeEstudoServiceImp areaDeEstudoServiceImp;

    @Autowired
    TemaTCCRepository temaTCCRepository;

    @Autowired
    ModelMapper modelMapper;

    public TemaTCC buscarTemaTCCId(Long id) {
        return temaTCCRepository
                .findById(id)
                .orElseThrow(TemaNotFoundException::new);
    }

    public Collection<TemaTCC> listarTodosTemasTCC() {
        return this.temaTCCRepository.findAll();
    }

    public TemaTCC modificarTemaTCC(Long id, TemaTCCDTO requestDTO) {
        if (!this.temaTCCRepository.existsById(id)){
            throw new TemaNotFoundException();
        }
        TemaTCC novoTema = modelMapper.map(requestDTO, TemaTCC.class);
        novoTema.setId(id);

        return modelMapper.map(this.temaTCCRepository.saveAndFlush(novoTema), TemaTCC.class);
    }

    public void removerTemaTCC(Long id) {
        TemaTCC temaTCC = this.temaTCCRepository
            .findById(id)
            .orElseThrow(TemaNotFoundException::new);
        this.temaTCCRepository.delete(temaTCC);
    }
}
