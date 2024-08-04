package com.ufcg.psoft.tccmanager.controller;

import com.ufcg.psoft.tccmanager.dto.professor.ProfessorAtualizaQuotaDto;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoAprovacaoResponseDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCAlunoGetAllDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCDTO;
import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoAprovacaoRequestDTO;
import com.ufcg.psoft.tccmanager.model.Professor;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCProfessorGetAllDTO;
import com.ufcg.psoft.tccmanager.model.TemaTCC;
import com.ufcg.psoft.tccmanager.service.professor.ProfessorServiceImp;
import com.ufcg.psoft.tccmanager.service.temaTCC.TemaTCCProfessorServiceImp;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/professores", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfessorController {

    @Autowired
    TemaTCCProfessorServiceImp temaTCCProfessorServiceImp;

    @Autowired
    ProfessorServiceImp professorServiceImp;

    @Transactional
    @PostMapping("/{professorId}/cadastrar-tema-tcc")
    public ResponseEntity<TemaTCC> cadastrarTemaTCC(@PathVariable("professorId") Long professorId,
                                                    @RequestBody @Valid TemaTCCDTO requestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.temaTCCProfessorServiceImp.cadastrarTemaTCC(professorId, requestDTO));
    }

    @Transactional
    @PatchMapping("/{professorId}/{areaDeEstudoId}/adicionar-area-de-estudo-interessada")
    public ResponseEntity<?> adicionarAreaDeEstudoInteressada(@PathVariable("professorId") Long professorId,
                                                              @PathVariable("areaDeEstudoId") Long areaDeEstudoId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(professorServiceImp.adicionarAreaDeEstudoInteressada(professorId, areaDeEstudoId));
    }

    @GetMapping("/{professorId}/temas-tcc-cadastrados")
    public ResponseEntity<Collection<TemaTCCProfessorGetAllDTO>> listarTemasTccCadastradosPorProfessor(@PathVariable Long professorId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.professorServiceImp.listarTemasTccCadastradosPorProfessor(professorId));
    }

    @Transactional
    @PatchMapping("/{professorId}/atualizar-quota")
    public ResponseEntity<Professor> atualizarQuotaProfessor(@PathVariable("professorId") Long professorId,
                                                             @RequestBody @Valid ProfessorAtualizaQuotaDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(professorServiceImp.atualizaQuota(professorId, requestDto));
    }

    @GetMapping("/temas-tcc-cadastrados-alunos")
    public ResponseEntity<Collection<TemaTCCAlunoGetAllDTO>> listarTemasTccCadastradosPorAlunos() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.professorServiceImp.listarTemasTccCadastradosPorAlunos());
    }

    @Transactional
    @PatchMapping("/definir-aprovacao-tema-tcc")
    public ResponseEntity<SolicitacaoAprovacaoResponseDTO> definirAprovacaoSolicitacao(@RequestBody @Valid SolicitacaoAprovacaoRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(professorServiceImp.definirAprovacaoSolicitacao(requestDTO));
    }

    @GetMapping("/{professorId}/solicitacoes-orientacao-alunos")
    public ResponseEntity<Collection<SolicitacaoDTO>> listarSolicitacoesDeOrientacaoPorProfessor(@PathVariable Long professorId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.professorServiceImp.listarSolicitacoesDeOrientacaoPorProfessor(professorId));
    }

}
