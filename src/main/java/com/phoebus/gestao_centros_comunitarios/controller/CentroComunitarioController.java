package com.phoebus.gestao_centros_comunitarios.controller;

import com.phoebus.gestao_centros_comunitarios.dto.AtualizarOcupacaoDTO;
import com.phoebus.gestao_centros_comunitarios.dto.CentroComunitarioDTO;
import com.phoebus.gestao_centros_comunitarios.model.CentroComunitario;
import com.phoebus.gestao_centros_comunitarios.service.CentroComunitarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/centros-comunitarios")
public class CentroComunitarioController {

    @Autowired
    private CentroComunitarioService service;

    @Operation(summary = "Cadastra um novo centro comunitário", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Centro comunitário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<CentroComunitario> adicionarCentro(@RequestBody CentroComunitarioDTO dto) {
        CentroComunitario novoCentro = service.adicionarCentro(dto);
        return new ResponseEntity<>(novoCentro, HttpStatus.CREATED);
    }

    @Operation(summary = "Lista todos os centros comunitários cadastrados", description = "")
    @GetMapping
    public ResponseEntity<List<CentroComunitario>> listarTodos() {
        List<CentroComunitario> centros = service.listarTodos();
        return ResponseEntity.ok(centros);
    }

    @Operation(summary = "Busca um centro comunitário por seu ID", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Centro comunitário encontrado"),
            @ApiResponse(responseCode = "404", description = "Centro comunitário não encontrado com o ID fornecido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CentroComunitario> buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id)
                .map(centro -> ResponseEntity.ok(centro))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualiza a ocupação de um centro comunitário", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ocupação atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Centro comunitário não encontrado com o ID fornecido")
    })
    @PatchMapping("/{id}/ocupacao")
    public ResponseEntity<CentroComunitario> atualizarOcupacao(@PathVariable String id, @RequestBody AtualizarOcupacaoDTO dto) {
        return service.atualizarOcupacao(id, dto.getNovaOcupacao())
                .map(centro -> ResponseEntity.ok(centro))
                .orElse(ResponseEntity.notFound().build());
    }
}
