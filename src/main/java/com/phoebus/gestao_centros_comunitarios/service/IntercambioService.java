package com.phoebus.gestao_centros_comunitarios.service;

import com.phoebus.gestao_centros_comunitarios.dto.IntercambioDTO;
import com.phoebus.gestao_centros_comunitarios.enums.TipoRecurso;
import com.phoebus.gestao_centros_comunitarios.model.CentroComunitario;
import com.phoebus.gestao_centros_comunitarios.model.HistoricoIntercambio;
import com.phoebus.gestao_centros_comunitarios.model.ItemIntercambio;
import com.phoebus.gestao_centros_comunitarios.model.Recurso;
import com.phoebus.gestao_centros_comunitarios.repository.CentroComunitarioRepository;
import com.phoebus.gestao_centros_comunitarios.repository.HistoricoIntercambioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IntercambioService {

    @Autowired
    private CentroComunitarioRepository centroRepository;
    @Autowired
    private HistoricoIntercambioRepository historicoRepository;

    @Transactional
    public HistoricoIntercambio realizarIntercambio(IntercambioDTO dto) {
        CentroComunitario origem = centroRepository.findById(dto.getCentroOrigemId())
                .orElseThrow(() -> new RuntimeException("Centro de origem com ID " + dto.getCentroOrigemId() + " não encontrado!"));
        CentroComunitario destino = centroRepository.findById(dto.getCentroDestinoId())
                .orElseThrow(() -> new RuntimeException("Centro de destino com ID " + dto.getCentroDestinoId() + " não encontrado!"));

        validarRecursosDisponiveis(origem, dto.getItensOferecidosOrigem());
        validarRecursosDisponiveis(destino, dto.getItensOferecidosDestino());

        int pontosOrigem = calcularPontos(dto.getItensOferecidosOrigem());
        int pontosDestino = calcularPontos(dto.getItensOferecidosDestino());

        boolean excecaoAplicada = false;
        if (pontosOrigem != pontosDestino) {
            if (origem.getPercentualOcupacao() > 90.0 || destino.getPercentualOcupacao() > 90.0) {
                excecaoAplicada = true;
            } else {
                throw new RuntimeException("A troca de pontos deve ser equivalente. A exceção de >90% de ocupação não se aplica.");
            }
        }

        atualizarInventario(origem, dto.getItensOferecidosOrigem(), dto.getItensOferecidosDestino());
        atualizarInventario(destino, dto.getItensOferecidosDestino(), dto.getItensOferecidosOrigem());

        centroRepository.save(origem);
        centroRepository.save(destino);

        HistoricoIntercambio historico = HistoricoIntercambio.builder()
                .centroOrigemId(origem.getId())
                .centroDestinoId(destino.getId())
                .itensOferecidosOrigem(dto.getItensOferecidosOrigem())
                .pontosOferecidosOrigem(pontosOrigem)
                .itensOferecidosDestino(dto.getItensOferecidosDestino())
                .pontosOferecidosDestino(pontosDestino)
                .dataIntercambio(new Date())
                .regraExcecaoAplicada(excecaoAplicada)
                .build();

        return historicoRepository.save(historico);
    }

    private int calcularPontos(List<ItemIntercambio> itens) {
        return itens.stream()
                .mapToInt(item -> item.getTipoRecurso().getPontos() * item.getQuantidade())
                .sum();
    }

    private void validarRecursosDisponiveis(CentroComunitario centro, List<ItemIntercambio> itensOfertados) {
        Map<TipoRecurso, Integer> inventario = centro.getRecursos().stream()
                .collect(Collectors.toMap(Recurso::getTipo, Recurso::getQuantidade));

        for (ItemIntercambio item : itensOfertados) {
            if (!inventario.containsKey(item.getTipoRecurso()) || inventario.get(item.getTipoRecurso()) < item.getQuantidade()) {
                throw new RuntimeException("O centro " + centro.getNome() + " não possui " + item.getQuantidade() + " unidades de " + item.getTipoRecurso());
            }
        }
    }

    private void atualizarInventario(CentroComunitario centro, List<ItemIntercambio> itensRemovidos, List<ItemIntercambio> itensAdicionados) {
        Map<TipoRecurso, Recurso> inventarioMap = centro.getRecursos().stream()
                .collect(Collectors.toMap(Recurso::getTipo, Function.identity()));

        for (ItemIntercambio item : itensRemovidos) {
            Recurso recurso = inventarioMap.get(item.getTipoRecurso());
            recurso.setQuantidade(recurso.getQuantidade() - item.getQuantidade());
        }

        for (ItemIntercambio item : itensAdicionados) {
            inventarioMap.compute(item.getTipoRecurso(), (tipo, recurso) -> {
                if (recurso == null) {
                    recurso = new Recurso();
                    recurso.setTipo(tipo);
                    recurso.setQuantidade(0);
                }
                recurso.setQuantidade(recurso.getQuantidade() + item.getQuantidade());
                return recurso;
            });
        }

        centro.setRecursos(inventarioMap.values().stream()
                .filter(r -> r.getQuantidade() > 0)
                .collect(Collectors.toList()));
    }
}
