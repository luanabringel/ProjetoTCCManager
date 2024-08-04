package com.ufcg.psoft.tccmanager.service.solicitacao;

import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoDTO;
import com.ufcg.psoft.tccmanager.model.Solicitacao;

import java.util.Collection;

public interface SolicitacaoService {

    Solicitacao criarSolicitacaoTCCCadastradoPeloAluno(SolicitacaoDTO requestDTO);
    Solicitacao criarSolicitacaoTCCCadastradoPeloProfessor(SolicitacaoDTO requestDTO);
    Collection<Solicitacao> listarTodasSolicitacoes();
}
