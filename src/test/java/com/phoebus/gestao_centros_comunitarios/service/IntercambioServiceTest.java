package com.phoebus.gestao_centros_comunitarios.service;

import com.phoebus.gestao_centros_comunitarios.dto.IntercambioDTO;
import com.phoebus.gestao_centros_comunitarios.enums.TipoRecurso;
import com.phoebus.gestao_centros_comunitarios.model.CentroComunitario;
import com.phoebus.gestao_centros_comunitarios.model.HistoricoIntercambio;
import com.phoebus.gestao_centros_comunitarios.model.ItemIntercambio;
import com.phoebus.gestao_centros_comunitarios.model.Recurso;
import com.phoebus.gestao_centros_comunitarios.repository.CentroComunitarioRepository;
import com.phoebus.gestao_centros_comunitarios.repository.HistoricoIntercambioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntercambioServiceTest {

    @Mock
    private CentroComunitarioRepository centroRepository;
    @Mock
    private HistoricoIntercambioRepository historicoRepository;
    @InjectMocks
    private IntercambioService intercambioService;

    private CentroComunitario centroOrigem;
    private CentroComunitario centroDestino;

    @BeforeEach
    void setUp() {
        centroOrigem = new CentroComunitario();
        centroOrigem.setId("id_origem");
        centroOrigem.setNome("Centro A");
        centroOrigem.setPercentualOcupacao(50.0);
        centroOrigem.setRecursos(new ArrayList<>(List.of(new Recurso(TipoRecurso.KIT_SUPRIMENTOS_MEDICOS, 10))));

        centroDestino = new CentroComunitario();
        centroDestino.setId("id_destino");
        centroDestino.setNome("Centro B");
        centroDestino.setPercentualOcupacao(60.0);
        centroDestino.setRecursos(new ArrayList<>(List.of(new Recurso(TipoRecurso.VOLUNTARIO, 10))));
    }

    @Test
    void deveRealizarIntercambioComSucesso_QuandoExcecaoDePontosSeAplica() {
        centroOrigem.setPercentualOcupacao(95.0);

        ItemIntercambio itemOrigem = new ItemIntercambio(TipoRecurso.KIT_SUPRIMENTOS_MEDICOS, 1); // 1 * 7 = 7 pontos
        ItemIntercambio itemDestino = new ItemIntercambio(TipoRecurso.VOLUNTARIO, 1); // 1 * 3 = 3 pontos

        IntercambioDTO dto = new IntercambioDTO();
        dto.setCentroOrigemId("id_origem");
        dto.setCentroDestinoId("id_destino");
        dto.setItensOferecidosOrigem(List.of(itemOrigem));
        dto.setItensOferecidosDestino(List.of(itemDestino));

        when(centroRepository.findById("id_origem")).thenReturn(Optional.of(centroOrigem));
        when(centroRepository.findById("id_destino")).thenReturn(Optional.of(centroDestino));
        when(historicoRepository.save(any(HistoricoIntercambio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HistoricoIntercambio resultado = intercambioService.realizarIntercambio(dto);

        assertNotNull(resultado);
        assertEquals(7, resultado.getPontosOferecidosOrigem());
        assertEquals(3, resultado.getPontosOferecidosDestino());
        assertTrue(resultado.isRegraExcecaoAplicada());

        verify(historicoRepository, times(1)).save(any(HistoricoIntercambio.class));
        verify(centroRepository, times(2)).save(any(CentroComunitario.class));
    }

    @Test
    void deveLancarExcecao_QuandoPontosSaoDiferentesSemExcecao() {

        ItemIntercambio itemOrigem = new ItemIntercambio(TipoRecurso.MEDICO, 1); // 1 * 4 = 4 pontos
        ItemIntercambio itemDestino = new ItemIntercambio(TipoRecurso.CESTA_BASICA, 1); // 1 * 2 = 2 pontos

        IntercambioDTO dtoInjusto = new IntercambioDTO();
        dtoInjusto.setCentroOrigemId("id_origem");
        dtoInjusto.setCentroDestinoId("id_destino");
        dtoInjusto.setItensOferecidosOrigem(List.of(itemOrigem));
        dtoInjusto.setItensOferecidosDestino(List.of(itemDestino));

        centroOrigem.setRecursos(new ArrayList<>(List.of(new Recurso(TipoRecurso.MEDICO, 5))));
        centroDestino.setRecursos(new ArrayList<>(List.of(new Recurso(TipoRecurso.CESTA_BASICA, 5))));

        when(centroRepository.findById("id_origem")).thenReturn(Optional.of(centroOrigem));
        when(centroRepository.findById("id_destino")).thenReturn(Optional.of(centroDestino));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            intercambioService.realizarIntercambio(dtoInjusto);
        });

        String mensagemEsperada = "A troca de pontos deve ser equivalente. A exceção de >90% de ocupação não se aplica.";
        assertEquals(mensagemEsperada, exception.getMessage());

        verify(centroRepository, never()).save(any(CentroComunitario.class));
        verify(historicoRepository, never()).save(any(HistoricoIntercambio.class));
    }
}