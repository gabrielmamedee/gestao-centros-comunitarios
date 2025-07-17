package com.phoebus.gestao_centros_comunitarios.repository;

import com.phoebus.gestao_centros_comunitarios.model.CentroComunitario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CentroComunitarioRepository extends MongoRepository<CentroComunitario, String> {
    List<CentroComunitario> findByPercentualOcupacaoGreaterThan(Double percentual);
}
