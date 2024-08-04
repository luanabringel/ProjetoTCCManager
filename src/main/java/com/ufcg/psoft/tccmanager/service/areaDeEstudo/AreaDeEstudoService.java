package com.ufcg.psoft.tccmanager.service.areaDeEstudo;

import com.ufcg.psoft.tccmanager.dto.areaDeEstudo.AreaDeEstudoDTO;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;

import java.util.Collection;

public interface AreaDeEstudoService {

    AreaDeEstudo criarAreaDeEstudo(AreaDeEstudoDTO areaDeEstudoDto);
    AreaDeEstudo modificarAreaDeEstudo(Long id, AreaDeEstudoDTO areaDeEstudoDto);
    AreaDeEstudo buscarAreaDeEstudoId(Long id);
    Collection<AreaDeEstudo> listarTodasAreasDeEstudo();
    void removerAreaDeEstudo(Long id);

}
