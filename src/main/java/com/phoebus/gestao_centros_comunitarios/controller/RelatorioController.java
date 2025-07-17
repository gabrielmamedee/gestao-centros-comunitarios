package com.phoebus.gestao_centros_comunitarios.controller;

import com.phoebus.gestao_centros_comunitarios.dto.MediaRecursoDTO;
import com.phoebus.gestao_centros_comunitarios.model.CentroComunitario;
import com.phoebus.gestao_centros_comunitarios.model.HistoricoIntercambio;
import com.phoebus.gestao_centros_comunitarios.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @Operation(summary = "Gera relatório de centros com ocupação superior a 90%", description = "")
    @GetMapping("/ocupacao-alta")
    public ResponseEntity<List<CentroComunitario>> getRelatorioOcupacaoAlta() {
        return ResponseEntity.ok(relatorioService.getCentrosComOcupacaoAlta());
    }

    @Operation(summary = "Gera relatório com a quantidade média de cada recurso por centro", description = "")
    @GetMapping("/media-recursos")
    public ResponseEntity<Map<String, List<MediaRecursoDTO>>> getRelatorioMediaRecursos() {
        return ResponseEntity.ok(relatorioService.getMediaRecursosPorCentro());
    }

    @Operation(summary = "Gera relatório de histórico de negociações de um centro", description = "")
    @GetMapping("/historico-intercambios")
    public ResponseEntity<List<HistoricoIntercambio>> getRelatorioHistoricoIntercambios(
            @RequestParam String centroId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // formato esperado da data (ex: 2025-07-17)
            Optional<Date> desde) {
        return ResponseEntity.ok(relatorioService.getHistoricoIntercambio(centroId, desde));
    }
}
