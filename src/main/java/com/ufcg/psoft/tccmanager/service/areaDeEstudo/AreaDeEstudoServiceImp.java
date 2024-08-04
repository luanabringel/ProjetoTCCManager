package com.ufcg.psoft.tccmanager.service.areaDeEstudo;

import com.ufcg.psoft.tccmanager.dto.areaDeEstudo.AreaDeEstudoDTO;
import com.ufcg.psoft.tccmanager.events.EventManager;
import com.ufcg.psoft.tccmanager.exception.AreaDeEstudoAlreadyExistsException;
import com.ufcg.psoft.tccmanager.exception.AreaDeEstudoNotFoundException;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import com.ufcg.psoft.tccmanager.repository.AreaDeEstudoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AreaDeEstudoServiceImp implements AreaDeEstudoService {

    @Autowired
    AreaDeEstudoRepository areaDeEstudoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Lazy
    @Autowired
    EventManager eventManager;

    @Override
    public AreaDeEstudo criarAreaDeEstudo(AreaDeEstudoDTO areaDeEstudoDto) {
        if (this.areaDeEstudoRepository.existsByNome(areaDeEstudoDto.getNome())){
            throw new AreaDeEstudoAlreadyExistsException();
        }
        AreaDeEstudo areaDeEstudo = this.areaDeEstudoRepository.save(modelMapper.map(areaDeEstudoDto, AreaDeEstudo.class));
        this.eventManager.cadastrarAreaDeEstudo(areaDeEstudo);
        return areaDeEstudo;
    }

    @Override
    public AreaDeEstudo buscarAreaDeEstudoId(Long id) {
        return this.areaDeEstudoRepository
                .findById(id)
                .orElseThrow(AreaDeEstudoNotFoundException::new);
    }

    @Override
    public Collection<AreaDeEstudo> listarTodasAreasDeEstudo() {
        return this.areaDeEstudoRepository.findAll();
    }

    @Override
    public AreaDeEstudo modificarAreaDeEstudo(Long id, AreaDeEstudoDTO areaDeEstudoDto) {
        if (!this.areaDeEstudoRepository.existsById(id)){
            throw new AreaDeEstudoNotFoundException();
        }
        AreaDeEstudo novaArea = modelMapper.map(areaDeEstudoDto, AreaDeEstudo.class);
        novaArea.setId(id);
        return modelMapper.map(this.areaDeEstudoRepository.saveAndFlush(novaArea), AreaDeEstudo.class);
    }

    @Override
    public void removerAreaDeEstudo(Long id) {
        AreaDeEstudo areaRemove = this.areaDeEstudoRepository
                .findById(id)
                .orElseThrow(AreaDeEstudoNotFoundException::new);
        this.areaDeEstudoRepository.delete(areaRemove);
        this.eventManager.removerAreaDeEstudo(areaRemove);
    }
}
