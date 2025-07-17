package com.phoebus.gestao_centros_comunitarios.service;

import com.phoebus.gestao_centros_comunitarios.dto.MediaRecursoDTO;
import com.phoebus.gestao_centros_comunitarios.enums.TipoRecurso;
import com.phoebus.gestao_centros_comunitarios.model.CentroComunitario;
import com.phoebus.gestao_centros_comunitarios.model.HistoricoIntercambio;
import com.phoebus.gestao_centros_comunitarios.model.Recurso;
import com.phoebus.gestao_centros_comunitarios.repository.CentroComunitarioRepository;
import com.phoebus.gestao_centros_comunitarios.repository.HistoricoIntercambioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private CentroComunitarioRepository centroRepository;
    @Autowired
    private HistoricoIntercambioRepository historicoRepository;

    public List<CentroComunitario> getCentrosComOcupacaoAlta() {
        return centroRepository.findByPercentualOcupacaoGreaterThan(90.0);
    }

    public Map<String, List<MediaRecursoDTO>> getMediaRecursosPorCentro() {
        List<CentroComunitario> todosCentros = centroRepository.findAll();
        if (todosCentros.isEmpty()) {
            return Collections.singletonMap("mediaRecursos", Collections.emptyList());
        }

        Map<TipoRecurso, Integer> somaTotalRecursos = new EnumMap<>(TipoRecurso.class);

        for (CentroComunitario centro : todosCentros) {
            for (Recurso recurso : centro.getRecursos()) {
                somaTotalRecursos.merge(recurso.getTipo(), recurso.getQuantidade(), Integer::sum);
            }
        }

        List<MediaRecursoDTO> medias = somaTotalRecursos.entrySet().stream()
                .map(entry -> {
                    double media = (double) entry.getValue() / todosCentros.size();
                    return new MediaRecursoDTO(entry.getKey(), media);
                })
                .collect(Collectors.toList());

        return Collections.singletonMap("mediaRecursos", medias);
    }

    public List<HistoricoIntercambio> getHistoricoIntercambio(String centroId, Optional<Date> desde) {
        if (desde.isPresent()) {
            return historicoRepository.findByCentroOrigemIdAndDataIntercambioAfterOrCentroDestinoIdAndDataIntercambioAfter(
                    centroId, desde.get(), centroId, desde.get());
        } else {
            return historicoRepository.findByCentroOrigemIdOrCentroDestinoId(centroId, centroId);
        }
    }
}
