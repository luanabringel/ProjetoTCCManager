package com.ufcg.psoft.tccmanager.controller;

import com.ufcg.psoft.tccmanager.dto.solicitacao.SolicitacaoDTO;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorGetInteressadosPorAlunoDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCDTO;
import com.ufcg.psoft.tccmanager.dto.temaTCC.TemaTCCProfessorAlunoGetAllDTO;
import com.ufcg.psoft.tccmanager.model.Solicitacao;
import com.ufcg.psoft.tccmanager.model.TemaTCC;
import com.ufcg.psoft.tccmanager.service.aluno.AlunoServiceImp;
import com.ufcg.psoft.tccmanager.service.solicitacao.SolicitacaoServiceImp;
import com.ufcg.psoft.tccmanager.service.temaTCC.TemaTCCAlunoServiceImp;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/alunos", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlunoController {

    @Autowired
    TemaTCCAlunoServiceImp temaTCCAlunoServiceImp;

    @Autowired
    AlunoServiceImp alunoServiceImp;

    @Autowired
    SolicitacaoServiceImp solicitacaoServiceImp;

    @Transactional
    @PostMapping("/{alunoId}/cadastrar-tema-tcc")
    public ResponseEntity<TemaTCC> cadastrarTemaTCC(@PathVariable("alunoId") Long alunoId,
                                                    @RequestBody @Valid TemaTCCDTO requestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(temaTCCAlunoServiceImp.cadastrarTemaTCC(alunoId, requestDTO));
    }

    @Transactional
    @PatchMapping("/{alunoId}/{areaDeEstudoId}/adicionar-area-de-estudo-interessada")
    public ResponseEntity<?> adicionarAreaDeEstudoInteressada(@PathVariable("alunoId") Long alunoId,
                                                              @PathVariable("areaDeEstudoId") Long areaDeEstudoId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(alunoServiceImp.adicionarAreaDeEstudoInteressada(alunoId, areaDeEstudoId));
    }

    @GetMapping("/{alunoId}/professores-interessados-areas-de-estudo")
    public ResponseEntity<Collection<ProfessorGetInteressadosPorAlunoDTO>> listarProfessoresInteressados(@PathVariable Long alunoId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.alunoServiceImp.listarProfessoresInteressados(alunoId));
    }

    @Transactional
    @PostMapping("/solicitar-orientacao-tcc-aluno")
    public ResponseEntity<Solicitacao> criarSolicitacaoTCCCadastradoPeloAluno(@RequestBody @Valid SolicitacaoDTO requestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(solicitacaoServiceImp.criarSolicitacaoTCCCadastradoPeloAluno(requestDTO));
    }

    @Transactional
    @PostMapping("/solicitar-orientacao-tcc-professor")
    public ResponseEntity<Solicitacao> criarSolicitacaoTCCCadastradoPeloProfessor(@RequestBody @Valid SolicitacaoDTO requestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(solicitacaoServiceImp.criarSolicitacaoTCCCadastradoPeloProfessor(requestDTO));
    }

    @GetMapping("/temas-tcc-cadastrados")
    public ResponseEntity<Collection<TemaTCCProfessorAlunoGetAllDTO>> listarTemasTccCadastradosPorProfessor() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.alunoServiceImp.listarTemasTCCCadastradosPorProfessor());
    }
}
