package com.phoebus.gestao_centros_comunitarios.controller;

import com.phoebus.gestao_centros_comunitarios.dto.IntercambioDTO;
import com.phoebus.gestao_centros_comunitarios.model.HistoricoIntercambio;
import com.phoebus.gestao_centros_comunitarios.service.IntercambioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/intercambios")
public class IntercambioController {

    @Autowired
    private IntercambioService service;

    @Operation(summary = "Executa o intercâmbio de recursos entre dois centros", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intercâmbio realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha na validação do intercâmbio, recursos insuficientes ou pontuação inválida)")
    })
    @PostMapping
    public ResponseEntity<?> realizarIntercambio(@RequestBody IntercambioDTO dto) {
        try {
            HistoricoIntercambio historico = service.realizarIntercambio(dto);
            return ResponseEntity.ok(historico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
