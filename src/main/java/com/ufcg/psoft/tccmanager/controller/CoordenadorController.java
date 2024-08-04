package com.ufcg.psoft.tccmanager.controller;

import com.ufcg.psoft.tccmanager.dto.aluno.AlunoDTO;
import com.ufcg.psoft.tccmanager.dto.areaDeEstudo.AreaDeEstudoDTO;
import com.ufcg.psoft.tccmanager.dto.coordenador.CoordenadorDTO;
import com.ufcg.psoft.tccmanager.dto.professor.ProfessorDTO;
import com.ufcg.psoft.tccmanager.model.Aluno;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import com.ufcg.psoft.tccmanager.model.Coordenador;
import com.ufcg.psoft.tccmanager.model.Professor;
import com.ufcg.psoft.tccmanager.service.coordenador.CoordenadorServiceImp;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;

@RestController
@RequestMapping(value = "/coordenador", produces = MediaType.APPLICATION_JSON_VALUE)
public class CoordenadorController {

    @Autowired
    CoordenadorServiceImp coordenadorServiceImp;

    @Transactional
    @PostMapping("/administrador")
    public ResponseEntity<Coordenador> criarCoordenador(@RequestBody @Valid CoordenadorDTO coordenadorDto) throws NoSuchAlgorithmException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.coordenadorServiceImp.criarCoordenador(coordenadorDto));
    }

    @Transactional
    @PutMapping("/administrador")
    public ResponseEntity<Coordenador> alterarCoordenador(@RequestBody @Valid CoordenadorDTO coordenadorDto) {
        return ResponseEntity
                .ok(this.coordenadorServiceImp.modificarCoordenador(coordenadorDto));
    }

    @Transactional
    @PostMapping("/alunos")
    public ResponseEntity<Aluno> criarAluno(@RequestBody @Valid AlunoDTO alunoDto,
                                            @RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.coordenadorServiceImp.criarAluno(alunoDto, senhaCoordenador));
    }

    @Transactional
    @PostMapping("/professores")
    public ResponseEntity<Professor> criarProfessor(@RequestBody @Valid ProfessorDTO professorDto,
                                                    @RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.coordenadorServiceImp.criarProfessor(professorDto, senhaCoordenador));
    }

    @Transactional
    @PostMapping("/areasDeEstudo")
    public ResponseEntity<AreaDeEstudo> criarAreaDeEstudo(@RequestBody @Valid AreaDeEstudoDTO areaDeEstudoDto,
                                                          @RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.coordenadorServiceImp.criarAreaDeEstudo(areaDeEstudoDto, senhaCoordenador));
    }

    @GetMapping("/alunos/{id}")
    public ResponseEntity<Aluno> listarAlunoPorId(@PathVariable Long id,
                                                  @RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity.ok(this.coordenadorServiceImp.buscarAlunoId(id, senhaCoordenador));
    }

    @GetMapping("/professores/{id}")
    public ResponseEntity<Professor> listarProfessorPorId(@PathVariable Long id,
                                                          @RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity
                .ok(this.coordenadorServiceImp.buscarProfessorId(id, senhaCoordenador));
    }

    @GetMapping("/areasDeEstudo/{id}")
    public ResponseEntity<AreaDeEstudo> listarAreaDeEstudoPorId(@PathVariable Long id,
                                                                @RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity
                .ok(this.coordenadorServiceImp.buscarAreaDeEstudoId(id, senhaCoordenador));
    }

    @GetMapping("/alunos")
    public ResponseEntity<Collection<Aluno>> listarTodosAlunos(@RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity
                .ok(this.coordenadorServiceImp.listarTodosAlunos(senhaCoordenador));
    }

    @GetMapping("/professores")
    public ResponseEntity<Collection<Professor>> listarTodosProfessores(@RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity
                .ok(this.coordenadorServiceImp.listarTodosProfessores(senhaCoordenador));
    }

    @GetMapping("/areasDeEstudo")
    public ResponseEntity<Collection<AreaDeEstudo>> listarTodasAreasDeEstudo(@RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity
                .ok(this.coordenadorServiceImp.listarTodasAreasDeEstudo(senhaCoordenador));
    }

    @Transactional
    @PutMapping("/alunos/{id}")
    public ResponseEntity<Aluno> alterarAluno(@PathVariable Long id,
                                              @RequestBody @Valid AlunoDTO alunoDto,
                                              @RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity
                .ok(this.coordenadorServiceImp.modificarAluno(id, alunoDto, senhaCoordenador));
    }

    @Transactional
    @PutMapping("/professores/{id}")
    public ResponseEntity<Professor> alterarProfessor(@PathVariable Long id,
                                                      @RequestBody @Valid ProfessorDTO professorDto,
                                                      @RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity
                .ok(this.coordenadorServiceImp.modificarProfessor(id, professorDto, senhaCoordenador));
    }

    @Transactional
    @PutMapping(value = "/areasDeEstudo/{id}")
    public ResponseEntity<AreaDeEstudo> alterarAreaDeEstudo(@PathVariable Long id,
                                                            @RequestBody @Valid AreaDeEstudoDTO areaDeEstudoDto,
                                                            @RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity
                .ok(this.coordenadorServiceImp.modificarAreaDeEstudo(id, areaDeEstudoDto, senhaCoordenador));
    }

    @Transactional
    @DeleteMapping("/alunos/{id}")
    public ResponseEntity<String> deletarAluno(@PathVariable Long id,
                                               @RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        this.coordenadorServiceImp.removerAluno(id, senhaCoordenador);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Transactional
    @DeleteMapping("/professores/{id}")
    public ResponseEntity<String> deletarProfessor(@PathVariable long id,
                                                   @RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        this.coordenadorServiceImp.removerProfessor(id, senhaCoordenador);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Transactional
    @DeleteMapping(value = "/areasDeEstudo/{id}")
    public ResponseEntity<String> deletarAreaDeEstudo(@PathVariable long id,
                                                      @RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        this.coordenadorServiceImp.removerAreaDeEstudo(id, senhaCoordenador);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/relatorio")
    public ResponseEntity<String> gerarRelatorio(@RequestHeader String senhaCoordenador) throws NoSuchAlgorithmException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.coordenadorServiceImp.gerarRelatorio(senhaCoordenador));
    }
}
