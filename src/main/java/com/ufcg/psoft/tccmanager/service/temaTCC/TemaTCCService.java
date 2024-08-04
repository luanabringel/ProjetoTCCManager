package com.ufcg.psoft.tccmanager.service.temaTCC;

import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCDTO;
import com.ufcg.psoft.tccmanager.model.TemaTCC;

import java.util.Collection;

public interface TemaTCCService {

    TemaTCC cadastrarTemaTCC(Long id, TemaTCCDTO requestDTO);
    TemaTCC buscarTemaTCCId(Long id);
    Collection<TemaTCC> listarTodosTemasTCC();
    TemaTCC modificarTemaTCC(Long id, TemaTCCDTO requestDTO);
    void removerTemaTCC(Long id);

}
