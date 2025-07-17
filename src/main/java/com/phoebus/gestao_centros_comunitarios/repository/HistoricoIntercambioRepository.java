package com.phoebus.gestao_centros_comunitarios.repository;

import com.phoebus.gestao_centros_comunitarios.model.HistoricoIntercambio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HistoricoIntercambioRepository extends MongoRepository<HistoricoIntercambio, String> {
    List<HistoricoIntercambio> findByCentroOrigemIdOrCentroDestinoId(String centroOrigemId, String centroDestinoId);

    List<HistoricoIntercambio> findByCentroOrigemIdAndDataIntercambioAfterOrCentroDestinoIdAndDataIntercambioAfter(
            String centroOrigemId, Date data, String centroDestinoId, Date data2);

}
