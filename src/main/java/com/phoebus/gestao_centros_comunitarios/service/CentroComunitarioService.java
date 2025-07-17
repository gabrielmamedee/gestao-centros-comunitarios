package com.phoebus.gestao_centros_comunitarios.service;

import com.phoebus.gestao_centros_comunitarios.dto.CentroComunitarioDTO;
import com.phoebus.gestao_centros_comunitarios.model.CentroComunitario;
import com.phoebus.gestao_centros_comunitarios.repository.CentroComunitarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CentroComunitarioService {

    @Autowired
    private CentroComunitarioRepository repository;

    public CentroComunitario adicionarCentro(CentroComunitarioDTO dto) {
        CentroComunitario centro = new CentroComunitario();
        centro.setNome(dto.getNome());
        centro.setEndereco(dto.getEndereco());
        centro.setLocalizacao(dto.getLocalizacao());
        centro.setCapacidadeMaxima(dto.getCapacidadeMaxima());
        centro.setOcupacaoAtual(dto.getOcupacaoAtual());
        centro.setRecursos(dto.getRecursos());

        centro.calcularPercentualOcupacao();

        return repository.save(centro);
    }

    public List<CentroComunitario> listarTodos() {
        return repository.findAll();
    }

    public Optional<CentroComunitario> buscarPorId(String id) {
        return repository.findById(id);
    }


    public Optional<CentroComunitario> atualizarOcupacao(String id, Integer novaOcupacao) {

        Optional<CentroComunitario> centroOpt = repository.findById(id);

        if (centroOpt.isEmpty()) {
            return Optional.empty();
        }

        CentroComunitario centro = centroOpt.get();
        centro.setOcupacaoAtual(novaOcupacao);
        centro.calcularPercentualOcupacao();

        if (centro.getPercentualOcupacao() >= 100.0) {
            System.out.println("ALERTA: Centro " + centro.getNome() + " atingiu 100% da capacidade!");
        }

        return Optional.of(repository.save(centro));
    }
}
